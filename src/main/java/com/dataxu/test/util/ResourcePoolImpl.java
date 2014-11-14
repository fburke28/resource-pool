/* File: ResourcePoolImpl.java
 * 
 * Copyright 2014, Finbarr Burke
 * All Rights Reserved
 *
 * This software and all information contained herein is the property
 * of Finbarr Burke.
 *
 *			  Restricted Rights Legend
 *			  ------------------------
 * Use, duplication, or disclosure by the Government is subject to
 * restrictions as set forth in paragraph (b)(3)(B) of the Rights in
 * Technical Data and Computer Software clause in DAR 7-104.9(a).
 */
package com.dataxu.test.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

/**
 * Implementation of resource pool to handle shared resources.
 * 
 * @author fburke
 * @param <R>
 */
public class ResourcePoolImpl<R> implements ResourcePool<R> {

	private static Logger logger = Logger.getLogger(ResourcePoolImpl.class);

	private static final int SLEEP_MS = 100;

	private volatile boolean open;
	private Queue<R> acquiredResources;
	private Queue<R> availableResources;

	// Read/Write lock to control access to removing, acquiring, closing
	// operations
	private ReadWriteLock lock;

	/**
	 * Default constructor
	 */
	public ResourcePoolImpl() {
		super();

		// Initialize internal objects on resource pool creation.
		this.open = false;
		this.acquiredResources = new ConcurrentLinkedQueue<R>();
		this.availableResources = new ConcurrentLinkedQueue<R>();

		// Ensure the lock is fair
		lock = new ReentrantReadWriteLock(true);

		logger.info("Resource pool created.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open() {
		this.open = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpen() {
		if (this.open) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws InterruptedException
	 */
	@Override
	public void close() throws InterruptedException {
		try {
			while (!acquiredResources.isEmpty()) {
				// Sleep for a time and check again for an available resource
				Thread.sleep(SLEEP_MS);

				if (acquiredResources.isEmpty()) {
					lock.writeLock().lock();
					break;
				}
			}
			// No acquired resources exist so we can go ahead and close the
			// resource pool
			this.open = false;
			logger.info("Resource pool closed.");
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeNow() {
		try {
			lock.writeLock().lock();
			this.open = false;
			logger.info("Resouce pool closed with immediate affect.");
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(R resource) {
		// We do not want to collide with a remove operation so we ensure to
		// acquire a read lock.
		try {
			lock.readLock().lock();
			if (!acquiredResources.contains(resource) && !availableResources.contains(resource)) {
				// Insert this resource at the tail of the available queue
				availableResources.offer(resource);
				logger.info("Number of available resources " + availableResources.size());
				return true;
			}
			return false;
		} finally {
			// This block always gets called regardless of return earlier
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws InterruptedException
	 */
	@Override
	public boolean remove(R resource) {
		try {
			// We need to get the write lock to remove a resource.
			lock.writeLock().lock();
			if (!acquiredResources.contains(resource) && !availableResources.contains(resource)) {
				logger.debug("Resource is not managed by this pool.");
				return false;
			} else if (availableResources.contains(resource)) {
				availableResources.remove(resource);
				return true;
			} else if (acquiredResources.contains(resource)) {
				// The release action will need a read lock to release the
				// resource
				lock.writeLock().unlock();

				// Wait until the resource is no longer acquired and then remove
				// it from the managed pool
				while (acquiredResources.contains(resource)) {
					Thread.sleep(SLEEP_MS);
				}
				// The resource is no longer acquired. Check that it exists in
				// the available resources and if so remove it.
				if (availableResources.contains(resource)) {
					lock.writeLock().lock();
					availableResources.remove(resource);
					return true;
				}
				return false;
			}
			// Default condition returns false as the successful removal have
			// already occurred
			return false;
		} catch (InterruptedException e) {
			logger.error("Error occurred removing resource from pool.", e);
			return false;
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeNow(R resource) {
		try {
			// We need to get the write lock to remove a resource.
			lock.writeLock().lock();
			if (!acquiredResources.contains(resource) && !availableResources.contains(resource)) {
				logger.debug("Resource is not managed by this pool.");
				return false;
			} else if (availableResources.contains(resource)) {
				availableResources.remove(resource);
				return true;
			} else if (acquiredResources.contains(resource)) {
				acquiredResources.remove(resource);
				return true;
			}
			// Default condition returns false as the successful removal have
			// already occurred
			return false;
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws InterruptedException
	 */
	@Override
	public R acquire() {
		try {
			lock.readLock().lock();
			do {
				if (!this.open) {
					return null;
				}

				R resource = availableResources.poll();
				// If the resource is not null we can return it
				if (resource != null) {
					acquiredResources.offer(resource);
					return resource;
				}

				// Check again for an available resource
				Thread.sleep(SLEEP_MS);
			} while (availableResources.isEmpty());
			return null;
		} catch (InterruptedException e) {
			logger.error("Error occurred acquiring a resource from the pool.", e);
			return null;
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public R acquire(long timeout, TimeUnit unit) {
		try {
			lock.readLock().lock();

			// Calculate timeout date
			Date timeoutDate = new Date(Calendar.getInstance().getTime().getTime() + unit.toMillis(timeout));

			do {
				if (!this.open || Calendar.getInstance().getTime().after(timeoutDate)) {
					return null;
				}

				R resource = availableResources.poll();
				// If the resource is not null we can return it
				if (resource != null) {
					acquiredResources.offer(resource);
					return resource;
				}

				// Avoid tight looping - inserting sleep
				Thread.sleep(SLEEP_MS);
			} while (availableResources.isEmpty());
			return null;
		} catch (InterruptedException e) {
			logger.error("Error occurred acquiring a resource from the pool.", e);
			return null;
		} finally {
			// This block always gets called regardless of return earlier
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release(R resource) {
		// Check to ensure that the resource is still managed by the pool
		try {
			// We need a read lock here as this may interfere with the remove
			// operation
			lock.readLock().lock();
			if (this.open && acquiredResources.contains(resource)) {
				boolean retVal = acquiredResources.remove(resource);
				availableResources.offer(resource);

				logger.debug("Released from acquired resources " + retVal);
			}
		} finally {
			// This block always gets called regardless of return earlier
			lock.readLock().unlock();
		}
	}

}
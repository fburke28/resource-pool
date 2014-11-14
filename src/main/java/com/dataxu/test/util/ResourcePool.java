/* File: ResourcePool.java
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

import java.util.concurrent.TimeUnit;

/**
 * Shared resource pool. The resource used is defined once the ResourcePool
 * object is created.
 * 
 * @author fburke
 * @param <R>
 *            Resource to share in the pool.
 */
public interface ResourcePool<R> {

	/**
	 * Open for the resource pool for work.
	 */
	void open();

	/**
	 * Check to see if the resource pool is opne
	 * 
	 * @return boolean
	 */
	boolean isOpen();

	/**
	 * Closes the resource pool once all existing acquired resources have been
	 * released.
	 */
	void close() throws InterruptedException;

	/**
	 * Closed the resource pool immediately without waiting for acquired
	 * resources to be released.
	 */
	void closeNow();

	/**
	 * Add the resource to the shared pool.
	 * 
	 * @param resource
	 * @return boolean True if not already added, false otherwise
	 */
	boolean add(R resource);

	/**
	 * Remove the resource from the shared pool. The resource will only be
	 * removed once the resource is released back into the pool.
	 * 
	 * @param resource
	 * @return boolean True if successfully removed, false otherwise
	 */
	boolean remove(R resource);

	/**
	 * Remove the shared resource from the pool without waiting for it to be
	 * released back into the pool.
	 * 
	 * @param resource
	 * @return boolean True if successfully removed, false otherwise
	 */
	boolean removeNow(R resource);

	/**
	 * Acquire the shared resource from the pool. The method will block until a
	 * resource is available.
	 * 
	 * @return <R> The resource to acquire or null if the pool is closed.
	 */
	R acquire();

	/**
	 * Acquire the shared resource within the specified time interval. If the
	 * resource cannot be acquired within the time interval an exception is
	 * thrown.
	 * 
	 * @param timeout
	 *            Time to wait
	 * @param unit
	 *            Unit of time
	 * @return <R> The resource to acquire or null if the pool is closed.
	 */
	R acquire(long timeout, TimeUnit unit);

	/**
	 * 
	 * @param resource
	 */
	void release(R resource);

}
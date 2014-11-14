/* File: AddAndAcquireActivity.java
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
package com.dataxu.test.driver;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dataxu.test.domain.Package;
import com.dataxu.test.util.ResourcePool;

/**
 * Integration test activity thread.
 * 
 * @author fburke
 */
public class AddAndAcquireActivity extends Thread {

	private static Logger logger = Logger.getLogger(AddAndAcquireActivity.class);

	private ResourcePool<Package> resourcePool;

	/**
	 * Constructor with args.
	 * 
	 * @param resourcePool
	 */
	public AddAndAcquireActivity(ResourcePool<Package> resourcePool) {
		this.resourcePool = resourcePool;
	}

	/**
	 * Activity thread will run a series of add and acquire operations.
	 */
	public void run() {
		Package package1 = new Package("fburke", "Boston", "Car", Double.valueOf(20000.00));
		Package package2 = new Package("fburke", "Boston", "Bus", Double.valueOf(50000.00));
		Package package3 = new Package("fburke", "Boston", "Bike", Double.valueOf(1000.00));

		resourcePool.add(package1);
		resourcePool.add(package2);

		Package acquiredPackage1 = resourcePool.acquire();
		logger.info("Acquired resource " + acquiredPackage1.getItem());

		resourcePool.add(package3);

		Package acquiredPackage2 = resourcePool.acquire();
		Package acquiredPackage3 = resourcePool.acquire();

		Package timeoutPackage = resourcePool.acquire(100, TimeUnit.MILLISECONDS);
		logger.info("Timeout package is " + timeoutPackage);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logger.error("Error occurred waiting.", e);
		}

		resourcePool.release(acquiredPackage1);
		resourcePool.release(acquiredPackage2);
		resourcePool.release(acquiredPackage3);
	}

}
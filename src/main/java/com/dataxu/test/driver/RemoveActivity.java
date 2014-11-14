/* File: RemoveActivity.java
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

import org.apache.log4j.Logger;

import com.dataxu.test.domain.Package;
import com.dataxu.test.util.ResourcePool;

/**
 * Integration test activity thread.
 * 
 * @author fburke
 */
public class RemoveActivity extends Thread {

	private static Logger logger = Logger.getLogger(RemoveActivity.class);

	private ResourcePool<Package> resourcePool;

	/**
	 * Constructor with args.
	 * 
	 * @param resourcePool
	 */
	public RemoveActivity(ResourcePool<Package> resourcePool) {
		this.resourcePool = resourcePool;
	}

	/**
	 * Activity thread will run a series of remove operations and then a close.
	 */
	public void run() {
		Package package1 = new Package("fburke", "Boston", "Car", Double.valueOf(20000.00));
		Package package2 = new Package("fburke", "Boston", "Bus", Double.valueOf(50000.00));
		Package package3 = new Package("fburke", "Boston", "Bike", Double.valueOf(1000.00));

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.error("Error occurred waiting.", e);
		}

		boolean remove1 = resourcePool.remove(package1);
		boolean remove2 = resourcePool.remove(package2);
		boolean remove3 = resourcePool.remove(package3);

		logger.info("Remove1 should be removed successfully " + remove1);
		logger.info("Remove2 should be removed successfully " + remove2);
		logger.info("Remove3 should be removed successfully " + remove3);

		boolean remove4 = resourcePool.remove(package3);

		logger.info("Package3 should already be removed returning value of false: " + (remove4));
	}

}
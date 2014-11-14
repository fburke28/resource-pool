/* File: MainDriver.java
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dataxu.test.domain.Package;
import com.dataxu.test.util.ResourcePool;

/**
 * Main driver to run integration test scenarios.
 * 
 * @author fburke
 */
public class MainDriver {

	private static Logger logger = Logger.getLogger(MainDriver.class);

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// Run main driver for integration test scenario
		ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
		ResourcePool<Package> resourcePool = (ResourcePool<Package>) context.getBean("resourcePool");

		// Try to acquire a resource before the pool is open
		Package resourcePackage = resourcePool.acquire();
		logger.info("Returned package before opening resource pool " + resourcePackage);

		resourcePool.open();

		logger.info("Is resource pool open " + resourcePool.isOpen());

		// Use resource pool and performance some integration tests
		AddAndAcquireActivity addAndAcquireActivity = new AddAndAcquireActivity(resourcePool);
		RemoveActivity removeActivity = new RemoveActivity(resourcePool);

		addAndAcquireActivity.start();
		removeActivity.start();
	}

}

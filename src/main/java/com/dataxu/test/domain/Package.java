/* File: Package.java
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
package com.dataxu.test.domain;

/**
 * Domain object to represent a package containing a single item to be delivered
 * to a user.
 * 
 * @author fburke
 */
public class Package {

	private String userName;
	private String address;
	private String item;
	private Double price;

	/**
	 * Default constructor with no-args.
	 */
	public Package() {
		super();
	}

	/**
	 * Constructor with args.
	 * 
	 * @param userName
	 * @param address
	 * @param item
	 * @param price
	 */
	public Package(String userName, String address, String item, Double price) {
		super();
		this.userName = userName;
		this.address = address;
		this.item = item;
		this.price = price;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	// Generated hashCode and equals methods.

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Package)) {
			return false;
		}
		Package other = (Package) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (item == null) {
			if (other.item != null) {
				return false;
			}
		} else if (!item.equals(other.item)) {
			return false;
		}
		if (price == null) {
			if (other.price != null) {
				return false;
			}
		} else if (!price.equals(other.price)) {
			return false;
		}
		if (userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!userName.equals(other.userName)) {
			return false;
		}
		return true;
	}

}
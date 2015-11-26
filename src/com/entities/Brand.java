package com.entities;

public class Brand {
	private short id;
	private String brandName;
	public Brand(short id, String brandName) {
		this.id = id;
		this.brandName = brandName;
	}

	public short getId() {
		return id;
	}
	
	public String getBrandName() {
		return this.brandName;
	}
	
	@Override
	public String toString() {
		return "Brand: " + brandName;
	}
}

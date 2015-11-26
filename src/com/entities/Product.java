package com.entities;

public class Product {
	private int id;
	private String productName;
	private Brand brand;
	private Category category;
	private float price;
	
	public Product(int id, Brand brand, Category category, float price) {
		this(id, null, brand, category, price);
	}
	public Product(int id, String productName, Brand brand, Category category, float price) {
		this.id = id;
		this.productName = productName;
		this.category = category;
		this.price = price;
		this.brand = brand;
	}
	
	@Override
	public String toString() {
		return "Product: " + productName + " Brand: " + brand.getBrandName() + " Category: " + category.getCategoryName() + " Price: " + price;
	}
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public Category getCategory() {
		return category;
	}

	public Brand getBrand() {
		return brand;
	}
	
	public String getProductName() {
		return this.productName;
	}

}

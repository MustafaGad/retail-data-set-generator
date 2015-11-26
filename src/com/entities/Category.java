package com.entities;

public class Category {
	private short id;
	private Category parentCategory = null;
	private String categoryName;
	
	public Category(short id, Category parentCategory) {
		this(id, parentCategory, null);
	}
	public Category(short id, Category parentCategory, String categoryName) {
		this.id = id;
		this.parentCategory = parentCategory;
		this.categoryName = categoryName;
	}

	public Category(short id) {
		this(id, null);
	}
	
	@Override
	public String toString() {
		if(parentCategory != null)
			return "Category : " + categoryName + " Parent : " + parentCategory.getCategoryName();
		return "Category : " + categoryName ;
	}
	public short getId() {
		return id;
	}

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}
	
	public String getCategoryName() {
		return this.categoryName;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
}

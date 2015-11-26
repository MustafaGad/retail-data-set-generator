package com.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EntityContainer {
	private static List<User> users;
	private static List<Store> stores;
	private static HashMap<Integer, Product> products;
	private static HashMap<Short, Category> categories;
	private static HashMap<Short, Brand> brands;
	private static HashMap<Short, Location> locations = new HashMap<Short, Location>();
	
	public static List<User> getUsers() {
		return users;
	}
	public static List<Store> getStores() {
		return stores;
	}
	public static HashMap<Integer, Product> getProducts() {
		return products;
	}
	public static void addLocation(Location location) {
		locations.put(location.getID(), location);
	}
	public static Location getLocation(Short id) {
		return locations.get(id);
	}
	
	public static void setUsers(ArrayList<User> usersList) {
		users = usersList;
	}
	public static void setCategories(HashMap<Short, Category> categoriesMap) {
		categories = categoriesMap;
	}
	public static void setBrands(HashMap<Short, Brand> brandsList) {
		brands = brandsList;
	}
	
	public static Brand getBrand(short brandID) {
		return brands.get(brandID);
	}
	
	public static Category getCategory(Short categoryID) {
		return categories.get(categoryID);
	}
	public static void addProduct(Product product) {
		if(products == null)
			products = new HashMap<Integer, Product>();
		products.put(product.getId(), product);
	}
	
	public static void dumpEntities() {
		dumpLocations();
		dumpUsers();
		dumpCategories();
		dumpBrands();
		dumpProducts();
	}
	
	private static void dumpProducts() {
		System.out.println("\nProducts: ");
		Set<Integer> keys = products.keySet();
		for(Integer key: keys) {
			System.out.println(products.get(key));
		}
	}
	private static void dumpBrands() {
		System.out.println("\nBrands: ");
		Set<Short> keys = brands.keySet();
		for(Short key: keys) {
			System.out.println(brands.get(key));
		}
	}
	private static void dumpCategories() {
		System.out.println("\nCategories:");
		Set<Short> keys = categories.keySet();
		for(Short key: keys) {
			System.out.println(categories.get(key));
		}
	}
	private static void dumpUsers() {
		System.out.println("\nUsers:");
		for(User user: users) {
			System.out.println("\t" + user);
		}
	}
	private static void dumpLocations() {
		System.out.println("\nLocations:");
		Set<Short> locationsKeys = locations.keySet();
		for(Short key: locationsKeys) {
			System.out.println("\t" + locations.get(key));
		}
	}
}

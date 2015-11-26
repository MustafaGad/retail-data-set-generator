package com.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Store {
	private char id;
	private Location location;
	private HashMap<Character, Integer> inventory;
	private HashMap<Character, Integer> capacity;
	
	public Store(char id, Location location) {
		this.id = id;
		this.location = location;
		inventory = new HashMap<Character, Integer>();
		capacity = new HashMap<Character, Integer>();
	}
	
	public void defineProducts(List<Character> products, List<Integer> desiredQuantity, List<Integer> desiredCapacity) {
		Iterator<Character> productItr = products.iterator();
		Iterator<Integer> quantityItr = desiredQuantity.iterator();
		Iterator<Integer> capacityItr = desiredCapacity.iterator();
		
		while(productItr.hasNext()) {
			Character product = productItr.next();
			inventory.put(product, quantityItr.next());
			capacity.put(product, capacityItr.next());
		}
	}

	public void updateInventory(List<Character> products, List<Integer> addedQuantity) {
		Iterator<Character> productItr = products.iterator();
		Iterator<Integer> addedQuantityItr = addedQuantity.iterator();
		Character product =  productItr.next();
		while(productItr.hasNext()) {
			inventory.put(product, inventory.get(product) + addedQuantityItr.next());
		}
	}
	
	public char getId() {
		return id;
	}

	public Location getLocation() {
		return location;
	}

	public HashMap<Character, Integer> getInventory() {
		return inventory;
	}

	public HashMap<Character, Integer> getCapacity() {
		return capacity;
	}
}

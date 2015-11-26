package com.entities;

public class Location {
	private short id;
	private String country;
	private String city;
	private String district;
	private double latitude;
	private double longitude;
	public Location(short id, String country, String city, String district, double latitude, double longitude) {
		this.id = id;
		this.country = country;
		this.city = city;
		this.district = district;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public  short getID() {
		return id;
	}
	public double getLatitued() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public String getCountry() {
		return country;
	}
	public String getCity() {
		return city;
	}
	public String getDistrict() {
		return district;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ID: " + id + " Country: " + country + " City: " + city + " District: " + district + " Latitude: " + latitude + " Longitude: " + longitude;
	}
}

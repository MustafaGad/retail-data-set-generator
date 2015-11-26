package com.entities;

public class User {
	public static final int BEHAV_FEATURE_COUNT = 8;
	private int id;
	private boolean gender;
	private float age;
	private Location location;
	private float[] behavProb;
	public float visitProb;
	private float viewProb;
	private float addToCartProb;
	private float checkoutProb;
	private float initPaymentProb;
	private float finishPaymentProb;
	private float stdvProductViewCount;
	private float meanProductViewCount;
	
//	public static final float[] userBehaviourSeg0 = new float [] {0.1f/24.0f, 0.5f, 0.4f, 0.5f, 0.9f, 0.9f};
//	public static final float[] userBehaviourSeg1 = new float [] {0.2f/24.0f, 0.5f, 0.4f, 0.5f, 0.9f, 0.9f};
//	public static final float[] userBehaviourSeg2 = new float [] {0.3f/24.0f, 0.5f, 0.4f, 0.5f, 0.9f, 0.9f};
//	public static final float[] userBehaviourSeg3 = new float [] {0.4f/24.0f, 0.5f, 0.4f, 0.5f, 0.9f, 0.9f};
	
	public static  float[][] userBehaviourSegMatrix ;
	
	public User(int id, boolean gender, float age, Location location, float [] behaviour) {
		this.id = id;
		this.gender = gender;
		this.age = age;
		this.location = location;
		this.behavProb = behaviour;
		visitProb = behavProb[0];
		viewProb = behavProb[1];
		addToCartProb = behavProb[2];
		checkoutProb = behavProb[3];
		initPaymentProb = behavProb[4];
		finishPaymentProb = behavProb[5];
		meanProductViewCount = behavProb[6];
		stdvProductViewCount = behavProb[7];
	}
	
	@Override
	public String toString() {
		return "ID: " + id + " Gender: " + gender + " Age: " + age + " Location: " + location.getCountry() + "-" + location.getCity() + "-" + location.getDistrict();
	}
	public int getId() {
		return id;
	}

	public boolean isGender() {
		return gender;
	}

	public float getAge() {
		return age;
	}

	public Location getLocation() {
		return location;
	}

	public float getVisitProb() {
		return visitProb;
	}

	public float getViewProb() {
		return viewProb;
	}

	public float getAddToCartProb() {
		return addToCartProb;
	}

	public float getCheckoutProb() {
		return checkoutProb;
	}

	public float getInitPaymentProb() {
		return initPaymentProb;
	}

	public float getFinishPaymentProb() {
		return finishPaymentProb;
	}

	public float getMeanProductViewCount() {
		return meanProductViewCount;
	}

	public float getStdvProductViewCount() {
		return stdvProductViewCount;
	}
	
	
}

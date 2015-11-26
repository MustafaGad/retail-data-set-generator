package com.mathUtilities;

import java.util.Random;

public class NormalDistribution implements ProbabilityDistribution {
	private Random random;
	private float minValue;
	private float maxValue;
	private float mean;
	private float stdv;
	
	public NormalDistribution(float mean, float stdv, float minValue, float maxValue) {
		this(mean, stdv, minValue, maxValue, Utilities.getSeed());
	}
	public NormalDistribution(float mean, float stdv, float minValue, float maxValue, long seed) {
		random = new Random(seed);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.mean = mean;
		this.stdv = stdv;
	}
	
	public float nextSample() {
		float retValue = (float)random.nextGaussian() * stdv + mean; 
		retValue = Math.max(retValue, minValue);
		retValue = Math.min(retValue, maxValue);
		return retValue;
	}
}

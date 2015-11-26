package com.mathUtilities;

import java.util.Random;

public class UniformDistribution implements ProbabilityDistribution {
	private Random random;
	private float minValue;
	private float maxValue;
	
	public UniformDistribution(float minValue, float maxValue) {
		this(minValue, maxValue, Utilities.getSeed());
	}
	
	public UniformDistribution(float minValue, float maxValue, long seed) {
		random = new Random(seed);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public float nextSample() {
		float sample = minValue + random.nextFloat() * (maxValue - minValue);
		return sample;
	}
}

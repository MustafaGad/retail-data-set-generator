package com.mathUtilities;

import java.util.Random;

public class BernoulliExperiment implements RandomExperiment<Boolean>{
	private float trueProb;
	private Random random;
	public BernoulliExperiment(float trueProb) {
		this.trueProb = trueProb;
		random = new Random(Utilities.getSeed());
	}
	@Override
	public Boolean nextRand() {
		
		return random.nextFloat() <= trueProb;
	}

}

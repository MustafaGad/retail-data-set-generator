package com.mathUtilities;

import java.util.HashMap;

public class RandomExperimentFactory {
	
	public static RandomExperiment<Boolean> getRandomExperiment(float trueProb) {
		return new BernoulliExperiment(trueProb);
	}
	
	public static RandomExperiment<Float> getRandomExperiment(float [][] distr) {
		return new BucketsRandomDistrExperiment(distr);
	}
	
	public static RandomExperiment<Integer> getRandomExperiment(float [] distr) {
		HashMap<Integer, Float> map = new HashMap<Integer, Float>();
		for(int i=0; i < distr.length; i++) {
			map.put(new Integer(i), distr[i]);
		}

		return new CategorialRandomExperiment<Integer>(map);
	}
	
	public static RandomExperiment<?> getRandomExperiment(HashMap<?, Float> map) {
		return new CategorialRandomExperiment<>(map);
	}

}

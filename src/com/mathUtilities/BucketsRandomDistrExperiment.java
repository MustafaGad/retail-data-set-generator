package com.mathUtilities;

import java.util.HashMap;

public class BucketsRandomDistrExperiment implements RandomExperiment<Float>{
	private CategorialRandomExperiment<Integer> cRE;
	private float [][] distr;
	//prob[i][0] --> bucket i inclusive start point && bucket i-1 exclusive end point.
	//prob[i][1] --> bucket i probability.
	//prob.length = number of buckets +1. Last row of "prob" carries last bucket end point and dummy probability to be ignored.
	public BucketsRandomDistrExperiment(float [][] distr) {
		this.distr = distr;
		HashMap<Integer, Float> map = new HashMap<Integer, Float>();
		for(int i=0; i < distr.length - 1; i++) {
			map.put(new Integer(i), distr[i][1]);
		}
		cRE = new CategorialRandomExperiment<>(map);
		
	}
	
	@Override
	public Float nextRand() {
		int randomBucketIndex = cRE.nextRand();
		float randomValue = distr[randomBucketIndex][0] + (float)Math.random() * (distr[randomBucketIndex + 1][0] - distr[randomBucketIndex][0]);
		
		return randomValue;
	}
}

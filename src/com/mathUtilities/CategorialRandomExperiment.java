package com.mathUtilities;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class CategorialRandomExperiment<k> implements RandomExperiment<k>{
	private HashMap<Integer, k> invertedIndex;
	private Random random;
	private float [] probRange;
	
	public  CategorialRandomExperiment(HashMap<k, Float> map) {
		invertedIndex = new HashMap<Integer, k>();
		random = new Random(Utilities.getSeed());
		
		Set<k> keySet = map.keySet();
		probRange = new float[keySet.size()];
		int i = 0;
		for(k key: keySet) {
			if(i == 0)
				probRange[i] = map.get(key);
			else
				probRange[i] = map.get(key) + probRange[i-1];
			invertedIndex.put(new Integer(i), key);
			i++;
		}
	}
	
//	public CategorialRandomExperiment(float [] prob) {
//		HashMap<Integer, Float> map = new HashMap<Integer, Float>();
//		for(int i=0; i < prob.length; i++) {
//			map.put(new Integer(i), prob[i]);
//		}
//
//	}
	@Override
	public k nextRand() {
		float r = random.nextFloat();
		for(int i=probRange.length - 1; i > 0; i--) {
			if(r <= probRange[i] && r > probRange[i-1])
				return invertedIndex.get(new Integer(i));
			else
				if(i-1 == 0)
					return invertedIndex.get(new Integer(0));
		}
		return null;
	}
	public static void main(String [] args) {
//		HashMap<Integer, Float> map = new HashMap<Integer, Float> ();
//		map.put(new Integer(0), 0.9f);
//		map.put(new Integer(1), 0.1f);
//		CategorialRandomExperiment<Integer> re = new CategorialRandomExperiment<>(map);
//		int zeroCount = 0, oneCount = 0;
//		for(int i=0; i<100000; i++) {
//			Integer rand = re.nextRand();
//			if(rand.intValue() == 0)
//				zeroCount++;
//			else {
//				if(rand.intValue() == 1)
//					oneCount++;
//				else
//				{
//					System.out.println("ERROR " + rand.intValue());
//					return;
//				}
//			}
//			System.out.println("Zero " + zeroCount/(float)(zeroCount + oneCount) + " One " + oneCount/(float)(zeroCount + oneCount));
//		}
	}
}

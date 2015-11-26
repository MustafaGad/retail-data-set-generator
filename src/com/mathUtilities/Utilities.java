package com.mathUtilities;

public class Utilities {
	private static long seed;
	
	public static void setSeed(long desiredSeed) {
		seed = desiredSeed;
	}
	
	public static long getSeed() {
		return seed++;
	}
}

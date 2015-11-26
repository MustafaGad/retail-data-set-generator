package com;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class threadTest {
	public static Lock testLock;
	public static Condition testCondition;
	public static ArrayList<String> testObj;
	
	public enum colours {
		RED, Green, Blue;
	}
	public static void main(String [] main) throws InterruptedException {
		System.out.println(colours.valueOf("Blue"));
	}
	

}

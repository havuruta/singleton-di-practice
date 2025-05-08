package com.springpractice.legacy;

public class LegacyCounter {
	private static final LegacyCounter INSTANCE = new LegacyCounter();
	private int value;
	
	private LegacyCounter() { }
	
	public static LegacyCounter getInstance() {
		return INSTANCE;
	}
	
	public int increment() { return ++value; }
	
	public void reset()    { value = 0; }
}
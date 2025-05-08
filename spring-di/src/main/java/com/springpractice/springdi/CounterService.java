package com.springpractice.springdi;

public class CounterService {
	private final Counter counter;
	public CounterService(Counter counter) { this.counter = counter; }
	public int process() { return counter.increment(); }
}
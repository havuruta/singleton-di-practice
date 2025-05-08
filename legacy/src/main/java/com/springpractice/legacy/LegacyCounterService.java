package com.springpractice.legacy;

public class LegacyCounterService {
	public String process() {
		int n = LegacyCounter.getInstance().increment();   //직접 접근
		return "count=" + n;
	}
}
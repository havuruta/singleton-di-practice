package com.springpractice.springdi;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component                 // 빈으로 등록
public class InMemoryCounter implements Counter {
	private final AtomicInteger value = new AtomicInteger();
	
	@Override public int increment() { return value.incrementAndGet(); }
	@Override public void reset()    { value.set(0); }
}
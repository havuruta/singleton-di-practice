package com.springpractice.springdi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Mock 이란?
 * ① 실제 객체를 흉내 내는 가짜(stand-in)
 * ② ‘예상 호출·반환 값’만 지정해서 단위 로직을 고립시킨다
 * ③ 빠르고(외부 I/O 제거) 재현성 있는 테스트를 만든다
 */

@ExtendWith(MockitoExtension.class)
class CounterServiceTest {
	
	// 1) Counter 인터페이스를 기반으로 손쉽게 가짜 생성
	@Mock
	Counter counter;
	
	// 2) 가짜를 주입받은 서비스 객체 생성 (DI)
	@InjectMocks
	CounterService service;
	
	@Test
	@DisplayName("Mock을 사용해 비즈니스 로직만 단위 테스트")
	void 비즈니스_로직만_검증() {
		/* 3) 가짜의 ‘행동 시나리오’ 정의 */
		when(counter.increment()).thenReturn(42);
		
		/* 4) 서비스 메서드 호출 → 실제 DB/네트워크 없이 동작 */
		String result = String.valueOf(service.process());
		
		/* 5) 결과·상호작용 모두 검증 */
		assertEquals("42", result);
		verify(counter).increment();          // 호출 여부 확인
	}

    /*
       - 상태 공유 걱정 無 (가짜마다 독립 메모리)
       - 외부 시스템 의존 제거 → 실행 속도 ↑, 실패 원인 파악 ↓ */
}

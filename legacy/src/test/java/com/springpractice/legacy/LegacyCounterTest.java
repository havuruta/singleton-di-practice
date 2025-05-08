package com.springpractice.legacy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 1) 전역(Singleton) 상태가 테스트간 누수되는 문제 체험
 * 2) Mock 을 쓸 수 없을 때 어떤 제약이 생기는지 느낀다
 */

@TestMethodOrder(MethodOrderer.Random.class)
class LegacyCounterTest {
	
	// 주석 처리하고 테스트 해보기
	@BeforeEach
	void clean() { LegacyCounter.getInstance().reset(); }
	
	@Test
	@DisplayName("싱글톤 카운터의 첫 번째 증가 테스트")
	void 첫번째() {
		Assertions.assertEquals(1, LegacyCounter.getInstance().increment());
	}
	
	@Test
	@DisplayName("싱글톤 카운터의 상태 누수 테스트")
	void 두번째_순서_바뀌면_실패() {
		// reset() 호출 안 함 → 테스트 간 상태 누수
		Assertions.assertEquals(1, LegacyCounter.getInstance().increment());
	}
	 /* 결과
       - 테스트끼리 철저히 독립시키기 어렵다
       - 의존 객체를 가짜로 바꿀 방법이 없다 (PowerMock·리플렉션 등 특수기술 제외) */
}
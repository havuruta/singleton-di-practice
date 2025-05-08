# singleton-demo 실습 가이드

> **목표**: 전통 싱글톤 패턴의 한계를 체험하고, Spring DI + Mock 을 통해 어떻게 해결되는지 단계별로 학습한다.

## 프로젝트 구조

```
singleton-demo/
├── settings.gradle
├── legacy/          # 전역 싱글톤 구현 & 테스트
│   └── src/
│       ├── main/java/com/example/legacy/
│       │   └── LegacyCounter.java
│       └── test/java/com/example/legacy/
│           └── LegacyCounterRandomTest.java
└── di/              # Spring Boot + DI + Mockito
    └── src/
        ├── main/java/com/example/di/
        │   ├── Counter.java
        │   ├── InMemoryCounter.java
        │   └── CounterService.java
        └── test/java/com/example/di/
            └── CounterServiceTest.java
```

### 모듈 요약

| 모듈       | 설명                                          | 학습 키워드                |
| -------- | ------------------------------------------- | --------------------- |
| `legacy` | 순수 자바 싱글톤(`LegacyCounter`) – 전역 상태 보존       | 전역 객체, 결합도, 테스트 누수    |
| `di`     | Spring Boot 컨테이너가 관리하는 싱글톤 스코프 빈 + 인터페이스 DI | IoC, DI, Mock, 테스트 격리 |

---

## 빌드 & 실행

```bash
# 전체 컴파일·테스트
./gradlew clean build

# 레거시 모듈만 테스트
./gradlew :legacy:test

# di 모듈만 테스트
./gradlew :spring-di:test

```

> **JDK 17** 이상 필요. 별도 DB·외부 의존성 없음.

---

## 테스트

### 1. 레거시 싱글톤 테스트 깨 보기 (`legacy`)

파일: `LegacyCounterRandomTest.java`

```java
@TestMethodOrder(MethodOrderer.Random.class)
class LegacyCounterRandomTest {
    @Test void first()  { assertEquals(1, LegacyCounter.getInstance().increment()); }
    @Test void second() { assertEquals(1, LegacyCounter.getInstance().increment()); }
}
```

1. **기본 실행** – 때로는 녹색, 때로는 빨간색<br>
2. `./gradlew :legacy:test --rerun` 여러 번 돌려 보며 랜덤 순서로 실패를 체감
3. `LegacyCounter` 가 **전역 상태**를 갖기 때문에 순서·병렬성에 취약함을 확인

> **포인트**: 테스트 격리를 위해 매번 `reset()` 을 넣는 것은 *임시 땜질*에 불과. 구조적 문제는 결합도.

---

### 2. DI + Mock 으로 고립 테스트 (`di`)

파일: `CounterServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class CounterServiceTest {
    @Mock Counter counter;           // 가짜 생성
    @InjectMocks CounterService svc; // DI로 자동 주입

    @Test void logic() {
        when(counter.increment()).thenReturn(42);
        assertEquals(42, svc.process());
        verify(counter).increment();
    }
}
```

**체험 흐름**

1. Mock 으로 원하는 값(42) 설정 → 외부 상태·I/O 없이 테스트 OK
2. 전역 객체가 없으니 순서 · 병렬 실행에도 **항상 녹색**
3. 협력 객체를 다른 구현으로 바꾸기도 한 줄(`when...thenReturn`)이면 끝

---

## 토비의 스프링 1-6과의 관계는?

| 토비 지적         | 실습에서 확인                             | DI 해결책                       |
| ------------- | ----------------------------------- | ---------------------------- |
| 전역 객체로 의존성 고착 | `LegacyCounter.getInstance()` 직접 호출 | `Counter` 인터페이스 주입, Mock 교체  |
| 라이프사이클 제어 불가  | 전역 상태 누수, 초기화 강제                    | Spring 컨테이너가 싱글톤 빈 관리        |
| 멀티스레드 위험      | `value++` 비동기 경쟁                    | `AtomicInteger`, stateless 빈 |
| 전략 교체 불편      | 소스 수정·검색 치환                         | 프로파일/빈 교체로 런타임 스왑            |

---

## 이것도 해보면 좋습니다.

1. **스레드 안전 비교**

   * `LegacyCounter` 를 `synchronized` 없이 JMH로 벤치마크 → 레이스 확인
   * DI 모듈에 `AtomicIntegerCounter`, `RedisCounter` 추가 후 빈 교체 실험
2. **프로파일 분리** (`@Profile("prod")` vs `"test"`)
3. **통합 테스트**

   * `CounterController` 추가 후 `MockMvc` 로 REST 호출 검증
4. **상태ful 싱글톤의 위험 실습**

   * DI 빈에 `value` 필드를 남겨 두고 병렬 테스트 run → 동시성 버그 노출

---

## 결론

* 전통 싱글톤은 *글로벌 상태, 테스트 곤란, 교체 난이도*의 문제가 존재한다.
* Spring DI + Mock 은 **의존성·라이프사이클·상태**를 분리하여 문제를 해소한다.
* 《토비의 스프링》의 “싱글톤 패턴의 구조적 한계 → IoC 컨테이너로 해결” 을 참조해서 구현되었습니다.
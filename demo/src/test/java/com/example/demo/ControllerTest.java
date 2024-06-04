package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ControllerTest {

    @BeforeAll
	static void initAll()
	{
		System.out.println("beforeAll");
	}
	
	@BeforeEach
	void init()
	{
		System.out.println("beforeEach");
	}
	
	@Test
	void succeedingTest()
	{
		System.out.println("succeedingTest");
	}
	
	// @Test
	// void failingTest()
	// {
	// 	fail("a failing test");
	// }
	
	@Test
	@Disabled("for demonstration purposes")
	void skippedTest()
	{
		System.out.println("skippedTest");
	}
	
	@Test
	void abortedTest()
	{
		assumeTrue("abc".contains("Z"));
		fail("test should have been aboret");
    }

	@Test
	void stringEqualTest() {
		char [] exoected = {'a','b','c'};
		char [] actual = "abc".toCharArray();

		assertArrayEquals(exoected, actual, "failure - byte arrays not same");
	}

	@Test
	void deltaEqualTest() {	
		double square = 2 * 2;
		double rectangle = 3 * 2;
		double delta = 3;

		assertEquals(square, rectangle, delta, "failure - doubles not same");
	}

	@Test
	void nullTest(){
		Object obj = null;
		assertNull(obj, "failure - object is null");
	}

	@Test
	void sameObjectTest(){
		String obj1 = "java";
		Optional<String> obj2 = Optional.of(obj1);

		assertSame(obj1, obj2.get(), "failure - objects not same");
	}

	@Test
	void assertAllTest(){
		Object obj = null;
		assertAll("heading",
			() -> assertEquals(4, 2 * 2),
			() -> assertEquals("java", "JAVA".toLowerCase()),
			() -> assertNull(obj, "failure - object is not null")
		);
	}

	@Test
	public void testExpectedException() {
		Exception exception = assertThrows(
			IllegalArgumentException.class, //예상하는 예외 타입
			() -> {
				throw new IllegalArgumentException("This is an illegal argument"); //예외 강제 발생
			}
		);

		assertEquals("This is an illegal argument", exception.getMessage()); //getMessage()로 예외 메시지 호출
	}

	@Test
	void asumtionTest(){
		String someString = "abc";
		assumingThat(
			someString.equals("abc"),
			() -> assertEquals(2 + 2, 4)
		);
	}
	@AfterAll
	static void endAll(){
		System.out.println("afterAll");
	}

	@AfterEach
	void end(){
		System.out.println("afterEach");
	}
}
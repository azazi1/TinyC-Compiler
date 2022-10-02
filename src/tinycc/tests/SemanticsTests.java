package tinycc.tests;

import org.junit.Test;
import prog2.tests.CompilerTests;

/**
 * Within this package you can implement your own tests that will
 * be run with the reference implementation.
 *
 * Note that no classes or interfaces will be available, except those initially
 * provided.
 * 
 * Do not write your own tests in this class. Use another class in this package.
 */
public class SemanticsTests extends CompilerTests {

	@Test
	public void testNegativeDerefInt() {
		final String code = "int foo() { return *1; }";
		checkCodeNegative(code,1,20);
	}

	@Test
	public void testNegativeRefInt() {
		final String code = "int* foo() { return &1; }";
		checkCodeNegative(code,1,21);
	}

	@Test
	public void testNegativeStringToInt() {
		final String code = "void foo(){ int x = \"abc\"; }";
		checkCodeNegative(code,1,17);
	}

	@Test
	public void testNegativeDerefToInt() {
		final String code = "void foo(){ int x = \"abc\"; }";
		checkCodeNegative(code,1,17);
	}

	@Test
	public void testNegativeDerefToPtr() {
		final String code = "void foo() { int x = 3; int* y = &x; int* z = *y; }";
		checkCodeNegative(code,1,43);
	}

	@Test
	public void testMultipleAssign() {
		final String code = ""
				+ "int a;\n"
				+ "char b;\n"
				+ "int c;\n"
				+ "int foo() {\n"
				+ "	a = b = c;\n"
				+ "	return a;\n"
				+ "}\n";
		checkCode(code);
	}

	@Test
	public void testInvalidFunctionCall() {
		final String code = ""
				+ "int foo(int x, int y) {\n"
				+ "		return x;\n"
				+ "	}\n"
				+ "int main() {\n"
				+ "	return foo(42, 42, 32);\n"
				+ "}\n";
		
		checkCodeNegative(code, 5, 12);
	}

	@Test
	public void testSimpleIf() {
		final String code = "void foo() { if(1) 3; }";
		checkCode(code);
	}

	@Test
	public void testNegativeBreakSimple() {
		final String code = "void foo() { break; }";
		checkCodeNegative(code,1,14);
	}

	@Test
	public void testNegativeBreak_1() {
		final String code = "void foo() { while(1) 3; break; }";
		checkCodeNegative(code,1,26);
	}

	@Test
	public void testBreakSimple() {
		final String code = "void foo() { while(1) break; }";
		checkCode(code);
	}

	@Test
	public void testBreakNestedLoop_1() {
		final String code = "void foo() { while(1) while(1) break; }";
		checkCode(code);
	}

	@Test
	public void testBreakNestedLoop_2() {
		final String code = "void foo() { while(1) { while(1) break; break; } }";
		checkCode(code);
	}

	@Test
	public void testBreakNestedBlock_1() {
		final String code = "void foo() { while(1) {3; {3; break;}} }";
		checkCode(code);
	}

	@Test
	public void testBreakNestedBlock_2() {
		final String code = "void foo() { while(1) if (1) break; else break; }";
		checkCode(code);
	}

	@Test
	public void testInvalidAfterBreak() {
		final String code = "void foo() { while(1) {break; int x = \"error\"; } }";
		checkCodeNegative(code, 1, 35);
	}

	@Test
	public void testWhile() {
		final String code = "void foo() { while(1) break; }";
		checkCode(code);
	}

	@Test
	public void testNegativeParamShadowing() {
		final String code = "void foo(int x) { int x; }";
		checkCodeNegative(code,1,23);
	}

	@Test
	public void testParamShadowing() {
		final String code = "void foo(int x) { { int x; } }";
		checkCode(code);
	}

	@Test
	public void testCall_Convertion() {
		final String code = ""
				+ "int foo(int x, int y) {\n"
				+ "		return x;\n"
				+ "	}\n"
				+ "int main() {\n"
				+ " char x = 1;\n"
				+ "	return foo(x, x);\n"
				+ "}\n";
				checkCode(code);
	}

}

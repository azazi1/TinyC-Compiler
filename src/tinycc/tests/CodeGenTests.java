package tinycc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import prog2.tests.CompilerTests;
import prog2.tests.MarsException;
import prog2.tests.MarsUtil;


public class CodeGenTests extends CompilerTests {
	
	@Test
	public void testFactorial() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n == 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return fact(n - 1) * n;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(7);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(5040, mars.run());
	}

	@Test
	public void testRem_tailRec() throws MarsException {
		final String code = "\n"
				+ "int remain(int x, int y) {\n"
				+ "	if (x <= y) {\n"
				+ "		return x;\n"
				+ "	} else {\n"
				+ "		return remain(x-y, y);\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return remain(36, 24);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(12, mars.run());
	}

	@Test
	public void testEuqlid_subt() throws MarsException {
		final String code = "\n"
				+ "int gcd(int a, int b) {\n"
				+ "	 while (a != b) {\n"
				+ "		if (a > b) {\n"
				+ "			a = a - b;\n"
				+ "		} else {\n"
				+ "			b = b - a;\n"
				+ "		}\n"
				+ "	 }\n"
				+ "	 return a;\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return gcd(36, 24);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(12, mars.run());
	}

	@Test
	public void testcall_localDecl() throws MarsException {
		final String code = "\n"
				+ "int foo(){"
				+ " return 2;"
				+ "}"
				+ "int main() {\n"
				+ " int x = foo();\n"
				+ " return x;"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}


	@Test
	public void testcall_AssignExprStmt() throws MarsException {
		final String code = "\n"
				+ "int foo(){"
				+ " return 3;"
				+ "}"
				+ "int main() {\n"
				+ " int y; \n"
				+ " y = foo();\n"
				+ " return y;"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(3, mars.run());
	}


	@Test
	public void testcall_parameter() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ " return x;"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(7-5+2) * 2;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(8, mars.run());
	}

	@Test
	public void testcall_parameter2() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ " return x;"
				+ "}"
				+ "int main() {\n"
				+ "	 return 4 * foo(2);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(8, mars.run());
	}

	@Test
	public void testcall_parameter3() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ " return x;"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(5) * foo(2) + foo(3);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(13, mars.run());
	}

	@Test
	public void testSizeofConstInt() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	return sizeof (1+1);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(4, mars.run());
	}

	@Test
	public void testSizeofVarInt1() throws MarsException {
		final String code = "\n"
				+ "int x;\n"
				+ "int main() {\n"
				+ "	return sizeof x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(4, mars.run());
	}

	@Test
	public void testSizeofVarInt2() throws MarsException {
		final String code = "\n"
				+ "int x;\n"
				+ "int main() {\n"
				+ "int* p = &x;\n"
				+ "	return sizeof (*p);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(4, mars.run());
	}

	@Test
	public void testLocalVariables() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "int x = 2;\n"
				+ "int y = 3;\n"
				+ "	return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testMultiScopes() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ " int x = 2;\n"
				+ " {\n"
				+ "   int y = 3;\n"
				+ "   return x;\n"
				+ " }\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testIfGreaterEqual() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 1;\n"
				+ "	if (x>=1) return 97;\n"
				+ "	else return 21;\n"
				+ "	 return 0;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(97, mars.run());
	}

	@Test
	public void testIfEqual() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 1;\n"
				+ "	if (x==1) return 97;\n"
				+ "	else return 21;\n"
				+ "	 return 0;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(97, mars.run());
	}

	@Test
	public void testIfLessEqual() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 1;\n"
				+ "	if (x<=1) return 97;\n"
				+ "	else return 21;\n"
				+ "	 return 0;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(97, mars.run());
	}

	@Test
	public void testIfLessThan() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 1;\n"
				+ "	if (x<1) return 97;\n"
				+ "	else return 21;\n"
				+ "	 return 0;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(21, mars.run());
	}

	@Test
	public void testIfGreaterThan() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 1;\n"
				+ "	if (x>1) return 97;\n"
				+ "	else return 21;\n"
				+ "	 return 0;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(21, mars.run());
	}

	@Test
	public void testIfUnequal() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 int x = 1;\n"
				+ "	 if (x!=1) return 97;\n"
				+ "	 else return 21;\n"
				+ "	 return 0;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(21, mars.run());
	}

	@Test
	public void testExpression() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 int x = 1*1 + 1*1 + 1*1 - 1*1;\n"
				+ "	 return 8*5 + 19 - 1;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(58, mars.run());
	}

	@Test
	public void testExpression_Const2() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 return 1*5*7 + 1*19 + 3*1 - 2*4;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(49, mars.run());
	}

	@Test
	public void testExpression_Var1() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 int x = (2*3 - 5*7 + 10*13) - (5*5*4);\n"
				+ "	 return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testExpression_Var2() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 int x = 8*5 + 19 - 1;\n"
				+ "	 return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(58, mars.run());
	}

	@Test
	public void testExpression_Var3() throws MarsException {
		final String code = "\n"
				+ "int main(int x, int y, int z, int w) {\n"
				+ "	 x = 2*3;\n"
				+ "	 y = 5*7;\n"
				+ "	 z = 10*13;\n"
				+ "	 w = 5*5;\n"
				+ "	 int v = 20/5;\n"
				+ "	 int u = (x - y + z) - (w*v);\n"
				+ "	 return (x - y + z) - (w*v) + u * u;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testSimpleCall() throws MarsException {
		final String code = "\n"
				+ "int foo() {\n"
				+ "	 return 42;\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	 return foo();\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(42, mars.run());
	}

	@Test
	public void testSimpleString() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 return sizeof \"Hello World!\";\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(13, mars.run());
	}

	@Test
	public void testSimpleStringVar() throws MarsException {
		final String code = "\n"
				+ "char main() {\n"
				+ "  char* str = \"Hello World!\";\n"
				+ "	 return  *str;\n"
				// it should return asciiCode ('H') = 72  
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(72, mars.run());
	}

	@Test
	public void testSimpleString_CharSum() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "  char* str = \"Hello World!\";\n"
				+ "	 return  (*str) + (*(str + 1));\n"
				// it should return asciiCode ('H') + asciiCode ('e') = 72 + 101 
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(173, mars.run());
	}

	@Test
	public void testSimpleString_globalVar() throws MarsException {
		final String code = "\n"
				+ "char* str;\n"
				+ "void foo() {\n"
				+ "  str = \"Hello World!\";\n"
				+ "	 return;\n"
				+ "}\n"
				+ "char main() {\n"
				+ "	 foo();\n"
				+ "	 return *str;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(72, mars.run());
	}

	@Test
	public void testSimpleString_() throws MarsException {
		final String code = "\n"
				+ "char* str;\n"
				+ "char* foo() {\n"
				+ "  str = \"Hello World!\";\n"
				+ "	 return str;\n"
				+ "}\n"
				+ "char main() {\n"
				+ "	 return *foo();\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(72, mars.run());
	}

	@Test
	public void testSizeofConstChar() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 char x = 1;\n"
				+ "	 char y = 3;\n"
				+ "	return sizeof x + sizeof y;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testReturnChar() throws MarsException {
		final String code = "\n"
				+ "char main() {\n"
				+ "	 char x = \'A\';\n"
				+ "	return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(65, mars.run());
	}
	
	@Test
	public void testReturnCharToInt() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	 char x = \'A\';\n"
				+ "	return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(65, mars.run());
	}

	@Test
	public void testCallAsciiChar() throws MarsException {
		final String code = "\n"
				+ "char foo() {\n"
				+ "  char c = \'H\';\n"
				+ "	 return c;\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	 int x = foo();\n"
				+ "	return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(72, mars.run());
	}

}

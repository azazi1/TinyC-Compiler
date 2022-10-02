package tinycc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import prog2.tests.CodegenExercise;
import prog2.tests.CompilerTests;
import prog2.tests.MarsException;
import prog2.tests.MarsUtil;
import prog2.tests.PublicTest;

public class Codegen_Khal extends CompilerTests implements PublicTest, CodegenExercise {

	@Test (timeout = 40000)
	public void testSimple() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	return 54;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(54, mars.run());
	}

	@Test
	public void testExpression_minus() throws MarsException {
		final String code = "int main(){ \n"
							+ "int x = 5-4;"
							+ "return 5-4;"
							+"}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testExpression_mul() throws MarsException {
		final String code = "int main(){ \n"
							+ "int x = 5*5;"
							+ "return x;"
							+"}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(25, mars.run());
	}


	@Test
	public void testExpression_while() throws MarsException {
		final String code = """
					int main(){
						int x =0;
						while(x<5){
							x=x+1;
						}
						return x;
					}
				""";
		final MarsUtil mars = prepareCode(code);
		assertEquals(5, mars.run());
	}


	@Test
	public void testExpression_if() throws MarsException {
		final String code = """
					int main(){
						int x =0;
						if (x) x=2; else x=3;
						return x;
					}
				""";
		final MarsUtil mars = prepareCode(code);
		assertEquals(3, mars.run());
	}
	

	@Test
	public void testExpression_plusMinus() throws MarsException {
		final String code = "int main(){ \n"
							+ "int x = (5+5)-1;"
							+ "return x;"
							+"}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(9, mars.run());
	}

	@Test
	public void testExpression_sizeOf() throws MarsException{
		final String code = "int main(){ \n"
					    + "int x=2;"
						+ "return sizeof(x);"
						+"}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(4, mars.run());
	}

	@Test
	public void testExpression_sizeOf1() throws MarsException{
		final String code = "int main(){ \n"
					    + "int x=2;"
						+ "return 2 * sizeof(x);"
						+"}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(8, mars.run());
	}

	@Test
	public void testExpression_sizeOfChar() throws MarsException{
		final String code = "int main(){ \n"
					    + "char x=2;"
						+ "return  sizeof(x);"
						+"}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testExpression_sizeOfPtr() throws MarsException{
		final String code = "int* ptr;"
						+ "int main(){ \n"
					    + "char x=2;"
						+ "return  sizeof(ptr);"
						+"}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(4, mars.run());
	}
	@Test
	public void testExpression_ptr() throws MarsException{
		final String code = """
			     int main() {
				 int x=56;
				 int *ap;
				 int **app;
				 ap = &x;
				 app= &ap;
				 *ap = 43;
				 *(*app) = 13;
				 return *(*app);
				 }
				""";
		final MarsUtil mars = prepareCode(code);
		assertEquals(13, mars.run());
		}
	
	


	@Test
	public void testFactorial() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n == 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return n * fact(n - 1);\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(7);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(5040, mars.run());
	}

		@Test
	public void testConditionEqualF() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n == 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(7);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testConditionEqualT() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n == 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(0);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}


	@Test
	public void testConditionGreaterT() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n > 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(7);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testConditionGreaterF() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n > 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(0);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testConditionLessT() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n < 5) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(1);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testConditionLessF() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n < 5) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(7);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testConditionUnequalT() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n != 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(7);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}


	@Test
	public void testConditionUnequalF() throws MarsException {
		final String code = "\n"
				+ "int fact(int n) {\n"
				+ "	if (n != 0) {\n"
				+ "		return 1;\n"
				+ "	} else {\n"
				+ "		return 2;\n"
				+ "	}\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	return fact(0);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testIf() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 1;\n"
				+ "	if (x) return 97;\n"
				+ "	else return 21;\n"
				+ "	 return 0;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(97, mars.run());
	}

	@Test
	public void testcall1() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ "return x;"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(5-1);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(4, mars.run());
	}

	@Test
	public void testReturn2() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ "if( x>= 2) {\n"
				+ "return 2; }"
				+ "else {return 4;}"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(2);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

		@Test
	public void testReturn3() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ "if( x>= 2) {\n"
				+ "return 2; }"
				+ "else {return 4;}"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(1);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(4, mars.run());
	}


	@Test
	public void testReturn4() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ "if( x <= 2) {\n"
				+ "return 2; }"
				+ "else {return 4;}"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(2);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

		@Test
	public void testReturn5() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ "if( x <= 2) {\n"
				+ "return 2; }"
				+ "else {return 4;}"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(1);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testcall3() throws MarsException {
		final String code = "\n"
				+ "int foo(){"
				+ " return 2;"
				+ "}"
				+ "int main() {\n"
				+ " int x= foo();\n"
				+ " return x;"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}


	@Test
	public void testcall4() throws MarsException {
		final String code = "\n"
				+ "int foo(){"
				+ " return 2;"
				+ "}"
				+ "int main() {\n"
				+ " int x; \n"
				+ " x = foo();\n"
				+ " return x;"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}


	@Test
	public void testcall5() throws MarsException {
		final String code = "\n"
				+ "int foo(int x){"
				+ " return x;"
				+ "}"
				+ "int main() {\n"
				+ "	 return foo(5-1) * 2;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(8, mars.run());
	}

	@Test
	public void testExpressionSimple() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "int x= 4/2 ;\n"
				+ "return x;"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}


	@Test
	public void testExp2() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 4 ;\n"
				+ "	return (x<0);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(0, mars.run());
	}


	@Test
	public void testExp3() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 4 ;\n"
				+ "	return (x>0);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testExpPtrSimple() throws MarsException {
		final String code = "\n"
				+ " int main() {\n"
				+ "	int* x;\n"
				+ " int y=3;\n"
				+ " x=&y;"
				+ "	return (*x);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(3, mars.run());
	}
	@Test
	public void testExpPtr() throws MarsException {
		final String code = "\n"
				+ " int main() {\n"
				+ "	int* x;\n"
				+ " int y=3;\n"
				+ " x=&y;"
				+ "int z;"
				+ "z=*x;"
				+ "	return z;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(3, mars.run());
	}

	


	@Test
	public void testAssign() throws MarsException {
		final String code = "\n"
				+ " int main() {\n"
				+ "	int x;\n"
				+ " int y=3;\n"
				+ " x=y;"
				+ "	return (x);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(3, mars.run());
	}
	@Test
	public void testWhile() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 0;\n"
				+ "	while (x != 37) x = x + 1;\n"
				+ "	return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(37, mars.run());
	}

	@Test

	public void testCall() throws MarsException {
		final String code = "\n"
				+ "int foo(int a, int b) {"
				+ "	return a + b;"
				+ "}"
				+ "int main() {\n"
				+ "	return foo(20, 4);"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(24, mars.run());
	}


	@Test
	public void testPointerSimple() throws MarsException {
		final String code = ""
				+ "void* get_scratch();\n"
				+ "int main() {\n"
				+ "	void *scratch = get_scratch();"
				+ "	int  *a       = scratch;\n"
				+ "	char *b       = scratch;\n"
				+ "	*(b + 4) = 100;\n"
				+ "	return *(a + 1);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(100, mars.run());
	}

	@Test
	public void testLocalVar() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	int x = 68;\n"
				+ "	return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(68, mars.run());
	}

	@Test
	public void testSimpleReturn() throws MarsException {
		final String code = "\n"
				+ "int main() {\n"
				+ "	return 2;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	@Test
	public void testSimpleReturn_1() throws MarsException {
		final String code = "\n"
				+ "void main() {\n"
				+ "	return;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(0, mars.run());
	}


	@Test
	public void testSimpleReturn_2() throws MarsException {
		final String code = "\n"
				+ "int foo(){\n"
				+ "return 1;\n"
				+ "}"
				+ "int main() {\n"
				+ "	return foo() ;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testGlobalVar() throws MarsException {
		final String code = "\n"
				+ "int x;\n"
				+ "int main() {\n"
				+ "	x = 93;\n"
				+ "	return x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(93, mars.run());
	}


	@Test
	public void testGlobalVar1() throws MarsException {
		final String code = "\n"
				+ "int x;\n"
				+ "int main() {\n"
				+ "	x = 10;\n"
				+ "	return x+x;\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(20, mars.run());
	}

	@Test
	public void testLocalandGlobal() throws MarsException {
		final String code = "\n"
				+ "int x;\n"
				+ "int main(int y) {\n"
				+ "	x = 10;\n"
				+ " y = 15;"
				+ "	return (x+y) * sizeof(y);\n"
				+ "}\n";
		final MarsUtil mars = prepareCode(code);
		assertEquals(100, mars.run());
	}

	@Test
	public void testNestedScope() throws MarsException {
		String code = "int main(){\n"
					+ "int x = 3;\n {"
					+ "int x = 5;\n{"
					+ "int x = 10;\n"
					+ "return x;"
					+ "}\n"
					+ "}\n"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(10, mars.run());
	}

	

	@Test 
	public void testReturnComplex() throws MarsException {
		String code = "int x;"
					 + "int main(int n){\n"
					 + "while (n<10){\n"
					 + "x=x+1;\n"
					 + "if(x ==5) return x;"
					 + "}"
					 + "return x;\n"
					 + "}";

		final MarsUtil mars = prepareCode(code);
		assertEquals(5, mars.run());
	}

	@Test
	public void testNestedScope1() throws MarsException {
		String code = "int main(){\n"
					+ "int x = 3;\n {"
					+ "int y = 5;\n{"
					+ "int x = 10;\n"
					+ "return y;"
					+ "}\n"
					+ "}\n"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(5, mars.run());
	}

	@Test
	public void testNestedScope2() throws MarsException {
		String code = "int w;"
					+ "int main(){\n"
					+ "int x = 3;\n {"
					+ " w = 15;\n"
					+ "int y = 5;\n{"
					+ "int x = 10;\n"
					+ "return y;"
					+ "}\n"
					+ "}\n"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(5, mars.run());
	}


	@Test
	public void testNestedScope3() throws MarsException {
		String code = "int w;"
					+ "int main(){\n"
					+ "int x = 3;\n {"
					+ " w = 15;\n"
					+ "int y = 5;\n{"
					+ "int x = 10;\n"
					+ "return y+x;"
					+ "}\n"
					+ "}\n"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(15, mars.run());
	}

	@Test
	public void testNestedScope4() throws MarsException {
		String code = "int w;"
					+ "int main(){\n"
					+ "int x = 3;\n {"
					+ " w = 15;\n"
					+ "int y = 5;\n{"
					+ "int x = 10;\n"
					+ "int y = 2;\n"
					+ "int z = 2;\n"
					+ "return y+x+z;"
					+ "}\n"
					+ "}\n"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(14, mars.run());
	}

	@Test
	public void testEuclid() throws MarsException {
		String code = "int gcd(int a,int b){"
					+ "if(a == 0) {\n"
					+ "return b;\n"
					+ "}"
					+ "else {\n"
					+ "return gcd(b-a,a);\n"
					+ "}"
					+ "}"
					+ "int main(){\n"
					+ "return gcd(1,1);"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}

	@Test
	public void testFactWhile() throws MarsException {
		String code = """
				int fact(int n){
					int res;
					res = 1;
					while (n>1){
						res = res *n;
						n = n-1;
					}
					return res;
				}
				int main(){
					return fact(5);
				}
				""";
		final MarsUtil mars = prepareCode(code);
		assertEquals(120, mars.run());
	}



	@Test
	public void testPointer() throws MarsException {
		String code = "int foo(){"
					+ "char c;\n"
					+ "char *pc;\n"
					+ "char **ppc;\n"
					+ "int i;\n"
					+ "int *pi;\n"
					+ "i = 41;\n"
					+ "return i-42;"
					+ "}"
					+ "int main(){"
					+ "return foo();\n"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(-1, mars.run());
	}

	@Test
	public void testExpression11() throws MarsException {
		String code = """
			int main(){
				int c;
				int *pc;
				int i;
				i=41;
				pc=&c;
				c=1;
				return *pc;
			}
				""";
		final MarsUtil mars = prepareCode(code);
		assertEquals(1, mars.run());
	}
	@Test
	public void testExpression1() throws MarsException {
		String code = "int main(){"
					+ "char c;\n"
					+ "char *pc;\n"
					+ "int i;\n"
					+ "i = 41;\n"
					+ "pc = &c;"
					+ "c = 1;"
					+ "return *pc + 1 +42;"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(44, mars.run());
	}

	@Test
	public void testPointer2() throws MarsException {
		String code = "int foo(){"
					+ "char c;\n"
					+ "int l=1;"
					+ "int *pc;\n"
					+ "char **ppc;\n"
					+ "int i;\n"
					+ "int *pi;\n"
					+ "i = 41;\n"
					+ "pc = &l;"
					+ "c = 1;"
					+ "int xx;"
					+ "xx = (*pc + 1)-42;"
					+ "return xx;"
					+ "}"
					+ "int main(){"
					+ "return foo();\n"
					+ "}";
		final MarsUtil mars = prepareCode(code);
		assertEquals(-40, mars.run());
	}

	@Test
	public void testPointerIdiot() throws MarsException {
		String code = """
				int main(){
					int x = 2;
					int res=0;
					int* px;
					px = &x;
					res = *px;
					return res;
				}
				""";;
		final MarsUtil mars = prepareCode(code);
		assertEquals(2, mars.run());
	}

	

}


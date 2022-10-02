package tinycc.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import prog2.tests.CompilerTests;
import tinycc.logic.solver.SolverResult;


public class VerificationTests extends CompilerTests {
	
	@Test
	public void testSimpleAssign() {
		final String code = "\n" +
				"int x;\n"+
				"int f() {" +
				"	x = 5;" +
				"	_Assert(x >= 5);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testIfAssign1() {
		final String code = "\n" +
				"int f(int x, int y, int z) {\n" +
				"	if (x <= z) {\n" +
				"		z = y - x;\n" +
				"	} else {\n" +
				"		y = x + z;\n" +
				"	}\n" +
				"	_Assert(y == x + z);\n" +
				"	return x;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testIfAssign2() {
		final String code = "\n" +
				"int min(int x, int y) {\n" +
				"   int res;\n" +
				"	if (x < y) {\n" +
				"		res = x;\n" +
				"	} else {\n" +
				"		res = y;\n" +
				"	}\n" +
				"	_Assert(res <= x && res <= y);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testIfAssign3() {
		final String code = "\n" +
				"int main(int x, int y) {\n" +
				"	_Assume(x == y);\n" +
				"   y = x;\n" +
				"	if (x < 0) {\n" +
				"		y = 0 - x;\n" +
				"		_Assume(x == y && y > 0);\n" +
				"	} else {}\n" +
				"	_Assert(y >= 0);\n" +
				"	return x;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testAbs() {
		final String code = "\n" +
				"int abs(int x) {\n" +
				"	if (x < 0) {\n" +
				"		x = 0 - x;\n" +
				"	}\n" +
				"	_Assert(x >= 0 );\n" +
				"	return x;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testWhile_nested() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c; k) {\n" +
				"		while (0) \n" +
				"		_Invariant ((res == 2*c && c<=x) && (c<x) && (x-c >= 0 && x-c <= k+1)) \n" +
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testWhile_simple() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c) {\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testWhile_WrongInv() {
		final String code = 
					"""
					int f(int x,int y){
						int c = 0;
						int z = x;
						while(y>0)_Invariant(z == x - c && y >= 0){
							c = c + 1;
							y = y - 1;
							x = x + 1;
						}
						_Assert(z == x - c);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_notSufficient() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c) {\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_WeakestInv() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (1) {\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_false() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (0) {\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_nested1() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c; k) {\n" +
				"		while (0) \n" +
				// outerloop's cond and loop_bound missed
				"		_Invariant ((res == 2*c && c<=x)) \n" + 
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_nested2() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c; k) {\n" +
				"		while (0) \n" +
						// outerloop's bound forgotten
				"		_Invariant ((res == 2*c && c<=x) && (c < x)) \n" + 
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_nested3() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c; k) {\n" +
				"		while (0) \n" +
				// wrong inner Invariant: false
				"		_Invariant (0) \n" + 
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_nested4() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c; k) {\n" +
				"		while (0) \n" +
				// too weak inner Invariant: true
				"		_Invariant (1) \n" + 
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_nested5() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c; k) {\n" +
				"		while (0) \n" +
				// outerloop's cond missed
				"		_Invariant ((res == 2*c && c<=x) && (x-c >= 0 && x-c <= k+1)) \n" + 
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_WrongInv_nestedShadowing() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c) {\n" +
				"   	int k;\n" +
				"		while (0) \n" +
				"		_Invariant ((res == 2*c && c<=x) && (c<x) && (x-c >= 0 && x-c <= k+1)) \n" +
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhile_nested_noLoopBound() {
		final String code = "\n" +
				"int mul2(int x) {\n" +
				"	_Assume(x >= 0);\n" +
				"   int res = 0;\n" +
				"   int c = 0;\n" +
				"	while (c < x) \n" +
				"	_Invariant (res == 2*c && c <= x) \n" +
				"	_Term (x - c) {\n" +
				"		while (0) \n" +
				"		_Invariant ((res == 2*c && c<=x) && (c<x)) \n" +
				"		_Term (0) {}\n" +
				"		c = c + 1;\n" +
				"		res = res + 2;\n" +
				"	}\n" +
				"	_Assert(res == 2 * x);\n" +
				"	return res;\n" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}
}

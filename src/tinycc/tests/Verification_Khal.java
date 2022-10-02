package tinycc.tests;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;



import org.junit.Test;

import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;
import prog2.tests.WPExercise;

import tinycc.logic.solver.SolverResult;

public class Verification_Khal extends CompilerTests implements WPExercise, PublicTest {

	@Test
	public void testSemaAssume() {
		final String code = "\n" +
				"int f(int x) {" +
				"	_Assume(x > 0);" +
				"	return x;" +
				"}\n";
		checkCode(code);
	}

	@Test
	public void testExampleReject() {
		final String code = "\n" +
				"void f(int y, int z) {" +
				"	y = (z = 5);" +
				"}\n";
				checkCode(code);
		assertThrows(Exception.class, () -> computeVerificationResult(code));
	}


	@Test
	public void testSimpleAssign() {
		final String code = "\n" +
				"int f(int x) {" +
				"	x = 5;" +
				"	_Assert(x >= 5);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testSimpleAssignWrong() {
		final String code = "\n" +
				"int f(int x) {" +
				"	x = 5;" +
				"	_Assert(x < 5);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testSimpleAssume() {
		final String code = "\n" +
				"int f(int x) {" +
				"	_Assume(x == 3);" +
				"	_Assert(x < 5);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}


	@Test
	public void testSwapSimple() {
		final String code = "\n" +
				"int f(int x, int y,int t) {" +
				"	_Assume(x == 2);" +
				"	t = x;" +
				"    y = t;" +
				"	_Assert(y == 2);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}
	

	@Test
	public void testPDFExample() {
		final String code = "\n" +
				"int f(int x, int y) {" +
				"	_Assume(y == 5);" +
				"	x = 5;" +
				"	_Assert(x == y);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}


	@Test
	public void testIntegerDivision() {
		final String code = 
					"""
			int integerDiv(int x,int y,int x1,int q){
				_Assume(x1 == x);
				q = 0;
				while(x>=y) _Invariant(x1==q*y+x){
					x=x-y;
					q=q+1;
				}
				_Assert(x1==q*y+x && x<y);
				return x;
			}
					""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testMinUsingDecrement() {
		final String code = 
					"""
					int f(int x,int y,int x1,int y1){
						_Assume(x==x1 && y==y1);
						while(x!=0)_Invariant(y-x==y1-x1){
							x=x-1;
							y=y-1;
						}
						_Assert(y==y1-x1);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testMinUsingDecrement2() {
		final String code = 
					"""
					int f(int x,int y,int x1,int y1){
						_Assume(x==x1 && y==y1);
						while(x)_Invariant(y-x==y1-x1){
							x=x-1;
							y=y-1;
						}
						_Assert(y==y1-x1);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testMinUsingDecrementWrongInv() {
		final String code = 
					"""
					int f(int x,int y,int x1,int y1){
						_Assume(x==x1 && y==y1);
						while(x!=0)_Invariant(1){
							x=x-1;
							y=y-1;
						}
						_Assert(y==y1-x1);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testMinUsingDecrementWrongInv2() {
		final String code = 
					"""
					int f(int x,int y,int x1,int y1){
						_Assume(x==x1 && y==y1);
						while(x!=0)_Invariant( y == y1 - x1){
							x=x-1;
							y=y-1;
						}
						_Assert(y==y1-x1);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}


	@Test
	public void testWhileInv3() {
		final String code = 
					"""
					int f(int x,int y){
						while(y>0)_Invariant(x+y==2){
								y=y-1;
								x=x+1;
						}
						_Assert(x);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhileInv5() {
		final String code = 
					"""
					int f(int x,int y){
						_Assume(x);
						while(x<=5)_Invariant(x<=6){
								x=x+1;
						}
						_Assert(x == 6);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhileInv6() {
		final String code = 
					"""
					int f(int x,int y){
						_Assume(x);
						while(x<=5)_Invariant(1){
								x=x+1;
						}
						_Assert(x == 6);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	

	@Test
	public void testWhileInv4() {
		final String code = 
					"""			
					int f(int x,int y){
						_Assume(x);
						while(y>0)_Invariant(x+y==2){
								y=y-1;
								x=x+1;
						}
						_Assert(x!=0);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}


	@Test
	public void testWhileInv7() {
		final String code = 
					"""			
					int f(int x,int y){
						while(x<2)_Invariant(1){
								x=x+1;
						}
						_Assert(x >= 2);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}


		@Test
	public void testWhileInv8() {
		final String code = 
					"""			
					int f(int x,int y){
						while(x<2)_Invariant(1){
								x=x+1;
						}
						_Assert(x);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testWhileInv9() {
		final String code = 
					"""			
					int f(int x,int y){
						while(x>=0)_Invariant(x>0){
								x=x-1;
						}
						_Assert(x>0);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testWhileInv10() {
		final String code = 
					"""			
					int f(int x,int y){
						_Assume(x>=0);
						x=1;
						while(x>0)_Invariant(x>0){
								x=x+1;
						}
						_Assert(x==0);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testWhileInv11() {
		final String code = 
					"""			
					int f(int q,int r,int y,int x){
						q=0;
						r=x;
						while(r >= y)_Invariant(x==q*y+r){
							q=q+1;
							r=r-y;
						}
						_Assert(x==q*y+r);
						return y;
					}  """;
	   SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}



	@Test
	public void testWhileWInv7() {
		final String code = 
					"""			
					int f(int x,int y){
						while(x<2)_Invariant(x<=3){
								x=x+1;
						}
						_Assert(x == 2);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}



	@Test
	public void testMinUsingDecrementWrongInv3() {
		final String code = 
					"""
					int f(int x,int y,int x1,int y1){
						_Assume(x==x1 && y==y1);
						while(x!=0)_Invariant(1==1){
							x=x-1;
							y=y-1;
						}
						_Assert(y==y1-x1);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}


	@Test
	public void testMinUsingDecrementWrongInv4() {
		final String code = 
					"""
					int f(int x,int y,int x1,int y1){
						_Assume(x==x1 && y==y1);
						while(x!=0)_Invariant(1){
							x=x-1;
							y=y-1;
						}
						_Assert(y==y1-x1);
						return x;
					}  """;
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}
	@Test
	public void testSimpleIf() {
		final String code = 
		"""
			void f(int x,int y,int z){
				if(x<=z)
					 {z=y-x;}
				else
				{y=x+z;}
				_Assert(y==x+z);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}



	@Test
	public void testSimpleIf2() {
		final String code = 
		"""
			void f(int x,int y,int z){
				_Assume(x == 0);
				if(1)
					 {x=x+1;}
				else
				{int y;y=x;}
				_Assert(x == 0);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testSimpleIf3() {
		final String code = 
		"""
			void f(int x,int y,int z){
				if(0)
					 {z=y-x;}
				else
				{y=x+z;}
				_Assert(y==x+z);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testIdiotTest() {
		final String code = 
		"""
			void f(int x,int y,int z){
				_Assume(x == 0);
				if(x>0)
					 {int x;x=2;}
				else
				{int x;x=3;}
				_Assert(x == 0);
				return;
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testIdiotTest2() {
		final String code = 
		"""
			void f(int x,int y,int z){
				_Assume(x >= 0);
				if(x>0)
					 {int y;x=2;}
				else
				{int x;x=3;}
				_Assert(x == 0);
				return;
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}


	@Test
	public void testAbs() {
		final String code = 
		"""
			void f(int x,int y,int z){
				if(x>0)
					 {y=x;}
				else
				{y=0-x;}
				_Assert(y>=0);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testAbsNoAlternt() {
		final String code = 
		"""
			void f(int x,int y){
				_Assume(y==x);
				y=x;
				if(x<0){
					y=0-x;
					_Assume(y == x && y>=0);
				} 
				_Assert(y>=0);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testIfNegative() {
		final String code = 
		"""
			void f(int x,int y,int z){
				_Assume(x ==2);
				if(x)
					 {int x;x = 3;}
				else
				{ int x;x=4;}
				_Assert(x == 2);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testIfNegative2() {
		final String code = 
		"""
			void f(int x,int y,int z){
				_Assume(x==1);
				if(x==1)
					 {int x;x=3;}
				else
				{int x;x=4;}
				_Assert(x==1 );
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testSimpleIf4() {
		final String code = 
		"""
			void f(int x,int y,int z){
				_Assume(x<z);
				if(x<=z)
					 {z=y-x;}
				else
				{y=x+z;}
				_Assert(y==x+z);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}


	@Test
	public void testSimpleIf5() {
		final String code = 
		"""
			void f(int x,int y,int z){
				_Assume(x>z);
				if(x<=z)
					 {z=y-x;}
				else
				{y=x+z;}
				_Assert(y==x+z);
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}



	@Test
	public void testBoolExpression() {
		final String code = 
		"""
			void f(int x){
				_Assume(x==0);
				int y;
				if(x<0)
					 {y=0-x;}
				else
				{y=1;}
				_Assert(y==0 );
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testAssgnIf() {
		final String code = 
		"""
			void f(int x,int x1,int y1,int y,int z){
				_Assume(x1 == x && y1 == y);
				if(x)
					 {z=x;}
				else
				{z=y;}
				_Assert(z==x1 || z==y1 );
				return;
			
			}
				""";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testMinVerif() {
		final String code = "\n" +
				"int f(int x, int y,int x1,int y1,int t) {" +
				"	_Assume(x == x1 && y == y1);" +
				"	 t = x;" +
				"     x = y;"+
				"     y = t;" +
				"	_Assert(x1 == y && y1 == x);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

}


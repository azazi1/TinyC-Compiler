package tinycc.implementation.expression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.PrimaryExpression.PrimaryExpression;
import tinycc.implementation.expression.PrimaryExpression.VarExpression;
import tinycc.implementation.expression.UnaryExpression.IndirExpression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.Formula;
import tinycc.logic.IntConst;
import tinycc.logic.Variable;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.TokenKind;

/**
 * The main expression class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Expression {

	protected Type type;
	
	/**
	 * Creates a string representation of this expression.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

	public Type getType() {
		return type;
	}

	protected void setType(Type type) {
		this.type = type;
	}

	public abstract Type checkType(Diagnostic d, Scope s);

	public abstract void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs);

	public boolean isLvalue() {
		return (this instanceof VarExpression || this instanceof IndirExpression);
	}

	public boolean isNullPointer() {
		if (!(this instanceof PrimaryExpression))
			return false;
		PrimaryExpression prExp = (PrimaryExpression) this;
        if (prExp.getToken().getKind() == TokenKind.NUMBER) {
            return prExp.getToken().getText().equals("0");
        } else {
            return false;
        }
    }

	public abstract int rgesConsum();

    public abstract Formula toLogicalExpr();

	public Formula toFormulaAsStatement(Formula postCond) {
		throw new IllegalArgumentException("Only assignments could be treated as statements");
	}

	public Formula toBoolLogicalExpr() {
		Formula exprFormula = this.toLogicalExpr();
		if (exprFormula.getType() == tinycc.logic.Type.INT) {
			return new BinaryOpFormula(BinaryOperator.NE, exprFormula, new IntConst(0));
		} else {
			return exprFormula;
		}
	}

    public Formula toTerm() {
        Formula termFormula = this.toLogicalExpr();
		return new BinaryOpFormula(BinaryOperator.GE, termFormula, new IntConst(0));
    }

	public Formula toTerm(String boundFormat, boolean plusOne) {
		// k
        Variable k = new Variable(boundFormat, tinycc.logic.Type.INT);
		// t
		Formula termExprFormula = this.toLogicalExpr();
		// t >= 0
		Formula termGEzero = new BinaryOpFormula(BinaryOperator.GE, termExprFormula, new IntConst(0));
		// the whole termination formula
		Formula termFormula;
		if (plusOne) {
			// k + 1
			Formula kPLUS1 = new BinaryOpFormula(BinaryOperator.ADD, k, new IntConst(1));
			// t ≤ k+1
			termFormula = new BinaryOpFormula(BinaryOperator.LE, termExprFormula, kPLUS1);
		} else {
			// t ≤ k
			termFormula = new BinaryOpFormula(BinaryOperator.LE, termExprFormula, k);
		}
		// either (0 ≤ t ≤ k+1) or (0 ≤ t ≤ k) depending on the parameter "plusOne"
		return new BinaryOpFormula(BinaryOperator.AND, termFormula, termGEzero);
    }

	/*
	public Formula toBoolLogicalExpr() {
		if (this.hasIntLogicType()) {
			return new BinaryOpFormula(BinaryOperator.NE, this.toLogicalExpr(), new IntConst(0));
		} else {
			return this.toLogicalExpr();
		}
	}

	public boolean hasIntLogicType() {
		return (this instanceof PrimaryExpression
			 || this instanceof AddExpression
			 || this instanceof SubtExpression
			 || this instanceof MulExpression);
	}
	*/

}

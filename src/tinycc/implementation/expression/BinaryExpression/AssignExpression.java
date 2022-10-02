package tinycc.implementation.expression.BinaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.PrimaryExpression.VarExpression;
import tinycc.implementation.expression.UnaryExpression.IndirExpression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.logic.Variable;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class AssignExpression extends BinaryExpression {
    
    public AssignExpression(Token operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type leftTy  = left.checkType(d, s);
        right.checkType(d, s);
        if (left.isLvalue() && leftTy.isAssignableFrom(right)) {
            this.setType(leftTy);
        } else {
            d.printError(getOperator(), "The operand %s is not L-value", left);
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs1) {
        List<GPRegister> regs2 = regs1.subList(1, regs1.size());
        
        right.codeR(out, offs, regs1);
        
        // codeL for left operand
        if (left instanceof VarExpression) {
            ((VarExpression) left).codeL(out, offs, regs2);
        } else {
            ((IndirExpression) left).codeL(out, offs, regs2);
        }

        // Assignment
        out.emitInstruction(type.getStoreInsn(), regs1.get(0), null, 0, regs2.get(0));
    }

    @Override
    public Formula toLogicalExpr() {
        throw new IllegalArgumentException("Assignments must be the outer expression and treated as logical statement in tinyC");
    }

    @Override
	public Formula toFormulaAsStatement(Formula postCond) {
		if (right instanceof AssignExpression) {
            throw new IllegalArgumentException("Nested assignments are not allowed in tinyC");
        }

        Formula var = left.toLogicalExpr();
        if (var instanceof Variable) {
            return postCond.subst((Variable) var, right.toLogicalExpr());
        } else {
            throw new IllegalArgumentException("Left formula of assignment must be a variable");
        }
	}
}

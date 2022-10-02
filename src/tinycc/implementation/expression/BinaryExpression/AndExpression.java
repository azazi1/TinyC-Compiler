package tinycc.implementation.expression.BinaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class AndExpression extends BinaryExpression {
    
    public AndExpression(Token operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type leftTy  = left.checkType(d, s);
        Type rightTy = right.checkType(d, s);
        if (leftTy.isScalarType() && rightTy.isScalarType()) {
            this.setType(Type.getIntType());
        } else {
            d.printError(getOperator(), "The operands do not match with the operator %s", getOperator());
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        // It belongs to the verification, hence no need for code generation
    } 

    @Override
    public Formula toLogicalExpr() {
        return new BinaryOpFormula(BinaryOperator.AND, left.toBoolLogicalExpr(), right.toBoolLogicalExpr());
    }
}

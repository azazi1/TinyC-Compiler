package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.MipsAsmGen;

public class AssertStatement extends Statement {
    
    private Locatable loc;
    private Expression condition;
    
    public AssertStatement(Locatable loc, Expression condition) {
        this.loc = loc;
        this.condition = condition;
    }

    public Locatable getLocation() {
        return loc;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "Assert["+condition.toString()+"]";
    }

    @Override
    public void checkType(Diagnostic d, Scope s) {
        if (!condition.checkType(d, s).isScalarType()) {
            d.printError(loc, "The condition expression of IF-statement is not of a scalar type");
        }
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope scope) {
        // Verification Part
        
    }

    @Override
    public Formula toPC(Formula postCond) {
        Formula expr = condition.toBoolLogicalExpr();
        return new BinaryOpFormula(BinaryOperator.AND, expr, postCond);
    }

    @Override
    public Formula toVC(Formula postCond) {
        return BoolConst.TRUE;
    }
}

package tinycc.implementation.expression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class ConditionalExpression extends Expression {
    
    private Token token;
    private Expression condition;
    private Expression consequence;
    private Expression alternative;
    
    public ConditionalExpression(Token token, Expression condition, Expression consequence,
                        Expression alternative) {
                this.token = token;
                this.condition = condition;
                this.consequence = consequence;
                this.alternative = alternative;
            }

    public Token getToken() {
        return token;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getConsequence(){
        return consequence;
    }

    public Expression getAlternative(){
        return alternative;
    }

    @Override
    public String toString() {
        String str = "If["+condition.toString()+", "+consequence.toString();

        if(alternative != null) {
            str += ", "+alternative.toString();
        }

        str += "]";

        return str;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        if (!condition.checkType(d, s).isScalarType()) {
            d.printError(token, "The condition of conditional expression is not of a scalar type");
        }
        Type conseqTy = consequence.checkType(d, s);
        Type alternTy = alternative.checkType(d, s);
        if (conseqTy.equals(alternTy)) {
            this.setType(conseqTy);
        } else {
            d.printError(token, "Consequence type in conditional expression does not match with alternative type");
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        // BONUS with no points
    }

    @Override
    public int rgesConsum() {
        // BONUS with no points
        return 0;
    }

    @Override
    public Formula toLogicalExpr() {
        throw new IllegalArgumentException("Conditional expression must not exist by verification");
    }
}

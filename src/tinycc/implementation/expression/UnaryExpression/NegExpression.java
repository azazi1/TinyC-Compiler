package tinycc.implementation.expression.UnaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.logic.UnaryOpFormula;
import tinycc.logic.UnaryOperator;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class NegExpression extends UnaryExpression {
    
    public NegExpression(Token operator, boolean postfix, Expression operand) {
        this.operator = operator;
        this.postfix = postfix;
        this.operand = operand;
    }
    

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type operandTy = getOperand().checkType(d, s);
        if (operandTy.isScalarType()) {
            this.setType(Type.getIntType());
        } else {
            d.printError(getOperator(), "The operand %s has not a scalar type", getOperand());
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
        return new UnaryOpFormula(UnaryOperator.NOT, operand.toBoolLogicalExpr());
    }
}

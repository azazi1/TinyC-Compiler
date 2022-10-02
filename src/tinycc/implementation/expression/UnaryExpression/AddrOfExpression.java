package tinycc.implementation.expression.UnaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.PrimaryExpression.VarExpression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class AddrOfExpression extends UnaryExpression {
    
    public AddrOfExpression(Token operator, boolean postfix, Expression operand) {
        this.operator = operator;
        this.postfix = postfix;
        this.operand = operand;
    }
    
    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type operandTy = getOperand().checkType(d, s);
        if (operandTy.isObjectType()) {
            if (getOperand().isLvalue()) {
                this.setType(Type.getPointerType(operandTy));
                return this.getType();
            }
        }
        d.printError(getOperator(), "The operand %s is not L-value", getOperand());
        this.setType(Type.getErrorType()); 
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        if (operand instanceof VarExpression) {
            ((VarExpression) operand).codeL(out, offs, regs);
        } else {
            ((IndirExpression) operand).codeL(out, offs, regs);
        }
    }

    @Override
    public Formula toLogicalExpr() {
        throw new IllegalArgumentException("Address expression  cannot be verified in TinyC");
    }
}

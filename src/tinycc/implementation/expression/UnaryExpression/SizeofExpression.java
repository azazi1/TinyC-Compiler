package tinycc.implementation.expression.UnaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.PrimaryExpression.ConstExpression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class SizeofExpression extends UnaryExpression {
    
    public SizeofExpression(Token operator, boolean postfix, Expression operand) {
        this.operator = operator;
        this.postfix = postfix;
        this.operand = operand;
    }
    

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type operandTy = getOperand().checkType(d, s);
        if (operandTy.isCompleteType()) {
            this.setType(Type.getIntType());
        } else {
            d.printError(getOperator(), "The operand %s has an incomplete type", getOperand());
            this.setType(Type.getErrorType());
        }
        return this.getType();
        /*if (operandTy.isObjectType()) {
            if (operandTy.isPointerType()) {
                Type pointedTy = ((PointerType) operandTy).getPointsTo();
                if (pointedTy.isCharType()) {
                    this.setType(Type.getIntType());
                    return this.getType();
                }
                if (pointedTy.isPointerType()) {
                    this.setType(Type.getIntType());
                    return this.getType();
                }
            } else {
                if (operandTy.isCompleteType()) {
                    this.setType(Type.getIntType());
                    return this.getType();
                }
            }  
        }*/
    }


    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        if (operand instanceof ConstExpression) {
            Token literal = ((ConstExpression) operand).getToken();
            switch(literal.getKind()) {
                case STRING:
                int str_len = literal.getText().length() + 1;
                out.emitInstruction(ImmediateInstruction.ADDI, regs.get(0), GPRegister.ZERO, str_len);
                break;
                
                default:
                out.emitInstruction(ImmediateInstruction.ADDI, regs.get(0), GPRegister.ZERO, operand.getType().getSize());
                break;
            } 
        } else {
            out.emitInstruction(ImmediateInstruction.ADDI, regs.get(0), GPRegister.ZERO, operand.getType().getSize());
        }
    }


    @Override
    public Formula toLogicalExpr() {
        throw new IllegalArgumentException("Sizeof expression  cannot be verified in TinyC");
    } 
}

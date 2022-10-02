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
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class CmpExpression extends BinaryExpression {
    
    public CmpExpression(Token operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type leftTy  = left.checkType(d, s);
        Type rightTy = right.checkType(d, s);
        if (leftTy.isIntegerType() && rightTy.isIntegerType()) {
            this.setType(Type.getIntType());
        } else if (leftTy.isPointerToCompleteType() && rightTy.isPointerToCompleteType() && leftTy.equals(rightTy)) {
            this.setType(Type.getIntType());
        } else {
            d.printError(getOperator(), "The operands do not match with the operator %s", getOperator());
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs1) {
        List<GPRegister> regs2 = regs1.subList(1, regs1.size());
        
        if (left.rgesConsum() >= right.rgesConsum()) {
            left.codeR(out, offs, regs1);
            right.codeR(out, offs, regs2);
            getCompareInsn(out, regs1.get(0), regs1.get(0), regs2.get(0));
        } else {
            right.codeR(out, offs, regs1);
            left.codeR(out, offs, regs2);
            getCompareInsn(out, regs1.get(0), regs2.get(0), regs1.get(0));
        }
    } 

    private void getCompareInsn(MipsAsmGen out, GPRegister destReg, GPRegister leftReg, GPRegister rightReg) {
        switch (operator.getKind()) {
            case LESS:
                out.emitInstruction(RegisterInstruction.SLT, destReg, leftReg, rightReg);
                break;
            
            case GREATER: // l > r <=> r < l
                out.emitInstruction(RegisterInstruction.SLT, destReg, rightReg, leftReg);
                break;
            
            case LESS_EQUAL: // l <= r <=> !(l > r)
                out.emitInstruction(RegisterInstruction.SLT, destReg, rightReg, leftReg);
                out.emitInstruction(ImmediateInstruction.XORI, destReg, destReg, 1);
                break;

            case GREATER_EQUAL: // l >= r <=> !(l < r)
                out.emitInstruction(RegisterInstruction.SLT, destReg, leftReg, rightReg);
                out.emitInstruction(ImmediateInstruction.XORI, destReg, destReg, 1);
                break;
                
            default:
                break;
        }
    }

    @Override
    public Formula toLogicalExpr() {
        BinaryOperator logicOp;
        switch (operator.getKind()) {
            case LESS:
                logicOp = BinaryOperator.LT;
                break;

            case LESS_EQUAL:
                logicOp = BinaryOperator.LE;
                break;

            case GREATER:
                logicOp = BinaryOperator.GT;
                break;

            case GREATER_EQUAL:
                logicOp = BinaryOperator.GE;
                break;
        
            default:
                logicOp = null;
                break;
        }
        return new BinaryOpFormula(logicOp, left.toLogicalExpr(), right.toLogicalExpr());
    }
}

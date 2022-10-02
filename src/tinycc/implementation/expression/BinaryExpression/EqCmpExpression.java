package tinycc.implementation.expression.BinaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.OperandsType;
import tinycc.implementation.type.Type;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class EqCmpExpression extends BinaryExpression {
    
    private OperandsType operandsType;
    
    public EqCmpExpression(Token operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public OperandsType getOperandsType() {
        return operandsType;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type leftTy  = left.checkType(d, s);
        Type rightTy = right.checkType(d, s);
        if (leftTy.isIntegerType() && rightTy.isIntegerType()) {
            this.setType(Type.getIntType());
            operandsType = OperandsType.INT_INT;
        } else if (leftTy.isPointerType() && rightTy.isPointerType()) {
            if (leftTy.equals(rightTy) || leftTy.isPointerToVoid() || rightTy.isPointerToVoid()) {
                this.setType(Type.getIntType());
                operandsType = OperandsType.PTR_PTR;
            } else {
                d.printError(getOperator(), String.format("The operands do not match with the pointer operator %s"), getOperator());
                this.setType(Type.getErrorType());
            }
        } 
        else if (leftTy.isPointerType() && right.isNullPointer()) {
                this.setType(Type.getIntType());
                operandsType = OperandsType.PTR_NULL;
        } 
        else if (left.isNullPointer() && rightTy.isPointerType()) {
                this.setType(Type.getIntType());
                operandsType = OperandsType.NULL_PTR;
        } 
        else {
            d.printError(getOperator(), "The operands do not match with the operator %s", getOperator());
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    /** #idea:
     *  subu $t0 $t0 $t1 
     *  sltu $t0 $zero $t0    # till here: non-equality
     *  xori $t0 $t0 1        # change result for equality
     */
    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs1) {
        List<GPRegister> regs2 = regs1.subList(1, regs1.size());
        // first: non-equality
        switch (operandsType) { 
            case INT_INT, PTR_PTR:
            // operands precedence of code generation
            if (left.rgesConsum() >= right.rgesConsum()) {
                left.codeR(out, offs, regs1);
                right.codeR(out, offs, regs2);
            } else {
                right.codeR(out, offs, regs1);
                left.codeR(out, offs, regs2);
            }
            // comparison
            out.emitInstruction(RegisterInstruction.SUBU, regs1.get(0), regs1.get(0), regs2.get(0));
            out.emitInstruction(RegisterInstruction.SLTU, regs1.get(0), GPRegister.ZERO, regs1.get(0));
            break;

            case PTR_NULL:
            if (left.isNullPointer()) {
                out.emitInstruction(ImmediateInstruction.ADDI, regs1.get(0), GPRegister.ZERO, 0);
            } else {
                out.emitInstruction(ImmediateInstruction.ADDI, regs1.get(0), GPRegister.ZERO, 1);
            }
            break;

            case NULL_PTR:
            if (right.isNullPointer()) {
                out.emitInstruction(ImmediateInstruction.ADDI, regs1.get(0), GPRegister.ZERO, 0);
            } else {
                out.emitInstruction(ImmediateInstruction.ADDI, regs1.get(0), GPRegister.ZERO, 1);
            }
            break;
        
            default:
            break;
        }
        // second: equality
        if (operator.getKind() == TokenKind.EQUAL_EQUAL) {
            out.emitInstruction(ImmediateInstruction.XORI, regs1.get(0), regs1.get(0), 1);
        }

    } 

    @Override
    public Formula toLogicalExpr() {
        // determine comparing operator 
        BinaryOperator logicOp;
        switch (operator.getKind()) {
            case EQUAL_EQUAL:
                logicOp = BinaryOperator.EQ;
                break;

            case BANG_EQUAL:
                logicOp = BinaryOperator.NE;
                break;

            default:
                logicOp = null;
                break;
        }

        // check if one of the operands has to be converted to bool
        Formula leftFormula = left.toLogicalExpr();
        Formula rightFormula = right.toLogicalExpr();
        if (leftFormula.getType() == tinycc.logic.Type.INT && rightFormula.getType() == tinycc.logic.Type.INT) {
            return new BinaryOpFormula(logicOp, leftFormula, rightFormula);
        } else {
            return new BinaryOpFormula(logicOp, left.toBoolLogicalExpr(), right.toBoolLogicalExpr());
        }
    }
}

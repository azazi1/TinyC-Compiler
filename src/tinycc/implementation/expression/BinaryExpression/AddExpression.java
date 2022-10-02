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

public class AddExpression extends BinaryExpression {
    
    private OperandsType operandsType;

    public AddExpression(Token operator, Expression left, Expression right) {
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
        } else if (leftTy.isPointerToCompleteType() && rightTy.isIntegerType()) {
            this.setType(leftTy);
            operandsType = OperandsType.PTR_INT;
        } else if (leftTy.isIntegerType() && rightTy.isPointerToCompleteType()) {
            this.setType(rightTy);
            operandsType = OperandsType.INT_PTR;
        } else {
            d.printError(getOperator(), "The operands do not match with the operator %s", getOperator());
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs1) {
        List<GPRegister> regs2 = regs1.subList(1, regs1.size());
        
        // operands precedence of code generation & preparing offset for PtrArithm
        if (left.rgesConsum() >= right.rgesConsum()) {
            left.codeR(out, offs, regs1);
            right.codeR(out, offs, regs2);
            switch (operandsType) {
                case PTR_INT:
                // sum = ptr + 4 * int, int <- right = reg2
                getTypeOffset(out, regs2.get(0));
                break;
    
                case INT_PTR:
                // sum = 4 * int + ptr, int <- left = reg1
                getTypeOffset(out, regs1.get(0));
                break;
            
                default:
                break;
            }
        } else {
            right.codeR(out, offs, regs1);
            left.codeR(out, offs, regs2);
            switch (operandsType) {
                case PTR_INT:
                // sum = ptr + 4 * int, int <- right = reg1
                getTypeOffset(out, regs1.get(0));
                break;
    
                case INT_PTR:
                // sum = 4 * int + ptr, int <- left = reg2
                getTypeOffset(out, regs2.get(0));
                break;
            
                default:
                break;
            }
        }

        // Addition instruction
        out.emitInstruction(RegisterInstruction.ADD, regs1.get(0), regs1.get(0), regs2.get(0));
        
    } 

    private void getTypeOffset(MipsAsmGen out, GPRegister leftReg) {
        if (!left.getType().isPointerToChar()) {
            out.emitInstruction(ImmediateInstruction.SLL, leftReg, leftReg, 2);
        }
    }

    @Override
    public Formula toLogicalExpr() {
        return new BinaryOpFormula(BinaryOperator.ADD, left.toLogicalExpr(), right.toLogicalExpr());
    }
}

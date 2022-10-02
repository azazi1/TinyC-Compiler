package tinycc.implementation.expression.UnaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class IndirExpression extends UnaryExpression {
    
    public IndirExpression(Token operator, boolean postfix, Expression operand) {
        this.operator = operator;
        this.postfix = postfix;
        this.operand = operand;
    }
    

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type operandTy = getOperand().checkType(d, s);
        if (operandTy.isPointerToCompleteType()) {
            this.setType(((PointerType) operandTy).getPointsTo());
        } else {
            d.printError(getOperator(),"The operand %s is not a pointer to complete typed object", getOperand());
            this.setType(Type.getErrorType());
        } 
        return this.getType();   
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        codeL(out, offs, regs);
        if(type.isPointerToChar()) {
            out.emitInstruction(MemoryInstruction.LA, regs.get(0), null, 0, regs.get(0));
        } else if (type.isCharType()) {
            out.emitInstruction(MemoryInstruction.LB, regs.get(0), null, 0, regs.get(0));
        } else {
            out.emitInstruction(MemoryInstruction.LW, regs.get(0), null, 0, regs.get(0));
        }
    } 

    //@Override
    public void codeL(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        operand.codeR(out, offs, regs);
    }


    @Override
    public Formula toLogicalExpr() {
        throw new IllegalArgumentException("Indirection expression cannot be verified in TinyC");
    }
}

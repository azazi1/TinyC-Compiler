package tinycc.implementation.statement;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;

public class ReturnStatement extends Statement {
    
    private Locatable loc;
    private Expression exp;
    
    public ReturnStatement(Locatable loc, Expression exp) {
        this.loc = loc;
        this.exp = exp;
    }

    public Locatable getLocation() {
        return loc;
    }

    public Expression getExpression() {
        return exp;
    }

    @Override
    public String toString() {
        String str = "Return[";

        if (exp != null) {
            str += exp.toString();
        }

        str += "]";
        
        return str;
    }

    @Override
    public void checkType(Diagnostic d, Scope s) {
        if (!s.getFuncReturnType().isVoidType()) {
            exp.checkType(d, s);
            if (!s.getFuncReturnType().isAssignableFrom(exp)) {
                d.printError(loc, "The return expression does not match with the function's return type");
            }
        }
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope offs) {
        List<GPRegister> regs = new ArrayList<GPRegister>(
            List.of(/*GPRegister.V0, GPRegister.V1,*/ GPRegister.T0, GPRegister.T1, 
                    GPRegister.T2, GPRegister.T3, GPRegister.T4, GPRegister.T5, 
                    GPRegister.T6, GPRegister.T7, GPRegister.T8, GPRegister.T9));
        if (this.getExpression() != null) {
            this.getExpression().codeR(out, offs, regs);
        }
        int localAllocSize = offs.getaccumlatedOffest() - offs.getFuncMaxOffset();
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, GPRegister.SP, localAllocSize);
        out.emitInstruction(RegisterInstruction.OR, GPRegister.V0, GPRegister.T0, GPRegister.ZERO);
        out.emitInstruction(BranchInstruction.BGEZ, GPRegister.ZERO, offs.getEndLabel());
    }

    @Override
    public Formula toPC(Formula postCond) {
        return BoolConst.TRUE;
    }

    @Override
    public Formula toVC(Formula postCond) {
        return BoolConst.TRUE;
    }
}

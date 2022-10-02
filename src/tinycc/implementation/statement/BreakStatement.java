package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;

public class BreakStatement extends Statement {
    private Locatable loc;
    private WhileStatement loop;
    
    public BreakStatement(Locatable loc) {
        this.loc = loc;
    }

    public Locatable getLocation() {
        return loc;
    }

    @Override
    public String toString() {
        return "Break";
    }

    @Override
    public void checkType(Diagnostic diag, Scope loopScope) {
        loop = loopScope.getLoop();
        if (loop == null) {
            diag.printError(loc, "The break location is invalid");
        }    
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope offs) {
        out.emitInstruction(BranchInstruction.BEQ, GPRegister.ZERO, loop.getLoopEndLabel());
    }

    @Override
    public Formula toPC(Formula postCond) {
        return loop.getInvariant().toBoolLogicalExpr();
    }

    @Override
    public Formula toVC(Formula postCond) {
        return BoolConst.TRUE;
    }
}

package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;

public class ContinueStatement extends Statement {
    private Locatable loc;
    private WhileStatement loop;
    
    public ContinueStatement(Locatable loc) {
        this.loc = loc;
    }

    public Locatable getLocation() {
        return loc;
    }

    @Override
    public String toString() {
        return "Continue";
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
        out.emitInstruction(BranchInstruction.BEQ, GPRegister.ZERO, loop.getTestLabel());
    }

    @Override
    public Formula toPC(Formula postCond) {
        // I
        Formula invFormula     = loop.getInvariant().toBoolLogicalExpr();
        // 0 ≤ t ≤ k
        Formula term_k  = loop.getTermination().toTerm(loop.getLoopBoundFormat(), false);
        // I ⋀ (0 ≤ t ≤ k)
        return new BinaryOpFormula(BinaryOperator.AND, invFormula, term_k);
    }

    @Override
    public Formula toVC(Formula postCond) {
        return BoolConst.TRUE;
    }
}

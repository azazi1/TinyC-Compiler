package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.MipsAsmGen;

public class ErrorStatement extends Statement {
    private Locatable loc;
    
    public ErrorStatement(Locatable loc) {
        this.loc = loc;
    }

    public Locatable getLocation() {
        return loc;
    }

    @Override
    public String toString() {
        return "Error["+loc.toString()+"]";
    }

    @Override
    public void checkType(Diagnostic d, Scope s) {
        // Error Statements are laways well-typed
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope offs) {
        throw new IllegalStateException();
    }

    @Override
    public Formula toPC(Formula postCond) {
        throw new IllegalStateException();
    }

    @Override
    public Formula toVC(Formula postCond) {
        throw new IllegalStateException();
    }

    
}

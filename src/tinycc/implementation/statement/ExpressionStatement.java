package tinycc.implementation.statement;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;

public class ExpressionStatement extends Statement {
    
    private Locatable loc;
    private Expression init;
    
    public ExpressionStatement(Locatable loc, Expression init) {
        this.loc = loc;
        this.init = init;
    }

    public Locatable getLocation() {
        return loc;
    }

    public Expression getInit() {
        return init;
    }

    @Override
    public String toString() {
        return init.toString();
    }

    @Override
    public void checkType(Diagnostic d, Scope s) {
        if (init.checkType(d, s) == null) {
            d.printError(loc, "The expression statement is ill-typed");
        }
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope offs) {
        // preparing regs list and unique labels
        List<GPRegister> regs = new ArrayList<GPRegister>(
                    List.of(GPRegister.T0, GPRegister.T1, GPRegister.T2, GPRegister.T3, GPRegister.T4, 
                            GPRegister.T5, GPRegister.T6, GPRegister.T7, GPRegister.T8, GPRegister.T9));
        init.codeR(out, offs, regs);
    }

    @Override
    public Formula toPC(Formula postCond) {
        return init.toFormulaAsStatement(postCond);
    }

    @Override
    public Formula toVC(Formula postCond) {
        return BoolConst.TRUE;
    }
}

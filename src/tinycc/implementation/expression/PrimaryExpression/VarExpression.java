package tinycc.implementation.expression.PrimaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.scope.scopeExceptions.IdUndeclared;
import tinycc.implementation.topLevelConstruct.Declaration;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.logic.Variable;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class VarExpression extends PrimaryExpression {
    
    private Declaration decl;

    public VarExpression(Token token) {
        this.token = token;
    }

    public Declaration getDeclaration() {
        return this.decl;
    }

    public void setDeclaration(Declaration decl) {
        this.decl = decl;
    }
    
    @Override
    public String toString() {
        return "Var_"+token.toString();
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        String id = token.getText();
        try {
            this.setDeclaration(s.lookup(id));
            this.setType(this.decl.getType());
        } catch (IdUndeclared e) {
            d.printError(token, "The Variable %s has not been declared", token);
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        codeL(out, offs, regs);
        out.emitInstruction(type.getLodeInsn(), regs.get(0), null, 0, regs.get(0));
    }
    
    //@Override
    public void codeL(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        int offset = offs.lookup(token.getText());
        if (offset != -1) {
            out.emitInstruction(ImmediateInstruction.ADDIU, regs.get(0), GPRegister.SP, offset);
        } else {
            DataLabel globalVar = offs.lookupDataLabel(token.getText());
            out.emitInstruction(MemoryInstruction.LA, regs.get(0), globalVar, 0, null);
        }
    }

    @Override
    public Formula toLogicalExpr() {
        if (this.getType().isIntegerType()) {
            return new Variable(this.decl.getFormulaVar(), tinycc.logic.Type.INT);
        } else {
            throw new IllegalArgumentException("Logic variable must have an integer type");
        }
    }
}

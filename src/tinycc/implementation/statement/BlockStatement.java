package tinycc.implementation.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.topLevelConstruct.Declaration;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;

public class BlockStatement extends Statement {
    private Locatable loc;
    private List<Statement> statements;
    private List<Declaration> localDeclarations;
    private int localAllocSize;

    public BlockStatement(Locatable loc, List<Statement> statements) {
        this.loc = loc;
        this.statements = statements;
    }

    public Locatable getLocation() {
        return loc;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public List<Declaration> getLocalDeclarations() {
        return localDeclarations;
    }

    public int getLocalAllocSize() {
        return localAllocSize;
    }

    @Override
    public String toString() {
        boolean manySatements = false;       
        String str = "Block[";
        for (Statement stmt: statements) {
            if (manySatements) {
                str += ", ";
            } else {
                manySatements = true;
            }
            str += stmt.toString();
        }
        str += "]";
        return str;
    }

    @Override
    public void checkType(Diagnostic diag, Scope parent) {
        Scope localScope = parent.newNestedScope(parent.getFuncReturnType());
        statements.stream().forEach(stmt -> stmt.checkType(diag, localScope));
        localDeclarations = new ArrayList<>(localScope.getDeclarations());
        localAllocSize = localDeclarations.size() * 4;
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope parent) {
        CompilationScope localOffs = parent.newNestedCompilationScope();
        
        // pick  a unique offset for the new local variable
        // allocate all local variables of this block (not its nested blocks)
        localDeclarations.stream().forEach(decl -> localOffs.add(decl.getName().getText()));
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, GPRegister.SP, -localAllocSize);

        // generate code for the block statements
        statements.stream().forEach(stmt -> stmt.codeS(out, localOffs));

        // deallocate all local variables
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, GPRegister.SP, localAllocSize);
    }

    @Override
    public Formula toPC(Formula postCond) {
        Formula postCond_tmp = postCond;
        ListIterator<Statement> stmtIterator = statements.listIterator(statements.size());
        while (stmtIterator.hasPrevious()) {
            postCond_tmp = stmtIterator.previous().toPC(postCond_tmp);
        }
        return postCond_tmp;
    }

    @Override
    public Formula toVC(Formula postCond) {
        Formula postCond_tmp = postCond;
        Formula verfCond_tmp = BoolConst.TRUE;
        Formula newVC_tmp;
        ListIterator<Statement> stmtIterator = statements.listIterator(statements.size());
        while (stmtIterator.hasPrevious()) {
            Statement currStmt = stmtIterator.previous();
            newVC_tmp    = currStmt.toVC(postCond_tmp);    // vc s_n-1 Q'
            postCond_tmp = currStmt.toPC(postCond_tmp);    // pc s_n-1 Q =: Q''
            verfCond_tmp = new BinaryOpFormula(BinaryOperator.AND, verfCond_tmp, newVC_tmp); // vc s_n Q && vc s_n-1 Q'
        }
        return verfCond_tmp;
    }

}

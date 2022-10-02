package tinycc.implementation.statement;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.scope.scopeExceptions.IdUndeclared;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.Formula;
import tinycc.logic.UnaryOpFormula;
import tinycc.logic.UnaryOperator;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class WhileStatement extends Statement {
    
    private Locatable loc;
    private Expression condition; 
    private Statement body; 
    private Expression invariant;
    private Expression term; 
    private Token loopBound;
    private String loopBoundFormat;
    private List<Statement> breaks = new ArrayList<Statement>();
    private TextLabel testLabel;
    private TextLabel loopEndLabel;
    
    public WhileStatement(Locatable loc, Expression condition, Statement body, 
        Expression invariant, Expression term, Token loopBound){
            this.loc       = loc;
            this.condition = condition;
            this.body      = body;
            this.invariant = invariant;
            this.term      = term;
            this.loopBound = loopBound;
    }

    public Locatable getLocation() {
        return loc;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody(){
        return body;
    }

    public Expression getInvariant() {
        return invariant;
    }

    public Expression getTermination() {
        return term;
    }

    public Token getLoopBound() {
        return loopBound;
    }

    public String getLoopBoundFormat() {
        return loopBoundFormat;
    }

    public TextLabel getTestLabel() {
        return testLabel;
    }

    public TextLabel getLoopEndLabel() {
        return loopEndLabel;
    }

    public List<Statement> getBreaks() {
        return breaks;
    }

    public void addBreak(Statement breakStmt) {
        breaks.add(breakStmt);
    }

    @Override
    public String toString() {
        String str = "While";

        if (loopBound != null) {
            str += "_"+loopBound.toString();
        }

        str += "["+condition.toString()+", "+body.toString();

        if (invariant != null) {
            str += ", "+invariant.toString();
        }

        if (term != null) {
            str += ", "+term.toString();
        }

        str += "]";

        return str;
    }

    @Override
    public void checkType(Diagnostic diag, Scope parent) {
        Scope loopScope, invScope;
        if (!condition.checkType(diag, parent).isScalarType()) {
            diag.printError(loc, "The condition expression of WHILE-loop is not of a scalar type");
        } else {
            loopScope = parent.newNestedLoopScope(parent.getFuncReturnType(), this);
            // check invariant
            if (invariant != null) {
                invScope = loopScope.newNestedInvariantScope(loopScope.getFuncReturnType(), this);
                invariant.checkType(diag, invScope);
                
                // check termination
                if (term != null) {
                    term.checkType(diag, loopScope);
                    if (loopBound != null) {
                        loopScope.addGhost(loopBound);
                        try {
                            loopBoundFormat = invScope.lookup(loopBound.getText()).getFormulaVar();
                        } catch (IdUndeclared e) {
                            diag.printError(loopBound, "The ghost variable %s couldn't get his format", loopBound);
                        }
                    } else {
                        try {
                            loopBound = new Token(loc, TokenKind.IDENTIFIER, "k");
                            loopScope.addGhost(loopBound);
                            loopBoundFormat = invScope.lookup("k").getFormulaVar();
                        } catch (IdUndeclared e) {
                            diag.printError(loc, "The ghost variable couldn't get his format");
                        }
                    }
                }
            }
            body.checkType(diag, loopScope);
        }
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope offs) {
        // preparing regs list and unique labels
        List<GPRegister> regs = new ArrayList<GPRegister>(
            List.of(GPRegister.T0, GPRegister.T1, GPRegister.T2, GPRegister.T3, GPRegister.T4, 
                    GPRegister.T5, GPRegister.T6, GPRegister.T7, GPRegister.T8, GPRegister.T9));
        TextLabel loopLabel = out.makeUniqueTextLabel("L_");
        testLabel = out.makeUniqueTextLabel("T_");
        loopEndLabel = out.makeUniqueTextLabel("LN_");

        // code generation
        out.emitInstruction(BranchInstruction.BEQ, GPRegister.ZERO, testLabel);
        out.emitLabel(loopLabel);
        body.codeS(out, offs);
        out.emitLabel(testLabel);
        condition.codeR(out, offs, regs);
        out.emitInstruction(BranchInstruction.BNE, regs.get(0), loopLabel);
        out.emitLabel(loopEndLabel);
    }

    @Override
    public Formula toPC(Formula postCond) {
        if (invariant == null) {
            throw new IllegalStateException("A loop must have an invariant");
        } else {
            Formula invFormula = invariant.toBoolLogicalExpr();
            if (term != null) {
                Formula termFormula = term.toTerm();
                return new BinaryOpFormula(BinaryOperator.AND, invFormula, termFormula);
            } else {
                return invFormula;
            }
        }
    }

    @Override
    public Formula toVC(Formula postCond) {
        // e
        Formula condFormula    = condition.toBoolLogicalExpr();
        // ¬e
        Formula negCondFormula = new UnaryOpFormula(UnaryOperator.NOT, condFormula);
        // I
        Formula invFormula     = invariant.toBoolLogicalExpr();
        // I ⋀ e
        Formula invANDcond     = new BinaryOpFormula(BinaryOperator.AND, invFormula, condFormula);
        // I ⋀ ¬e
        Formula invANDnegCond  = new BinaryOpFormula(BinaryOperator.AND, invFormula, negCondFormula);
        
        Formula bodyPostCond, bodyVerfCond, vc_side1, vc_side2, vc_side;
        
        if (term != null) {
            // 0 ≤ t ≤ k+1
            Formula term_k1 = term.toTerm(loopBoundFormat, true);
            // I ⋀ e ⋀ (0 ≤ t ≤ k+1)
            Formula invANDterm_k1 = new BinaryOpFormula(BinaryOperator.AND, invANDcond, term_k1);

            // 0 ≤ t ≤ k
            Formula term_k  = term.toTerm(loopBoundFormat, false);
            // I ⋀ (0 ≤ t ≤ k)
            Formula invANDterm_k  = new BinaryOpFormula(BinaryOperator.AND, invFormula, term_k);
            // pc s (I ⋀ 0 ≤ t ≤ k)
            bodyPostCond = body.toPC(invANDterm_k);
            // vc s (I ⋀ 0 ≤ t ≤ k)
            bodyVerfCond = body.toVC(invANDterm_k);

            // vc1 := (I ⋀ e ⋀ 0 ≤ t ≤ k+1) ⇒ (pc s (I ⋀ 0 ≤ t ≤ k))
            vc_side1 = new BinaryOpFormula(BinaryOperator.IMPLIES, invANDterm_k1, bodyPostCond);
        } else {
            // pc s I
            bodyPostCond = body.toPC(invFormula);
            // vc s I
            bodyVerfCond = body.toVC(invFormula);
            // vc1 := (I ⋀ e) ⇒ (pc s I)
            vc_side1 = new BinaryOpFormula(BinaryOperator.IMPLIES, invANDcond, bodyPostCond);
        }

        if (breaks.isEmpty()) {
            // vc2 := (I ⋀ ¬e) ⇒ Q
            vc_side2 = new BinaryOpFormula(BinaryOperator.IMPLIES, invANDnegCond, postCond);
        } else {
            // vc2 := I ⇒ Q
            vc_side2 = new BinaryOpFormula(BinaryOperator.IMPLIES, invFormula, postCond);
        }
        
        // vc1 ⋀ vc2
        vc_side  = new BinaryOpFormula(BinaryOperator.AND, vc_side1, vc_side2);

        // vc1 ⋀ vc2 ⋀ vc s (I ⋀ term?)
        return new BinaryOpFormula(BinaryOperator.AND, vc_side, bodyVerfCond);
    }
}

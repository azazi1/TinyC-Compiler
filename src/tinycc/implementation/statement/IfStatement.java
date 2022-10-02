package tinycc.implementation.statement;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.Formula;
import tinycc.logic.UnaryOpFormula;
import tinycc.logic.UnaryOperator;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;

public class IfStatement extends Statement {
    
    private Locatable loc;
    private Expression condition;
    private Statement consequence;
    private Statement alternative;
    
    public IfStatement(Locatable loc, Expression condition, Statement consequence,
                        Statement alternative) {
                this.loc = loc;
                this.condition = condition;
                this.consequence = consequence;
                this.alternative = alternative;
            }

    public Locatable getLocation() {
        return loc;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getConsequence(){
        return consequence;
    }

    public Statement getAlternative(){
        return alternative;
    }

    @Override
    public String toString() {
        String str = "If["+condition.toString()+", "+consequence.toString();

        if(alternative != null) {
            str += ", "+alternative.toString();
        }

        str += "]";

        return str;
    }

    @Override
    public void checkType(Diagnostic d, Scope s) {
        if (!condition.checkType(d, s).isScalarType()) {
            d.printError(loc, "The condition expression of IF-statement is not of a scalar type");
        } else {
            consequence.checkType(d, s);
            if(alternative != null) {
                alternative.checkType(d, s);
            }
        }
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope offs) {
        // preparing regs list and unique labels
        List<GPRegister> regs = new ArrayList<GPRegister>(
            List.of(GPRegister.T0, GPRegister.T1, GPRegister.T2, GPRegister.T3, GPRegister.T4, 
                    GPRegister.T5, GPRegister.T6, GPRegister.T7, GPRegister.T8, GPRegister.T9));
        TextLabel alternLabel = out.makeUniqueTextLabel("F");
        TextLabel endLabel = out.makeUniqueTextLabel("N");

        // code generation
        condition.codeR(out, offs, regs);
        out.emitInstruction(BranchInstruction.BEQ, regs.get(0), alternLabel);
        consequence.codeS(out, offs);
        out.emitInstruction(BranchInstruction.BEQ, GPRegister.ZERO, endLabel);
        out.emitLabel(alternLabel);
        if (alternative != null) {
            alternative.codeS(out, offs);
        }
        out.emitLabel(endLabel);
    }

    @Override
    public Formula toPC(Formula postCond) {
        Formula condFormula    = condition.toBoolLogicalExpr();
        Formula negCondFormula = new UnaryOpFormula(UnaryOperator.NOT, condFormula);
        Formula conseqFormula  = consequence.toPC(postCond);
        Formula conseqPart     = new BinaryOpFormula(BinaryOperator.AND, condFormula, conseqFormula);
        if (alternative != null) {
            Formula alternFormula = alternative.toPC(postCond);
            Formula alternPart    = new BinaryOpFormula(BinaryOperator.AND, negCondFormula, alternFormula);
            return new BinaryOpFormula(BinaryOperator.OR, conseqPart, alternPart);
        } else {
            Formula alternPart    = new BinaryOpFormula(BinaryOperator.AND, negCondFormula, postCond);
            return new BinaryOpFormula(BinaryOperator.OR, conseqPart, alternPart);
        }
    }

    @Override
    public Formula toVC(Formula postCond) {
        Formula conseqFormula  = consequence.toVC(postCond);
        if (alternative != null) {
            Formula alternFormula  = alternative.toVC(postCond);
            return new BinaryOpFormula(BinaryOperator.AND, conseqFormula, alternFormula);
        } else {
            return conseqFormula;
        }
    }

}

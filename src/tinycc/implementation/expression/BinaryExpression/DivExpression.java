package tinycc.implementation.expression.BinaryExpression;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.SpecialInstruction;
import tinycc.mipsasmgen.SpecialRegisterInstruction;
import tinycc.parser.Token;

public class DivExpression extends BinaryExpression {
    
    public DivExpression(Token operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type leftTy  = left.checkType(d, s);
        Type rightTy = right.checkType(d, s);
        if (leftTy.isIntegerType() && rightTy.isIntegerType()) {
            this.setType(Type.getIntType());
        } else {
            d.printError(getOperator(), "The operands do not match with the operator %s", getOperator());
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs1) {
        List<GPRegister> regs2 = regs1.subList(1, regs1.size());
        
        // operands precedence of code generation
        if (left.rgesConsum() >= right.rgesConsum()) {
            left.codeR(out, offs, regs1);
            right.codeR(out, offs, regs2);
            out.emitInstruction(SpecialInstruction.DIV, regs1.get(0), regs2.get(0));
        } else {
            right.codeR(out, offs, regs1);
            left.codeR(out, offs, regs2);
            out.emitInstruction(SpecialInstruction.DIV, regs2.get(0), regs1.get(0));
        }
        out.emitInstruction(SpecialRegisterInstruction.MFLO, regs1.get(0));
    } 

    @Override
    public Formula toLogicalExpr() {
        throw new IllegalArgumentException("Division cannot be verified in TinyC");
    }
}

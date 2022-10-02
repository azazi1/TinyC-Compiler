package tinycc.implementation.expression.PrimaryExpression;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.logic.IntConst;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class ConstExpression extends PrimaryExpression {
    
    public ConstExpression(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Const_"+token.toString();
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        switch (token.getKind()) {
            case STRING:
                this.setType(Type.getPointerType(Type.getCharType()));
                break;

            case NUMBER, CHARACTER:
                this.setType(Type.getIntType());
                break;

            default:
                this.setType(null);
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        if (token.getKind() == TokenKind.STRING) {
            int randomNum = ThreadLocalRandom.current().nextInt(1000000);
            DataLabel str_begin = out.makeDataLabel("str_B"+randomNum); //TODO: check
            DataLabel str_end = out.makeDataLabel("str_E"+randomNum); 
            out.emitASCIIZ(str_begin, token.getText());
            out.emitLabel(str_end);
            out.emitInstruction(MemoryInstruction.LA, regs.get(0), str_begin, 0, null);
            out.emitInstruction(MemoryInstruction.LA, regs.get(1), str_end, 0, null);
        } else if (token.getKind() == TokenKind.CHARACTER) {
            char asciiChar = token.getText().charAt(0);
            int immediate = asciiChar;
            out.emitInstruction(ImmediateInstruction.ADDI, regs.get(0), GPRegister.ZERO, immediate);
        } else {
            int immediate = Integer.parseInt(token.getText());
            out.emitInstruction(ImmediateInstruction.ADDI, regs.get(0), GPRegister.ZERO, immediate);
        }
    }

    @Override
    public Formula toLogicalExpr() {
        switch (token.getKind()) {
            case NUMBER, CHARACTER:
                return new IntConst(Integer.parseInt(token.getText()));

            default:
                throw new IllegalArgumentException("Logic variable must have an integer type");
        }
    }
}

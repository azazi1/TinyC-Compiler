package tinycc.implementation.statement;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.scope.scopeExceptions.FunctionAlreadyDefined;
import tinycc.implementation.scope.scopeExceptions.IdAlreadyDeclared;
import tinycc.implementation.scope.scopeExceptions.IllegalDeclaration;
import tinycc.implementation.topLevelConstruct.Declaration;
import tinycc.implementation.type.Type;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.logic.Variable;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class DeclarationStatement extends Statement implements Declaration {
    private Type type;
    private Token name;
    private Expression init;
    private String formulaVar;

    public DeclarationStatement(Type type, Token name, Expression init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public DeclarationStatement(Type type, Token name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Token getName() {
        return this.name;
    }

    public Expression getInit() {
        return this.init;
    }

    @Override
    public String getFormulaVar() {
        return this.formulaVar;
    }
    
    @Override
    public void setFormulaVar(int uniqueID) {
		this.formulaVar = this.getName().getText()+ "_" + uniqueID;
	}

    @Override
    public String toString() {
        String str = "Declaration_"+name.toString()+"["+type.toString();
        if(init != null) {
            str += ", "+init.toString();
        }
        str += "]";
        return str;
    }

    @Override
    public void checkType(Diagnostic d, Scope localScope) {
        String id = name.getText();

        if (init != null) {
            init.checkType(d, localScope);
            if (!type.isAssignableFrom(init)) {
                d.printError(name, "The local varible %s cannot be initialized with a value of unconvertable type", id);
            }
        }

		switch (name.getKind()) {
            case IDENTIFIER:
				if (type.isVoidType()) {
					d.printError(name, "The local Variable %s must not be of type void", id);
				}

				try {
                    this.setFormulaVar(localScope.getVariableID());
					localScope.add(id, this);
				} catch (IllegalDeclaration | IdAlreadyDeclared | FunctionAlreadyDefined e) {
					d.printError(name, "The local varible %s can only be re-decleared with the same type", id);
				}
				break;
            default:
				d.printError(name, "The local Variable %s not a valid identifier", id);
        }
    }

    @Override
    public void codeS(MipsAsmGen out, CompilationScope localOffs) {
        // evaluate the assignment if local variable initialized
        if (init != null) {
            List<GPRegister> regs1 = new ArrayList<GPRegister>(
            List.of(GPRegister.T0, GPRegister.T1, GPRegister.T2, GPRegister.T3, GPRegister.T4, 
                    GPRegister.T5, GPRegister.T6, GPRegister.T7, GPRegister.T8, GPRegister.T9));
            List<GPRegister> regs2 = regs1.subList(1, regs1.size());
            
            // generate code for the intialization value
            init.codeR(out, localOffs, regs2);

            // get the local variable offset, then generate code for its assignment 
            int offset = localOffs.lookup(name.getText());
            out.emitInstruction(MemoryInstruction.SW, regs2.get(0), null, offset, GPRegister.SP);
        }
    }

    @Override
    public Formula toPC(Formula postCond) {
        if (init != null) {
            return postCond.subst(new Variable(this.getFormulaVar(), tinycc.logic.Type.INT), init.toLogicalExpr());
        } else {
            return postCond;
        }
    }

    @Override
    public Formula toVC(Formula postCond) {
        return BoolConst.TRUE;
    }
}

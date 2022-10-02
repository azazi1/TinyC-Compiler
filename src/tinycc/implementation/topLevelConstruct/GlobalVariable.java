package tinycc.implementation.topLevelConstruct;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.scope.scopeExceptions.FunctionAlreadyDefined;
import tinycc.implementation.scope.scopeExceptions.IdAlreadyDeclared;
import tinycc.implementation.scope.scopeExceptions.IllegalDeclaration;
import tinycc.implementation.type.Type;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class GlobalVariable extends ExternalDeclaration {

    public GlobalVariable(Type type, Token name){
        this.type = type;
		this.name = name;
    }

    @Override
    public void checkCode(Diagnostic d, Scope globalScope) {
		String id = name.getText();
		switch (name.getKind()) {
            case IDENTIFIER:
				if (type.isVoidType()) {
					d.printError(name, "Variable %s must not be of type void", id);
				}
				try {
					this.setFormulaVar(globalScope.getVariableID());
					globalScope.add(id, this);
				} 
				catch (IllegalDeclaration | IdAlreadyDeclared | FunctionAlreadyDefined e) {
					d.printError(name, "The varible %s can only be re-decleared with the same type", id);
				}
				break;
            default:
				d.printError(name, "Variable %s not a valid identifier", id);
        }
	}

	@Override
	public void generateCode(MipsAsmGen out, CompilationScope offs) {
		String id = name.getText();
		if (offs.lookupDataLabel(id) == null) {
			DataLabel label = out.makeDataLabel(id);
			offs.addDataLabel(id, label);
			if (type.isCharType()) {
				out.emitByte(label, (byte) 0);
			} else {
				out.emitWord(label, 0);
			}
		}
	}

	@Override
	public Formula genFormula() {
		return BoolConst.TRUE;
	}
}

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
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;
import tinycc.parser.Token;

public class FunctionDeclaration extends ExternalDeclaration {
    

	public FunctionDeclaration(Type type, Token name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public void checkCode(Diagnostic d, Scope globalScope) {
		String id = name.getText();
		switch (name.getKind()) {
            case IDENTIFIER:
				try {
					globalScope.add(id, this);
				} catch (IllegalDeclaration | IdAlreadyDeclared | FunctionAlreadyDefined e) {
					d.printError(name, "The function %s can only be re-decleared with the same type", id);
				}
				break;
            default:
				d.printError(name, "The function %s is not a valid identifier", id);
        }
	}

	@Override
	public void generateCode(MipsAsmGen out, CompilationScope globaScope) {
		String id = name.getText();
		TextLabel label = out.makeTextLabel(id);
		globaScope.addTextLabel(id, label);
	}

	@Override
	public Formula genFormula() {
		return BoolConst.TRUE;
	}

}

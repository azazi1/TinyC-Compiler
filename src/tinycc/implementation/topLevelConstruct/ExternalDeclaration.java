package tinycc.implementation.topLevelConstruct;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public abstract class ExternalDeclaration implements Declaration {
    protected Type type;
	protected Token name;
	protected String formulaVar;

	@Override
	public Type getType() {
		return this.type;
	}

	@Override
	public Token getName() {
		return this.name;
	}

	@Override
	public String getFormulaVar() {
		return this.formulaVar;
	}

	@Override
	public void setFormulaVar(int uniqueID) {
		this.formulaVar = this.getName().getText()+ "_" + uniqueID;
	}

	public abstract void checkCode(Diagnostic d, Scope globalScope);

    public abstract void generateCode(MipsAsmGen out, CompilationScope offs);

	public abstract Formula genFormula();
}

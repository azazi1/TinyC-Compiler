package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.MipsAsmGen;

/**
 * The main statement class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Statement {

	/**
	 * Creates a string representation of this statement.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

	public abstract void checkType(Diagnostic d, Scope s);

	public abstract void codeS(MipsAsmGen out, CompilationScope offs);

	public abstract Formula toPC(Formula postCond);

	public abstract Formula toVC(Formula postCond);
}

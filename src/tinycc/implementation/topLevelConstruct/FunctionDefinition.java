package tinycc.implementation.topLevelConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.scope.scopeExceptions.FunctionAlreadyDefined;
import tinycc.implementation.scope.scopeExceptions.IdAlreadyDeclared;
import tinycc.implementation.scope.scopeExceptions.IllegalDeclaration;
import tinycc.implementation.statement.BlockStatement;
import tinycc.implementation.statement.DeclarationStatement;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;
import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.JumpRegisterInstruction;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;
import tinycc.implementation.type.FunctionType;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class FunctionDefinition extends ExternalDeclaration {
    private List<Token> parameterNames;
	private Statement body;
    private List<Declaration> paramDeclarations;
    private List<Declaration> localDeclarations;

	public FunctionDefinition(Type type, Token name, List<Token> parameterNames, Statement body) {
		this.type = type;
		this.name = name;
        this.parameterNames = parameterNames;
        this.body = body;
        this.paramDeclarations = new ArrayList<Declaration>();
	}

    public List<Token> getParameterNames() {
        return parameterNames;
    }

    public Statement getBody() {
        return body;
    }

    private List<Type> getParameterTypes() {
        return ((FunctionType) this.getType()).getParameters();
    }

    private Type getReturnType() {
        return ((FunctionType) this.getType()).getReturnType();
    }

    public List<Declaration> getParameterDeclarations() {
        return paramDeclarations;
    }

    public List<Declaration> getLocalDeclarations() {
        return localDeclarations;
    }

	@Override
	public void checkCode(Diagnostic diag, Scope globalScope) {
        String id = this.getName().getText();
        if (getName().getKind() == TokenKind.IDENTIFIER) {
            try {
                // add the function_declaration to the global environment (if possible)
                globalScope.add(id, this);

                // add parameters to the new function scope (type environment) and parameters' declaration list
                Scope funScope = globalScope.newNestedScope(this.getReturnType());
                checkParametersType(diag, funScope);

                // if the body is a block, this block must use the function scope 
                if (body instanceof BlockStatement) {
                    ((BlockStatement) body).getStatements().stream().forEach(stmt -> stmt.checkType(diag, funScope));
                } else {
                    body.checkType(diag, funScope);
                }

                // annotate local declarations
                localDeclarations = funScope.getDeclarations()
                                            .stream()
                                            .filter(decl -> !paramDeclarations.contains(decl))
                                            .collect(Collectors.toList());

            } catch (IllegalDeclaration | IdAlreadyDeclared | FunctionAlreadyDefined e) {
                diag.printError(getName(), "The function must be only once defined", id);
            }
        } else {
            diag.printError(getName(), "The function not a valid identifier", id);
        }
	}

    public void checkParametersType(Diagnostic diag, Scope funScope) {
        boolean noVoidParam = getParameterTypes().stream().noneMatch(paramTy -> paramTy.isVoidType());
        if(noVoidParam) {
            for (int i=0; i<getParameterNames().size(); i++) {
                Token paramName = getParameterNames().get(i);
                Type  paramType = getParameterTypes().get(i);
                Declaration paramDecl = new DeclarationStatement(paramType, paramName);
                try {
                    paramDecl.setFormulaVar(funScope.getVariableID());
                    funScope.add(paramName.getText(), paramDecl);
                    paramDeclarations.add(paramDecl);
                } catch (IllegalDeclaration | IdAlreadyDeclared | FunctionAlreadyDefined e) {
                    diag.printError(paramName, "the parameter %s cannot be added to the scope of function %s", paramName, name);
                }
            }
        } else {
            diag.printError(name, "Some parameter of %s must not be of type void", name);
        }
    }

    @Override
    public void generateCode(MipsAsmGen out, CompilationScope globaScope) {
        // create label for later use
        String funcID = name.getText();
        TextLabel funcLabel = globaScope.lookupTextLabel(funcID);
        if (funcLabel == null) {
            funcLabel = out.makeTextLabel(funcID);
            globaScope.addTextLabel(funcID, funcLabel);
        }
        
        TextLabel funcEnd = out.makeTextLabel(name.getText()+"_end");

        // emit function label in MIPS code
        out.emitLabel(funcLabel);
        
        // pick a unique offset for the parameters (save mapping in the scope)
        CompilationScope offs = globaScope.newNestedCompilationScope();
        offs.setEndLabel(funcEnd);
        parameterNames.stream().forEach(param -> offs.add(param.getText()));

        /* generate code for the prologue:
         * 1) determine S = the maximum size of the stack frame = #parameters * 4bytes
         * 2) decrement the strack pointer by S+44
         * 3) store the return_address in the stack, where offset = S
         * 4) store all 10 temporary expressions in the stack
         * 5) store the parameters in the stack
         * 6) copy the function's max offset to the offset scope
        */
        GPRegister[] argRegs = {GPRegister.A0, GPRegister.A1, GPRegister.A2, GPRegister.A3};
        GPRegister[] tmpRegs = {GPRegister.T0, GPRegister.T1, GPRegister.T2, GPRegister.T3,
                                GPRegister.T4, GPRegister.T5, GPRegister.T6, GPRegister.T7,
                                GPRegister.T8, GPRegister.T9};
        int param_sz = parameterNames.size();
        int S = param_sz * 4;
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, -(S + 44));
        out.emitInstruction(MemoryInstruction.SW, GPRegister.RA, null, S, GPRegister.SP);
        for (int i=0; i<10; i++) {
            int offset = S + 4 + i*4;
            out.emitInstruction(MemoryInstruction.SW, tmpRegs[i], null, offset, GPRegister.SP);
        }
        for (int i=0; i<param_sz; i++) {
            int offset = i*4;
            out.emitInstruction(MemoryInstruction.SW, argRegs[i], null, offset, GPRegister.SP);
        }

        // memorize the max offset to be used after function call
        offs.setFuncMaxOffset();

        /**
         * generate code for function body
         * NOTE: if the body is a block, this block must get its local declaration 
         * from the function sematic scope  */
        if (body instanceof BlockStatement) {
            generateBodyCode(out, offs, ((BlockStatement) body));
        } else {
            body.codeS(out, offs);
        }

        // generate code for the epilogue
        out.emitLabel(funcEnd);
        out.emitInstruction(MemoryInstruction.LW, GPRegister.RA, null, S, GPRegister.SP);
        for (int i=0; i<10; i++) {
            int offset = S + 4 + i*4;
            out.emitInstruction(MemoryInstruction.LW, tmpRegs[i], null, offset, GPRegister.SP);
        }
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, offs.getMaxOffest() + 44);
        out.emitInstruction(JumpRegisterInstruction.JR, GPRegister.RA);
    }

    private void generateBodyCode(MipsAsmGen out, CompilationScope funOffs, BlockStatement body) {
        CompilationScope localOffs = funOffs.newNestedCompilationScope();
        int localAllocSize = localDeclarations.size() * 4;

        // pick a unique offset for the new local variable
        // allocate all local variables of this block (not its nested blocks)
        localDeclarations.stream().forEach(decl -> localOffs.add(decl.getName().getText()));
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, GPRegister.SP, -localAllocSize);

        // generate code for the block statements
        body.getStatements().stream().forEach(stmt -> stmt.codeS(out, localOffs));

        // deallocate all local variables
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, GPRegister.SP, localAllocSize);
    }

    @Override
    public Formula genFormula() {
        Formula pc = body.toPC(BoolConst.TRUE);
        Formula vc = body.toVC(BoolConst.TRUE);
        return new BinaryOpFormula(BinaryOperator.AND, pc, vc);
    }
}

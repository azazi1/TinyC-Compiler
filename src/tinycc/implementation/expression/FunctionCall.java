package tinycc.implementation.expression;

import java.util.List;
import java.util.stream.Collectors;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.PrimaryExpression.VarExpression;
import tinycc.implementation.scope.CompilationScope;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.mipsasmgen.TextLabel;
import tinycc.parser.Token;

public class FunctionCall extends Expression {
    
    private Token token;
    private Expression callee;
    private List<Expression> arguments;
    private List<Type> argumentsTy;
    
    public FunctionCall(Token token, Expression callee, List<Expression> arguments) {
        this.token = token;
        this.callee = callee;
        this.arguments = arguments;
    }

    public Token getToken() {
        return token;
    }

    public Expression getCallee() {
        return callee;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public List<Type> getArgumentTypes() {
        return argumentsTy;
    }
    
    @Override
    public String toString() {
        String str = "Call["+callee.toString();
        for (Expression arg : arguments) {
            str += ", "+arg.toString();
        }
        str += "]";
        return str;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // check the type of callee function
        Type calleeTy = callee.checkType(d, s);

        // extract the types of its arguments and annotate them
        argumentsTy = arguments.stream().map(arg -> arg.checkType(d, s)).collect(Collectors.toList());

        if (calleeTy.isFunctionType()) {

            // extract the types of its parameters
            List<Type> parametersTy = ((FunctionType) calleeTy).getParameters();

            // check if the arguments do NOT match with the function parametes
            if (parametersTy.size() != argumentsTy.size()) {
                d.printError(token, "The arguments of callee either too few or too many w.r.t. parameters of function %s", token);
                this.setType(Type.getErrorType());
                return this.getType();
            }
            
            for (int i=0; i<parametersTy.size(); i++) {
                if (!parametersTy.get(i).isAssignableFrom(arguments.get(i))) {
                    d.printError(token, "The arguments of callee is not assignable to the parameters of function %s", token);
                    this.setType(Type.getErrorType());
                    return this.getType();
                }
            }

            // if they do, extract and annotate the callee return_type
            Type returnTy = ((FunctionType) calleeTy).getReturnType();
            this.setType(returnTy);
        } else {
            d.printError(token, String.format("The callee %s does not have a function type"), token);
            this.setType(Type.getErrorType());
        }
        return this.getType();
    }

    @Override
    public void codeR(MipsAsmGen out, CompilationScope offs, List<GPRegister> regs) {
        // pass arguments
        GPRegister[] argRegs = {GPRegister.A0, GPRegister.A1, GPRegister.A2, GPRegister.A3};
        int argCalleeSZ = arguments.size();
        for (int i=0; i<argCalleeSZ; i++) {
            arguments.get(i).codeR(out, offs, regs.subList(i, regs.size()));
            out.emitInstruction(RegisterInstruction.OR, argRegs[i], regs.get(i), GPRegister.ZERO);
        }
        
        // call the callee function
        String calleeID = ((VarExpression) callee).getToken().getText();
        TextLabel calleeLabel = offs.lookupTextLabel(calleeID);
        out.emitInstruction(JumpInstruction.JAL, calleeLabel);
        out.emitInstruction(RegisterInstruction.OR, regs.get(0), GPRegister.V0, GPRegister.ZERO);
    }

    @Override
    public int rgesConsum() {
        //return arguments.stream().mapToInt(arg -> arg.rgesConsum()).sum();
        return 10;
    } 

    @Override
    public Formula toLogicalExpr() {
        throw new IllegalArgumentException("Function call must not exist by verification");
    }
}

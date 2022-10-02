package tinycc.implementation.type;

import java.util.List;

public class FunctionType extends Type {
    
    private Type returnType;
    private List<Type> parameters;

    public FunctionType(Type returnType, List<Type> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String str = "FunctionType["+returnType.toString();
        
        for (Type param : parameters) {
            str += ", "+param.toString();
        }

        str += "]";

        return str;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FunctionType))
            return false;
        else {
            FunctionType funTy = (FunctionType) obj;
            return this.returnType.equals(funTy.getReturnType()) 
                && this.parameters.equals(funTy.getParameters());
        }
    }

    @Override
    public int hashCode() {
        return 10000 + returnType.hashCode() + parameters.size()*1000;
    }

    /*@Override
    public void clone(Type ty) {
        FunctionType funcTy = (FunctionType)ty;
        this.returnType.clone(funcTy.getReturnType());
        funcTy.getParameters().stream().forEach(typ -> parameters.add(typ));
    }*/
}

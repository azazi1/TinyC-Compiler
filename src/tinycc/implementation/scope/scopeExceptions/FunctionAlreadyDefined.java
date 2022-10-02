package tinycc.implementation.scope.scopeExceptions;


public class FunctionAlreadyDefined extends Exception{
    private String id;

    public FunctionAlreadyDefined(String id) {
        super("The function has been already defined");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

package tinycc.implementation.scope.scopeExceptions;

public class IdUndeclared extends Exception{
    private String id;

    public IdUndeclared(String id) {
        super("The identifier has not been decleared yet");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

package tinycc.implementation.scope.scopeExceptions;

public class IdAlreadyDeclared extends Exception{
    private String id;

    public IdAlreadyDeclared(String id) {
        super("The identifier has been already decleared");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

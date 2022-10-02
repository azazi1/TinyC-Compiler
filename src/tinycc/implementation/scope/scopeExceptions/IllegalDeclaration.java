package tinycc.implementation.scope.scopeExceptions;


public class IllegalDeclaration extends Exception{
    private String id;

    public IllegalDeclaration(String id) {
        super("The varible can only be re-decleared with the same type");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

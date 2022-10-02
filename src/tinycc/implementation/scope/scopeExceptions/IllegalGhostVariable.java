package tinycc.implementation.scope.scopeExceptions;

public class IllegalGhostVariable extends Exception{
    private String id;

    public IllegalGhostVariable(String id) {
        super("The ghost variable must only be used in invariants");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

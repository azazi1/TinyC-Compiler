package tinycc.implementation.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tinycc.implementation.scope.scopeExceptions.FunctionAlreadyDefined;
import tinycc.implementation.scope.scopeExceptions.IdAlreadyDeclared;
import tinycc.implementation.scope.scopeExceptions.IdUndeclared;
import tinycc.implementation.scope.scopeExceptions.IllegalDeclaration;
import tinycc.implementation.statement.DeclarationStatement;
import tinycc.implementation.statement.WhileStatement;
import tinycc.implementation.topLevelConstruct.Declaration;
import tinycc.implementation.topLevelConstruct.FunctionDefinition;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class Scope {
    
    private final Map<String, Declaration> table;
    private String ghostVar;
    private final Scope parent;
    private final Type returnType;
    private List<Declaration> decls;
    public int varID;
    private final WhileStatement loop;
    private boolean invMode;

    public Scope() {
        this(null, null, null, 0);
    }

    private Scope(Scope parent, Type returnType, WhileStatement loop, int varID) {
        this.parent     = parent;
        this.table      = new HashMap<String, Declaration>();
        this.returnType = returnType;
        this.decls      = new ArrayList<Declaration>();
        this.varID      = varID;
        this.loop       = loop;
    }

    public Scope newNestedScope(Type returnType) {
        return new Scope(this, returnType, this.loop, this.varID);
    }

    public Scope newNestedLoopScope(Type returnType, WhileStatement loop) {
        return new Scope(this, returnType, loop, this.varID);
    }

    public Scope newNestedInvariantScope(Type returnType, WhileStatement loop) {
        Scope invScope = new Scope(this, returnType, loop, this.varID);
        invScope.enableInvMode();
        return invScope;
    }

    public Scope getParent() {
        return this.parent;
    }

    public Type getFuncReturnType() {
        return this.returnType;
    }

    public List<Declaration> getDeclarations() {
        return this.decls;
    }

    public WhileStatement getLoop() {
        return this.loop;
    }

    public int getVariableID() {
        //Scope currScope = this;
        //while (currScope != null) {
        //    currScope.varID++;
        //}
        return this.varID++;
    }

    public String getGhostVariable() {
        return this.ghostVar;
    }

    public void enableInvMode() {
        this.invMode = true;
    }

    public boolean isGhostVariable(String var) {
        return var.equals(this.ghostVar);
    }

    public void addGhost(Token loopBound) {
        String id = loopBound.getText();
        Declaration decl = new DeclarationStatement(Type.getIntType(), loopBound);
        decl.setFormulaVar(this.getVariableID());
        table.put(id, decl);
        this.ghostVar = id;
    }

    public void add(String id, Declaration decl) throws IllegalDeclaration, IdAlreadyDeclared, FunctionAlreadyDefined {
        // global scope
        if(parent == null) {
            try {
                if (checkAlreadyDefinedFun(id, decl)) {
                    throw new FunctionAlreadyDefined(id);
                }
                if(!lookup(id).getType().equals(decl.getType())) {
                    throw new IllegalDeclaration(id);
                }
                
            } catch (IdUndeclared e) {}
            table.put(id, decl);
            decls.add(decl);
        } 
        
        // local scope
        else {
            if (decl.getType().isFunctionType())
                throw new IllegalDeclaration(id);
            if (table.containsKey(id) && !isGhostVariable(id))
                throw new IdAlreadyDeclared(id);
            this.ghostVar = null;
            table.put(id, decl);
            decls.add(decl);
        }
    }

    public Declaration lookup(String id) throws IdUndeclared {
        Scope currScope = this;
        while (currScope != null) {
            if(currScope.table.containsKey(id) 
            && (!currScope.isGhostVariable(id) || this.invMode)) {
                return currScope.table.get(id);
            } else {
                currScope = currScope.getParent();
            }
        }
        throw new IdUndeclared(id);
    }


    public boolean checkAlreadyDefinedFun(String id, Declaration decl) throws IdUndeclared {
        return (decl instanceof FunctionDefinition) 
            && (lookup(id) instanceof FunctionDefinition)
            && lookup(id).getType().equals(decl.getType());
    }
}

package tinycc.implementation.expression.PrimaryExpression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public abstract class PrimaryExpression extends Expression {
    
    protected Token token;

    public Token getToken() {
        return token;
    }

    @Override
    public abstract Type checkType(Diagnostic d, Scope s);

    @Override
    public int rgesConsum() {
        return 1;
    }
}

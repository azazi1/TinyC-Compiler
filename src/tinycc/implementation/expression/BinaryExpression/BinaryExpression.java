package tinycc.implementation.expression.BinaryExpression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public abstract class BinaryExpression extends Expression {
    
    protected Token operator;
    protected Expression left;
    protected Expression right;
    
    public Token getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    } 

    @Override
    public String toString() {
        return "Binary_"+operator.toString()+"["+left.toString()+", "+right.toString()+"]";
    }

    @Override
    public abstract Type checkType(Diagnostic d, Scope s);

    @Override
    public int rgesConsum() {
        if(left.rgesConsum() == right.rgesConsum()) {
            return left.rgesConsum() + 1;
        } else {
            return left.rgesConsum() > right.rgesConsum() ? left.rgesConsum() : right.rgesConsum();
        }
    } 
}

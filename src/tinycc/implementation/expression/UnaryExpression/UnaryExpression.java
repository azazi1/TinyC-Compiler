package tinycc.implementation.expression.UnaryExpression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.scope.Scope;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public abstract class UnaryExpression extends Expression {
    
    protected Token operator;
    protected boolean postfix;
    protected Expression operand;
    
    public Token getOperator() {
        return operator;
    }

    public boolean getPostfix() {
        return postfix;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return "Unary_"+operator.toString()+"["+operand.toString()+"]";
    }

    @Override
    public abstract Type checkType(Diagnostic d, Scope s);

    @Override
    public int rgesConsum() {
        return operand.rgesConsum();
    }
}

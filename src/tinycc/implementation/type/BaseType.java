package tinycc.implementation.type;

import tinycc.parser.TokenKind;

public abstract class BaseType extends ObjectType {
    
   public abstract TokenKind getTokenKind();

    @Override
    public String toString() {
        return "Type_"+getTokenKind().toString();
    }
}

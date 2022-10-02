package tinycc.implementation.type;

import tinycc.parser.TokenKind;

public abstract class IntegerType extends BaseType{
    protected TokenKind kind;

    @Override
    public TokenKind getTokenKind() {
        return kind;
    }

}

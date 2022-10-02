package tinycc.implementation.type;

import tinycc.parser.TokenKind;

public class VoidType extends BaseType {
    
    private TokenKind kind;

    public VoidType() {
        this.kind = TokenKind.VOID;
    }

    @Override
    public TokenKind getTokenKind() {
        return kind;
    }

    @Override
	public boolean equals(Object obj) {
		return obj instanceof VoidType;
	}

    @Override
    public int hashCode() {
        return 10;
    }

    /*@Override
    public void clone(Type ty) {
        this.kind = ((VoidType)ty).getTokenKind();
    }*/
}

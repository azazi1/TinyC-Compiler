package tinycc.implementation.type;

import tinycc.parser.TokenKind;

public class IntType extends IntegerType {
    
    public IntType() {
        this.kind = TokenKind.INT;
    }

    @Override
	public boolean equals(Object obj) {
		return obj instanceof IntType;
	}

    @Override
    public int hashCode() {
        return 11;
    }
}

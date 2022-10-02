package tinycc.implementation.type;

import tinycc.parser.TokenKind;

public class CharType extends IntegerType {
    
    public CharType() {
        this.kind = TokenKind.CHAR;
    }

    @Override
	public boolean equals(Object obj) {
		return obj instanceof CharType;
	}

    @Override
    public int hashCode() {
        return 12;
    }
}

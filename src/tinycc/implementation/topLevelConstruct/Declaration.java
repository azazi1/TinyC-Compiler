package tinycc.implementation.topLevelConstruct;

import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public interface Declaration {
    
    public Type getType();
    
    public Token getName();

    public String getFormulaVar();

    public void setFormulaVar(int uniqueID);

}

package tinycc.implementation.scope;

import java.util.HashMap;
import java.util.Map;

import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.TextLabel;


public class CompilationScope {
    
    private final Map<String, Integer> table = new HashMap<String, Integer>();
    private final Map<String, DataLabel> usedDataLabels;
    private final Map<String, TextLabel> usedTextLabels;
    private final CompilationScope parent;
    private TextLabel funcEnd;
    private int maxOffset;
    private int accuOffset;
    private int funcMaxOffset;

    public CompilationScope() {
        this.parent = null;
        this.usedDataLabels = new HashMap<String, DataLabel>();
        this.usedTextLabels = new HashMap<String, TextLabel>();
    }

    private CompilationScope(CompilationScope parent) {
        this.parent         = parent;
        this.usedDataLabels = parent.usedDataLabels;
        this.usedTextLabels = parent.usedTextLabels;
        this.funcEnd        = parent.getEndLabel();
        this.funcMaxOffset  = parent.getFuncMaxOffset();
        this.accuOffset     = parent.getaccumlatedOffest();
    }

    public CompilationScope newNestedCompilationScope() {
        return new CompilationScope(this);
    }

    public CompilationScope getParent() {
        return parent;
    }

    public int getMaxOffest() {
        return this.maxOffset;
    }

    private void incrementOffest() {
        this.maxOffset += 4;
        this.accuOffset += 4;
    }

    public TextLabel getEndLabel() {
        return this.funcEnd;
    }

    public int getFuncMaxOffset() {
        return this.funcMaxOffset;
    }

    public void setFuncMaxOffset() {
        this.funcMaxOffset = this.getMaxOffest();
    }

    public int getaccumlatedOffest() {
        return this.accuOffset;
    }

    public void setEndLabel(TextLabel funcEnd) {
        this.funcEnd = funcEnd;
    }

    public void add(String id) {
        table.put(id, this.getMaxOffest());
        this.incrementOffest();
    }

    public int lookup(String id) {
        CompilationScope currScope = this;
        int offSum = 0;
        while (currScope != null) {
            if(currScope.table.containsKey(id)) {
                return offSum + currScope.table.get(id);
            } else {
                offSum += currScope.getMaxOffest();
                currScope = currScope.getParent();
            }
        }
        return -1;
    }

    public void addDataLabel(String id, DataLabel label) {
        usedDataLabels.put(id, label);
    }

    public DataLabel lookupDataLabel(String id) {
        return this.usedDataLabels.get(id);
    }

    public void addTextLabel(String id, TextLabel label) {
        usedTextLabels.put(id, label);
    }

    public TextLabel lookupTextLabel(String id) {
        return this.usedTextLabels.get(id);
    }
}

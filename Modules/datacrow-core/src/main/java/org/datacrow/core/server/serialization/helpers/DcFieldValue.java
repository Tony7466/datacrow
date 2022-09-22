package org.datacrow.core.server.serialization.helpers;

public class DcFieldValue {
    
    private int fieldIndex;
    private int moduleIndex;
    private Object value;
    private boolean changed;
    
    public DcFieldValue(int moduleIndex, int fieldIndex, Object value, boolean changed) {
        this.fieldIndex = fieldIndex;
        this.moduleIndex = moduleIndex;
        this.value = value;
        this.changed = changed;
    }
    
    public int getFieldIndex() {
        return fieldIndex;
    }

    public int getModuleIndex() {
        return moduleIndex;
    }
    
    public Object getValue() {
        return value;
    }
    
    public boolean isChanged() {
        return changed;
    }
}

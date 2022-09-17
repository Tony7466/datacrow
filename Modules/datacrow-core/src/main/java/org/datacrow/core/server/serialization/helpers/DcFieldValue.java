package org.datacrow.core.server.serialization.helpers;

public class DcFieldValue {
    
    private int index;
    private Object value;
    private boolean changed;
    
    public DcFieldValue(int index, Object value, boolean changed) {
        this.index = index;
        this.value = value;
        this.changed = changed;
    }
    
    public int getIndex() {
        return index;
    }
    
    public Object getValue() {
        return value;
    }
    
    public boolean isChanged() {
        return changed;
    }
}

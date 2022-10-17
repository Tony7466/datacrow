package org.datacrow.core.services;

import java.util.Collection;

public class FilterField {

    private Collection<?> options;
    private String name;
    
    public FilterField(String name, Collection<?> options) {
        this.options = options;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public Collection<?> getOptions() {
        return options;
    }
}

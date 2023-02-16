package com.etheller.warsmash.util;

public abstract class AbstractListItemProperty {
    
    protected ListItemEnum dataType;
    protected String rawValue;

    public AbstractListItemProperty(ListItemEnum dataType, String rawValue) {
        this.dataType = dataType;
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return this.rawValue;
    }

    public ListItemEnum getItemType() {
        return this.dataType;
    }

    // ======== GLOBAL ========= //

    public static AbstractListItemProperty createFromType(String input, ListItemEnum dataType) {
        switch(dataType) {
            case ITEM_STRING:
                return new ListItemStringProperty(input);
            default:
                return null;
        }
    }
}

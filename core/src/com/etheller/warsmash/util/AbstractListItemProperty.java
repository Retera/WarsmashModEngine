package com.etheller.warsmash.util;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;

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

    public int compare(AbstractListItemProperty itemProperty) {
        return 0;
    }

    // ======== GLOBAL ========= //

    public static AbstractListItemProperty createFromType(String input, ListItemEnum dataType, GameUI gameUI, DataSource dataSource) {
        switch(dataType) {
            case ITEM_STRING:
                return new ListItemStringProperty(input);
            case ITEM_MAP:
                return new ListItemMapProperty(dataType, input, gameUI, dataSource);
            default:
                return null;
        }
    }
}

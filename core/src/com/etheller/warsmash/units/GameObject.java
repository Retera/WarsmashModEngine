package com.etheller.warsmash.units;

import java.util.List;
import java.util.Set;

public interface GameObject {

	public void setField(String field, String value);

	public void setField(String field, String value, int index);

	public String getField(String field);

	public String getField(String field, int index);

	public int getFieldValue(String field);

	public int getFieldValue(String field, int index);

	public float getFieldFloatValue(String field);

	public List<? extends GameObject> getFieldAsList(String field, ObjectData objectData);

	public String getId();

	public ObjectData getTable();

	public String getName();

	public Set<String> keySet();

}
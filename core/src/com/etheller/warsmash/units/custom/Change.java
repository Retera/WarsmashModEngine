package com.etheller.warsmash.units.custom;

import com.etheller.warsmash.util.War3ID;

public final class Change {
	private War3ID id;
	private int vartype, level, dataptr;
	private int longval;
	private float realval;
	private String strval;

	private boolean boolval;
	private War3ID junkDNA;

	public War3ID getId() {
		return this.id;
	}

	public void setId(final War3ID id) {
		this.id = id;
	}

	public int getVartype() {
		return this.vartype;
	}

	public void setVartype(final int vartype) {
		this.vartype = vartype;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	public int getDataptr() {
		return this.dataptr;
	}

	public void setDataptr(final int dataptr) {
		this.dataptr = dataptr;
	}

	public int getLongval() {
		return this.longval;
	}

	public void setLongval(final int longval) {
		this.longval = longval;
	}

	public float getRealval() {
		return this.realval;
	}

	public void setRealval(final float realval) {
		this.realval = realval;
	}

	public String getStrval() {
		return this.strval;
	}

	public void setStrval(final String strval) {
		this.strval = strval;
	}

	public boolean isBoolval() {
		return this.boolval;
	}

	public void setBoolval(final boolean boolval) {
		this.boolval = boolval;
	}

	public void setJunkDNA(final War3ID junkDNA) {
		this.junkDNA = junkDNA;
	}

	public War3ID getJunkDNA() {
		return this.junkDNA;
	}

	public void copyFrom(final Change other) {
		this.id = other.id;
		this.level = other.level;
		this.dataptr = other.dataptr;
		this.vartype = other.vartype;
		this.longval = other.longval;
		this.realval = other.realval;
		this.strval = other.strval;
		this.boolval = other.boolval;
		this.junkDNA = other.junkDNA;
	}

	@Override
	public Change clone() {
		final Change copy = new Change();
		copy.copyFrom(this);
		return copy;
	}
}

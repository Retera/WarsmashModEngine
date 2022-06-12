package com.etheller.warsmash.parsers.terrain;

public class TilePathing {
	boolean unwalkable;
	boolean unflyable;
	boolean unbuildable;

	public byte mask() {
		byte mask = 0;
		mask |= this.unwalkable ? 0b00000010 : 0;
		mask |= this.unflyable ? 0b00000100 : 0;
		mask |= this.unbuildable ? 0b00001000 : 0;
		return mask;
	}
}
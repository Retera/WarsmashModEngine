package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import com.etheller.warsmash.parsers.w3x.w3e.Corner;

public class RenderCorner extends Corner {
	public boolean cliff;
	public boolean romp;
	public float rampAdjust;
	public float depth;
	public boolean hideCliff;

	public RenderCorner(final Corner corner) {
		super(corner);
	}

}

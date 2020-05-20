package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumSet;

import com.etheller.warsmash.parsers.fdf.datamodel.BackdropCornerFlags;
import com.etheller.warsmash.parsers.fdf.datamodel.Insets;
import com.etheller.warsmash.parsers.fdf.frames.base.Frame;

public class FrameBackdrop extends Frame {
	private boolean tileBackground;
	private boolean halfSides;
	private boolean blendAll;
	private String background;
	private EnumSet<BackdropCornerFlags> cornerFlags;
	private float cornerSize;
	private float backgroundSize;
	private Insets backgroundInsets;
	private String cornerFile;
	private String leftFile;
	private String rightFile;
	private String topFile;
	private String bottomFile;
	private String edgeFile;

	public boolean isTileBackground() {
		return this.tileBackground;
	}

	public boolean isHalfSides() {
		return this.halfSides;
	}

	public boolean isBlendAll() {
		return this.blendAll;
	}

	public String getBackground() {
		return this.background;
	}

	public EnumSet<BackdropCornerFlags> getCornerFlags() {
		return this.cornerFlags;
	}

	public float getCornerSize() {
		return this.cornerSize;
	}

	public String getCornerFile() {
		return this.cornerFile;
	}

	public String getLeftFile() {
		return this.leftFile;
	}

	public String getRightFile() {
		return this.rightFile;
	}

	public String getTopFile() {
		return this.topFile;
	}

	public String getBottomFile() {
		return this.bottomFile;
	}

	public void setTileBackground(final boolean tileBackground) {
		this.tileBackground = tileBackground;
	}

	public void setHalfSides(final boolean halfSides) {
		this.halfSides = halfSides;
	}

	public void setBlendAll(final boolean blendAll) {
		this.blendAll = blendAll;
	}

	public void setBackground(final String background) {
		this.background = background;
	}

	public void setCornerFlags(final EnumSet<BackdropCornerFlags> cornerFlags) {
		this.cornerFlags = cornerFlags;
	}

	public void setCornerSize(final float cornerSize) {
		this.cornerSize = cornerSize;
	}

	public void setCornerFile(final String cornerFile) {
		this.cornerFile = cornerFile;
	}

	public void setLeftFile(final String leftFile) {
		this.leftFile = leftFile;
	}

	public void setRightFile(final String rightFile) {
		this.rightFile = rightFile;
	}

	public void setTopFile(final String topFile) {
		this.topFile = topFile;
	}

	public void setBottomFile(final String bottomFile) {
		this.bottomFile = bottomFile;
	}

}

package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumSet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etheller.warsmash.parsers.fdf.datamodel.BackdropCornerFlags;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;

public class BackdropFrame extends AbstractUIFrame {
	private final boolean decorateFileNames;
	private final boolean tileBackground;
	private Texture background;
	private final EnumSet<BackdropCornerFlags> cornerFlags;
	private final float cornerSize;
	private float backgroundSize;
	private final Vector4Definition backgroundInsets;
	private final Texture edgeFile;
	private final float edgeFileWidth;
	private final float edgeUVWidth;
	private final float edgeFileHeight;
	private final float edgeUVHeight;
	private final boolean mirrored;

	public BackdropFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final boolean tileBackground, final Texture background, final EnumSet<BackdropCornerFlags> cornerFlags,
			final float cornerSize, final float backgroundSize, final Vector4Definition backgroundInsets,
			final Texture edgeFile, final boolean mirrored) {
		super(name, parent);
		this.decorateFileNames = decorateFileNames;
		this.tileBackground = tileBackground && (backgroundSize > 0);
		this.background = background;
		this.cornerFlags = cornerFlags;
		this.cornerSize = cornerSize;
		this.backgroundSize = backgroundSize;
		this.backgroundInsets = backgroundInsets;
		this.edgeFile = edgeFile;
		this.edgeFileWidth = edgeFile == null ? 0.0f : edgeFile.getWidth();
		this.edgeFileHeight = edgeFile == null ? 0.0f : edgeFile.getHeight();
		this.edgeUVWidth = 1f / 8f;
		this.edgeUVHeight = 1f;
		this.mirrored = mirrored;
		this.backgroundSize -= (backgroundInsets.getX() + backgroundInsets.getY() + backgroundInsets.getZ()
				+ backgroundInsets.getW()) / 2f;
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		if (this.background != null) {
			final float backgroundX = this.renderBounds.x + this.backgroundInsets.getX();
			final float backgroundY = this.renderBounds.y + this.backgroundInsets.getY();
			final float backgroundWidth = this.renderBounds.width - this.backgroundInsets.getX()
					- this.backgroundInsets.getZ();
			final float backgroundHeight = this.renderBounds.height - this.backgroundInsets.getY()
					- this.backgroundInsets.getW();
			if (this.tileBackground) {
				final float backgroundVerticalRepeatCount = (backgroundHeight / this.backgroundSize);
				final float backgroundHorizontalRepeatCount = backgroundWidth / this.backgroundSize;
				final float backgroundHeightRemainder = backgroundHeight % this.backgroundSize;
				final float backgroundHeightRemainderRatio = backgroundHeightRemainder / this.backgroundSize;
				final float backgroundWidthRemainder = backgroundWidth % this.backgroundSize;
				final float backgroundWidthRemainderRatio = backgroundWidthRemainder / this.backgroundSize;
				final int backgroundVerticalFloorRepeatCount = (int) Math.floor(backgroundVerticalRepeatCount);
				final int backgroundHorizontalFloorRepeatCount = (int) Math.floor(backgroundHorizontalRepeatCount);
				for (int j = 0; j < backgroundVerticalFloorRepeatCount; j++) {
					for (int i = 0; i < backgroundHorizontalFloorRepeatCount; i++) {
						batch.draw(this.background, backgroundX + (i * this.backgroundSize),
								backgroundY + (j * this.backgroundSize), this.backgroundSize, this.backgroundSize);
					}
					batch.draw(this.background,
							backgroundX + ((backgroundHorizontalFloorRepeatCount) * this.backgroundSize),
							backgroundY + (j * this.backgroundSize), backgroundWidthRemainder, this.backgroundSize, 0,
							1.0f, backgroundWidthRemainderRatio, 0);
				}
				for (int i = 0; i < backgroundHorizontalFloorRepeatCount; i++) {
					batch.draw(this.background, backgroundX + (i * this.backgroundSize),
							backgroundY + (backgroundVerticalFloorRepeatCount * this.backgroundSize),
							this.backgroundSize, backgroundHeightRemainder, 0, 1.0f, 1.0f,
							1.0f - backgroundHeightRemainderRatio);
				}
				batch.draw(this.background,
						backgroundX + ((backgroundHorizontalFloorRepeatCount) * this.backgroundSize),
						backgroundY + (backgroundVerticalFloorRepeatCount * this.backgroundSize),
						backgroundWidthRemainder, backgroundHeightRemainder, 0, 1.0f, backgroundWidthRemainderRatio,
						1.0f - backgroundHeightRemainderRatio);
			}
			else {
				if (this.mirrored) {
					batch.draw(this.background, backgroundX, backgroundY, backgroundWidth, backgroundHeight, 0, 0,
							this.background.getWidth(), this.background.getHeight(), true, false);
				}
				else {
					batch.draw(this.background, backgroundX, backgroundY, backgroundWidth, backgroundHeight);
				}
			}
		}
		if (this.edgeFile != null) {
			float widthNeedingEdge = this.renderBounds.width;
			float heightNeedingEdge = this.renderBounds.height;
			float xNeedingEdge = this.renderBounds.x;
			float yNeedingEdge = this.renderBounds.y;
			if (this.cornerFlags.contains(BackdropCornerFlags.BL)) {
				batch.draw(this.edgeFile, this.renderBounds.x, this.renderBounds.y, this.cornerSize, this.cornerSize,
						this.edgeUVWidth * 6, this.edgeUVHeight, this.edgeUVWidth * 7, 0);
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.BR)) {
				batch.draw(this.edgeFile, (this.renderBounds.x + this.renderBounds.width) - this.cornerSize,
						this.renderBounds.y, this.cornerSize, this.cornerSize, this.edgeUVWidth * 7, this.edgeUVHeight,
						this.edgeUVWidth * 8, 0);
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.UL)) {
				batch.draw(this.edgeFile, this.renderBounds.x,
						this.renderBounds.y + (this.renderBounds.height - this.cornerSize), this.cornerSize,
						this.cornerSize, this.edgeUVWidth * 4, this.edgeUVHeight, this.edgeUVWidth * 5, 0);
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.UR)) {
				batch.draw(this.edgeFile, (this.renderBounds.x + this.renderBounds.width) - this.cornerSize,
						this.renderBounds.y + (this.renderBounds.height - this.cornerSize), this.cornerSize,
						this.cornerSize, this.edgeUVWidth * 5, this.edgeUVHeight, this.edgeUVWidth * 6, 0);
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.BL)
					|| this.cornerFlags.contains(BackdropCornerFlags.BR)) {
				yNeedingEdge += this.cornerSize;
				heightNeedingEdge -= this.cornerSize;
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.UL)
					|| this.cornerFlags.contains(BackdropCornerFlags.BL)) {
				xNeedingEdge += this.cornerSize;
				widthNeedingEdge -= this.cornerSize;
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.UR)
					|| this.cornerFlags.contains(BackdropCornerFlags.BR)) {
				widthNeedingEdge -= this.cornerSize;
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.UR)
					|| this.cornerFlags.contains(BackdropCornerFlags.UL)) {
				heightNeedingEdge -= this.cornerSize;
			}
			if (heightNeedingEdge < 0) {
				heightNeedingEdge = 0;
			}
			if (widthNeedingEdge < 0) {
				widthNeedingEdge = 0;
			}
			final float borderVerticalRepeatCount = (heightNeedingEdge / this.cornerSize);
			final float heightRemainder = (heightNeedingEdge % this.cornerSize) / this.cornerSize;
			final int heightRemainderByHeight = (int) (heightRemainder * this.edgeFileHeight);
			final int heightRemainderByCornerSize = (int) (heightRemainder * this.cornerSize);
			final int borderVerticalRepeatCountFloored = (int) Math.floor(borderVerticalRepeatCount);
			if (this.cornerFlags.contains(BackdropCornerFlags.L)) {
				for (int i = 0; i < borderVerticalRepeatCountFloored; i++) {
					batch.draw(this.edgeFile, this.renderBounds.x, yNeedingEdge + (this.cornerSize * i),
							this.cornerSize, this.cornerSize, this.edgeUVWidth * 0, this.edgeUVHeight,
							this.edgeUVWidth * 1, 0);
				}
				if (borderVerticalRepeatCount > borderVerticalRepeatCountFloored) {
					batch.draw(this.edgeFile, this.renderBounds.x,
							yNeedingEdge + (this.cornerSize * borderVerticalRepeatCountFloored), this.cornerSize,
							heightRemainderByCornerSize, this.edgeUVWidth * 0, heightRemainderByHeight,
							this.edgeUVWidth * 1, 0);
				}
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.R)) {
				for (int i = 0; i < borderVerticalRepeatCountFloored; i++) {
					batch.draw(this.edgeFile, (this.renderBounds.x + this.renderBounds.width) - this.cornerSize,
							yNeedingEdge + (this.cornerSize * i), this.cornerSize, this.cornerSize,
							this.edgeUVWidth * 1, this.edgeUVHeight, this.edgeUVWidth * 2, 0);
				}
				if (borderVerticalRepeatCount > borderVerticalRepeatCountFloored) {
					batch.draw(this.edgeFile, (this.renderBounds.x + this.renderBounds.width) - this.cornerSize,
							yNeedingEdge + (this.cornerSize * borderVerticalRepeatCountFloored), this.cornerSize,
							heightRemainderByCornerSize, this.edgeUVWidth * 1, heightRemainderByHeight,
							this.edgeUVWidth * 2, 0);
				}
			}

			final float borderHorizontalRepeatCount = widthNeedingEdge / this.cornerSize;
			final float widthRemainder = (widthNeedingEdge % this.cornerSize) / this.cornerSize;
			final int widthRemainderByHeight = (int) (widthRemainder * this.edgeFileHeight);
			final int widthRemainderByCornerSize = (int) (widthRemainder * this.cornerSize);
			final int borderHorizontalRepeatCountFloored = (int) Math.floor(borderHorizontalRepeatCount);
			final float halfPi = 270;
			if (this.cornerFlags.contains(BackdropCornerFlags.B)) {
				for (int i = 0; i < borderHorizontalRepeatCountFloored; i++) {
					batch.draw(this.edgeFile, xNeedingEdge + (this.cornerSize * i), this.renderBounds.y,
							this.cornerSize / 2, this.cornerSize / 2, this.cornerSize, this.cornerSize, 1.0f, 1.0f,
							halfPi, (int) ((this.edgeFileWidth * 3f) / 8f), 0, (int) (this.edgeFileWidth / 8),
							(int) this.edgeFileHeight, false, false);
				}
				if (borderHorizontalRepeatCount > borderHorizontalRepeatCountFloored) {
					batch.draw(this.edgeFile,
							(xNeedingEdge + (this.cornerSize * borderHorizontalRepeatCountFloored))
									- ((this.cornerSize - widthRemainderByCornerSize) / 2),
							this.renderBounds.y + ((this.cornerSize - widthRemainderByCornerSize) / 2),
							this.cornerSize / 2, widthRemainderByCornerSize / 2, this.cornerSize,
							widthRemainderByCornerSize, 1.0f, 1.0f, halfPi, (int) ((this.edgeFileWidth * 3f) / 8f), 0,
							(int) (this.edgeFileWidth / 8), widthRemainderByHeight, false, false);
				}
			}
			if (this.cornerFlags.contains(BackdropCornerFlags.T)) {
				for (int i = 0; i < borderHorizontalRepeatCountFloored; i++) {
					batch.draw(this.edgeFile, xNeedingEdge + (this.cornerSize * i),
							this.renderBounds.y + (this.renderBounds.height - this.cornerSize), this.cornerSize / 2,
							this.cornerSize / 2, this.cornerSize, this.cornerSize, 1.0f, 1.0f, halfPi,
							(int) ((this.edgeFileWidth * 2f) / 8f), 0, (int) (this.edgeFileWidth / 8),
							(int) this.edgeFileHeight, false, false);
				}
				if (borderHorizontalRepeatCount > borderHorizontalRepeatCountFloored) {
					batch.draw(this.edgeFile,
							(xNeedingEdge + (this.cornerSize * borderHorizontalRepeatCountFloored))
									- ((this.cornerSize - widthRemainderByCornerSize) / 2),
							this.renderBounds.y
									+ (this.renderBounds.height - ((this.cornerSize + widthRemainderByCornerSize) / 2)),
							this.cornerSize / 2, widthRemainderByCornerSize / 2, this.cornerSize,
							widthRemainderByCornerSize, 1.0f, 1.0f, halfPi, (int) ((this.edgeFileWidth * 2f) / 8f), 0,
							(int) (this.edgeFileWidth / 8), widthRemainderByHeight, false, false);
				}
			}
		}
		super.internalRender(batch, baseFont, glyphLayout);
	}

	public float getCornerSize() {
		return this.cornerSize;
	}

	public void setBackground(final Texture background) {
		this.background = background;
	}
}

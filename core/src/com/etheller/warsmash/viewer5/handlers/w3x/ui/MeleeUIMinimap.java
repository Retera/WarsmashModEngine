package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;

public class MeleeUIMinimap {
	private final Rectangle minimap;
	private final Rectangle minimapFilledArea;
	private final Texture minimapTexture;
	private final Rectangle playableMapArea;
	private final Texture[] teamColors;

	public MeleeUIMinimap(final Rectangle displayArea, final Rectangle playableMapArea, final Texture minimapTexture,
			final Texture[] teamColors) {
		this.playableMapArea = playableMapArea;
		this.minimapTexture = minimapTexture;
		this.teamColors = teamColors;
		this.minimap = displayArea;
		final float worldWidth = playableMapArea.getWidth();
		final float worldHeight = playableMapArea.getHeight();
		final float worldSize = Math.max(worldWidth, worldHeight);
		final float minimapFilledWidth = (worldWidth / worldSize) * this.minimap.width;
		final float minimapFilledHeight = (worldHeight / worldSize) * this.minimap.height;

		this.minimapFilledArea = new Rectangle(this.minimap.x + ((this.minimap.width - minimapFilledWidth) / 2),
				this.minimap.y + ((this.minimap.height - minimapFilledHeight) / 2), minimapFilledWidth,
				minimapFilledHeight);
	}

	public void render(final SpriteBatch batch, final Iterable<RenderUnit> units) {
		batch.draw(this.minimapTexture, this.minimap.x, this.minimap.y, this.minimap.width, this.minimap.height);

		for (final RenderUnit unit : units) {
			final Texture minimapIcon = this.teamColors[unit.getSimulationUnit().getPlayerIndex()];
			batch.draw(minimapIcon,
					this.minimapFilledArea.x
							+ (((unit.location[0] - this.playableMapArea.getX()) / (this.playableMapArea.getWidth()))
									* this.minimapFilledArea.width),
					this.minimapFilledArea.y
							+ (((unit.location[1] - this.playableMapArea.getY()) / (this.playableMapArea.getHeight()))
									* this.minimapFilledArea.height),
					4, 4);
		}
	}

	public Vector2 getWorldPointFromScreen(final float screenX, final float screenY) {
		final Rectangle filledArea = this.minimapFilledArea;
		final float clickX = (screenX - filledArea.x) / filledArea.width;
		final float clickY = (screenY - filledArea.y) / filledArea.height;
		final float worldX = (clickX * this.playableMapArea.width) + this.playableMapArea.x;
		final float worldY = (clickY * this.playableMapArea.height) + this.playableMapArea.y;
		return new Vector2(worldX, worldY);

	}

	public boolean containsMouse(final float x, final float y) {
		return this.minimapFilledArea.contains(x, y);
	}
}

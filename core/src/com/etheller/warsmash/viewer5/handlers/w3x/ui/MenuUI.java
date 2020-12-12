package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;

public class MenuUI {
	private static final Vector2 screenCoordsVector = new Vector2();

	private final DataSource dataSource;
	private final Scene uiScene;
	private final ExtendViewport uiViewport;
	private final FreeTypeFontGenerator fontGenerator;
	private final MdxViewer viewer;
	private final RootFrameListener rootFrameListener;
	private final float widthRatioCorrection;
	private final float heightRatioCorrection;
	private GameUI rootFrame;
	private SpriteFrame cursorFrame;

	private UIFrame mainMenuFrame;

	private SpriteFrame glueSpriteLayerTopRight;

	private SpriteFrame glueSpriteLayerTopLeft;

	public MenuUI(final DataSource dataSource, final ExtendViewport uiViewport,
			final FreeTypeFontGenerator fontGenerator, final Scene uiScene, final MdxViewer viewer,
			final RootFrameListener rootFrameListener) {
		this.dataSource = dataSource;
		this.uiViewport = uiViewport;
		this.fontGenerator = fontGenerator;
		this.uiScene = uiScene;
		this.viewer = viewer;
		this.rootFrameListener = rootFrameListener;

		this.widthRatioCorrection = this.uiViewport.getMinWorldWidth() / 1600f;
		this.heightRatioCorrection = this.uiViewport.getMinWorldHeight() / 1200f;
	}

	public float getHeightRatioCorrection() {
		return this.heightRatioCorrection;
	}

	/**
	 * Called "main" because this was originally written in JASS so that maps could
	 * override it, and I may convert it back to the JASS at some point.
	 */
	public void main() {
		// =================================
		// Load skins and templates
		// =================================
		this.rootFrame = new GameUI(this.dataSource, GameUI.loadSkin(this.dataSource, 1), this.uiViewport,
				this.fontGenerator, this.uiScene, this.viewer);
		this.rootFrameListener.onCreate(this.rootFrame);
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\FrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load FrameDef.toc", exc);
		}
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\SmashFrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load SmashFrameDef.toc", exc);
		}
		this.mainMenuFrame = this.rootFrame.createFrame("MainMenuFrame", this.rootFrame, 0, 0);

		final SpriteFrame warcraftIIILogo = (SpriteFrame) this.rootFrame.getFrameByName("WarCraftIIILogo", 0);
		this.rootFrame.setSpriteFrameModel(warcraftIIILogo, this.rootFrame.getSkinField("MainMenuLogo_V1"));
		this.rootFrame.getFrameByName("RealmSelect", 0).setVisible(false);

		this.glueSpriteLayerTopRight = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerTopRight", this.rootFrame, "", 0);
		this.glueSpriteLayerTopRight.setSetAllPoints(true);
		final String topRightModel = this.rootFrame.getSkinField("GlueSpriteLayerTopRight_V1");
		this.rootFrame.setSpriteFrameModel(this.glueSpriteLayerTopRight, topRightModel);
		this.glueSpriteLayerTopRight.setSequence("MainMenu Birth");

		this.glueSpriteLayerTopLeft = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerTopLeft", this.rootFrame, "", 0);
		this.glueSpriteLayerTopLeft.setSetAllPoints(true);
		final String topLeftModel = this.rootFrame.getSkinField("GlueSpriteLayerTopLeft_V1");
		this.rootFrame.setSpriteFrameModel(this.glueSpriteLayerTopLeft, topLeftModel);
		this.glueSpriteLayerTopLeft.setSequence("MainMenu Birth");

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", this.rootFrame,
				"", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
		this.cursorFrame.setSequence("Normal");
		this.cursorFrame.setZDepth(-1.0f);
		Gdx.input.setCursorCatched(true);

		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);

	}

	public void resize() {

	}

	public void render(final SpriteBatch batch, final BitmapFont font20, final GlyphLayout glyphLayout) {
		this.rootFrame.render(batch, font20, glyphLayout);
	}

	public void update(final float deltaTime) {

		final int baseMouseX = Gdx.input.getX();
		int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		int mouseY = baseMouseY;
		final int minX = this.uiViewport.getScreenX();
		final int maxX = minX + this.uiViewport.getScreenWidth();
		final int minY = this.uiViewport.getScreenY();
		final int maxY = minY + this.uiViewport.getScreenHeight();

		mouseX = Math.max(minX, Math.min(maxX, mouseX));
		mouseY = Math.max(minY, Math.min(maxY, mouseY));
		if (Gdx.input.isCursorCatched()) {
			Gdx.input.setCursorPosition(mouseX, mouseY);
		}

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);
		this.cursorFrame.setSequence("Normal");

		if (this.glueSpriteLayerTopRight.isSequenceEnded()) {
			this.glueSpriteLayerTopRight.setSequence("MainMenu Stand");
		}
	}
}

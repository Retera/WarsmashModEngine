package com.etheller.warsmash.parsers.fdf.frames;

import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.LuaEnvironment;
import com.etheller.warsmash.parsers.fdf.UIFrameLuaWrapper;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

/**
 * The WoW {@code <Minimap>} frame: a north-up, circular top-down view of the
 * terrain around the player, built from the client's per-tile minimap art
 * ({@code World\Minimaps\<Map>\map<col>_<row>.blp}, one 256px image per
 * 533-yard WDT tile) with the rotating player arrow on top.
 *
 * <p>
 * The player's render-space position is converted back to WoW tile coordinates
 * through {@link War3MapViewer.WdtWorldMapping}; every tile intersecting the
 * zoom radius is drawn into the frame's square, and a fragment shader clips
 * the result to a soft-edged circle measured in the batch's own coordinate
 * space (so the mask needs no per-frame texture work and no viewport math).
 * The ring art and zoom buttons are ordinary child frames from Minimap.xml and
 * draw over this.
 */
public class MinimapFrame extends SimpleFrame {
	/** Half-width of the view in WDT tiles per zoom level (0 = furthest out). */
	private static final float[] ZOOM_RADIUS_TILES = { 1.0f, 0.75f, 0.55f, 0.4f, 0.3f, 0.22f };
	private static final int DEFAULT_ZOOM = 2;
	private static final String PLAYER_ARROW_TEXTURE = "Interface\\Minimap\\MinimapArrow.blp";
	/** Arrow sprite size as a fraction of the minimap diameter. */
	private static final float PLAYER_ARROW_SIZE = 0.13f;

	private static final String MASK_VERTEX_SHADER = "" //
			+ "attribute vec4 a_position;\n" //
			+ "attribute vec4 a_color;\n" //
			+ "attribute vec2 a_texCoord0;\n" //
			+ "uniform mat4 u_projTrans;\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "varying vec2 v_position;\n" //
			+ "void main() {\n" //
			+ "  v_color = a_color;\n" //
			+ "  v_color.a = v_color.a * (255.0/254.0);\n" //
			+ "  v_texCoords = a_texCoord0;\n" //
			+ "  v_position = a_position.xy;\n" //
			+ "  gl_Position = u_projTrans * a_position;\n" //
			+ "}\n";
	private static final String MASK_FRAGMENT_SHADER = "" //
			+ "#ifdef GL_ES\n" //
			+ "precision mediump float;\n" //
			+ "#endif\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "varying vec2 v_position;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "uniform vec2 u_circleCenter;\n" // in the batch's (UI) coordinate space, same as a_position
			+ "uniform float u_circleRadius;\n" //
			+ "void main() {\n" //
			+ "  float dist = length(v_position - u_circleCenter);\n" //
			+ "  float edge = u_circleRadius * 0.02;\n" //
			+ "  float mask = 1.0 - smoothstep(u_circleRadius - edge, u_circleRadius, dist);\n" //
			+ "  vec4 color = v_color * texture2D(u_texture, v_texCoords);\n" //
			+ "  color.a *= mask;\n" //
			+ "  if (color.a <= 0.001) { discard; }\n" //
			+ "  gl_FragColor = color;\n" //
			+ "}\n";
	private static ShaderProgram maskShader;

	private final GameUI gameUI;
	private int zoom = DEFAULT_ZOOM;
	/** Tile textures by path; a missing tile is cached as null so it is not re-fetched every frame. */
	private final Map<String, Texture> tileTextures = new HashMap<>();
	private Texture arrowTexture;
	private boolean arrowTextureLoaded;
	private final float[] tileHeap = new float[2];

	public MinimapFrame(final String name, final UIFrame parent, final GameUI gameUI) {
		super(name, parent);
		this.gameUI = gameUI;
	}

	public int getZoom() {
		return this.zoom;
	}

	public int getZoomLevels() {
		return ZOOM_RADIUS_TILES.length;
	}

	public void setZoom(final int zoom) {
		this.zoom = Math.max(0, Math.min(ZOOM_RADIUS_TILES.length - 1, zoom));
	}

	private static ShaderProgram getMaskShader() {
		if (maskShader == null) {
			ShaderProgram.pedantic = false;
			maskShader = new ShaderProgram(MASK_VERTEX_SHADER, MASK_FRAGMENT_SHADER);
			if (!maskShader.isCompiled()) {
				System.err.println("Minimap mask shader failed to compile: " + maskShader.getLog());
			}
		}
		return maskShader;
	}

	private Texture getTileTexture(final String path) {
		if (this.tileTextures.containsKey(path)) {
			return this.tileTextures.get(path);
		}
		Texture texture = null;
		try {
			texture = this.gameUI.loadTexture(path);
		}
		catch (final Exception exc) {
			// no art for this tile
		}
		this.tileTextures.put(path, texture);
		return texture;
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		renderTerrain(batch);
		super.internalRender(batch, baseFont, glyphLayout);
	}

	private void renderTerrain(final SpriteBatch batch) {
		final LuaEnvironment luaEnvironment = this.gameUI.getLuaGlobals();
		if (luaEnvironment == null) {
			return;
		}
		final War3MapViewer viewer = luaEnvironment.getWar3MapViewer();
		final CUnit pawn = luaEnvironment.getPawnUnit();
		if ((viewer == null) || (pawn == null)) {
			return;
		}
		final War3MapViewer.WdtWorldMapping mapping = viewer.getWdtWorldMappingAt(pawn.getX());
		if ((mapping == null) || (mapping.mapName == null)) {
			return;
		}
		final ShaderProgram shader = getMaskShader();
		if (!shader.isCompiled()) {
			return;
		}
		mapping.wc3ToWowTile(pawn.getX(), pawn.getY(), this.tileHeap);
		final float playerCol = this.tileHeap[0];
		final float playerRow = this.tileHeap[1];

		final float diameter = Math.min(this.renderBounds.width, this.renderBounds.height);
		final float centerX = this.renderBounds.x + (this.renderBounds.width / 2);
		final float centerY = this.renderBounds.y + (this.renderBounds.height / 2);
		final float radiusTiles = ZOOM_RADIUS_TILES[this.zoom];
		final float pixelsPerTile = diameter / (2 * radiusTiles);

		// The circle mask works in the batch's own coordinate space (the vertex
		// positions handed to batch.draw), so it needs no knowledge of the window
		// or viewport: the fragment shader just measures each pixel's distance from
		// the frame center against the frame radius.
		final ShaderProgram previousShader = batch.getShader();
		batch.setShader(shader);
		shader.bind();
		shader.setUniformf("u_circleCenter", centerX, centerY);
		shader.setUniformf("u_circleRadius", diameter / 2);

		final int minCol = (int) Math.floor(playerCol - radiusTiles);
		final int maxCol = (int) Math.floor(playerCol + radiusTiles);
		final int minRow = (int) Math.floor(playerRow - radiusTiles);
		final int maxRow = (int) Math.floor(playerRow + radiusTiles);
		for (int row = minRow; row <= maxRow; row++) {
			for (int col = minCol; col <= maxCol; col++) {
				if ((col < 0) || (col >= 64) || (row < 0) || (row >= 64)) {
					continue;
				}
				final Texture texture = getTileTexture(
						"World\\Minimaps\\" + mapping.mapName + "\\map" + col + "_" + row + ".blp");
				if (texture == null) {
					continue;
				}
				// North is up: tile rows increase southward, so a higher row sits lower.
				final float x = centerX + ((col - playerCol) * pixelsPerTile);
				final float top = centerY + ((playerRow - row) * pixelsPerTile);
				batch.draw(texture, x, top - pixelsPerTile, pixelsPerTile, pixelsPerTile);
			}
		}
		batch.setShader(previousShader);

		// Player arrow: the art points north (up); Warcraft III facing is degrees
		// counter-clockwise from east, and render y increases northward.
		if (!this.arrowTextureLoaded) {
			this.arrowTextureLoaded = true;
			try {
				this.arrowTexture = this.gameUI.loadTexture(PLAYER_ARROW_TEXTURE);
			}
			catch (final Exception exc) {
				this.arrowTexture = null;
			}
		}
		if (this.arrowTexture != null) {
			final float arrowSize = diameter * PLAYER_ARROW_SIZE;
			batch.draw(this.arrowTexture, centerX - (arrowSize / 2), centerY - (arrowSize / 2), arrowSize / 2,
					arrowSize / 2, arrowSize, arrowSize, 1f, 1f, pawn.getFacing() - 90f, 0, 0,
					this.arrowTexture.getWidth(), this.arrowTexture.getHeight(), false, false);
		}
	}

	@Override
	public void setupTable(final LuaTable table, final LuaEnvironment luaEnvironment,
			final UIFrameLuaWrapper luaWrapper) {
		super.setupTable(table, luaEnvironment, luaWrapper);
		table.set("GetZoom", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable) {
				return LuaValue.valueOf(MinimapFrame.this.zoom);
			}
		});
		table.set("GetZoomLevels", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable) {
				return LuaValue.valueOf(getZoomLevels());
			}
		});
		table.set("SetZoom", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue zoomArg) {
				setZoom(zoomArg.optint(DEFAULT_ZOOM));
				luaEnvironment.notifyMinimapZoomChanged();
				return LuaValue.NIL;
			}
		});
		table.set("GetPingPosition", new VarArgFunction() {
			@Override
			public Varargs invoke(final Varargs args) {
				return LuaValue.varargsOf(LuaValue.ZERO, LuaValue.ZERO);
			}
		});
		table.set("PingLocation", new VarArgFunction() {
			@Override
			public Varargs invoke(final Varargs args) {
				return LuaValue.NIL; // no party pings in single player
			}
		});
	}
}

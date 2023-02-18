package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame.ListBoxSelelectionListener;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class MapListBoxFrame extends ControlFrame implements ScrollBarFrame.ScrollBarChangeListener, ListBoxFrame.ListBoxSelelectionListener {
	private final float mapIconSize;

	private final List<MapItem> mapItems = new ArrayList<>();

	private final List<SingleStringFrame> mapNameFrames = new ArrayList<>();
	private final List<SingleStringFrame> mapPlayerCountFrames = new ArrayList<>();
	private final List<BackdropFrame> mapTypeFrames = new ArrayList<>();
	
	private BitmapFont frameFont;
	private float listBoxBorder;
	private int selectedIndex = -1;
	private int mouseOverIndex = -1;

	private final TextureFrame selectionFrame;
	private final TextureFrame mouseHighlightFrame;
	private GameUI gameUI;
	private Viewport viewport;
	private ScrollBarFrame scrollBarFrame;
	private ListBoxSelelectionListener selectionListener = ListBoxSelelectionListener.DO_NOTHING;

	private DataSource dataSource;

	public MapListBoxFrame(final String name, final UIFrame parent, final Viewport viewport, DataSource dataSource) {
		super(name, parent);
		mapIconSize = GameUI.convertY(viewport, 1/48f);
		this.listBoxBorder = GameUI.convertX(viewport, 0.01f);
		this.selectionFrame = new TextureFrame(null, this, false, null);
		this.mouseHighlightFrame = new TextureFrame(null, this, false, null);
		final Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(ListBoxFrame.SELECT_COLOR);
		pixmap.fill();
		this.selectionFrame.setTexture(new Texture(pixmap));
		final Pixmap mousePixmap = new Pixmap(1, 1, Format.RGBA8888);
		mousePixmap.setColor(ListBoxFrame.MOUSE_OVER_HIGHLIGHT_COLOR);
		mousePixmap.fill();
		this.mouseHighlightFrame.setTexture(new Texture(mousePixmap));

		this.dataSource = dataSource;
	}

	public void setScrollBarFrame(final ScrollBarFrame scrollBarFrame) {
		this.scrollBarFrame = scrollBarFrame;
		// TODO might be a better place to add these set points, but we definitely need
		// them
		scrollBarFrame.addSetPoint(
				new SetPoint(FramePoint.TOPRIGHT, this, FramePoint.TOPRIGHT, -this.listBoxBorder, -this.listBoxBorder));
		scrollBarFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, this, FramePoint.BOTTOMRIGHT,
				-this.listBoxBorder, this.listBoxBorder));
		scrollBarFrame.setChangeListener(this);
	}

	public ScrollBarFrame getScrollBarFrame() {
		return this.scrollBarFrame;
	}

	public void setListBoxBorder(final float listBoxBorder) {
		this.listBoxBorder = listBoxBorder;
	}

	public float getListBoxBorder() {
		return this.listBoxBorder;
	}

	public void setFrameFont(final BitmapFont frameFont) {
		this.frameFont = frameFont;
	}

	public BitmapFont getFrameFont() {
		return this.frameFont;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		this.gameUI = gameUI;
		this.viewport = viewport;
		super.innerPositionBounds(gameUI, viewport);
		updateUI(gameUI, viewport);
	}

	private void positionChildren(final GameUI gameUI, final Viewport viewport) {
		for (int i = 0; i < mapNameFrames.size(); i++) {
			mapTypeFrames.get(i).positionBounds(gameUI, viewport);
			mapPlayerCountFrames.get(i).positionBounds(gameUI, viewport);
			mapNameFrames.get(i).positionBounds(gameUI, viewport);
		}
		selectionFrame.positionBounds(gameUI, viewport);
		mouseHighlightFrame.positionBounds(gameUI, viewport);
		if (scrollBarFrame != null) {
			scrollBarFrame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		this.selectionFrame.render(batch, baseFont, glyphLayout);
		this.mouseHighlightFrame.render(batch, baseFont, glyphLayout);
		for (int i = 0; i < mapNameFrames.size(); i++) {
			mapNameFrames.get(i).render(batch, baseFont, glyphLayout);
			mapTypeFrames.get(i).render(batch, baseFont, glyphLayout);
			mapPlayerCountFrames.get(i).render(batch, baseFont, glyphLayout);
		}
		if (this.scrollBarFrame != null) {
			this.scrollBarFrame.render(batch, baseFont, glyphLayout);
		}
	}

	public void addItem(final String item, final GameUI gameUI, final Viewport viewport) {
		try {
			final War3Map map = War3MapViewer.beginLoadingMap(dataSource, item);
			final War3MapW3i mapInfo = map.readMapInformation();
			final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
			gameUI.setMapStrings(wtsFile);
			
			String mapName = gameUI.getTrigStr(mapInfo.getName());
			int playerCount = mapInfo.getPlayers().size();
			MapType type = mapInfo.hasFlag(War3MapW3iFlags.MELEE_MAP) ? MapType.MELEE_MAP : MapType.CUSTOM_MAP;
			mapItems.add(new MapItem(item, mapName, playerCount, type));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setItems(final List<String> items, final GameUI gameUI, final Viewport viewport) {
		mapItems.clear();

		for (String item : items) {
			try {
				final War3Map map = War3MapViewer.beginLoadingMap(dataSource, item);
				final War3MapW3i mapInfo = map.readMapInformation();
				final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
				gameUI.setMapStrings(wtsFile);
				
				String mapName = gameUI.getTrigStr(mapInfo.getName());
				int playerCount = mapInfo.getPlayers().size();
				MapType type = mapInfo.hasFlag(War3MapW3iFlags.MELEE_MAP) ? MapType.MELEE_MAP : MapType.CUSTOM_MAP;
				mapItems.add(new MapItem(item, mapName, playerCount, type));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void removeItem(final String item, final GameUI gameUI, final Viewport viewport) {
		int index = 0;
		while (index < mapItems.size()) {
			if (mapItems.get(index).mapFilename.equals(item)) {
				break;
			}
			index++;
		}
		if (index >= mapItems.size()) return;
		mapItems.remove(index);
	}

	public void removeItem(final int index, final GameUI gameUI, final Viewport viewport) {
		if (index >= mapItems.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		mapItems.remove(index);
	}

	public void removeAllItems() {
		mapItems.clear();
	}

	public void sortItems() {
		Collections.sort(mapItems, new Comparator<MapItem>() {
			@Override
			public int compare(MapItem arg0, MapItem arg1) {
				return MapItem.compare(arg0, arg1); 
			}
		});
	}

	public void setSelectedIndex(final int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public int getSelectedIndex() {
		return this.selectedIndex;
	}

	public String getSelectedItem() {
		if (this.selectedIndex < 0 || this.selectedIndex >= mapItems.size()) {
			return null;
		}
		return mapItems.get(selectedIndex).mapFilename;
	}

	private void updateUI(final GameUI gameUI, final Viewport viewport) {
		final float numStringSize = (float) Math.max(mapIconSize, frameFont.getLineHeight());
		AbstractRenderableFrame prev = null;
		boolean foundSelected = false;
		boolean foundMouseOver = false;
		final int numStringFrames = (int) Math.min(mapItems.size(),
			Math.floor((renderBounds.height - listBoxBorder * 2) / numStringSize));

		final int scrollOffset = computeScrollOffset(numStringFrames);
		if (numStringFrames != mapNameFrames.size()) {
			mapNameFrames.clear();
			mapPlayerCountFrames.clear();
			mapTypeFrames.clear();

			final BitmapFont refFont = ((StringFrame)gameUI.getFrameByName("MaxPlayersValue", 0)).getFrameFont();
			for (int stringFrameIndex = 0; stringFrameIndex < numStringFrames; stringFrameIndex++) {
				final int index = stringFrameIndex + scrollOffset;
				boolean selected = (index == selectedIndex);
				boolean mouseOver = (index == mouseOverIndex);

				final SingleStringFrame mapNameFrame = new SingleStringFrame("MapNameY_" + stringFrameIndex, this, Color.WHITE, TextJustify.LEFT, TextJustify.MIDDLE, frameFont);
				mapNameFrame.setWidth(this.renderBounds.getWidth() - 2 * listBoxBorder - mapIconSize);
				mapNameFrame.setHeight(frameFont.getLineHeight());
				
				final BackdropFrame mapTypeFrame = (BackdropFrame) gameUI.createFrameByType("BACKDROP", "MapTypeY_" + stringFrameIndex, this, "", 0);
				mapTypeFrame.setWidth(mapIconSize);
				mapTypeFrame.setHeight(mapIconSize);
				
				final SingleStringFrame mapPlayerCountFrame = new SingleStringFrame("MapPlayerCountY_" + stringFrameIndex, mapTypeFrame, Color.YELLOW, TextJustify.CENTER, TextJustify.MIDDLE, refFont);

				if (index < mapItems.size()) {
					mapNameFrame.setText(mapItems.get(index).mapName);

					if (mapItems.get(index).mapType == MapType.MELEE_MAP) {
						mapTypeFrame.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-file-melee.blp"));
					} else if (mapItems.get(index).mapType == MapType.CUSTOM_MAP) {
						mapTypeFrame.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-file-ums.blp"));
					}

					mapPlayerCountFrame.setText(Integer.toString(mapItems.get(index).mapPlayerCount));
				}
				if (prev != null) {
					mapTypeFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, prev, FramePoint.BOTTOMLEFT, 0, 0));
				} else {
					mapTypeFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, listBoxBorder, -listBoxBorder));
				}
				mapNameFrame.addSetPoint(new SetPoint(FramePoint.LEFT, mapTypeFrame, FramePoint.RIGHT, 0, 0));
				mapPlayerCountFrame.addSetPoint(new SetPoint(FramePoint.CENTER, mapTypeFrame, FramePoint.CENTER, 0, 0));

				mapNameFrames.add(mapNameFrame);
				mapTypeFrames.add(mapTypeFrame);
				mapPlayerCountFrames.add(mapPlayerCountFrame);
				prev = mapTypeFrame;

				if (selected) {
					selectionFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapTypeFrame, FramePoint.TOPLEFT, 0, 0));
					selectionFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, mapNameFrame, FramePoint.BOTTOMRIGHT, 0, 0));
					foundSelected = true;
				} else if (mouseOver) {
					mouseHighlightFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapTypeFrame, FramePoint.TOPLEFT, 0, 0));
					mouseHighlightFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, mapNameFrame, FramePoint.BOTTOMRIGHT, 0, 0));
					foundMouseOver = true;
				}
			}
		} else {
			for (int stringFrameIndex = 0; stringFrameIndex < numStringFrames; stringFrameIndex++) {
				final int index = stringFrameIndex + scrollOffset;
				boolean selected = (index == selectedIndex);
				boolean mouseOver = (index == mouseOverIndex);

				SingleStringFrame mapNameFrame = mapNameFrames.get(stringFrameIndex);
				SingleStringFrame mapPlayerCountFrame = mapPlayerCountFrames.get(stringFrameIndex);
				BackdropFrame mapTypeFrame = mapTypeFrames.get(stringFrameIndex);

				if (index < mapItems.size()) {
					mapNameFrame.setText(mapItems.get(index).mapName);
					
					if (mapItems.get(index).mapType == MapType.MELEE_MAP) {
						mapTypeFrame.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-file-melee.blp"));
					} else if (mapItems.get(index).mapType == MapType.CUSTOM_MAP) {
						mapTypeFrame.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-file-ums.blp"));
					}

					mapPlayerCountFrame.setText(Integer.toString(mapItems.get(index).mapPlayerCount));
				}

				if (selected) {
					selectionFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapTypeFrame, FramePoint.TOPLEFT, 0, 0));
					selectionFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, mapNameFrame, FramePoint.BOTTOMRIGHT, 0, 0));
					foundSelected = true;
				} else if (mouseOver) {
					mouseHighlightFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapTypeFrame, FramePoint.TOPLEFT, 0, 0));
					mouseHighlightFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, mapNameFrame, FramePoint.BOTTOMRIGHT, 0, 0));
					foundMouseOver = true;
				}
			}
		}
		this.selectionFrame.setVisible(foundSelected);
		this.mouseHighlightFrame.setVisible(foundMouseOver);
		positionChildren(gameUI, viewport);
	}

	protected int computeScrollOffset(final int numStringFrames) {
		int scrollOffset;
		if ((this.scrollBarFrame != null) && (mapItems.size() > numStringFrames)) {
			scrollOffset = (int) Math
					.ceil(((100 - this.scrollBarFrame.getValue()) / 100f) * (mapItems.size() - numStringFrames));
		} else {
			scrollOffset = 0;
		}
		return scrollOffset;
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			if (this.scrollBarFrame != null) {
				final UIFrame sliderFrameChildUnderMouse = this.scrollBarFrame.touchDown(screenX, screenY, button);
				if (sliderFrameChildUnderMouse != null) {
					return sliderFrameChildUnderMouse;
				}
			}
			int index = 0;
			for (int i = 0; i < mapNameFrames.size(); i++) {
				Rectangle mapNameFrameRect = mapNameFrames.get(i).getRenderBounds();
				Rectangle mapTypeFrameRect = mapTypeFrames.get(i).getRenderBounds();
				if (mapNameFrameRect.contains(screenX, screenY) || mapTypeFrameRect.contains(screenX, screenY)) {
					selectedIndex = index + computeScrollOffset(mapNameFrames.size());
					break;
				}
				index++;
			}
			updateUI(this.gameUI, this.viewport);
			this.selectionListener.onSelectionChanged(this.selectedIndex, getSelectedItem());
			return this;
		}
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			if (this.scrollBarFrame != null) {
				final UIFrame sliderFrameChildUnderMouse = this.scrollBarFrame.touchDown(screenX, screenY, button);
				if (sliderFrameChildUnderMouse != null) {
					return sliderFrameChildUnderMouse;
				}
			}
		}
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			if (this.scrollBarFrame != null) {
				final UIFrame sliderFrameChildUnderMouse = this.scrollBarFrame.getFrameChildUnderMouse(screenX,
						screenY);
				if (sliderFrameChildUnderMouse != null) {
					return sliderFrameChildUnderMouse;
				}
			}
			int index = 0;
			int mouseOverIndex = -1;
			for (int i = 0; i < mapNameFrames.size(); i++) {
				Rectangle mapNameFrameRect = mapNameFrames.get(i).getRenderBounds();
				Rectangle mapTypeFrameRect = mapTypeFrames.get(i).getRenderBounds();
				if (mapNameFrameRect.contains(screenX, screenY) || mapTypeFrameRect.contains(screenX, screenY)) {
					mouseOverIndex = index;
					break;
				}
				index++;
			}
			if (this.mouseOverIndex != mouseOverIndex) {
				this.mouseOverIndex = mouseOverIndex + computeScrollOffset(mapNameFrames.size());
				updateUI(this.gameUI, this.viewport);
			}
		}
		return super.getFrameChildUnderMouse(screenX, screenY);
	}

	public void setSelectionListener(final ListBoxSelelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	@Override
	public void onChange(final GameUI gameUI, final Viewport uiViewport, final int newValue) {
		updateUI(gameUI, uiViewport);
	}

	private class MapItem {
		public String mapFilename = null;
		public String mapName = null;
		public int mapPlayerCount = 0;
		public MapType mapType = null;

		public MapItem(String filename, String name, int playerCount, MapType type) {
			mapFilename = filename;
			mapName = name;
			mapPlayerCount = playerCount;
			mapType = type;
		}

		public static int compare(MapItem map1, MapItem map2) {
			if (map1.mapPlayerCount == map2.mapPlayerCount) {
				return map1.mapName.compareTo(map2.mapName);
			} else {
				return Integer.compare(map1.mapPlayerCount, map2.mapPlayerCount);
			}
		}
	}

	public enum MapType {
		MELEE_MAP,
		CUSTOM_MAP,
		FOLDER_MAP,
		FOLDERBACK_MAP
	}

	@Override
	public void onSelectionChanged(int newSelectedIndex, String newSelectedItem){}
}

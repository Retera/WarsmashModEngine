package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumMap;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.LuaEnvironment;
import com.etheller.warsmash.parsers.fdf.ThirdPersonLuaXmlClick;
import com.etheller.warsmash.parsers.fdf.ThirdPersonLuaXmlEvent;
import com.etheller.warsmash.parsers.fdf.UIFrameLuaWrapper;
import com.etheller.warsmash.parsers.fdf.UIFrameScripts;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.lua.SixArgFunction;

public abstract class AbstractRenderableFrame implements UIFrame {
	private static final FramePoint[] LEFT_ANCHOR_PRIORITY = { FramePoint.LEFT, FramePoint.TOPLEFT,
			FramePoint.BOTTOMLEFT };
	private static final FramePoint[] RIGHT_ANCHOR_PRIORITY = { FramePoint.RIGHT, FramePoint.TOPRIGHT,
			FramePoint.BOTTOMRIGHT };
	private static final FramePoint[] CENTER_HORIZ_ANCHOR_PRIORITY = { FramePoint.CENTER, FramePoint.TOP,
			FramePoint.BOTTOM };
	private static final FramePoint[] CENTER_VERT_ANCHOR_PRIORITY = { FramePoint.CENTER, FramePoint.LEFT,
			FramePoint.RIGHT };
	private static final FramePoint[] TOP_ANCHOR_PRIORITY = { FramePoint.TOP, FramePoint.TOPLEFT, FramePoint.TOPRIGHT };
	private static final FramePoint[] BOTTOM_ANCHOR_PRIORITY = { FramePoint.BOTTOM, FramePoint.BOTTOMLEFT,
			FramePoint.BOTTOMRIGHT };
	private static final boolean DEBUG_LOG = false;
	protected String name;
	protected UIFrame parent;
	protected boolean visible = true;
	protected int level;
	protected final Rectangle renderBounds = new Rectangle(0, 0, 0, 0); // in libgdx rendering space
	private final EnumMap<FramePoint, FramePointAssignment> framePointToAssignment = new EnumMap<>(FramePoint.class);
	protected float assignedHeight;
	protected float assignedWidth;
	private UIFrameScripts scripts;
	private int id;

	public AbstractRenderableFrame(final String name, final UIFrame parent) {
		this.name = name;
		this.parent = parent;
	}

	@Override
	public void setSetAllPoints(final boolean setAllPoints) {
		if (!this.framePointToAssignment.containsKey(FramePoint.CENTER)) {
			for (final FramePoint framePoint : FramePoint.values()) {
				if (!this.framePointToAssignment.containsKey(framePoint)) {
					this.framePointToAssignment.put(framePoint,
							new SetPoint(framePoint, this.parent, framePoint, 0, 0));
				}
			}
		}
	}

	@Override
	public void setSetAllPoints(final boolean setAllPoints, final float inset) {
		this.framePointToAssignment.put(FramePoint.TOPLEFT,
				new SetPoint(FramePoint.TOPLEFT, this.parent, FramePoint.TOPLEFT, inset, -inset));
		this.framePointToAssignment.put(FramePoint.LEFT,
				new SetPoint(FramePoint.LEFT, this.parent, FramePoint.LEFT, inset, 0));
		this.framePointToAssignment.put(FramePoint.BOTTOMLEFT,
				new SetPoint(FramePoint.BOTTOMLEFT, this.parent, FramePoint.BOTTOMLEFT, inset, inset));
		this.framePointToAssignment.put(FramePoint.BOTTOM,
				new SetPoint(FramePoint.BOTTOM, this.parent, FramePoint.BOTTOM, 0, inset));
		this.framePointToAssignment.put(FramePoint.BOTTOMRIGHT,
				new SetPoint(FramePoint.BOTTOMRIGHT, this.parent, FramePoint.BOTTOMRIGHT, -inset, inset));
		this.framePointToAssignment.put(FramePoint.RIGHT,
				new SetPoint(FramePoint.RIGHT, this.parent, FramePoint.RIGHT, -inset, 0));
		this.framePointToAssignment.put(FramePoint.TOPRIGHT,
				new SetPoint(FramePoint.TOPRIGHT, this.parent, FramePoint.TOPRIGHT, -inset, -inset));
		this.framePointToAssignment.put(FramePoint.TOP,
				new SetPoint(FramePoint.TOP, this.parent, FramePoint.TOP, 0, -inset));
		this.framePointToAssignment.put(FramePoint.CENTER,
				new SetPoint(FramePoint.CENTER, this.parent, FramePoint.CENTER, 0, 0));
	}

	@Override
	public void setWidth(final float width) {
		this.assignedWidth = width;
		this.renderBounds.width = width;
	}

	@Override
	public float getAssignedWidth() {
		return this.assignedWidth;
	}

	@Override
	public float getAssignedHeight() {
		return this.assignedHeight;
	}

	@Override
	public void setHeight(final float height) {
		this.assignedHeight = height;
		this.renderBounds.height = height;
	}

	private FramePointAssignment getByPriority(final FramePoint[] priorities) {
		for (final FramePoint priorityFramePoint : priorities) {
			final FramePointAssignment framePointAssignment = this.framePointToAssignment.get(priorityFramePoint);
			if (framePointAssignment != null) {
				return framePointAssignment;
			}
		}
		return null;
	}

	public void clearFramePointAssignments() {
		this.framePointToAssignment.clear();
	}

	private FramePointAssignment getLeftAnchor() {
		return getByPriority(LEFT_ANCHOR_PRIORITY);
	}

	private FramePointAssignment getRightAnchor() {
		return getByPriority(RIGHT_ANCHOR_PRIORITY);
	}

	private FramePointAssignment getTopAnchor() {
		return getByPriority(TOP_ANCHOR_PRIORITY);
	}

	private FramePointAssignment getBottomAnchor() {
		return getByPriority(BOTTOM_ANCHOR_PRIORITY);
	}

	private FramePointAssignment getCenterHorizontalAnchor() {
		return getByPriority(CENTER_HORIZ_ANCHOR_PRIORITY);
	}

	private FramePointAssignment getCenterVerticalAnchor() {
		return getByPriority(CENTER_VERT_ANCHOR_PRIORITY);
	}

	@Override
	public float getFramePointX(final FramePoint framePoint) {
		switch (framePoint) {
		case CENTER:
		case BOTTOM:
		case TOP:
			return this.renderBounds.x + (this.renderBounds.width / 2);
		case BOTTOMLEFT:
		case LEFT:
		case TOPLEFT:
			return this.renderBounds.x;
		case BOTTOMRIGHT:
		case RIGHT:
		case TOPRIGHT:
			return this.renderBounds.x + this.renderBounds.width;
		default:
			return 0;
		}
	}

	@Override
	public void setFramePointX(final FramePoint framePoint, final float x) {
		if (this.renderBounds.width == 0) {
			this.renderBounds.x = x;
			return;
		}
		switch (framePoint) {
		case CENTER:
		case BOTTOM:
		case TOP:
			this.renderBounds.x = x - (this.renderBounds.width / 2);
			return;
		case BOTTOMLEFT:
		case LEFT:
		case TOPLEFT:
			if (getRightAnchor() != null) {
				final float oldRightX = this.renderBounds.x + this.renderBounds.width;
				this.renderBounds.x = x;
				this.renderBounds.width = oldRightX - x;
			}
			else {
				// no right anchor, keep width
				this.renderBounds.x = x;
			}
			return;
		case BOTTOMRIGHT:
		case RIGHT:
		case TOPRIGHT:
			if (getLeftAnchor() != null) {
				this.renderBounds.width = x - this.renderBounds.x;
			}
			else {
				this.renderBounds.x = x - this.renderBounds.width;
			}
			return;
		default:
			return;
		}
	}

	@Override
	public float getFramePointY(final FramePoint framePoint) {
		switch (framePoint) {
		case LEFT:
		case CENTER:
		case RIGHT:
			return this.renderBounds.y + (this.renderBounds.height / 2);
		case BOTTOMLEFT:
		case BOTTOM:
		case BOTTOMRIGHT:
			return this.renderBounds.y;
		case TOPLEFT:
		case TOP:
		case TOPRIGHT:
			return this.renderBounds.y + this.renderBounds.height;
		default:
			return 0;
		}
	}

	@Override
	public void setFramePointY(final FramePoint framePoint, final float y) {
		if (this.renderBounds.height == 0) {
			this.renderBounds.y = y;
			return;
		}
		switch (framePoint) {
		case LEFT:
		case CENTER:
		case RIGHT:
			this.renderBounds.y = y - (this.renderBounds.height / 2);
			return;
		case TOPLEFT:
		case TOP:
		case TOPRIGHT:
			if (getBottomAnchor() != null) {
				this.renderBounds.height = y - this.renderBounds.y;
			}
			else {
				this.renderBounds.y = y - this.renderBounds.height;
			}
			return;
		case BOTTOMLEFT:
		case BOTTOM:
		case BOTTOMRIGHT:
			if (getTopAnchor() != null) {
				final float oldBottomY = this.renderBounds.y + this.renderBounds.height;
				this.renderBounds.y = y;
				this.renderBounds.height = oldBottomY - y;
			}
			else {
				this.renderBounds.y = y;
			}
			return;
		default:
			return;
		}
	}

	@Override
	public void addAnchor(final AnchorDefinition anchorDefinition) {
		this.framePointToAssignment.put(anchorDefinition.getMyPoint(), new SetPoint(anchorDefinition.getMyPoint(),
				this.parent, anchorDefinition.getRelativePoint(), anchorDefinition.getX(), anchorDefinition.getY()));
	}

	@Override
	public void addSetPoint(final SetPoint setPointDefinition) {
		this.framePointToAssignment.put(setPointDefinition.getMyPoint(), setPointDefinition);
	}

	@Override
	public void positionBounds(final GameUI gameUI, final Viewport viewport) {
		if ("SpellbookMicroButton".equals(this.name)) {
			System.out.println(1);
		}
		if (this.parent == null) {
			// TODO this is a bit of a hack, remove later
			return;
		}
		if (this.framePointToAssignment.isEmpty()) {
			this.renderBounds.x = this.parent.getFramePointX(FramePoint.LEFT);
			this.renderBounds.y = this.parent.getFramePointY(FramePoint.BOTTOM);
		}
		else {
			final FramePointAssignment leftAnchor = getLeftAnchor();
			final FramePointAssignment rightAnchor = getRightAnchor();
			final FramePointAssignment topAnchor = getTopAnchor();
			final FramePointAssignment bottomAnchor = getBottomAnchor();
			final FramePointAssignment centerHorizontalAnchor = getCenterHorizontalAnchor();
			final FramePointAssignment centerVerticalAnchor = getCenterVerticalAnchor();
			if (leftAnchor != null) {
				this.renderBounds.x = leftAnchor.getX(gameUI, viewport);
				if (this.assignedWidth == 0) {
					if (rightAnchor != null) {
						this.renderBounds.width = rightAnchor.getX(gameUI, viewport) - this.renderBounds.x;
					}
					else if (centerHorizontalAnchor != null) {
						this.renderBounds.width = (centerHorizontalAnchor.getX(gameUI, viewport) - this.renderBounds.x)
								* 2;
					}
				}
			}
			else if (rightAnchor != null) {
				this.renderBounds.x = rightAnchor.getX(gameUI, viewport) - this.renderBounds.width;
				if (centerHorizontalAnchor != null) {
					this.renderBounds.width = (this.renderBounds.x - centerHorizontalAnchor.getX(gameUI, viewport)) * 2;
				}
			}
			else if (centerHorizontalAnchor != null) {
				this.renderBounds.x = centerHorizontalAnchor.getX(gameUI, viewport) - (this.renderBounds.width / 2);
			}
			if (bottomAnchor != null) {
				this.renderBounds.y = bottomAnchor.getY(gameUI, viewport);
				if (this.assignedHeight == 0) {
					if (topAnchor != null) {
						this.renderBounds.height = topAnchor.getY(gameUI, viewport) - this.renderBounds.y;
					}
					else if (centerVerticalAnchor != null) {
						this.renderBounds.height = (centerVerticalAnchor.getY(gameUI, viewport) - this.renderBounds.y)
								* 2;
					}
				}
			}
			else if (topAnchor != null) {
				this.renderBounds.y = topAnchor.getY(gameUI, viewport) - this.renderBounds.height;
				if (centerVerticalAnchor != null) {
					this.renderBounds.height = (this.renderBounds.y - centerVerticalAnchor.getY(gameUI, viewport)) * 2;
				}
			}
			else if (centerVerticalAnchor != null) {
				this.renderBounds.y = centerVerticalAnchor.getY(gameUI, viewport) - (this.renderBounds.height / 2);
			}
		}
		if (DEBUG_LOG) {
			System.out.println(getClass().getSimpleName() + ":" + this.name + ":" + hashCode()
					+ " finishing position bounds: " + this.renderBounds);
		}
		innerPositionBounds(gameUI, viewport);
	}

	protected abstract void innerPositionBounds(GameUI gameUI, final Viewport viewport);

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	@Override
	public final void render(final SpriteBatch batch, final BitmapFont font20, final GlyphLayout glyphLayout) {
		if (this.visible) {
			internalRender(batch, font20, glyphLayout);
		}
	}

	@Override
	public UIFrame getParent() {
		return this.parent;
	}

	@Override
	public void setParent(final UIFrame parent) {
		this.parent = parent;
	}

	@Override
	public boolean isVisibleOnScreen() {
		boolean visibleOnScreen = this.visible;
		UIFrame ancestor = this.parent;
		while (visibleOnScreen && (ancestor != null)) {
			visibleOnScreen &= ancestor.isVisible();
			ancestor = ancestor.getParent();
		}
		return visibleOnScreen;
	}

	protected abstract void internalRender(SpriteBatch batch, BitmapFont baseFont, GlyphLayout glyphLayout);

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		return null;
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		return null;
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public Rectangle getRenderBounds() {
		return this.renderBounds;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public void setID(final int x) {
		this.id = x;
	}

	@Override
	public UIFrameScripts getScripts() {
		return this.scripts;
	}

	@Override
	public void setScripts(final UIFrameScripts scripts) {
		this.scripts = scripts;
	}

	@Override
	public void setupTable(final LuaTable table, final LuaEnvironment luaEnvironment,
			final UIFrameLuaWrapper luaWrapper) {
		table.set("RegisterEvent", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg) {
				final ThirdPersonLuaXmlEvent eventToRegister = ThirdPersonLuaXmlEvent.valueOf(arg.checkjstring());
				luaEnvironment.registerEvent(eventToRegister, luaWrapper);
				return LuaValue.NIL;
			}
		});
		table.set("RegisterForClicks", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue args) {
				final int narg = args.narg();
				for (int i = 1; i <= narg; i++) {
					final String argString = args.tojstring(i);
					final ThirdPersonLuaXmlClick click = ThirdPersonLuaXmlClick.valueOf(argString);
					luaEnvironment.registerForClick(click, luaWrapper);
				}
				return LuaValue.NIL;
			}
		});
		table.set("GetName", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(getName());
			}
		});
		table.set("Hide", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				setVisible(false);
				return LuaValue.NIL;
			}
		});
		table.set("Show", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				setVisible(true);
				return LuaValue.NIL;
			}
		});
		table.set("SetHeight", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg) {
				setHeight(GameUI.convertXMLCoordY((float) arg.checkdouble()));
				// TODO positionBounds
				return LuaValue.NIL;
			}
		});
		table.set("SetWidth", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg) {
				setWidth(GameUI.convertXMLCoordX((float) arg.checkdouble()));
				// TODO positionBounds
				return LuaValue.NIL;
			}
		});
		table.set("GetWidth", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(GameUI.unconvertXMLCoordX(getAssignedWidth()));
//				return LuaValue.valueOf(GameUI.unconvertXMLCoordX(getRenderBounds().getWidth()));
			}
		});
		table.set("GetHeight", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(GameUI.unconvertXMLCoordY(getAssignedHeight()));
//				return LuaValue.valueOf(GameUI.unconvertXMLCoordY(getRenderBounds().getHeight()));
			}
		});
		table.set("IsShown", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(isVisible());
			}
		});
		table.set("IsVisible", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(isVisible());
			}
		});
		table.set("SetPoint", new SixArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue myPoint, final LuaValue otherName,
					final LuaValue otherPoint, final LuaValue xValue, final LuaValue yValue) {
				final UIFrame other = luaEnvironment.getRootFrame().getFrameByName(otherName.checkjstring(), 0);
				addSetPoint(new SetPoint(FramePoint.valueOf(myPoint.checkjstring()), other,
						FramePoint.valueOf(otherPoint.checkjstring()),
						GameUI.convertXMLCoordX((float) xValue.checkdouble()),
						GameUI.convertXMLCoordY((float) yValue.checkdouble())));
				positionBounds(luaEnvironment.getRootFrame(), luaEnvironment.getUiViewport());
				// TODO positionBounds
				return LuaValue.NIL;
			}
		});
		table.set("SetFrameLevel", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg) {
				setLevel(arg.checkint()); // TODO doubt this currently actually does anything
				return LuaValue.NIL;
			}
		});
		table.set("GetFrameLevel", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(getLevel());
			}
		});
		table.set("GetID", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(getID());
			}
		});
		table.set("SetID", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg) {
				setID(arg.checkint());
				return LuaValue.NIL;
			}
		});
		table.set("GetParent", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return AbstractRenderableFrame.this.parent.getScripts().getLuaWrapper().getTable();
			}
		});
	}
}

package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;

public abstract class AbstractRenderableFrame implements UIFrame {
	private static final boolean DEBUG_LOG = true;
	protected String name;
	protected UIFrame parent;
	protected boolean visible = true;
	protected int level;
	protected final Rectangle renderBounds = new Rectangle(0, 0, 0, 0); // in libgdx rendering space
	protected List<AnchorDefinition> anchors = new ArrayList<>();
	protected List<SetPoint> setPoints = new ArrayList<>();
	private boolean setAllPoints;

	public AbstractRenderableFrame(final String name, final UIFrame parent) {
		this.name = name;
		this.parent = parent;
	}

	@Override
	public void setSetAllPoints(final boolean setAllPoints) {
		this.setAllPoints = setAllPoints;
	}

	@Override
	public void setWidth(final float width) {
		this.renderBounds.width = width;
	}

	@Override
	public void setHeight(final float height) {
		this.renderBounds.height = height;
	}

	private boolean hasLeftAnchor() {
		for (final AnchorDefinition anchor : this.anchors) {
			switch (anchor.getMyPoint()) {
			case CENTER:
			case BOTTOM:
			case TOP:
			case BOTTOMRIGHT:
			case RIGHT:
			case TOPRIGHT:
				break;
			case BOTTOMLEFT:
			case LEFT:
			case TOPLEFT:
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private boolean hasRightAnchor() {
		for (final AnchorDefinition anchor : this.anchors) {
			switch (anchor.getMyPoint()) {
			case CENTER:
			case BOTTOM:
			case TOP:
			case BOTTOMLEFT:
			case LEFT:
			case TOPLEFT:
				break;
			case BOTTOMRIGHT:
			case RIGHT:
			case TOPRIGHT:
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private boolean hasTopAnchor() {
		for (final AnchorDefinition anchor : this.anchors) {
			switch (anchor.getMyPoint()) {
			case CENTER:
			case BOTTOM:
			case BOTTOMLEFT:
			case LEFT:
			case BOTTOMRIGHT:
			case RIGHT:
				break;
			case TOP:
			case TOPLEFT:
			case TOPRIGHT:
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private boolean hasBottomAnchor() {
		for (final AnchorDefinition anchor : this.anchors) {
			switch (anchor.getMyPoint()) {
			case CENTER:
			case LEFT:
			case RIGHT:
			case TOP:
			case TOPLEFT:
			case TOPRIGHT:
				break;
			case BOTTOM:
			case BOTTOMLEFT:
			case BOTTOMRIGHT:
				return true;
			default:
				break;
			}
		}
		return false;
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
			if (hasRightAnchor()) {
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
			if (hasLeftAnchor()) {
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
			if (hasBottomAnchor()) {
				this.renderBounds.height = y - this.renderBounds.y;
			}
			else {
				this.renderBounds.y = y - this.renderBounds.height;
			}
			return;
		case BOTTOMLEFT:
		case BOTTOM:
		case BOTTOMRIGHT:
			if (hasTopAnchor()) {
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
		this.anchors.add(anchorDefinition);
	}

	@Override
	public void addSetPoint(final SetPoint setPointDefinition) {
		// TODO this is O(N) in the number of SetPoints, and that
		// is not good performance.
		final Iterator<SetPoint> iterator = this.setPoints.iterator();
		while (iterator.hasNext()) {
			final SetPoint setPoint = iterator.next();
			if (setPoint.getMyPoint() == setPointDefinition.getMyPoint()) {
				iterator.remove();
			}
		}
		this.setPoints.add(setPointDefinition);
	}

	@Override
	public void positionBounds(final Viewport viewport) {
		if (this.parent == null) {
			// TODO this is a bit of a hack, remove later
			return;
		}
		if (this.anchors.isEmpty() && this.setPoints.isEmpty()) {
			this.renderBounds.x = this.parent.getFramePointX(FramePoint.LEFT);
			this.renderBounds.y = this.parent.getFramePointY(FramePoint.BOTTOM);
		}
		else {
			for (final AnchorDefinition anchor : this.anchors) {
				final float parentPointX = this.parent.getFramePointX(anchor.getMyPoint());
				final float parentPointY = this.parent.getFramePointY(anchor.getMyPoint());
				setFramePointX(anchor.getMyPoint(), parentPointX + anchor.getX());
				setFramePointY(anchor.getMyPoint(), parentPointY + anchor.getY());
				if (DEBUG_LOG) {
					System.out.println(getClass().getSimpleName() + ":" + this.name + " anchoring to: " + anchor);
				}
			}
			for (final SetPoint setPoint : this.setPoints) {
				final UIFrame other = setPoint.getOther();
				if (other == null) {
					continue;
				}
				final float parentPointX = other.getFramePointX(setPoint.getOtherPoint());
				final float parentPointY = other.getFramePointY(setPoint.getOtherPoint());
				setFramePointX(setPoint.getMyPoint(), parentPointX + setPoint.getX());
				setFramePointY(setPoint.getMyPoint(), parentPointY + setPoint.getY());
			}
		}
		if (this.setAllPoints) {
			if (this.renderBounds.width == 0) {
				this.renderBounds.width = this.parent.getFramePointX(FramePoint.RIGHT)
						- this.parent.getFramePointX(FramePoint.LEFT);
			}
			if (this.renderBounds.height == 0) {
				this.renderBounds.height = this.parent.getFramePointY(FramePoint.TOP)
						- this.parent.getFramePointY(FramePoint.BOTTOM);
			}
		}
		if (DEBUG_LOG) {
			System.out.println(getClass().getSimpleName() + ":" + this.name + ":" + hashCode()
					+ " finishing position bounds: " + this.renderBounds);
		}
		innerPositionBounds(viewport);
	}

	protected abstract void innerPositionBounds(final Viewport viewport);

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
	public final void render(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		if (this.visible) {
			internalRender(batch, baseFont, glyphLayout);
		}
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
	public String getName() {
		return this.name;
	}

}

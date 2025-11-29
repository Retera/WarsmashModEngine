package com.etheller.warsmash.parsers.fdf.frames;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.LuaEnvironment;
import com.etheller.warsmash.parsers.fdf.UIFrameLuaWrapper;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.lua.FourArgFunction;

public class SimpleStatusBarFrame extends AbstractUIFrame {
	private final boolean decorateFileNames;
	private final TextureFrame barFrame;
	private final TextureFrame borderFrame;
	private final float barInset;
	private float lastValue = Float.NaN;
	private float luaMin, luaMax;

	public SimpleStatusBarFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final boolean borderBelow, final float barInset) {
		super(name, parent);
		this.decorateFileNames = decorateFileNames;
		this.barInset = barInset;
		this.barFrame = new TextureFrame(name + "Bar", this, decorateFileNames, new Vector4Definition(0, 1, 0, 1));
		this.borderFrame = new TextureFrame(name + "Border", this, decorateFileNames,
				new Vector4Definition(0, 1, 0, 1));
		this.borderFrame.setSetAllPoints(true);
		this.barFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, barInset, -barInset));
		this.barFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMLEFT, this, FramePoint.BOTTOMLEFT, barInset, barInset));
		this.barFrame.setSetAllPoints(true, barInset);
		if (borderBelow) {
			add(this.borderFrame);
			add(this.barFrame);
		}
		else {
			add(this.barFrame);
			add(this.borderFrame);
		}
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		if (!Float.isNaN(this.lastValue)) {
			this.barFrame.setWidth(((this.renderBounds.width - (this.barInset * 2)) * this.lastValue));
		}
		super.innerPositionBounds(gameUI, viewport);
	}

	public boolean isDecorateFileNames() {
		return this.decorateFileNames;
	}

	public void setValue(final float value) {
		this.barFrame.setTexCoord(0, value, 0, 1);
		this.barFrame.setWidth(((this.renderBounds.width - (this.barInset * 2)) * value));
		this.lastValue = value;
	}

	public TextureFrame getBarFrame() {
		return this.barFrame;
	}

	public TextureFrame getBorderFrame() {
		return this.borderFrame;
	}

	@Override
	public void setupTable(final LuaTable table, final LuaEnvironment luaEnvironment,
			final UIFrameLuaWrapper luaWrapper) {
		super.setupTable(table, luaEnvironment, luaWrapper);
		table.set("SetMinMaxValues", new ThreeArgFunction() {
			@Override
			public LuaValue call(final LuaValue thisTable, final LuaValue minValue, final LuaValue maxValue) {
				SimpleStatusBarFrame.this.luaMin = minValue.tofloat();
				SimpleStatusBarFrame.this.luaMax = maxValue.tofloat();
				return null;
			}
		});
		table.set("SetValue", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thisTable, final LuaValue currValue) {
				setValue((currValue.tofloat() - SimpleStatusBarFrame.this.luaMin)
						/ (SimpleStatusBarFrame.this.luaMax - SimpleStatusBarFrame.this.luaMin));
				return null;
			}
		});
		table.set("SetStatusBarColor", new FourArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg, final LuaValue arg2,
					final LuaValue arg3) {
				final Color prevColor = SimpleStatusBarFrame.this.barFrame.getColor();
				SimpleStatusBarFrame.this.barFrame.setColor(arg.tofloat(), arg2.tofloat(), arg3.tofloat(),
						prevColor == null ? 1.0f : prevColor.a);
				return LuaValue.NIL;
			}
		});
	}
}

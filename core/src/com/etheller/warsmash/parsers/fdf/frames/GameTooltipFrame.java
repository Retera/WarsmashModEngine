package com.etheller.warsmash.parsers.fdf.frames;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import com.etheller.warsmash.parsers.fdf.LuaEnvironment;
import com.etheller.warsmash.parsers.fdf.UIFrameLuaWrapper;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class GameTooltipFrame extends SimpleFrame {
	private StringFrame text;

	public GameTooltipFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	@Override
	public void add(final UIFrame childFrame) {
		super.add(childFrame);
		if ("GameTooltipText".equals(childFrame.getName())) {
			this.text = (StringFrame) childFrame;
		}
	}

	@Override
	public void setupTable(final LuaTable table, final LuaEnvironment luaEnvironment,
			final UIFrameLuaWrapper luaWrapper) {
		super.setupTable(table, luaEnvironment, luaWrapper);
		table.set("SetUnit", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue unitKey) {
				final CUnit unit = luaEnvironment.getUnit(unitKey.checkjstring());
				luaEnvironment.getRootFrame().setText(GameTooltipFrame.this.text, luaEnvironment.getUnitName(unit));
				return LuaValue.NIL;
			}
		});
	}
}

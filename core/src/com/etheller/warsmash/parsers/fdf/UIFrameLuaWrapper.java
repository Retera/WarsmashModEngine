package com.etheller.warsmash.parsers.fdf;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public class UIFrameLuaWrapper extends TwoArgFunction {
	private final UIFrame frame;
	private final LuaTable table;

	public UIFrameLuaWrapper(final UIFrame frame, final LuaEnvironment luaEnvironment) {
		this.frame = frame;
		final LuaTable uiFrameTable = new LuaTable();
		this.frame.setupTable(uiFrameTable, luaEnvironment, this);
		this.table = uiFrameTable;
	}

	@Override
	public LuaValue call(final LuaValue libname, final LuaValue env) {
		final Globals globals = env.checkglobals();
		globals.set("this", this.table);
		return this.table;
	}

	public LuaTable getTable() {
		return this.table;
	}

}

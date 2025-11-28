package com.etheller.warsmash.parsers.fdf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public class UIFrameLuaWrapper extends TwoArgFunction {
	public static final String WS_THIS = "wsthis";
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
		this.table.set(WS_THIS, this);
		return this.table;
	}

	public LuaTable getTable() {
		return this.table;
	}

	public UIFrame getFrame() {
		return this.frame;
	}

	private static final class DumbTable extends LuaTable {
		private static final String[] CAPTURE_KEYS = { "bookType", "spell", "pet", "ability" };
		private static final Set<String> CAPTURE_KEY_SET = new HashSet<>(Arrays.asList(CAPTURE_KEYS));

		public DumbTable() {
			super();
			// TODO Auto-generated constructor stub
		}

		public DumbTable(final int narray, final int nhash) {
			super(narray, nhash);
			// TODO Auto-generated constructor stub
		}

		public DumbTable(final LuaValue[] named, final LuaValue[] unnamed, final Varargs lastarg) {
			super(named, unnamed, lastarg);
			// TODO Auto-generated constructor stub
		}

		public DumbTable(final Varargs varargs, final int firstarg) {
			super(varargs, firstarg);
			// TODO Auto-generated constructor stub
		}

		public DumbTable(final Varargs varargs) {
			super(varargs);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void set(final LuaValue key, final LuaValue value) {
			if (CAPTURE_KEY_SET.contains(key.toString())) {
				System.err.println(toString() + ":" + "set(" + key + ") = " + value);
			}
			super.set(key, value);
		}

		@Override
		public LuaValue get(final String key) {
			final LuaValue result = super.get(key);
			if (CAPTURE_KEY_SET.contains(key)) {
				System.err.println(toString() + ":" + "get(" + key + ") = " + result);
			}
			return result;
		}

		@Override
		public LuaValue get(final LuaValue key) {
			final LuaValue result = super.get(key);
			if (CAPTURE_KEY_SET.contains(key.toString())) {
				System.err.println(toString() + ":" + "get(" + key + ") = " + result);
			}
			return result;
		}

	}
}

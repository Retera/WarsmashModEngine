package com.etheller.warsmash.parsers.fdf;

import org.luaj.vm2.LuaValue;

import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefinition;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public class UIFrameScripts {
	public LuaValue OnLoad;
	public LuaValue OnSizeChanged;
	public LuaValue OnEvent;
	public LuaValue OnUpdate;
	public LuaValue OnShow;
	public LuaValue OnHide;
	public LuaValue OnEnter;
	public LuaValue OnLeave;
	public LuaValue OnMouseDown;
	public LuaValue OnMouseUp;
	public LuaValue OnMouseWheel;
	public LuaValue OnDragStart;
	public LuaValue OnDragStop;
	public LuaValue OnReceiveDrag;
	public LuaValue OnClick;
	public LuaValue OnValueChanged;
	public LuaValue OnUpdateModel;
	public LuaValue OnAnimFinished;
	public LuaValue OnEnterPressed;
	public LuaValue OnEscapePressed;
	public LuaValue OnSpacePressed;
	public LuaValue OnTabPressed;
	public LuaValue OnTextChanged;
	public LuaValue OnTextSet;
	public LuaValue OnHorizontalScroll;
	public LuaValue OnVerticalScroll;
	public LuaValue OnScrollRangeChanged;
	public LuaValue OnChar;
	public LuaValue OnKeyDown;
	public LuaValue OnKeyUp;
	public LuaValue OnHyperlinkEnter;
	public LuaValue OnHyperlinkLeave;

	private UIFrameLuaWrapper thisFrame;
	private LuaEnvironment luaEnvironment;

	public void load(final LuaEnvironment luaEnvironment, final FrameDefinition frameDef, final UIFrame uiFrame) {
		this.luaEnvironment = luaEnvironment;
		if (frameDef != null) {
			this.OnLoad = loadSingle(luaEnvironment, frameDef, "OnLoad");
			this.OnSizeChanged = loadSingle(luaEnvironment, frameDef, "OnSizeChanged");
			this.OnEvent = loadSingle(luaEnvironment, frameDef, "OnEvent");
			this.OnUpdate = loadSingle(luaEnvironment, frameDef, "OnUpdate");
			this.OnShow = loadSingle(luaEnvironment, frameDef, "OnShow");
			this.OnHide = loadSingle(luaEnvironment, frameDef, "OnHide");
			this.OnEnter = loadSingle(luaEnvironment, frameDef, "OnEnter");
			this.OnLeave = loadSingle(luaEnvironment, frameDef, "OnLeave");
			this.OnMouseDown = loadSingle(luaEnvironment, frameDef, "OnMouseDown");
			this.OnMouseUp = loadSingle(luaEnvironment, frameDef, "OnMouseUp");
			this.OnMouseWheel = loadSingle(luaEnvironment, frameDef, "OnMouseWheel");
			this.OnDragStart = loadSingle(luaEnvironment, frameDef, "OnDragStart");
			this.OnDragStop = loadSingle(luaEnvironment, frameDef, "OnDragStop");
			this.OnReceiveDrag = loadSingle(luaEnvironment, frameDef, "OnReceiveDrag");
			this.OnClick = loadSingle(luaEnvironment, frameDef, "OnClick");
			this.OnValueChanged = loadSingle(luaEnvironment, frameDef, "OnValueChanged");
			this.OnUpdateModel = loadSingle(luaEnvironment, frameDef, "OnUpdateModel");
			this.OnAnimFinished = loadSingle(luaEnvironment, frameDef, "OnAnimFinished");
			this.OnEnterPressed = loadSingle(luaEnvironment, frameDef, "OnEnterPressed");
			this.OnEscapePressed = loadSingle(luaEnvironment, frameDef, "OnEscapePressed");
			this.OnSpacePressed = loadSingle(luaEnvironment, frameDef, "OnSpacePressed");
			this.OnTabPressed = loadSingle(luaEnvironment, frameDef, "OnTabPressed");
			this.OnTextChanged = loadSingle(luaEnvironment, frameDef, "OnTextChanged");
			this.OnTextSet = loadSingle(luaEnvironment, frameDef, "OnTextSet");
			this.OnHorizontalScroll = loadSingle(luaEnvironment, frameDef, "OnHorizontalScroll");
			this.OnVerticalScroll = loadSingle(luaEnvironment, frameDef, "OnVerticalScroll");
			this.OnScrollRangeChanged = loadSingle(luaEnvironment, frameDef, "OnScrollRangeChanged");
			this.OnChar = loadSingle(luaEnvironment, frameDef, "OnChar");
			this.OnKeyDown = loadSingle(luaEnvironment, frameDef, "OnKeyDown");
			this.OnKeyUp = loadSingle(luaEnvironment, frameDef, "OnKeyUp");
			this.OnHyperlinkEnter = loadSingle(luaEnvironment, frameDef, "OnHyperlinkEnter");
			this.OnHyperlinkLeave = loadSingle(luaEnvironment, frameDef, "OnHyperlinkLeave");
		}

		this.thisFrame = new UIFrameLuaWrapper(uiFrame, luaEnvironment);
		luaEnvironment.getGlobals().set(uiFrame.getName(), this.thisFrame.getTable());

		if (this.OnLoad != null) {
			luaEnvironment.load(this.thisFrame);
			try {
				this.OnLoad.call();
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public void onClick() {
		if (this.OnClick != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.OnClick.call();
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	private LuaValue loadSingle(final LuaEnvironment luaEnvironment, final FrameDefinition frameDef, final String key) {
		final String code = frameDef.getString(key);
		if (code != null) {
			try {
				return luaEnvironment.load(code);
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
		return null;
	}

	public UIFrameLuaWrapper getLuaWrapper() {
		return this.thisFrame;
	}
}

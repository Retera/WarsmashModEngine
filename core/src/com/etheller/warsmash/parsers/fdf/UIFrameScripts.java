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
	private FrameDefinition frameDef;

	public void inflate(final LuaEnvironment luaEnvironment, final FrameDefinition frameDef, final UIFrame uiFrame) {
		this.luaEnvironment = luaEnvironment;
		this.frameDef = frameDef;

		this.thisFrame = new UIFrameLuaWrapper(uiFrame, luaEnvironment);
		luaEnvironment.getGlobals().set(uiFrame.getName(), this.thisFrame.getTable());
	}

	public void onLoad() {
		if (this.frameDef != null) {
			this.OnLoad = loadSingle(this.luaEnvironment, this.frameDef, "OnLoad");
			this.OnSizeChanged = loadSingle(this.luaEnvironment, this.frameDef, "OnSizeChanged");
			this.OnEvent = loadSingle(this.luaEnvironment, this.frameDef, "OnEvent");
			this.OnUpdate = loadSingle(this.luaEnvironment, this.frameDef, "OnUpdate");
			this.OnShow = loadSingle(this.luaEnvironment, this.frameDef, "OnShow");
			this.OnHide = loadSingle(this.luaEnvironment, this.frameDef, "OnHide");
			this.OnEnter = loadSingle(this.luaEnvironment, this.frameDef, "OnEnter");
			this.OnLeave = loadSingle(this.luaEnvironment, this.frameDef, "OnLeave");
			this.OnMouseDown = loadSingle(this.luaEnvironment, this.frameDef, "OnMouseDown");
			this.OnMouseUp = loadSingle(this.luaEnvironment, this.frameDef, "OnMouseUp");
			this.OnMouseWheel = loadSingle(this.luaEnvironment, this.frameDef, "OnMouseWheel");
			this.OnDragStart = loadSingle(this.luaEnvironment, this.frameDef, "OnDragStart");
			this.OnDragStop = loadSingle(this.luaEnvironment, this.frameDef, "OnDragStop");
			this.OnReceiveDrag = loadSingle(this.luaEnvironment, this.frameDef, "OnReceiveDrag");
			this.OnClick = loadSingle(this.luaEnvironment, this.frameDef, "OnClick");
			this.OnValueChanged = loadSingle(this.luaEnvironment, this.frameDef, "OnValueChanged");
			this.OnUpdateModel = loadSingle(this.luaEnvironment, this.frameDef, "OnUpdateModel");
			this.OnAnimFinished = loadSingle(this.luaEnvironment, this.frameDef, "OnAnimFinished");
			this.OnEnterPressed = loadSingle(this.luaEnvironment, this.frameDef, "OnEnterPressed");
			this.OnEscapePressed = loadSingle(this.luaEnvironment, this.frameDef, "OnEscapePressed");
			this.OnSpacePressed = loadSingle(this.luaEnvironment, this.frameDef, "OnSpacePressed");
			this.OnTabPressed = loadSingle(this.luaEnvironment, this.frameDef, "OnTabPressed");
			this.OnTextChanged = loadSingle(this.luaEnvironment, this.frameDef, "OnTextChanged");
			this.OnTextSet = loadSingle(this.luaEnvironment, this.frameDef, "OnTextSet");
			this.OnHorizontalScroll = loadSingle(this.luaEnvironment, this.frameDef, "OnHorizontalScroll");
			this.OnVerticalScroll = loadSingle(this.luaEnvironment, this.frameDef, "OnVerticalScroll");
			this.OnScrollRangeChanged = loadSingle(this.luaEnvironment, this.frameDef, "OnScrollRangeChanged");
			this.OnChar = loadSingle(this.luaEnvironment, this.frameDef, "OnChar");
			this.OnKeyDown = loadSingle(this.luaEnvironment, this.frameDef, "OnKeyDown");
			this.OnKeyUp = loadSingle(this.luaEnvironment, this.frameDef, "OnKeyUp");
			this.OnHyperlinkEnter = loadSingle(this.luaEnvironment, this.frameDef, "OnHyperlinkEnter");
			this.OnHyperlinkLeave = loadSingle(this.luaEnvironment, this.frameDef, "OnHyperlinkLeave");
		}

		if (this.thisFrame.getFrame().getName().equals("SpellButton1")) {
			System.out.println("SB1");
		}

		this.frameDef = null;
		if (this.OnLoad != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.OnLoad.call();
			}
			catch (final Exception exc) {
				System.err.println("Who called load??? " + this.thisFrame.getFrame().getName());
				exc.printStackTrace();
			}
		}

	}

	public boolean isLoaded() {
		return this.frameDef == null;
	}

	public void onClick(final ThirdPersonLuaXmlButton button) {
		if (this.OnClick != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.luaEnvironment.getGlobals().set("arg1", button.name());
				this.OnClick.call();
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public void onShow() {
		if (this.OnShow != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.OnShow.call();
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public void onHide() {

		if (this.OnHide != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.OnHide.call();
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public void onSizeChanged() {

		if (this.OnSizeChanged != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.OnSizeChanged.call();
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public void onEvent(final ThirdPersonLuaXmlEvent event, final LuaValue arg1) {
		if (this.OnEvent != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.luaEnvironment.getGlobals().set("event", event.name());
				this.luaEnvironment.getGlobals().set("arg1", arg1);
				this.OnEvent.call();
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public void onUpdate(final double elapsedMillis) {
		if (this.OnUpdate != null) {
			this.luaEnvironment.load(this.thisFrame);
			try {
				this.luaEnvironment.getGlobals().set("arg1", LuaValue.valueOf(elapsedMillis));
				this.OnUpdate.call();
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

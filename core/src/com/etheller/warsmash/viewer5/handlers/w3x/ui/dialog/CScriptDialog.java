package com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CScriptDialog {
	private final GlobalScope globalScope;
	private SimpleFrame scriptDialogFrame;
	private StringFrame scriptDialogTextFrame;
	private UIFrame lastAddedComponent;
	private final List<Trigger> eventTriggers = new ArrayList<>();

	public CScriptDialog(final GlobalScope globalScope, final SimpleFrame scriptDialogFrame,
			final StringFrame scriptDialogTextFrame) {
		this.globalScope = globalScope;
		this.scriptDialogFrame = scriptDialogFrame;
		this.scriptDialogTextFrame = scriptDialogTextFrame;
		this.lastAddedComponent = scriptDialogTextFrame;
	}

	public SimpleFrame getScriptDialogFrame() {
		return this.scriptDialogFrame;
	}

	public StringFrame getScriptDialogTextFrame() {
		return this.scriptDialogTextFrame;
	}

	public UIFrame getLastAddedComponent() {
		return this.lastAddedComponent;
	}

	public void setTitle(final GameUI rootFrame, final String text) {
		rootFrame.setText(this.scriptDialogTextFrame, text);
	}

	public void reset(final SimpleFrame scriptDialogFrame, final StringFrame scriptDialogTextFrame) {
		this.scriptDialogFrame = scriptDialogFrame;
		this.scriptDialogTextFrame = scriptDialogTextFrame;
		this.lastAddedComponent = scriptDialogTextFrame;
	}

	public void setVisible(final boolean flag) {
		this.scriptDialogFrame.setVisible(flag);
	}

	public RemovableTriggerEvent addEvent(final Trigger trigger) {
		this.eventTriggers.add(trigger);
		return new RemovableTriggerEvent(trigger) {
			@Override
			public void remove() {
				CScriptDialog.this.eventTriggers.remove(trigger);
			}
		};
	}

	public void onButtonClick(final CScriptDialogButton cScriptDialogButton) {
		this.scriptDialogFrame.setVisible(false);
		for (final Trigger trigger : this.eventTriggers) {
			final CommonTriggerExecutionScope scope = CommonTriggerExecutionScope
					.triggerDialogScope(JassGameEventsWar3.EVENT_DIALOG_CLICK, trigger, this, cScriptDialogButton);
			this.globalScope.queueTrigger(null, null, trigger, scope, scope);
		}
	}

	public void addButton(final GameUI rootFrame, final Viewport uiViewport,
			final CScriptDialogButton scriptDialogButton) {
		final GlueTextButtonFrame buttonFrame = scriptDialogButton.getButtonFrame();
		this.scriptDialogFrame.add(buttonFrame);
		this.lastAddedComponent = buttonFrame;
		buttonFrame.positionBounds(rootFrame, uiViewport);
		this.scriptDialogFrame
				.setHeight(((this.scriptDialogFrame.getAssignedHeight() + (buttonFrame.getFramePointY(FramePoint.TOP)))
						- buttonFrame.getFramePointY(FramePoint.BOTTOM)) * 1.5f);
		this.scriptDialogFrame.positionBounds(rootFrame, uiViewport);
		scriptDialogButton.setupEvents(this);
	}

	public GlobalScope getGlobalScope() {
		return this.globalScope;
	}
}

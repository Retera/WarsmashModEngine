package com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CScriptDialogButton {
	private final GlueTextButtonFrame buttonFrame;
	private final StringFrame buttonText;
	private final List<Trigger> eventTriggers = new ArrayList<>();

	public CScriptDialogButton(final GlueTextButtonFrame buttonFrame, final StringFrame buttonText) {
		this.buttonFrame = buttonFrame;
		this.buttonText = buttonText;
	}

	public GlueTextButtonFrame getButtonFrame() {
		return this.buttonFrame;
	}

	public void setText(final GameUI rootFrame, final String text) {
		rootFrame.setText(this.buttonText, text);
	}

	public void setupEvents(final CScriptDialog dialog) {
		this.buttonFrame.setOnClick(new Runnable() {
			@Override
			public void run() {
				for (final Trigger trigger : CScriptDialogButton.this.eventTriggers) {
					final CommonTriggerExecutionScope scope = CommonTriggerExecutionScope.triggerDialogScope(
							JassGameEventsWar3.EVENT_DIALOG_BUTTON_CLICK, trigger, dialog, CScriptDialogButton.this);
					dialog.getGlobalScope().queueTrigger(null, null, trigger, scope, scope);
				}
				dialog.onButtonClick(CScriptDialogButton.this);
			}
		});
	}

	public RemovableTriggerEvent addEvent(final Trigger trigger) {
		this.eventTriggers.add(trigger);
		return new RemovableTriggerEvent(trigger) {
			@Override
			public void remove() {
				CScriptDialogButton.this.eventTriggers.remove(trigger);
			}
		};
	}
}

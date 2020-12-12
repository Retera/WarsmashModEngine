package com.etheller.warsmash.desktop.editor.mdx.listeners;

import com.etheller.warsmash.parsers.mdlx.MdlxModel;
import com.etheller.warsmash.util.SubscriberSetNotifier;

public interface YseraGUIListener {
	void openModel(MdlxModel model);

	// probably repaint
	void stateChanged();

	class YseraGUINotifier extends SubscriberSetNotifier<YseraGUIListener> implements YseraGUIListener {

		@Override
		public void openModel(final MdlxModel model) {
			for (final YseraGUIListener listener : set) {
				listener.openModel(model);
			}
		}

		@Override
		public void stateChanged() {
			for (final YseraGUIListener listener : set) {
				listener.stateChanged();
			}
		}

	}
}

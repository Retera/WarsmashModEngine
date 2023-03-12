package com.etheller.warsmash.parsers.fdf.frames;

import com.etheller.warsmash.parsers.fdf.UIFrameScripts;

public class XmlCheckBoxFrame extends CheckBoxFrame {

	public XmlCheckBoxFrame(final String name, final UIFrame parent) {
		super(name, parent);
		setOnClick(new Runnable() {
			@Override
			public void run() {
				final UIFrameScripts scripts = getScripts();
				if (scripts != null) {
					scripts.onClick();
				}
			}
		});
	}
}

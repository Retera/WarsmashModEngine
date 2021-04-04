package com.etheller.warsmash.units.custom;

public interface WTS {
	String get(int key);

	WTS DO_NOTHING = new WTS() {
		@Override
		public String get(final int key) {
			return "TRIGSTR_" + key;
		}
	};
}

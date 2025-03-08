package com.etheller.warsmash;

import com.etheller.warsmash.viewer5.FogSettings;

public interface SingleModelScreen {
	void setModel(String path, FogSettings fogSettings);

	void alternateModelToBattlenet();

	void unAlternateModelBackToNormal();
}

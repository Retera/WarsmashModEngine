package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import com.badlogic.gdx.graphics.Texture;

public class UnitIconUI  extends IconUI {
    private String reviveTip;
    private String awakenTip;

    public UnitIconUI(Texture icon, Texture iconDisabled, int buttonPositionX, int buttonPositionY, String toolTip, String uberTip, String reviveTip, String awakenTip) {
        super(icon, iconDisabled, buttonPositionX, buttonPositionY, toolTip, uberTip);
        this.reviveTip = reviveTip;
        this.awakenTip = awakenTip;
    }

    public String getReviveTip() {
        return reviveTip;
    }

    public String getAwakenTip() {
        return awakenTip;
    }
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.rect;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.util.CHandle;

public class CRect extends Rectangle implements CHandle {
    private final int handleId;

    public CRect(int handleId, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.handleId = handleId;
    }

    @Override
    public int getHandleId() {
        return handleId;
    }
}

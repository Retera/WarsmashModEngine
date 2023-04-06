package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

public enum CDirection {
    NORTH_WEST(-1, 1),
    NORTH(0, 1),
    NORTH_EAST(1, 1),
    EAST(1, 0),
    SOUTH_EAST(1, -1),
    SOUTH(0, -1),
    SOUTH_WEST(-1, -1),
    WEST(-1, 0);

    public static final CDirection[] VALUES = values();

    public final int xOffset;
    public final int yOffset;
    public final double length;

    private CDirection(final int xOffset, final int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        final double sqrt = Math.sqrt((xOffset * xOffset) + (yOffset * yOffset));
        this.length = sqrt;
    }
}
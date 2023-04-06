package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.awt.geom.Point2D;

public class CPathingNode {
    public CDirection cameFromDirection;
    public final Point2D.Float point;
    public double f;
    public double g;
    public CPathingNode cameFrom;
    public int pathfindJobId;

    public CPathingNode(final Point2D.Float point) {
        this.point = point;
    }

    public void touch(final int pathfindJobId) {
        if (pathfindJobId != this.pathfindJobId) {
            this.g = Float.POSITIVE_INFINITY;
            this.f = Float.POSITIVE_INFINITY;
            this.cameFrom = null;
            this.cameFromDirection = null;
            this.pathfindJobId = pathfindJobId;
        }
    }
}

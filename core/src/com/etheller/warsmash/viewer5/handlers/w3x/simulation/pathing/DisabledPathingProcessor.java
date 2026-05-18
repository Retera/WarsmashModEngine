package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;

import java.awt.geom.Point2D;

// tdauth: Use to avoid "OutOfMemoryError: Java heap space" in CPathfindingProcessor.java:46
public class DisabledPathingProcessor  implements PathingProcessor {

    @Override
    public void findNaiveSlowPath(CUnit ignoreIntersectionsWithThisUnit, CUnit ignoreIntersectionsWithThisSecondUnit, float startX, float startY, Point2D.Float goal, PathingGrid.MovementType movementType, float collisionSize, boolean allowSmoothing, CBehaviorMove queueItem) {}

    @Override
    public void removeFromPathfindingQueue(CBehaviorMove behaviorMove) {}

    @Override
    public void update(CSimulation simulation) {}
}

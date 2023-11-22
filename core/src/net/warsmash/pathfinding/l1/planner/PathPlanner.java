package net.warsmash.pathfinding.l1.planner;

import java.util.List;

import net.warsmash.pathfinding.l1.AbstractGraph;
import net.warsmash.pathfinding.l1.Geometry;
import net.warsmash.pathfinding.l1.util.Point;

public interface PathPlanner extends TransformedSpace {
	AbstractGraph getGraph();

	Geometry getGeometry();

	public double search(double tx, double ty, double sx, double sy, List<Point> outo);
}

package net.warsmash.pathfinding.l1.planner;

import java.util.List;

import net.warsmash.pathfinding.l1.vertex.Vertex;

public interface INode {
	List<Vertex> getVerts();

	List<Bucket> getBuckets();

	boolean isLeaf();

	INode getLeft();

	INode getRight();

	double getX();
}

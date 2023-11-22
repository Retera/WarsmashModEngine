package net.warsmash.pathfinding.l1.planner;

import java.util.List;

import net.warsmash.pathfinding.l1.vertex.Vertex;

public class Leaf implements INode {
	public final List<Vertex> verts;

	@Override
	public List<Vertex> getVerts() {
		return verts;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public INode getLeft() {
		return null;
	}

	@Override
	public INode getRight() {
		return null;
	}

	@Override
	public double getX() {
		return 0;
	}

	@Override
	public List<Bucket> getBuckets() {
		return null;
	}

	public Leaf(final List<Vertex> verts) {
		this.verts = verts;
	}
}

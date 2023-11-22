package net.warsmash.pathfinding.l1;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.pathfinding.l1.vertex.Vertex;;

public class AbstractGraph {
	public Vertex target;
	public List<Vertex> verts = new ArrayList<>();
	public Vertex freeList;
	public Vertex toVisit;
	public Vertex lastS;
	public Vertex lastT;
	public double srcX = 0;
	public double srcY = 0;
	public double dstX = 0;
	public double dstY = 0;
	public List<Vertex> landmarks = new ArrayList<>();
	public double[] landmarkDist;

}

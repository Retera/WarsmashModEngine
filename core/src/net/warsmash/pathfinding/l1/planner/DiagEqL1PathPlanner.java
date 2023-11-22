package net.warsmash.pathfinding.l1.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.warsmash.pathfinding.l1.BSearch;
import net.warsmash.pathfinding.l1.DiagEqGraph;
import net.warsmash.pathfinding.l1.Geometry;
import net.warsmash.pathfinding.l1.util.Point;
import net.warsmash.pathfinding.l1.vertex.IPoint;
import net.warsmash.pathfinding.l1.vertex.Vertex;

public class DiagEqL1PathPlanner implements PathPlanner {
	public Geometry geometry;
	public DiagEqGraph graph;
	public INode root;

	private DiagEqL1PathPlanner(final Geometry geometry, final DiagEqGraph graph, final INode root) {
		this.geometry = geometry;
		this.graph = graph;
		this.root = root;
	}

	public static int compareBucket(final Bucket bucket, final double y) {
		return (int) Math.signum(bucket.y0 - y);
	}

	public static void connectList(final List<Vertex> nodes, final Geometry geom, final DiagEqGraph graph,
			final boolean target, final double x, final double y) {
		for (int i = 0; i < nodes.size(); ++i) {
			final Vertex v = nodes.get(i);
			if (!geom.stabBox(v.x, v.y, x, y)) {
				if (target) {
					graph.addT(v);
				}
				else {
					graph.addS(v);
				}
			}
		}
	}

	public static void connectNodes(final Geometry geom, final DiagEqGraph graph, INode node, final boolean target,
			final double x, final double y) {
		// Mark target nodes
		while (node != null) {
			// Check leaf case
			if (node.isLeaf()) {
				final List<Vertex> vv = node.getVerts();
				final int nn = vv.size();
				for (int i = 0; i < nn; ++i) {
					final Vertex v = vv.get(i);
					if (!geom.stabBox(v.x, v.y, x, y)) {
						if (target) {
							graph.addT(v);
						}
						else {
							graph.addS(v);
						}
					}
				}
				break;
			}

			// Otherwise, glue into buckets
			final List<Bucket> buckets = node.getBuckets();
			final int idx = BSearch.search(buckets, y, DiagEqL1PathPlanner::compareBucket);
			if (idx >= 0) {
				final Bucket bb = buckets.get(idx);
				if (y < bb.y1) {
					// Common case:
					if (node.getX() >= x) {
						// Connect right
						connectList(bb.right, geom, graph, target, x, y);
					}
					if (node.getX() <= x) {
						// Connect left
						connectList(bb.left, geom, graph, target, x, y);
					}
					// Connect on
					connectList(bb.on, geom, graph, target, x, y);
				}
				else {
					// Connect to bottom of bucket above
					final Vertex v = buckets.get(idx).bottom;
					if ((v != null) && !geom.stabBox(v.x, v.y, x, y)) {
						if (target) {
							graph.addT(v);
						}
						else {
							graph.addS(v);
						}
					}
					// Connect to top of bucket below
					if ((idx + 1) < buckets.size()) {
						final Vertex v2 = buckets.get(idx + 1).top;
						if ((v2 != null) && !geom.stabBox(v2.x, v2.y, x, y)) {
							if (target) {
								graph.addT(v2);
							}
							else {
								graph.addS(v2);
							}
						}
					}
				}
			}
			else {
				// Connect to top of box
				final Vertex v = buckets.get(0).top;
				if ((v != null) && !geom.stabBox(v.x, v.y, x, y)) {
					if (target) {
						graph.addT(v);
					}
					else {
						graph.addS(v);
					}
				}
			}
			if (node.getX() > x) {
				node = node.getLeft();
			}
			else if (node.getX() < x) {
				node = node.getRight();
			}
			else {
				break;
			}
		}
	}

	public static void resetSourceList(final List<Vertex> nodes, final Geometry geom, final DiagEqGraph graph,
			final double x, final double y) {
		for (int i = 0; i < nodes.size(); ++i) {
			final Vertex v = nodes.get(i);
			if (!geom.stabBox(v.x, v.y, x, y)) {
				graph.resetS(v);
			}
		}
	}

	public static void resetSourceNodes(final Geometry geom, final DiagEqGraph graph, INode node, final double x,
			final double y) {
		// Mark target nodes
		while (node != null) {
			// Check leaf case
			if (node.isLeaf()) {
				final List<Vertex> vv = node.getVerts();
				final int nn = vv.size();
				for (int i = 0; i < nn; ++i) {
					final Vertex v = vv.get(i);
					if (!geom.stabBox(v.x, v.y, x, y)) {
						graph.resetS(v);
					}
				}
				break;
			}

			// Otherwise, glue into buckets
			final List<Bucket> buckets = node.getBuckets();
			final int idx = BSearch.search(buckets, y, DiagEqL1PathPlanner::compareBucket);
			if (idx >= 0) {
				final Bucket bb = buckets.get(idx);
				if (y < bb.y1) {
					// Common case:
					if (node.getX() >= x) {
						// Connect right
						resetSourceList(bb.right, geom, graph, x, y);
					}
					if (node.getX() <= x) {
						// Connect left
						resetSourceList(bb.left, geom, graph, x, y);
					}
					// Connect on
					resetSourceList(bb.on, geom, graph, x, y);
				}
				else {
					// Connect to bottom of bucket above
					final Vertex v = buckets.get(idx).bottom;
					if ((v != null) && !geom.stabBox(v.x, v.y, x, y)) {
						graph.resetS(v);
					}
					// Connect to top of bucket below
					if ((idx + 1) < buckets.size()) {
						final Vertex v2 = buckets.get(idx + 1).top;
						if ((v2 != null) && !geom.stabBox(v2.x, v2.y, x, y)) {
							graph.resetS(v2);
						}
					}
				}
			}
			else {
				// Connect to top of box
				final Vertex v = buckets.get(0).top;
				if ((v != null) && !geom.stabBox(v.x, v.y, x, y)) {
					graph.resetS(v);
				}
			}
			if (node.getX() > x) {
				node = node.getLeft();
			}
			else if (node.getX() < x) {
				node = node.getRight();
			}
			else {
				break;
			}
		}
	}

	public static void connectListFailingTarget(final List<Vertex> nodes, final DiagEqGraph graph, final double x,
			final double y) {
		for (int i = 0; i < nodes.size(); ++i) {
			final Vertex v = nodes.get(i);
			graph.addT(v);
		}
	}

	public static void locateBackupTarget(final Geometry geom, final DiagEqGraph graph, final INode node,
			final double sourceX, final double sourceY, final double targetX, final double targetY) {
		double bestDistance = Double.POSITIVE_INFINITY;
		Vertex bestNode = null;
		for (final Vertex vertex : graph.verts) {
			final double vertexDistance = DiagEqGraph.heuristicDistance(vertex.x, vertex.y, targetX, targetY);
			if (vertexDistance < bestDistance) {
				bestNode = vertex;
				bestDistance = vertexDistance;
			}
		}
		if (bestNode != null) {
			graph.addT(bestNode);
		}
	}

	public static void locateBackupTargetInSourceComponent(final Geometry geom, final DiagEqGraph graph,
			final INode node, final double sourceX, final double sourceY, final double targetX, final double targetY) {
		double bestDistance = Double.POSITIVE_INFINITY;
		Vertex bestNode = null;
		for (final Vertex vertex : graph.verts) {
			if (vertex.component == graph.lastS.component) {
				final double vertexDistance = DiagEqGraph.heuristicDistance(vertex.x, vertex.y, targetX, targetY);
				if (vertexDistance < bestDistance) {
					bestNode = vertex;
					bestDistance = vertexDistance;
				}
			}
		}
		if (bestNode != null) {
			graph.addT(bestNode);
		}
	}

	@Override
	public double search(final double tx, final double ty, final double sx, final double sy, final List<Point> outo) {
		final Geometry geom = this.geometry;

		// Degenerate case: s and t are equal
		if ((tx == sx) && (ty == sy)) {
			// TODO comparing double for "==", should check impl
			if (!geom.stabBox(tx, ty, sx, sy)) {
				if (outo != null) {
					outo.add(new Point(sx, sy));
				}
				return 0;
			}
			return Double.POSITIVE_INFINITY;
		}

		// Check easy case - s and t directly connected
		if (!geom.stabBox(tx, ty, sx, sy)) {
			if (outo != null) {
				outo.add(new Point(tx, ty));
				outo.add(new Point(sx, sy));
			}
			return Math.abs(tx - sx) + Math.abs(ty - sy);
		}

		// Prepare graph
		final DiagEqGraph graph = this.graph;
		graph.setSourceAndTarget(sx, sy, tx, ty);

		boolean sourceOrTargetFailed = false;
		// Mark target
		connectNodes(geom, graph, this.root, true, tx, ty);

		if (!graph.foundTarget()) {
			// Mark fake target for the purpose of "best-effort"
			locateBackupTarget(geom, graph, root, sx, sy, tx, ty);
			sourceOrTargetFailed = true;
		}

		// Mark source
		connectNodes(geom, graph, this.root, false, sx, sy);
		if (graph.foundSource() && graph.searchWillFail()) {
			// Mark fake target for the purpose of "best-effort"
			locateBackupTargetInSourceComponent(geom, graph, root, sx, sy, tx, ty);
			resetSourceNodes(geom, graph, root, sx, sy);
			sourceOrTargetFailed = true;
		}

		double dist;
		if (!sourceOrTargetFailed || geom.stabBox(sx, sy, graph.lastT.x, graph.lastT.y)) {
			// Run A*
			dist = graph.search(sourceOrTargetFailed);
		}
		else {
			dist = -1;
		}

		// Recover path
		if (outo != null) {
			graph.getPath(outo);
		}

		return dist;
	}

	public static int comparePoint(final IPoint a, final IPoint b) {
		final double d = a.getY() - b.getY();
		if (d != 0) {
			return (int) Math.signum(d);
		}
		return (int) Math.signum(a.getX() - b.getX());
	}

	public static Partition makePartition(final double x, final List<IPoint> corners, final Geometry geom,
			final Object edges) {
		final List<IPoint> left = new ArrayList<>();
		final List<IPoint> right = new ArrayList<>();
		final List<IPoint> on = new ArrayList<>();

		// Intersect rays along x horizontal line
		for (int i = 0; i < corners.size(); i++) {
			final IPoint c = corners.get(i);
			if (!geom.stabRay(c.getX(), c.getY(), x)) {
				on.add(c);
			}
			if (c.getX() < x) {
				left.add(c);
			}
			else if (c.getX() > x) {
				right.add(c);
			}
		}

		// Sort on events by y then x
		on.sort(DiagEqL1PathPlanner::comparePoint);

		// Construct vertices and horizontal edges
		final List<IPoint> vis = new ArrayList<>();
		final List<IPoint> rem = new ArrayList<>();
		for (int i = 0; i < on.size();) {
			double l = x;
			double r = x;
			final IPoint v = on.get(i);
			final double y = v.getY();
			while ((i < on.size()) && (on.get(i).getY() == y) && (on.get(i).getX() < x)) {
				l = on.get(i++).getX();
			}
			if (l < x) {
				vis.add(new Vertex(l, y));
			}
			while ((i < on.size()) && (on.get(i).getY() == y) && (on.get(i).getX() == x)) {
				rem.add(on.get(i));
				vis.add(on.get(i));
				++i;
			}
			if ((i < on.size()) && (on.get(i).getY() == y)) {
				r = on.get(i++).getX();
				while ((i < on.size()) && (on.get(i).getY() == y)) {
					++i;
				}
			}

			if (r > x) {
				vis.add(new Vertex(r, y));
			}
		}

		return new Partition(x, left, right, rem, vis);
	}

	public static DiagEqL1PathPlanner create(final PathfinderGrid grid) {
		final Builder builder = new Builder(grid);
		return builder.build();
	}

	private static final class Builder {
		private final PathfinderGrid grid;
		Geometry geom;
		DiagEqGraph graph = new DiagEqGraph();
		Map<IPoint, Vertex> verts = new HashMap<IPoint, Vertex>();
		List<IPoint[]> edges = new ArrayList<>();

		public Builder(final PathfinderGrid grid) {
			this.grid = grid;
		}

		public Vertex makeVertex(final IPoint point) {
			if (point == null) {
				return null;
			}
			Vertex vertex = verts.get(point);
			if (vertex == null) {
				vertex = graph.addVertex(point.getX(), point.getY());
			}
			return vertex;
		}

		public DiagEqL1PathPlanner build() {
			geom = Geometry.createGeometry(grid);
			final INode root = makeTree(geom.corners, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

			// Link edges
			for (int i = 0; i < edges.size(); ++i) {
				Vertex.link(verts.get(edges.get(i)[0]), verts.get(edges.get(i)[1]));
			}

			// Initialized graph
			graph.init();

			// Return resulting tree
			return new DiagEqL1PathPlanner(geom, graph, root);
		}

		public List<Vertex> makeVertexList(final List<IPoint> inList) {
			final List<Vertex> output = new ArrayList<>(inList.size());
			for (int i = 0; i < inList.size(); i++) {
				output.add(makeVertex(inList.get(i)));
			}
			return output;
		}

		public Leaf makeLeaf(final List<IPoint> corners, final double x0, final double x1) {
			final List<Vertex> localVerts = new ArrayList<>();
			for (int i = 0; i < corners.size(); ++i) {
				final IPoint u = corners.get(i);
				final Vertex ux = graph.addVertex(u.getX(), u.getY());
				localVerts.add(ux);
				verts.put(u, ux);
				for (int j = 0; j < i; ++j) {
					final IPoint v = corners.get(j);
					if (!geom.stabBox(u.getX(), u.getY(), v.getX(), v.getY())) {
						edges.add(new IPoint[] { u, v });
					}
				}
			}
			return new Leaf(localVerts);
		}

		BucketInfo makeBucket(final List<IPoint> corners, final double x) {
			// Split visible corners into 3 cases
			final List<IPoint> left = new ArrayList<>();
			final List<IPoint> right = new ArrayList<>();
			final List<IPoint> on = new ArrayList<>();
			for (int i = 0; i < corners.size(); ++i) {
				if (corners.get(i).getX() < x) {
					left.add(corners.get(i));
				}
				else if (corners.get(i).getX() > x) {
					right.add(corners.get(i));
				}
				else {
					on.add(corners.get(i));
				}
			}

			// Add Steiner vertices if needed
			final double y0 = corners.get(0).getY();
			final double y1 = corners.get(corners.size() - 1).getY();
			final IPoint loSteiner = addSteiner(on, x, y0, true);
			final IPoint hiSteiner = addSteiner(on, x, y1, false);

			bipartite(left, right);
			bipartite(on, left);
			bipartite(on, right);

			// Connect vertical edges
			for (int i = 1; i < on.size(); ++i) {
				final IPoint u = on.get(i - 1);
				final IPoint v = on.get(i);
				if (!geom.stabBox(u.getX(), u.getY(), v.getX(), v.getY())) {
					edges.add(new IPoint[] { u, v });
				}
			}

			return new BucketInfo(left, right, on, loSteiner, hiSteiner, y0, y1);
		}

		IPoint addSteiner(final List<IPoint> on, final double x, final double y, final boolean first) {
			if (!geom.stabTile(y, y)) {
				for (int i = 0; i < on.size(); i++) {
					if ((on.get(i).getX() == x) && (on.get(i).getY() == y)) {
						return on.get(i);
					}
				}
				final Vertex pair = new Vertex(x, y);
				if (first) {
					on.add(0, pair);
				}
				else {
					on.add(pair);
				}
				if (!verts.containsKey(pair)) {
					verts.put(pair, graph.addVertex(x, y));
				}
				return pair;
			}
			return null;
		}

		void bipartite(final List<IPoint> a, final List<IPoint> b) {
			for (int i = 0; i < a.size(); ++i) {
				final IPoint u = a.get(i);
				for (int j = 0; j < b.size(); ++j) {
					final IPoint v = b.get(j);

					if (!geom.stabBox(u.getX(), u.getY(), v.getX(), v.getY())) {
						edges.add(new IPoint[] { u, v });
					}
				}
			}
		}

		INode makeTree(final List<IPoint> corners, final double x0, final double x1) {
			if (corners.isEmpty()) {
				return null;
			}

			if (corners.size() < Constants.LEAF_CUTOFF) {
				return makeLeaf(corners, x0, x1);
			}

			final double x = corners.get((int) ((long) corners.size() >> 1)).getX();
			final Partition partition = makePartition(x, corners, geom, edges);
			final INode left = makeTree(partition.left, x0, x);
			final INode right = makeTree(partition.right, x, x1);

			// Construct vertices
			for (int i = 0; i < partition.on.size(); ++i) {
				final IPoint partitionOnAtI = partition.on.get(i);
				verts.put(partitionOnAtI, graph.addVertex(partitionOnAtI.getX(), partitionOnAtI.getY()));
			}

			// Build buckets
			final List<IPoint> vis = partition.vis;
			final List<Bucket> buckets = new ArrayList<>();
			IPoint lastSteiner = null;
			for (int i = 0; i < vis.size();) {
				final int v0 = i;
				int v1 = Math.min((i + Constants.BUCKET_SIZE) - 1, vis.size() - 1);
				while ((++v1 < vis.size()) && (vis.get(v1 - 1).getY() == vis.get(v1).getY())) {
				}
				i = v1;
				final BucketInfo bb = makeBucket(vis.subList(v0, v1), x);
				if ((lastSteiner != null) && (bb.steiner0 != null) && !geom.stabBox(lastSteiner.getX(),
						lastSteiner.getY(), bb.steiner0.getX(), bb.steiner0.getY())) {
					edges.add(new IPoint[] { lastSteiner, bb.steiner0 });
				}
				lastSteiner = bb.steiner1;
				buckets.add(new Bucket(bb.y0, bb.y1, makeVertex(bb.steiner0), makeVertex(bb.steiner1),
						makeVertexList(bb.left), makeVertexList(bb.right), makeVertexList(bb.on)));
			}
			return new Node(x, buckets, left, right);
		}
	}

	@Override
	public DiagEqGraph getGraph() {
		return graph;
	}

	@Override
	public Geometry getGeometry() {
		return geometry;
	}

	@Override
	public double getUnprojectedX(final double x, final double y) {
		return x;
	}

	@Override
	public double getUnprojectedY(final double x, final double y) {
		return y;
	}
}

package net.warsmash.pathfinding.l1;

import static net.warsmash.pathfinding.l1.vertex.Vertex.NIL;
import static net.warsmash.pathfinding.l1.vertex.Vertex.NUM_LANDMARKS;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.pathfinding.l1.util.Point;
import net.warsmash.pathfinding.l1.vertex.IPoint;
import net.warsmash.pathfinding.l1.vertex.Vertex;;

public class Graph extends AbstractGraph {

	public static double heuristic(final double[] tdist, final double tx, final double ty, final Vertex node) {
		final double nx = +node.x;
		final double ny = +node.y;
		double pi = Math.abs(nx - tx) + Math.abs(ny - ty);
		final double[] ndist = node.landmark;
		for (int i = 0; i < NUM_LANDMARKS; ++i) {
			pi = Math.max(pi, tdist[i] - ndist[i]);
		}
		return 1.0000009536743164 * pi;
	}

	public Graph() {
		this.target = Vertex.createVertex(0, 0);
		this.freeList = this.target;
		this.toVisit = NIL;
		this.lastS = null;
		this.lastT = null;
		this.landmarkDist = Vertex.LANDMARK_DIST();
	}

	public Vertex addVertex(final double x, final double y) {
		final Vertex v = Vertex.createVertex(x, y);
		this.verts.add(v);
		return v;
	}

	public void setSourceAndTarget(final double sx, final double sy, final double tx, final double ty) {
		this.srcX = sx;
		this.srcY = sy;
		this.dstX = tx;
		this.dstY = ty;
	}

	// Mark vertex connected to source
	public void addS(final Vertex v) {
		if ((v.state & 2) == 0) {
			v.heuristic = heuristic(this.landmarkDist, this.dstX, this.dstY, v);
			v.weight = Math.abs(this.srcX - v.x) + Math.abs(this.srcY - v.y) + v.heuristic;
			v.state |= 2;
			v.pred = null;
			this.toVisit = Vertex.push(this.toVisit, v);
			this.freeList = Vertex.insert(this.freeList, v);
			this.lastS = v;
		}
	}

	// Mark vertex connected to target
	public void addT(final Vertex v) {
		if ((v.state & 1) == 0) {
			v.state |= 1;
			this.freeList = Vertex.insert(this.freeList, v);
			this.lastT = v;

			// Update heuristic
			final double d = Math.abs(v.x - this.dstX) + Math.abs(v.y - this.dstY);
			final double[] vdist = v.landmark;
			final double[] tdist = this.landmarkDist;
			for (int i = 0; i < NUM_LANDMARKS; ++i) {
				tdist[i] = Math.min(tdist[i], vdist[i] + d);
			}
		}
	}

	// Retrieves the path from dst->src
	public List<Point> getPath(List<Point> outpath) {
		if (outpath == null) {
			outpath = new ArrayList<Point>();
		}
		double prevX = this.dstX;
		double prevY = this.dstY;
		outpath.add(new Point(prevX, prevY));
		Vertex head = this.target.pred;
		while (head != null) {
			if ((prevX != head.x) && (prevY != head.y)) {
				outpath.add(new Point(head.x, prevY));
			}
			if ((prevX != head.x) || (prevY != head.y)) {
				outpath.add(new Point(head.x, head.y));
			}
			prevX = head.x;
			prevY = head.y;
			head = head.pred;
		}
		if ((prevX != this.srcX) && (prevY != this.srcY)) {
			outpath.add(new Point(this.srcX, prevY));
		}
		if ((prevX != this.srcX) || (prevY != this.srcY)) {
			outpath.add(new Point(this.srcX, this.srcY));
		}
		return outpath;
	}

	public List<List<Vertex>> findComponents() {
		final List<Vertex> verts = this.verts;
		final int n = verts.size();
		for (int i = 0; i < n; ++i) {
			verts.get(i).component = -1;
		}
		final List<List<Vertex>> components = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			final Vertex root = verts.get(i);
			if (root.component >= 0) {
				continue;
			}
			final int label = components.size();
			root.component = label;
			final List<Vertex> toVisit = new ArrayList<>();
			toVisit.add(root);
			int ptr = 0;
			while (ptr < toVisit.size()) {
				final Vertex v = toVisit.get(ptr++);
				final List<Vertex> adj = v.edges;
				for (int j = 0; j < adj.size(); ++j) {
					final Vertex u = adj.get(j);
					if (u.component >= 0) {
						continue;
					}
					u.component = label;
					toVisit.add(u);
				}
			}
			components.add(toVisit);
		}
		return components;
	}

	// Find all landmarks
	public static int compareVert(final IPoint a, final IPoint b) {
		final int d = (int) Math.signum(a.getX() - b.getX());
		if (d != 0) {
			return d;
		}
		return (int) Math.signum(a.getY() - b.getY());
	}

	// For each connected component compute a set of landmarks
	public void findLandmarks(final List<Vertex> component) {
		component.sort(Graph::compareVert);
		Vertex v = component.get((int) ((long) component.size() >> 1));
		for (int k = 0; k < NUM_LANDMARKS; ++k) {
			v.weight = 0.0;
			this.landmarks.add(v);
			for (Vertex toVisit = v; toVisit != NIL;) {
				v = toVisit;
				v.state = 2;
				toVisit = Vertex.pop(toVisit);
				final double w = v.weight;
				final List<Vertex> adj = v.edges;
				for (int i = 0; i < adj.size(); i++) {
					final Vertex u = adj.get(i);
					if (u.state == 2) {
						continue;
					}
					final double d = w + Math.abs(v.x - u.x) + Math.abs(v.y - u.y);
					if (u.state == 0) {
						u.state = 1;
						u.weight = d;
						toVisit = Vertex.push(toVisit, u);
					}
					else if (d < u.weight) {
						u.weight = d;
						toVisit = Vertex.decreaseKey(toVisit, u);
					}
				}
			}
			double farthestD = 0.0;
			for (int i = 0; i < component.size(); ++i) {
				final Vertex u = component.get(i);
				u.state = 0;
				u.landmark[k] = u.weight;
				double s = Double.POSITIVE_INFINITY;
				for (int j = 0; j <= k; ++j) {
					s = Math.min(s, u.landmark[j]);
				}
				if (s > farthestD) {
					v = u;
					farthestD = s;
				}
			}
		}
	}

	public void init() {
		final List<List<Vertex>> components = this.findComponents();
		for (int i = 0; i < components.size(); ++i) {
			this.findLandmarks(components.get(i));
		}
	}

	// Runs a* on the graph
	public double search() {
		final Vertex target = this.target;
		Vertex freeList = this.freeList;
		final double[] tdist = this.landmarkDist;

		// Initialize target properties
		double dist = Double.POSITIVE_INFINITY;

		// Test for case where S and T are disconnected
		if ((this.lastS != null) && (this.lastT != null) && (this.lastS.component == this.lastT.component)) {
			final double sx = +this.srcX;
			final double sy = +this.srcY;
			final double tx = +this.dstX;
			final double ty = +this.dstY;

			for (Vertex toVisit = this.toVisit; toVisit != NIL;) {
				final Vertex node = toVisit;
				final double nx = +node.x;
				final double ny = +node.y;
				final double d = Math.floor(node.weight - node.heuristic);

				if (node.state == 3) {
					// If node is connected to target, exit
					dist = d + Math.abs(tx - nx) + Math.abs(ty - ny);
					target.pred = node;
					break;
				}

				// Mark node closed
				node.state = 4;

				// Pop node from toVisit queue
				toVisit = Vertex.pop(toVisit);

				final List<Vertex> adj = node.edges;
				final int n = adj.size();
				for (int i = 0; i < n; ++i) {
					final Vertex v = adj.get(i);
					final int state = v.state;
					if (state == 4) {
						continue;
					}
					final double vd = d + Math.abs(nx - v.x) + Math.abs(ny - v.y);
					if (state < 2) {
						final double vh = heuristic(tdist, tx, ty, v);
						v.state |= 2;
						v.heuristic = vh;
						v.weight = vh + vd;
						v.pred = node;
						toVisit = Vertex.push(toVisit, v);
						freeList = Vertex.insert(freeList, v);
					}
					else {
						final double vw = vd + v.heuristic;
						if (vw < v.weight) {
							v.weight = vw;
							v.pred = node;
							toVisit = Vertex.decreaseKey(toVisit, v);
						}
					}
				}
			}
		}

		// Clear the free list & priority queue
		Vertex.clear(freeList);

		// Reset pointers
		this.freeList = target;
		this.toVisit = NIL;
		this.lastS = this.lastT = null;

		// Reset landmark distance
		for (int i = 0; i < NUM_LANDMARKS; ++i) {
			tdist[i] = Double.POSITIVE_INFINITY;
		}

		// Return target distance
		return dist;
	}
}

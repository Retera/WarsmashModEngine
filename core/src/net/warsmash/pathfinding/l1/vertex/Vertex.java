package net.warsmash.pathfinding.l1.vertex;

import java.util.ArrayList;
import java.util.List;

//Vertices have to do multiple things
//
//  1.  They store the topology of the graph which is going to get searched
//  2.  They implement the pairing heap data structure (intrusively)
//  3.  They implement a linked list for tracking clean up
//  4.  Track search information (keep track of predecessors, distances, open state)
//
public class Vertex implements IPoint {
	public final double x;
	public final double y;
	public double heuristic = 0.25;
	public double weight = 0.25;
	public Vertex left = null;
	public Vertex right = null;
	public Vertex parent = null;
	public int state = 0;
	public Vertex pred = null;
	public Vertex nextFree = null;
	public List<Vertex> edges = new ArrayList<>();
	public double[] landmark;
	public int component = 0;

	public static final Vertex NIL;

	static {
		// Sentinel node
		NIL = new Vertex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		NIL.weight = Double.NEGATIVE_INFINITY;
		NIL.left = NIL.right = NIL.parent = NIL;
	}

	public Vertex(final double x, final double y) {
		this.x = x;
		this.y = y;
		this.landmark = LANDMARK_DIST();
	}

	public static final int NUM_LANDMARKS = 16;

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof IPoint) {
			final IPoint c = (IPoint) o;
			return ((int) (c.getX() * 100) == (int) (this.x * 100)) && ((int) (c.getY() * 100) == (int) (this.y * 100));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (((int) x & 0xFFFF) << 16) + ((int) y & 0xFFFF);
	}

	public static double[] LANDMARK_DIST() {
		final double[] res = new double[NUM_LANDMARKS];
		for (int count = 0; count < NUM_LANDMARKS; ++count) {
			res[count] = Double.POSITIVE_INFINITY;
		}
		return res;
	}

	// Heap insertion
	private static Vertex heapInsert(final Vertex a, final Vertex b) {
		final Vertex al = a.left;
		b.right = al;
		al.parent = b;
		b.parent = a;
		a.left = b;
		a.right = NIL;
		return a;
	}

	private static Vertex merge(final Vertex a, final Vertex b) {
		if (a == NIL) {
			return b;
		}
		else if (b == NIL) {
			return a;
		}
		else if (a.weight < b.weight) {
			return heapInsert(a, b);
		}
		else {
			return heapInsert(b, a);
		}
	}

	public static Vertex push(final Vertex root, final Vertex node) {
		if (root == NIL) {
			return node;
		}
		else if (root.weight < node.weight) {
			final Vertex l = root.left;
			node.right = l;
			l.parent = node;
			node.parent = root;
			root.left = node;
			return root;
		}
		else {
			final Vertex l = node.left;
			node.right = l;
			l.parent = root;
			root.parent = node;
			node.left = root;
			return node;
		}
	}

	public static Vertex pop(Vertex root) {
		Vertex p = root.left;
		root.left = NIL;
		root = p;
		while (true) {
			Vertex q = root.right;
			if (q == NIL) {
				break;
			}
			p = root;
			Vertex r = q.right;
			Vertex s = merge(p, q);
			root = s;
			while (true) {
				p = r;
				q = r.right;
				if (q == NIL) {
					break;
				}
				r = q.right;
				s = s.right = merge(p, q);
			}
			s.right = NIL;
			if (p != NIL) {
				p.right = root;
				root = p;
			}
		}
		root.parent = NIL;
		return root;
	}

	public static Vertex decreaseKey(final Vertex root, final Vertex p) {
		final Vertex q = p.parent;
		if (q.weight < p.weight) {
			return root;
		}
		final Vertex r = p.right;
		r.parent = q;
		;
		if (q.left == p) {
			q.left = r;
		}
		else {
			q.right = r;
		}
		if (root.weight <= p.weight) {
			final Vertex l = root.left;
			l.parent = p;
			p.right = l;
			root.left = p;
			p.parent = root;
			return root;
		}
		else {
			final Vertex l = p.left;
			root.right = l;
			l.parent = root;
			p.left = root;
			root.parent = p;
			p.right = p.parent = NIL;
			return p;
		}
	}

	// Topology
	public static Vertex createVertex(final double x, final double y) {
		final Vertex result = new Vertex(x, y);
		result.left = result.right = result.parent = NIL;
		return result;
	}

	public static void link(final Vertex u, final Vertex v) {
		u.edges.add(v);
		v.edges.add(u);
	}

	// Free list functions
	public static Vertex insert(final Vertex list, final Vertex node) {
		if (node.nextFree != null) {
			return list;
		}
		node.nextFree = list;
		return node;
	}

	public static void clear(Vertex v) {
		while (v != null) {
			final Vertex next = v.nextFree;
			v.state = 0;
			v.left = v.right = v.parent = NIL;
			v.nextFree = null;
			v = next;
		}
	}

}

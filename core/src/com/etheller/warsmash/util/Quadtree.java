package com.etheller.warsmash.util;

import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Quadtree<T> {
	private static final int MAX_DEPTH = 9; // 2^9 = 512, and 512 is the biggest map size...
	private static final int SPLIT_THRESHOLD = 6;

	private final Rectangle bounds;
	private Quadtree<T> northeast;
	private Quadtree<T> northwest;
	private Quadtree<T> southwest;
	private Quadtree<T> southeast;
	private final Array<Node<T>> nodes = new Array<>();
	private boolean leaf = true;
	private final NodeAdder nodeAdder = new NodeAdder();
	private final UniqueNodeAdder uniqueNodeAdder = new UniqueNodeAdder();

	public Quadtree(final Rectangle bounds) {
		this.bounds = bounds;
	}

	public void add(final T object, final Rectangle bounds) {
		final Node<T> node = new Node<T>(object, bounds);
		add(node, 0);
	}

	public void remove(final T object, final Rectangle bounds) {
		remove(object, bounds, null);
	}

	public void translate(final T object, final Rectangle prevBoundsToUpdate, final float xShift, final float yShift) {
		final Node<T> node = remove(object, prevBoundsToUpdate, null);
		prevBoundsToUpdate.x += xShift;
		prevBoundsToUpdate.y += yShift;
		add(node, 0);
	}

	public boolean intersect(final Rectangle bounds, final QuadtreeIntersector<T> intersector) {
		if (this.leaf) {
			for (int i = 0; i < this.nodes.size; i++) {
				final Node<T> node = this.nodes.get(i);
				if (node.bounds.overlaps(bounds)) {
					if (intersector.onIntersect(node.object)) {
						return true;
					}
				}
			}
			return false;
		}
		else {
			if (this.northeast.bounds.overlaps(bounds)) {
				if (this.northeast.intersect(bounds, intersector)) {
					return true;
				}
			}
			if (this.northwest.bounds.overlaps(bounds)) {
				if (this.northwest.intersect(bounds, intersector)) {
					return true;
				}
			}
			if (this.southwest.bounds.overlaps(bounds)) {
				if (this.southwest.intersect(bounds, intersector)) {
					return true;
				}
			}
			if (this.southeast.bounds.overlaps(bounds)) {
				if (this.southeast.intersect(bounds, intersector)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean intersect(final float x, final float y, final QuadtreeIntersector<T> intersector) {
		if (this.leaf) {
			for (int i = 0; i < this.nodes.size; i++) {
				final Node<T> node = this.nodes.get(i);
				if (node.bounds.contains(x, y)) {
					if (intersector.onIntersect(node.object)) {
						return true;
					}
				}
			}
			return false;
		}
		else {
			if (this.northeast.bounds.contains(x, y)) {
				if (this.northeast.intersect(x, y, intersector)) {
					return true;
				}
			}
			if (this.northwest.bounds.contains(x, y)) {
				if (this.northwest.intersect(x, y, intersector)) {
					return true;
				}
			}
			if (this.southwest.bounds.contains(x, y)) {
				if (this.southwest.intersect(x, y, intersector)) {
					return true;
				}
			}
			if (this.southeast.bounds.contains(x, y)) {
				if (this.southeast.intersect(x, y, intersector)) {
					return true;
				}
			}
			return false;
		}
	}

	private void add(final Node<T> node, final int depth) {
		if (this.leaf) {
			if ((this.nodes.size >= SPLIT_THRESHOLD) && (depth < MAX_DEPTH)) {
				split(depth);
				// then dont return and add as a nonleaf
			}
			else {
				this.nodes.add(node);
				return;
			}
		}
		boolean overlapsAny = false;
		if (this.northeast.bounds.overlaps(node.bounds)) {
			this.northeast.add(node, depth + 1);
			overlapsAny = true;
		}
		if (this.northwest.bounds.overlaps(node.bounds)) {
			this.northwest.add(node, depth + 1);
			overlapsAny = true;
		}
		if (this.southwest.bounds.overlaps(node.bounds)) {
			this.southwest.add(node, depth + 1);
			overlapsAny = true;
		}
		if (this.southeast.bounds.overlaps(node.bounds)) {
			this.southeast.add(node, depth + 1);
			overlapsAny = true;
		}
		if (!overlapsAny) {
			throw new IllegalStateException("Does not overlap anything!");
		}
	}

	private void split(final int depth) {
		final int splitDepth = depth + 1;
		final float halfWidth = this.bounds.width / 2;
		final float x = this.bounds.x;
		final float xMidpoint = x + halfWidth;
		final float halfHeight = this.bounds.height / 2;
		final float y = this.bounds.y;
		final float yMidpoint = y + halfHeight;
		this.northeast = new Quadtree<>(new Rectangle(xMidpoint, yMidpoint, halfWidth, halfHeight));
		this.northwest = new Quadtree<>(new Rectangle(x, yMidpoint, halfWidth, halfHeight));
		this.southwest = new Quadtree<>(new Rectangle(x, y, halfWidth, halfHeight));
		this.southeast = new Quadtree<>(new Rectangle(xMidpoint, y, halfWidth, halfHeight));
		this.leaf = false;
		this.nodes.forEach(this.nodeAdder.reset(splitDepth));
		this.nodes.clear();
	}

	private Node<T> remove(final T object, final Rectangle bounds, final Quadtree<T> parent) {
		Node<T> returnValue = null;
		if (this.leaf) {
			for (int i = 0; i < this.nodes.size; i++) {
				if (this.nodes.get(i).object == object) {
					returnValue = this.nodes.removeIndex(i);
					break;
				}
			}
		}
		else {
			if (this.northeast.bounds.overlaps(bounds)) {
				returnValue = this.northeast.remove(object, bounds, this);
			}
			if (this.northwest.bounds.overlaps(bounds)) {
				returnValue = this.northwest.remove(object, bounds, this);
			}
			if (this.southwest.bounds.overlaps(bounds)) {
				returnValue = this.southwest.remove(object, bounds, this);
			}
			if (this.southeast.bounds.overlaps(bounds)) {
				returnValue = this.southeast.remove(object, bounds, this);
			}
			mergeIfNecessary();
		}
		return returnValue;
	}

	private void mergeIfNecessary() {
		if (this.northeast.leaf && this.northwest.leaf && this.southwest.leaf && this.southeast.leaf) {
			final int children = this.northeast.nodes.size + this.northwest.nodes.size + this.southwest.nodes.size
					+ this.southeast.nodes.size; // might include duplicates
			if (children <= SPLIT_THRESHOLD) {
				this.leaf = true;
				addAllUnique(this.northeast.nodes);
				addAllUnique(this.northwest.nodes);
				addAllUnique(this.southwest.nodes);
				addAllUnique(this.southeast.nodes);
				this.northeast = this.northwest = this.southwest = this.southeast = null;
			}
		}
	}

	private void addAllUnique(final Array<Node<T>> nodes) {
		nodes.forEach(this.uniqueNodeAdder);
	}

	private static final class Node<T> {
		private final T object;
		private final Rectangle bounds;

		public Node(final T object, final Rectangle bounds) {
			this.object = object;
			this.bounds = bounds;
		}
	}

	private final class NodeAdder implements Consumer<Node<T>> {
		private int splitDepth;

		private NodeAdder reset(final int splitDepth) {
			this.splitDepth = splitDepth;
			return this;
		}

		@Override
		public void accept(final Node<T> node) {
			add(node, this.splitDepth);
		}
	}

	private final class UniqueNodeAdder implements Consumer<Node<T>> {

		private UniqueNodeAdder reset() {
			return this;
		}

		@Override
		public void accept(final Node<T> node) {
			for (int i = 0; i < Quadtree.this.nodes.size; i++) {
				if (Quadtree.this.nodes.get(i) == node) {
					return;
				}
			}
			Quadtree.this.nodes.add(node);
		}
	}
}

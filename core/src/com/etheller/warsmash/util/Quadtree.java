package com.etheller.warsmash.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

	public Rectangle getBounds() {
		return bounds;
	}

	public void add(final T object, final Rectangle bounds) {
		final Node<T> node = new Node<>(object, bounds);
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
		if (leaf) {
			return IntStream.range(0, nodes.size)
					.mapToObj(nodes::get)
					.filter(node -> node.getBounds().overlaps(bounds))
					.anyMatch(node -> intersector.onIntersect(node.getObject()));
		}

		return Stream.of(northeast, northwest, southwest, southeast)
				.map(quadTree -> northeast.getBounds().overlaps(bounds) && northeast.intersect(bounds, intersector))
				.reduce((previous, current) -> previous || current)
				.orElse(false);

	}

	public boolean intersect(final float x, final float y, final QuadtreeIntersector<T> intersector) {
		if (leaf) {
			return IntStream.range(0, nodes.size)
					.mapToObj(nodes::get)
					.filter(node -> node.bounds.contains(x, y))
					.anyMatch(node -> intersector.onIntersect(node.getObject()));
		}

		return Stream.of(northeast, northwest, southwest, southeast)
				.map(quadTree -> quadTree.getBounds().contains(x, y) && quadTree.intersect(x, y, intersector))
				.reduce((previous, current) -> previous || current)
				.orElse(false);
	}



	private void add(final Node<T> node, final int depth) {
		if (leaf) {
			if ((nodes.size >= SPLIT_THRESHOLD) && (depth < MAX_DEPTH)) {
				split(depth);
				// then dont return and add as a nonleaf
			}
			else {
				nodes.add(node);
				return;
			}
		}

		boolean overlapsAny = addIfOverlaps(node, depth, northeast, northwest, southwest, southeast);

		assert overlapsAny : "Does not overlap anything!";
	}

	@SafeVarargs
	public final boolean addIfOverlaps(Node<T> node, int currentDepth, Quadtree<T>... quadTrees) {
		return Arrays.stream(quadTrees)
				.filter(quadTree -> quadTree.getBounds().overlaps(node.getBounds()))
				.map(quadTree -> {
					quadTree.add(node, currentDepth + 1);
					return true;
				})
				.anyMatch(overlaps -> true);
	}

	private void split(final int depth) {
		final float halfWidth = bounds.width / 2;
		final float x = bounds.x;
		final float xMidpoint = x + halfWidth;
		final float halfHeight = bounds.height / 2;
		final float y = bounds.y;
		final float yMidpoint = y + halfHeight;
		northeast = new Quadtree<>(new Rectangle(xMidpoint, yMidpoint, halfWidth, halfHeight));
		northwest = new Quadtree<>(new Rectangle(x, yMidpoint, halfWidth, halfHeight));
		southwest = new Quadtree<>(new Rectangle(x, y, halfWidth, halfHeight));
		southeast = new Quadtree<>(new Rectangle(xMidpoint, y, halfWidth, halfHeight));
		leaf = false;
		final int splitDepth = depth + 1;
		nodes.forEach(nodeAdder.reset(splitDepth));
		nodes.clear();
	}

	private Node<T> remove(final T object, final Rectangle bounds, final Quadtree<T> parent) {
		Node<T> returnValue = null;
		if (leaf) {
			for (int i = 0; i < nodes.size; i++) {
				if (Objects.equals(nodes.get(i).getObject(), object)) {
					returnValue = nodes.removeIndex(i);
					break;
				}
			}
		}
		else {
			returnValue = removeIfOverlap(object, bounds, northeast, northwest, southwest, southeast);

			mergeIfNecessary();
		}
		return returnValue;
	}

	@SafeVarargs
	private final Quadtree.Node<T> removeIfOverlap(T object, Rectangle bounds, Quadtree<T>... quadTrees) {
		return Arrays.stream(quadTrees)
				.filter(quadTree -> quadTree.getBounds().overlaps(bounds))
				.map(quadTree -> quadTree.remove(object, bounds, this))
				.reduce((previous, current) -> current)
				.orElse(null);
	}

	private void mergeIfNecessary() {
		if (northeast.leaf && northwest.leaf && southwest.leaf && southeast.leaf) {
			final int children = northeast.nodes.size + northwest.nodes.size + southwest.nodes.size
					+ southeast.nodes.size; // might include duplicates
			if (children <= SPLIT_THRESHOLD) {
				leaf = true;
				addAllUnique(northeast.nodes);
				addAllUnique(northwest.nodes);
				addAllUnique(southwest.nodes);
				addAllUnique(southeast.nodes);
				northeast = northwest = southwest = southeast = null;
			}
		}
	}

	private void addAllUnique(final Array<Node<T>> nodes) {
		nodes.forEach(uniqueNodeAdder);
	}

	private static final class Node<T> {
		private final T object;
		private final Rectangle bounds;

		private Node(final T object, final Rectangle bounds) {
			this.object = object;
			this.bounds = bounds;
		}

		public Rectangle getBounds() {
			return bounds;
		}

		public T getObject() {
			return object;
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
			add(node, splitDepth);
		}
	}

	private final class UniqueNodeAdder implements Consumer<Node<T>> {

		private UniqueNodeAdder reset() {
			return this;
		}

		@Override
		public void accept(final Node<T> node) {
			for (int i = 0; i < nodes.size; i++) {
				if (nodes.get(i) == node) {
					return;
				}
			}
			nodes.add(node);
		}
	}
}

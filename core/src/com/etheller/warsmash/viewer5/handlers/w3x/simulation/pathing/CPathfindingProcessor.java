package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;

public class CPathfindingProcessor {
	private final PathingGrid pathingGrid;
	private final Node[][] nodes;
	private Node goal;

	public CPathfindingProcessor(final PathingGrid pathingGrid) {
		this.pathingGrid = pathingGrid;
		this.nodes = new Node[pathingGrid.getHeight()][pathingGrid.getWidth()];
		for (int i = 0; i < this.nodes.length; i++) {
			for (int j = 0; j < this.nodes[i].length; j++) {
				this.nodes[i][j] = new Node(new Point(j, i));
			}
		}
	}

	/**
	 * Finds the path to a point using a naive, slow, and unoptimized algorithm.
	 * Does not have optimizations yet, do this for a bunch of units and it will
	 * probably lag like a walrus. The implementation here was created by reading
	 * the wikipedia article on A* to jog my memory from data structures class back
	 * in college, and is meant only as a first draft to get things working.
	 *
	 *
	 * @param start
	 * @param goal
	 * @return
	 */
	public List<Point> findNaiveSlowPath(final int startX, final int startY, final int goalX, final int goalY,
			final PathingGrid.MovementType movementType) {
		if ((startX == goalX) && (startY == goalY)) {
			return Collections.emptyList();
		}
		this.goal = this.nodes[goalY][goalX];
		final Node start = this.nodes[startY][startX];
		for (int i = 0; i < this.nodes.length; i++) {
			for (int j = 0; j < this.nodes[i].length; j++) {
				this.nodes[i][j].g = Float.POSITIVE_INFINITY;
				this.nodes[i][j].f = Float.POSITIVE_INFINITY;
				this.nodes[i][j].cameFrom = null;
			}
		}
		start.g = 0;
		start.f = h(start);
		final PriorityQueue<Node> openSet = new PriorityQueue<>(new Comparator<Node>() {
			@Override
			public int compare(final Node a, final Node b) {
				return Double.compare(f(a), f(b));
			}
		});
		openSet.add(start);

		while (!openSet.isEmpty()) {
			Node current = openSet.poll();
			if (current == this.goal) {
				final LinkedList<Point> totalPath = new LinkedList<>();
				Direction lastCameFromDirection = null;
				while (current.cameFrom != null) {
					if ((lastCameFromDirection == null) || (current.cameFromDirection != lastCameFromDirection)) {
						totalPath.addFirst(current.point);
						lastCameFromDirection = current.cameFromDirection;
					}
					current = current.cameFrom;
				}
				return totalPath;
			}

			for (final Direction direction : Direction.VALUES) {
				final int x = current.point.x + direction.xOffset;
				final int y = current.point.y + direction.yOffset;
				if ((x >= 0) && (x < this.pathingGrid.getWidth()) && (y >= 0) && (y < this.pathingGrid.getHeight())
						&& movementType.isPathable(this.pathingGrid.getCellPathing(x, y))
						&& movementType.isPathable(this.pathingGrid.getCellPathing(current.point.x, y))
						&& movementType.isPathable(this.pathingGrid.getCellPathing(x, current.point.y))) {
					double turnCost;
					if ((current.cameFromDirection != null) && (direction != current.cameFromDirection)) {
						turnCost = 0.25;
					}
					else {
						turnCost = 0;
					}
					final double tentativeScore = current.g + direction.length + turnCost;
					final Node neighbor = this.nodes[y][x];
					if (tentativeScore < neighbor.g) {
						neighbor.cameFrom = current;
						neighbor.cameFromDirection = direction;
						neighbor.g = tentativeScore;
						neighbor.f = tentativeScore + h(neighbor);
						if (!openSet.contains(neighbor)) {
							openSet.add(neighbor);
						}
					}
				}
			}
		}
		return Collections.emptyList();
	}

	public double f(final Node n) {
		return n.g + h(n);
	}

	public double g(final Node n) {
		return n.g;
	}

	public float h(final Node n) {
		return (float) n.point.distance(this.goal.point);
	}

	public static final class Node {
		public Direction cameFromDirection;
		private final Point point;
		private double f;
		private double g;
		private Node cameFrom;

		private Node(final Point point) {
			this.point = point;
		}
	}

	private static enum Direction {
		NORTH_WEST(-1, 1),
		NORTH(0, 1),
		NORTH_EAST(1, 1),
		EAST(1, 0),
		SOUTH_EAST(1, -1),
		SOUTH(0, -1),
		SOUTH_WEST(-1, -1),
		WEST(-1, 0);

		public static final Direction[] VALUES = values();

		private final int xOffset;
		private final int yOffset;
		private final double length;

		private Direction(final int xOffset, final int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			final double sqrt = Math.sqrt((xOffset * xOffset) + (yOffset * yOffset));
			this.length = sqrt;
		}
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;

public class CPathfindingProcessor {
	private static final Rectangle tempRect = new Rectangle();
	private final PathingGrid pathingGrid;
	private final CWorldCollision worldCollision;
	private final Node[][] nodes;
	private final Node[][] cornerNodes;
	private final Node[] goalSet = new Node[4];
	private int goals = 0;

	public CPathfindingProcessor(final PathingGrid pathingGrid, final CWorldCollision worldCollision) {
		this.pathingGrid = pathingGrid;
		this.worldCollision = worldCollision;
		this.nodes = new Node[pathingGrid.getHeight()][pathingGrid.getWidth()];
		this.cornerNodes = new Node[pathingGrid.getHeight() + 1][pathingGrid.getWidth() + 1];
		for (int i = 0; i < this.nodes.length; i++) {
			for (int j = 0; j < this.nodes[i].length; j++) {
				this.nodes[i][j] = new Node(new Point2D.Float(pathingGrid.getWorldX(j), pathingGrid.getWorldY(i)));
			}
		}
		for (int i = 0; i < this.cornerNodes.length; i++) {
			for (int j = 0; j < this.cornerNodes[i].length; j++) {
				this.cornerNodes[i][j] = new Node(
						new Point2D.Float(pathingGrid.getWorldXFromCorner(j), pathingGrid.getWorldYFromCorner(i)));
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
	 * @param collisionSize
	 *
	 *
	 * @param start
	 * @param goal
	 * @return
	 */
	public List<Point2D.Float> findNaiveSlowPath(final CUnit ignoreIntersectionsWithThisUnit, final float startX,
			final float startY, final Point2D.Float goal, final PathingGrid.MovementType movementType,
			final float collisionSize, final boolean allowSmoothing) {
		return findNaiveSlowPath(ignoreIntersectionsWithThisUnit, null, startX, startY, goal, movementType,
				collisionSize, allowSmoothing);
	}

	public List<Point2D.Float> findNaiveSlowPath(final CUnit ignoreIntersectionsWithThisUnit,
			final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
			final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
			final boolean allowSmoothing) {
		final float goalX = goal.x;
		final float goalY = goal.y;
		float weightForHittingWalls = 1E9f;
		if (!this.pathingGrid.isPathable(goalX, goalY, movementType, collisionSize) || !isPathableDynamically(goalX,
				goalY, ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType)) {
			weightForHittingWalls = 5E2f;
		}
		System.out.println("beginning findNaiveSlowPath for  " + startX + "," + startY + "," + goalX + "," + goalY);
		if ((startX == goalX) && (startY == goalY)) {
			return Collections.emptyList();
		}
		tempRect.set(0, 0, collisionSize * 2, collisionSize * 2);
		Node[][] searchGraph;
		GridMapping gridMapping;
		if (isCollisionSizeBetterSuitedForCorners(collisionSize)) {
			searchGraph = this.cornerNodes;
			gridMapping = GridMapping.CORNERS;
			System.out.println("using corners");
		}
		else {
			searchGraph = this.nodes;
			gridMapping = GridMapping.CELLS;
			System.out.println("using cells");
		}
		final int goalCellY = gridMapping.getY(this.pathingGrid, goalY);
		final int goalCellX = gridMapping.getX(this.pathingGrid, goalX);
		final Node mostLikelyGoal = searchGraph[goalCellY][goalCellX];
		final double bestGoalDistance = mostLikelyGoal.point.distance(goalX, goalY);
		Arrays.fill(this.goalSet, null);
		this.goals = 0;
		for (int i = goalCellX - 1; i <= (goalCellX + 1); i++) {
			for (int j = goalCellY - 1; j <= (goalCellY + 1); j++) {
				final Node possibleGoal = searchGraph[j][i];
				if (possibleGoal.point.distance(goalX, goalY) <= bestGoalDistance) {
					this.goalSet[this.goals++] = possibleGoal;
				}
			}
		}
		final int startGridY = gridMapping.getY(this.pathingGrid, startY);
		final int startGridX = gridMapping.getX(this.pathingGrid, startX);
		for (int i = 0; i < searchGraph.length; i++) {
			for (int j = 0; j < searchGraph[i].length; j++) {
				final Node node = searchGraph[i][j];
				node.g = Float.POSITIVE_INFINITY;
				node.f = Float.POSITIVE_INFINITY;
				node.cameFrom = null;
				node.cameFromDirection = null;
			}
		}
		final PriorityQueue<Node> openSet = new PriorityQueue<>(new Comparator<Node>() {
			@Override
			public int compare(final Node a, final Node b) {
				return Double.compare(f(a), f(b));
			}
		});

		final Node start = searchGraph[startGridY][startGridX];
		int startGridMinX;
		int startGridMinY;
		int startGridMaxX;
		int startGridMaxY;
		if (startX > start.point.x) {
			startGridMinX = startGridX;
			startGridMaxX = startGridX + 1;
		}
		else if (startX < start.point.x) {
			startGridMinX = startGridX - 1;
			startGridMaxX = startGridX;
		}
		else {
			startGridMinX = startGridX;
			startGridMaxX = startGridX;
		}
		if (startY > start.point.y) {
			startGridMinY = startGridY;
			startGridMaxY = startGridY + 1;
		}
		else if (startY < start.point.y) {
			startGridMinY = startGridY - 1;
			startGridMaxY = startGridY;
		}
		else {
			startGridMinY = startGridY;
			startGridMaxY = startGridY;
		}
		for (int cellX = startGridMinX; cellX <= startGridMaxX; cellX++) {
			for (int cellY = startGridMinY; cellY <= startGridMaxY; cellY++) {
				if ((cellX >= 0) && (cellX < this.pathingGrid.getWidth()) && (cellY >= 0)
						&& (cellY < this.pathingGrid.getHeight())) {
					final Node possibleNode = searchGraph[cellY][cellX];
					final float x = possibleNode.point.x;
					final float y = possibleNode.point.y;
					if (pathableBetween(ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, startX,
							startY, movementType, collisionSize, x, y)) {

						final double tentativeScore = possibleNode.point.distance(startX, startY);
						possibleNode.g = tentativeScore;
						possibleNode.f = tentativeScore + h(possibleNode);
						openSet.add(possibleNode);

					}
					else {
						final double tentativeScore = weightForHittingWalls;
						possibleNode.g = tentativeScore;
						possibleNode.f = tentativeScore + h(possibleNode);
						openSet.add(possibleNode);

					}
				}
			}
		}

		while (!openSet.isEmpty()) {
			Node current = openSet.poll();
			if (isGoal(current)) {
				final LinkedList<Point2D.Float> totalPath = new LinkedList<>();
				Direction lastCameFromDirection = null;

				if ((current.cameFrom != null)
						&& pathableBetween(ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit,
								current.point.x, current.point.y, movementType, collisionSize, goalX, goalY)
						&& pathableBetween(ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit,
								current.cameFrom.point.x, current.cameFrom.point.y, movementType, collisionSize,
								current.point.x, current.point.y)
						&& pathableBetween(ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit,
								current.cameFrom.point.x, current.cameFrom.point.y, movementType, collisionSize, goalX,
								goalY)
						&& allowSmoothing) {
					// do some basic smoothing to walk straight to the goal if it is not obstructed,
					// skipping the last grid location
					totalPath.addFirst(goal);
					current = current.cameFrom;
				}
				else {
					totalPath.addFirst(goal);
					totalPath.addFirst(current.point);
				}
				lastCameFromDirection = current.cameFromDirection;
				Node lastNode = null;
				while (current.cameFrom != null) {
					lastNode = current;
					current = current.cameFrom;
					if ((lastCameFromDirection == null) || (current.cameFromDirection != lastCameFromDirection)
							|| (current.cameFromDirection == null)) {
						if ((current.cameFromDirection != null) || (lastNode == null)
								|| !pathableBetween(ignoreIntersectionsWithThisUnit,
										ignoreIntersectionsWithThisSecondUnit, startX, startY, movementType,
										collisionSize, current.point.x, current.point.y)
								|| !pathableBetween(ignoreIntersectionsWithThisUnit,
										ignoreIntersectionsWithThisSecondUnit, current.point.x, current.point.y,
										movementType, collisionSize, lastNode.point.x, lastNode.point.y)
								|| !pathableBetween(ignoreIntersectionsWithThisUnit,
										ignoreIntersectionsWithThisSecondUnit, startX, startY, movementType,
										collisionSize, lastNode.point.x, lastNode.point.y)
								|| !allowSmoothing) {
							// Add the point if it's not the first one, or if we can only complete
							// the journey by specifically walking to the first one
							totalPath.addFirst(current.point);
							lastCameFromDirection = current.cameFromDirection;
						}
					}
				}
				return totalPath;
			}

			for (final Direction direction : Direction.VALUES) {
				final float x = current.point.x + (direction.xOffset * 32);
				final float y = current.point.y + (direction.yOffset * 32);
				if (this.pathingGrid.contains(x, y)) {
					double turnCost;
					if ((current.cameFromDirection != null) && (direction != current.cameFromDirection)) {
						turnCost = 0.25;
					}
					else {
						turnCost = 0;
					}
					double tentativeScore = current.g + ((direction.length + turnCost) * 32);
					if (!pathableBetween(ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit,
							current.point.x, current.point.y, movementType, collisionSize, x, y)) {
						tentativeScore += (direction.length) * weightForHittingWalls;
					}
					final Node neighbor = searchGraph[gridMapping.getY(this.pathingGrid, y)][gridMapping
							.getX(this.pathingGrid, x)];
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

	private boolean pathableBetween(final CUnit ignoreIntersectionsWithThisUnit,
			final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
			final PathingGrid.MovementType movementType, final float collisionSize, final float x, final float y) {
		return this.pathingGrid.isPathable(x, y, movementType, collisionSize)
				&& this.pathingGrid.isPathable(startX, y, movementType, collisionSize)
				&& this.pathingGrid.isPathable(x, startY, movementType, collisionSize)
				&& isPathableDynamically(x, y, ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit,
						movementType)
				&& isPathableDynamically(x, startY, ignoreIntersectionsWithThisUnit,
						ignoreIntersectionsWithThisSecondUnit, movementType)
				&& isPathableDynamically(startX, y, ignoreIntersectionsWithThisUnit,
						ignoreIntersectionsWithThisSecondUnit, movementType);
	}

	private boolean isPathableDynamically(final float x, final float y, final CUnit ignoreIntersectionsWithThisUnit,
			final CUnit ignoreIntersectionsWithThisSecondUnit, final PathingGrid.MovementType movementType) {
		return !this.worldCollision.intersectsAnythingOtherThan(tempRect.setCenter(x, y),
				ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType);
	}

	public static boolean isCollisionSizeBetterSuitedForCorners(final float collisionSize) {
		return (((2 * (int) collisionSize) / 32) % 2) == 1;
	}

	public double f(final Node n) {
		return n.g + h(n);
	}

	public double g(final Node n) {
		return n.g;
	}

	private boolean isGoal(final Node n) {
		for (int i = 0; i < this.goals; i++) {
			if (n == this.goalSet[i]) {
				return true;
			}
		}
		return false;
	}

	public float h(final Node n) {
		float bestDistance = 0;
		for (int i = 0; i < this.goals; i++) {
			final float possibleDistance = (float) n.point.distance(this.goalSet[i].point);
			if (possibleDistance > bestDistance) {
				bestDistance = possibleDistance; // always overestimate
			}
		}
		return bestDistance;
	}

	public static final class Node {
		public Direction cameFromDirection;
		private final Point2D.Float point;
		private double f;
		private double g;
		private Node cameFrom;

		private Node(final Point2D.Float point) {
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

	public static interface GridMapping {
		int getX(PathingGrid grid, float worldX);

		int getY(PathingGrid grid, float worldY);

		public static final GridMapping CELLS = new GridMapping() {
			@Override
			public int getX(final PathingGrid grid, final float worldX) {
				return grid.getCellX(worldX);
			}

			@Override
			public int getY(final PathingGrid grid, final float worldY) {
				return grid.getCellY(worldY);
			}

		};

		public static final GridMapping CORNERS = new GridMapping() {
			@Override
			public int getX(final PathingGrid grid, final float worldX) {
				return grid.getCornerX(worldX);
			}

			@Override
			public int getY(final PathingGrid grid, final float worldY) {
				return grid.getCornerY(worldY);
			}

		};
	}
}

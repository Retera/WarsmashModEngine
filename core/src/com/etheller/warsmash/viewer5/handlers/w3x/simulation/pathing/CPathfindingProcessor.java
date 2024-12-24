package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;

public class CPathfindingProcessor {
	private static final Rectangle tempRect = new Rectangle();
	private final PathingGrid pathingGrid;
	private final CWorldCollision worldCollision;
	private final LinkedList<PathfindingJob> moveQueue = new LinkedList<>();
	// things with modified state per current job:
	private final Node[][] nodes;
	private final Node[][] cornerNodes;
	private final Node[] goalSet = new Node[4];
	private int goals = 0;
	private int pathfindJobId = 0;
	private int totalIterations = 0;
	private int totalJobLoops = 0;
	private final int pathingGridCellCount;

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
		this.pathingGridCellCount = pathingGrid.getWidth() * pathingGrid.getHeight();
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
	 * @param playerIndex
	 * @param queueItem
	 * @return
	 */
	public void findNaiveSlowPath(final CUnit ignoreIntersectionsWithThisUnit,
			final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
			final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
			final boolean allowSmoothing, final CBehaviorMove queueItem) {
		this.moveQueue.offer(new PathfindingJob(ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit,
				startX, startY, goal, movementType, collisionSize, allowSmoothing, queueItem));
	}

	public void removeFromPathfindingQueue(final CBehaviorMove behaviorMove) {
		// TODO because of silly java things, this remove is O(N) for now,
		// we could do some refactors to make it O(1) but do we care?
		final Iterator<PathfindingJob> iterator = this.moveQueue.iterator();
		while (iterator.hasNext()) {
			final PathfindingJob job = iterator.next();
			if (job.queueItem == behaviorMove) {
				iterator.remove();
			}
		}

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
				ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType, false);
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
		private int pathfindJobId;

		private Node(final Point2D.Float point) {
			this.point = point;
		}

		private void touch(final int pathfindJobId) {
			if (pathfindJobId != this.pathfindJobId) {
				this.g = Float.POSITIVE_INFINITY;
				this.f = Float.POSITIVE_INFINITY;
				this.cameFrom = null;
				this.cameFromDirection = null;
				this.pathfindJobId = pathfindJobId;
			}
		}
	}

	private static enum Direction {
		NORTH_WEST(-1, 1), NORTH(0, 1), NORTH_EAST(1, 1), EAST(1, 0), SOUTH_EAST(1, -1), SOUTH(0, -1),
		SOUTH_WEST(-1, -1), WEST(-1, 0);

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

	public void update(final CSimulation simulation) {
		int workIterations = 0;
		JobsLoop: while (!this.moveQueue.isEmpty()) {
			this.totalJobLoops++;
			final PathfindingJob job = this.moveQueue.peek();
			if (!job.jobStarted) {
				this.pathfindJobId++;
				this.totalIterations = 0;
				this.totalJobLoops = 0;
				job.jobStarted = true;
				System.out.println("starting job with smoothing=" + job.allowSmoothing);
				workIterations += 5; // setup of job predicted cost
				job.goalX = job.goal.x;
				job.goalY = job.goal.y;
				job.weightForHittingWalls = 1E9f;
				if (!this.pathingGrid.isPathable(job.goalX, job.goalY, job.movementType, job.collisionSize)
						|| !isPathableDynamically(job.goalX, job.goalY, job.ignoreIntersectionsWithThisUnit,
								job.ignoreIntersectionsWithThisSecondUnit, job.movementType)) {
					job.weightForHittingWalls = 5E2f;
				}
				System.out.println("beginning findNaiveSlowPath for  " + job.startX + "," + job.startY + "," + job.goalX
						+ "," + job.goalY);
				if ((job.startX == job.goalX) && (job.startY == job.goalY)) {
					job.queueItem.pathFound(Collections.emptyList(), simulation);
					this.moveQueue.poll();
					continue JobsLoop;
				}
				tempRect.set(0, 0, job.collisionSize * 2, job.collisionSize * 2);
				if (isCollisionSizeBetterSuitedForCorners(job.collisionSize)) {
					job.searchGraph = this.cornerNodes;
					job.gridMapping = GridMapping.CORNERS;
					System.out.println("using corners");
				}
				else {
					job.searchGraph = this.nodes;
					job.gridMapping = GridMapping.CELLS;
					System.out.println("using cells");
				}
				final int goalCellY = job.gridMapping.getY(this.pathingGrid, job.goalY);
				final int goalCellX = job.gridMapping.getX(this.pathingGrid, job.goalX);
				final Node mostLikelyGoal = job.searchGraph[goalCellY][goalCellX];
				mostLikelyGoal.touch(this.pathfindJobId);
				final double bestGoalDistance = mostLikelyGoal.point.distance(job.goalX, job.goalY);
				Arrays.fill(this.goalSet, null);
				this.goals = 0;
				for (int i = goalCellX - 1; i <= (goalCellX + 1); i++) {
					for (int j = goalCellY - 1; j <= (goalCellY + 1); j++) {
						if ((j >= 0) && (j <= job.searchGraph.length)) {
							if ((i >= 0) && (i < job.searchGraph[j].length)) {
								final Node possibleGoal = job.searchGraph[j][i];
								possibleGoal.touch(this.pathfindJobId);
								if (possibleGoal.point.distance(job.goalX, job.goalY) <= bestGoalDistance) {
									this.goalSet[this.goals++] = possibleGoal;
								}
							}
						}
					}
				}
				final int startGridY = job.gridMapping.getY(this.pathingGrid, job.startY);
				final int startGridX = job.gridMapping.getX(this.pathingGrid, job.startX);
				job.openSet = new PriorityQueue<>(new Comparator<Node>() {
					@Override
					public int compare(final Node a, final Node b) {
						return Double.compare(f(a), f(b));
					}
				});

				job.start = job.searchGraph[startGridY][startGridX];
				job.start.touch(this.pathfindJobId);
				if (job.startX > job.start.point.x) {
					job.startGridMinX = startGridX;
					job.startGridMaxX = startGridX + 1;
				}
				else if (job.startX < job.start.point.x) {
					job.startGridMinX = startGridX - 1;
					job.startGridMaxX = startGridX;
				}
				else {
					job.startGridMinX = startGridX;
					job.startGridMaxX = startGridX;
				}
				if (job.startY > job.start.point.y) {
					job.startGridMinY = startGridY;
					job.startGridMaxY = startGridY + 1;
				}
				else if (job.startY < job.start.point.y) {
					job.startGridMinY = startGridY - 1;
					job.startGridMaxY = startGridY;
				}
				else {
					job.startGridMinY = startGridY;
					job.startGridMaxY = startGridY;
				}
				for (int cellX = job.startGridMinX; cellX <= job.startGridMaxX; cellX++) {
					for (int cellY = job.startGridMinY; cellY <= job.startGridMaxY; cellY++) {
						if ((cellX >= 0) && (cellX < this.pathingGrid.getWidth()) && (cellY >= 0)
								&& (cellY < this.pathingGrid.getHeight())) {
							final Node possibleNode = job.searchGraph[cellY][cellX];
							possibleNode.touch(this.pathfindJobId);
							final float x = possibleNode.point.x;
							final float y = possibleNode.point.y;
							if (pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, job.startX, job.startY, job.movementType,
									job.collisionSize, x, y)) {

								final double tentativeScore = possibleNode.point.distance(job.startX, job.startY);
								possibleNode.g = tentativeScore;
								possibleNode.f = tentativeScore + h(possibleNode);
								job.openSet.add(possibleNode);

							}
							else {
								final double tentativeScore = job.weightForHittingWalls;
								possibleNode.g = tentativeScore;
								possibleNode.f = tentativeScore + h(possibleNode);
								job.openSet.add(possibleNode);

							}
						}
					}
				}
			}

			while (!job.openSet.isEmpty()) {
				Node current = job.openSet.poll();
				current.touch(this.pathfindJobId);
				if (isGoal(current)) {
					final LinkedList<Point2D.Float> totalPath = new LinkedList<>();
					Direction lastCameFromDirection = null;

					if ((current.cameFrom != null)
							&& pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, current.point.x, current.point.y,
									job.movementType, job.collisionSize, job.goalX, job.goalY)
							&& pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, current.cameFrom.point.x,
									current.cameFrom.point.y, job.movementType, job.collisionSize, current.point.x,
									current.point.y)
							&& pathableBetween(job.ignoreIntersectionsWithThisUnit,
									job.ignoreIntersectionsWithThisSecondUnit, current.cameFrom.point.x,
									current.cameFrom.point.y, job.movementType, job.collisionSize, job.goalX, job.goalY)
							&& job.allowSmoothing) {
						// do some basic smoothing to walk straight to the goal if it is not obstructed,
						// skipping the last grid location
						totalPath.addFirst(job.goal);
						current = current.cameFrom;
					}
					else {
						totalPath.addFirst(job.goal);
						totalPath.addFirst(current.point);
					}
					lastCameFromDirection = current.cameFromDirection;
					Node lastNode = null;
					int stepsBackward = 0;
					while (current.cameFrom != null) {
						lastNode = current;
						current = current.cameFrom;
						if ((lastCameFromDirection == null) || (current.cameFromDirection != lastCameFromDirection)
								|| (current.cameFromDirection == null)) {
							if ((current.cameFromDirection != null) || (lastNode == null)
									|| !pathableBetween(job.ignoreIntersectionsWithThisUnit,
											job.ignoreIntersectionsWithThisSecondUnit, job.startX, job.startY,
											job.movementType, job.collisionSize, current.point.x, current.point.y)
									|| !pathableBetween(job.ignoreIntersectionsWithThisUnit,
											job.ignoreIntersectionsWithThisSecondUnit, current.point.x, current.point.y,
											job.movementType, job.collisionSize, lastNode.point.x, lastNode.point.y)
									|| !pathableBetween(job.ignoreIntersectionsWithThisUnit,
											job.ignoreIntersectionsWithThisSecondUnit, job.startX, job.startY,
											job.movementType, job.collisionSize, lastNode.point.x, lastNode.point.y)
									|| !job.allowSmoothing) {
								// Add the point if it's not the first one, or if we can only complete
								// the journey by specifically walking to the first one
								totalPath.addFirst(current.point);
								lastCameFromDirection = current.cameFromDirection;
							}
						}
						if (stepsBackward > this.pathingGridCellCount) {
							new IllegalStateException(
									"PATHING SYSTEM ERROR: The path finding algorithm hit an infinite cycle at or near pt: "
											+ current.cameFrom.point
											+ ".\nThis means the A* search algorithm heuristic 'admissable' constraint was probably violated.\n\nUnit1:"
											+ CUnit.maybeMeaningfulName(job.ignoreIntersectionsWithThisUnit)
											+ "\nUnit2:"
											+ CUnit.maybeMeaningfulName(job.ignoreIntersectionsWithThisSecondUnit))
									.printStackTrace();
							totalPath.clear();
							break;
						}
						stepsBackward++;
					}
					job.queueItem.pathFound(totalPath, simulation);
					this.moveQueue.poll();
					System.out.println("Task " + this.pathfindJobId + " took " + this.totalIterations
							+ " iterations and " + this.totalJobLoops + " job loops!");
					continue JobsLoop;
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
						if (!pathableBetween(job.ignoreIntersectionsWithThisUnit,
								job.ignoreIntersectionsWithThisSecondUnit, current.point.x, current.point.y,
								job.movementType, job.collisionSize, x, y)) {
							tentativeScore += (direction.length) * job.weightForHittingWalls;
						}
						final Node neighbor = job.searchGraph[job.gridMapping.getY(this.pathingGrid, y)][job.gridMapping
								.getX(this.pathingGrid, x)];
						neighbor.touch(this.pathfindJobId);
						if (tentativeScore < neighbor.g) {
							neighbor.cameFrom = current;
							neighbor.cameFromDirection = direction;
							neighbor.g = tentativeScore;
							neighbor.f = tentativeScore + h(neighbor);
							if (!job.openSet.contains(neighbor)) {
								job.openSet.add(neighbor);
							}
						}
					}
				}
				workIterations++;
				this.totalIterations++;
				if (this.totalIterations > 20000) {
					break;
				}
				if (workIterations >= 1500) {
					// breaking jobs loop will implicitly exit without calling pathFound() below
					break JobsLoop;
				}
			}
			job.queueItem.pathFound(Collections.emptyList(), simulation);
			this.moveQueue.poll();
			System.out.println("Task " + this.pathfindJobId + " took " + this.totalIterations + " iterations and "
					+ this.totalJobLoops + " job loops!");
		}
	}

	public static final class PathfindingJob {
		private final CUnit ignoreIntersectionsWithThisUnit;
		private final CUnit ignoreIntersectionsWithThisSecondUnit;
		private final float startX;
		private final float startY;
		private final Point2D.Float goal;
		private final MovementType movementType;
		private final float collisionSize;
		private final boolean allowSmoothing;
		private final CBehaviorMove queueItem;
		private boolean jobStarted;
		public float goalY;
		public float goalX;
		public float weightForHittingWalls;
		Node[][] searchGraph;
		GridMapping gridMapping;
		PriorityQueue<Node> openSet;
		Node start;
		int startGridMinX;
		int startGridMinY;
		int startGridMaxX;
		int startGridMaxY;

		public PathfindingJob(final CUnit ignoreIntersectionsWithThisUnit,
				final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
				final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
				final boolean allowSmoothing, final CBehaviorMove queueItem) {
			this.ignoreIntersectionsWithThisUnit = ignoreIntersectionsWithThisUnit;
			this.ignoreIntersectionsWithThisSecondUnit = ignoreIntersectionsWithThisSecondUnit;
			this.startX = startX;
			this.startY = startY;
			this.goal = goal;
			this.movementType = movementType;
			this.collisionSize = collisionSize;
			this.allowSmoothing = allowSmoothing;
			this.queueItem = queueItem;
			this.jobStarted = false;
		}
	}
}

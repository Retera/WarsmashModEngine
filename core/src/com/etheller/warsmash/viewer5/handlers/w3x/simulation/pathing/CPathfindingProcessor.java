package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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
	private final CPathingNode[][] nodes;
	private final CPathingNode[][] cornerNodes;
	private final CPathingNode[] goalSet = new CPathingNode[4];
	private int goals = 0;
	private int pathfindJobId = 0;
	private int totalIterations = 0;
	private int totalJobLoops = 0;
	private final int pathingGridCellCount;

	/**
	 * Store the shallow copies of the searched Nodes where CPathfindingProcessors
	 * would read/write the properties of the nodes without affecting the whole,
	 * shared searchGraph. (master nodes)
	 */
	private Map<Integer, CPathingNode> metaNodes = new HashMap<Integer, CPathingNode>();

	public CPathfindingProcessor(final PathingGrid pathingGrid, final CWorldCollision worldCollision) {
		this.pathingGrid = pathingGrid;
		this.worldCollision = worldCollision;
		this.nodes = new CPathingNode[pathingGrid.getHeight()][pathingGrid.getWidth()];
		this.cornerNodes = new CPathingNode[pathingGrid.getHeight() + 1][pathingGrid.getWidth() + 1];
		for (int i = 0; i < this.nodes.length; i++) {
			for (int j = 0; j < this.nodes[i].length; j++) {
				this.nodes[i][j] = new CPathingNode(new Point2D.Float(pathingGrid.getWorldX(j), pathingGrid.getWorldY(i)));
			}
		}
		for (int i = 0; i < this.cornerNodes.length; i++) {
			for (int j = 0; j < this.cornerNodes[i].length; j++) {
				this.cornerNodes[i][j] = new CPathingNode(
						new Point2D.Float(pathingGrid.getWorldXFromCorner(j), pathingGrid.getWorldYFromCorner(i)));
			}
		}
		this.pathingGridCellCount = pathingGrid.getWidth() * pathingGrid.getHeight();
	}

	/**
	 * Instead of initializing their own node arrays, CPathfindingProcessor assign a reference
	 * to the "master" node arrays, allowing to save memory space.
	 * 
	 * 
	 * @param pathingGrid
	 * @param worldCollision
	 * @param nodes	
	 * @param cornerNodes
	 */
	public CPathfindingProcessor(final PathingGrid pathingGrid, final CWorldCollision worldCollision, CPathingNode[][] nodes, CPathingNode[][] cornerNodes) {
		this.pathingGrid = pathingGrid;
		this.worldCollision = worldCollision;
		this.nodes = nodes;
		this.cornerNodes = cornerNodes;
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
				ignoreIntersectionsWithThisUnit, ignoreIntersectionsWithThisSecondUnit, movementType);
	}

	public static boolean isCollisionSizeBetterSuitedForCorners(final float collisionSize) {
		return (((2 * (int) collisionSize) / 32) % 2) == 1;
	}

	public double f(final CPathingNode n) {
		return n.g + h(n);
	}

	public double g(final CPathingNode n) {
		return n.g;
	}

	private boolean isGoal(final CPathingNode n) {
		for (int i = 0; i < this.goals; i++) {
			if (n.point == this.goalSet[i].point) {
				return true;
			}
		}
		return false;
	}

	public float h(final CPathingNode n) {
		float bestDistance = 0;
		for (int i = 0; i < this.goals; i++) {
			final float possibleDistance = (float) n.point.distance(this.goalSet[i].point);
			if (possibleDistance > bestDistance) {
				bestDistance = possibleDistance; // always overestimate
			}
		}
		return bestDistance;
	}

	public CPathingNode searchNode(PathfindingJob job, int x, int y){
		int pos = x + y * this.pathingGrid.getWidth();
		if (!this.metaNodes.containsKey(pos)){
			this.metaNodes.put(pos, job.searchGraph[y][x].clone());
		}
		return this.metaNodes.get(pos);
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
				final CPathingNode mostLikelyGoal = this.searchNode(job, goalCellX, goalCellY);
				mostLikelyGoal.touch(this.pathfindJobId);
				final double bestGoalDistance = mostLikelyGoal.point.distance(job.goalX, job.goalY);
				Arrays.fill(this.goalSet, null);
				this.goals = 0;
				for (int i = goalCellX - 1; i <= (goalCellX + 1); i++) {
					for (int j = goalCellY - 1; j <= (goalCellY + 1); j++) {
						if ((j >= 0) && (j <= job.searchGraph.length)) {
							if ((i >= 0) && (i < job.searchGraph[j].length)) {
								final CPathingNode possibleGoal = this.searchNode(job, i, j);
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
				job.openSet = new PriorityQueue<>(new Comparator<CPathingNode>() {
					@Override
					public int compare(final CPathingNode a, final CPathingNode b) {
						return Double.compare(f(a), f(b));
					}
				});

				job.start = this.searchNode(job, startGridX, startGridY);
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
							final CPathingNode possibleNode = this.searchNode(job, cellX, cellY);
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
				CPathingNode current = job.openSet.poll();
				current.touch(this.pathfindJobId);
				if (isGoal(current)) {
					final LinkedList<Point2D.Float> totalPath = new LinkedList<>();
					CDirection lastCameFromDirection = null;

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
					CPathingNode lastNode = null;
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
					this.metaNodes.clear();
					System.out.println("Task " + this.pathfindJobId + " took " + this.totalIterations
							+ " iterations and " + this.totalJobLoops + " job loops!");
					continue JobsLoop;
				}

				for (final CDirection direction : CDirection.VALUES) {
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
						int tmpY = job.gridMapping.getY(this.pathingGrid, y);
						int tmpX = job.gridMapping.getX(this.pathingGrid, x);
						final CPathingNode neighbor = this.searchNode(job, tmpX, tmpY);
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
		CPathingNode[][] searchGraph;
		GridMapping gridMapping;
		PriorityQueue<CPathingNode> openSet;
		CPathingNode start;
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

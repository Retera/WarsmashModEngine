package net.warsmash.pathfinding.l1;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.pathfinding.l1.contour.Contour;
import net.warsmash.pathfinding.l1.planner.PathfinderGrid;
import net.warsmash.pathfinding.l1.util.Point;
import net.warsmash.pathfinding.l1.vertex.IPoint;
import net.warsmash.pathfinding.l1.vertex.Vertex;

public class Geometry {
	public List<IPoint> corners;
	public int[][] grid;

	public Geometry(final List<IPoint> corners, final int[][] grid) {
		this.corners = corners;
		this.grid = grid;
	}

	public boolean stabRay(final double vx, final double vy, final double x) {
		return this.stabBox(vx, vy, x, vy);
	}

	public boolean stabTile(final double x, final double y) {
		return this.stabBox(x, y, x, y);
	}

	public double integrate(final double x, final double y) {
		if ((x < 0) || (y < 0)) {
			return 0;
		}
		return this.grid[(int) Math.min(x, this.grid.length - 1)][(int) Math.min(y, this.grid[0].length - 1)];
	}

	public boolean stabBox(final double ax, final double ay, final double bx, final double by) {
		final double lox = Math.min(ax, bx);
		final double loy = Math.min(ay, by);
		final double hix = Math.max(ax, bx);
		final double hiy = Math.max(ay, by);

		final double s = (integrate(lox - 1, loy - 1) - integrate(lox - 1, hiy) - integrate(hix, loy - 1))
				+ integrate(hix, hiy);

		return s > 0;
	}

	public static int[][] createSummedAreaTable(final int[][] img) {
		final int[][] result = new int[img.length][img[0].length];
		for (int x = 0; x < img.length; x++) {
			int sum = 0;
			for (int y = 0; y < img[0].length; y++) {
				sum += img[x][y];
				if (x == 0) {
					result[x][y] = sum;
				}
				else {
					result[x][y] = result[x - 1][y] + sum;
				}
			}
		}
		return result;
	}

	public static int comparePoint(final IPoint a, final IPoint b) {
		final int d = (int) Math.signum(a.getX() - b.getX());
		if (d != 0) {
			return d;
		}
		return (int) Math.signum(a.getY() - b.getY());
	}

	public static Geometry createGeometry(final PathfinderGrid grid) {
		final List<List<Point>> loops = Contour.getContours(grid.createTranspose(), false);

		// Extract corners
		List<IPoint> corners = new ArrayList<>();
		for (int k = 0; k < loops.size(); ++k) {
			final List<Point> polygon = loops.get(k);
			for (int i = 0; i < polygon.size(); ++i) {
				final Point a = polygon.get(((i + polygon.size()) - 1) % polygon.size());
				final Point b = polygon.get(i);
				final Point c = polygon.get((i + 1) % polygon.size());
				if (Orientation.orient(a, b, c) > 0) {
					double x = 0, y = 0;
					if ((b.x - a.x) != 0) {// TODO double 0 check!! check previous impl
						x = b.x - a.x;
					}
					else {
						x = b.x - c.x;
					}
					x = b.x + Math.min((int) Math.round(x / Math.abs(x)), 0);
					if ((b.y - a.y) != 0) {
						y = b.y - a.y;
					}
					else {
						y = b.y - c.y;
					}
					y = b.y + Math.min((int) Math.round(y / Math.abs(y)), 0);
					final Vertex offset = new Vertex(x, y);
					if ((offset.x >= 0) && (offset.x < grid.getWidth()) && (offset.y >= 0)
							&& (offset.y < grid.getHeight()) && (grid.isPathable((int) offset.x, (int) offset.y))) {
						corners.add(offset);
					}
				}
			}
		}

		// Remove duplicate corners
		corners = Unique.uniq(corners, Geometry::comparePoint, false);

		// Create integral image
		int[][] img = new int[grid.getWidth()][grid.getHeight()];
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				img[x][y] = (grid.isPathable(x, y) ? 0 : 1);
			}
		}
		img = createSummedAreaTable(img);

		// Return resulting geometry
		return new Geometry(corners, img);
	}
}

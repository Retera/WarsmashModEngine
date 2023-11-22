package net.warsmash.pathfinding.l1.contour;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.pathfinding.l1.planner.PathfinderGrid;
import net.warsmash.pathfinding.l1.util.Point;

public class Contour {
	private static List<Segment> getParallelContours(final PathfinderGrid array, final boolean direction) {
		final int n = array.getWidth();
		final int m = n == 0 ? 0 : array.getHeight();
		final List<Segment> contours = new ArrayList<>();
		// Scan top row
		boolean a = false;
		boolean b = false;
		boolean c = false;
		boolean d = false;
		int x0 = 0;
		int i = 0;
		int j = 0;
		for (j = 0; j < m; ++j) {
			b = (!array.isPathable(0, j));
			if (b == a) {
				continue;
			}
			if (a) {
				contours.add(new Segment(x0, j, direction, 0));
			}
			if (b) {
				x0 = j;
			}
			a = b;
		}
		if (a) {
			contours.add(new Segment(x0, j, direction, 0));
		}
		// Scan center
		for (i = 1; i < n; ++i) {
			a = false;
			b = false;
			x0 = 0;
			for (j = 0; j < m; ++j) {
				c = !array.isPathable(i - 1, j);
				d = !array.isPathable(i, j);
				if ((c == a) && (d == b)) {
					continue;
				}
				if (a != b) {
					if (a) {
						contours.add(new Segment(j, x0, direction, i));
					}
					else {
						contours.add(new Segment(x0, j, direction, i));
					}
				}
				if (c != d) {
					x0 = j;
				}
				a = c;
				b = d;
			}
			if (a != b) {
				if (a) {
					contours.add(new Segment(j, x0, direction, i));
				}
				else {
					contours.add(new Segment(x0, j, direction, i));
				}
			}
		}
		// Scan bottom row
		a = false;
		x0 = 0;
		for (j = 0; j < m; ++j) {
			b = !array.isPathable(n - 1, j);
			if (b == a) {
				continue;
			}
			if (a) {
				contours.add(new Segment(j, x0, direction, n));
			}
			if (b) {
				x0 = j;
			}
			a = b;
		}
		if (a) {
			contours.add(new Segment(j, x0, direction, n));
		}
		return contours;
	}

	public static List<ContourVertex> getVertices(final List<Segment> contours) {
		final int defaultVerticesSize = contours.size() * 2;
		final List<ContourVertex> vertices = new ArrayList<>(defaultVerticesSize);
		for (int nullEntryCount = 0; nullEntryCount < defaultVerticesSize; nullEntryCount++) {
			vertices.add(null);
		}
		for (int i = 0; i < contours.size(); ++i) {
			final Segment h = contours.get(i);
			if (!h.direction) {
				vertices.set(2 * i, new ContourVertex(h.start, h.height, h, 0));
				vertices.set((2 * i) + 1, new ContourVertex(h.end, h.height, h, 1));
			}
			else {
				vertices.set(2 * i, new ContourVertex(h.height, h.start, h, 0));
				vertices.set((2 * i) + 1, new ContourVertex(h.height, h.end, h, 1));
			}
		}
		return vertices;
	}

	public static List<Point> walk(Segment v, final boolean clockwise) {
		final List<Point> result = new ArrayList<>();
		while (!v.visited) {
			v.visited = true;
			if (v.direction) {
				result.add(new Point(v.height, v.end));
			}
			else {
				result.add(new Point(v.start, v.height));
			}
			if (clockwise) {
				v = v.next;
			}
			else {
				v = v.prev;
			}
		}
		return result;
	}

	private static int compareVertex(final ContourVertex a, final ContourVertex b) {
		double d = a.x - b.x;
		// TODO check != 0 impl from original source material
		if (d != 0) {
			return (int) Math.signum(d);
		}
		d = a.y - b.y;
		if (d != 0) {
			return (int) Math.signum(d);
		}
		return a.orientation - b.orientation;
	}

	public static List<List<Point>> getContours(final PathfinderGrid array, final boolean clockwise) {
		// First extract horizontal contours and vertices
		final List<Segment> horizontalContours = getParallelContours(array, false);
		final List<ContourVertex> horizontalVertices = getVertices(horizontalContours);
		horizontalVertices.sort(Contour::compareVertex);

		// Extract vertical contours and vertices
		final List<Segment> verticalContours = getParallelContours(array.createTranspose(), true);
		final List<ContourVertex> verticalVertices = getVertices(verticalContours);
		verticalVertices.sort(Contour::compareVertex);

		// Glue horizontal and vertical vertices together
		final int numVertices = horizontalVertices.size();
		for (int i = 0; i < numVertices; ++i) {
			final ContourVertex horizontal = horizontalVertices.get(i);
			final ContourVertex vertical = verticalVertices.get(i);
			if (horizontal.orientation != 0) {
				horizontal.segment.next = vertical.segment;
				vertical.segment.prev = horizontal.segment;
			}
			else {
				horizontal.segment.prev = vertical.segment;
				vertical.segment.next = horizontal.segment;
			}
		}

		// Unwrap loops
		final List<List<Point>> loops = new ArrayList<>();
		for (int i = 0; i < horizontalContours.size(); ++i) {
			final Segment horizontal = horizontalContours.get(i);
			if (!horizontal.visited) {
				loops.add(walk(horizontal, clockwise));
			}
		}

		return loops;
	}
}

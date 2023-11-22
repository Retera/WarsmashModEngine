package net.warsmash.pathfinding.l1;

import java.util.List;

public class BSearch {
	public static interface SearchIntervalFunc<T, Y> {
		int call(T t, Y y);
	}

	private static <T, Y> int searchInternal(final List<T> a, int l, int h, final Y y,
			final SearchIntervalFunc<T, Y> c) {
		int i = l - 1;
		while (l <= h) {
			final int m = (int) ((long) (l + h) >> 1);
			final T x = a.get(m);
			if (c.call(x, y) < 0) {
				i = m;
				l = m + 1;
			}
			else {
				h = m - 1;
			}
		}
		return i;
	}

	public static <T, Y> int search(final List<T> a, final Y y, final SearchIntervalFunc<T, Y> c) {
		return searchInternal(a, 0, a.size() - 1, y, c);
	}
}

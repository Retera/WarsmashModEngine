package net.warsmash.pathfinding.l1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Unique {
	public static <T> List<T> uniquePred(final List<T> list, final Comparator<T> compare) {
		int ptr = 1;
		final int len = list.size();
		T a = list.get(0);
		T b = list.get(0);
		for (int i = 1; i < len; ++i) {
			b = a;
			a = list.get(i);
			if (compare.compare(a, b) != 0) {
				if (i == ptr) {
					ptr++;
					continue;
				}
				list.set(ptr++, a);
			}
		}
		// TODO this is probably a wasteful copy, why didn't we just fill
		// this list while iterating the first time?
		return list.subList(0, ptr);
	}

	public static <T> List<T> uniq(List<T> list, final Comparator<T> compare, final boolean sorted) {
		if (list.size() == 0) {
			return list;
		}
		if (!sorted) {
			list = new ArrayList<T>(list);
			list.sort(compare);
		}
		return uniquePred(list, compare);
	}
}

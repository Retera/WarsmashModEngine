package net.warsmash.pathfinding.l1.util;

import net.warsmash.pathfinding.l1.planner.PathfinderGrid;

public class GridUtil {
	public static int[][] transpose(final int[][] array) {
		final int originalWidth = array.length;
		final int originalHeight = array[0].length;
		final int[][] transposedArray = new int[originalHeight][originalWidth];
		for (int i = 0; i < originalWidth; i++) {
			for (int j = 0; j < originalHeight; j++) {
				transposedArray[j][i] = array[i][j];
			}
		}
		return transposedArray;
	}

	public static byte[][] transpose(final byte[][] array) {
		final int originalWidth = array.length;
		final int originalHeight = array[0].length;
		final byte[][] transposedArray = new byte[originalHeight][originalWidth];
		for (int i = 0; i < originalWidth; i++) {
			for (int j = 0; j < originalHeight; j++) {
				transposedArray[j][i] = array[i][j];
			}
		}
		return transposedArray;
	}

	public static PathfinderGrid transpose(final PathfinderGrid array) {
		return new TransposedGrid(array);
	}

	private static final class TransposedGrid implements PathfinderGrid {
		private final PathfinderGrid delegate;

		public TransposedGrid(final PathfinderGrid delegate) {
			this.delegate = delegate;
		}

		@Override
		public int getWidth() {
			return delegate.getHeight();
		}

		@Override
		public int getHeight() {
			return delegate.getWidth();
		}

		@Override
		public boolean isPathable(final int x, final int y) {
			return delegate.isPathable(y, x);
		}

		@Override
		public PathfinderGrid createTranspose() {
			return delegate;
		}

	}
}

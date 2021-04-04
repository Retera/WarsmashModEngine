package com.etheller.warsmash.viewer5;

public interface PathSolver {
	SolvedPath solve(String src, Object solverParams);

	// We generally just use the default path solver.
	// These things were apparently meant to work as the Ghostwolf's JavaScript's
	// equivalent of the DataSource interface you will find in this Java repo.
	// But I did not know that and wasn't sure what it was for, so I kept it in the
	// port of his code. Eventually it should be removed.
	public static final PathSolver DEFAULT = new PathSolver() {
		@Override
		public SolvedPath solve(final String src, final Object solverParams) {
			final int dotIndex = src.lastIndexOf('.');
			if ((dotIndex == -1)) {
				throw new IllegalStateException("unable to resolve: " + src);
			}
			return new SolvedPath(src, src.substring(dotIndex), true);
		}
	};
	public static final PathSolver NOFETCH = new PathSolver() {
		@Override
		public SolvedPath solve(final String src, final Object solverParams) {
			final int dotIndex = src.lastIndexOf('.');
			if ((dotIndex == -1)) {
				throw new IllegalStateException("unable to resolve: " + src);
			}
			return new SolvedPath(src, src.substring(dotIndex), false);
		}
	};
}

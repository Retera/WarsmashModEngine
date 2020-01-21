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
			return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
		}
	};
}

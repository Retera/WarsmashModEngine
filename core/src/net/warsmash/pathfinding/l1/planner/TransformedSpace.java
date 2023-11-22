package net.warsmash.pathfinding.l1.planner;

public interface TransformedSpace {
	double getUnprojectedX(double x, double y);

	double getUnprojectedY(double x, double y);

	TransformedSpace IDENTITY = new TransformedSpace() {
		@Override
		public double getUnprojectedX(final double x, final double y) {
			return x;
		}

		@Override
		public double getUnprojectedY(final double x, final double y) {
			return y;
		}
	};
}

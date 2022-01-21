package net.warsmash.nio.util;

public interface ExceptionListener {
	void caught(Exception e);

	ExceptionListener THROW_RUNTIME = new ExceptionListener() {
		@Override
		public void caught(final Exception e) {
			throw new RuntimeException(e);
		}
	};
}

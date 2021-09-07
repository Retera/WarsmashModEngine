package com.etheller.warsmash.viewer5;

public class AudioContext {
	private boolean running = false;
	public Listener listener;
	public AudioDestination destination;

	public AudioContext(final Listener listener, final AudioDestination destination) {
		this.listener = listener;
		this.destination = destination;
	}

	public void suspend() {
		this.running = false;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void resume() {
		this.running = true;
	}

	public static interface Listener {

		float getX();

		float getY();

		float getZ();

		public void setPosition(final float x, final float y, final float z);

		public void setOrientation(final float forwardX, final float forwardY, final float forwardZ, final float upX,
				final float upY, final float upZ);

		boolean is3DSupported();

		Listener DO_NOTHING = new Listener() {
			private float x;
			private float y;
			private float z;

			@Override
			public void setPosition(final float x, final float y, final float z) {
				this.x = x;
				this.y = y;
				this.z = z;
			}

			@Override
			public float getX() {
				return x;
			}

			@Override
			public float getY() {
				return y;
			}

			@Override
			public float getZ() {
				return z;
			}

			@Override
			public void setOrientation(final float forwardX, final float forwardY, final float forwardZ,
					final float upX, final float upY, final float upZ) {

			}

			@Override
			public boolean is3DSupported() {
				return false;
			}
		};
	}

	public AudioPanner createPanner() {
		return createPanner(true);
	}

	public AudioPanner createPanner(final boolean stopWhenOutOfRange) {
		if (!stopWhenOutOfRange) {
			return new AudioPanner(this.listener) {
				@Override
				public void connect(final AudioDestination destination) {
				}

				@Override
				public boolean isWithinListenerDistance() {
					return true;
				}
			};
		}
		else {
			return new AudioPanner(this.listener) {
				@Override
				public void connect(final AudioDestination destination) {
				}
			};
		}
	}

	public AudioBufferSource createBufferSource() {
		return new AudioBufferSource();
	}

}

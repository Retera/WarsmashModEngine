package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

public class EmittedObjectUpdater {
	final List<EmittedObject> objects;
	private int alive;

	public EmittedObjectUpdater() {
		this.objects = new ArrayList<>();
		this.alive = 0;
	}

	public void add(final EmittedObject object) {
		this.objects.add(object);
		this.alive++;
	}

	public void update(final float dt) {
		for (int i = 0; i < this.alive; i++) {
			final EmittedObject object = this.objects.get(i);

			object.update(dt);

			if (object.health <= 0) {
				this.alive -= 1;

				object.emitter.kill(object);

				// Swap between this object and the last living object.
				// Decrement the iterator so the swapped object is updated this frame.
				if (i != this.alive) {
					this.objects.set(i, this.objects.remove(this.alive));
					i -= 1;
				}
				else {
					this.objects.remove(this.alive);
				}
			}
		}
	}
}

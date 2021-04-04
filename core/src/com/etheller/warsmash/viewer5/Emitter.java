package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

public abstract class Emitter<MODEL_INSTANCE extends ModelInstance, EMITTED_OBJECT extends EmittedObject<MODEL_INSTANCE, ? extends Emitter<MODEL_INSTANCE, EMITTED_OBJECT>>>
		implements UpdatableObject {

	public final MODEL_INSTANCE instance;
	public final List<EMITTED_OBJECT> objects;
	public int alive;
	protected float currentEmission;

	public Emitter(final MODEL_INSTANCE instance) {
		this.instance = instance;
		this.objects = new ArrayList<>();
		this.alive = 0;
		this.currentEmission = 0;
	}

	public final EMITTED_OBJECT emitObject(final int flags) {
		if (this.alive == this.objects.size()) {
			this.objects.add(this.createObject());
		}

		final EMITTED_OBJECT object = this.objects.get(this.alive);

		object.index = this.alive;

		object.bind(flags);

		this.alive += 1;
		this.currentEmission -= 1;

		this.instance.scene.emitterObjectUpdater.add(object);

		return object;
	}

	@Override
	public void update(final float dt, final boolean objectVisible) {
		if (!objectVisible) {
			return;
		}
		this.updateEmission(dt);

		final float currentEmission = this.currentEmission;

		if (currentEmission >= 1) {
			for (int i = 0; i < currentEmission; i += 1) {
				this.emit();
			}
		}
	}

	public void kill(final EMITTED_OBJECT object) {
		this.alive -= 1;

		final EMITTED_OBJECT otherObject = this.objects.get(this.alive);
		if (object.index == -1) {
			System.err.println("bad");
		}
		this.objects.set(object.index, otherObject);
		this.objects.set(this.alive, object);

		otherObject.index = object.index;
		object.index = -1;
	}

	public final void clear() {
		for (int i = 0; i < this.alive; i++) {
			this.objects.get(i).health = 0;
		}
		this.currentEmission = 0;
	}

	protected abstract void updateEmission(float dt);

	protected abstract void emit();

	protected abstract EMITTED_OBJECT createObject();
}

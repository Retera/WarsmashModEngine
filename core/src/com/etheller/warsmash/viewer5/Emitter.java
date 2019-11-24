package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.EmitterObject;

public abstract class Emitter {

	private final ModelInstance instance;
	private final EmitterObject emitterObject;
	private final List<EmittedObject> objects;
	private int alive;
	private int currentEmission;

	public Emitter(final ModelInstance instance, final EmitterObject emitterObject) {
		this.instance = instance;
		this.emitterObject = emitterObject;
		this.objects = new ArrayList<>();
		this.alive = 0;
		this.currentEmission = 0;
	}

	public final EmittedObject emitObject(final int flags) {
		if (this.alive == this.objects.size()) {
			this.objects.add(this.createObject());
		}

		final EmittedObject object = this.objects.get(this.alive);
		object.index = this.alive;
		object.bind(flags);

		this.alive += 1;
		this.currentEmission -= 1;

		this.instance.scene.emitterObjectUpdater.add(object);

		return object;
	}

	public final void update(final float dt) {
		this.updateEmission(dt);

		final int currentEmission = this.currentEmission;
		if (currentEmission >= 1) {
			for (int i = 0; i < currentEmission; i += 1) {
				this.emit();
			}
		}
	}

	public final void kill(final EmittedObject object) {
		this.alive -= 1;

		final EmittedObject otherObject = this.objects.get(this.alive);
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

	protected abstract EmittedObject createObject();
}

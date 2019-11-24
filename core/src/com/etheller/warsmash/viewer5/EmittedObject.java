package com.etheller.warsmash.viewer5;

public abstract class EmittedObject {
	abstract void update(float dt);

	public float health;
	public Emitter emitter;
	public int index;

	protected abstract void bind(int flags);
}

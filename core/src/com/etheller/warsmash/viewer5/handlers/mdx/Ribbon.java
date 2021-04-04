package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.EmittedObject;

public class Ribbon extends EmittedObject<MdxComplexInstance, RibbonEmitter> {
	private static final float[] vectorHeap = new float[3];
	private static final Vector3 belowHeap = new Vector3();
	private static final Vector3 aboveHeap = new Vector3();
	private static final float[] colorHeap = new float[3];
	private static final float[] alphaHeap = new float[1];
	private static final long[] slotHeap = new long[1];

	public float[] vertices = new float[6];
	public byte[] color = new byte[4];
	public int slot;
	public Ribbon prev;
	public Ribbon next;

	public Ribbon(final RibbonEmitter emitter) {
		super(emitter);
	}

	@Override
	protected void bind(final int flags) {
		final RibbonEmitter emitter = this.emitter;
		final MdxComplexInstance instance = emitter.instance;
		final RibbonEmitterObject emitterObject = emitter.emitterObject;
		final MdxNode node = instance.nodes[emitterObject.index];
		final Vector3 pivot = node.pivot;
		final float x = pivot.x, y = pivot.y, z = pivot.z;
		final Matrix4 worldMatrix = node.worldMatrix;
		final float[] vertices = this.vertices;

		this.health = emitter.emitterObject.lifeSpan;

		emitterObject.getHeightBelow(vectorHeap, instance.sequence, instance.frame, instance.counter);
		belowHeap.set(vectorHeap);
		emitterObject.getHeightAbove(vectorHeap, instance.sequence, instance.frame, instance.counter);
		aboveHeap.set(vectorHeap);

		belowHeap.y = y - belowHeap.x;
		belowHeap.x = x;
		belowHeap.z = z;

		aboveHeap.y = y + aboveHeap.x;
		aboveHeap.x = x;
		aboveHeap.z = z;

		belowHeap.prj(worldMatrix);
		aboveHeap.prj(worldMatrix);

		vertices[0] = aboveHeap.x;
		vertices[1] = aboveHeap.y;
		vertices[2] = aboveHeap.z;
		vertices[3] = belowHeap.x;
		vertices[4] = belowHeap.y;
		vertices[5] = belowHeap.z;
	}

	@Override
	public void update(final float dt) {
		this.health -= dt;

		if (this.health > 0) {
			final RibbonEmitter emitter = this.emitter;
			final MdxComplexInstance instance = emitter.instance;
			final RibbonEmitterObject emitterObject = emitter.emitterObject;
			final byte[] color = this.color;
			final float[] vertices = this.vertices;
			final float gravity = emitterObject.gravity * dt * dt;

			emitterObject.getColor(colorHeap, instance.sequence, instance.frame, instance.counter);
			emitterObject.getAlpha(alphaHeap, instance.sequence, instance.frame, instance.counter);
			emitterObject.getTextureSlot(slotHeap, instance.sequence, instance.frame, instance.counter);

			vertices[1] -= gravity;
			vertices[4] -= gravity;

			color[0] = (byte) (colorHeap[0] * 255);
			color[1] = (byte) (colorHeap[1] * 255);
			color[2] = (byte) (colorHeap[2] * 255);
			color[3] = (byte) (alphaHeap[0] * 255);

			this.slot = (int) slotHeap[0];
		}
	}

}

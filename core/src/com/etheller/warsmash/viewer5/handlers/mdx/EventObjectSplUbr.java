package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.EmittedObject;

public class EventObjectSplUbr
		extends EmittedObject<MdxComplexInstance, EventObjectEmitter<EventObjectEmitterObject, EventObjectSplUbr>> {
	private static final Vector3 vertexHeap = new Vector3();

	public final float[] vertices = new float[12];

	public EventObjectSplUbr(final EventObjectEmitter<EventObjectEmitterObject, EventObjectSplUbr> emitter) {
		super(emitter);
	}

	@Override
	protected void bind(final int flags) {
		final EventObjectEmitter<EventObjectEmitterObject, EventObjectSplUbr> emitter = this.emitter;
		final MdxComplexInstance instance = emitter.instance;
		final EventObjectEmitterObject emitterObject = emitter.emitterObject;
		final float[] vertices = this.vertices;
		final float scale = emitterObject.scale;
		final MdxNode node = instance.nodes[emitterObject.index];
		final Matrix4 worldMatrix = node.worldMatrix;

		this.health = emitterObject.lifeSpan;

		vertexHeap.x = scale;
		vertexHeap.y = scale;
		vertexHeap.prj(worldMatrix);
		vertices[0] = vertexHeap.x;
		vertices[1] = vertexHeap.y;
		vertices[2] = vertexHeap.z;

		vertexHeap.x = -scale;
		vertexHeap.y = scale;
		vertexHeap.prj(worldMatrix);
		vertices[3] = vertexHeap.x;
		vertices[4] = vertexHeap.y;
		vertices[5] = vertexHeap.z;

		vertexHeap.x = -scale;
		vertexHeap.y = -scale;
		vertexHeap.prj(worldMatrix);
		vertices[6] = vertexHeap.x;
		vertices[7] = vertexHeap.y;
		vertices[8] = vertexHeap.z;

		vertexHeap.x = scale;
		vertexHeap.y = -scale;
		vertexHeap.prj(worldMatrix);
		vertices[9] = vertexHeap.x;
		vertices[10] = vertexHeap.y;
		vertices[11] = vertexHeap.z;

	}

	@Override
	public void update(final float dt) {
		this.health -= dt;
	}
}

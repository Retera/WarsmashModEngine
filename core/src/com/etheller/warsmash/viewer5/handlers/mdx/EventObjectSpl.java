package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.EmittedObject;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.IVec3;

public class EventObjectSpl
		extends EmittedObject<MdxComplexInstance, EventObjectEmitter<EventObjectEmitterObject, EventObjectSpl>> {
	private static final Vector3 vertexHeap = new Vector3();

	public final float[] vertices = new float[12];
	public final IVec3 normal = new IVec3(0, 0, 0);

	public EventObjectSpl(final EventObjectEmitter<EventObjectEmitterObject, EventObjectSpl> emitter) {
		super(emitter);
	}

	@Override
	protected void bind(final int flags) {
		final EventObjectEmitter<EventObjectEmitterObject, EventObjectSpl> emitter = this.emitter;
		final MdxComplexInstance instance = emitter.instance;
		final EventObjectEmitterObject emitterObject = emitter.emitterObject;
		final float[] vertices = this.vertices;
		final float scale = emitterObject.scale;
		final MdxNode node = instance.nodes[emitterObject.index];
		final Matrix4 worldMatrix = node.worldMatrix;
		final Vector3 pivot = node.pivot;
		final Quaternion worldRotation = node.worldRotation;
		final Vector3 worldScale = node.worldScale;

		this.health = emitterObject.lifeSpan;

		vertexHeap.x = pivot.x + (scale / worldScale.x);
		vertexHeap.y = pivot.y + (scale / worldScale.y);
		vertexHeap.z = pivot.z;
		vertexHeap.prj(worldMatrix);
		vertices[0] = vertexHeap.x;
		vertices[1] = vertexHeap.y;
		vertices[2] = vertexHeap.z;

		vertexHeap.x = pivot.x - (scale / worldScale.x);
		vertexHeap.y = pivot.y + (scale / worldScale.y);
		vertexHeap.z = pivot.z;
		vertexHeap.prj(worldMatrix);
		vertices[3] = vertexHeap.x;
		vertices[4] = vertexHeap.y;
		vertices[5] = vertexHeap.z;

		vertexHeap.x = pivot.x - (scale / worldScale.x);
		vertexHeap.y = pivot.y - (scale / worldScale.y);
		vertexHeap.z = pivot.z;
		vertexHeap.prj(worldMatrix);
		vertices[6] = vertexHeap.x;
		vertices[7] = vertexHeap.y;
		vertices[8] = vertexHeap.z;

		vertexHeap.x = pivot.x + (scale / worldScale.x);
		vertexHeap.y = pivot.y - (scale / worldScale.y);
		vertexHeap.z = pivot.z;
		vertexHeap.prj(worldMatrix);
		vertices[9] = vertexHeap.x;
		vertices[10] = vertexHeap.y;
		vertices[11] = vertexHeap.z;

		vertexHeap.set(0, 0, 1);
		worldRotation.transform(vertexHeap);
		vertexHeap.nor();
		normal.x = (byte) (((vertexHeap.x + 1.0) / 2.0) * 255);
		normal.y = (byte) (((vertexHeap.y + 1.0) / 2.0) * 255);
		normal.z = (byte) (((vertexHeap.z + 1.0) / 2.0) * 255);

	}

	@Override
	public void update(final float dt) {
		this.health -= dt;
	}
}

package com.etheller.warsmash.viewer5.handlers.mdx;

import java.nio.FloatBuffer;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SceneLightInstance;
import com.etheller.warsmash.viewer5.UpdatableObject;

public class LightInstance implements UpdatableObject, SceneLightInstance {
	private static final Matrix4 matrix4Heap = new Matrix4();
	private static final Vector3 vector3Heap = new Vector3();
	private static final float[] vectorHeap = new float[3];
	private static final float[] scalarHeap = new float[1];
	protected final MdxNode node;
	protected final Light light;
	private boolean visible;
	private boolean loadedInScene;
	private final MdxComplexInstance instance;

	public LightInstance(final MdxComplexInstance instance, final Light light) {
		this.instance = instance;
		this.node = instance.nodes[light.index];
		this.light = light;
	}

	public void bind(final int offset, final FloatBuffer floatBuffer) {
		final int sequence = this.instance.sequence;
		final int frame = this.instance.frame;
		final int counter = this.instance.counter;
		this.light.getAttenuationStart(scalarHeap, sequence, frame, counter);
		final float attenuationStart = scalarHeap[0];
		this.light.getAttenuationEnd(scalarHeap, sequence, frame, counter);
		final float attenuationEnd = scalarHeap[0];
		this.light.getIntensity(scalarHeap, sequence, frame, counter);
		final float intensity = scalarHeap[0];
		this.light.getColor(vectorHeap, sequence, frame, counter);
		final float colorRed = vectorHeap[0];
		final float colorGreen = vectorHeap[1];
		final float colorBlue = vectorHeap[2];
		this.light.getAmbientIntensity(scalarHeap, sequence, frame, counter);
		final float ambientIntensity = scalarHeap[0];
		this.light.getAmbientColor(vectorHeap, sequence, frame, counter);
		final float ambientColorRed = vectorHeap[0];
		final float ambientColorGreen = vectorHeap[1];
		final float ambientColorBlue = vectorHeap[2];
		switch (this.light.getType()) {
		case AMBIENT:
		case OMNIDIRECTIONAL:
			floatBuffer.put(offset, this.node.worldLocation.x);
			floatBuffer.put(offset + 1, this.node.worldLocation.y);
			floatBuffer.put(offset + 2, this.node.worldLocation.z);
			break;
		case DIRECTIONAL:
			vector3Heap.set(0, 0, 1);
			this.node.localRotation.transform(vector3Heap);
			vector3Heap.nor();
			floatBuffer.put(offset, vector3Heap.x);
			floatBuffer.put(offset + 1, vector3Heap.y);
			floatBuffer.put(offset + 2, vector3Heap.z);
			break;
		}
		// I use some padding to make the memory structure of the light be a 4x4 float
		// grid, when somebody who actually has experience with this stuff comes along
		// to change this to something smart, maybe they'll remove the padding if it's
		// not necessary. I'm basing how I implement this on how Ghostwolf did
		// BoneTexture
		floatBuffer.put(offset + 3, this.instance.worldLocation.z);
		floatBuffer.put(offset + 4, this.light.getType().ordinal());
		floatBuffer.put(offset + 5, attenuationStart);
		floatBuffer.put(offset + 6, attenuationEnd);
		floatBuffer.put(offset + 7, 0);
		floatBuffer.put(offset + 8, colorRed);
		floatBuffer.put(offset + 9, colorGreen);
		floatBuffer.put(offset + 10, colorBlue);
		floatBuffer.put(offset + 11, intensity);
		floatBuffer.put(offset + 12, ambientColorRed);
		floatBuffer.put(offset + 13, ambientColorGreen);
		floatBuffer.put(offset + 14, ambientColorBlue);
		floatBuffer.put(offset + 15, ambientIntensity);
	}

	@Override
	public void update(final float dt, final boolean visible) {
	}

	public void update(final Scene scene) {
		this.light.getVisibility(scalarHeap, this.instance.sequence, this.instance.frame, this.instance.counter);
		this.visible = scalarHeap[0] > 0;
		updateVisibility(scene, this.visible);
	}

	public void remove(final Scene scene) {
		updateVisibility(scene, false);
	}

	private void updateVisibility(final Scene scene, final boolean visible) {
		if (scene != null) {
			if (this.loadedInScene != visible) {
				if (visible) {
					scene.addLight(this);
				}
				else {
					scene.removeLight(this);
				}
				this.loadedInScene = visible;
			}
		}
	}
}

package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.AudioBufferSource;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.AudioPanner;
import com.etheller.warsmash.viewer5.EmittedObject;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;

public class EventObjectSnd extends EmittedObject<MdxComplexInstance, EventObjectSndEmitter> {
	public EventObjectSnd(final EventObjectSndEmitter emitter) {
		super(emitter);
	}

	@Override
	protected void bind(final int flags) {
		final EventObjectSndEmitter emitter = this.emitter;
		final MdxComplexInstance instance = emitter.instance;
		final ModelViewer viewer = instance.model.viewer;
		final Scene scene = instance.scene;

		// Is audio enabled both viewer-wide and in this scene?
		if (viewer.audioEnabled && scene.audioEnabled) {
			final EventObjectEmitterObject emitterObject = emitter.emitterObject;
			final MdxNode node = instance.nodes[emitterObject.index];
			final AudioContext audioContext = scene.audioContext;
			final List<Sound> decodedBuffers = emitterObject.decodedBuffers;
			if (decodedBuffers.isEmpty()) {
				return;
			}
			final AudioPanner panner = audioContext.createPanner();
			final AudioBufferSource source = audioContext.createBufferSource();
			final Vector3 location = node.worldLocation;

			// Panner settings
			panner.setPosition(location.x, location.y, location.z);
			panner.setDistances(emitterObject.distanceCutoff, emitterObject.minDistance);
			panner.connect(audioContext.destination);

			// Source.
			source.buffer = decodedBuffers.get((int) (Math.random() * decodedBuffers.size()));
			source.connect(panner);

			// Make a sound.
			source.start(0, emitterObject.volume,
					(emitterObject.pitch + ((float) Math.random() * emitterObject.pitchVariance * 2))
							- emitterObject.pitchVariance,
					false);
		}
	}

	@Override
	public void update(final float dt) {

	}
}

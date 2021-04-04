package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.EmittedObject;
import com.etheller.warsmash.viewer5.Scene;

public class EventObjectSpn extends EmittedObject<MdxComplexInstance, EventObjectSpnEmitter> {
	private final MdxComplexInstance internalInstance;

	public EventObjectSpn(final EventObjectSpnEmitter emitter) {
		super(emitter);

		final EventObjectEmitterObject emitterObject = emitter.emitterObject;
		final MdxModel internalModel = emitterObject.internalModel;

		this.internalInstance = (MdxComplexInstance) internalModel.addInstance();
	}

	@Override
	protected void bind(final int flags) {
		final EventObjectSpnEmitter emitter = this.emitter;
		final MdxComplexInstance instance = emitter.instance;
		final Scene scene = instance.scene;
		final MdxNode node = instance.nodes[emitter.emitterObject.index];
		final MdxComplexInstance internalInstance = this.internalInstance;

		internalInstance.setSequence(0);
		internalInstance.setTransformation(node.worldLocation, node.worldRotation, node.worldScale);
		internalInstance.show();

		scene.addInstance(internalInstance);

		this.health = 1;
	}

	@Override
	public void update(final float dt) {
		final MdxComplexInstance instance = this.internalInstance;
		final MdxModel model = (MdxModel) instance.model;

		// Once the sequence finishes, this event object dies
		if (model.getSequences().isEmpty()) {
			System.err.println("NO SEQ FOR " + model.name);
		}
		if (instance.frame >= model.getSequences().get(0).getInterval()[1]) {
			this.health = 0;

			instance.hide();
		}
	}

}

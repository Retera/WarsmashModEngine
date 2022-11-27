package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

/**
 * Stores render world missile data. This should probably be renamed later not to have
 * "UI" in the name.
 */
public class EffectAttachmentUIMissile extends EffectAttachmentUI {

	private float arc;

	public EffectAttachmentUIMissile(String modelPath, List<String> attachmentPoint, float arc) {
		super(modelPath, attachmentPoint);
		this.arc = arc;
	}

	public float getArc() {
		return arc;
	}
}

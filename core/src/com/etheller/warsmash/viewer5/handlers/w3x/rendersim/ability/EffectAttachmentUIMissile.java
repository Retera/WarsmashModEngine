package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

/**
 * Stores render world missile data. This should probably be renamed later not to have
 * "UI" in the name.
 */
public class EffectAttachmentUIMissile extends EffectAttachmentUI {

	private float arc;
	private float speed;
	private boolean homing;

	public EffectAttachmentUIMissile(String modelPath, List<String> attachmentPoint, float arc, float speed, boolean homing) {
		super(modelPath, attachmentPoint);
		this.arc = arc;
		this.speed = speed;
		this.homing = homing;
	}

	public float getArc() {
		return arc;
	}

	public float getSpeed() {
		return speed;
	}

	public boolean isHoming() {
		return homing;
	}
}

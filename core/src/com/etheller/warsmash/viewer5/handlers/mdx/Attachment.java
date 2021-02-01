package com.etheller.warsmash.viewer5.handlers.mdx;

import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxAttachment;

public class Attachment extends GenericObject {
	protected String name;
	protected final String path;
	protected final int attachmentId;
	protected MdxModel internalModel;

	public Attachment(final MdxModel model, final MdlxAttachment attachment, final int index) {
		super(model, attachment, index);

		final String path = attachment.getPath().toLowerCase().replace(".mdl", ".mdx");

		this.name = attachment.getName().toLowerCase();
		this.path = path;
		this.attachmentId = attachment.getAttachmentId();
		this.internalModel = null;

		// Second condition is against custom resources using arbitrary paths
		if (!path.equals("") && (path.indexOf(".mdx") != -1)) {
			this.internalModel = (MdxModel) model.viewer.load(path, model.pathSolver, model.solverParams);
		}
	}

	@Override
	public int getVisibility(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KATV.getWar3id(), sequence, frame, counter, 1);
	}

	public String getName() {
		return this.name;
	}

	public int getAttachmentId() {
		return this.attachmentId;
	}
}

package com.etheller.warsmash.fdfparser;

import com.etheller.warsmash.fdfparser.FDFParser.BackdropBackgroundElementContext;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefintion;

public class FrameDefinitionFieldVisitor extends FDFBaseVisitor<Void> {
	private final FrameDefintion frameDefintion;

	public FrameDefinitionFieldVisitor(final FrameDefintion frameDefintion,
			final FrameDefinitionVisitor frameDefinitionVisitor) {
		this.frameDefintion = frameDefintion;
	}

	@Override
	public Void visitBackdropBackgroundElement(final BackdropBackgroundElementContext ctx) {
		this.frameDefintion.set("BackdropBackground", value);
		return null;
	}
}

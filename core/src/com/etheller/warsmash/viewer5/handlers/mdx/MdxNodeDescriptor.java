package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.Descriptor;

public class MdxNodeDescriptor implements Descriptor<MdxNode> {
	public static final MdxNodeDescriptor INSTANCE = new MdxNodeDescriptor();

	@Override
	public MdxNode create() {
		return new MdxNode();
	}

}

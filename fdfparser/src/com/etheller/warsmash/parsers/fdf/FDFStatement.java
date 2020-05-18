package com.etheller.warsmash.parsers.fdf;

import com.etheller.warsmash.parsers.fdf.templates.FrameTemplateEnvironment;

public interface FDFStatement {
	void loadTemplate(FrameTemplateEnvironment frameDef);
}

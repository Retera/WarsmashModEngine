package com.etheller.warsmash.parsers.fdf;

import java.util.List;

import com.etheller.warsmash.parsers.fdf.templates.FrameTemplateEnvironment;

public class FDFStringListStatement implements FDFStatement {
	private final List<FDFNamedString> namedStrings;

	public FDFStringListStatement(final List<FDFNamedString> namedStrings) {
		this.namedStrings = namedStrings;
	}

	@Override
	public void loadTemplate(final FrameTemplateEnvironment frameDef) {
		for (final FDFNamedString string : this.namedStrings) {
			frameDef.addDecoratedString(string.getName(), string.getValue());
		}
	}
}

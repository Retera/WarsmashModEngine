package com.etheller.warsmash.fdfparser;

import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.etheller.warsmash.fdfparser.FDFParser.IncludeStatementContext;
import com.etheller.warsmash.fdfparser.FDFParser.StringListStatementContext;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;

public class FrameDefinitionVisitor extends FDFBaseVisitor<Void> {
	private final FrameTemplateEnvironment templates;
	private final FDFParserBuilder fdfParserBuilder;

	public FrameDefinitionVisitor(final FrameTemplateEnvironment templates, final FDFParserBuilder fdfParserBuilder) {
		this.templates = templates;
		this.fdfParserBuilder = fdfParserBuilder;
	}

	@Override
	public Void visitStringListStatement(final StringListStatementContext ctx) {
		final List<TerminalNode> ids = ctx.ID();
		final List<TerminalNode> strings = ctx.STRING_LITERAL();
		for (int i = 0; i < ids.size(); i++) {
			final String id = ids.get(i).getText();
			String value = strings.get(i).getText();
			value = value.substring(1, value.length() - 1);
			this.templates.addDecoratedString(id, value);
		}
		return null;
	}

	@Override
	public Void visitIncludeStatement(final IncludeStatementContext ctx) {
		String includeFilePath = ctx.STRING_LITERAL().getText();
		includeFilePath = includeFilePath.substring(1, includeFilePath.length() - 1);
		final FDFParser parser = this.fdfParserBuilder.build(includeFilePath);
		visit(parser.program());
		return null;
	}
}

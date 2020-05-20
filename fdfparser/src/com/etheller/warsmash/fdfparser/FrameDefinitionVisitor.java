package com.etheller.warsmash.fdfparser;

import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.etheller.warsmash.fdfparser.FDFParser.CompDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.CompSubTypeDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.CompSubTypeDefinitionWithChildrenContext;
import com.etheller.warsmash.fdfparser.FDFParser.FrameDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.FrameSubTypeDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.FrameSubTypeDefinitionWithChildrenContext;
import com.etheller.warsmash.fdfparser.FDFParser.IncludeStatementContext;
import com.etheller.warsmash.fdfparser.FDFParser.StringListStatementContext;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefintion;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;

public class FrameDefinitionVisitor extends FDFBaseVisitor<FrameDefintion> {
	private final FrameTemplateEnvironment templates;
	private final FDFParserBuilder fdfParserBuilder;

	public FrameDefinitionVisitor(final FrameTemplateEnvironment templates, final FDFParserBuilder fdfParserBuilder) {
		this.templates = templates;
		this.fdfParserBuilder = fdfParserBuilder;
	}

	@Override
	public FrameDefintion visitStringListStatement(final StringListStatementContext ctx) {
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
	public FrameDefintion visitIncludeStatement(final IncludeStatementContext ctx) {
		String includeFilePath = ctx.STRING_LITERAL().getText();
		includeFilePath = includeFilePath.substring(1, includeFilePath.length() - 1);
		final FDFParser parser = this.fdfParserBuilder.build(includeFilePath);
		visit(parser.program());
		return null;
	}

	@Override
	public FrameDefintion visitFrameDefinition(final FrameDefinitionContext ctx) {
		return super.visitFrameDefinition(ctx);
	}

	@Override
	public FrameDefintion visitFrameSubTypeDefinition(final FrameSubTypeDefinitionContext ctx) {
		return super.visitFrameSubTypeDefinition(ctx);
	}

	@Override
	public FrameDefintion visitFrameSubTypeDefinitionWithChildren(final FrameSubTypeDefinitionWithChildrenContext ctx) {
		return super.visitFrameSubTypeDefinitionWithChildren(ctx);
	}

	@Override
	public FrameDefintion visitCompDefinition(final CompDefinitionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitCompDefinition(ctx);
	}

	@Override
	public FrameDefintion visitCompSubTypeDefinition(final CompSubTypeDefinitionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitCompSubTypeDefinition(ctx);
	}

	@Override
	public FrameDefintion visitCompSubTypeDefinitionWithChildren(final CompSubTypeDefinitionWithChildrenContext ctx) {
		// TODO Auto-generated method stub
		return super.visitCompSubTypeDefinitionWithChildren(ctx);
	}
}

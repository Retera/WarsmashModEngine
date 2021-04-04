package com.etheller.warsmash.fdfparser;

import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.etheller.warsmash.fdfparser.FDFParser.AnonymousCompDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.AnonymousCompSubTypeDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.AnonymousCompSubTypeDefinitionWithChildrenContext;
import com.etheller.warsmash.fdfparser.FDFParser.CompDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.CompSubTypeDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.CompSubTypeDefinitionWithChildrenContext;
import com.etheller.warsmash.fdfparser.FDFParser.FrameDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.FrameSubTypeDefinitionContext;
import com.etheller.warsmash.fdfparser.FDFParser.FrameSubTypeDefinitionWithChildrenContext;
import com.etheller.warsmash.fdfparser.FDFParser.Frame_elementContext;
import com.etheller.warsmash.fdfparser.FDFParser.IncludeStatementContext;
import com.etheller.warsmash.fdfparser.FDFParser.StringListStatementContext;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameClass;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;

public class FrameDefinitionVisitor extends FDFBaseVisitor<FrameDefinition> {
	private final FrameTemplateEnvironment templates;
	private final FDFParserBuilder fdfParserBuilder;

	public FrameDefinitionVisitor(final FrameTemplateEnvironment templates, final FDFParserBuilder fdfParserBuilder) {
		this.templates = templates;
		this.fdfParserBuilder = fdfParserBuilder;
	}

	@Override
	public FrameDefinition visitStringListStatement(final StringListStatementContext ctx) {
		final List<TerminalNode> ids = ctx.ID();
		final List<TerminalNode> strings = ctx.STRING_LITERAL();
		for (int i = 0; i < ids.size(); i++) {
			final String id = ids.get(i).getText();
			String value = strings.get(i).getText();
			value = unquote(value);
			this.templates.addDecoratedString(id, value);
		}
		return null;
	}

	@Override
	public FrameDefinition visitIncludeStatement(final IncludeStatementContext ctx) {
		final String includeFilePath = unquote(ctx.STRING_LITERAL().getText());
		final FDFParser parser = this.fdfParserBuilder.build(includeFilePath);
		visit(parser.program());
		return null;
	}

	private String unquote(String includeFilePath) {
		includeFilePath = includeFilePath.substring(1, includeFilePath.length() - 1);
		return includeFilePath;
	}

	@Override
	public FrameDefinition visitFrameDefinition(final FrameDefinitionContext ctx) {
		final String type = unquote(ctx.STRING_LITERAL(0).getText());
		final String name = unquote(ctx.STRING_LITERAL(1).getText());
		final FrameDefinition frameDefinition = new FrameDefinition(FrameClass.Frame, type, name);
		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		this.templates.put(name, frameDefinition);
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitFrameSubTypeDefinition(final FrameSubTypeDefinitionContext ctx) {
		final String type = unquote(ctx.STRING_LITERAL(0).getText());
		final String name = unquote(ctx.STRING_LITERAL(1).getText());
		final String parent = unquote(ctx.STRING_LITERAL(2).getText());
		final FrameDefinition frameDefinition = new FrameDefinition(FrameClass.Frame, type, name);
		// INHERITS
		final FrameDefinition inheritParent = this.templates.getFrame(parent);
		if (inheritParent == null) {
			throw new IllegalStateException(
					"\"" + name + "\" cannot inherit from \"" + parent + "\" because it does not exist!");
		}
		frameDefinition.inheritFrom(inheritParent, false);

		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		this.templates.put(name, frameDefinition);
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitFrameSubTypeDefinitionWithChildren(
			final FrameSubTypeDefinitionWithChildrenContext ctx) {
		final String type = unquote(ctx.STRING_LITERAL(0).getText());
		final String name = unquote(ctx.STRING_LITERAL(1).getText());
		final String parent = unquote(ctx.STRING_LITERAL(2).getText());
		final FrameDefinition frameDefinition = new FrameDefinition(FrameClass.Frame, type, name);
		// INHERITS
		final FrameDefinition inheritParent = this.templates.getFrame(parent);
		if (inheritParent == null) {
			throw new IllegalStateException(
					"\"" + name + "\" cannot inherit from \"" + parent + "\" because it does not exist!");
		}
		frameDefinition.inheritFrom(inheritParent, true);

		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		this.templates.put(name, frameDefinition);
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitAnonymousCompDefinition(final AnonymousCompDefinitionContext ctx) {
		final FrameDefinition frameDefinition = new FrameDefinition(
				FrameClass.valueOf(ctx.frame_type_qualifier().getText()), null, null);
		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitAnonymousCompSubTypeDefinition(final AnonymousCompSubTypeDefinitionContext ctx) {
		final String parent = unquote(ctx.STRING_LITERAL().getText());
		final FrameClass frameClass = FrameClass.valueOf(ctx.frame_type_qualifier().getText());
		final FrameDefinition frameDefinition = new FrameDefinition(frameClass, null, null);
		// INHERITS
		final FrameDefinition inheritParent = this.templates.getFrame(parent);
		if (inheritParent == null) {
			throw new IllegalStateException(
					"" + frameClass + " cannot inherit from \"" + parent + "\" because it does not exist!");
		}
		frameDefinition.inheritFrom(inheritParent, false);

		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitAnonymousCompSubTypeDefinitionWithChildren(
			final AnonymousCompSubTypeDefinitionWithChildrenContext ctx) {
		final String parent = unquote(ctx.STRING_LITERAL().getText());
		final FrameClass frameClass = FrameClass.valueOf(ctx.frame_type_qualifier().getText());
		final FrameDefinition frameDefinition = new FrameDefinition(frameClass, null, null);
		// INHERITS
		final FrameDefinition inheritParent = this.templates.getFrame(parent);
		if (inheritParent == null) {
			throw new IllegalStateException(
					"" + frameClass + " cannot inherit from \"" + parent + "\" because it does not exist!");
		}
		frameDefinition.inheritFrom(inheritParent, true);

		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitCompDefinition(final CompDefinitionContext ctx) {
		final String name = unquote(ctx.STRING_LITERAL().getText());
		final FrameDefinition frameDefinition = new FrameDefinition(
				FrameClass.valueOf(ctx.frame_type_qualifier().getText()), null, name);
		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		this.templates.put(name, frameDefinition);
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitCompSubTypeDefinition(final CompSubTypeDefinitionContext ctx) {
		final String name = unquote(ctx.STRING_LITERAL(0).getText());
		final String parent = unquote(ctx.STRING_LITERAL(1).getText());
		final FrameDefinition frameDefinition = new FrameDefinition(
				FrameClass.valueOf(ctx.frame_type_qualifier().getText()), null, name);
		// INHERITS
		final FrameDefinition inheritParent = this.templates.getFrame(parent);
		if (inheritParent == null) {
			throw new IllegalStateException(
					"\"" + name + "\" cannot inherit from \"" + parent + "\" because it does not exist!");
		}
		frameDefinition.inheritFrom(inheritParent, false);

		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		this.templates.put(name, frameDefinition);
		return frameDefinition;
	}

	@Override
	public FrameDefinition visitCompSubTypeDefinitionWithChildren(final CompSubTypeDefinitionWithChildrenContext ctx) {
		final String name = unquote(ctx.STRING_LITERAL(0).getText());
		final String parent = unquote(ctx.STRING_LITERAL(1).getText());
		final FrameDefinition frameDefinition = new FrameDefinition(
				FrameClass.valueOf(ctx.frame_type_qualifier().getText()), null, name);
		// INHERITS
		final FrameDefinition inheritParent = this.templates.getFrame(parent);
		if (inheritParent == null) {
			throw new IllegalStateException(
					"\"" + name + "\" cannot inherit from \"" + parent + "\" because it does not exist!");
		}
		frameDefinition.inheritFrom(inheritParent, true);

		final FrameDefinitionFieldVisitor fieldVisitor = new FrameDefinitionFieldVisitor(frameDefinition, this);
		for (final Frame_elementContext element : ctx.frame_element()) {
			fieldVisitor.visit(element);
		}
		this.templates.put(name, frameDefinition);
		return frameDefinition;
	}
}

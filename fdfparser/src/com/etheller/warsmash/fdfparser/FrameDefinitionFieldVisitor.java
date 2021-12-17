package com.etheller.warsmash.fdfparser;

import com.etheller.warsmash.fdfparser.FDFParser.AnchorElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.FlagElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.FloatElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.FontElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.FrameFrameElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.MenuItemElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.SetPointElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.SimpleFontElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.StringElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.StringPairElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.TextJustifyElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.Vector2ElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.Vector3ElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.Vector4CommaElementContext;
import com.etheller.warsmash.fdfparser.FDFParser.Vector4ElementContext;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FontDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.MenuItem;
import com.etheller.warsmash.parsers.fdf.datamodel.SetPointDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector2Definition;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FloatFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FontFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.MenuItemFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringPairFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.TextJustifyFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector2FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector4FrameDefinitionField;

public class FrameDefinitionFieldVisitor extends FDFBaseVisitor<Void> {
	private static final int JUSTIFY_OFFSET = "JUSTIFY".length();
	private final FrameDefinition frameDefinition;
	private final FrameDefinitionVisitor frameDefinitionVisitor;

	public FrameDefinitionFieldVisitor(final FrameDefinition frameDefinition,
			final FrameDefinitionVisitor frameDefinitionVisitor) {
		this.frameDefinition = frameDefinition;
		this.frameDefinitionVisitor = frameDefinitionVisitor;
	}

	@Override
	public Void visitStringElement(final StringElementContext ctx) {
		String text = ctx.STRING_LITERAL().getText();
		text = text.substring(1, text.length() - 1);
		this.frameDefinition.set(ctx.ID().getText(), new StringFrameDefinitionField(text));
		return null;
	}

	@Override
	public Void visitStringPairElement(final StringPairElementContext ctx) {
		String first = ctx.STRING_LITERAL(0).getText();
		first = first.substring(1, first.length() - 1);
		String second = ctx.STRING_LITERAL(1).getText();
		second = second.substring(1, second.length() - 1);
		this.frameDefinition.set(ctx.ID().getText(), new StringPairFrameDefinitionField(first, second));
		return super.visitStringPairElement(ctx);
	}

	@Override
	public Void visitFloatElement(final FloatElementContext ctx) {
		this.frameDefinition.set(ctx.ID().getText(),
				new FloatFrameDefinitionField(Float.parseFloat(ctx.FLOAT().getText())));
		return null;
	}

	@Override
	public Void visitFlagElement(final FlagElementContext ctx) {
		this.frameDefinition.add(ctx.ID().getText());
		return null;
	}

	@Override
	public Void visitVector2Element(final Vector2ElementContext ctx) {
		this.frameDefinition.set(ctx.ID().getText(),
				new Vector2FrameDefinitionField(new Vector2Definition(Float.parseFloat(ctx.FLOAT(0).getText()),
						Float.parseFloat(ctx.FLOAT(1).getText()))));
		return null;
	}

	@Override
	public Void visitVector3Element(final Vector3ElementContext ctx) {
		this.frameDefinition.set(ctx.ID().getText(),
				new Vector4FrameDefinitionField(new Vector4Definition(Float.parseFloat(ctx.FLOAT(0).getText()),
						Float.parseFloat(ctx.FLOAT(1).getText()), Float.parseFloat(ctx.FLOAT(2).getText()), 1.0f)));
		return null;
	}

	@Override
	public Void visitVector4Element(final Vector4ElementContext ctx) {
		this.frameDefinition.set(ctx.ID().getText(),
				new Vector4FrameDefinitionField(new Vector4Definition(Float.parseFloat(ctx.FLOAT(0).getText()),
						Float.parseFloat(ctx.FLOAT(1).getText()), Float.parseFloat(ctx.FLOAT(2).getText()),
						Float.parseFloat(ctx.FLOAT(3).getText()))));
		return null;
	}

	@Override
	public Void visitVector4CommaElement(final Vector4CommaElementContext ctx) {
		this.frameDefinition.set(ctx.ID().getText(),
				new Vector4FrameDefinitionField(new Vector4Definition(Float.parseFloat(ctx.FLOAT(0).getText()),
						Float.parseFloat(ctx.FLOAT(1).getText()), Float.parseFloat(ctx.FLOAT(2).getText()),
						Float.parseFloat(ctx.FLOAT(3).getText()))));
		return null;
	}

	@Override
	public Void visitSetPointElement(final SetPointElementContext ctx) {
		String other = ctx.STRING_LITERAL().getText();
		other = other.substring(1, other.length() - 1);
		final SetPointDefinition setPointDefinition = new SetPointDefinition(
				FramePoint.valueOf(ctx.frame_point(0).getText()), other,
				FramePoint.valueOf(ctx.frame_point(1).getText()), Float.parseFloat(ctx.FLOAT(0).getText()),
				Float.parseFloat(ctx.FLOAT(1).getText()));
		this.frameDefinition.add(setPointDefinition);
		return null;
	}

	@Override
	public Void visitAnchorElement(final AnchorElementContext ctx) {
		final AnchorDefinition anchorDefinition = new AnchorDefinition(FramePoint.valueOf(ctx.frame_point().getText()),
				Float.parseFloat(ctx.FLOAT(0).getText()), Float.parseFloat(ctx.FLOAT(1).getText()));
		this.frameDefinition.add(anchorDefinition);
		return null;
	}

	@Override
	public Void visitTextJustifyElement(final TextJustifyElementContext ctx) {
		final TextJustify justify = TextJustify.valueOf(ctx.text_justify().getText().substring(JUSTIFY_OFFSET));
		this.frameDefinition.set(ctx.ID().getText(), new TextJustifyFrameDefinitionField(justify));
		return null;
	}

	@Override
	public Void visitFontElement(final FontElementContext ctx) {
		String text = ctx.STRING_LITERAL(0).getText();
		text = text.substring(1, text.length() - 1);
		this.frameDefinition.set(ctx.ID().getText(), new FontFrameDefinitionField(
				new FontDefinition(text, Float.parseFloat(ctx.FLOAT().getText()), ctx.STRING_LITERAL(1).getText())));
		return null;
	}

	@Override
	public Void visitSimpleFontElement(final SimpleFontElementContext ctx) {
		String text = ctx.STRING_LITERAL().getText();
		text = text.substring(1, text.length() - 1);
		this.frameDefinition.set(ctx.ID().getText(),
				new FontFrameDefinitionField(new FontDefinition(text, Float.parseFloat(ctx.FLOAT().getText()), null)));
		return null;
	}

	@Override
	public Void visitMenuItemElement(final MenuItemElementContext ctx) {
		String text = ctx.STRING_LITERAL().getText();
		text = text.substring(1, text.length() - 1);
		this.frameDefinition.add(ctx.MENUITEM().getText(),
				new MenuItemFrameDefinitionField(new MenuItem(text, (int) Float.parseFloat(ctx.FLOAT().getText()))));
		return null;
	}

	@Override
	public Void visitFrameFrameElement(final FrameFrameElementContext ctx) {
		this.frameDefinition.add(this.frameDefinitionVisitor.visit(ctx));
		return null;
	}
}

package com.etheller.warsmash.fdfparser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.etheller.warsmash.fdfparser.FDFParser.IncludeStatementContext;
import com.etheller.warsmash.fdfparser.FDFParser.StringListStatementContext;
import com.etheller.warsmash.parsers.fdf.FDFNamedString;
import com.etheller.warsmash.parsers.fdf.FDFStatement;
import com.etheller.warsmash.parsers.fdf.FDFStringListStatement;

public class FDFStatementVisitor extends FDFBaseVisitor<FDFStatement> {

	@Override
	public FDFStatement visitStringListStatement(final StringListStatementContext ctx) {
		final List<FDFNamedString> namedStrings = new ArrayList<>();
		final List<TerminalNode> ids = ctx.ID();
		final List<TerminalNode> strings = ctx.STRING_LITERAL();
		for (int i = 0; i < ids.size(); i++) {
			final String id = ids.get(i).getText();
			String value = strings.get(i).getText();
			value = value.substring(1, value.length() - 1);
			namedStrings.add(new FDFNamedString(id, value));
		}
		return new FDFStringListStatement(namedStrings);
	}

	@Override
	public FDFStatement visitIncludeStatement(final IncludeStatementContext ctx) {
		String includeFilePath = ctx.STRING_LITERAL().getText();
		includeFilePath = includeFilePath.substring(1, includeFilePath.length() - 1);
		return super.visitIncludeStatement(ctx);
	}
}

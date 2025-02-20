package com.etheller.warsmash.parsers.jass.triggers;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.interpreter.ast.value.StringJassValue;

public class StringList extends ArrayList<StringJassValue> implements CHandle {
	private final int handleId;

	public StringList(final int handleId) {
		this.handleId = handleId;
	}

	public StringList(final int handleId, final List<String> javaValueList) {
		this(handleId);
		for (final String str : javaValueList) {
			add(StringJassValue.of(str));
		}
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	public List<String> asJavaValue() {
		// bad for performance
		final List<String> javaList = new ArrayList<>();
		for (final StringJassValue stringJass : this) {
			javaList.add(stringJass.getValue());
		}
		return javaList;
	}

}

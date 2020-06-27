package com.etheller.interpreter.ast.scope;

import java.util.HashMap;
import java.util.Map;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;

public final class LocalScope {
	private final Map<String, Assignable> locals = new HashMap<>();

	public void createLocal(final String name, final JassType type) {
		this.locals.put(name, new Assignable(type));
	}

	public void createLocal(final String name, final JassType type, final JassValue value) {
		final Assignable assignable = new Assignable(type);
		assignable.setValue(value);
		this.locals.put(name, assignable);
	}

	public void setLocal(final String name, final JassValue value) {
		final Assignable assignable = this.locals.get(name);
		if (assignable == null) {
			throw new RuntimeException("Undefined local variable: " + name);
		}
		assignable.setValue(value);
	}

	public JassValue getLocal(final String name) {
		final Assignable local = this.locals.get(name);
		if (local == null) {
			throw new RuntimeException("Undefined local variable: " + name);
		}
		return local.getValue();
	}

	public Assignable getAssignableLocal(final String name) {
		return this.locals.get(name);
	}
}

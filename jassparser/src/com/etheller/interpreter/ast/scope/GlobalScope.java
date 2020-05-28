package com.etheller.interpreter.ast.scope;

import java.util.HashMap;
import java.util.Map;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;

public final class GlobalScope {
	private final Map<String, Assignable> globals = new HashMap<>();
	private final Map<String, JassFunction> functions = new HashMap<>();
	private final Map<String, JassType> types = new HashMap<>();

	public void createGlobalArray(final String name, final JassType type) {
		final Assignable assignable = new Assignable(type);
		assignable.setValue(new ArrayJassValue((ArrayJassType) type)); // TODO less bad code
		globals.put(name, assignable);
	}

	public void createGlobal(final String name, final JassType type) {
		globals.put(name, new Assignable(type));
	}

	public void createGlobal(final String name, final JassType type, final JassValue value) {
		final Assignable assignable = new Assignable(type);
		assignable.setValue(value);
		globals.put(name, assignable);
	}

	public void setGlobal(final String name, final JassValue value) {
		final Assignable assignable = globals.get(name);
		if (assignable == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		if (assignable.getType().visit(ArrayPrimitiveTypeVisitor.getInstance()) != null) {
			throw new RuntimeException("Unable to assign array variable: " + name);
		}
		assignable.setValue(value);
	}

	public JassValue getGlobal(final String name) {
		final Assignable global = globals.get(name);
		if (global == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		return global.getValue();
	}

	public Assignable getAssignableGlobal(final String name) {
		return globals.get(name);
	}

	public void defineFunction(final String name, final JassFunction function) {
		functions.put(name, function);
	}

	public JassFunction getFunctionByName(final String name) {
		return functions.get(name);
	}

	public PrimitiveJassType parseType(final String text) {
		if (text.equals("string")) {
			return JassType.STRING;
		} else if (text.equals("integer")) {
			return JassType.INTEGER;
		} else if (text.equals("boolean")) {
			return JassType.BOOLEAN;
		} else if (text.equals("real")) {
			return JassType.REAL;
		} else if (text.equals("code")) {
			return JassType.CODE;
		} else if (text.equals("nothing")) {
			return JassType.NOTHING;
		} else {
			throw new RuntimeException("Unknown type: " + text);
		}
	}

	public JassType parseArrayType(final String primitiveTypeName) {
		final String arrayTypeName = primitiveTypeName + " array";
		JassType arrayType = types.get(arrayTypeName);
		if (arrayType == null) {
			arrayType = new ArrayJassType(parseType(primitiveTypeName));
			types.put(arrayTypeName, arrayType);
		}
		return arrayType;
	}
}

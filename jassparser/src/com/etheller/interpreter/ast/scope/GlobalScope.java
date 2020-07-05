package com.etheller.interpreter.ast.scope;

import java.util.HashMap;
import java.util.Map;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleTypeSuperTypeLoadingVisitor;

public final class GlobalScope {
	private final Map<String, Assignable> globals = new HashMap<>();
	private final Map<String, JassFunction> functions = new HashMap<>();
	private final Map<String, JassType> types = new HashMap<>();
	private final HandleTypeSuperTypeLoadingVisitor handleTypeSuperTypeLoadingVisitor = new HandleTypeSuperTypeLoadingVisitor();

	public final HandleJassType handleType;

	private static int lineNumber;

	public GlobalScope() {
		this.handleType = registerHandleType("handle");// the handle type
		registerPrimitiveType(JassType.BOOLEAN);
		registerPrimitiveType(JassType.INTEGER);
		registerPrimitiveType(JassType.CODE);
		registerPrimitiveType(JassType.NOTHING);
		registerPrimitiveType(JassType.REAL);
		registerPrimitiveType(JassType.STRING);
	}

	public static void setLineNumber(final int lineNo) {
		lineNumber = lineNo;
	}

	public static int getLineNumber() {
		return lineNumber;
	}

	public HandleJassType registerHandleType(final String name) {
		final HandleJassType handleJassType = new HandleJassType(null, name);
		this.types.put(name, handleJassType);
		return handleJassType;
	}

	private void registerPrimitiveType(final PrimitiveJassType type) {
		this.types.put(type.getName(), type);
	}

	public void createGlobalArray(final String name, final JassType type) {
		final Assignable assignable = new Assignable(type);
		assignable.setValue(new ArrayJassValue((ArrayJassType) type)); // TODO less bad code
		this.globals.put(name, assignable);
	}

	public void createGlobal(final String name, final JassType type) {
		this.globals.put(name, new Assignable(type));
	}

	public void createGlobal(final String name, final JassType type, final JassValue value) {
		final Assignable assignable = new Assignable(type);
		assignable.setValue(value);
		this.globals.put(name, assignable);
	}

	public void setGlobal(final String name, final JassValue value) {
		final Assignable assignable = this.globals.get(name);
		if (assignable == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		if (assignable.getType().visit(ArrayPrimitiveTypeVisitor.getInstance()) != null) {
			throw new RuntimeException("Unable to assign array variable: " + name);
		}
		assignable.setValue(value);
	}

	public JassValue getGlobal(final String name) {
		final Assignable global = this.globals.get(name);
		if (global == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		return global.getValue();
	}

	public Assignable getAssignableGlobal(final String name) {
		return this.globals.get(name);
	}

	public void defineFunction(final String name, final JassFunction function) {
		this.functions.put(name, function);
	}

	public JassFunction getFunctionByName(final String name) {
		return this.functions.get(name);
	}

	public JassType parseType(final String text) {
		final JassType type = this.types.get(text);
		if (type != null) {
			return type;
		}
		else {
			throw new RuntimeException("Unknown type: " + text);
		}
	}

	public JassType parseArrayType(final String primitiveTypeName) {
		final String arrayTypeName = primitiveTypeName + " array";
		JassType arrayType = this.types.get(arrayTypeName);
		if (arrayType == null) {
			arrayType = new ArrayJassType(parseType(primitiveTypeName));
			this.types.put(arrayTypeName, arrayType);
		}
		return arrayType;
	}

	public void loadTypeDefinition(final String type, final String supertype) {
		final JassType superType = this.types.get(supertype);
		if (superType != null) {
			final HandleJassType handleSuperType = superType.visit(HandleJassTypeVisitor.getInstance());
			if (handleSuperType != null) {
				final JassType jassType = this.types.get(type);
				if (jassType != null) {
					jassType.visit(this.handleTypeSuperTypeLoadingVisitor.reset(handleSuperType));
				}
				else {
					throw new RuntimeException(
							"unable to declare type " + type + " because it does not exist natively");
				}
			}
			else {
				throw new RuntimeException("type " + type + " cannot extend primitive type " + supertype);
			}
		}
		else {
			throw new RuntimeException("type " + type + " cannot extend unknown type " + supertype);
		}
	}
}

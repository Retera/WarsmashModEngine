package com.etheller.interpreter.ast.function;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.JassType;

public class JassNativeManager {
	private final Map<String, JassFunction> nameToNativeCode;
	private final Set<String> registeredNativeNames = new HashSet<>();

	public JassNativeManager() {
		this.nameToNativeCode = new HashMap<>();
	}

	public void createNative(final String name, final JassFunction nativeCode) {
		this.nameToNativeCode.put(name, nativeCode);
	}

	public void registerNativeCode(final int lineNo, final String sourceFile, final String name,
			final List<JassParameter> parameters, final JassType returnType, final GlobalScope globals) {
		if (this.registeredNativeNames.contains(name)) {
			throw new RuntimeException("Native already registered: " + name);
		}
		final JassFunction nativeCode = this.nameToNativeCode.remove(name);
		globals.defineFunction(lineNo, sourceFile, name,
				new NativeJassFunction(parameters, returnType, name, nativeCode));
		this.registeredNativeNames.add(name);
	}

	public void checkUnregisteredNatives() {
		// TODO maybe do this later
	}
}

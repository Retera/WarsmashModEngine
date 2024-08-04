package com.etheller.interpreter.ast.util;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.definition.JassDefinitionBlock;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.scope.GlobalScope;

public class JassProgram {
	public final GlobalScope globalScope = new GlobalScope();
	public final JassNativeManager jassNativeManager = new JassNativeManager();
	public final List<JassDefinitionBlock> definitionBlocks = new ArrayList<>();

	public void initialize() {
		for (final JassDefinitionBlock definitionBlock : this.definitionBlocks) {
			definitionBlock.define("", this);
		}
		this.globalScope.runThreadUntilCompletion(this.globalScope.createThread(
				this.globalScope.getUserFunctionInstructionPtr(GlobalScope.INIT_GLOBALS_AUTOGEN_FXN_NAME)));
		this.globalScope.resetGlobalInitialization();
	}

	public GlobalScope getGlobalScope() {
		return this.globalScope;
	}

	public GlobalScope getGlobals() {
		return this.globalScope;
	}

	public JassNativeManager getJassNativeManager() {
		return this.jassNativeManager;
	}
}

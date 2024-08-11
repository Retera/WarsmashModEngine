package com.etheller.interpreter.ast.util;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.definition.JassDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassLibraryDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassScopeDefinitionBlock;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.scope.DefaultScope;
import com.etheller.interpreter.ast.scope.GlobalScope;

public class JassProgram {
	public final GlobalScope globalScope = new GlobalScope();
	public final JassNativeManager jassNativeManager = new JassNativeManager();
	public final List<JassDefinitionBlock> definitionBlocks = new ArrayList<>();

	public void initialize() {
		final DefaultScope defaultScope = new DefaultScope(this.globalScope);
		for (final JassDefinitionBlock definitionBlock : this.definitionBlocks) {
			definitionBlock.define(defaultScope, this);
		}
		final Integer globalsInitializerFunctionPtr = this.globalScope
				.getUserFunctionInstructionPtr(GlobalScope.INIT_GLOBALS_AUTOGEN_FXN_NAME);
		if (globalsInitializerFunctionPtr != null) {
			this.globalScope.runThreadUntilCompletion(this.globalScope.createThread(globalsInitializerFunctionPtr));
		}
		this.globalScope.resetGlobalInitialization();
	}

	public void addAll(final List<JassDefinitionBlock> blocks) {
		final List<JassDefinitionBlock> sortedBlocks = new ArrayList<>();

		final List<JassLibraryDefinitionBlock> libraries = new ArrayList<>();
		final List<JassScopeDefinitionBlock> scopes = new ArrayList<>();
		final List<JassDefinitionBlock> everythingElse = new ArrayList<>();

		for (final JassDefinitionBlock definitionBlock : blocks) {
			// TODO: change to visitor
			if (definitionBlock instanceof JassLibraryDefinitionBlock) {
				libraries.add((JassLibraryDefinitionBlock) definitionBlock);
			}
			else if (definitionBlock instanceof JassScopeDefinitionBlock) {
				scopes.add((JassScopeDefinitionBlock) definitionBlock);
			}
			else {
				everythingElse.add(definitionBlock);
			}
		}

		JassLibraryDefinitionBlock.topologicalSort(libraries);

		sortedBlocks.addAll(libraries);
		sortedBlocks.addAll(everythingElse);
		sortedBlocks.addAll(scopes);

		this.definitionBlocks.addAll(blocks);
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

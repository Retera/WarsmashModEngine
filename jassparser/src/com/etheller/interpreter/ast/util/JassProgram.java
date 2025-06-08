package com.etheller.interpreter.ast.util;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.definition.JassDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassLibraryDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassScopeDefinitionBlock;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.scope.DefaultScope;
import com.etheller.interpreter.ast.scope.GlobalScope;

public class JassProgram {
	public final GlobalScope globalScope = new GlobalScope();
	public final JassNativeManager jassNativeManager = new JassNativeManager();

	public final List<JassLibraryDefinitionBlock> libraries = new ArrayList<>();
	public final List<JassScopeDefinitionBlock> scopes = new ArrayList<>();
	public final List<JassDefinitionBlock> everythingElse = new ArrayList<>();

	private boolean paused = false;

	public void initialize() {
		try {
			final DefaultScope defaultScope = new DefaultScope(this.globalScope);
			JassLibraryDefinitionBlock.topologicalSort(this.libraries);
			for (final JassDefinitionBlock definitionBlock : this.everythingElse) {
				definitionBlock.define(defaultScope, this);
			}
			for (final JassDefinitionBlock definitionBlock : this.libraries) {
				definitionBlock.define(defaultScope, this);
			}
			for (final JassDefinitionBlock definitionBlock : this.scopes) {
				definitionBlock.define(defaultScope, this);
			}
			final Integer globalsInitializerFunctionPtr = this.globalScope
					.getUserFunctionInstructionPtr(GlobalScope.INIT_GLOBALS_AUTOGEN_FXN_NAME);
			try {
				if (globalsInitializerFunctionPtr != null) {
					this.globalScope
							.runThreadUntilCompletion(this.globalScope.createThread(globalsInitializerFunctionPtr));
				}
			}
			catch (final JassException exc) {
				exc.printStackTrace();
			}
			this.globalScope.resetGlobalInitialization();
		}
		finally {
			this.libraries.clear();
			this.everythingElse.clear();
			this.scopes.clear();
		}
	}

	public void addAll(final List<JassDefinitionBlock> blocks) {
		final List<JassDefinitionBlock> sortedBlocks = new ArrayList<>();

		for (final JassDefinitionBlock definitionBlock : blocks) {
			// TODO: change to visitor
			if (definitionBlock instanceof JassLibraryDefinitionBlock) {
				this.libraries.add((JassLibraryDefinitionBlock) definitionBlock);
			}
			else if (definitionBlock instanceof JassScopeDefinitionBlock) {
				this.scopes.add((JassScopeDefinitionBlock) definitionBlock);
			}
			else {
				this.everythingElse.add(definitionBlock);
			}
		}

	}

	public void setPaused(final boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return this.paused;
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

	public void inheritFrom(final JassProgram jassProgramVisitor) {
		this.globalScope.inheritFrom(jassProgramVisitor.getGlobalScope());
	}
}

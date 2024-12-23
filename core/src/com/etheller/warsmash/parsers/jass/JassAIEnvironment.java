package com.etheller.warsmash.parsers.jass;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class JassAIEnvironment {
	private final JassProgram jassProgram;
	private final int playerIndex;
	private boolean paused;

	public JassAIEnvironment(final JassProgram jassProgramVisitor, final DataSource dataSource,
			final Viewport uiViewport, final Scene uiScene, final CSimulation simulation, final int playerIndex) {
		this.jassProgram = jassProgramVisitor;
		this.playerIndex = playerIndex;

		final JassNativeManager jassNativeManager = jassProgramVisitor.getJassNativeManager();
		// StartThread is included in Jass2 for availability in other scripts

		jassNativeManager.createNative("DoAiScriptDebug", (arguments, globalScope, triggerScope) -> {

			return BooleanJassValue.TRUE;
		});
		jassNativeManager.createNative("GetAiPlayer", (arguments, globalScope, triggerScope) -> {

			return IntegerJassValue.of(playerIndex);
		});
		// TODO GetHeroId (?)
		// TODO GetHeroLevelAI
		jassNativeManager.createNative("GetUnitCount", (arguments, globalScope, triggerScope) -> {
			final Integer unitId = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
			simulation.getPlayer(playerIndex).getTechtreeUnlocked(new War3ID(unitId));
			return IntegerJassValue.of(playerIndex);
		});
	}

	public JassProgram getJassProgram() {
		return this.jassProgram;
	}

	public void setPaused(final boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void update() {
		if (!this.paused) {
			this.jassProgram.getGlobalScope().runThreads();
		}
	}

}

package com.etheller.warsmash.parsers.jass;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.jass.ai.AITown;
import com.etheller.warsmash.parsers.jass.ai.AIUnit;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashUI;

public class JassAIEnvironment {
	private final JassProgram jassProgram;
	private final int playerIndex;
	private boolean paused;

	private final List<AITown> aiTowns = new ArrayList<>();
	private final IntMap<AIUnit> handleIdToAIUnit = new IntMap<AIUnit>();

	public JassAIEnvironment(final JassProgram jassProgramVisitor, final DataSource dataSource,
			final Viewport uiViewport, final Scene uiScene, final CSimulation simulation, final int playerIndex,
			final WarsmashUI meleeUI) {
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
		jassNativeManager.createNative("GetHeroId", (arguments, globalScope, triggerScope) -> {
			// TODO review if TriggerExecutionScope will actually work here
			if (triggerScope instanceof CommonTriggerExecutionScope) {
				return IntegerJassValue
						.of(((CommonTriggerExecutionScope) triggerScope).getLevelingUnit().getTypeId().getValue());
			}
			return IntegerJassValue.ZERO;
		});
		jassNativeManager.createNative("GetHeroLevelAI", (arguments, globalScope, triggerScope) -> {
			// TODO review if TriggerExecutionScope will actually work here
			if (triggerScope instanceof CommonTriggerExecutionScope) {
				final CAbilityHero heroData = ((CommonTriggerExecutionScope) triggerScope).getLevelingUnit()
						.getHeroData();
				if (heroData != null) {
					return IntegerJassValue.of(heroData.getHeroLevel());
				}
			}
			return IntegerJassValue.ZERO;
		});
		jassNativeManager.createNative("GetUnitCount", (arguments, globalScope, triggerScope) -> {
			final Integer unitId = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
			final War3ID typeId = new War3ID(unitId);
			final int techtreeUnlocked = simulation.getPlayer(playerIndex).getTechtreeUnlocked(typeId);
			final int techtreeInProgress = simulation.getPlayer(playerIndex).getTechtreeInProgress(typeId);
			return IntegerJassValue.of(techtreeUnlocked + techtreeInProgress);
		});
		jassNativeManager.createNative("GetPlayerUnitTypeCount", (arguments, globalScope, triggerScope) -> {
			final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
			final Integer unitId = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
			final War3ID typeId = new War3ID(unitId);
			final int techtreeUnlocked = player.getTechtreeUnlocked(typeId);
			final int techtreeInProgress = player.getTechtreeInProgress(typeId);
			return IntegerJassValue.of(techtreeUnlocked + techtreeInProgress);
		});

		jassNativeManager.createNative("GetUnitCountDone", (arguments, globalScope, triggerScope) -> {
			final Integer unitId = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
			final War3ID typeId = new War3ID(unitId);
			final int techtreeUnlocked = simulation.getPlayer(playerIndex).getTechtreeUnlocked(typeId);
			return IntegerJassValue.of(techtreeUnlocked);
		});
		jassNativeManager.createNative("GetTownUnitCount", (arguments, globalScope, triggerScope) -> {
			final Integer unitId = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
			final Integer townId = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
			final boolean done = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
			final CPlayer player = simulation.getPlayer(playerIndex);

			// NOTE: slow, loops all units
			int countSum = 0;
			for (final CUnit unit : simulation.getUnits()) {
				final AIUnit aiUnit = this.handleIdToAIUnit.get(unit.getHandleId());
				if ((aiUnit != null) && (aiUnit.getTownId() == townId)) {
					final boolean constructing = unit.isConstructing();
					final boolean upgrading = unit.isUpgrading();
					if (upgrading) {
						War3ID typeId;
						if (done) {
							typeId = unit.getTypeId();
						}
						else {
							typeId = unit.getUpgradeIdType();
						}
						if (typeId.getValue() == unitId) {
							countSum++;
						}
					}
					else if (!done || !constructing) {
						if (unit.getTypeId().getValue() == unitId) {
							countSum++;
						}
					}
				}
			}
			return IntegerJassValue.of(countSum);
		});
		jassNativeManager.createNative("GetTownUnitCount", (arguments, globalScope, triggerScope) -> {
			final Integer unitId = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
			final Integer townId = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
			final boolean done = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
			final CPlayer player = simulation.getPlayer(playerIndex);

			// NOTE: slow, loops all units
			int countSum = 0;
			for (final CUnit unit : simulation.getUnits()) {
				final AIUnit aiUnit = this.handleIdToAIUnit.get(unit.getHandleId());
				if ((aiUnit != null) && (aiUnit.getTownId() == townId)) {
					final boolean constructing = unit.isConstructing();
					final boolean upgrading = unit.isUpgrading();
					if (upgrading) {
						War3ID typeId;
						if (done) {
							typeId = unit.getTypeId();
						}
						else {
							typeId = unit.getUpgradeIdType();
						}
						if (typeId.getValue() == unitId) {
							countSum++;
						}
					}
					else if (!done || !constructing) {
						if (unit.getTypeId().getValue() == unitId) {
							countSum++;
						}
					}
				}
			}
			return IntegerJassValue.of(countSum);
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

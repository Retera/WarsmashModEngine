package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.visitor;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGenericDoNothing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilitySellItems;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABTimedInstantTransformationBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABTimedTransformationBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler.OnTransformationActions;

public class ABGetInstantTransformationBuffVisitor implements CAbilityVisitor<ABBuff> {
	private static final ABGetInstantTransformationBuffVisitor INSTANCE = new ABGetInstantTransformationBuffVisitor();

	public static ABGetInstantTransformationBuffVisitor getInstance() {
		return INSTANCE;
	}

	private CSimulation game;
	private CUnit caster;
	private ABLocalDataStore localStore;
	private CUnitType newType;
	private boolean keepRatios;
	private OnTransformationActions actions;
	private War3ID buffId;
	private boolean addAlternateTagAfter;
	private float transformationTime;
	private float duration;
	private boolean permanent;

	public ABGetInstantTransformationBuffVisitor reset(CSimulation game, CUnit caster, ABLocalDataStore localStore,
			CUnitType newType, final boolean keepRatios, OnTransformationActions actions, War3ID buffId,
			boolean addAlternateTagAfter, float transformationTime, float duration, boolean permanent) {
		this.game = game;
		this.caster = caster;
		this.localStore = localStore;
		this.newType = newType;
		this.keepRatios = keepRatios;
		this.actions = actions;
		this.buffId = buffId;
		this.addAlternateTagAfter = addAlternateTagAfter;
		this.transformationTime = transformationTime;
		this.duration = duration;
		this.permanent = permanent;
		return this;
	}

	@Override
	public ABBuff accept(final ABAbilityBuilderActiveAbility ability) {
		return new ABTimedTransformationBuff(game.getHandleIdAllocator().createId(), localStore, ability, caster,
				actions, buffId == null ? ability.getAlias() : buffId, duration, ability, newType,
				!addAlternateTagAfter, permanent, transformationTime);
	}

	@Override
	public ABBuff accept(final GenericSingleIconActiveAbility ability) {
		return null;
	}

	@Override
	public ABBuff accept(GenericSingleIconPassiveAbility ability) {
		if (ability instanceof ABAbilityBuilderPassiveAbility) {
			return new ABTimedInstantTransformationBuff(game.getHandleIdAllocator().createId(), localStore, ability,
					caster, actions, buffId == null ? ability.getAlias() : buffId, duration,
					(ABAbilityBuilderPassiveAbility) ability, newType, keepRatios, !addAlternateTagAfter, permanent,
					transformationTime);
		}
		return null;
	}

	@Override
	public ABBuff accept(final GenericNoIconAbility ability) {
		if (ability instanceof ABAbilityBuilderPassiveAbility) {
			return new ABTimedInstantTransformationBuff(game.getHandleIdAllocator().createId(), localStore, ability,
					caster, actions, buffId == null ? ability.getAlias() : buffId, duration,
					(ABAbilityBuilderPassiveAbility) ability, newType, keepRatios, !addAlternateTagAfter, permanent,
					transformationTime);
		}
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityAttack ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityMove ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityOrcBuild ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityHumanBuild ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityUndeadBuild ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityNightElfBuild ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityGenericDoNothing ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityColdArrows ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityNagaBuild ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityNeutralBuild ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityBuildInProgress ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityQueue ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilitySellItems ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityUpgrade ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityReviveHero ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityRoot ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityRally ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CBuff ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityReturnResources ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityHero ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityJass ability) {
		return null;
	}

	@Override
	public ABBuff accept(final CAbilityNeutralBuilding ability) {
		return null;
	}
}

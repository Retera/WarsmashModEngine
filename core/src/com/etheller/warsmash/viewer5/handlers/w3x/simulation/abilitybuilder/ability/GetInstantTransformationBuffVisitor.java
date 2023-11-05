package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.*;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedInstantTransformationBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTransformationBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;

public class GetInstantTransformationBuffVisitor implements CAbilityVisitor<ABBuff> {
	private static final GetInstantTransformationBuffVisitor INSTANCE = new GetInstantTransformationBuffVisitor();

	public static GetInstantTransformationBuffVisitor getInstance() {
		return INSTANCE;
	}

	private CSimulation game;
	private Map<String, Object> localStore;
	private CUnitType newType;
	private OnTransformationActions actions;
	private War3ID buffId;
	private boolean addAlternateTagAfter;
	private float transformationTime;
	private float duration;
	private boolean permanent;

	public GetInstantTransformationBuffVisitor reset(CSimulation game, Map<String, Object> localStore,
			CUnitType newType, OnTransformationActions actions, War3ID buffId, boolean addAlternateTagAfter,
			float transformationTime, float duration, boolean permanent) {
		this.game = game;
		this.localStore = localStore;
		this.newType = newType;
		this.actions = actions;
		this.buffId = buffId;
		this.addAlternateTagAfter = addAlternateTagAfter;
		this.transformationTime = transformationTime;
		this.duration = duration;
		this.permanent = permanent;
		return this;
	}

	@Override
	public ABBuff accept(final AbilityBuilderActiveAbility ability) {
		return new ABTimedTransformationBuff(game.getHandleIdAllocator().createId(), localStore, actions,
				buffId == null ? ability.getAlias() : buffId, duration, ability, newType, !addAlternateTagAfter,
				permanent, transformationTime);
	}

	@Override
	public ABBuff accept(final GenericSingleIconActiveAbility ability) {
		return null;
	}

	@Override
	public ABBuff accept(GenericSingleIconPassiveAbility ability) {
		if (ability instanceof AbilityBuilderPassiveAbility) {
			return new ABTimedInstantTransformationBuff(game.getHandleIdAllocator().createId(), localStore, actions,
					buffId == null ? ability.getAlias() : buffId, duration, (AbilityBuilderPassiveAbility)ability, newType, !addAlternateTagAfter,
					permanent, transformationTime);
		}
		return null;
	}

	@Override
	public ABBuff accept(final GenericNoIconAbility ability) {
		if (ability instanceof AbilityBuilderPassiveAbility) {
			return new ABTimedInstantTransformationBuff(game.getHandleIdAllocator().createId(), localStore, actions,
					buffId == null ? ability.getAlias() : buffId, duration, (AbilityBuilderPassiveAbility)ability, newType, !addAlternateTagAfter,
					permanent, transformationTime);
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

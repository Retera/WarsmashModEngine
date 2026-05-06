package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.visitor;

import com.etheller.warsmash.util.War3ID;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;

public class ABGetABAbilityByRawcodeVisitor implements CAbilityVisitor<ABAbilityBuilderAbility> {
	private static final ABGetABAbilityByRawcodeVisitor INSTANCE = new ABGetABAbilityByRawcodeVisitor();

	public static ABGetABAbilityByRawcodeVisitor getInstance() {
		return INSTANCE;
	}

	private War3ID rawcode;

	public ABGetABAbilityByRawcodeVisitor reset(final War3ID rawcode) {
		this.rawcode = rawcode;
		return this;
	}

	@Override
	public ABAbilityBuilderAbility accept(final ABAbilityBuilderActiveAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return (ABAbilityBuilderAbility) ability;
		}
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final GenericSingleIconActiveAbility ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(GenericSingleIconPassiveAbility ability) {
		if (ability instanceof ABAbilityBuilderAbility && this.rawcode.equals(ability.getAlias())) {
			return (ABAbilityBuilderAbility) ability;
		}
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final GenericNoIconAbility ability) {
		if (ability instanceof ABAbilityBuilderAbility && this.rawcode.equals(ability.getAlias())) {
			return (ABAbilityBuilderAbility) ability;
		}
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityAttack ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityMove ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityOrcBuild ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityHumanBuild ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityUndeadBuild ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityNightElfBuild ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityGenericDoNothing ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityColdArrows ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityNagaBuild ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityNeutralBuild ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityBuildInProgress ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityQueue ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilitySellItems ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityUpgrade ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityReviveHero ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityRoot ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityRally ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CBuff ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityReturnResources ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityHero ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityJass ability) {
		return null;
	}

	@Override
	public ABAbilityBuilderAbility accept(final CAbilityNeutralBuilding ability) {
		return null;
	}
}

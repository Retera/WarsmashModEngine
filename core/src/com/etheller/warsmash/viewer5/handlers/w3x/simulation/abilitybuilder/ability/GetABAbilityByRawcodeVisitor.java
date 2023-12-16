package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

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

public class GetABAbilityByRawcodeVisitor implements CAbilityVisitor<AbilityBuilderAbility> {
	private static final GetABAbilityByRawcodeVisitor INSTANCE = new GetABAbilityByRawcodeVisitor();

	public static GetABAbilityByRawcodeVisitor getInstance() {
		return INSTANCE;
	}

	private War3ID rawcode;

	public GetABAbilityByRawcodeVisitor reset(final War3ID rawcode) {
		this.rawcode = rawcode;
		return this;
	}

	@Override
	public AbilityBuilderAbility accept(final AbilityBuilderActiveAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return (AbilityBuilderAbility) ability;
		}
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final GenericSingleIconActiveAbility ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(GenericSingleIconPassiveAbility ability) {
		if (ability instanceof AbilityBuilderAbility && this.rawcode.equals(ability.getAlias())) {
			return (AbilityBuilderAbility) ability;
		}
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final GenericNoIconAbility ability) {
		if (ability instanceof AbilityBuilderAbility && this.rawcode.equals(ability.getAlias())) {
			return (AbilityBuilderAbility) ability;
		}
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityAttack ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityMove ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityOrcBuild ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityHumanBuild ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityUndeadBuild ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityNightElfBuild ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityGenericDoNothing ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityColdArrows ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityNagaBuild ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityNeutralBuild ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityBuildInProgress ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityQueue ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilitySellItems ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityUpgrade ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityReviveHero ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityRoot ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityRally ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CBuff ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityReturnResources ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityHero ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityJass ability) {
		return null;
	}

	@Override
	public AbilityBuilderAbility accept(final CAbilityNeutralBuilding ability) {
		return null;
	}
}

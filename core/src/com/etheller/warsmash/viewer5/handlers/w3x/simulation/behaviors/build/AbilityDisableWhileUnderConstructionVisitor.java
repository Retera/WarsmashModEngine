package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityDisableType;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;

public class AbilityDisableWhileUnderConstructionVisitor implements CAbilityVisitor<Void> {
	public static final AbilityDisableWhileUnderConstructionVisitor INSTANCE = new AbilityDisableWhileUnderConstructionVisitor();

	@Override
	public Void accept(final CAbilityAttack ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityMove ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityOrcBuild ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityHumanBuild ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityUndeadBuild ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityNightElfBuild ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityGenericDoNothing ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityColdArrows ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityNagaBuild ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityNeutralBuild ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityBuildInProgress ability) {
		ability.setDisabled(false, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityQueue ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilitySellItems ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityUpgrade ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityReviveHero ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final AbilityBuilderActiveAbility ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final GenericSingleIconActiveAbility ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final GenericSingleIconPassiveAbility ability) {
		return null;
	}

	@Override
	public Void accept(final CAbilityRoot ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityRally ability) {
		ability.setDisabled(false, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final GenericNoIconAbility ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityNeutralBuilding ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CBuff ability) {
		// this doesn't affect buffs
		return null;
	}

	@Override
	public Void accept(final CAbilityReturnResources ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityHero ability) {
		ability.setDisabled(true, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(false);
		return null;
	}

	@Override
	public Void accept(final CAbilityJass ability) {
		final boolean enabledWhileUnderConstruction = ability.isEnabledWhileUnderConstruction();
		ability.setDisabled(!enabledWhileUnderConstruction, CAbilityDisableType.CONSTRUCTION);
		ability.setIconShowing(enabledWhileUnderConstruction);
		return null;
	}
}

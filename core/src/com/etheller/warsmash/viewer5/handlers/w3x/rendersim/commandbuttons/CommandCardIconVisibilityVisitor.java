package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;

/**
 * Decides whether an ability would contribute at least one top-level icon to the
 * Warcraft III command card, mirroring the show/hide decisions of
 * {@link CommandCardPopulatingAbilityVisitor}. Used to build the list of
 * castable "abilities-as-icons" for the World of Warcraft spellbook / action bar
 * so that abilities the command card hides (inventory, bags, passive/internal
 * book-keeping abilities, item abilities, etc.) are likewise skipped there.
 *
 * <p>
 * This is per-ability (not per-icon), so an ability that shows several icons on
 * the Warcraft III command card contributes a single entry here -- which is an
 * acceptable simplification for the action bar.
 */
public enum CommandCardIconVisibilityVisitor implements CAbilityVisitor<Boolean> {
	INSTANCE;

	private static Boolean shownWhenIconShowing(final CAbility ability) {
		return ability.isIconShowing();
	}

	@Override
	public Boolean accept(final CAbilityAttack ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityMove ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityOrcBuild ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityHumanBuild ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityUndeadBuild ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityNightElfBuild ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityNagaBuild ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityNeutralBuild ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityColdArrows ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityBuildInProgress ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityQueue ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityUpgrade ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilitySellItems ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityReviveHero ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final GenericSingleIconActiveAbility ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final GenericSingleIconPassiveAbility ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityRally ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityHero ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityJass ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final CAbilityRoot ability) {
		return shownWhenIconShowing(ability);
	}

	@Override
	public Boolean accept(final AbilityBuilderActiveAbility ability) {
		return shownWhenIconShowing(ability);
	}

	// --- Abilities the command card never shows as a usable command-card icon ---

	@Override
	public Boolean accept(final CAbilityGenericDoNothing ability) {
		return Boolean.FALSE; // placeholders are disabled on the command card
	}

	@Override
	public Boolean accept(final GenericNoIconAbility ability) {
		return Boolean.FALSE; // e.g. inventory and other internal no-icon abilities
	}

	@Override
	public Boolean accept(final CAbilityReturnResources ability) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean accept(final CBuff ability) {
		return Boolean.FALSE; // buffs render on the buff bar, not as castable icons
	}

	@Override
	public Boolean accept(final CAbilityNeutralBuilding ability) {
		return Boolean.FALSE; // shop interaction button, not relevant to the action bar
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;

/**
 * A visitor for the lowest level inherent types of an ability. It's a bit of a
 * design clash to have the notion of an ability visitor pattern while also
 * having any arbitrary number of "ability types" defined in config files. But
 * the way that we will handle this for now will be with the notion of a generic
 * ability (one whose UI information and behaviors come from a rawcode) versus
 * abilities with engine-level type information (move, stop, attack).
 */
public interface CAbilityVisitor<T> {
	T accept(CAbilityAttack ability);

	T accept(CAbilityMove ability);

	T accept(CAbilityOrcBuild ability);

	T accept(CAbilityHumanBuild ability);

	T accept(CAbilityUndeadBuild ability);

	T accept(CAbilityNightElfBuild ability);

	T accept(CAbilityGenericDoNothing ability);

	T accept(CAbilityColdArrows ability);

	T accept(CAbilityNagaBuild ability);

	T accept(CAbilityNeutralBuild ability);

	T accept(CAbilityBuildInProgress ability);

	T accept(CAbilityQueue ability);

	T accept(CAbilityUpgrade ability);

	T accept(CAbilityReviveHero ability);

	T accept(CAbilityReturnResources ability);

	T accept(GenericSingleIconActiveAbility ability);

	T accept(CAbilityRally ability);

	T accept(GenericNoIconAbility ability);

	T accept(CAbilityHero ability);
}

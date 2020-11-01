package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;

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

	T accept(CAbilityGeneric ability);

	T accept(CAbilityColdArrows ability);

	T accept(CAbilityNagaBuild ability);

	T accept(CAbilityNeutralBuild ability);
}

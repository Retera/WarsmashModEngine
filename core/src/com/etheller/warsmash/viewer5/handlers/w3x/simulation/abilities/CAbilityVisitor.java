package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

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
}

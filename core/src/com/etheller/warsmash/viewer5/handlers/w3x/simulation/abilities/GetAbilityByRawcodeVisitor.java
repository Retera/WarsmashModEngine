package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.util.War3ID;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;

public class GetAbilityByRawcodeVisitor implements CAbilityVisitor<CAbility> {
	private static final GetAbilityByRawcodeVisitor INSTANCE = new GetAbilityByRawcodeVisitor();

	public static GetAbilityByRawcodeVisitor getInstance() {
		return INSTANCE;
	}

	private static final War3ID RALLY_RAWCODE = War3ID.fromString("Aral");
	private War3ID rawcode;

	public GetAbilityByRawcodeVisitor reset(final War3ID rawcode) {
		this.rawcode = rawcode;
		return this;
	}

	@Override
	public CAbility accept(final CAbilityAttack ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityMove ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityOrcBuild ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityHumanBuild ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityUndeadBuild ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityNightElfBuild ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityGeneric ability) {
		if (this.rawcode.equals(ability.getRawcode())) {
			return ability;
		}
		return null;
	}

	@Override
	public CAbility accept(final CAbilityColdArrows ability) {
		if (this.rawcode.equals(ability.getRawcode())) {
			return ability;
		}
		return null;
	}

	@Override
	public CAbility accept(final CAbilityNagaBuild ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityNeutralBuild ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityBuildInProgress ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityQueue ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityUpgrade ability) {
		return null;
	}

	@Override
	public CAbility accept(final CAbilityReviveHero ability) {
		return null;
	}

	@Override
	public CAbility accept(final GenericSingleIconActiveAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	public CAbility accept(final CAbilityRally ability) {
		if (this.rawcode.equals(RALLY_RAWCODE)) {
			return ability;
		}
		return null;
	}

	@Override
	public CAbility accept(final GenericNoIconAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	public CAbility accept(final CAbilityHero ability) {
		return null;
	}

}

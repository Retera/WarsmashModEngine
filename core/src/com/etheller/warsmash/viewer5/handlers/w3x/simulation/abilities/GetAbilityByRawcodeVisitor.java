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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;

public class GetAbilityByRawcodeVisitor implements CAbilityVisitor<CLevelingAbility> {
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
	public CLevelingAbility accept(final CAbilityAttack ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityMove ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityOrcBuild ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityHumanBuild ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityUndeadBuild ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityNightElfBuild ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityGenericDoNothing ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityColdArrows ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityNagaBuild ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityNeutralBuild ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityBuildInProgress ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityQueue ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityUpgrade ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityReviveHero ability) {
		return null;
	}

	@Override
	public CLevelingAbility accept(final GenericSingleIconActiveAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityRally ability) {
		if (this.rawcode.equals(RALLY_RAWCODE)) {
			return ability;
		}
		return null;
	}

	@Override
	public CLevelingAbility accept(final GenericNoIconAbility ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityReturnResources ability) {
		if (this.rawcode.equals(ability.getAlias())) {
			return ability;
		}
		return null;
	}

	@Override
	public CLevelingAbility accept(final CAbilityHero ability) {
		return null;
	}

}

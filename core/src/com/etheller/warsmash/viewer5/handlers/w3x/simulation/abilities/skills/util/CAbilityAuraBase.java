package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPassiveSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public abstract class CAbilityAuraBase extends CAbilityPassiveSpellBase {
	private static final float AURA_PERIODIC_CHECK_TIME = 2.00f;
	private static final int AURA_PERIODIC_CHECK_TIME_TICKS = (int) (Math
			.ceil(AURA_PERIODIC_CHECK_TIME / WarsmashConstants.SIMULATION_STEP_TIME));
	private War3ID buffId;
	private SimulationRenderComponent fx;
	private int nextAreaCheck = 0;

	public CAbilityAuraBase(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	public final void populateData(final GameObject worldEditorAbility, final int level) {
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		populateAuraData(worldEditorAbility, level);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.fx = game.createPersistentSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET, 0);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		this.fx.remove();
	}

	@Override
	public void onTick(final CSimulation game, final CUnit source) {
		final int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick >= nextAreaCheck) {
			game.getWorldCollision().enumUnitsInRange(source.getX(), source.getY(), getAreaOfEffect(), (enumUnit) -> {
				if (enumUnit.canBeTargetedBy(game, source, getTargetsAllowed())) {
					// TODO: the below system of adding an ability instead leveling it should maybe
					// be standardized
					final CLevelingAbility existingBuff = enumUnit
							.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(getBuffId()));
					boolean addNewBuff = false;
					final int level = getLevel();
					if (existingBuff == null) {
						addNewBuff = true;
					}
					else {
						if (existingBuff.getLevel() < level) {
							enumUnit.remove(game, existingBuff);
							addNewBuff = true;
						}
					}
					if (addNewBuff) {
						final CBuffAuraBase buff = createBuff(game.getHandleIdAllocator().createId(), source, enumUnit);
						buff.setAuraSourceUnit(source);
						buff.setAuraSourceAbility(this);
						buff.setLevel(game, source, level);
						enumUnit.add(game, buff);
					}
				}
				return false;
			});
			nextAreaCheck = gameTurnTick + AURA_PERIODIC_CHECK_TIME_TICKS;
		}
	}

	protected abstract CBuffAuraBase createBuff(int handleId, CUnit source, CUnit enumUnit);

	public abstract void populateAuraData(GameObject worldEditorAbility, int level);

	public War3ID getBuffId() {
		return buffId;
	}

	public void setBuffId(final War3ID buffId) {
		this.buffId = buffId;
	}
}

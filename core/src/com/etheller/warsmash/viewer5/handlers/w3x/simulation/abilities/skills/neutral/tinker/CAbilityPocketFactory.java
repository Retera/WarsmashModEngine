package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityPocketFactory extends CAbilityPointTargetSpellBase {
	private War3ID factoryUnitId;
	private War3ID factoryUnitBuffId;
	private float projectileSpeed;
	private War3ID spawnUnitId;
	private float leashRange;
	private float spawnInterval;
	private War3ID spawnUnitBuffId;
	private float spawnUnitDuration;
	private float spawnUnitOffset;

	public CAbilityPocketFactory(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.summonfactory;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.factoryUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0));
		this.factoryUnitBuffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level, 0);
		this.spawnUnitBuffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level, 1);
		projectileSpeed = worldEditorAbility.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, 0);

		this.spawnUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.DATA_B + level, 0));
		this.leashRange = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_E + level, 0);
		this.spawnInterval = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.spawnUnitDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		this.spawnUnitOffset = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		simulation.createProjectile(caster, getAlias(), caster.getX(), caster.getY(), (float) caster.angleTo(target),
				projectileSpeed, false, target, new CAbilityProjectileListener() {
					@Override
					public void onLaunch(final CSimulation game, CProjectile projectile, final AbilityTarget target) {

					}

					@Override
					public void onHit(final CSimulation game, CProjectile projectile, final AbilityTarget target) {
						final CUnit factoryUnit = simulation.createUnitSimple(CAbilityPocketFactory.this.factoryUnitId,
								caster.getPlayerIndex(), target.getX(), target.getY(),
								game.getGameplayConstants().getBuildingAngle());
						factoryUnit.addClassification(CUnitClassification.SUMMONED);
						factoryUnit.add(game, new CBuffTimedLife(game.getHandleIdAllocator().createId(),
								factoryUnitBuffId, getDuration(), false));
						final CAbilityFactory factory = new CAbilityFactory(game.getHandleIdAllocator().createId(),
								War3ID.fromString("ANfy"), War3ID.fromString("ANfy"));
						factory.setLeashRange(leashRange);
						factory.setSpawnUnitId(spawnUnitId);
						factory.setSpawnInterval(spawnInterval);
						factory.setBuffId(spawnUnitBuffId);
						factory.setDuration(spawnUnitDuration);
						factory.setAreaOfEffect(spawnUnitOffset);
						factory.setIconShowing(false);
						factoryUnit.add(game, factory);

						factoryUnit.add(game, new CAbilityRally(game.getHandleIdAllocator().createId()));
					}
				});
		return false;
	}
}

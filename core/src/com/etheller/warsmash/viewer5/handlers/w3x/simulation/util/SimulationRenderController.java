package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import java.awt.image.BufferedImage;

import com.badlogic.gdx.graphics.Color;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.TextTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CCollisionProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CJassProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CPsuedoProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public interface SimulationRenderController {
	CAttackProjectile createAttackProjectile(CSimulation simulation, float launchX, float launchY, float launchFacing,
			CUnit source, CUnitAttackMissile attack, AbilityTarget target, float damage, int bounceIndex,
			CUnitAttackListener attackListener);

	CAbilityProjectile createProjectile(CSimulation cSimulation, float launchX, float launchY, float launchFacing,
			float speed, boolean homing, CUnit source, War3ID spellAlias, AbilityTarget target,
			CAbilityProjectileListener projectileListener);

	CJassProjectile createJassProjectile(CSimulation cSimulation, float launchX, float launchY, float launchFacing,
			float speed, boolean homing, CUnit source, War3ID spellAlias, AbilityTarget target);

	CCollisionProjectile createCollisionProjectile(CSimulation cSimulation, float launchX, float launchY,
			float launchFacing, float projectileSpeed, boolean homing, CUnit source, War3ID spellAlias,
			AbilityTarget target, int maxHits, int hitsPerTarget, float startingRadius, float finalRadius,
			float collisionInterval, CAbilityCollisionProjectileListener projectileListener, boolean provideCounts);

	CPsuedoProjectile createPseudoProjectile(CSimulation cSimulation, float launchX, float launchY, float launchFacing,
			float projectileSpeed, float projectileStepInterval, int projectileArtSkip, boolean homing, CUnit source,
			War3ID spellAlias, CEffectType effectType, int effectArtIndex, AbilityTarget target, int maxHits,
			int hitsPerTarget, float startingRadius, float finalRadius,
			CAbilityCollisionProjectileListener projectileListener, boolean provideCounts);

	SimulationRenderComponentLightning createLightning(CSimulation simulation, War3ID lightningId, CUnit source,
			CUnit target);

	SimulationRenderComponentLightning createLightning(CSimulation simulation, War3ID lightningId, CUnit source,
			CUnit target, Float duration);

	SimulationRenderComponentLightning createAbilityLightning(CSimulation simulation, War3ID lightningId, CUnit source,
			CUnit target, int index);

	SimulationRenderComponentLightning createAbilityLightning(CSimulation simulation, War3ID lightningId, CUnit source,
			CUnit target, int index, Float duration);

	CUnit createUnit(CSimulation simulation, final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing);

	CItem createItem(CSimulation simulation, final War3ID typeId, final float x, final float y);

	CDestructable createDestructable(War3ID typeId, float x, float y, float facing, float scale, int variation);

	CDestructable createDestructableZ(War3ID typeId, float x, float y, float z, float facing, float scale,
			int variation);

	void createInstantAttackEffect(CSimulation cSimulation, CUnit source, CUnitAttackInstant attack, CWidget target);

	void spawnDamageSound(CWidget damagedDestructable, String weaponSound, String armorType);

	void spawnUnitConstructionSound(CUnit constructingUnit, CUnit constructedStructure);

	void removeUnit(CUnit unit);

	void removeDestructable(CDestructable dest);

	BufferedImage getBuildingPathingPixelMap(War3ID rawcode);

	BufferedImage getDestructablePathingPixelMap(War3ID rawcode);

	BufferedImage getDestructablePathingDeathPixelMap(War3ID rawcode);

	void spawnUnitConstructionFinishSound(CUnit constructedStructure);

	void spawnUnitUpgradeFinishSound(CUnit constructedStructure);

	void spawnDeathExplodeEffect(CUnit cUnit, War3ID explodesOnDeathBuffId);

	void spawnGainLevelEffect(CUnit cUnit);

	void spawnUnitReadySound(CUnit trainedUnit);

	void unitRepositioned(CUnit cUnit);

	TextTag spawnTextTag(CUnit unit, TextTagConfigType configType, int displayAmount);

	TextTag spawnTextTag(CUnit unit, TextTagConfigType configType, String message);

	TextTag createTextTag();

	void destroyTextTag(TextTag textTag);

	void spawnEffectOnUnit(CUnit unit, String effectPath);

	void spawnTemporarySpellEffectOnUnit(CUnit unit, War3ID alias, CEffectType effectType);

	SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(CUnit unit, War3ID alias, CEffectType effectType);

	SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(CUnit unit, War3ID alias, CEffectType effectType,
			int index);

	SimulationRenderComponentModel spawnSpellEffectOnPoint(float x, float y, float facing, War3ID alias,
			CEffectType effectType, int index);

	void spawnTemporarySpellEffectOnPoint(float x, float y, float facing, War3ID alias, CEffectType effectType,
			int index);

	void spawnUIUnitGetItemSound(CUnit cUnit, CItem item);

	void spawnUIUnitDropItemSound(CUnit cUnit, CItem item);

	SimulationRenderComponent spawnAbilitySoundEffect(CUnit caster, War3ID alias);

	SimulationRenderComponent loopAbilitySoundEffect(CUnit caster, War3ID alias);

	void stopAbilitySoundEffect(CUnit caster, War3ID alias);

	void unitPreferredSelectionReplacement(CUnit unit, CUnit newUnit);

	void heroRevived(CUnit trainedUnit);

	void heroDeathEvent(CUnit cUnit);

	SimulationRenderComponentModel createSpellEffectOverDestructable(CUnit source, CDestructable target, War3ID alias,
			float artAttachmentHeight);

	void unitUpgradingEvent(CUnit unit, War3ID upgradeIdType);

	void unitCancelUpgradingEvent(CUnit unit, War3ID upgradeIdType);

	void setBlight(float x, float y, float radius, boolean blighted);

	void unitUpdatedType(CUnit unit, War3ID typeId);

	void changeUnitColor(CUnit unit, int playerIndex);

	void changeUnitPlayerColor(CUnit unit, int previousColor, int newColor);

	void changeUnitVertexColor(CUnit unit, Color color);

	void changeUnitVertexColor(CUnit unit, float r, float g, float b);

	void changeUnitVertexColor(CUnit unit, float r, float g, float b, float a);

	float[] getUnitVertexColor(CUnit unit);

	int getTerrainHeight(float x, float y);

	boolean isTerrainRomp(float x, float y);

	boolean isTerrainWater(float x, float y);

}

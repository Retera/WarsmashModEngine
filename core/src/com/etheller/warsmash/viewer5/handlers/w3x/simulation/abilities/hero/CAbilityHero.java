package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;

public class CAbilityHero extends AbstractCAbility {
	private Set<War3ID> skillsAvailable;
	private int xp;
	private int heroLevel;
	private int skillPoints = 1;

	private HeroStatValue strength;
	private HeroStatValue agility;
	private HeroStatValue intelligence;
	private String properName;
	private boolean awaitingRevive;
	private boolean reviving;

	public CAbilityHero(final int handleId, final List<War3ID> skillsAvailable) {
		super(handleId, War3ID.fromString("AHer"));
		this.skillsAvailable = new LinkedHashSet<>(skillsAvailable);
		setPermanent(true);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.heroLevel = 1;
		this.xp = 0;
		final CUnitType unitType = unit.getUnitType();
		this.strength = new HeroStatValue(unitType.getStartingStrength(), unitType.getStrengthPerLevel());
		this.agility = new HeroStatValue(unitType.getStartingAgility(), unitType.getAgilityPerLevel());
		this.intelligence = new HeroStatValue(unitType.getStartingIntelligence(), unitType.getIntelligencePerLevel());
		calculateDerivatedFields(game, unit);

		final int properNamesCount = unitType.getProperNamesCount();
		final int nameIndex = properNamesCount > 0 ? game.getSeededRandom().nextInt(properNamesCount) : 0;

		String properName;
		final List<String> heroProperNames = unitType.getHeroProperNames();
		if (heroProperNames.size() > 0) {
			if (nameIndex < heroProperNames.size()) {
				properName = heroProperNames.get(nameIndex);
			}
			else {
				properName = heroProperNames.get(heroProperNames.size() - 1);
			}
		}
		else {
			properName = WarsmashConstants.DEFAULT_STRING;
		}
		this.properName = properName;
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		learnSkill(game, caster, orderIdAsRawtype);
		return false;
	}

	private void learnSkill(final CSimulation game, final CUnit caster, final War3ID skillId) {
		final CAbilityType<?> abilityType = game.getAbilityData().getAbilityType(skillId);
		if (abilityType != null) {
			this.skillPoints--;
			final CLevelingAbility existingAbility = caster
					.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(skillId));
			if (existingAbility == null) {
				final CAbility newAbility = abilityType.createAbility(game.getHandleIdAllocator().createId());
				caster.add(game, newAbility);
			}
			else {
				abilityType.setLevel(game, caster, existingAbility, existingAbility.getLevel() + 1);
			}
		}
		else {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
					"NOTEXTERN: Ability is not yet programmed, unable to learn!");
		}
	}

	public void selectHeroSkill(final CSimulation game, final CUnit caster, final War3ID skillId) {
		final BooleanAbilityActivationReceiver activationReceiver = BooleanAbilityActivationReceiver.INSTANCE;
		checkCanUse(game, caster, skillId.getValue(), activationReceiver);
		if (activationReceiver.isOk()) {
			learnSkill(game, caster, skillId);
		}
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		if (this.skillsAvailable.contains(orderIdAsRawtype)) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		if (this.skillsAvailable.contains(orderIdAsRawtype)) {
			if (this.skillPoints > 0) {
				final CAbilityData abilityData = game.getAbilityData();
				final int priorLevel = unit.getAbilityLevel(orderIdAsRawtype);
				final int heroRequiredLevel = abilityData.getHeroRequiredLevel(game, orderIdAsRawtype, priorLevel);
				final CAbilityType<?> abilityType = abilityData.getAbilityType(orderIdAsRawtype);
				// TODO check abilityType.getRequiredLevel() which api doesn't currently offer!!
				if ((abilityType == null) || (priorLevel < abilityType.getLevelCount())) {
					if (this.heroLevel >= heroRequiredLevel) {
						receiver.useOk();
					}
					else {
						receiver.missingHeroLevelRequirement(heroRequiredLevel);
					}
				}
				else {
					receiver.techtreeMaximumReached();
				}
			}
			else {
				receiver.noHeroSkillPointsAvailable();
			}
		}
		else {
			receiver.useOk();
		}
	}

	public int getSkillPoints() {
		return this.skillPoints;
	}

	public void setSkillPoints(final int skillPoints) {
		this.skillPoints = skillPoints;
	}

	public int getXp() {
		return this.xp;
	}

	public void setXp(final int xp) {
		this.xp = xp;
	}

	public int getHeroLevel() {
		return this.heroLevel;
	}

	public HeroStatValue getStrength() {
		return this.strength;
	}

	public HeroStatValue getAgility() {
		return this.agility;
	}

	public HeroStatValue getIntelligence() {
		return this.intelligence;
	}

	public String getProperName() {
		return this.properName;
	}

	public void setAwaitingRevive(final boolean awaitingRevive) {
		this.awaitingRevive = awaitingRevive;
	}

	public boolean isAwaitingRevive() {
		return this.awaitingRevive;
	}

	public void setReviving(final boolean reviving) {
		this.reviving = reviving;
	}

	public boolean isReviving() {
		return this.reviving;
	}

	private void levelUpHero(final CSimulation simulation, final CUnit unit, boolean showEffect) {
		final CGameplayConstants gameplayConstants = simulation.getGameplayConstants();
		while ((this.heroLevel < gameplayConstants.getMaxHeroLevel())
				&& (this.xp >= gameplayConstants.getNeedHeroXPSum(this.heroLevel))) {
			this.heroLevel++;
			this.skillPoints++;
			calculateDerivatedFields(simulation, unit);
			simulation.unitGainLevelEvent(unit, showEffect);
		}
	}

	public void addXp(final CSimulation simulation, final CUnit unit, final int xp, boolean showEffect) {
		this.xp += xp * simulation.getPlayer(unit.getPlayerIndex()).getHandicapXP();
		levelUpHero(simulation, unit, showEffect);
		unit.internalPublishHeroStatsChanged();
	}

	// In the original engine setXp is only called if the passed xp value > the
	// hero's current xp.
	// setXp cannot be used to decrease the hero's xp or level.
	public void setXp(final CSimulation simulation, final CUnit unit, final int xp, boolean showEyeCandy) {
		final int newXpVal = xp * Math.round(simulation.getPlayer(unit.getPlayerIndex()).getHandicapXP());
		if (newXpVal > this.xp) {
			addXp(simulation, unit, newXpVal - this.xp, showEyeCandy);
		}
	}

	public void setHeroLevel(final CSimulation simulation, final CUnit unit, final int level,
			final boolean showEffect) {
		final CGameplayConstants gameplayConstants = simulation.getGameplayConstants();
		final int neededTotalXp = gameplayConstants.getNeedHeroXPSum(level - 1);
		if (this.xp < neededTotalXp) {
			addXp(simulation, unit,
					(int) Math.ceil(
							(neededTotalXp - this.xp) / simulation.getPlayer(unit.getPlayerIndex()).getHandicapXP()),
					showEffect);
		}
		else {
			// remove xp TODO
		}
	}

	public void setStrengthBase(final CSimulation game, final CUnit unit, final int strengthBase) {
		this.strength.setBase(strengthBase);
		calculateDerivatedFields(game, unit);
	}

	public void setAgilityBase(final CSimulation game, final CUnit unit, final int agilityBase) {
		this.agility.setBase(agilityBase);
		calculateDerivatedFields(game, unit);
	}

	public void setIntelligenceBase(final CSimulation game, final CUnit unit, final int intelligenceBase) {
		this.intelligence.setBase(intelligenceBase);
		calculateDerivatedFields(game, unit);
	}

	public void addStrengthBonus(final CSimulation game, final CUnit unit, final int strengthBonus) {
		this.strength.setBonus(this.strength.getBonus() + strengthBonus);
		calculateDerivatedFields(game, unit);
	}

	public void addAgilityBonus(final CSimulation game, final CUnit unit, final int agilityBonus) {
		this.agility.setBonus(this.agility.getBonus() + agilityBonus);
		calculateDerivatedFields(game, unit);
	}

	public void addIntelligenceBonus(final CSimulation game, final CUnit unit, final int intelligenceBonus) {
		this.intelligence.setBonus(this.intelligence.getBonus() + intelligenceBonus);
		calculateDerivatedFields(game, unit);
	}

	public void addStrengthBase(final CSimulation game, final CUnit unit, final int strengthBonus) {
		this.strength.setBase(this.strength.getBase() + strengthBonus);
		calculateDerivatedFields(game, unit);
	}

	public void addAgilityBase(final CSimulation game, final CUnit unit, final int agilityBonus) {
		this.agility.setBase(this.agility.getBase() + agilityBonus);
		calculateDerivatedFields(game, unit);
	}

	public void addIntelligenceBase(final CSimulation game, final CUnit unit, final int intelligenceBonus) {
		this.intelligence.setBase(this.intelligence.getBase() + intelligenceBonus);
		calculateDerivatedFields(game, unit);
	}

	private HeroStatValue getStat(final CPrimaryAttribute attribute) {
		switch (attribute) {
		case AGILITY:
			return this.agility;
		case INTELLIGENCE:
			return this.intelligence;
		default:
		case STRENGTH:
			return this.strength;
		}
	}

	private void calculateDerivatedFields(final CSimulation game, final CUnit unit) {
		final CGameplayConstants gameplayConstants = game.getGameplayConstants();
		final int prevStrength = this.strength.getCurrent();
		final int prevAgility = this.agility.getCurrent();
		final int prevIntelligence = this.intelligence.getCurrent();
		this.strength.calculate(this.heroLevel);
		this.agility.calculate(this.heroLevel);
		this.intelligence.calculate(this.heroLevel);
		final int currentStrength = this.strength.getCurrent();
		final int deltaStrength = currentStrength - prevStrength;
		final int currentIntelligence = this.intelligence.getCurrent();
		final int deltaIntelligence = currentIntelligence - prevIntelligence;
		final int currentAgility = this.agility.getCurrent();
		final int currentAgilityBase = this.agility.getCurrentBase();
		final int currentAgilityBonus = this.agility.getBonus();

		final HeroStatValue primaryAttributeStat = getStat(unit.getUnitType().getPrimaryAttribute());
		final int primaryAttributeBase = primaryAttributeStat.getCurrentBase();
		final int primaryAttributeBonus = primaryAttributeStat.getBonus();
		final float agiAttackSpeedBonus = gameplayConstants.getAgiAttackSpeedBonus() * currentAgility;
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setPrimaryAttributePermanentDamageBonus(
					(int) (primaryAttributeBase * gameplayConstants.getStrAttackBonus()));
			attack.setPrimaryAttributeTemporaryDamageBonus(
					(int) (primaryAttributeBonus * gameplayConstants.getStrAttackBonus()));
			attack.setAgilityAttackSpeedBonus(agiAttackSpeedBonus);
		}

		final float hitPointIncrease = gameplayConstants.getStrHitPointBonus() * deltaStrength;
		final int oldMaximumLife = unit.getMaximumLife();
		final float oldLife = unit.getLife();
		final int newMaximumLife = Math.round(oldMaximumLife + hitPointIncrease);
		final float newLife = (oldLife * (newMaximumLife)) / oldMaximumLife;
		unit.setMaximumLife(newMaximumLife);
		unit.setLife(game, newLife);

		final float manaPointIncrease = gameplayConstants.getIntManaBonus() * deltaIntelligence;
		final int oldMaximumMana = unit.getMaximumMana();
		final float oldMana = unit.getMana();
		final int newMaximumMana = Math.round(oldMaximumMana + manaPointIncrease);
		final float newMana = (oldMana * (newMaximumMana)) / oldMaximumMana;
		unit.setMaximumMana(newMaximumMana);
		unit.setMana(newMana);

		final int agilityDefenseBonus = Math.round(
				gameplayConstants.getAgiDefenseBase() + (gameplayConstants.getAgiDefenseBonus() * currentAgilityBase));
		unit.setAgilityDefensePermanentBonus(agilityDefenseBonus);
		unit.setAgilityDefenseTemporaryBonus(gameplayConstants.getAgiDefenseBonus() * currentAgilityBonus);
		unit.setLifeRegenStrengthBonus(currentStrength * gameplayConstants.getStrRegenBonus());
		unit.setManaRegenIntelligenceBonus(currentIntelligence * gameplayConstants.getIntRegenBonus());
	}

	public void recalculateAllStats(final CSimulation game, final CUnit unit) {
		final CGameplayConstants gameplayConstants = game.getGameplayConstants();
		this.strength.calculate(this.heroLevel);
		this.agility.calculate(this.heroLevel);
		this.intelligence.calculate(this.heroLevel);
		final int currentStrength = this.strength.getCurrent();
		final int currentIntelligence = this.intelligence.getCurrent();
		final int currentAgility = this.agility.getCurrent();
		final int currentAgilityBase = this.agility.getCurrentBase();
		final int currentAgilityBonus = this.agility.getBonus();

		final HeroStatValue primaryAttributeStat = getStat(unit.getUnitType().getPrimaryAttribute());
		final int primaryAttributeBase = primaryAttributeStat.getCurrentBase();
		final int primaryAttributeBonus = primaryAttributeStat.getBonus();
		final float agiAttackSpeedBonus = gameplayConstants.getAgiAttackSpeedBonus() * currentAgility;
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setPrimaryAttributePermanentDamageBonus(
					(int) (primaryAttributeBase * gameplayConstants.getStrAttackBonus()));
			attack.setPrimaryAttributeTemporaryDamageBonus(
					(int) (primaryAttributeBonus * gameplayConstants.getStrAttackBonus()));
			attack.setAgilityAttackSpeedBonus(agiAttackSpeedBonus);
		}

		final float hitPointIncrease = gameplayConstants.getStrHitPointBonus() * currentStrength;
		final int oldMaximumLife = unit.getMaximumLife();
		final float oldLife = unit.getLife();
		final int newMaximumLife = Math.round(oldMaximumLife + hitPointIncrease);
		final float newLife = (oldLife * (newMaximumLife)) / oldMaximumLife;
		unit.setMaximumLife(newMaximumLife);
		unit.setLife(game, newLife);

		final float manaPointIncrease = gameplayConstants.getIntManaBonus() * currentIntelligence;
		final int oldMaximumMana = unit.getMaximumMana();
		final float oldMana = unit.getMana();
		final int newMaximumMana = Math.round(oldMaximumMana + manaPointIncrease);
		final float newMana = (oldMana * (newMaximumMana)) / oldMaximumMana;
		unit.setMaximumMana(newMaximumMana);
		unit.setMana(newMana);

		final int agilityDefenseBonus = Math.round(
				gameplayConstants.getAgiDefenseBase() + (gameplayConstants.getAgiDefenseBonus() * currentAgilityBase));
		unit.setAgilityDefensePermanentBonus(agilityDefenseBonus);
		unit.setAgilityDefenseTemporaryBonus(gameplayConstants.getAgiDefenseBonus() * currentAgilityBonus);
		unit.setLifeRegenStrengthBonus(currentStrength * gameplayConstants.getStrRegenBonus());
		unit.setManaRegenIntelligenceBonus(currentIntelligence * gameplayConstants.getIntRegenBonus());
	}

	public static final class HeroStatValue {
		private final float perLevelFactor;
		private int base;
		private int bonus;
		private int currentBase;
		private int current;

		private HeroStatValue(final int base, final float perLevelFactor) {
			this.base = base;
			this.perLevelFactor = perLevelFactor;
		}

		public void calculate(final int level) {
			this.currentBase = this.base + (int) ((level - 1) * this.perLevelFactor);
			this.current = this.currentBase + this.bonus;
		}

		public void setBase(final int base) {
			this.base = base;
		}

		public void setBonus(final int bonus) {
			this.bonus = bonus;
		}

		public int getBase() {
			return this.base;
		}

		public int getCurrentBase() {
			return this.currentBase;
		}

		public int getBonus() {
			return this.bonus;
		}

		public int getCurrent() {
			return this.current;
		}

		public String getDisplayText() {
			String text = Integer.toString(this.currentBase);
			if (this.bonus != 0) {
				if (this.bonus > 0) {
					text += "|cFF00FF00 +" + this.bonus + "";
				}
				else {
					text += "|cFFFF0000 " + this.bonus + "";
				}
			}
			return text;
		}
	}

	public Set<War3ID> getSkillsAvailable() {
		return this.skillsAvailable;
	}

	public void setSkillsAvailable(final List<War3ID> skillsAvailable) {
		this.skillsAvailable = new LinkedHashSet<>(skillsAvailable);
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityHero extends AbstractCAbility {
	private final List<War3ID> skillsAvailable;
	private int xp;
	private int heroLevel;
	private int skillPoints;

	private HeroStatValue strength;
	private HeroStatValue agility;
	private HeroStatValue intelligence;
	private String properName;

	public CAbilityHero(final int handleId, final List<War3ID> skillsAvailable) {
		super(handleId);
		this.skillsAvailable = skillsAvailable;
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

		final int nameIndex = game.getSeededRandom().nextInt(unitType.getProperNamesCount());

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
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId, AbilityTarget target) {
		return true;
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
		receiver.orderIdNotAccepted();
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
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

	public void setHeroLevel(final int level) {
		this.heroLevel = level;
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

	public void addXp(final CSimulation simulation, final CUnit unit, final int xp) {
		this.xp += xp;
		final CGameplayConstants gameplayConstants = simulation.getGameplayConstants();
		while ((this.heroLevel < gameplayConstants.getMaxHeroLevel())
				&& (this.xp >= gameplayConstants.getNeedHeroXPSum(this.heroLevel))) {
			this.heroLevel++;
			this.skillPoints++;
			calculateDerivatedFields(simulation, unit);
			simulation.unitGainLevelEvent(unit);
		}
		unit.internalPublishHeroStatsChanged();
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
		final int deltaStrength = this.strength.getCurrent() - prevStrength;
		final int deltaIntelligence = this.intelligence.getCurrent() - prevIntelligence;
		final int currentAgility = this.agility.getCurrent();
		final int deltaAgility = currentAgility - prevAgility;

		final int primaryAttribute = getStat(unit.getUnitType().getPrimaryAttribute()).getCurrent();
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setPrimaryAttributeDamageBonus((int) (primaryAttribute * gameplayConstants.getStrAttackBonus()));
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
				gameplayConstants.getAgiDefenseBase() + (gameplayConstants.getAgiDefenseBonus() * currentAgility));
		unit.setAgilityDefenseBonus(agilityDefenseBonus);
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

		public int getCurrent() {
			return this.current;
		}

		public String getDisplayText() {
			String text = Integer.toString(this.currentBase);
			if (this.bonus != 0) {
				if (this.bonus > 0) {
					text += "|cFF00FF00 (+" + this.bonus + ")";
				}
				else {
					text += "|cFFFF0000 (+" + this.bonus + ")";
				}
			}
			return text;
		}
	}
}

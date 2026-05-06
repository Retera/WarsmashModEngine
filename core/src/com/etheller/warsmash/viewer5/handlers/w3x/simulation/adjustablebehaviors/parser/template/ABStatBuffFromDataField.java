package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.template;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class ABStatBuffFromDataField {
	private ABStatBuffType type;
	private ABDataFieldLetter percentageBooleanField;
	private ABDataFieldLetter flatBooleanField;
	private ABDataFieldLetter dataField;
	private Boolean percentageOverride;
	private ABDataFieldLetter targetMeleeField;
	private ABDataFieldLetter targetRangeField;
	
	private transient NonStackingStatBuff buff;
	private transient NonStackingStatBuff secondAtkBuff;
	
	public ABStatBuffFromDataField(ABStatBuffFromDataField statBuff) {
		this.type = statBuff.getType();
		this.percentageBooleanField = statBuff.getPercentageBooleanField();
		this.flatBooleanField = statBuff.getFlatBooleanField();
		this.dataField = statBuff.getDataField();
		this.percentageOverride = statBuff.isPercentageOverride();
		this.targetMeleeField = statBuff.getTargetMeleeField();
		this.targetRangeField = statBuff.getTargetRangeField();
	}
	public ABStatBuffType getType() {
		return type;
	}
	public void setType(ABStatBuffType type) {
		this.type = type;
	}
	public ABDataFieldLetter getPercentageBooleanField() {
		return percentageBooleanField;
	}
	public void setPercentageBooleanField(ABDataFieldLetter percentageBooleanField) {
		this.percentageBooleanField = percentageBooleanField;
	}
	public ABDataFieldLetter getFlatBooleanField() {
		return flatBooleanField;
	}
	public void setFlatBooleanField(ABDataFieldLetter flatBooleanField) {
		this.flatBooleanField = flatBooleanField;
	}
	public ABDataFieldLetter getDataField() {
		return dataField;
	}
	public void setDataField(ABDataFieldLetter dataField) {
		this.dataField = dataField;
	}
	public Boolean isPercentageOverride() {
		return percentageOverride;
	}
	public void setPercentageOverride(Boolean percentageOverride) {
		this.percentageOverride = percentageOverride;
	}
	public ABDataFieldLetter getTargetMeleeField() {
		return targetMeleeField;
	}
	public void setTargetMeleeField(ABDataFieldLetter targetMeleeField) {
		this.targetMeleeField = targetMeleeField;
	}
	public ABDataFieldLetter getTargetRangeField() {
		return targetRangeField;
	}
	public void setTargetRangeField(ABDataFieldLetter targetRangeField) {
		this.targetRangeField = targetRangeField;
	}
	public NonStackingStatBuff getBuff() {
		return buff;
	}
	public void setBuff(NonStackingStatBuff buff) {
		this.buff = buff;
	}
	public NonStackingStatBuff getSecondAtkBuff() {
		return secondAtkBuff;
	}
	public void setSecondAtkBuff(NonStackingStatBuff secondAtkBuff) {
		this.secondAtkBuff = secondAtkBuff;
	}
	
	public NonStackingStatBuffType convertToNonStackingType(ABAbilityBuilderAbilityTypeLevelData abilityData) {
		boolean percentage = false;
		if (this.isPercentageOverride() != null) {
			percentage = this.isPercentageOverride();
		} else {
			if (this.getPercentageBooleanField() != null) {
				try {
					percentage = Integer.parseInt(abilityData.getData()
							.get(this.getPercentageBooleanField().getIndex())) == 1;
				} catch (NumberFormatException exc) {
					percentage = false;
				}
			} else if (this.getFlatBooleanField() != null) {
				try {
					percentage = !(Integer.parseInt(
							abilityData.getData().get(this.getFlatBooleanField().getIndex())) == 1);
				} catch (NumberFormatException exc) {
					percentage = true;
				}
			}
		}
		if (this.getType() == ABStatBuffType.ATK) {
			boolean targetMelee = false;
			boolean targetRange = false;
			if (this.getTargetMeleeField() != null) {
				targetMelee = Integer.parseInt(
						abilityData.getData().get(this.getTargetMeleeField().getIndex())) == 1;
			}
			if (this.getTargetRangeField() != null) {
				targetRange = Integer.parseInt(
						abilityData.getData().get(this.getTargetRangeField().getIndex())) == 1;
			}

			return this.getType().toAtkNonStackingStatBuffType(percentage, targetMelee, targetRange);
		} else {
			return this.getType().toNonStackingStatBuffType(percentage);
		}
	}
}

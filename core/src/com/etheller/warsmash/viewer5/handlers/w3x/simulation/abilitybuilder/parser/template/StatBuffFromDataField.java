package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class StatBuffFromDataField {
	private StatBuffType type;
	private DataFieldLetter percentageBooleanField;
	private DataFieldLetter flatBooleanField;
	private DataFieldLetter dataField;
	private Boolean percentageOverride;
	private DataFieldLetter targetMeleeField;
	private DataFieldLetter targetRangeField;
	
	private transient NonStackingStatBuff buff;
	private transient NonStackingStatBuff secondAtkBuff;
	
	public StatBuffType getType() {
		return type;
	}
	public void setType(StatBuffType type) {
		this.type = type;
	}
	public DataFieldLetter getPercentageBooleanField() {
		return percentageBooleanField;
	}
	public void setPercentageBooleanField(DataFieldLetter percentageBooleanField) {
		this.percentageBooleanField = percentageBooleanField;
	}
	public DataFieldLetter getFlatBooleanField() {
		return flatBooleanField;
	}
	public void setFlatBooleanField(DataFieldLetter flatBooleanField) {
		this.flatBooleanField = flatBooleanField;
	}
	public DataFieldLetter getDataField() {
		return dataField;
	}
	public void setDataField(DataFieldLetter dataField) {
		this.dataField = dataField;
	}
	public Boolean isPercentageOverride() {
		return percentageOverride;
	}
	public void setPercentageOverride(Boolean percentageOverride) {
		this.percentageOverride = percentageOverride;
	}
	public DataFieldLetter getTargetMeleeField() {
		return targetMeleeField;
	}
	public void setTargetMeleeField(DataFieldLetter targetMeleeField) {
		this.targetMeleeField = targetMeleeField;
	}
	public DataFieldLetter getTargetRangeField() {
		return targetRangeField;
	}
	public void setTargetRangeField(DataFieldLetter targetRangeField) {
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
}

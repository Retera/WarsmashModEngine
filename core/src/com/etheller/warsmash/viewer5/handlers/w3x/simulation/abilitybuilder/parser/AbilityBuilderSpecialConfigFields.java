package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.orderid.ABOrderIdCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeysEnum;

public class AbilityBuilderSpecialConfigFields {
	private ABIntegerCallback bufferManaRequired;
	private ABIntegerCallback manaDrainedPerSecond;
	
	private List<ABCondition> pointTargeted;
	private List<ABCondition> targetedSpell;

	private List<ABAction> autoAquireTarget;

	private ABIDCallback pairAbilityId;
	private ABIDCallback pairUnitId;
	private CommandStringErrorKeysEnum pairUnitTypeError;
	private CommandStringErrorKeysEnum cantTargetError;
	private CommandStringErrorKeysEnum cantPairError;
	private CommandStringErrorKeysEnum cantPairOffError;
	private ABFloatCallback pairSearchRadius;
	private ABBooleanCallback autoTargetPartner;
	private ABIntegerCallback maxPartners;
	private ABOrderIdCallback pairingOrderId;
	private ABOrderIdCallback pairingOffOrderId;
	private ABBooleanCallback orderPairedUnit;
	private ABOrderIdCallback orderPairedUnitOrderId;
	private ABOrderIdCallback orderPairedUnitOffOrderId;
	
	private CBehaviorCategory behaviorCategory;

	public ABIntegerCallback getBufferManaRequired() {
		return bufferManaRequired;
	}

	public void setBufferManaRequired(ABIntegerCallback bufferManaRequired) {
		this.bufferManaRequired = bufferManaRequired;
	}

	public ABIntegerCallback getManaDrainedPerSecond() {
		return manaDrainedPerSecond;
	}

	public void setManaDrainedPerSecond(ABIntegerCallback manaDrainedPerSecond) {
		this.manaDrainedPerSecond = manaDrainedPerSecond;
	}

	public List<ABCondition> getPointTargeted() {
		return pointTargeted;
	}

	public void setPointTargeted(List<ABCondition> pointTargeted) {
		this.pointTargeted = pointTargeted;
	}

	public List<ABCondition> getTargetedSpell() {
		return targetedSpell;
	}

	public void setTargetedSpell(List<ABCondition> targetedSpell) {
		this.targetedSpell = targetedSpell;
	}

	public List<ABAction> getAutoAquireTarget() {
		return autoAquireTarget;
	}

	public void setAutoAquireTarget(List<ABAction> autoAquireTarget) {
		this.autoAquireTarget = autoAquireTarget;
	}

	public ABIDCallback getPairAbilityId() {
		return pairAbilityId;
	}

	public void setPairAbilityId(ABIDCallback pairAbilityId) {
		this.pairAbilityId = pairAbilityId;
	}

	public ABIDCallback getPairUnitId() {
		return pairUnitId;
	}

	public void setPairUnitId(ABIDCallback pairUnitId) {
		this.pairUnitId = pairUnitId;
	}

	public CommandStringErrorKeysEnum getPairUnitTypeError() {
		return pairUnitTypeError;
	}

	public void setPairUnitTypeError(CommandStringErrorKeysEnum pairUnitTypeError) {
		this.pairUnitTypeError = pairUnitTypeError;
	}

	public CommandStringErrorKeysEnum getCantTargetError() {
		return cantTargetError;
	}

	public void setCantTargetError(CommandStringErrorKeysEnum cantTargetError) {
		this.cantTargetError = cantTargetError;
	}

	public CommandStringErrorKeysEnum getCantPairError() {
		return cantPairError;
	}

	public void setCantPairError(CommandStringErrorKeysEnum cantPairError) {
		this.cantPairError = cantPairError;
	}

	public CommandStringErrorKeysEnum getCantPairOffError() {
		return cantPairOffError;
	}

	public void setCantPairOffError(CommandStringErrorKeysEnum cantPairOffError) {
		this.cantPairOffError = cantPairOffError;
	}

	public ABFloatCallback getPairSearchRadius() {
		return pairSearchRadius;
	}

	public void setPairSearchRadius(ABFloatCallback pairSearchRadius) {
		this.pairSearchRadius = pairSearchRadius;
	}

	public ABBooleanCallback getAutoTargetPartner() {
		return autoTargetPartner;
	}

	public void setAutoTargetPartner(ABBooleanCallback autoTargetPartner) {
		this.autoTargetPartner = autoTargetPartner;
	}

	public ABIntegerCallback getMaxPartners() {
		return maxPartners;
	}

	public void setMaxPartners(ABIntegerCallback maxPartners) {
		this.maxPartners = maxPartners;
	}

	public ABOrderIdCallback getPairingOrderId() {
		return pairingOrderId;
	}

	public void setPairingOrderId(ABOrderIdCallback pairingOrderId) {
		this.pairingOrderId = pairingOrderId;
	}

	public ABOrderIdCallback getPairingOffOrderId() {
		return pairingOffOrderId;
	}

	public void setPairingOffOrderId(ABOrderIdCallback pairingOffOrderId) {
		this.pairingOffOrderId = pairingOffOrderId;
	}

	public ABBooleanCallback getOrderPairedUnit() {
		return orderPairedUnit;
	}

	public void setOrderPairedUnit(ABBooleanCallback orderPairedUnit) {
		this.orderPairedUnit = orderPairedUnit;
	}

	public ABOrderIdCallback getOrderPairedUnitOrderId() {
		return orderPairedUnitOrderId;
	}

	public void setOrderPairedUnitOrderId(ABOrderIdCallback orderPairedUnitOrderId) {
		this.orderPairedUnitOrderId = orderPairedUnitOrderId;
	}

	public ABOrderIdCallback getOrderPairedUnitOffOrderId() {
		return orderPairedUnitOffOrderId;
	}

	public void setOrderPairedUnitOffOrderId(ABOrderIdCallback orderPairedUnitOffOrderId) {
		this.orderPairedUnitOffOrderId = orderPairedUnitOffOrderId;
	}

	public CBehaviorCategory getBehaviorCategory() {
		return behaviorCategory;
	}

	public void setBehaviorCategory(CBehaviorCategory behaviorCategory) {
		this.behaviorCategory = behaviorCategory;
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUpgradeData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public final class CommandCardActivationReceiverPreviewCallback implements AbilityActivationReceiver {
    private CUnitData unitData;
    private CUpgradeData upgradeData;
    private FrameTemplateEnvironment templates;

    private boolean disabled;
    private boolean omitIconEntirely;
    private boolean notEnoughMana;
    private final StringBuilder requirementsTextBuilder = new StringBuilder();
    private float cooldownRemaining;
    private float cooldownMax;

    public CommandCardActivationReceiverPreviewCallback setup(CUnitData unitData, CUpgradeData upgradeData, FrameTemplateEnvironment templates) {
        this.unitData = unitData;
        this.upgradeData = upgradeData;
        this.templates = templates;
        return this;
    }

    public CommandCardActivationReceiverPreviewCallback reset() {
        this.disabled = false;
        this.omitIconEntirely = false;
        this.cooldownRemaining = 0;
        this.requirementsTextBuilder.setLength(0);
        return this;
    }

    @Override
    public void useOk() {

    }

    @Override
    public void unknownReasonUseNotOk() {

    }

    @Override
    public void notAnActiveAbility() {

    }

    @Override
    public void missingRequirement(final War3ID type, final int level) {
        final CUnitType unitType = unitData.getUnitType(type);
        String requirementString;
        if (unitType != null) {
            requirementString = unitType.getName();
        } else {
            final CUpgradeType upgradeType = upgradeData
                    .getType(type);
            if (upgradeType != null) {
                final CUpgradeType.UpgradeLevel upgradeLevel = upgradeType.getLevel(level - 1);
                if (upgradeLevel != null) {
                    requirementString = upgradeLevel.getName();
                } else {
                    requirementString = "NOTEXTERN Unknown Level of Upgrade ('" + type + "')";
                }
            } else {
                requirementString = "NOTEXTERN Unknown ('" + type + "')";
            }
        }
        missingRequirement(requirementString);
    }

    @Override
    public void missingHeroLevelRequirement(final int level) {
        missingRequirement(String.format(templates
                .getDecoratedString("INFOPANEL_LEVEL").replace("%u", "%d"), level));
    }

    public void missingRequirement(final String requirementString) {
        this.disabled = true;
        if (this.requirementsTextBuilder.length() == 0) {
            this.requirementsTextBuilder.append(templates
                    .getDecoratedString("REQUIRESTOOLTIP"));
            this.requirementsTextBuilder.append("|n - ");
        } else {
            this.requirementsTextBuilder.append(" - ");
        }
        this.requirementsTextBuilder.append(requirementString);
        this.requirementsTextBuilder.append("|n");
    }

    @Override
    public void noHeroSkillPointsAvailable() {
        this.disabled = true;
    }

    @Override
    public void techtreeMaximumReached() {
        this.omitIconEntirely = true;
    }

    @Override
    public void techItemAlreadyInProgress() {
        this.omitIconEntirely = true;
    }

    @Override
    public void activationCheckFailed(String commandStringErrorKey) {
        // TODO below we check == and not .equals(...) intentionally for performance
        // but maybe it should be an entirely different method instead
        if (commandStringErrorKey == CommandStringErrorKeys.NOT_ENOUGH_MANA) {
            this.notEnoughMana = true;
        }
    }

    @Override
    public void disabled() {
        this.disabled = true;
    }

    @Override
    public void cooldownNotYetReady(final float cooldownRemaining, final float cooldownMax) {
        this.cooldownRemaining = cooldownRemaining;
        this.cooldownMax = cooldownMax;

    }

    @Override
    public void noChargesRemaining() {

    }

    public boolean isShowingRequirements() {
        return this.requirementsTextBuilder.length() != 0;
    }

    public String getRequirementsText() {
        return this.requirementsTextBuilder.toString();
    }

    public boolean isOmitIconEntirely() {
        return omitIconEntirely;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isNotEnoughMana() {
        return notEnoughMana;
    }

    public float getCooldownMax() {
        return cooldownMax;
    }

    public float getCooldownRemaining() {
        return cooldownRemaining;
    }
}
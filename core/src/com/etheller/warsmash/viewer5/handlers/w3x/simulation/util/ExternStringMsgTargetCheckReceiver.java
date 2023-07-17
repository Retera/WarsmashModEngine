package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public final class ExternStringMsgTargetCheckReceiver<TARGET_TYPE> implements AbilityTargetCheckReceiver<TARGET_TYPE> {
    private static final ExternStringMsgTargetCheckReceiver<?> INSTANCE = new ExternStringMsgTargetCheckReceiver<>();

    public static <T> ExternStringMsgTargetCheckReceiver<T> getInstance() {
        return (ExternStringMsgTargetCheckReceiver<T>) INSTANCE;
    }

    private TARGET_TYPE target;
    private String externStringKey;

    public TARGET_TYPE getTarget() {
        return this.target;
    }

    public String getExternStringKey() {
        return externStringKey;
    }

    public ExternStringMsgTargetCheckReceiver<TARGET_TYPE> reset() {
        this.target = null;
        this.externStringKey = null;
        return this;
    }

    @Override
    public void targetOk(final TARGET_TYPE target) {
        this.target = target;
    }

    @Override
    public void notAnActiveAbility() {
        this.externStringKey = "";
    }

    @Override
    public void orderIdNotAccepted() {
        this.externStringKey = ""; // no meaningful error
    }

    @Override
    public void targetCheckFailed(String commandStringErrorKey) {
        this.externStringKey = commandStringErrorKey;
    }
}

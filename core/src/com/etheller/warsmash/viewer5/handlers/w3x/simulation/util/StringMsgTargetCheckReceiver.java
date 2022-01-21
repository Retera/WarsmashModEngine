package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public final class StringMsgTargetCheckReceiver<TARGET_TYPE> implements AbilityTargetCheckReceiver<TARGET_TYPE> {
	private static final StringMsgTargetCheckReceiver<?> INSTANCE = new StringMsgTargetCheckReceiver<>();

	public static <T> StringMsgTargetCheckReceiver<T> getInstance() {
		return (StringMsgTargetCheckReceiver<T>) INSTANCE;
	}

	private TARGET_TYPE target;
	private String message;

	public TARGET_TYPE getTarget() {
		return this.target;
	}

	public String getMessage() {
		return this.message;
	}

	public StringMsgTargetCheckReceiver<TARGET_TYPE> reset() {
		this.target = null;
		this.message = null;
		return this;
	}

	@Override
	public void targetOk(final TARGET_TYPE target) {
		this.target = target;
	}

	@Override
	public void mustTargetTeamType(final TeamType correctType) {
		switch (correctType) {
		case ALLIED:
			this.message = "NOTEXTERN: Must target an allied unit.";
			break;
		case ENEMY:
			this.message = "NOTEXTERN: Must target an enemy unit.";
			break;
		case PLAYER_UNITS:
			this.message = "NOTEXTERN: Unable to target a unit you do not control.";
			break;
		default:
			this.message = "NOTEXTERN: Must target team type: " + correctType;
		}
	}

	@Override
	public void mustTargetType(final TargetType correctType) {
		switch (correctType) {
		case POINT:
			this.message = "NOTEXTERN: Must target a point.";
			break;
		case UNIT:
			this.message = "NOTEXTERN: Must target a unit.";
			break;
		case UNIT_OR_POINT:
			this.message = "NOTEXTERN: Must target a unit or point.";
			break;
		default:
			this.message = "NOTEXTERN: Must target type: " + correctType;
		}
	}

	@Override
	public void notHolyBoltTarget() {
		this.message = "NOTEXTERN: Holybolttarget=Must target friendly living units or enemy Undead units.";
	}

	@Override
	public void alreadyFullHealth() {
		this.message = "NOTEXTERN: Target is already full health!";
	}

	@Override
	public void notDeathCoilTarget() {
		this.message = "NOTEXTERN: Deathcoiltarget=Must target enemy living units or friendly Undead units.";
	}

	@Override
	public void mustTargetResources() {
		this.message = "NOTEXTERN: Must target resources.";
	}

	@Override
	public void targetOutsideRange() {
		this.message = "NOTEXTERN: Target is outside range.";
	}

	@Override
	public void notAnActiveAbility() {
		this.message = "NOTEXTERN: Not an active ability.";
	}

	@Override
	public void targetNotVisible() {
		this.message = "NOTEXTERN: Target is not visible.";
	}

	@Override
	public void targetTooComplicated() {
		this.message = "NOTEXTERN: Target is too complicated.";
	}

	@Override
	public void targetNotInPlayableMap() {
		this.message = "NOTEXTERN: Target is not within the designed combat area.";
	}

	@Override
	public void orderIdNotAccepted() {
		this.message = "NOTEXTERN: OrderID not accepted.";
	}

}

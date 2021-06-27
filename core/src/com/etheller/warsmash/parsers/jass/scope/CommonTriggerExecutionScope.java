package com.etheller.warsmash.parsers.jass.scope;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimerJass;

public class CommonTriggerExecutionScope extends TriggerExecutionScope {
	private final CUnit triggeringUnit;
	private final CUnit filterUnit;
	private final CUnit enumUnit;
	private final CDestructable filterDestructable;
	private final CDestructable enumDestructable;
	private final CItem filterItem;
	private final CItem enumItem;
	private final CPlayerJass filterPlayer;
	private final CPlayerJass enumPlayer;
	private final CTimerJass expiringTimer;
	private final CUnit enteringUnit;
	private final CUnit leavingUnit;
	private final CRegion triggeringRegion;

	public CommonTriggerExecutionScope(final TriggerExecutionScope parentScope, final CUnit triggeringUnit,
			final CUnit filterUnit, final CUnit enumUnit, final CDestructable filterDestructable,
			final CDestructable enumDestructable, final CItem filterItem, final CItem enumItem,
			final CPlayerJass filterPlayer, final CPlayerJass enumPlayer, final CTimerJass expiringTimer,
			final CUnit enteringUnit, final CUnit leavingUnit, final CRegion triggeringRegion) {
		super(parentScope.getTriggeringTrigger());
		this.triggeringUnit = triggeringUnit;
		this.filterUnit = filterUnit;
		this.enumUnit = enumUnit;
		this.filterDestructable = filterDestructable;
		this.enumDestructable = enumDestructable;
		this.filterItem = filterItem;
		this.enumItem = enumItem;
		this.filterPlayer = filterPlayer;
		this.enumPlayer = enumPlayer;
		this.expiringTimer = expiringTimer;
		this.enteringUnit = enteringUnit;
		this.leavingUnit = leavingUnit;
		this.triggeringRegion = triggeringRegion;
	}

	public CUnit getEnumUnit() {
		return this.enumUnit;
	}

	public CUnit getTriggeringUnit() {
		return this.triggeringUnit;
	}

	public CUnit getFilterUnit() {
		return this.filterUnit;
	}

	public CDestructable getFilterDestructable() {
		return this.filterDestructable;
	}

	public CDestructable getEnumDestructable() {
		return this.enumDestructable;
	}

	public CItem getFilterItem() {
		return this.filterItem;
	}

	public CItem getEnumItem() {
		return this.enumItem;
	}

	public CPlayerJass getFilterPlayer() {
		return this.filterPlayer;
	}

	public CPlayerJass getEnumPlayer() {
		return this.enumPlayer;
	}

	public CTimerJass getExpiringTimer() {
		return this.expiringTimer;
	}

	public CUnit getEnteringUnit() {
		return this.enteringUnit;
	}

	public CUnit getLeavingUnit() {
		return this.leavingUnit;
	}

	public CRegion getTriggeringRegion() {
		return this.triggeringRegion;
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CUnit filterUnit) {
		return new CommonTriggerExecutionScope(parentScope, null, filterUnit, null, null, null, null, null, null, null,
				null, null, null, null);
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope, final CUnit enumUnit) {
		return new CommonTriggerExecutionScope(parentScope, null, null, enumUnit, null, null, null, null, null, null,
				null, null, null, null);
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CPlayerJass filterPlayer) {
		return new CommonTriggerExecutionScope(parentScope, null, null, null, null, null, null, null, filterPlayer,
				null, null, null, null, null);
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope,
			final CPlayerJass enumPlayer) {
		return new CommonTriggerExecutionScope(parentScope, null, null, null, null, null, null, null, null, enumPlayer,
				null, null, null, null);
	}

	public static CommonTriggerExecutionScope expiringTimer(final CTimerJass cTimerJass) {
		return new CommonTriggerExecutionScope(TriggerExecutionScope.EMPTY, null, null, null, null, null, null, null,
				null, null, cTimerJass, null, null, null);
	}

	public static CommonTriggerExecutionScope unitEnterRegionScope(final TriggerExecutionScope parentScope,
			final CUnit enteringUnit, final CRegion triggeringRegion) {
		return new CommonTriggerExecutionScope(parentScope, null, null, null, null, null, null, null, null, null, null,
				enteringUnit, null, triggeringRegion);
	}

	public static CommonTriggerExecutionScope unitLeaveRegionScope(final TriggerExecutionScope parentScope,
			final CUnit leavingUnit, final CRegion triggeringRegion) {
		return new CommonTriggerExecutionScope(parentScope, null, null, null, null, null, null, null, null, null, null,
				null, leavingUnit, triggeringRegion);
	}

}

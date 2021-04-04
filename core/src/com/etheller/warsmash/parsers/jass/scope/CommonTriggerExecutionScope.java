package com.etheller.warsmash.parsers.jass.scope;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;

public class CommonTriggerExecutionScope extends TriggerExecutionScope {
	private final CUnit triggeringUnit;
	private final CUnit filterUnit;
	private final CUnit enumUnit;
	private final CPlayerJass filterPlayer;
	private final CPlayerJass enumPlayer;

	public CommonTriggerExecutionScope(final TriggerExecutionScope parentScope, final CUnit triggeringUnit,
			final CUnit filterUnit, final CUnit enumUnit, final CPlayerJass filterPlayer,
			final CPlayerJass enumPlayer) {
		super(parentScope.getTriggeringTrigger());
		this.triggeringUnit = triggeringUnit;
		this.filterUnit = filterUnit;
		this.enumUnit = enumUnit;
		this.filterPlayer = filterPlayer;
		this.enumPlayer = enumPlayer;
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

	public CPlayerJass getFilterPlayer() {
		return this.filterPlayer;
	}

	public CPlayerJass getEnumPlayer() {
		return this.enumPlayer;
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CUnit filterUnit) {
		return new CommonTriggerExecutionScope(parentScope, null, filterUnit, null, null, null);
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope, final CUnit enumUnit) {
		return new CommonTriggerExecutionScope(parentScope, null, null, enumUnit, null, null);
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CPlayerJass filterPlayer) {
		return new CommonTriggerExecutionScope(parentScope, null, null, null, filterPlayer, null);
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope,
			final CPlayerJass enumPlayer) {
		return new CommonTriggerExecutionScope(parentScope, null, null, null, null, enumPlayer);
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.jass;

import java.util.Collections;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;

public class CBehaviorJass implements CBehavior {
	private final int highlightOrderId;
	private final JassFunction updateFunction;
	private final GlobalScope globalScope;
	private CAbilityJass jassAbility;

	public CBehaviorJass(final int highlightOrderId, final JassFunction updateFunction, final GlobalScope globalScope) {
		this.highlightOrderId = highlightOrderId;
		this.updateFunction = updateFunction;
		this.globalScope = globalScope;
	}

	public void setAbility(final CAbilityJass jassAbility) {
		this.jassAbility = jassAbility;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		return this.updateFunction
				.call(Collections.emptyList(), this.globalScope,
						TriggerExecutionScope.EMPTY /* TODO this.jassAbility.getJassAbilityBasicScope() */)
				.visit(ObjectJassValueVisitor.getInstance());
	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}

}

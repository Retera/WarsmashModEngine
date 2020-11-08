package com.etheller.warsmash.viewer5.handlers.w3x.simulation.test;

import java.awt.Point;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

/*
 * IAbility
  Execute(unit caster, int orderId, unit targetUnit, point targetPoint);

IBehavior
  ResolveNext();

IState
  Execute();

abstract BaseState
  ctor(unit unit, IBehavior behavior)
  abstract Execute();

abstract BaseBehavior
  ctor(unit unit)

 */
public interface IAbility {
	void execute(CUnit caster, int orderId, CWidget targetUnit, Point targetPoint);
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CWidgetEvent;

public abstract class CWidget implements AbilityTarget, CHandle {
	protected static final Rectangle tempRect = new Rectangle();
	private final int handleId;
	private float x;
	private float y;
	protected float life;
	private final EnumMap<JassGameEventsWar3, List<CWidgetEvent>> eventTypeToEvents = new EnumMap<>(
			JassGameEventsWar3.class);

	public CWidget(final int handleId, final float x, final float y, final float life) {
		this.handleId = handleId;
		this.x = x;
		this.y = y;
		this.life = life;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	public float getLife() {
		return this.life;
	}

	public abstract float getMaxLife();

	protected void setX(final float x) {
		this.x = x;
	}

	protected void setY(final float y) {
		this.y = y;
	}

	public void setLife(final CSimulation simulation, final float life) {
		this.life = life;
	}

	public abstract void damage(final CSimulation simulation, final CUnit source, final CAttackType attackType,
			final String weaponType, final float damage);

	public abstract float getFlyHeight();

	public abstract float getImpactZ();

	public boolean isDead() {
		return this.life <= 0;
	}

	public abstract boolean canBeTargetedBy(CSimulation simulation, CUnit source,
			final EnumSet<CTargetType> targetsAllowed);

	public double distanceSquaredNoCollision(final AbilityTarget target) {
		return distanceSquaredNoCollision(target.getX(), target.getY());
	}

	public double distanceSquaredNoCollision(final float targetX, final float targetY) {
		final double dx = targetX - getX();
		final double dy = targetY - getY();
		return (dx * dx) + (dy * dy);
	}

	public abstract boolean isInvulnerable();

	public void fireDeathEvents(final CSimulation simulation) {
		fireEvents(CommonTriggerExecutionScope::widgetTriggerScope, JassGameEventsWar3.EVENT_WIDGET_DEATH);
	}

	private List<CWidgetEvent> getOrCreateEventList(final JassGameEventsWar3 eventType) {
		List<CWidgetEvent> playerEvents = this.eventTypeToEvents.get(eventType);
		if (playerEvents == null) {
			playerEvents = new ArrayList<>();
			this.eventTypeToEvents.put(eventType, playerEvents);
		}
		return playerEvents;
	}

	protected List<CWidgetEvent> getEventList(final JassGameEventsWar3 eventType) {
		return this.eventTypeToEvents.get(eventType);
	}

	public RemovableTriggerEvent addEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType) {
		final CWidgetEvent playerEvent = new CWidgetEvent(globalScope, this, whichTrigger, eventType, null);
		getOrCreateEventList(eventType).add(playerEvent);
		return playerEvent;
	}

	public void removeEvent(final CWidgetEvent playerEvent) {
		final List<CWidgetEvent> eventList = getEventList(playerEvent.getEventType());
		if (eventList != null) {
			eventList.remove(playerEvent);
		}
	}

	private void fireEvents(final CommonTriggerExecutionScope.WidgetEventScopeBuilder eventScopeBuilder,
			final JassGameEventsWar3 eventType) {
		final List<CWidgetEvent> eventList = getEventList(eventType);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, eventScopeBuilder.create(eventType, event.getTrigger(), this));
			}
		}
	}

	public RemovableTriggerEvent addDeathEvent(final GlobalScope globalScope, final Trigger whichTrigger) {
		return addEvent(globalScope, whichTrigger, JassGameEventsWar3.EVENT_WIDGET_DEATH);
	}
}

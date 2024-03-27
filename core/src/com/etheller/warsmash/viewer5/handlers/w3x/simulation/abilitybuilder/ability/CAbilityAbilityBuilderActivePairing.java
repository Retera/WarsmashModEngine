package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CPairingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorSendOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.iterstructs.UnitAndRange;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeysEnum;

public class CAbilityAbilityBuilderActivePairing extends CAbilityAbilityBuilderGenericActive
		implements CPairingAbility {
	private ABBehavior behavior;

	private War3ID pairUnitId = null;
	private War3ID pairAbilityId = null;

	private boolean autoTargetPartner = false;
	private float pairSearchRadius = 0;

	private int internalOrderId = -1;
	private int maxPartners = 1;

	private boolean orderPairedUnit = false;
	private int orderPairedUnitOrderId = -1;

	private int internalOffOrderId = -1;
	private int orderPairedUnitOffOrderId = -1;

	public CAbilityAbilityBuilderActivePairing(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, code, alias, levelData, config, localStore);

		if (this.castingPrimaryTag == null) {
			this.castingPrimaryTag = PrimaryTag.STAND;
		}
		
		this.allowCastlessDeactivate = false;
	}

	private void setPairingValues(CSimulation game, CUnit unit) {

		if (this.config.getSpecialFields() != null) {
			if (this.config.getSpecialFields().getPairAbilityId() != null) {
				this.pairAbilityId = this.config.getSpecialFields().getPairAbilityId().callback(game, unit, localStore,
						castId);
			}
			if (this.config.getSpecialFields().getPairUnitId() != null) {
				this.pairUnitId = this.config.getSpecialFields().getPairUnitId().callback(game, unit, localStore,
						castId);
			}

			if (this.config.getSpecialFields().getAutoTargetPartner() != null) {
				this.autoTargetPartner = this.config.getSpecialFields().getAutoTargetPartner().callback(game, unit,
						localStore, castId);
			}
			if (this.config.getSpecialFields().getPairSearchRadius() != null) {
				this.pairSearchRadius = this.config.getSpecialFields().getPairSearchRadius().callback(game, unit,
						localStore, castId);
			}

			if (this.config.getSpecialFields().getPairingOrderId() != null) {
				this.internalOrderId = this.config.getSpecialFields().getPairingOrderId().callback(game, unit,
						localStore, castId);
			}

			if (this.config.getSpecialFields().getPairingOffOrderId() != null) {
				this.internalOffOrderId = this.config.getSpecialFields().getPairingOffOrderId().callback(game, unit,
						localStore, castId);
			}
			if (this.config.getSpecialFields().getMaxPartners() != null) {
				this.maxPartners = this.config.getSpecialFields().getMaxPartners().callback(game, unit, localStore,
						castId);
			}
			if (this.config.getSpecialFields().getOrderPairedUnit() != null) {
				this.orderPairedUnit = this.config.getSpecialFields().getOrderPairedUnit().callback(game, unit,
						localStore, castId);
			}
			if (this.config.getSpecialFields().getOrderPairedUnitOrderId() != null) {
				this.orderPairedUnitOrderId = this.config.getSpecialFields().getOrderPairedUnitOrderId().callback(game,
						unit, localStore, castId);
			}
			if (this.config.getSpecialFields().getOrderPairedUnitOffOrderId() != null) {
				this.orderPairedUnitOffOrderId = this.config.getSpecialFields().getOrderPairedUnitOffOrderId().callback(game,
						unit, localStore, castId);
			}
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.PAIRABILITY, this);
		this.behavior = this.createRangedBehavior(unit);
		this.behavior.setInstant(true);
		super.onAdd(game, unit);
		this.setPairingValues(game, unit);
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		this.setPairingValues(game, unit);
	}

	protected boolean innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
//		System.err.println(unit.getUnitType().getName() + " Checking can use order: " + orderId + " (Base: "
//				+ this.getBaseOrderId() + ", Internal: " + this.getPairOrderId(game, unit) + ")");
		if (checkNoTargetOrderId(game, unit, orderId)) {
			Set<CUnit> partners = this.findPairUnits(game, unit);
			if (partners == null || partners.isEmpty()) {
				boolean isOffId = orderId == this.getOffOrderId() || orderId == this.getPairOffOrderId(game, unit);
				if (!isOffId && this.config.getSpecialFields().getCantPairError() != null) {
//					System.out.println("Use check failed: no parter, special message");
					receiver.activationCheckFailed(this.config.getSpecialFields().getCantPairError().getKey());
					return false;
				} else if (isOffId && this.config.getSpecialFields().getCantPairOffError() != null) {
//					System.out.println("Use check failed: no parter, special message");
					receiver.activationCheckFailed(this.config.getSpecialFields().getCantPairOffError().getKey());
					return false;
				} else {
//					System.out.println("Use check failed: no parter");
					receiver.activationCheckFailed(CommandStringErrorKeysEnum.UNABLE_TO_FIND_COUPLE_TARGET.getKey());
					return false;
				}
			}
//			System.err.println("Check use: Partner found");
		}
		return true;
	}

	@Override
	protected boolean innerCheckCastOrderId(final CSimulation game, final CUnit unit, final int orderId) {
		return orderId == getBaseOrderId() || orderId == this.getPairOrderId(game, unit) || offOrderId(orderId)
				|| orderId == this.getPairOffOrderId(game, unit);
	}

	private boolean checkNoTargetOrderId(final CSimulation game, final CUnit unit, final int orderId) {
		return this.autoTargetParter(game, unit) && (onOrderId(orderId) || offOrderId(orderId));
	}

	private boolean checkTargetPrimeOrderId(final CSimulation game, final CUnit unit, final int orderId) {
		return !this.autoTargetParter(game, unit) && (onOrderId(orderId) || offOrderId(orderId));
	}

	private boolean checkTargetInternalOrderId(final CSimulation game, final CUnit unit, final int orderId) {
		return (((!this.active || this.separateOnAndOff)
				&& orderId == this.getPairOrderId(game, unit))
				|| ((this.toggleable && this.active) || this.separateOnAndOff)
						&& orderId == this.getPairOffOrderId(game, unit));
	}

	private boolean onOrderId(final int orderId) {
		return (!this.active || this.separateOnAndOff) && orderId == this.getBaseOrderId();
	}

	private boolean offOrderId(final int orderId) {
		return ((this.toggleable && this.active) || this.separateOnAndOff) && orderId == this.getOffOrderId();
	}

	// ----
	// Non-Targeted
	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		if (checkNoTargetOrderId(game, caster, orderId)) {

//			System.err.println(caster.getUnitType().getName() + " Beginning NoTarget: " + orderId);
			boolean isOffId = orderId == this.getOffOrderId();
			Set<CUnit> partners = this.findPairUnits(game, caster);
			CUnit finalPartner = null;
			if (partners != null) {
//				System.err.println(caster.getUnitType().getName() + " Found Partners");
				int sendOrderId = isOffId ? this.orderPairedUnitOffOrderId(game, caster)
						: this.orderPairedUnitOrderId(game, caster);
//				System.err.println(caster.getUnitType().getName() + " isOffId = " + isOffId + " so picking between " + this.orderPairedUnitOrderId(game, caster) + " and " + this.orderPairedUnitOffOrderId(game, caster));
				boolean ordered = sendOrderId < 0;
				for (CUnit partner : partners) {
					if (sendOrderId >= 0) {
						ordered |= partner.order(game, sendOrderId, caster);
//						System.err.println(caster.getUnitType().getName() + " Sending order to " + partner.getUnitType().getName() + " (" + sendOrderId + ")");
					}
					if ((isOffId ? this.getPairOffOrderId(game, caster) : this.getPairOrderId(game, caster)) >= 0) {
//						System.err.println(caster.getUnitType().getName() + " Saved last partner for self-order: "
//								+ partner.getUnitType().getName());
						finalPartner = partner;
					}
				}
				if (!ordered) {
					// Failed to order any partners despite wanting to
//					System.err.println(
//							caster.getUnitType().getName() + " Attempted to order parter(s) but failed to order any: "
//									+ (isOffId ? this.orderPairedUnitOffOrderId(game, caster)
//											: this.orderPairedUnitOrderId(game, caster)));
					if (!isOffId && this.config.getSpecialFields().getCantPairError() != null) {
						game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
								this.config.getSpecialFields().getCantPairError().getKey());
					} else if (isOffId && this.config.getSpecialFields().getCantPairOffError() != null) {
						game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
								this.config.getSpecialFields().getCantPairOffError().getKey());
					} else {
						game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
								CommandStringErrorKeysEnum.UNABLE_TO_FIND_COUPLE_TARGET.getKey());
					}
					return null;
				}
			}
			if (finalPartner != null
					&& (isOffId ? this.getPairOffOrderId(game, caster) : this.getPairOrderId(game, caster)) >= 0) {
//				System.err.println(caster.getUnitType().getName() + " Have final partner, issuing self order behavior");
				if (isOffId) {
					return new CBehaviorSendOrder(caster, this, this.getPairOffOrderId(game, caster),
							this.getBaseOrderId(), finalPartner);
				}
				return new CBehaviorSendOrder(caster, this, this.getPairOrderId(game, caster), this.getBaseOrderId(),
						finalPartner);
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (checkNoTargetOrderId(game, unit, orderId)) {
			return true;
		} else {
			receiver.orderIdNotAccepted();
			return false;
		}
	}

	// ----
	// Targeted
	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		this.castId++;
//		System.err.println(caster.getUnitType().getName() + " Received pair target order: " + orderId + " (Base: "
//				+ this.getBaseOrderId() + ", Internal: " + this.getPairOrderId(game, caster) + ")");
		if (checkTargetPrimeOrderId(game, caster, orderId)) {
			final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			if (this.orderPairedUnit(game, caster) && this.orderPairedUnitOrderId(game, caster) != null) {
//				System.err.println(caster.getUnitType().getName() + " Sending internal order to paired unit");
				boolean ordered = targetUnit.order(game, this.orderPairedUnitOrderId(game, caster), caster);
				if (!ordered) {
					if (this.config.getSpecialFields() != null
							&& this.config.getSpecialFields().getCantPairError() != null) {
						game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
								this.config.getSpecialFields().getCantPairError().getKey());
					} else {
						game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
								CommandStringErrorKeysEnum.UNABLE_TO_MERGE_WITH_THAT_UNIT.getKey());
					}
					return caster.pollNextOrderBehavior(game);
				}
			}
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
			this.localStore.put(ABLocalStoreKeys.ABILITYPAIREDUNIT + castId, targetUnit);
//			System.out.println("Starting targeted behavior");

			this.runOnOrderIssuedActions(game, caster, orderId);
			this.behavior.setCastId(castId);
			return this.behavior.reset(game, target);
		} else if (checkTargetInternalOrderId(game, caster, orderId)) {
//			System.err.println(caster.getUnitType().getName() + " Got internal order");
			final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
			this.localStore.put(ABLocalStoreKeys.ABILITYPAIREDUNIT + castId, targetUnit);
//			System.out.println("Starting internal targeted behavior with target: " + targetUnit);

			this.runOnOrderIssuedActions(game, caster, orderId);
			this.behavior.setCastId(castId);
			return this.behavior.reset(game, target, orderId);
		} else {
			return null;
		}
	}

	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
//		System.err.println(unit.getUnitType().getName() + " Checking can pair target order: " + orderId + " (Base: "
//				+ this.getBaseOrderId() + ", Internal: " + this.getPairOrderId(game, unit) + ")");
		if (checkTargetPrimeOrderId(game, unit, orderId)
				|| checkTargetInternalOrderId(game, unit, orderId)) {
			final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);

			if (targetUnit != null && unit.getPlayerIndex() != targetUnit.getPlayerIndex()) {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ONE_OF_YOUR_OWN_UNITS);
				return false;
			}

			if (this.config.getSpecialFields() != null
					&& this.config.getSpecialFields().getPairUnitTypeError() != null) {
				if (getPairUnitID(game, unit) != null && !targetUnit.getTypeId().equals(getPairUnitID(game, unit))) {
					receiver.targetCheckFailed(this.config.getSpecialFields().getPairUnitTypeError().getKey());
					return false;
				}
			}
			if (!this.canPairWith(game, unit, targetUnit)) {
				if (this.config.getSpecialFields() != null
						&& this.config.getSpecialFields().getCantTargetError() != null) {
					receiver.targetCheckFailed(this.config.getSpecialFields().getCantTargetError().getKey());
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_MERGE_WITH_THAT_UNIT);
				}
				return false;
			}
			return true;
		} else {
			receiver.orderIdNotAccepted();
			return false;
		}
	}

	// Method to search out partner unit
	private boolean canPairWith(CSimulation game, CUnit caster, CUnit target) {
		if (caster.isPaused() || target.isPaused() || caster.getPlayerIndex() != target.getPlayerIndex()) {
//			System.err.println(
//					caster.getUnitType().getName() + " Couldn't pair: unit is owned by different player, or is paused");
			return false;
		}
		if (getPairUnitID(game, caster) != null && !target.getTypeId().equals(getPairUnitID(game, caster))) {
//			System.err.println(caster.getUnitType().getName() + " Couldn't pair: wrong unit id (Want:"
//					+ getPairUnitID(game, caster) + ", Got:" + target.getTypeId() + ")");
			return false;
		}
		if (getPairAbilityCode(game, caster) != null) {
			for (CAbility ability : target.getAbilities()) {
				if (ability.getCode().equals(this.getPairAbilityCode(game, caster))) {
					localStore.put(ABLocalStoreKeys.LASTPARTNERABILITY, ability);
					return true;
				}
			}
//			System.err.println(caster.getUnitType().getName() + " Couldn't pair: No matching ability (Want:"
//					+ getPairAbilityCode(game, caster) + ")");
		}
		return false;
	}

	@Override
	public Set<CUnit> findPairUnits(CSimulation game, CUnit caster) {
		if (this.getPairAbilityCode(game, caster) != null || this.getPairUnitID(game, caster) != null) {
			final Set<CUnit> retSet = new HashSet<>();
			if (this.maxPartners(game, caster) != 1) {
				game.getWorldCollision().enumUnitsInRange(caster.getX(), caster.getY(),
						this.getPairSearchRadius(game, caster), (enumUnit) -> {
							if ((enumUnit != caster) && canPairWith(game, caster, enumUnit)) {
								retSet.add(enumUnit);
							}
							return maxPartners(game, caster) != 0 && retSet.size() >= maxPartners(game, caster);
						});
			} else {
				final UnitAndRange ur = new UnitAndRange();
				Rectangle rect = new Rectangle();
				float rangeVal = this.getPairSearchRadius(game, caster);

				rect.set(caster.getX() - rangeVal, caster.getY() - rangeVal, rangeVal * 2, rangeVal * 2);
				game.getWorldCollision().enumUnitsInRect(rect, new CUnitEnumFunction() {
					@Override
					public boolean call(final CUnit enumUnit) {
						if (caster.canReach(enumUnit, rangeVal)) {
							double dist = caster.distance(enumUnit);
							if (ur.getUnit() == null || ur.getRange() > dist) {
								if ((enumUnit != caster) && canPairWith(game, caster, enumUnit)) {
									ur.setRange(dist);
									ur.setUnit(enumUnit);
								}
							}
						}
						return false;
					}
				});
				if (ur.getUnit() != null) {
					retSet.add(ur.getUnit());
				}
			}
			if (retSet.size() > 0) {
				return retSet;
			}
		}
		return null;
	}

	// Methods to identify partner unit(s)
	@Override
	public War3ID getPairAbilityCode(CSimulation game, CUnit caster) {
		return this.pairAbilityId;
	}

	@Override
	public War3ID getPairUnitID(CSimulation game, CUnit caster) {
		return this.pairUnitId;
	}

	@Override
	public float getPairSearchRadius(CSimulation game, CUnit caster) {
		if (this.pairSearchRadius == 0) {
			return Float.MAX_VALUE;
		}
		return this.pairSearchRadius;
	}

	// Methods to determine how targeting works
	@Override
	public boolean autoTargetParter(CSimulation game, CUnit caster) {
		return this.autoTargetPartner;
	}

	@Override
	public int maxPartners(CSimulation game, CUnit caster) {
		return this.maxPartners;
	} // should only be one if autoTargetParter is false

	// Internal order to give to casting unit (generally used if auto targeting a
	// partner. Not always needed)
	@Override
	public Integer getPairOrderId(CSimulation game, CUnit caster) {
		return this.internalOrderId;
	}

	@Override
	public Integer getPairOffOrderId(CSimulation game, CUnit caster) {
		return this.internalOffOrderId;
	}

	// Optional order to send to paired unit
	@Override
	public boolean orderPairedUnit(CSimulation game, CUnit caster) {
		return this.orderPairedUnit;
	}

	@Override
	public Integer orderPairedUnitOrderId(CSimulation game, CUnit caster) {
		return this.orderPairedUnitOrderId;
	}

	@Override
	public Integer orderPairedUnitOffOrderId(CSimulation game, CUnit caster) {
		return this.orderPairedUnitOffOrderId;
	}

	// Not Used
	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		return null;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
		return false;
	}

}

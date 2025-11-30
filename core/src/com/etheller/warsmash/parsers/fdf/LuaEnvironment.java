package com.etheller.warsmash.parsers.fdf;

import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.SingleOrderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;

public class LuaEnvironment {
	private static final String UNITKEY_PLAYER = "player";
	private static final String UNITKEY_TARGET = "target";
	private static final String UNITKEY_MOUSEOVER = "mouseover";

	private final CSimulation game;
	private final GameUI rootFrame;
	private final Viewport uiViewport;
	private final Globals globals;
	private final Map<ThirdPersonLuaXmlEvent, LinkedHashSet<UIFrameLuaWrapper>> eventToRegistered = new HashMap<>();
	private final Map<ThirdPersonLuaXmlClick, LinkedHashSet<UIFrameLuaWrapper>> clickToRegistered = new HashMap<>();
	private final Map<ThirdPersonLuaXmlButton, LinkedHashSet<UIFrameLuaWrapper>> buttonToDragRegistered = new HashMap<>();
	private final CUnit pawnUnit;
	private final CAbilityPlayerPawn abilityPlayerPawn;
	private final AbilityDataUI abilityDataUI;
	private final CPlayerUnitOrderListener uiOrderListener;
	private RenderWidget targetUnit;
	private RenderWidget mouseOverUnit;
	private CUnitStateListenerImplementation targetStateListener;

	private final Map<String, String> bindingKeys = new HashMap<>();
	private final Map<Integer, String> keysToBinding = new HashMap<>();

	public LuaEnvironment(final CSimulation game, final GameUI rootFrame, final Viewport uiViewport,
			final Scene uiScene, final KeyedSounds uiSounds, final CUnit pawnUnit,
			final CAbilityPlayerPawn abilityPlayerPawn, final AbilityDataUI abilityDataUI,
			final CPlayerUnitOrderListener uiOrderListener) {
		this.game = game;
		this.rootFrame = rootFrame;
		this.uiViewport = uiViewport;
		this.pawnUnit = pawnUnit;
		this.abilityPlayerPawn = abilityPlayerPawn;
		this.abilityDataUI = abilityDataUI;
		this.uiOrderListener = uiOrderListener;
		this.globals = new Globals();
		this.globals.load(new JseBaseLib());
		this.globals.load(new Bit32Lib());
		this.globals.load(new TableLib());
		this.globals.load(new JseStringLib());
		this.globals.load(new JseMathLib());
		LoadState.install(this.globals);
		LuaC.install(this.globals);

		this.globals.set("GetReleaseTimeRemaining", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaDouble.valueOf(0);
			}
		});
		this.globals.set("GetCurrentResolution", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				final DisplayMode displayMode = Gdx.graphics.getDisplayMode();
				return LuaString.valueOf(displayMode.width + "x" + displayMode.height);
			}
		});
		this.globals.set("PlayerHasSpells", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(false);
			}
		});
		this.globals.set("HasPetSpells", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(false);
			}
		});
		this.globals.set("UnitIsDead", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				return LuaValue.valueOf(unit.isDead());
			}
		});
		this.globals.set("GetScreenResolutions", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				final LuaTable table = new LuaTable();
				final DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();
				for (int i = 0; i < displayModes.length; i++) {
					final DisplayMode displayMode = displayModes[i];
					table.set(i + 1, LuaString.valueOf(displayMode.width + "x" + displayMode.height));
				}
				table.set("n", displayModes.length);
				return table;
			}
		});
		this.globals.set("ceil", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				return LuaValue.valueOf(Math.ceil(arg.checkdouble()));
			}
		});
		this.globals.set("format", this.globals.get("string").get("format"));
		this.globals.set("gsub", this.globals.get("string").get("gsub"));
		this.globals.set("strbyte", this.globals.get("string").get("byte"));
		this.globals.set("strchar", this.globals.get("string").get("char"));
		this.globals.set("strfind", this.globals.get("string").get("find"));
		this.globals.set("strlen", this.globals.get("string").get("len"));
		this.globals.set("strlower", this.globals.get("string").get("lower"));
		this.globals.set("strmatch", this.globals.get("string").get("match"));
		this.globals.set("strrep", this.globals.get("string").get("rep"));
		this.globals.set("strsub", this.globals.get("string").get("sub"));
		this.globals.set("strupper", this.globals.get("string").get("upper"));
		this.globals.set("strrep", this.globals.get("string").get("rep"));
		this.globals.set("getglobal", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				final String checkjstring = arg.checkjstring();
				final LuaValue luaValue = getGlobals().get(checkjstring);
				if ((luaValue == null) || (luaValue == LuaValue.NIL)) {
					final UIFrame frameByName = rootFrame.getFrameByName(checkjstring, 0);
					if (frameByName == null) {
						return LuaValue.NIL;
//						throw new NullPointerException("getglobal: " + checkjstring);
					}
					return frameByName.getScripts().getLuaWrapper().getTable();
				}
				return luaValue;
			}
		});
		this.globals.set("PlaySound", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				final String soundName = arg.checkjstring();
				uiSounds.getSound(soundName).play(uiScene.audioContext, 0, 0, 0);
				return LuaValue.NIL; // TODO
			}
		});
		this.globals.set("ChangeActionBarPage", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(false);
			}
		});
		this.globals.set("UpdateSpells", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(false);
			}
		});
		this.globals.set("GetBindingKey", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				System.err.println("GetBindingKey: " + arg);

				final String bindingKeyValue = LuaEnvironment.this.bindingKeys.get(arg.checkjstring());
				if (bindingKeyValue == null) {
					return LuaValue.NIL;
				}
				return LuaValue.valueOf(bindingKeyValue);// TODO
			}
		});
		this.globals.set("IsShiftKeyDown", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				return LuaValue.valueOf(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
						|| Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
			}
		});
		this.globals.set("SetPortraitTexture", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue textureTable, final LuaValue texture) {
				if (false) {
					final Varargs id = textureTable.get("GetID").invoke();
					final String idString = id.checkjstring(1);
					final UIFrame frame = rootFrame.getFrameByName(idString, 0);
					if (frame instanceof TextureFrame) {
						((TextureFrame) frame).setTexture(
								rootFrame.loadTexture("Interface\\CharacterFrame\\TemporaryPortrait-Male-Troll.blp"));
					}
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("PutItemInBag", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				return LuaValue.NIL; // TODO
			}
		});
		this.globals.set("GetContainerNumSlots", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				return LuaValue.valueOf(16); // TODO
			}
		});
		this.globals.set("GetBagName", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				return LuaValue.valueOf("WarsmashBackpack"); // TODO
			}
		});
		this.globals.set("GetContainerItemInfo", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				// TODO lookup?
				return LuaValue.valueOf("Interface\\Icons\\INV_Misc_Bag_01");
//						LuaTable.listOf(new LuaValue[] { LuaValue.valueOf("Interface\\Icons\\INV_Misc_Bag_01"),
//						LuaValue.valueOf(16), LuaValue.valueOf(false), LuaValue.valueOf(false) });
			}
		});
		this.globals.set("GetContainerItemCooldown", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				// TODO lookup?
				return LuaValue
						.listOf(new LuaValue[] { LuaValue.valueOf(0f), LuaValue.valueOf(0f), LuaValue.valueOf(false) });
			}
		});
		// ===========
		// Action bar

		this.globals.set("GetActionTexture", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				System.err.println("GetActionTexture sees: " + id);
				final CAbility ability = getAbility(id.checkint(), "ability?");
				if (ability != null) {
					final IconUI iconUI = getIconUI(abilityDataUI, ability);
					return LuaString.valueOf(iconUI.getIconPath());
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("HasAction", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				final CAbility ability = getAbility(id.checkint(), "ability?");
				return LuaValue.valueOf(ability != null);
			}
		});
		this.globals.set("IsAttackAction", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				final CAbility ability = getAbility(id.checkint(), "ability?");
				return LuaValue.valueOf(ability instanceof CAbilityAttack);
			}
		});
		this.globals.set("GetActionCount", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				return LuaValue.ZERO;
			}
		});
		this.globals.set("GetActionCooldown", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				final CAbility ability = getAbility(varargs.arg(1).checkint(), "ability?");
				long start = 0, duration = 0;
				long enable = 0;
				if (ability != null) {
					final AbilityActivationGetter activationGetter = AbilityActivationGetter.INSTANCE.reset();
					int orderId = OrderIds.smart;
					if (ability instanceof SingleOrderAbility) {
						orderId = ((SingleOrderAbility) ability).getBaseOrderId();
					}
					ability.checkCanUse(game, pawnUnit, orderId, activationGetter);
					if (activationGetter.cooldownRemaining > 0) {
						duration = (long) (activationGetter.cooldown * 1000);
						start = System.currentTimeMillis()
								- (long) ((activationGetter.cooldown - activationGetter.cooldownRemaining) * 1000);
					}
					enable = 1;
				}
				return LuaValue.varargsOf(new LuaValue[] { LuaInteger.valueOf(start), LuaInteger.valueOf(duration),
						LuaInteger.valueOf(enable) });

			}
		});
		this.globals.set("IsUsableAction", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				boolean isUsable = false;
				boolean notEnoughMana = false;
				final CAbility ability = getAbility(varargs.arg(1).checkint(), "ability?");
				if (ability != null) {
					final AbilityActivationGetter activationGetter = AbilityActivationGetter.INSTANCE.reset();
					int orderId = OrderIds.smart;
					if (ability instanceof SingleOrderAbility) {
						orderId = ((SingleOrderAbility) ability).getBaseOrderId();
					}
					ability.checkCanUse(game, pawnUnit, orderId, activationGetter);
					isUsable = activationGetter.ok;
					notEnoughMana = activationGetter.commandStringErrorKey == CommandStringErrorKeys.NOT_ENOUGH_MANA;
				}
				return LuaValue
						.varargsOf(new LuaValue[] { LuaValue.valueOf(isUsable), LuaValue.valueOf(notEnoughMana) });

			}
		});
		this.globals.set("IsCurrentAction", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				final CAbility ability = getAbility(id.checkint(), "ability?");
				if (ability != null) {
					final COrder currentOrder = pawnUnit.getCurrentOrder();
					return LuaBoolean.valueOf(
							(currentOrder != null) && (currentOrder.getAbilityHandleId() == ability.getHandleId()));
				}
				return LuaValue.FALSE;
			}
		});
		this.globals.set("UseAction", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				final int actionId = id.checkint();
				useAction(game, rootFrame, pawnUnit, uiOrderListener, actionId);
				return LuaValue.NIL;
			}
		});
		this.globals.set("PickupAction", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				return LuaValue.NIL;
			}
		});

		// ===========
		// ===========
		// Buff bar

		this.globals.set("GetPlayerBuff", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				return LuaValue.varargsOf(new LuaValue[] { LuaInteger.valueOf(-1), LuaInteger.valueOf(false) });

			}
		});
		this.globals.set("GetPlayerBuffTexture", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				return LuaValue.NIL;
			}
		});

		// ===========
		this.globals.set("GetSpellName", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				final CAbility ability = getAbility(varargs.arg(1).checkint(), varargs.arg(2).checkjstring());
				if (ability != null) {
					final IconUI iconUI = getIconUI(abilityDataUI, ability);
					final String subNameText = "";// iconUI.getUberTip();
					return LuaValue.varargsOf(
							new LuaValue[] { LuaString.valueOf(iconUI.getToolTip()), LuaString.valueOf(subNameText) });
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetSpellTexture", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue id, final LuaValue bookType) {
				System.err.println("GetSpellTexture sees: " + id);
				final CAbility ability = getAbility(id.checkint(), bookType.checkjstring());
				if (ability != null) {
					final IconUI iconUI = getIconUI(abilityDataUI, ability);
					System.err.println("GetSpellTexture givess: " + iconUI.getIconPath());
					return LuaString.valueOf(iconUI.getIconPath());
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetSpellCooldown", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				final CAbility ability = getAbility(varargs.arg(1).checkint(), varargs.arg(2).checkjstring());
				if (ability != null) {
					final int start = 0;
					final int duration = 0;
					final War3ID alias = ability.getAlias();
					return LuaValue.varargsOf(new LuaValue[] { LuaInteger.valueOf(start), LuaInteger.valueOf(duration),
							LuaInteger.valueOf(1) });
				}
				return LuaValue.varargsOf(
						new LuaValue[] { LuaInteger.valueOf(0), LuaInteger.valueOf(0), LuaInteger.valueOf(0) });

			}
		});
		this.globals.set("CastSpell", new TwoArgFunction() {

			@Override
			public LuaValue call(final LuaValue id, final LuaValue bookType) {
				final CAbility ability = getAbility(id.checkint(), bookType.checkjstring());
				if (ability != null) {
					System.err.println("CastSpell: " + id);

					int orderId = OrderIds.smart;
					if (ability instanceof SingleOrderAbility) {
						orderId = ((SingleOrderAbility) ability).getBaseOrderId();
					}

					final ExternStringMsgAbilityActivationReceiver activationReceiver = ExternStringMsgAbilityActivationReceiver.INSTANCE
							.reset();
					ability.checkCanUse(game, pawnUnit, orderId, activationReceiver);
					if (activationReceiver.isUseOk()) {
						if (LuaEnvironment.this.targetUnit != null) {
							final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = ExternStringMsgTargetCheckReceiver
									.<CWidget>getInstance().reset();
							ability.checkCanTarget(game, pawnUnit, orderId,
									LuaEnvironment.this.targetUnit.getSimulationWidget(), targetReceiver);
							if (targetReceiver.getTarget() != null) {
								uiOrderListener.issueTargetOrder(pawnUnit.getHandleId(), ability.getHandleId(), orderId,
										targetReceiver.getTarget().getHandleId(), false);
							}
							else {
								final ExternStringMsgTargetCheckReceiver<Void> noTargetReceiver = ExternStringMsgTargetCheckReceiver
										.<Void>getInstance().reset();
								ability.checkCanTargetNoTarget(game, pawnUnit, orderId, noTargetReceiver);
								if (noTargetReceiver.getExternStringKey() == null) {
									uiOrderListener.issueImmediateOrder(pawnUnit.getHandleId(), ability.getHandleId(),
											orderId, false);
								}
								else {
									rootFrame.getUiSounds().getSound("HumanFemale_CantUseGeneric")
											.play(rootFrame.getUiScene().audioContext, 0, 0, 0);
								}
							}
						}
						else {
							final ExternStringMsgTargetCheckReceiver<Void> noTargetReceiver = ExternStringMsgTargetCheckReceiver
									.<Void>getInstance().reset();
							ability.checkCanTargetNoTarget(game, pawnUnit, orderId, noTargetReceiver);
							if (noTargetReceiver.getExternStringKey() == null) {
								uiOrderListener.issueImmediateOrder(pawnUnit.getHandleId(), ability.getHandleId(),
										orderId, false);
							}
							else {
								rootFrame.getUiSounds().getSound("HumanFemale_CantCastGenericNoTa")
										.play(rootFrame.getUiScene().audioContext, 0, 0, 0);
							}
						}
					}
					else {
						rootFrame.getUiSounds().getSound("HumanFemale_CantUseGeneric")
								.play(rootFrame.getUiScene().audioContext, 0, 0, 0);
					}
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("IsCurrentCast", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue id, final LuaValue bookType) {
				final CAbility ability = getAbility(id.checkint(), bookType.checkjstring());
				if (ability != null) {
					final COrder currentOrder = pawnUnit.getCurrentOrder();
					return LuaBoolean.valueOf(
							(currentOrder != null) && (currentOrder.getAbilityHandleId() == ability.getHandleId()));
				}
				return LuaValue.FALSE;
			}
		});
		this.globals.set("IsSpellPassive", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue id, final LuaValue bookType) {
				final CAbility ability = getAbility(id.checkint(), bookType.checkjstring());
				if (ability != null) {
					final AbilityActivationGetter activationGetter = AbilityActivationGetter.INSTANCE.reset();
					ability.checkCanUse(game, pawnUnit, OrderIds.smart /* TODO TODO */, activationGetter);
					return LuaBoolean.valueOf(activationGetter.passive);
				}
				return LuaValue.FALSE;
			}
		});
		// =====================
		// Paper doll frame stuff
		// also inventory stuff

		this.globals.set("GetInventorySlotInfo", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				// arg1 will be "HeadSlot" or "TabardSlot" or whatever
				final int itemId = 0;
				final String textureName = "";
				return LuaValue
						.varargsOf(new LuaValue[] { LuaInteger.valueOf(itemId), LuaInteger.valueOf(textureName) });

			}
		});
		this.globals.set("GetInventoryItemTexture", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue id) {
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetInventoryItemLink", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue id) {
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetInventoryItemCount", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue id) {
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetInventoryItemCooldown", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				return LuaValue.varargsOf(
						new LuaValue[] { LuaInteger.valueOf(0), LuaInteger.valueOf(0), LuaInteger.valueOf(0) });

			}
		});
		this.globals.set("IsInventoryItemLocked", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue id) {
				return LuaValue.FALSE;
			}
		});

		// ====================
		this.globals.set("UnitPowerType", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue luaWrapper) {
				return LuaValue.ZERO;
			}
		});
		this.globals.set("UnitName", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				return LuaString.valueOf(getUnitName(unit));
			}
		});
		this.globals.set("UnitHealth", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.ZERO;
				}
				return LuaValue.valueOf(unit.getLife());
			}
		});
		this.globals.set("UnitExists", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.FALSE;
				}
				return LuaValue.TRUE;
			}
		});
		this.globals.set("UnitHealthMax", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.ZERO;
				}
				return LuaValue.valueOf(unit.getMaximumLife());
			}
		});
		this.globals.set("UnitMana", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.ZERO;
				}
				return LuaValue.valueOf(unit.getMana());
			}
		});
		this.globals.set("UnitManaMax", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.ZERO;
				}
				return LuaValue.valueOf(unit.getMaximumMana());
			}
		});
		this.globals.set("UnitIsUnit", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue otherUnitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				final CUnit otherUnit = getUnit(otherUnitKey.checkjstring());
				return LuaBoolean.valueOf(((unit == otherUnit) && (unit != null)) || (otherUnit != null));
			}
		});
		this.globals.set("UnitIsEnemy", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue otherUnitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				final CUnit otherUnit = getUnit(otherUnitKey.checkjstring());
				return LuaBoolean.valueOf(!otherUnit.isUnitAlly(game.getPlayer(unit.getPlayerIndex())));
			}
		});
		this.globals.set("UnitIsFriend", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue otherUnitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				final CUnit otherUnit = getUnit(otherUnitKey.checkjstring());
				return LuaBoolean.valueOf(otherUnit.isUnitAlly(game.getPlayer(unit.getPlayerIndex())));
			}
		});
		this.globals.set("UnitReaction", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue otherUnitKey) {
				final CUnit target = getUnit(unitKey.checkjstring());
				final CUnit player = getUnit(otherUnitKey.checkjstring());

				final int targetLevel = getUnitLevel(target);
				final int playerLevel = getUnitLevel(player);

				if (target.isUnitAlly(game.getPlayer(player.getPlayerIndex()))) {
					if (game.getPlayer(target.getPlayerIndex()).hasAlliance(player.getPlayerIndex(),
							CAllianceType.SHARED_VISION)) {
						return LuaValue.valueOf(5);
					}
					return LuaValue.valueOf(4);
				}
				return LuaBoolean.valueOf(2);
			}
		});
		this.globals.set("UnitIsPlayer", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				return LuaBoolean.valueOf(unit.getFirstAbilityOfType(CAbilityPlayerPawn.class) != null);
			}
		});
		this.globals.set("UnitIsCharmed", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				return LuaBoolean.valueOf(false);
			}
		});
		this.globals.set("UnitIsPartyLeader", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
//				final CUnit unit = getUnit(unitKey.checkjstring());
				return LuaValue.valueOf(false);
			}
		});
		this.globals.set("UnitIsPlusMob", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
//				final CUnit unit = getUnit(unitKey.checkjstring());
				return LuaValue.valueOf(false);
			}
		});
		this.globals.set("UnitInParty", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				return LuaBoolean.valueOf(false);
			}
		});
		this.globals.set("GetPartyMember", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				// takes id in the form "1" or "2" or "3"
				return LuaBoolean.NIL;
			}
		});
		this.globals.set("GetPartyLeaderIndex", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {
				return LuaBoolean.ZERO;
			}
		});
		this.globals.set("GetZoneText", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf("Warsmash");
			}
		});
		this.globals.set("GetSubZoneText", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf("Absolutely Somewhere");
			}
		});
		this.globals.set("GetMinimapZoneText", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf("Eastern Nowhere");
			}
		});
		this.globals.set("UnitLevel", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				return LuaValue.valueOf(getUnitLevel(unit));
			}
		});
		this.globals.set("UnitXP", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				final CAbilityHero heroData = unit.getHeroData();
				if (heroData != null) {
					final int prevXp = game.getGameplayConstants().getNeedHeroXP(heroData.getHeroLevel() - 1);
					return LuaValue.valueOf(heroData.getXp() - prevXp);
				}
				return LuaValue.ZERO;
			}
		});
		this.globals.set("UnitXPMax", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				final CAbilityHero heroData = unit.getHeroData();
				if (heroData != null) {
					return LuaValue.valueOf(game.getGameplayConstants().getNeedHeroXP(heroData.getHeroLevel()));
				}
				return LuaValue.ZERO;
			}
		});
		this.globals.set("GetNumPartyMembers", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaBoolean.ZERO;
			}
		});
		this.globals.set("IsPartyLeader", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaBoolean.FALSE;
			}
		});
		this.globals.set("UnitCanCooperate", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue otherUnitKey) {
				final CUnit unit = getUnit(unitKey.checkjstring());
				final CUnit otherUnit = getUnit(otherUnitKey.checkjstring());
				return LuaBoolean.valueOf(otherUnit.isUnitAlly(game.getPlayer(unit.getPlayerIndex())));
			}
		});
		this.globals.set("SpellIsTargeting", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaBoolean.valueOf(false);
			}
		});
		this.globals.set("SpellStopTargeting", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.NIL;
			}
		});
		// ==================
		// Pet
		this.globals.set("GetPetActionInfo", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				final String name = "";
				final String subtext = "";
				final String texture = "";
				final boolean isToken = false;
				final boolean isActive = false;
				final boolean autoCastAllowed = false;
				final boolean autoCastEnabled = false;

				return LuaValue.varargsOf(
						new LuaValue[] { LuaValue.valueOf(name), LuaValue.valueOf(subtext), LuaValue.valueOf(texture),
								LuaValue.valueOf(isToken), LuaValue.valueOf(isActive), LuaValue.valueOf(isActive),
								LuaValue.valueOf(autoCastAllowed), LuaValue.valueOf(autoCastEnabled), });

			}
		});
		this.globals.set("GetPetActionCooldown", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs arg) {
				return LuaValue
						.varargsOf(new LuaValue[] { LuaValue.valueOf(0), LuaValue.valueOf(0), LuaValue.valueOf(0) });
			}
		});
		this.globals.set("PetHasActionBar", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaBoolean.valueOf(false);
			}
		});
		this.globals.set("GetBonusBarOffset", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaBoolean.valueOf(0);
			}
		});
		this.globals.set("GetNumShapeshiftForms", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaBoolean.valueOf(0);
			}
		});

		this.globals.set("GetLootMethod", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs args) {
				// possibilities:
				// freeforall
				// roundrobin
				// master
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf("freeforall"), LuaValue.valueOf(-1) });
			}
		});
		this.globals.set("GetTime", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(System.currentTimeMillis());
			}
		});
		this.globals.set("GetGameTime", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs arg) {
				final float gameTimeOfDay = game.getGameTimeOfDay();
				final float gameDayHours = game.getGameplayConstants().getGameDayHours();
				final int gameDayTotalMinutes = (int) ((gameTimeOfDay / gameDayHours) * 60);
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf(gameDayTotalMinutes / 60),
						LuaValue.valueOf(gameDayTotalMinutes % 60) });
			}
		});
		this.globals.set("GetNetStats", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs args) {
				final double bandwidthIn = 0;
				final double bandwidthOut = 0;
				final double latency = 0;
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf(bandwidthIn),
						LuaValue.valueOf(bandwidthOut), LuaValue.valueOf(latency) });
			}
		});
		this.pawnUnit.addStateListener(new CUnitStateListenerImplementation(UNITKEY_PLAYER));
		for (int i = 1; i <= 12; i++) {
			final String binding = "ACTIONBUTTON" + (i);
			this.bindingKeys.put(binding, Integer.toString(i));
			this.keysToBinding.put(Input.Keys.valueOf("" + i), binding);
		}
	}

	public LinkedHashSet<UIFrameLuaWrapper> getRegistered(final ThirdPersonLuaXmlEvent event) {
		LinkedHashSet<UIFrameLuaWrapper> registeredSet = this.eventToRegistered.get(event);
		if (registeredSet == null) {
			registeredSet = new LinkedHashSet<>();
			this.eventToRegistered.put(event, registeredSet);
		}
		return registeredSet;
	}

	public LinkedHashSet<UIFrameLuaWrapper> getRegistered(final ThirdPersonLuaXmlClick click) {
		LinkedHashSet<UIFrameLuaWrapper> registeredSet = this.clickToRegistered.get(click);
		if (registeredSet == null) {
			registeredSet = new LinkedHashSet<>();
			this.clickToRegistered.put(click, registeredSet);
		}
		return registeredSet;
	}

	public LinkedHashSet<UIFrameLuaWrapper> getRegistered(final ThirdPersonLuaXmlButton button) {
		LinkedHashSet<UIFrameLuaWrapper> registeredSet = this.buttonToDragRegistered.get(button);
		if (registeredSet == null) {
			registeredSet = new LinkedHashSet<>();
			this.buttonToDragRegistered.put(button, registeredSet);
		}
		return registeredSet;
	}

	public void runLua(final String script) {
		load(script).call();
	}

	public void runLua(final Reader reader, final String chunk) {
		this.globals.load(reader, chunk).call();
	}

	public LuaValue load(final String script) {
		return this.globals.load(script);
	}

	public void registerEvent(final ThirdPersonLuaXmlEvent eventToRegister, final UIFrameLuaWrapper luaWrapper) {
		getRegistered(eventToRegister).add(luaWrapper);
	}

	public void registerForClick(final ThirdPersonLuaXmlClick clickToRegister, final UIFrameLuaWrapper luaWrapper) {
		getRegistered(clickToRegister).add(luaWrapper);
	}

	public void registerForDrag(final ThirdPersonLuaXmlButton dragToRegister, final UIFrameLuaWrapper luaWrapper) {
		getRegistered(dragToRegister).add(luaWrapper);
	}

	public GameUI getRootFrame() {
		return this.rootFrame;
	}

	public Viewport getUiViewport() {
		return this.uiViewport;
	}

	public void load(final UIFrameLuaWrapper thisFrame) {
		this.globals.load(thisFrame);
	}

	public Globals getGlobals() {
		return this.globals;
	}

	private IconUI getIconUI(final AbilityDataUI abilityDataUI, final CAbility ability) {
		final War3ID alias = ability.getAlias();

		final AbilityUI abilityUI = abilityDataUI.getUI(alias);
		IconUI iconUI = abilityUI == null ? null : abilityUI.getOnIconUI(0);
		if (alias.asStringValue().equals("Aatk")) {
			iconUI = abilityDataUI.getAttackUI();
		}
		if ((iconUI == null) || iconUI.getIconPath().isEmpty()) {
			iconUI = new IconUI(null, "Textures\\BTNTemp.blp", null, "", 0, 0, ability.getClass().getSimpleName(),
					"Tooltip missing!", '?');
		}
		return iconUI;
	}

	private CAbility getAbility(final int id, final String bookType) {
		final int idInt = id - 1;
		final List<CAbility> abilities = this.pawnUnit.getAbilities();
		if ((idInt >= 0) && (idInt < abilities.size())) {
			final CAbility cAbility = abilities.get(idInt);
			return cAbility;
		}
		return null;
	}

	private final class CUnitStateListenerImplementation implements CUnitStateListener {
		private final LuaValue unitKey;

		public CUnitStateListenerImplementation(final String unitKey) {
			this.unitKey = LuaValue.valueOf(unitKey);
		}

		@Override
		public void waypointsChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void rallyPointChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void queueChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void ordersChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void manaChanged() {
			final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(ThirdPersonLuaXmlEvent.UNIT_MANA);
			for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
				frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.UNIT_MANA, this.unitKey);
			}
		}

		@Override
		public void lifeChanged() {
			final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(ThirdPersonLuaXmlEvent.UNIT_HEALTH);
			for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
				frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.UNIT_HEALTH, this.unitKey);
			}
		}

		@Override
		public void inventoryChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void hideStateChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void heroStatsChanged() {
			final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(ThirdPersonLuaXmlEvent.PLAYER_XP_UPDATE);
			for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
				frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.PLAYER_XP_UPDATE, this.unitKey);
			}
		}

		@Override
		public void attacksChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void abilitiesChanged() {
			final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(ThirdPersonLuaXmlEvent.SPELLS_CHANGED);
			for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
				frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.SPELLS_CHANGED, this.unitKey);
			}
		}
	}

	private static final class AbilityActivationGetter implements AbilityActivationReceiver {
		private boolean ok;
		private boolean passive;

		public static AbilityActivationGetter INSTANCE = new AbilityActivationGetter();

		private float cooldownRemaining;

		private float cooldown;

		private String commandStringErrorKey;

		public AbilityActivationGetter reset() {
			this.passive = false;
			this.cooldown = 0;
			this.cooldownRemaining = 0;
			this.ok = false;
			return this;
		}

		@Override
		public void useOk() {
			this.ok = true;
		}

		@Override
		public void unknownReasonUseNotOk() {
			// TODO Auto-generated method stub

		}

		@Override
		public void notAnActiveAbility() {
			this.passive = true;
		}

		@Override
		public void missingRequirement(final War3ID type, final int level) {

		}

		@Override
		public void missingHeroLevelRequirement(final int level) {
			// TODO Auto-generated method stub

		}

		@Override
		public void noHeroSkillPointsAvailable() {
			// TODO Auto-generated method stub

		}

		@Override
		public void disabled() {
			// TODO Auto-generated method stub

		}

		@Override
		public void techtreeMaximumReached() {
			// TODO Auto-generated method stub

		}

		@Override
		public void techItemAlreadyInProgress() {
			// TODO Auto-generated method stub

		}

		@Override
		public void cooldownNotYetReady(final float cooldownRemaining, final float cooldown) {
			this.cooldownRemaining = cooldownRemaining;
			this.cooldown = cooldown;

		}

		@Override
		public void noChargesRemaining() {
			// TODO Auto-generated method stub

		}

		@Override
		public void activationCheckFailed(final String commandStringErrorKey) {
			this.commandStringErrorKey = commandStringErrorKey;
		}

	}

	public CUnit getUnit(final String unitKey) {
		switch (unitKey) {
		case UNITKEY_TARGET:
			if (this.targetUnit == null) {
				return null;
			}
			return this.targetUnit.getSimulationWidget().visit(AbilityTargetVisitor.UNIT);
		case UNITKEY_PLAYER:
			return this.pawnUnit;
		case UNITKEY_MOUSEOVER:
			if (this.mouseOverUnit == null) {
				return null;
			}
			return this.mouseOverUnit.getSimulationWidget().visit(AbilityTargetVisitor.UNIT);
		default:
			return null;

		}
	}

	public void notifySetTarget(final RenderWidget targetUnit) {
		if (this.targetStateListener != null) {
			if (this.targetUnit instanceof RenderUnit) {
				((RenderUnit) this.targetUnit).getSimulationUnit().removeStateListener(this.targetStateListener);
			}
			this.targetUnit = null;
			final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(
					ThirdPersonLuaXmlEvent.PLAYER_TARGET_CHANGED);
			for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
				frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.PLAYER_TARGET_CHANGED,
						this.targetStateListener.unitKey);
			}
		}
		if (targetUnit != null) {
			this.targetUnit = targetUnit;
			if (this.targetUnit instanceof RenderUnit) {
				this.targetStateListener = new CUnitStateListenerImplementation(UNITKEY_TARGET);
				((RenderUnit) this.targetUnit).getSimulationUnit().addStateListener(this.targetStateListener);
			}
			final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(
					ThirdPersonLuaXmlEvent.PLAYER_TARGET_CHANGED);
			for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
				frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.PLAYER_TARGET_CHANGED,
						this.targetStateListener.unitKey);
			}
		}
		else {
			this.targetStateListener = null;
		}
	}

	public void notifyUpdateMouseOver(final RenderWidget mouseOverUnit) {
		this.mouseOverUnit = mouseOverUnit;
		final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(ThirdPersonLuaXmlEvent.UPDATE_MOUSEOVER_UNIT);
		for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
			frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.UPDATE_MOUSEOVER_UNIT, LuaValue.NIL);
		}
	}

	public String getUnitName(final CUnit unit) {
		if (unit == null) {
			return "NO UNIT";
		}
		final CAbilityHero heroData = unit.getHeroData();
		if (heroData != null) {
			return heroData.getProperName();
		}
		return unit.getUnitType().getName();
	}

	private void loadMyString() {

		this.globals.set("format", new LibFunction() {
			@Override
			public Varargs invoke(final Varargs args) {
				final String formatString = args.checkjstring(1);
				final int formatArgCount = args.narg() - 1;
				final Object[] formatArgs = new Object[formatArgCount];
				for (int i = 2; i <= args.narg(); i++) {
					final LuaValue luaArg = args.arg(i);
					if (luaArg.isint()) {
						formatArgs[i - 2] = luaArg.checkint();
					}
					else if (luaArg.islong()) {
						formatArgs[i - 2] = luaArg.checklong();
					}
					else if (luaArg.isnumber()) {
						formatArgs[i - 2] = luaArg.checkdouble();
					}
					else if (luaArg.isstring()) {
						formatArgs[i - 2] = luaArg.checkjstring();
					}
					else {
						throw new RuntimeException("Unknown arg type: " + luaArg);
					}
				}
				return LuaValue.valueOf(String.format(formatString, formatArgs));
			}
		});
		this.globals.set("strlen", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue text) {
				final int length = text.checkstring().length();
				return LuaInteger.valueOf(length);
			}
		});
		this.globals.set("strfind", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue text, final LuaValue toFind) {
				final int indexOf = text.checkjstring().indexOf(toFind.checkjstring());
				if (indexOf == -1) {
					return LuaValue.FALSE;
				}
				return LuaInteger.valueOf(indexOf);
			}
		});
		this.globals.set("strupper", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue text) {
				return LuaString.valueOf(text.checkjstring().toUpperCase());
			}
		});
		this.globals.set("strsub", new ThreeArgFunction() {
			@Override
			public LuaValue call(final LuaValue text, final LuaValue idx, final LuaValue length) {
				final int idxInt = idx.checkint();
				if (length == LuaValue.NIL) {
					return LuaInteger.valueOf(text.checkjstring().substring(idxInt));
				}
				return LuaInteger.valueOf(text.checkjstring().substring(idxInt, idxInt + length.checkint()));
			}
		});
	}

	private int getUnitLevel(final CUnit unit) {
		final CAbilityHero heroData = unit.getHeroData();
		if (heroData != null) {
			return heroData.getHeroLevel();
		}
		return unit.getUnitType().getLevel();
	}

	private void useAction(final CSimulation game, final GameUI rootFrame, final CUnit pawnUnit,
			final CPlayerUnitOrderListener uiOrderListener, final int actionId) {
		final CAbility ability = getAbility(actionId, "ability?");
		if (ability != null) {
			System.err.println("CastSpell: " + actionId);

			int orderId = OrderIds.smart;
			if (ability instanceof SingleOrderAbility) {
				orderId = ((SingleOrderAbility) ability).getBaseOrderId();
			}

			final ExternStringMsgAbilityActivationReceiver activationReceiver = ExternStringMsgAbilityActivationReceiver.INSTANCE
					.reset();
			ability.checkCanUse(game, pawnUnit, orderId, activationReceiver);
			if (activationReceiver.isUseOk()) {
				if (LuaEnvironment.this.targetUnit != null) {
					final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = ExternStringMsgTargetCheckReceiver
							.<CWidget>getInstance().reset();
					ability.checkCanTarget(game, pawnUnit, orderId,
							LuaEnvironment.this.targetUnit.getSimulationWidget(), targetReceiver);
					if (targetReceiver.getTarget() != null) {
						uiOrderListener.issueTargetOrder(pawnUnit.getHandleId(), ability.getHandleId(), orderId,
								targetReceiver.getTarget().getHandleId(), false);
					}
					else {
						final ExternStringMsgTargetCheckReceiver<Void> noTargetReceiver = ExternStringMsgTargetCheckReceiver
								.<Void>getInstance().reset();
						ability.checkCanTargetNoTarget(game, pawnUnit, orderId, noTargetReceiver);
						if (noTargetReceiver.getExternStringKey() == null) {
							uiOrderListener.issueImmediateOrder(pawnUnit.getHandleId(), ability.getHandleId(), orderId,
									false);
						}
						else {
							rootFrame.getUiSounds().getSound("HumanFemale_CantUseGeneric")
									.play(rootFrame.getUiScene().audioContext, 0, 0, 0);
						}
					}
				}
				else {
					final ExternStringMsgTargetCheckReceiver<Void> noTargetReceiver = ExternStringMsgTargetCheckReceiver
							.<Void>getInstance().reset();
					ability.checkCanTargetNoTarget(game, pawnUnit, orderId, noTargetReceiver);
					if (noTargetReceiver.getExternStringKey() == null) {
						uiOrderListener.issueImmediateOrder(pawnUnit.getHandleId(), ability.getHandleId(), orderId,
								false);
					}
					else {
						rootFrame.getUiSounds().getSound("HumanFemale_CantCastGenericNoTa")
								.play(rootFrame.getUiScene().audioContext, 0, 0, 0);
					}
				}
			}
			else {
				rootFrame.getUiSounds().getSound("HumanFemale_CantUseGeneric").play(rootFrame.getUiScene().audioContext,
						0, 0, 0);
			}
		}
	}

	public void keyUp(final int keycode) {
		final String binding = this.keysToBinding.get(keycode);
		if (binding != null) {
			if (binding.startsWith("ACTIONBUTTON")) {
				final String keyText = binding.substring(12);
				useAction(this.game, this.rootFrame, this.pawnUnit, this.uiOrderListener, Integer.parseInt(keyText));
			}
		}
	}
}

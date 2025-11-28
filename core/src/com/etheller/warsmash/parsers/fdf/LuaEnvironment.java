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
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;

public class LuaEnvironment {
	private final GameUI rootFrame;
	private final Viewport uiViewport;
	private final Globals globals;
	private final Map<ThirdPersonLuaXmlEvent, LinkedHashSet<UIFrameLuaWrapper>> eventToRegistered = new HashMap<>();
	private final Map<ThirdPersonLuaXmlClick, LinkedHashSet<UIFrameLuaWrapper>> clickToRegistered = new HashMap<>();
	private final Map<ThirdPersonLuaXmlButton, LinkedHashSet<UIFrameLuaWrapper>> buttonToDragRegistered = new HashMap<>();
	private final CUnit pawnUnit;
	private final CAbilityPlayerPawn abilityPlayerPawn;
	private final AbilityDataUI abilityDataUI;

	public LuaEnvironment(final GameUI rootFrame, final Viewport uiViewport, final Scene uiScene,
			final KeyedSounds uiSounds, final CUnit pawnUnit, final CAbilityPlayerPawn abilityPlayerPawn,
			final AbilityDataUI abilityDataUI) {
		this.rootFrame = rootFrame;
		this.uiViewport = uiViewport;
		this.pawnUnit = pawnUnit;
		this.abilityPlayerPawn = abilityPlayerPawn;
		this.abilityDataUI = abilityDataUI;
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
		this.globals.set("UnitIsDead", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(false); // TODO TODO TODO
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
		this.globals.set("getglobal", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				final String checkjstring = arg.checkjstring();
				final UIFrame frameByName = rootFrame.getFrameByName(checkjstring, 0);
				if (frameByName == null) {
					throw new NullPointerException("getglobal: " + checkjstring);
				}
				return frameByName.getScripts().getLuaWrapper().getTable();
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
				return LuaValue.valueOf("A");// TODO
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
		this.globals.set("GetSpellName", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue id, final LuaValue bookType) {
				final List<CAbility> abilities = pawnUnit.getAbilities();
				final int idInt = id.checkint();
				if ((idInt >= 0) && (idInt < abilities.size())) {
					final War3ID alias = abilities.get(idInt).getAlias();
					final AbilityUI abilityUI = abilityDataUI.getUI(alias);
					final LuaTable tooltip = new LuaTable();
					tooltip.set(1, LuaString.valueOf(abilityUI.getOnIconUI(0).getToolTip()));
					tooltip.set(2, LuaString.valueOf(abilityUI.getOnIconUI(0).getUberTip()));
					tooltip.set("n", 2);
					return tooltip;
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetSpellTexture", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue id, final LuaValue bookType) {
				final List<CAbility> abilities = pawnUnit.getAbilities();
				final int idInt = id.checkint();
				if ((idInt >= 0) && (idInt < abilities.size())) {
					final War3ID alias = abilities.get(idInt).getAlias();

					final AbilityUI abilityUI = abilityDataUI.getUI(alias);
					return LuaString.valueOf(abilityUI.getOnIconUI(0).getIconPath());
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetSpellCooldown", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue id, final LuaValue bookType) {
				final List<CAbility> abilities = pawnUnit.getAbilities();
				final int idInt = id.checkint();
				if ((idInt >= 0) && (idInt < abilities.size())) {
					final CAbility cAbility = abilities.get(idInt);
					final double start = 0;
					final double duration = 0;
					final boolean enable = true;
					final War3ID alias = cAbility.getAlias();
					final LuaTable spellCooldownTuple = new LuaTable();
					spellCooldownTuple.set(1, LuaDouble.valueOf(start));
					spellCooldownTuple.set(2, LuaDouble.valueOf(duration));
					spellCooldownTuple.set(3, LuaBoolean.valueOf(enable));
					spellCooldownTuple.set("n", 3);
					return spellCooldownTuple;
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("UnitPowerType", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue luaWrapper) {
				return LuaValue.ZERO;
			}
		});
		this.globals.set("strlen", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue text) {
				return LuaInteger.valueOf(text.checkstring().length());
			}
		});
		this.pawnUnit.addStateListener(new CUnitStateListener() {

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
					frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.UNIT_MANA);
				}
			}

			@Override
			public void lifeChanged() {
				final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(ThirdPersonLuaXmlEvent.UNIT_HEALTH);
				for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
					frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.UNIT_HEALTH);
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
				// TODO Auto-generated method stub

			}

			@Override
			public void attacksChanged() {
				// TODO Auto-generated method stub

			}

			@Override
			public void abilitiesChanged() {
				final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(
						ThirdPersonLuaXmlEvent.SPELLS_CHANGED);
				for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
					frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.SPELLS_CHANGED);
				}
			}
		});
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
}

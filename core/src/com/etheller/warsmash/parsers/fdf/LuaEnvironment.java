package com.etheller.warsmash.parsers.fdf;

import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderItem;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.ItemUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.UnitIconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandCardIconVisibilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.SingleOrderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityBag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CItemSlotHolder;
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

	/** Large stand-in screen size for WoW's GetScreenHeight/Width (container column wrapping only). */
	private static final float SCREEN_HEIGHT_FALLBACK = 100000f;
	private static final float SCREEN_WIDTH_FALLBACK = 100000f;
	/** Synthetic inventory-slot id of the first equipped-bag bar button (WoW's Bag0Slot). */
	private static final int BAG_SLOT_INVENTORY_BASE = 20;
	/** WoW offset mapping a container/bag id (1..) to its equipped-bag inventory slot id. */
	private static final int CONTAINER_BAG_INVENTORY_OFFSET = 19;

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

	/**
	 * The item currently "held" on the mouse cursor by the WoW bag UI, or null. We
	 * never remove the item from its slot while it is on the cursor; the pickup is
	 * purely a UI state, and the actual move happens as a single Warcraft III order
	 * when the item is dropped onto a destination slot. The source is remembered only
	 * so the lifted slot can render empty: {@link #cursorBagId} is the source WoW
	 * container id (0 = backpack, 1..4 = carried bags) with {@link #cursorSlot} the
	 * 1-based bag slot; OR {@link #cursorBagId} is {@link #CURSOR_SOURCE_INVENTORY}
	 * (-1) when the item was lifted off a bag-bar button, with {@link #cursorSlot}
	 * then the 0-based Warcraft III inventory slot.
	 */
	private CItem cursorItem;
	private int cursorBagId;
	private int cursorSlot;
	/** {@link #cursorBagId} sentinel: the item was lifted off the bag bar (unit inventory). */
	private static final int CURSOR_SOURCE_INVENTORY = -1;

	/** Receives the held item's icon path (or null to clear) so the owning UI can paint the cursor. */
	public interface CursorItemDisplayListener {
		void onCursorItemChanged(String itemIconPath);
	}

	private CursorItemDisplayListener cursorItemDisplayListener;

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
		// Lua 5.0 (which WoW's FrameXML targets) exposed the math functions as plain
		// globals; luaj is 5.1+ and only puts them under `math`, so the FrameXML calls
		// (e.g. ContainerFrame.lua uses mod()) would hit nil. Alias the common ones.
		final LuaValue mathTable = this.globals.get("math");
		this.globals.set("floor", mathTable.get("floor"));
		this.globals.set("abs", mathTable.get("abs"));
		this.globals.set("sqrt", mathTable.get("sqrt"));
		this.globals.set("max", mathTable.get("max"));
		this.globals.set("min", mathTable.get("min"));
		this.globals.set("random", mathTable.get("random"));
		// `mod` was removed in Lua 5.1; reimplement Lua 5.0 floor-modulo semantics.
		this.globals.set("mod", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue a, final LuaValue b) {
				final double x = a.checkdouble();
				final double y = b.checkdouble();
				return LuaValue.valueOf(x - (Math.floor(x / y) * y));
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
			public LuaValue call(final LuaValue textureTable, final LuaValue unitKey) {
				// WoW: SetPortraitTexture(portraitTexture, unit). The portrait is a plain 2D
				// <Texture> (PlayerPortrait/TargetPortrait); we put the unit's command icon
				// there, cropped to a circle by us (the frame ring does NOT clip the square
				// texture — the engine has to). (Real WoW renders a 3D head here; an icon is
				// the simple stand-in.)
				final LuaValue getName = textureTable.get("GetName");
				if (getName.isnil()) {
					return LuaValue.NIL;
				}
				final UIFrame frame = rootFrame.getFrameByName(getName.call().tojstring(), 0);
				if (!(frame instanceof TextureFrame)) {
					return LuaValue.NIL;
				}
				final TextureFrame portrait = (TextureFrame) frame;
				String iconPath = null;
				CWidget widget = getWidget(unitKey.optjstring(""));
				if (widget != null) {
					CUnit unit = widget.visit(AbilityTargetVisitor.UNIT);
					if (unit != null) {
						final UnitIconUI unitUI = LuaEnvironment.this.abilityDataUI.getUnitUI(unit.getTypeId());
						if (unitUI != null) {
							iconPath = unitUI.getIconPath();
						}
					} else {
						CItem item = widget.visit(AbilityTargetVisitor.ITEM);
						if (item != null) {
							ItemUI itemUI = abilityDataUI.getItemUI(item.getTypeId());
							if (itemUI != null) {
								iconPath = itemUI.getIconUI().getIconPath();
							}
						}
					}
				}
				if ((iconPath != null) && !iconPath.isEmpty()) {
					portrait.setTexture(rootFrame.loadCircularMaskedTexture(iconPath));
				}
				else {
					portrait.setTexture((TextureRegion) null);
				}
				return LuaValue.NIL;
			}
		});
		this.globals.set("PutItemInBag", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				// Plain-click on a bag-bar button: if an item is on the cursor, drop it into
				// the corresponding Warcraft III inventory slot and report it placed (truthy)
				// so BagSlotButton_OnClick does NOT also toggle the bag open. Empty cursor ->
				// nil so the click falls through to ToggleBag.
				if (LuaEnvironment.this.cursorItem == null) {
					return LuaValue.NIL;
				}
				final int inventorySlotIndex = arg.checkint() - BAG_SLOT_INVENTORY_BASE;
				dropCursorItemIntoInventorySlot(inventorySlotIndex);
				return LuaValue.TRUE;
			}
		});
		this.globals.set("PutItemInBackpack", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				// Plain-click on the backpack button: drop a held cursor item into the first
				// free backpack slot (truthy so BackpackButton_OnClick doesn't also toggle).
				if (LuaEnvironment.this.cursorItem == null) {
					return LuaValue.NIL;
				}
				final CAbilityBag backpack = resolveBag(0);
				if (backpack != null) {
					final int freeSlot = backpack.getFirstEmptySlot();
					if (freeSlot != -1) {
						dropCursorItemIntoBag(0, freeSlot);
					}
				}
				return LuaValue.TRUE;
			}
		});
		this.globals.set("PickupBagFromSlot", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				// Shift-click on a bag-bar button: lift the Warcraft III inventory item in
				// that slot onto the cursor (so it can be dropped into a bag/another slot).
				if (LuaEnvironment.this.cursorItem != null) {
					return LuaValue.NIL;
				}
				final int inventorySlotIndex = arg.checkint() - BAG_SLOT_INVENTORY_BASE;
				final CItem item = getEquippedBagItem(arg.checkint());
				if (item == null) {
					return LuaValue.NIL;
				}
				LuaEnvironment.this.cursorItem = item;
				LuaEnvironment.this.cursorBagId = CURSOR_SOURCE_INVENTORY;
				LuaEnvironment.this.cursorSlot = inventorySlotIndex;
				updateCursorVisual();
				notifyBagsChanged();
				return LuaValue.NIL;
			}
		});
		this.globals.set("ShowContainerSellCursor", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue bag, final LuaValue slot) {
				return LuaValue.NIL; // merchant sell affordance; no-op until shops are wired
			}
		});
		this.globals.set("HideSellCursor", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetContainerNumSlots", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				final CAbilityBag bag = resolveBag(arg.checkint());
				return LuaValue.valueOf(bag == null ? 0 : bag.getSlotCount());
			}
		});
		this.globals.set("GetBagName", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				final int bagId = arg.checkint();
				if (bagId == 0) {
					return LuaValue.valueOf("Backpack");
				}
				final CAbilityBag bag = resolveBag(bagId);
				if ((bag != null) && (bag.getItem() != null)) {
					final ItemUI itemUI = LuaEnvironment.this.abilityDataUI.getItemUI(bag.getItem().getTypeId());
					if (itemUI != null) {
						return LuaValue.valueOf(itemUI.getName());
					}
				}
				return LuaValue.valueOf("");
			}
		});
		this.globals.set("GetContainerItemInfo", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				// WoW calls this as GetContainerItemInfo(bagID, slot) and expects
				// (texture, itemCount, locked, quality). Container slots are 1-based.
				final int bagId = varargs.arg(1).checkint();
				final int slot = varargs.arg(2).checkint();
				// While an item is lifted onto the cursor, its source slot renders empty.
				if ((LuaEnvironment.this.cursorItem != null) && (bagId == LuaEnvironment.this.cursorBagId)
						&& (slot == LuaEnvironment.this.cursorSlot)) {
					return LuaValue.NIL;
				}
				final CAbilityBag bag = resolveBag(bagId);
				final CItem item = bag == null ? null : bag.getItemInSlot(slot - 1);
				if (item == null) {
					return LuaValue.NIL;
				}
				String texture = "";
				final ItemUI itemUI = LuaEnvironment.this.abilityDataUI.getItemUI(item.getTypeId());
				if (itemUI != null) {
					texture = itemUI.getItemIconPathForDragging();
				}
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf(texture),
						LuaValue.valueOf(item.getCharges()), LuaValue.FALSE, LuaValue.valueOf(1) });
			}
		});
		this.globals.set("ContainerIDToInventoryID", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				// WoW maps container id (1..NUM_BAG_FRAMES) to the equipped-bag inventory
				// slot id via a fixed offset of 19 (bag 1 -> inventory slot 20).
				return LuaValue.valueOf(arg.checkint() + CONTAINER_BAG_INVENTORY_OFFSET);
			}
		});
		// Natives invoked while a container window is being built/shown or hidden. They
		// are visual niceties we don't need yet, but they MUST exist or the open path
		// (ContainerFrame_GenerateFrame -> SetBagPortaitTexture, updateContainerFrameAnchors
		// -> GetScreenHeight) throws on a nil global and the bag window never appears.
		this.globals.set("SetBagPortaitTexture", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue textureFrame, final LuaValue bagId) {
				return LuaValue.NIL; // TODO set the container portrait to the bag's icon
			}
		});
		this.globals.set("UpdateBagButtonHighlight", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue bagId) {
				return LuaValue.NIL; // TODO highlight the matching bag bar button
			}
		});
		this.globals.set("GetScreenHeight", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				// Used by the container code only for column wrapping; a large value keeps
				// all open bags in a single column and avoids any coordinate-space mismatch.
				return LuaValue.valueOf(SCREEN_HEIGHT_FALLBACK);
			}
		});
		this.globals.set("GetScreenWidth", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(SCREEN_WIDTH_FALLBACK);
			}
		});
		this.globals.set("GetContainerItemCooldown", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				// WoW expects MULTIPLE return values (start, duration, enable) as NUMBERS, e.g.
				// `local start, duration, enable = GetContainerItemCooldown(bag, slot)`. Returning
				// a single table here made `start` a table, and CooldownFrame_SetTimer's
				// `start > 0` comparison threw, aborting ContainerFrame_GenerateFrame before
				// frame:Show() (so a bag holding any item refused to open) and aborting the
				// slot loop in ContainerFrame_Update (so drags refreshed only partially). No
				// container cooldowns are tracked yet, so report none. TODO real lookup.
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf(0), LuaValue.valueOf(0), LuaValue.valueOf(0) });
			}
		});
		// ===========
		// Drag & drop (cursor item) — WoW's container/inventory item buttons call these
		// from ContainerFrameItemButton_OnClick. PickupContainerItem toggles between
		// lifting an item onto the cursor and dropping it into a slot. The others keep
		// the Lua from erroring on a nil global and report the cursor-held state.
		this.globals.set("PickupContainerItem", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue bagArg, final LuaValue slotArg) {
				handleContainerPickupOrDrop(bagArg.checkint(), slotArg.checkint());
				return LuaValue.NIL;
			}
		});
		this.globals.set("PickupInventoryItem", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue arg) {
				// TODO inventory/paper-doll item buttons (incl. the bag-bar carried bags in
				// inventory slots 20-23). Distinct from container pickup: these address the
				// hero's CAbilityInventory, not a bag's contents. No-op until wired so the
				// Lua never errors on a nil global.
				return LuaValue.NIL;
			}
		});
		this.globals.set("UseContainerItem", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue bagArg, final LuaValue slotArg) {
				return LuaValue.NIL; // TODO right-click to use/consume the item
			}
		});
		this.globals.set("SplitContainerItem", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.NIL; // TODO stack splitting (Warcraft III items rarely stack)
			}
		});
		this.globals.set("GetContainerItemLink", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue bagArg, final LuaValue slotArg) {
				return LuaValue.valueOf(""); // no item links yet; empty keeps chat-insert harmless
			}
		});
		this.globals.set("CursorHasItem", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(LuaEnvironment.this.cursorItem != null);
			}
		});
		this.globals.set("ClearCursor", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				clearCursorItemState();
				return LuaValue.NIL;
			}
		});
		this.globals.set("GetCursorInfo", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				if (LuaEnvironment.this.cursorItem == null) {
					return LuaValue.NIL;
				}
				String name = "";
				final ItemUI itemUI = LuaEnvironment.this.abilityDataUI
						.getItemUI(LuaEnvironment.this.cursorItem.getTypeId());
				if (itemUI != null) {
					name = itemUI.getName();
				}
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf("item"),
						LuaValue.valueOf(LuaEnvironment.this.cursorItem.getHandleId()), LuaValue.valueOf(name) });
			}
		});
		// ===========
		// Action bar

		this.globals.set("GetActionTexture", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue id) {				final CAbility ability = getAbility(id.checkint(), "ability?");
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
					ability.checkCanUse(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false, activationGetter);
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
					ability.checkCanUse(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false, activationGetter);
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
			public LuaValue call(final LuaValue id, final LuaValue bookType) {				final CAbility ability = getAbility(id.checkint(), bookType.checkjstring());
				if (ability != null) {
					final IconUI iconUI = getIconUI(abilityDataUI, ability);					return LuaString.valueOf(iconUI.getIconPath());
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

					int orderId = OrderIds.smart;
					if (ability instanceof SingleOrderAbility) {
						orderId = ((SingleOrderAbility) ability).getBaseOrderId();
					}

					final ExternStringMsgAbilityActivationReceiver activationReceiver = ExternStringMsgAbilityActivationReceiver.INSTANCE
							.reset();
					ability.checkCanUse(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false, activationReceiver);
					if (activationReceiver.isUseOk()) {
						if (LuaEnvironment.this.targetUnit != null) {
							final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = ExternStringMsgTargetCheckReceiver
									.<CWidget>getInstance().reset();
							ability.checkCanTarget(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false,
									LuaEnvironment.this.targetUnit.getSimulationWidget(), targetReceiver);
							if (targetReceiver.getTarget() != null) {
								uiOrderListener.issueTargetOrder(pawnUnit.getHandleId(), ability.getHandleId(), orderId,
										targetReceiver.getTarget().getHandleId(), false);
							}
							else {
								final ExternStringMsgTargetCheckReceiver<Void> noTargetReceiver = ExternStringMsgTargetCheckReceiver
										.<Void>getInstance().reset();
								ability.checkCanTargetNoTarget(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId,
										false, noTargetReceiver);
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
							ability.checkCanTargetNoTarget(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false,
									noTargetReceiver);
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
					ability.checkCanUse(game, pawnUnit, pawnUnit.getPlayerIndex(), OrderIds.smart /* TODO TODO */,
							false, activationGetter);
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
				// arg1 is the slot name with the "Character" prefix already stripped by the
				// Lua, e.g. "Bag0Slot".."Bag3Slot" for the equipped-bag bar buttons. We give
				// those stable inventory ids 20..23 so GetInventoryItemTexture can map them
				// back to Warcraft III inventory slots 0..3. Other (equipment) slots are
				// unsupported for now and report id 0.
				int itemId = 0;
				final String slotName = varargs.arg(1).optjstring("");
				if (slotName.startsWith("Bag") && slotName.endsWith("Slot")) {
					try {
						final int bagIndex = Integer.parseInt(slotName.substring(3, slotName.length() - 4));
						itemId = BAG_SLOT_INVENTORY_BASE + bagIndex;
					}
					catch (final NumberFormatException e) {
						itemId = 0;
					}
				}
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf(itemId), LuaValue.valueOf("") });
			}
		});
		this.globals.set("GetInventoryItemTexture", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey, final LuaValue id) {
				// While a bag-bar item is lifted onto the cursor, render its source slot empty.
				if ((LuaEnvironment.this.cursorItem != null)
						&& (LuaEnvironment.this.cursorBagId == CURSOR_SOURCE_INVENTORY)
						&& (LuaEnvironment.this.cursorSlot == (id.checkint() - BAG_SLOT_INVENTORY_BASE))) {
					return LuaValue.NIL;
				}
				final CItem item = getEquippedBagItem(id.checkint());
				String path = null;
				if (item != null) {
					final ItemUI itemUI = LuaEnvironment.this.abilityDataUI.getItemUI(item.getTypeId());
					if (itemUI != null) {
						path = itemUI.getItemIconPathForDragging();
					}
				}
				return path == null ? LuaValue.NIL : LuaValue.valueOf(path);
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
				final CItem item = getEquippedBagItem(id.checkint());
				return LuaValue.valueOf(item == null ? 0 : item.getCharges());
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
				final CWidget unit = getWidget(unitKey.checkjstring());
				return LuaString.valueOf(getWidgetName(unit));
			}
		});
		this.globals.set("UnitHealth", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CWidget unit = getWidget(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.ZERO;
				}
				return LuaValue.valueOf(Math.max(0, unit.getLife()));
			}
		});
		this.globals.set("UnitExists", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CWidget unit = getWidget(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.FALSE;
				}
				return LuaValue.TRUE;
			}
		});
		this.globals.set("UnitHealthMax", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue unitKey) {
				final CWidget unit = getWidget(unitKey.checkjstring());
				if (unit == null) {
					return LuaValue.ZERO;
				}
				return LuaValue.valueOf(unit.getMaxLife());
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
				final CWidget unit = getWidget(unitKey.checkjstring());
				final CWidget otherUnit = getWidget(otherUnitKey.checkjstring());
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
				if (target == null || player == null) {
					return LuaValue.valueOf(4);
				}

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
				if (unit == null) {
					return LuaBoolean.FALSE;
				}
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
				if (unit == null) {
					return LuaBoolean.FALSE;
				}
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
				if (unit == null) {
					return LuaBoolean.FALSE;
				}
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
				if (unit == null || otherUnit == null) {
					return LuaBoolean.FALSE;
				}
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
		// ===========
		// Loot window — the lootable subject is whatever lootable item is currently the
		// player's target (set by right-clicking a world item, see beginLootInteraction).
		// There is exactly one slot: the targeted item itself.
		this.globals.set("GetNumLootItems", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(getTargetItem() != null ? 1 : 0);
			}
		});
		this.globals.set("GetLootSlotInfo", new LuaFunction() {
			@Override
			public Varargs invoke(final Varargs varargs) {
				// WoW: GetLootSlotInfo(slot) -> texture, itemName, quantity, quality
				final CItem item = (varargs.arg(1).checkint() == 1) ? getTargetItem() : null;
				if (item == null) {
					return LuaValue.NIL;
				}
				final ItemUI itemUI = LuaEnvironment.this.abilityDataUI.getItemUI(item.getTypeId());
				final String texture = itemUI == null ? "" : itemUI.getItemIconPathForDragging();
				final String name = itemUI == null ? "" : itemUI.getName();
				return LuaValue.varargsOf(new LuaValue[] { LuaValue.valueOf(texture), LuaValue.valueOf(name),
						LuaValue.valueOf(Math.max(1, item.getCharges())), LuaValue.valueOf(1) });
			}
		});
		this.globals.set("LootSlotIsItem", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue slot) {
				return LuaValue.valueOf((slot.checkint() == 1) && (getTargetItem() != null));
			}
		});
		this.globals.set("LootSlotIsCoin", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue slot) {
				return LuaValue.FALSE;
			}
		});
		this.globals.set("GetLootSlotLink", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue slot) {
				return LuaValue.valueOf("");
			}
		});
		this.globals.set("IsFishingLoot", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.FALSE;
			}
		});
		// NOTE: LootSlot/CloseLoot are called as bare Lua STATEMENTS (no return captured),
		// which luaj dispatches via call(...) rather than invoke(); the abstract LuaFunction
		// base does not route call(...) to invoke (only the *ArgFunction subclasses do), so
		// these must extend TwoArgFunction/OneArgFunction or the call throws "attempt to call
		// function". (Natives invoked inside assignments, e.g. GetLootSlotInfo, can stay LuaFunction.)
		this.globals.set("LootSlot", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue slot, final LuaValue confirm) {
				// Take the looted item: order the hero to pick it up (smart -> getitem), which
				// walks to it and stores it in the inventory/a bag. Then close the window.
				final CItem item = (slot.checkint() == 1) ? getTargetItem() : null;
				if (item != null) {
					// abilityHandleId 0 lets the order resolver pick the ability that accepts a
					// smart pickup of an item (the inventory, or a bag on overflow).
					LuaEnvironment.this.uiOrderListener.issueTargetOrder(LuaEnvironment.this.pawnUnit.getHandleId(), 0,
							OrderIds.smart, item.getHandleId(), false);
				}
				notifyLootClosed();
				return LuaValue.NIL;
			}
		});
		this.globals.set("CloseLoot", new OneArgFunction() {
			@Override
			public LuaValue call(final LuaValue failFlag) {
				if (false) {
					// Called by LootFrame_OnHide whenever the window closes. End the loot crouch.
					LuaEnvironment.this.uiOrderListener.issueImmediateOrder(LuaEnvironment.this.pawnUnit.getHandleId(),
							LuaEnvironment.this.abilityPlayerPawn.getHandleId(), OrderIds.pawnLootReleased, false);
				}
				return LuaValue.NIL;
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

	/**
	 * Resolves a WoW container/bag id to its backing {@link CAbilityBag}: id 0 is
	 * the pawn's built-in backpack; ids 1..4 are the carried bag items in Warcraft
	 * III inventory slots 0..3 (null when that slot holds no bag item).
	 */
	private CAbilityBag resolveBag(final int bagId) {
		if (bagId == 0) {
			return this.abilityPlayerPawn == null ? null : this.abilityPlayerPawn.getBackpack();
		}
		final CItem bagItem = getEquippedBagItem(BAG_SLOT_INVENTORY_BASE + (bagId - 1));
		if (bagItem == null) {
			return null;
		}
		for (final CAbility ability : this.pawnUnit.getAbilities()) {
			if (ability instanceof CAbilityBag) {
				final CAbilityBag bag = (CAbilityBag) ability;
				if (bag.getItem() == bagItem) {
					return bag;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Warcraft III inventory item shown on an equipped-bag bar button,
	 * given that button's synthetic inventory slot id (>= {@link #BAG_SLOT_INVENTORY_BASE}).
	 * Each WoW bag button maps to one Warcraft III inventory slot; returns null when
	 * out of range or empty.
	 */
	private CItem getEquippedBagItem(final int inventorySlotId) {
		final int slotIndex = inventorySlotId - BAG_SLOT_INVENTORY_BASE;
		if (slotIndex < 0) {
			return null;
		}
		final CAbilityInventory inventory = this.pawnUnit.getInventoryData();
		if (inventory == null) {
			return null;
		}
		if (slotIndex >= inventory.getItemCapacity()) {
			return null;
		}
		return inventory.getItemInSlot(slotIndex);
	}

	/**
	 * WoW's PickupContainerItem toggle for a bag popup (bagId, 1-based slot): with an
	 * empty cursor it lifts the item there onto the cursor; with an item already held
	 * it drops it into that slot. The drop issues a Warcraft III bagitemdrag order on
	 * the destination {@link CAbilityBag}; that ability swaps within itself, or pulls
	 * the item across from whichever container currently holds it (another bag or the
	 * unit inventory) — see {@link CItemSlotHolder#transfer}. Stays authoritative in
	 * the lockstep sim.
	 */
	private void handleContainerPickupOrDrop(final int bagId, final int slot) {
		final CAbilityBag bag = resolveBag(bagId);
		if (bag == null) {
			return;
		}
		final int slotIndex = slot - 1;
		if ((slotIndex < 0) || (slotIndex >= bag.getSlotCount())) {
			return;
		}
		if (this.cursorItem == null) {
			final CItem item = bag.getItemInSlot(slotIndex);
			if (item == null) {
				return; // empty slot, nothing to pick up
			}
			this.cursorItem = item;
			this.cursorBagId = bagId;
			this.cursorSlot = slot;
			updateCursorVisual();
			notifyBagsChanged(); // render the lifted source slot as empty
		}
		else {
			dropCursorItemIntoBag(bagId, slotIndex);
		}
	}

	/**
	 * Issues the order that moves the held cursor item into the bag's 0-based slot
	 * (within-bag swap or cross-container move), then clears the cursor.
	 */
	private void dropCursorItemIntoBag(final int bagId, final int slotIndex) {
		final CAbilityBag bag = resolveBag(bagId);
		if ((bag != null) && (this.cursorItem != null) && (slotIndex >= 0) && (slotIndex < bag.getSlotCount())) {
			this.uiOrderListener.issueTargetOrder(this.pawnUnit.getHandleId(), bag.getHandleId(),
					OrderIds.bagitemdrag00 + slotIndex, this.cursorItem.getHandleId(), false);
		}
		clearCursorItemState();
		notifyBagsChanged(); // a same-slot (no-op) drop still needs the slot un-greyed
	}

	/**
	 * Issues the order that moves the held cursor item into the unit inventory's
	 * 0-based slot (within-inventory swap or cross-container move), then clears the
	 * cursor. Used when dropping onto a bag-bar button.
	 */
	private void dropCursorItemIntoInventorySlot(final int inventorySlotIndex) {
		final CAbilityInventory inventory = this.pawnUnit.getInventoryData();
		if ((inventory != null) && (this.cursorItem != null) && (inventorySlotIndex >= 0)
				&& (inventorySlotIndex < inventory.getItemCapacity())) {
			this.uiOrderListener.issueTargetOrder(this.pawnUnit.getHandleId(), inventory.getHandleId(),
					OrderIds.itemdrag00 + inventorySlotIndex, this.cursorItem.getHandleId(), false);
		}
		clearCursorItemState();
		notifyBagsChanged();
	}

	/** Pushes the current cursor item's icon (or null) to the display listener. */
	private void updateCursorVisual() {
		if (this.cursorItemDisplayListener != null) {
			String iconPath = null;
			if (this.cursorItem != null) {
				final ItemUI itemUI = this.abilityDataUI.getItemUI(this.cursorItem.getTypeId());
				if (itemUI != null) {
					iconPath = itemUI.getItemIconPathForDragging();
				}
			}
			this.cursorItemDisplayListener.onCursorItemChanged(iconPath);
		}
	}

	/** Clears the held-item state and the cursor visual. The item never left its slot. */
	private void clearCursorItemState() {
		this.cursorItem = null;
		this.cursorBagId = 0;
		this.cursorSlot = 0;
		updateCursorVisual();
	}

	public void setCursorItemDisplayListener(final CursorItemDisplayListener listener) {
		this.cursorItemDisplayListener = listener;
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

	/**
	 * Sets the Lua {@code this} global to the given frame and returns the previous
	 * value so a handler can restore it on exit. The {@code this} global is shared,
	 * so a handler that triggers a nested handler (e.g. OnClick calling Show, which
	 * fires OnShow) would otherwise leave {@code this} pointing at the inner frame
	 * when control returns. Pair with {@link #restoreThis}.
	 */
	public LuaValue loadSavingThis(final UIFrameLuaWrapper thisFrame) {
		final LuaValue previousThis = this.globals.get("this");
		this.globals.load(thisFrame);
		return previousThis;
	}

	public void restoreThis(final LuaValue previousThis) {
		this.globals.set("this", previousThis);
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
		if (idInt < 0) {
			return null;
		}
		// The WoW spellbook / action bar should list the same abilities that show as
		// icons on the Warcraft III command card -- so inventory, bags, item abilities,
		// and internal/passive no-icon abilities are skipped. We index into that
		// filtered view, and every ability-indexing native goes through here so the
		// indexing stays internally consistent. (This is per-ability, so an ability
		// that had multiple command-card icons contributes a single entry here.)
		int visibleIndex = 0;
		for (final CAbility cAbility : this.pawnUnit.getAbilities()) {
			if (!Boolean.TRUE.equals(cAbility.visit(CommandCardIconVisibilityVisitor.INSTANCE))) {
				continue;
			}
			if (visibleIndex == idInt) {
				return cAbility;
			}
			visibleIndex++;
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

		@Override
		public void upgradesChanged() {
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

	public CWidget getWidget(final String unitKey) {
		switch (unitKey) {
		case UNITKEY_TARGET:
			if (this.targetUnit == null) {
				return null;
			}
			return this.targetUnit.getSimulationWidget();
		case UNITKEY_PLAYER:
			return this.pawnUnit;
		case UNITKEY_MOUSEOVER:
			if (this.mouseOverUnit == null) {
				return null;
			}
			return this.mouseOverUnit.getSimulationWidget();
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
			this.targetStateListener = new CUnitStateListenerImplementation(UNITKEY_TARGET);
			if (this.targetUnit instanceof RenderUnit) {
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

	/**
	 * The current target as a lootable item: non-null only when the player's target is
	 * a CItem that is still lying in the world (not picked up). This is what makes an
	 * item "target" lootable to the WoW loot natives (GetNumLootItems, LootSlot, ...).
	 */
	private CItem getTargetItem() {
		if (this.targetUnit == null) {
			return null;
		}
		final CItem item = this.targetUnit.getSimulationWidget().visit(AbilityTargetVisitor.ITEM);
		if ((item == null) || item.isDead() || item.isHidden()) {
			return null;
		}
		return item;
	}

	/**
	 * Begins a loot interaction on the currently targeted item (set just before this by
	 * right-clicking the item): plays the hero's loot crouch and opens the WoW loot
	 * window (which then reads the loot natives above to show the single item).
	 */
	public void beginLootInteraction() {
		if (getTargetItem() == null) {
			return;
		}
		this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
				OrderIds.pawnLootPressed, false);
		notifyLootOpened();
	}

	private void fireLootEvent(final ThirdPersonLuaXmlEvent event) {
		final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(event);
		for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
			try {
				frameLuaWrapper.getFrame().getScripts().onEvent(event, LuaValue.NIL);
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void notifyLootOpened() {
		fireLootEvent(ThirdPersonLuaXmlEvent.LOOT_OPENED);
	}

	public void notifyLootClosed() {
		fireLootEvent(ThirdPersonLuaXmlEvent.LOOT_CLOSED);
	}

	/**
	 * Fires the WoW ACTIONBAR_UPDATE_COOLDOWN event to every frame registered for
	 * it (the action buttons). Their OnEvent handlers re-query GetActionCooldown and
	 * call CooldownFrame_SetTimer, which arms the cooldown swipe model. The engine
	 * has no native cooldown-changed callback, so callers poll this periodically.
	 */
	public void notifyActionBarCooldownsChanged() {
		final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(
				ThirdPersonLuaXmlEvent.ACTIONBAR_UPDATE_COOLDOWN);
		for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
			try {
				frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.ACTIONBAR_UPDATE_COOLDOWN,
						LuaValue.NIL);
			}
			catch (final Exception e) {
				// One frame's handler throwing must not abort the dispatch to the rest.
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fires the WoW BAG_UPDATE event for each bag id (0 = backpack, 1..n = the
	 * carried-bag bar buttons) so the bag bar buttons re-read their icons and any
	 * open container window re-reads its contents. The container handlers gate on
	 * arg1 == their bag id, so we pass the bag id as arg1. The engine has no native
	 * inventory-changed callback into this UI, so callers poll this periodically.
	 */
	public void notifyBagsChanged() {
		final LinkedHashSet<UIFrameLuaWrapper> registered = getRegistered(ThirdPersonLuaXmlEvent.BAG_UPDATE);
		if (registered.isEmpty()) {
			return;
		}
		final CAbilityInventory inventory = this.pawnUnit.getInventoryData();
		final int carriedBagButtons = inventory == null ? 0 : Math.min(4, inventory.getItemCapacity());
		for (int bagId = 0; bagId <= carriedBagButtons; bagId++) {
			final LuaValue arg1 = LuaValue.valueOf(bagId);
			for (final UIFrameLuaWrapper frameLuaWrapper : registered) {
				try {
					frameLuaWrapper.getFrame().getScripts().onEvent(ThirdPersonLuaXmlEvent.BAG_UPDATE, arg1);
				}
				catch (final Exception e) {
					// A single frame's handler throwing (e.g. a missing/strict native)
					// must not abort the dispatch to the remaining registered frames,
					// or unrelated UI (the bag bar icons) would stop refreshing.
					e.printStackTrace();
				}
			}
		}
	}

	public String getWidgetName(final CWidget widget) {
		if (widget == null) {
			return "NO UNIT";
		}
		CUnit unit = widget.visit(AbilityTargetVisitor.UNIT);
		if (unit == null) {
			CItem item = widget.visit(AbilityTargetVisitor.ITEM);
			if (item != null) {
//				return "Item";
				ItemUI itemUI = abilityDataUI.getItemUI(item.getTypeId());
				return itemUI.getName();
			}
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

			int orderId = OrderIds.smart;
			if (ability instanceof SingleOrderAbility) {
				orderId = ((SingleOrderAbility) ability).getBaseOrderId();
			}

			final ExternStringMsgAbilityActivationReceiver activationReceiver = ExternStringMsgAbilityActivationReceiver.INSTANCE
					.reset();
			ability.checkCanUse(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false, activationReceiver);
			if (activationReceiver.isUseOk()) {
				if (LuaEnvironment.this.targetUnit != null) {
					final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = ExternStringMsgTargetCheckReceiver
							.<CWidget>getInstance().reset();
					ability.checkCanTarget(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false,
							LuaEnvironment.this.targetUnit.getSimulationWidget(), targetReceiver);
					if (targetReceiver.getTarget() != null) {
						uiOrderListener.issueTargetOrder(pawnUnit.getHandleId(), ability.getHandleId(), orderId,
								targetReceiver.getTarget().getHandleId(), false);
					}
					else {
						final ExternStringMsgTargetCheckReceiver<Void> noTargetReceiver = ExternStringMsgTargetCheckReceiver
								.<Void>getInstance().reset();
						ability.checkCanTargetNoTarget(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false,
								noTargetReceiver);
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
					ability.checkCanTargetNoTarget(game, pawnUnit, pawnUnit.getPlayerIndex(), orderId, false,
							noTargetReceiver);
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

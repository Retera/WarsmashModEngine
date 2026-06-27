package com.etheller.warsmash.viewer5.handlers.w3x.ui.thirdperson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.warsmash.parsers.dbc.DbcParser;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderSoundEntries;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleStatusBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.ThirdPersonCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.thirdperson.CBehaviorPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashToggleableUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;
import com.hiveworkshop.rms.util.BinaryReader;

public class ThirdPersonUI implements WarsmashToggleableUI {
	// looks pretty but might break multiplayer (altho maybe I fixed it so it wont):
	private static final boolean ALLOW_INSTANT_REDIRECT = true;
	private static final boolean ALL_PLAYERS = false;
	private static final Vector2 screenCoordsVector = new Vector2();
	private ThirdPersonCameraManager cameraManager;
	private final War3MapViewer war3MapViewer;
	private final Scene uiScene;
	private final ExtendViewport uiViewport;
	private final Scene portraitScene;
	private final Rectangle tempRect = new Rectangle();
	private int lastX;
	private int lastY;
	private int touchDownX;
	private int touchDownY;
	private int button;
	private boolean showing = false;
	private GameUI rootFrame;
	private SpriteFrame cursorFrame;
	private TextureFrame cursorItemIconFrame;
	private boolean holdingCursorItem;
	private boolean touchDown;
//	private final ModelInstance skyModelInstance;
	private SimpleFrame mainMenuBar;
	private SimpleFrame worldFrame;
	private SimpleFrame uiParent;
	private SimpleStatusBarFrame mainMenuExpBar;
	private StringFrame mainMenuExpBarText;
	private UIFrame castingBarFrame;
	private ClickableFrame mouseDownUIFrame;
	private ClickableFrame mouseOverUIFrame;
	private UIFrame tooltipFrame;
	private StringFrame tooltipFrame1;
	private CUnit pawnUnit;
	private CAbilityPlayerPawn abilityPlayerPawn;
	private final CPlayerUnitOrderListener uiOrderListener;
	private final War3ID pawnId;
	private RenderWidget mouseOverUnit;
	private RenderWidget targetUnit;
	private final boolean userControlEnabled = true;
	private final AnyClickableUnitFilter anyClickableUnitFilter;
	private final AnyTargetableUnitFilter anyTargetableUnitFilter;
	private KeyedSounds uiSounds;
	private static final float COOLDOWN_REFRESH_INTERVAL = 0.1f;
	private float cooldownRefreshTimer;
	private static final float BAG_REFRESH_INTERVAL = 0.25f;
	private boolean autoSpinLeft = false;
	private boolean autoSpinRight = false;

	public ThirdPersonUI(final War3MapViewer war3MapViewer, final Scene uiScene, final ExtendViewport uiViewport,
			final Scene portraitScene, final CPlayerUnitOrderListener uiOrderListener, final War3ID pawnId) {
		this.war3MapViewer = war3MapViewer;
		this.uiScene = uiScene;
		this.uiViewport = uiViewport;
		this.portraitScene = portraitScene;
		this.uiOrderListener = uiOrderListener;
		this.pawnId = pawnId;
		this.anyClickableUnitFilter = new AnyClickableUnitFilter();
		this.anyTargetableUnitFilter = new AnyTargetableUnitFilter();

//		final MdxModel skyModel = war3MapViewer
//				.loadModelMdx("environment\\sky\\lordaeronsummersky\\lordaeronsummersky.mdx");
//		this.skyModelInstance = skyModel.addInstance();
////		this.skyModelInstance.setParent(pawnComplexInstance.getAttachment(0));
//		this.skyModelInstance.setScene(war3MapViewer.worldScene);
//		this.skyModelInstance.uniformScale(10);
//		this.skyModelInstance.setLocation(0, 0, 0);
//		((MdxComplexInstance) this.skyModelInstance).setSequence(0);

	}

	@Override
	public void main() {
		final List<CUnit> pawnUnits = new ArrayList<>();
		if (ALL_PLAYERS) {
			for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
				final float[] startLocation = this.war3MapViewer.simulation.getPlayer(i).getStartLocation();
				pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId, i, startLocation[0],
						startLocation[1], 0));
			}

			this.pawnUnit = pawnUnits.get(this.war3MapViewer.getLocalPlayerIndex());
		}
		else {
			final float[] startLocation = this.war3MapViewer.simulation
					.getPlayer(this.war3MapViewer.getLocalPlayerIndex()).getStartLocation();
//			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0));

			// WESTFALL====
			// -31797.357, -341638.3
//			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
//			this.war3MapViewer.getLocalPlayerIndex(), startLocation[0] - 31797.357f,
//			startLocation[1] - 341638.3f, 0));
			
			// SW======
//			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
//			this.war3MapViewer.getLocalPlayerIndex(), -9385.966f, -298138.1f, 0));
			
			// BR======
			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
			this.war3MapViewer.getLocalPlayerIndex(), 31608.973f, -262313.06f, 0));

			// IF=====

//			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0] + 24126.52f,
//					startLocation[1] - 172875.25f, 0));
//			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0] + 3250, startLocation[1] - 29795.25f,
//					0));
//			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0] + 96242.28f,
//					startLocation[1] -202859.8f, 0));

			// Stranglethorn cave:
//				pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
//					this.war3MapViewer.getLocalPlayerIndex(), 19943.59f, -415891.3f, 74.39996f));// 20000, -425481.5f,
			// 96.36743f));//
			// startLocation[0]
			// +
			// 60000,
			// startLocation[1]
			// -
			// 420000, 0));
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hwtw"),
//			this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hpea"),
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hpea"),
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hpea"),
//			this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 2000,
//					startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 4000,
//					startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 6000,
//					startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 8000,
//					startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("ewis"), 0, startLocation[0],
//					startLocation[1], 0);
			new Thread(new Runnable() {

				@Override
				public void run() {
					final Scanner scanner = new Scanner(System.in);
					while (scanner.hasNextLine()) {
						final String nextLine = scanner.nextLine();
						final String[] bits = nextLine.split(" ");
						try {
							Gdx.app.postRunnable(new Runnable() {

								@Override
								public void run() {
									if ("SetTerrainWdtHole".equals(bits[0])) {
										final boolean isHole = "true".equals(bits[1]);

										System.out.println(
												"call SetTerrainWdtHole(" + ThirdPersonUI.this.pawnUnit.getX() + ", "
														+ ThirdPersonUI.this.pawnUnit.getY() + ", " + isHole + ")");
										ThirdPersonUI.this.war3MapViewer.terrain.setWdtHole(
												ThirdPersonUI.this.pawnUnit.getX(), ThirdPersonUI.this.pawnUnit.getY(),
												isHole);
									}
									else {

										final War3ID unitId = War3ID.fromString(bits[0]);
										int playerId = 0;
										if (bits.length > 0) {
											playerId = Integer.parseInt(bits[1]);
										}
										final int finalPlayerId = playerId;
										final CUnit createdUnit = ThirdPersonUI.this.war3MapViewer.simulation
												.createUnit(unitId, finalPlayerId, ThirdPersonUI.this.pawnUnit.getX(),
														ThirdPersonUI.this.pawnUnit.getY(),
														ThirdPersonUI.this.pawnUnit.getFacing());
										if (createdUnit != null) {
											if (createdUnit.getFirstAbilityOfType(CAbilityPlayerPawn.class) != null) {
												System.out.println("set u = CreateUnit(Player(" + finalPlayerId + "), '"
														+ unitId.toString() + "', " + ThirdPersonUI.this.pawnUnit.getX()
														+ ", " + ThirdPersonUI.this.pawnUnit.getY() + ", "
														+ ThirdPersonUI.this.pawnUnit.getFacing() + ")");
												System.out.println("call SetPlayerPawnZ(u, "
														+ ThirdPersonUI.this.abilityPlayerPawn.getZ() + ")");
											}
											else {
												System.out.println("call CreateUnit(Player(" + finalPlayerId + "), '"
														+ unitId.toString() + "', " + ThirdPersonUI.this.pawnUnit.getX()
														+ ", " + ThirdPersonUI.this.pawnUnit.getY() + ", "
														+ ThirdPersonUI.this.pawnUnit.getFacing() + ")");
											}
										}
									}

								}
							});
						}
						catch (final Exception exc) {
							exc.printStackTrace();
						}
					}
				}
			}).start();
			;
			this.war3MapViewer.simulation.getPlayer(0).addGold(9999);
			this.war3MapViewer.simulation.getPlayer(0).addLumber(9999);
			if (false) {
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("Hart"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("H0sh"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("ocat"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("ocat"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				for (int i = 0; i < 15; i++) {
					this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("ocat"),
							this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				}
//			this.war3MapViewer.simulation.getPlayer(0).addGold(99999);
//			this.war3MapViewer.simulation.getPlayer(0).addLumber(99999);
			}

			// for (int i = 0; i < 6; i++) {
//				for (int k = 0; k < 7; k++) {
//					this.war3MapViewer.simulation.createItem(War3ID.fromString("tkno"), i * 32, k * 32);
//				}
//			}
//			for (int i = 0; i < 6; i++) {
//				for (int k = 0; k < 7; k++) {
//					this.war3MapViewer.simulation.createItem(War3ID.fromString("gold"), i * 32, k * 32);
//					this.war3MapViewer.simulation.createItem(War3ID.fromString("lmbr"), i * 32, k * 32);
//				}
//			}

			this.pawnUnit = pawnUnits.get(0);
		}
		this.abilityPlayerPawn = this.pawnUnit.getFirstAbilityOfType(CAbilityPlayerPawn.class);

		final RenderUnit pawnRenderUnit = this.war3MapViewer.getRenderPeer(this.pawnUnit);

		this.cameraManager = new ThirdPersonCameraManager(pawnRenderUnit, this.abilityPlayerPawn, this.war3MapViewer);
		this.cameraManager.setupCamera(this.war3MapViewer.worldScene);

		final CPlayer localPlayer = this.war3MapViewer.simulation.getPlayer(this.war3MapViewer.getLocalPlayerIndex());

		final WorldEditStrings worldEditStrings = new WorldEditStrings(this.war3MapViewer.mapMpq);
		final DataTable uiSoundsTable = new DataTable(worldEditStrings);
		try {
			DbcParser.parse(new BinaryReader(this.war3MapViewer.mapMpq.read("DBFilesClient\\SoundEntries.dbc")),
					new DbcDecoderSoundEntries(), uiSoundsTable);
		}
		catch (final IOException e1) {
			e1.printStackTrace();
		}

		this.uiSounds = new KeyedSounds(uiSoundsTable, this.war3MapViewer.mapMpq);
		this.rootFrame = new GameUI(this.war3MapViewer.mapMpq, GameUI.loadSkin(this.war3MapViewer.mapMpq, 0),
				this.uiViewport, this.uiScene, this.war3MapViewer, 0, this.war3MapViewer.getAllObjectData().getWts(),
				this.uiSounds);

		try {
			this.rootFrame.bindPawnUnit(this.pawnUnit, this.abilityPlayerPawn, this.war3MapViewer.getAbilityDataUI(),
					this.uiOrderListener);
			this.rootFrame.loadTOCFile("Interface\\FrameXML\\FrameXML.toc");
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}

		final UIFrame mainMenuBarFixed = this.rootFrame.getFrameByName("MainMenuBar", 0);
		mainMenuBarFixed.setVisible(true);

		// At the time when we do our setup here, the GlobalScope has not yet been created.
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				Trigger refreshBagsTrigger = new Trigger();
				refreshBagsTrigger.addAction((arguments, globalScope, triggerScope) -> {
					rootFrame.getLuaGlobals().notifyBagsChanged();
					return null;
				});
				pawnUnit.addEvent(war3MapViewer.simulation.getGlobalScope(), refreshBagsTrigger,
						JassGameEventsWar3.EVENT_UNIT_DROP_ITEM);
				pawnUnit.addEvent(war3MapViewer.simulation.getGlobalScope(), refreshBagsTrigger,
						JassGameEventsWar3.EVENT_UNIT_PICKUP_ITEM);
				pawnUnit.addEvent(war3MapViewer.simulation.getGlobalScope(), refreshBagsTrigger,
						JassGameEventsWar3.EVENT_UNIT_PAWN_ITEM);
				pawnUnit.addEvent(war3MapViewer.simulation.getGlobalScope(), refreshBagsTrigger,
						JassGameEventsWar3.EVENT_UNIT_USE_ITEM);

				Trigger refreshBagPositionsTrigger = new Trigger();
				refreshBagPositionsTrigger.addAction((arguments, globalScope, triggerScope) -> {
					int issuedOrderId = ((CommonTriggerExecutionScope) triggerScope).getIssuedOrderId();
					// Refresh after the swap is actually applied by the simulation (orders may be
					// delayed a tick), for both the unit-inventory itemdrag and the within-bag
					// bagitemdrag (CAbilityBag) drags.
					if ((issuedOrderId >= OrderIds.itemdrag00 && issuedOrderId <= OrderIds.itemdrag05)
							|| (issuedOrderId >= OrderIds.bagitemdrag00
									&& issuedOrderId < OrderIds.bagitemdrag00 + 256)) {
						rootFrame.getLuaGlobals().notifyBagsChanged();
					}
					return null;
				});
				pawnUnit.addEvent(war3MapViewer.simulation.getGlobalScope(), refreshBagPositionsTrigger,
						JassGameEventsWar3.EVENT_UNIT_ISSUED_TARGET_ORDER);
			}
		});

		this.tooltipFrame = this.rootFrame.createFrame("GameTooltip", this.rootFrame, 0, 0);
//		this.uiParent.add(this.tooltipFrame);
		this.tooltipFrame1 = (StringFrame) this.rootFrame.getFrameByName("$parentTextLeft1", 0);

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashTPCursorFrame",
				this.rootFrame, "", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, "Interface\\Cursor\\Cursor.mdx");
		this.cursorFrame.setSequence("Point");
		this.cursorFrame.setZDepth(1.0f);
		this.cursorFrame.setVisible(false);

		// The WoW cursor model has no "carry the item icon" mechanism (its sequences are
		// Point/Pickup/etc. and it has no replaceable-id texture slot), and the FrameXML
		// never draws the held item either — in real WoW the C client paints it. So we
		// draw it ourselves: a small texture that follows the cursor while an item is held
		// (positioned in update(), rendered on top in render()). Not added to the frame
		// tree; we drive its bounds/render manually.
		this.cursorItemIconFrame = new TextureFrame("SmashTPCursorItemIcon", this.rootFrame, false,
				TextureFrame.DEFAULT_TEX_COORDS);
		this.cursorItemIconFrame.setVisible(true);

		// Let the bag UI paint a dragged item onto the cursor (see setCursorItem).
		this.rootFrame.getLuaGlobals().setCursorItemDisplayListener(this::setCursorItem);

		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);
		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);
	}

	@Override
	public void update(final float deltaTime) {
		if (this.showing) {
			this.cameraManager.updateCamera();

			// The engine has no native ability-cooldown-changed hook into the WoW Lua UI,
			// so periodically tell the action buttons to re-check their cooldowns. Once a
			// cooldown is armed, the cooldown frame's OnUpdateModel script animates the
			// swipe every render frame on its own.
			this.cooldownRefreshTimer += deltaTime;
			if ((this.cooldownRefreshTimer >= COOLDOWN_REFRESH_INTERVAL) && (this.rootFrame != null)) {
				this.cooldownRefreshTimer = 0;
				this.rootFrame.getLuaGlobals().notifyActionBarCooldownsChanged();
			}
		}

		final int baseMouseX = Gdx.input.getX();
		final int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		final int mouseY = baseMouseY;

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);

		if (this.holdingCursorItem) {
			// Follow the cursor with the held-item icon (drawn in render()). Size relative
			// to the UI world height so it scales with resolution (~a 36px icon at 720p).
			final float iconSize = this.uiViewport.getMinWorldHeight() * 0.05f;
			this.cursorItemIconFrame.getRenderBounds().set(screenCoordsVector.x - (iconSize / 2),
					screenCoordsVector.y - (iconSize / 2), iconSize, iconSize);
		}

		if (this.showing) {
			this.cursorFrame.setVisible(!this.touchDown);
		}

		boolean wasAutoSpinRight = this.autoSpinRight;
		boolean wasAutoSpinLeft = this.autoSpinLeft;
		if (!ALLOW_INSTANT_REDIRECT && this.touchDown && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			float targetAngle = (float) Math.toDegrees(this.cameraManager.horizontalAngle);
			float currentAngle = this.pawnUnit.getFacing();
			targetAngle = ((targetAngle % 360) + 360) % 360;
			currentAngle = ((currentAngle % 360) + 360) % 360;
			if (targetAngle < currentAngle - 180) {
				targetAngle += 360;
			}
			if (targetAngle > currentAngle + 180) {
				targetAngle -= 360;
			}
			float delta = targetAngle - currentAngle;
			boolean newAutoSpinLeft = delta > 10;
			boolean newAutoSpinRight = delta < -10;
			autoSpinLeft = newAutoSpinLeft;
			autoSpinRight = newAutoSpinRight;
		} else {
			autoSpinLeft = autoSpinRight = false;
		}
		if (autoSpinLeft != wasAutoSpinLeft) {
			if (autoSpinLeft) {
				this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
						OrderIds.pawnLeftPressed, false);
			} else {
				this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
						OrderIds.pawnLeftReleased, false);
			} 
		}
		if (autoSpinRight != wasAutoSpinRight) {
			if (autoSpinRight) {
				this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
						OrderIds.pawnRightPressed, false);
			} else {
				this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
						OrderIds.pawnRightReleased, false);
			}
		}
	}

	/**
	 * Reflects the held bag item on the mouse cursor by driving the WoW cursor model's
	 * own animation, the same way {@code MeleeUI} drives the WC3 cursor's HoldItem
	 * sequence — but we keep this UI's WoW 0.5.3 cursor model
	 * ({@code Interface\Cursor\Cursor.mdx}) and play ITS corresponding sequence. That
	 * model's sequences are Point, Cast, Pickup, Attack, Buy, Interact, Speak, ...; the
	 * item-drag analogue is "Pickup". A null/empty path returns to "Point".
	 *
	 * <p>
	 * The WoW cursor model can't carry the item's icon (no replaceable-id slot, and the
	 * FrameXML doesn't draw it — that's the C client's job in real WoW), so in addition
	 * to the "Pickup" hand animation we show the actual item icon via a small overlay
	 * texture ({@link #cursorItemIconFrame}) that tracks the cursor.
	 */
	private void setCursorItem(final String itemIconPath) {
		if ((itemIconPath != null) && !itemIconPath.isEmpty()) {
			this.cursorItemIconFrame.setTexture(itemIconPath, this.rootFrame);
			this.holdingCursorItem = true;
			this.uiSounds.getSound("igAbilityIconPickup").play(this.uiScene.audioContext, 0, 0, 0);
		}
		else {
			this.cursorItemIconFrame.setTexture((TextureRegion) null);
			this.holdingCursorItem = false;
			this.uiSounds.getSound("igAbilityIconDrop").play(this.uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		final BitmapFont font = this.rootFrame.getFont();
		if (WarsmashConstants.SHOW_FPS) {
			final Color originalColor = font.getColor();
			final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
			glyphLayout.setText(font, fpsString);
			font.setColor(Color.BLACK);
			font.draw(batch, fpsString, ((this.uiViewport.getMinWorldWidth() - glyphLayout.width) / 2) + 2,
					(this.uiViewport.getMinWorldHeight() * 0.93f) + 2);
			font.setColor(Color.YELLOW);
			font.draw(batch, fpsString, (this.uiViewport.getMinWorldWidth() - glyphLayout.width) / 2,
					this.uiViewport.getMinWorldHeight() * 0.93f);
			font.setColor(originalColor);
		}
		this.rootFrame.render(batch, this.rootFrame.getFont20(), glyphLayout);
		// Draw the held-item icon last so it sits on top of the cursor and all UI.
		if (this.holdingCursorItem) {
			this.cursorItemIconFrame.render(batch, this.rootFrame.getFont20(), glyphLayout);
		}
		final float worldWidth = this.uiViewport.getMinWorldWidth();
		final float worldHeight = this.uiViewport.getMinWorldHeight();
	}

	@Override
	public void dispose() {

	}

	@Override
	public void resize(final int width, final int height) {
		this.cameraManager.resize(setupWorldFrameViewport(width, height));
	}

	private Rectangle setupWorldFrameViewport(final int width, final int height) {
		this.tempRect.x = 0;
		this.tempRect.width = width;
		final float topHeight = 0;
		final float bottomHeight = 0;
		this.tempRect.y = (int) bottomHeight;
		this.tempRect.height = height - (int) (topHeight + bottomHeight);
		return this.tempRect;
	}

	@Override
	public boolean keyDown(final int keycode) {
		if (keycode == Input.Keys.SPACE) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnJumpPressed, false);
		}
		else if (keycode == Input.Keys.X) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnSitPressed, false);
		}
		if (keycode == Input.Keys.Z) {
			CBehaviorPlayerPawn.HACKON = !CBehaviorPlayerPawn.HACKON;
		}
		if ((keycode == Input.Keys.LEFT) || (keycode == Input.Keys.A)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnLeftPressed, false);
			return true;
		}
		else if ((keycode == Input.Keys.RIGHT) || (keycode == Input.Keys.D)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnRightPressed, false);
			return true;
		}
		else if ((keycode == Input.Keys.DOWN) || (keycode == Input.Keys.S)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnDownPressed, false);
			return true;
		}
		else if ((keycode == Input.Keys.UP) || (keycode == Input.Keys.W)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnUpPressed, false);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		if (keycode == Input.Keys.SPACE) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnJumpReleased, false);
		}
		else if ((keycode == Input.Keys.LEFT) || (keycode == Input.Keys.A)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnLeftReleased, false);
			return true;
		}
		else if ((keycode == Input.Keys.RIGHT) || (keycode == Input.Keys.D)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnRightReleased, false);
			return true;
		}
		else if ((keycode == Input.Keys.DOWN) || (keycode == Input.Keys.S)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnDownReleased, false);
			return true;
		}
		else if ((keycode == Input.Keys.UP) || (keycode == Input.Keys.W)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnUpReleased, false);
			return true;
		}
		else {
			this.rootFrame.getLuaGlobals().keyUp(keycode);
		}
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		this.lastX = this.touchDownX = screenX;
		this.lastY = this.touchDownY = screenY;
		this.button = button;

		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchDown(screenCoordsVector.x, screenCoordsVector.y, button);
		if (clickedUIFrame == null) {
			this.touchDown = true;
			if (this.mouseOverUnit == null) {
				this.cameraManager.setTouchDown(true);
			}
		}
		else {
			if (clickedUIFrame instanceof ClickableFrame) {
				this.mouseDownUIFrame = (ClickableFrame) clickedUIFrame;
				this.mouseDownUIFrame.mouseDown(this.rootFrame, this.uiViewport);
			}
		}
		return false;
	}

	private void updateMouseOverUnit(final int screenX, final float worldScreenY) {
		final RenderWidget newMouseOverUnit;
		if (this.userControlEnabled) {
			newMouseOverUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY, this.anyClickableUnitFilter);
		}
		else {
			newMouseOverUnit = null;
		}
		if (newMouseOverUnit != this.mouseOverUnit) {
			this.war3MapViewer.clearUnitMouseOverHighlight();
			if (newMouseOverUnit != null) {
				this.war3MapViewer.showUnitMouseOverHighlightThirdPerson(newMouseOverUnit);
			}
			this.mouseOverUnit = newMouseOverUnit;
			this.rootFrame.getLuaGlobals().notifyUpdateMouseOver(this.mouseOverUnit);
		}
	}

	/**
	 * If a lootable world item is under the cursor, target it (so the loot natives see
	 * it) and start the loot interaction (hero loot animation + loot window). Returns
	 * true if an item was found and loot was initiated.
	 */
	private boolean tryLootUnderCursor(final int screenX, final float worldScreenY) {
		final RenderWidget picked = this.war3MapViewer.rayPickUnit(screenX, worldScreenY, this.anyTargetableUnitFilter);
		if (picked == null) {
			return false;
		}
		final CItem item = picked.getSimulationWidget().visit(AbilityTargetVisitor.ITEM);
		if ((item == null) || item.isDead() || item.isHidden()) {
			return false;
		}
		setTarget(picked);
		this.rootFrame.getLuaGlobals().beginLootInteraction();
		return true;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (this.mouseDownUIFrame != null) {
			if (clickedUIFrame == this.mouseDownUIFrame) {
				this.mouseDownUIFrame.onClick(button);
				final String soundKey = this.mouseDownUIFrame.getSoundKey();
				if (soundKey != null) {
//					this.war3MapViewer.getUiSounds().getSound(soundKey).play(this.uiScene.audioContext, 0, 0, 0);
				}
			}
			this.mouseDownUIFrame.mouseUp(this.rootFrame, this.uiViewport);
		}
		else {
			this.touchDown = false;
			// Right-click (without a camera drag) on a lootable world item: target it and
			// open the loot window. Handled before the camera/select logic below.
			if ((button == Input.Buttons.RIGHT) && (this.touchDownX == screenX) && (this.touchDownY == screenY)
					&& tryLootUnderCursor(screenX, worldScreenY)) {
				if (this.cameraManager.isTouchDown()) {
					this.cameraManager.setTouchDown(false);
				}
				Gdx.input.setCursorPosition(this.touchDownX, this.touchDownY);
				this.mouseDownUIFrame = null;
				return false;
			}
			if (button == Input.Buttons.LEFT) {
				updateMouseOverUnit(screenX, worldScreenY);
			}
			if (this.cameraManager.isTouchDown()) {
				this.cameraManager.setTouchDown(false);
			}
			else {
				if ((this.mouseOverUnit != null) && isUnitSelectable(this.mouseOverUnit)) {
					this.war3MapViewer.doSelectUnitThirdPerson(Arrays.asList(this.mouseOverUnit));
					final CUnit mousedUnit = this.mouseOverUnit.getSimulationWidget().visit(AbilityTargetVisitor.UNIT);
					if (mousedUnit != null) {
						final CPlayer localPlayer = this.war3MapViewer.simulation
								.getPlayer(this.war3MapViewer.getLocalPlayerIndex());
						final CPlayer mousedUnitPlayer = this.war3MapViewer.simulation
								.getPlayer(mousedUnit.getPlayerIndex());
						String soundKey = "GAMETARGETNEUTRALUNIT";
						if (localPlayer.hasAlliance(mousedUnit.getPlayerIndex(), CAllianceType.PASSIVE)) {
							if (mousedUnitPlayer.hasAlliance(this.war3MapViewer.getLocalPlayerIndex(),
									CAllianceType.HELP_REQUEST)) {
								soundKey = "GAMETARGETFRIENDLYUNIT";
							}
						}
						else {
							soundKey = "GAMETARGETHOSTILEUNIT";
						}
//						this.uiSounds.getSound(soundKey).play(this.uiScene.audioContext, 0, 0, 0);
					}
					setTarget(this.mouseOverUnit);
				}
			}
			if ((this.mouseOverUnit == null) && (this.touchDownX == screenX) && (this.touchDownY == screenY)) {
				if (this.targetUnit != null) {
//					this.uiSounds.getSound("INTERFACESOUND_LOSTTARGETUNIT").play(this.uiScene.audioContext, 0, 0, 0);
					this.war3MapViewer.deselect();
					setTarget(null);
				}
			}
			Gdx.input.setCursorPosition(this.touchDownX, this.touchDownY);
		}
		this.mouseDownUIFrame = null;
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		final int newX = screenX;
		final int newY = screenY;
		final int dx = newX - this.lastX;
		final int dy = newY - this.lastY;
		if (this.touchDown) {
			if (this.button == Input.Buttons.LEFT) {
				this.cameraManager.horizontalAngle -= Math.toRadians(dx * 0.15 * 2);
				this.cameraManager.verticalAngle -= Math.toRadians(dy * 0.15 * 2);
			}
			else if (this.button == Input.Buttons.RIGHT) {
				this.cameraManager.horizontalAngle -= Math.toRadians(dx * 0.15 * 2);
				this.cameraManager.verticalAngle -= Math.toRadians(dy * 0.15 * 2);
				if (ALLOW_INSTANT_REDIRECT) {
					float targetAngle = (float) Math.toDegrees(this.cameraManager.horizontalAngle);
//					pawnUnit.setFacing(targetAngle);
					this.uiOrderListener.issuePointOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
							OrderIds.pawnCheesyRightMouseTurn, 0, targetAngle, false);
				}
			}
			this.lastX = newX;
			this.lastY = newY;
		}
		else if (this.mouseDownUIFrame != null) {
			screenCoordsVector.set(screenX, screenY);
			this.uiViewport.unproject(screenCoordsVector);
			this.mouseDownUIFrame.mouseDragged(this.rootFrame, this.uiViewport, screenCoordsVector.x,
					screenCoordsVector.y);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		this.lastX = screenX;
		this.lastY = screenY;
		updateHoverFrame(screenX, screenY);
		updateMouseOverUnit(screenX, worldScreenY);
		return false;
	}

	/**
	 * Recomputes which UI frame is under the cursor and fires mouseEnter/mouseExit
	 * accordingly (driving the engine's mouse-over HighlightTexture). Run both on
	 * discrete mouseMoved events AND every frame from update(): the dynamically
	 * generated bag item buttons could otherwise keep a stale highlight if a single
	 * move event off them is ever missed, since their HighlightTexture is shown purely
	 * while the frame's mouseOver flag is set (it is not a Lua/checkbox state and no
	 * Lua event resets it).
	 */
	private void updateHoverFrame(final int screenX, final int screenY) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame mousedUIFrame = this.rootFrame.getFrameChildUnderMouse(screenCoordsVector.x,
				screenCoordsVector.y);
		if (mousedUIFrame != this.mouseOverUIFrame) {
			if (this.mouseOverUIFrame != null) {
				this.mouseOverUIFrame.mouseExit(this.rootFrame, this.uiViewport);
			}
			if (mousedUIFrame instanceof ClickableFrame) {
				this.mouseOverUIFrame = (ClickableFrame) mousedUIFrame;
				this.mouseOverUIFrame.mouseEnter(this.rootFrame, this.uiViewport);
				if (mousedUIFrame instanceof ClickableActionFrame) {
					loadTooltip((ClickableActionFrame) mousedUIFrame);
				}
			}
			else {
				this.mouseOverUIFrame = null;
				if (this.tooltipFrame != null) {
					this.tooltipFrame.setVisible(false);
				}
			}
		}
	}

	private void loadTooltip(final ClickableActionFrame mousedUIFrame) {
		final String toolTip = mousedUIFrame.getToolTip();
		final String uberTip = mousedUIFrame.getUberTip();
		if ((toolTip == null) || (uberTip == null)) {
			if (this.tooltipFrame != null) {
				this.tooltipFrame.setVisible(false);
			}
		}
		else {
			if (this.tooltipFrame1 != null) {
				this.rootFrame.setText(this.tooltipFrame1, uberTip);
			}
		}
	}

	public void setTarget(final RenderWidget target) {
		this.targetUnit = target;
		this.rootFrame.getLuaGlobals().notifySetTarget(this.targetUnit);

	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		this.cameraManager.distance += amountY * 10;
		return false;
	}

	@Override
	public void gameClosed() {

	}

	@Override
	public void onHide() {
		this.war3MapViewer.simulation.setFogEnabled(true);
		this.war3MapViewer.simulation.setFogMaskEnabled(true);
		this.war3MapViewer.removeScene(this.uiScene);
		this.war3MapViewer.removeScene(this.portraitScene);
		this.war3MapViewer.addScene(this.portraitScene);
		this.war3MapViewer.addScene(this.uiScene);
		this.showing = false;
		this.cursorFrame.setVisible(false);
//		this.skyModelInstance.hide();

	}

	@Override
	public void onShow() {
		this.war3MapViewer.simulation.setFogEnabled(false);
		this.war3MapViewer.simulation.setFogMaskEnabled(false);
		this.war3MapViewer.removeScene(this.portraitScene);
//		this.war3MapViewer.addScene(this.portraitScene);
//		this.war3MapViewer.addScene(this.uiScene);
		this.showing = true;
		this.cursorFrame.setVisible(true);
//		this.skyModelInstance.show();
	}

	private final class AnyClickableUnitFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			final RenderWidget renderPeer = ThirdPersonUI.this.war3MapViewer.getRenderPeer(unit);
			return /*!unit.isDead() && */renderPeer.isSelectable(ThirdPersonUI.this.war3MapViewer.simulation,
					ThirdPersonUI.this.war3MapViewer.getLocalPlayerIndex());
		}
	}

	private final class AnyTargetableUnitFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			return true;//!unit.isDead();
		}
	}

	private boolean isUnitSelectable(final RenderWidget mouseOverUnit) {
		return mouseOverUnit.isSelectable(this.war3MapViewer.simulation, this.war3MapViewer.getLocalPlayerIndex())
				/*&& !mouseOverUnit.getSimulationWidget().isDead()*/;
	}
}

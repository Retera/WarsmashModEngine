package com.etheller.warsmash.parsers.fdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.fdfparser.FDFParser;
import com.etheller.warsmash.fdfparser.FrameDefinitionVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.BackdropCornerFlags;
import com.etheller.warsmash.parsers.fdf.datamodel.ControlStyle;
import com.etheller.warsmash.parsers.fdf.datamodel.FontDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FontFlags;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameClass;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;
import com.etheller.warsmash.parsers.fdf.datamodel.MenuItem;
import com.etheller.warsmash.parsers.fdf.datamodel.SetPointDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector2Definition;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector3Definition;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FloatFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FontFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringPairFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.TextJustifyFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector2FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector3FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector4FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetMenuItemFieldVisitor;
import com.etheller.warsmash.parsers.fdf.frames.AbstractUIFrame;
import com.etheller.warsmash.parsers.fdf.frames.BackdropFrame;
import com.etheller.warsmash.parsers.fdf.frames.CheckBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.ClickConsumingTextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.ControlFrame;
import com.etheller.warsmash.parsers.fdf.frames.EditBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.FilterModeTextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.MenuFrame;
import com.etheller.warsmash.parsers.fdf.frames.MenuFrame.MenuClickListener;
import com.etheller.warsmash.parsers.fdf.frames.PopupMenuFrame;
import com.etheller.warsmash.parsers.fdf.frames.ScrollBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleStatusBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame2;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextAreaFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.fdf.frames.XmlButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.XmlCheckBoxFrame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.AbstractMdxModelViewer;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.FocusableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;

public final class GameUI extends AbstractUIFrame implements UIFrame {
	private static final String IN_STRING = " in ";
	private static final boolean SHOW_BLACKNESS_BEHIND_DIALOGS = false;
	public static final boolean DEBUG = false;
	public static final boolean DEBUG_LOG = false;
	private static final boolean PIN_FAIL_IS_FATAL = false;
	private final DataSource dataSource;
	private final Element skin;
	private final Viewport viewport;
	private final Scene uiScene;
	private final AbstractMdxModelViewer modelViewer;
	private final int racialCommandIndex;
	private final FrameTemplateEnvironment templates;
	private final Map<String, Texture> pathToTexture = new HashMap<>();
	private boolean autoPosition = true;
	private final FontGeneratorHolder fontGenerator;
	private final FreeTypeFontParameter fontParam;
	private final Map<String, UIFrame> nameToFrame = new HashMap<>();
	private final Viewport fdfCoordinateResolutionDummyViewport;
	private final DataTable skinData;
	private final Element errorStrings;
	private final GlyphLayout glyphLayout;
	private WTS mapStrings;
	private final KeyedSounds uiSounds;
	private final BitmapFont font;
	private final BitmapFont font20;
	private final DynamicFontGeneratorHolder dynamicFontGeneratorHolder;
	private final List<FocusableFrame> focusableFrames = new ArrayList<>();

	private LuaEnvironment luaGlobals;
	private CUnit pawnUnit;
	private CAbilityPlayerPawn abilityPlayerPawn;
	private AbilityDataUI abilityDataUI;
	private final List<Runnable> pendingScriptLoads = new ArrayList<>();

	public GameUI(final DataSource dataSource, final GameSkin skin, final Viewport viewport, final Scene uiScene,
			final AbstractMdxModelViewer modelViewer, final int racialCommandIndex, final WTS mapStrings,
			final KeyedSounds uiSounds) {
		super("GameUI", null);
		this.dataSource = dataSource;
		this.skin = skin.getSkin();
		this.viewport = viewport;
		this.uiScene = uiScene;
		this.modelViewer = modelViewer;
		this.racialCommandIndex = racialCommandIndex;
		if (viewport instanceof ExtendViewport) {
			this.renderBounds.set(0, 0, ((ExtendViewport) viewport).getMinWorldWidth(),
					((ExtendViewport) viewport).getMinWorldHeight());
		}
		else {
			this.renderBounds.set(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		}
		this.templates = new FrameTemplateEnvironment();

		this.dynamicFontGeneratorHolder = new DynamicFontGeneratorHolder(this.modelViewer.dataSource, this.skin);
		this.fontGenerator = this.dynamicFontGeneratorHolder.getFontGenerator("MasterFont");
		final FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = 32;
		this.font = this.fontGenerator.generateFont(fontParam);
		fontParam.size = 20;
		this.font20 = this.fontGenerator.generateFont(fontParam);
		this.fontParam = new FreeTypeFontParameter();
		this.fdfCoordinateResolutionDummyViewport = new FitViewport(0.8f, 0.6f);
		this.skinData = skin.getSkinsTable();
		this.errorStrings = this.skinData.get("Errors");
		this.glyphLayout = new GlyphLayout();
		this.mapStrings = mapStrings;
		this.uiSounds = uiSounds;
	}

	public static GameSkin loadSkin(final DataSource dataSource, final String skin) {
		final DataTable skinsTable = new DataTable(StringBundle.EMPTY);
		try (InputStream stream = dataSource.getResourceAsStream("UI\\war3skins.txt")) {
			skinsTable.readTXT(stream, true);
			try (InputStream miscDataTxtStream = dataSource.getResourceAsStream("Units\\CommandFunc.txt")) {
				skinsTable.readTXT(miscDataTxtStream, true);
			}
			try (InputStream miscDataTxtStream = dataSource.getResourceAsStream("Units\\CommandStrings.txt")) {
				skinsTable.readTXT(miscDataTxtStream, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		// TODO eliminate duplicate read of skin TXT!!
		if (dataSource.has("war3mapSkin.txt")) {
			try (InputStream miscDataTxtStream = dataSource.getResourceAsStream("war3mapSkin.txt")) {
				skinsTable.readTXT(miscDataTxtStream, true);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
//		final Element main = skinsTable.get("Main");
//		final String skinsField = main.getField("Skins");
//		final String[] skins = skinsField.split(",");
		final Element defaultSkin = skinsTable.get("Default");
		final Element userSkin = skinsTable.get(skin);
		final Element customSkin = skinsTable.get("CustomSkin");
		for (final String key : defaultSkin.keySet()) {
			if (!userSkin.hasField(key)) {
				userSkin.setField(key, defaultSkin.getField(key));
			}
		}
		if (customSkin != null) {
			for (final String key : customSkin.keySet()) {
				userSkin.setField(key, customSkin.getField(key));
			}
		}
		return new GameSkin(userSkin, skinsTable);
	}

	public static GameSkin loadSkin(final DataSource dataSource, final int skinIndex) {
		final DataTable skinsTable = new DataTable(StringBundle.EMPTY);
		try (InputStream stream = dataSource.getResourceAsStream("UI\\war3skins.txt")) {
			skinsTable.readTXT(stream, true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		// TODO eliminate duplicate read of skin TXT!!
		if (dataSource.has("war3mapSkin.txt")) {
			try (InputStream miscDataTxtStream = dataSource.getResourceAsStream("war3mapSkin.txt")) {
				skinsTable.readTXT(miscDataTxtStream, true);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		final Element main = skinsTable.get("Main");
		final String skinsField = main.getField("Skins");
		final String[] skins = skinsField.split(",");
		final Element defaultSkin = skinsTable.get("Default");
		final Element userSkin;
		if ((skinIndex >= 0) && (skinIndex < skins.length)) {
			userSkin = skinsTable.get(skins[skinIndex]);
		}
		else {
			userSkin = new Element("UserSkin", skinsTable);
		}
		final Element customSkin = skinsTable.get("CustomSkin");
		for (final String key : defaultSkin.keySet()) {
			if (!userSkin.hasField(key)) {
				userSkin.setField(key, defaultSkin.getField(key));
			}
		}
		if (customSkin != null) {
			for (final String key : customSkin.keySet()) {
				userSkin.setField(key, customSkin.getField(key));
			}
		}
		return new GameSkin(userSkin, skinsTable);
	}

	public void loadTOCFile(final String tocFilePath) throws IOException {
		final DataSourceFDFParserBuilder dataSourceFDFParserBuilder = new DataSourceFDFParserBuilder(this.dataSource);
		final FrameDefinitionVisitor fdfVisitor = new FrameDefinitionVisitor(this.templates,
				dataSourceFDFParserBuilder);
		System.err.println("Loading TOC file: " + tocFilePath);
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(this.dataSource.getResourceAsStream(tocFilePath)))) {
			String line;
			int tocLines = 0;
			while ((line = reader.readLine()) != null) {
				if (line.toLowerCase().endsWith(".xml")) {
					if (line.startsWith("#")) {
						continue;
					}
					if (this.luaGlobals == null) {
						this.luaGlobals = new LuaEnvironment(this, this.viewport, this.uiScene, this.uiSounds,
								this.pawnUnit, this.abilityPlayerPawn, this.abilityDataUI);
						loadLuaFile("Interface/FrameXML/GlobalStrings.lua");// didn't see the link for what loads this
					}
					final DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
					try {
						final DocumentBuilder docBuilder = newInstance.newDocumentBuilder();
						final Document root = docBuilder.parse(this.dataSource.getResourceAsStream(line));
						final NodeList rootChildNodes = root.getChildNodes();
						for (int i = 0; i < rootChildNodes.getLength(); i++) {
							final Node uiChild = rootChildNodes.item(i);
							if (uiChild.getNodeName().equals("Ui")) {
								final int filenameCutoff = Math.max(line.lastIndexOf('\\'), line.lastIndexOf('/'));
								final List<FrameDefinition> mainUiElements = inflateMultipleXMLs(
										line.substring(0, filenameCutoff), uiChild.getChildNodes());
//								final SimpleFrame rootUiElement = new SimpleFrame("#Ui" + this.untitledXMLFrameId++,
//										this);
//								rootUiElement.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
//										convertX(this.viewport, 0), convertY(this.viewport, 0)));
//								rootUiElement.addAnchor(new AnchorDefinition(FramePoint.TOPRIGHT,
//										convertX(this.viewport, 0.8f), convertY(this.viewport, 0.6f)));
//								boolean foundAny = false;
								for (final FrameDefinition mainUiElement : mainUiElements) {
									if (mainUiElement.getFrameClass() == FrameClass.Scripts) {
										throw new IllegalStateException(
												"Ui root cannot have attached script events: " + line);
									}
									if (!mainUiElement.has("XMLVirtual")) {
										final UIFrame autoInflatedFrame = inflate(mainUiElement, this, null, false);
										autoInflatedFrame.positionBounds(this, this.viewport);
//										rootUiElement.add(autoInflatedFrame);
//										foundAny = true;
										final UIFrame autoFrameParent = autoInflatedFrame.getParent();
										if (autoFrameParent == this) {
											this.add(autoInflatedFrame);
										}
										else if (autoFrameParent instanceof AbstractUIFrame) {
											((AbstractUIFrame) autoFrameParent).add(autoInflatedFrame);
										}
									}
								}
								for (final Runnable job : this.pendingScriptLoads) {
									job.run();
								}
								this.pendingScriptLoads.clear();

//								if (foundAny) {
//									this.add(rootUiElement);
//								}
							}
							else if (uiChild.getNodeName().equals("#comment")) {
								if (DEBUG_LOG) {
									System.err.println("Skipping #comment in XML");
								}
							}
							else {
								throw new IllegalStateException("node name wasn't ui: " + uiChild.getNodeName());
							}
						}
					}
					catch (final ParserConfigurationException e) {
						e.printStackTrace();
					}
					catch (final SAXException e) {
						e.printStackTrace();
					}
					catch (final IOException e) {
						e.printStackTrace();
					}
				}
				else {
					final FDFParser firstFileParser = dataSourceFDFParserBuilder.build(line);
					fdfVisitor.visit(firstFileParser.program());
				}
				tocLines++;
			}
			System.out.println("TOC file loaded " + tocLines + " lines");
		}
	}

	public boolean hasSkinField(final String file) {
		return (file != null) && this.skin.hasField(file);
	}

	public String getSkinField(String file) {
		if (file == null) {
			throw new NullPointerException("file is null");
		}
		if (this.skin.hasField(file)) {
			file = this.skin.getField(file);
		}
		else {
			final String fieldVersioned = file + "_V" + WarsmashConstants.GAME_VERSION;
			if (this.skin.hasField(fieldVersioned)) {
				file = this.skin.getField(fieldVersioned);
			}
			else {
				throw new IllegalStateException("Decorated file name lookup not available: " + file);
			}
		}
		return file;
	}

	public String trySkinField(String file) {
		if (file == null) {
			throw new NullPointerException("file is null");
		}
		if (this.skin.hasField(file)) {
			file = this.skin.getField(file);
		}
		else {
			final String fieldVersioned = file + "_V" + WarsmashConstants.GAME_VERSION;
			if (this.skin.hasField(fieldVersioned)) {
				file = this.skin.getField(fieldVersioned);
			}
		}
		return file;
	}

	public DataTable getSkinData() {
		return this.skinData;
	}

	public UIFrame createFrame(final String name, final UIFrame owner, final int priority, final int createContext) {
		final FrameDefinition frameDefinition = this.templates.getFrame(name);
		final UIFrame inflatedFrame = inflate(frameDefinition, owner, null, frameDefinition.has("DecorateFileNames"));
		if (owner == this) {
			add(inflatedFrame);
		}
		return inflatedFrame;
	}

	public UIFrame createSimpleFrame(final String name, final UIFrame owner, final int createContext) {
		final FrameDefinition frameDefinition = this.templates.getFrame(name);
		if (frameDefinition == null) {
			final SimpleFrame simpleFrame = new SimpleFrame(name, owner);
			if (owner instanceof AbstractUIFrame) {
				((AbstractUIFrame) owner).add(simpleFrame);
			}
			else {
				add(simpleFrame);
			}
			return simpleFrame;
		}
		else if (frameDefinition.getFrameClass() == FrameClass.Frame) {
			final UIFrame inflated = inflate(frameDefinition, owner, null, frameDefinition.has("DecorateFileNames"));
			if (owner instanceof AbstractUIFrame) {
				((AbstractUIFrame) owner).add(inflated);
			}
			else {
				add(inflated);
			}
			return inflated;
		}
		return null;
	}

	public TextureFrame createTextureFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final Vector4Definition texCoord) {
		final TextureFrame textureFrame = new TextureFrame(name, parent, decorateFileNames, texCoord);
		this.nameToFrame.put(name, textureFrame);
		add(textureFrame);
		checkInternalMappingSize();
		return textureFrame;
	}

	public TextureFrame createTextureFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final Vector4Definition texCoord, final FilterMode filterMode) {
		final FilterModeTextureFrame textureFrame = new FilterModeTextureFrame(name, parent, decorateFileNames,
				texCoord);
		textureFrame.setFilterMode(filterMode);
		this.nameToFrame.put(name, textureFrame);
		add(textureFrame);
		checkInternalMappingSize();
		return textureFrame;
	}

	public StringFrame createStringFrame(final String name, final UIFrame parent, final Color color,
			final TextJustify justifyH, final TextJustify justifyV, final float fdfFontSize) {
		return createStringFrame(name, parent, color, null, justifyH, justifyV, fdfFontSize);
	}

	public StringFrame createStringFrame(final String name, final UIFrame parent, final Color color,
			final Color highlightColor, final TextJustify justifyH, final TextJustify justifyV,
			final float fdfFontSize) {
		final BitmapFont frameFont = generateFont(fdfFontSize);
		final StringFrame stringFrame = new StringFrame(name, parent, color, justifyH, justifyV, frameFont, name,
				highlightColor, null);
		this.nameToFrame.put(name, stringFrame);
		add(stringFrame);
		checkInternalMappingSize();
		return stringFrame;
	}

	@Override
	public void remove(final UIFrame childFrame) {
		this.nameToFrame.remove(childFrame.getName());
		super.remove(childFrame);
	}

	public BitmapFont generateFont(final float fdfFontSize) {
		this.fontParam.size = (int) convertY(this.viewport, fdfFontSize);
		if (this.fontParam.size == 0) {
			this.fontParam.size = 128;
		}
		final BitmapFont frameFont = this.fontGenerator.generateFont(this.fontParam);
		return frameFont;
	}

	private int untitledXMLFrameId = 0;

	private FrameDefinition inflateXMLToDef(final String currentWorkingDir, final Node xmlNode) {
		return inflateXMLToDef(currentWorkingDir, xmlNode, null);
	}

	private FrameDefinition inflateXMLToDef(final String currentWorkingDir, final Node xmlNode,
			final String forceType) {
		FrameDefinition frameDefinition = null;
		FrameClass frameClass = null;
		String frameType = null;
		final NamedNodeMap attributes = xmlNode.getAttributes();
		String baseXmlNodeName = xmlNode.getNodeName();
		if (forceType != null) {
			baseXmlNodeName = forceType;
		}
		if ("Script".equals(baseXmlNodeName)) {
			// NOTE: "Scripts" to be handled differently and hook evts
			final Node scriptFile = getAttributesNamedItem(attributes, "file");
			if (scriptFile != null) {
				String fileName = getAttributeText(scriptFile);
				if (fileName != null) {
					fileName = currentWorkingDir + "/" + fileName;
					loadLuaFile(fileName);
				}
			}
			else {
				this.luaGlobals.runLua(xmlNode.getTextContent());
			}
			return null;
		}
		else if ("Scripts".equals(baseXmlNodeName)) {
			final FrameDefinition luaScriptDef = new FrameDefinition(FrameClass.Scripts, "XMLLUASCRIPT",
					"#InlineLuaScripts" + this.untitledXMLFrameId++);
			final NodeList childNodes = xmlNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				final Node item = childNodes.item(i);
				final String nodeName = item.getNodeName();
				final String textContent = item.getTextContent();
				luaScriptDef.set(nodeName, new StringFrameDefinitionField(textContent));
			}
			return luaScriptDef;
		}
		else if ("#text".equals(baseXmlNodeName) || "#comment".equals(baseXmlNodeName)) {
			return null;
		}
		else if ("Frame".equals(baseXmlNodeName) || "GameTooltip".equals(baseXmlNodeName)
				|| "WorldFrame".equals(baseXmlNodeName)) {
			// TODO got my own world frame, ignoring this one
			frameClass = FrameClass.Frame;
			frameType = "SIMPLEFRAME";
		}
		else if ("StatusBar".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SIMPLESTATUSBAR";
		}
		else if ("EditBox".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "EDITBOX";
		}
		else if ("Slider".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SLIDER";
		}
		else if ("ScrollFrame".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SCROLLBAR";
		}
		else if ("ScrollChild".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SIMPLEFRAME";
		}
		else if ("TitleRegion".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SIMPLEFRAME";
		}
		else if ("MessageFrame".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SIMPLEFRAME";
		}
		else if ("ScrollingMessageFrame".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "TEXTAREA";
		}
		else if ("SimpleHTML".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "TEXTAREA";
		}
		else if ("Minimap".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SIMPLEFRAME";
		}
		else if ("Model".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SPRITE";
		}
		else if ("PlayerModel".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SPRITE";
		}
		else if ("TabardModel".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SPRITE";
		}
		else if ("FontString".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "TEXT";
		}
		else if ("Button".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "XMLBUTTON";
		}
		else if ("CheckButton".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "CHECKBOX";
		}
		else if ("Backdrop".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "BACKDROP";
		}
		else if ("Layer".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Frame;
			frameType = "SIMPLEFRAME"; // TODO changed this from just FrameClass.Layer with no type, but why?
		}
		else if ("Texture".equals(baseXmlNodeName)) {
			frameClass = FrameClass.Texture;
		}
		final Node nameAttribute = getAttributesNamedItem(attributes, "name");
		String name = null;
		if (nameAttribute != null) {
			name = getAttributeText(nameAttribute);
		}
		if (name == null) {
			name = "#UntitledXMLFrame" + this.untitledXMLFrameId++;
		}
		frameDefinition = new FrameDefinition(frameClass, frameType, name);
		frameDefinition.add("FromXML");
		if ("WorldFrame".equals(baseXmlNodeName) || "Layer".equals(baseXmlNodeName)) {
			frameDefinition.add("SetAllPoints");
		}
		final Node inheritsItem = getAttributesNamedItem(attributes, "inherits");
		if (inheritsItem != null) {
			final String inheritsText = getAttributeText(inheritsItem);
			if (inheritsText != null) {
				final FrameDefinition toInheritFrom = this.templates.getFrame(inheritsText);
				if (toInheritFrom != null) {
					frameDefinition.inheritFrom(toInheritFrom, true);
					frameDefinition.remove("XMLVirtual"); // virtual ness not inherited
				}
				else {
					System.err.println("UNABLE TO FIND FRAME TO INHERIT: " + inheritsText);
				}
			}
		}
		final Node idItem = getAttributesNamedItem(attributes, "id");
		if (idItem != null) {
			final String attribText = getAttributeText(idItem);
			if (attribText != null) {
				frameDefinition.set("ID", new StringFrameDefinitionField(attribText));
			}
		}
		final Node parentItem = getAttributesNamedItem(attributes, "parent");
		if (parentItem != null) {
			final String attribText = getAttributeText(parentItem);
			if (attribText != null) {
				frameDefinition.set("XMLParent", new StringFrameDefinitionField(attribText));
			}
		}
		final Node virtualItem = getAttributesNamedItem(attributes, "virtual");
		if (virtualItem != null) {
			final String attribText = getAttributeText(virtualItem);
			if (attribText != null) {
				if ("true".equals(attribText)) {
					frameDefinition.add("XMLVirtual");
				}
			}
		}
		final Node hiddenItem = getAttributesNamedItem(attributes, "hidden");
		if (hiddenItem != null) {
			final String attribText = getAttributeText(hiddenItem);
			if (attribText != null) {
				if ("true".equals(attribText)) {
					frameDefinition.add("XMLHidden");
				}
			}
		}
		final Node frameStrataItem = getAttributesNamedItem(attributes, "frameStrata");
		if (frameStrataItem != null) {
			final String attribText = getAttributeText(frameStrataItem);
			if (attribText != null) {
				frameDefinition.set("FrameStrata", new StringFrameDefinitionField(attribText));
			}
		}
		final Node setAllPointsItem = getAttributesNamedItem(attributes, "setAllPoints");
		if (setAllPointsItem != null) {
			final String attribText = getAttributeText(setAllPointsItem);
			if ((attribText != null) && "true".equals(attribText.toLowerCase())) {
				frameDefinition.add("SetAllPoints");
			}
		}
		final NodeList childNodes = xmlNode.getChildNodes();
		if (frameClass == null) {
			throw new IllegalStateException("Unknown node name: " + baseXmlNodeName);
		}
		if ("Backdrop".equals(baseXmlNodeName)) {
			final Node bgFileItem = getAttributesNamedItem(attributes, "bgFile");
			if (bgFileItem != null) {
				final String attribText = getAttributeText(bgFileItem);
				if (attribText != null) {
					frameDefinition.set("BackdropBackground", new StringFrameDefinitionField(attribText));
				}
			}
			final Node edgeFileItem = getAttributesNamedItem(attributes, "edgeFile");
			if (edgeFileItem != null) {
				final String attribText = getAttributeText(edgeFileItem);
				if (attribText != null) {
					frameDefinition.set("BackdropEdgeFile", new StringFrameDefinitionField(attribText));
				}
			}
			final Node tileItem = getAttributesNamedItem(attributes, "tile");
			if (tileItem != null) {
				final String attribText = getAttributeText(tileItem);
				if ((attribText != null) && "true".equals(attribText.toLowerCase())) {
					frameDefinition.add("BackdropTileBackground");
				}
			}
		}
		float myFontHeight = 12;
		switch (frameClass) {
		case Frame:
		case Layer:
		case Texture:
			for (int i = 0; i < childNodes.getLength(); i++) {
				final Node item = childNodes.item(i);
				final String nodeName = item.getNodeName();
				switch (nodeName) {
				case "Size":
					final Node absDimensionChild = firstChild(item, "AbsDimension");
					if (absDimensionChild != null) {
						final NamedNodeMap dimensionAttributes = absDimensionChild.getAttributes();
						final Node xAttr = dimensionAttributes.getNamedItem("x");
						if (xAttr != null) {
							frameDefinition.set("Width", new FloatFrameDefinitionField(
									convertXMLCoordX(Float.parseFloat(getAttributeText(xAttr)))));
						}
						final Node yAttr = dimensionAttributes.getNamedItem("y");
						if (yAttr != null) {
							frameDefinition.set("Height", new FloatFrameDefinitionField(
									convertXMLCoordY(Float.parseFloat(getAttributeText(yAttr)))));
						}
					}
					else {
						throw new RuntimeException("Bad size");
					}
					break;
				case "Anchors":
					final NodeList anchorNodes = item.getChildNodes();
					for (int j = 0; j < anchorNodes.getLength(); j++) {
						final Node anchorNode = anchorNodes.item(j);
						if ("Anchor".equals(anchorNode.getNodeName())) {
							final NamedNodeMap anchorAttributes = anchorNode.getAttributes();
							final Node pointAttr = anchorAttributes.getNamedItem("point");
							final Node relativeToAttr = anchorAttributes.getNamedItem("relativeTo");
							final Node relativePointAttr = anchorAttributes.getNamedItem("relativePoint");
							final String pointText = getAttributeText(pointAttr);
							final FramePoint framePoint = FramePoint.valueOf(pointText);
							final NodeList anchorChildNodes = anchorNode.getChildNodes();
							float offsetX = 0;
							float offsetY = 0;
							for (int k = 0; k < anchorChildNodes.getLength(); k++) {
								final Node anchorChild = anchorChildNodes.item(k);
								if (anchorChild.getNodeName().equals("Offset")) {
									final Node absDimension = firstChild(anchorChild, "AbsDimension");
									if (absDimension.getNodeName().equals("AbsDimension")) {
										final NamedNodeMap dimensionAttributes = absDimension.getAttributes();
										final Node xAttr = dimensionAttributes.getNamedItem("x");
										if (xAttr != null) {
											offsetX = convertXMLCoordX(Float.parseFloat(getAttributeText(xAttr)));
										}
										final Node yAttr = dimensionAttributes.getNamedItem("y");
										if (yAttr != null) {
											offsetY = convertXMLCoordY(Float.parseFloat(getAttributeText(yAttr)));
										}
									}
									else {
										throw new RuntimeException("Not AbsDimension:" + absDimension.getNodeName());
									}
								}
							}
							if (relativeToAttr != null) {
								final String relativeToText = getAttributeText(relativeToAttr);
								FramePoint relativePoint = framePoint;
								if (relativePointAttr != null) {
									final String relativePointText = getAttributeText(relativePointAttr);
									relativePoint = FramePoint.valueOf(relativePointText);
								}
								frameDefinition.add(new SetPointDefinition(framePoint, relativeToText, relativePoint,
										offsetX, offsetY));
							}
							else {
								if (relativePointAttr != null) {
									final String relativePointText = getAttributeText(relativePointAttr);
									final FramePoint relativePoint = FramePoint.valueOf(relativePointText);
									frameDefinition
											.add(new AnchorDefinition(framePoint, relativePoint, offsetX, offsetY));
								}
								else {
									frameDefinition.add(new AnchorDefinition(framePoint, offsetX, offsetY));
								}
							}
						}
						else if ("#text".equals(anchorNode.getNodeName())) {
							if (DEBUG_LOG) {
								System.err.println("skipping text in Anchors: " + anchorNode.getTextContent());
							}
						}
						else {
							throw new RuntimeException("Not anchor:" + anchorNode.getNodeName());
						}
					}
					break;
				case "Frames":
				case "Layers": {
					FrameDefinition baseToAddInto;
					if (baseXmlNodeName.equals("EditBox")) {
						final String baseName = "#EditBox" + this.untitledXMLFrameId++;
						baseToAddInto = new FrameDefinition(FrameClass.Frame, "SIMPLEFRAME", baseName);
						baseToAddInto.add("SetAllPoints");
						frameDefinition.set("ControlBackdrop", new StringFrameDefinitionField(baseName));
						frameDefinition.add(baseToAddInto);
					}
					else {
						baseToAddInto = frameDefinition;
					}
					for (final FrameDefinition subFrameDef : inflateMultipleXMLs(currentWorkingDir,
							item.getChildNodes())) {
						if (subFrameDef.getFrameClass() == FrameClass.Scripts) {
							baseToAddInto.setScriptDefinition(subFrameDef);
						}
						else {
							baseToAddInto.add(subFrameDef);
						}
					}
					break;
				}
				case "Scripts":
					frameDefinition.setScriptDefinition(inflateXMLToDef(currentWorkingDir, item));
					break;
				case "Texture":
				case "Frame":
				case "Backdrop":
				case "ScrollChild":
				case "SimpleHTML":
				case "TitleRegion":
				case "EditBox":
				case "Script": {
					final FrameDefinition childDef = inflateXMLToDef(currentWorkingDir, item);
					frameDefinition.add(childDef);
					if ("Backdrop".equals(nodeName) && "Button".equals(baseXmlNodeName)) {
						frameDefinition.set("ControlBackdrop", new StringFrameDefinitionField(childDef.getName()));
					}
					break;
				}
				case "FontString":
					frameDefinition.add(inflateXMLToDef(currentWorkingDir, item));
					break;
				case "BarTexture":
					final Node barTextureFileItem = item.getAttributes().getNamedItem("file");
					frameDefinition.set("BarTexture",
							new StringFrameDefinitionField(getAttributeText(barTextureFileItem)));
					break;
				case "BarColor":
					frameDefinition.set("BarColor", parseColorAttributes(item.getAttributes()));
					break;
				case "#text":
					if (DEBUG_LOG) {
						System.err.println("skipping #text: " + item.getTextContent());
					}
					break;
				case "Color":
					final NamedNodeMap colorAttributes = item.getAttributes();
					String colorKey = "Color";
					if ("FontString".equals(baseXmlNodeName)) {
						colorKey = "FontColor";
					}
					frameDefinition.set(colorKey, parseColorAttributes(colorAttributes));
					break;
				case "FontHeight":
					final NamedNodeMap fontHeightAttributes = firstChild(item, "AbsValue").getAttributes();
					myFontHeight = convertXMLCoordY(
							Float.parseFloat(fontHeightAttributes.getNamedItem("val").getNodeValue()));
					frameDefinition.set("FontHeight", new FloatFrameDefinitionField(myFontHeight));
					break;
//					final Float cornerSizeNullable = frameDefinition.getFloat("BackdropCornerSize");
//					final Float backgroundSizeNullable = frameDefinition.getFloat("BackdropBackgroundSize");
//					Vector4Definition backgroundInsets = frameDefinition.getVector4("BackdropBackgroundInsets");
				case "EdgeSize": {
					final NamedNodeMap valueAttributes = firstChild(item, "AbsValue").getAttributes();
					frameDefinition.set("BackdropCornerSize", new FloatFrameDefinitionField(
							convertXMLCoordX(Float.parseFloat(valueAttributes.getNamedItem("val").getNodeValue()))));
					break;
				}
				case "TileSize": {
					final NamedNodeMap valueAttributes = firstChild(item, "AbsValue").getAttributes();
					frameDefinition.set("BackdropBackgroundSize", new FloatFrameDefinitionField(
							convertXMLCoordX(Float.parseFloat(valueAttributes.getNamedItem("val").getNodeValue()))));
					break;
				}
				case "BackgroundInsets": {
					final NamedNodeMap valueAttributes = firstChild(item, "AbsInset").getAttributes();
					frameDefinition.set("BackdropBackgroundInsets",
							new Vector4FrameDefinitionField(new Vector4Definition(
									convertXMLCoordX(
											Float.parseFloat(valueAttributes.getNamedItem("left").getNodeValue())),
									convertXMLCoordX(
											Float.parseFloat(valueAttributes.getNamedItem("right").getNodeValue())),
									convertXMLCoordY(
											Float.parseFloat(valueAttributes.getNamedItem("top").getNodeValue())),
									convertXMLCoordY(
											Float.parseFloat(valueAttributes.getNamedItem("bottom").getNodeValue())))));
					break;
				}
				case "NormalTexture": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item, "Texture");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("ControlBackdrop", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "PushedTexture": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item, "Texture");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("ControlPushedBackdrop", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "CheckedTexture": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item, "Texture");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("CheckBoxCheckHighlight", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "DisabledCheckedTexture": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item, "Texture");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("CheckBoxDisabledCheckHighlight",
							new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "DisabledTexture": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item, "Texture");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("ControlDisabledBackdrop", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "HighlightTexture": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item, "Texture");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("ControlMouseOverHighlight", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "ThumbTexture": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item, "Texture");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("SliderThumbButtonFrame", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "NormalText": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item,
							"FontString");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					frameDefinition.set("ButtonText", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "HighlightText": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item,
							"FontString");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					// TODO ButtonTextHighlight is bogus, made up, not going to work
					frameDefinition.set("ButtonTextHighlight", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "DisabledText": {
					final FrameDefinition normalTextureDefinition = inflateXMLToDef(currentWorkingDir, item,
							"FontString");
					final String normalTextureName = normalTextureDefinition.getName();
					if (normalTextureName == null) {
						throw new IllegalStateException();
					}
					frameDefinition.add(normalTextureDefinition);
					// TODO ButtonTextDisabled is bogus, made up, not going to work
					frameDefinition.set("ButtonTextDisabled", new StringFrameDefinitionField(normalTextureName));
					break;
				}
				case "TexCoords":
					final NamedNodeMap texCoordsAttributes = item.getAttributes();
					frameDefinition.set("TexCoord",
							new Vector4FrameDefinitionField(new Vector4Definition(
									Float.parseFloat(texCoordsAttributes.getNamedItem("left").getNodeValue()),
									Float.parseFloat(texCoordsAttributes.getNamedItem("right").getNodeValue()),
									Float.parseFloat(texCoordsAttributes.getNamedItem("top").getNodeValue()),
									Float.parseFloat(texCoordsAttributes.getNamedItem("bottom").getNodeValue()))));
					break;
				case "Shadow":
					// FontShadowColor
					// FontShadowOffset
//					<Offset>
//						<AbsDimension x="1" y="-1"/>
//					</Offset>
//					<Color r="0" g="0" b="0"/>
					final NodeList shadowChildren = item.getChildNodes();
					for (int k = 0; k < shadowChildren.getLength(); k++) {
						final Node shadowChild = shadowChildren.item(k);
						if (shadowChild.getNodeName().startsWith("Color")) {
							frameDefinition.set("FontShadowColor", parseColorAttributes(shadowChild.getAttributes()));
						}
						else if (shadowChild.getNodeName().equals("Offset")) {
							frameDefinition.set("FontShadowOffset", new Vector2FrameDefinitionField(
									parseAbsDimension(firstChild(shadowChild, "AbsDimension"))));
						}
						else if (shadowChild.getNodeName().equals("#text")) {
							// ignore
							if (DEBUG_LOG) {
								System.err.println("Skipping shadow child text: " + shadowChild);
							}
						}
						else {
							throw new IllegalArgumentException(shadowChild.getNodeName());
						}
					}
					break;
				case "PushedTextOffset":
				case "HitRectInsets":
					// TODO i am currently ignoring this
					break;
				default:
					throw new IllegalArgumentException(nodeName);
				}
			}
			break;
		default:
			throw new IllegalArgumentException(String.valueOf(frameClass));
		}
		if (frameClass == FrameClass.Texture) {
			{
				final Node textItem = getAttributesNamedItem(attributes, "file");
				if (textItem != null) {
					final String attribText = getAttributeText(textItem);
					if (attribText != null) {
						frameDefinition.set("File", new StringFrameDefinitionField(attribText));
					}
				}
			}
			{
				final Node textItem = getAttributesNamedItem(attributes, "alphaMode");
				if (textItem != null) {
					final String attribText = getAttributeText(textItem);
					if (attribText != null) {
						frameDefinition.set("AlphaMode", new StringFrameDefinitionField(attribText));
					}
				}
			}
		}
		else if (frameClass == FrameClass.Layer) {
			{
				final Node textItem = getAttributesNamedItem(attributes, "level");
				if (textItem != null) {
					final String attribText = getAttributeText(textItem);
					if (attribText != null) {
						frameDefinition.set("XMLLayerLevel", new StringFrameDefinitionField(attribText));
					}
				}
			}
		}
		if ("FontString".equals(baseXmlNodeName)) {
			final Node fontItem = getAttributesNamedItem(attributes, "font");
			if (fontItem != null) {
				final String attribText = getAttributeText(fontItem);
				if (attribText != null) {
					frameDefinition.set("FrameFont",
							new FontFrameDefinitionField(new FontDefinition(attribText, myFontHeight, null)));
				}
			}
			final Node textItem = getAttributesNamedItem(attributes, "text");
			if (textItem != null) {
				final String attribText = getAttributeText(textItem);
				if (attribText != null) {
					frameDefinition.set("Text", new StringFrameDefinitionField(attribText));
				}
			}
			final Node justifyHItem = getAttributesNamedItem(attributes, "justifyH");
			if (justifyHItem != null) {
				final String attribText = getAttributeText(justifyHItem);
				if (attribText != null) {
					frameDefinition.set("FontJustificationH",
							new TextJustifyFrameDefinitionField(TextJustify.valueOf(attribText.toUpperCase())));
				}
			}
			final Node justifyVItem = getAttributesNamedItem(attributes, "justifyV");
			if (justifyVItem != null) {
				final String attribText = getAttributeText(justifyVItem);
				if (attribText != null) {
					frameDefinition.set("FontJustificationV",
							new TextJustifyFrameDefinitionField(TextJustify.valueOf(attribText.toUpperCase())));
				}
			}
		}
		else if ("Model".equals(baseXmlNodeName)) {
			final Node textItem = getAttributesNamedItem(attributes, "file");
			if (textItem != null) {
				final String attribText = getAttributeText(textItem);
				if (attribText != null) {
					frameDefinition.set("BackgroundArt", new StringFrameDefinitionField(attribText));
				}
			}
		}
		else if ("Slider".equals(baseXmlNodeName)) {
			final Node orientationItem = getAttributesNamedItem(attributes, "orientation");
			if (orientationItem != null) {
				final String attribText = getAttributeText(orientationItem);
				if (attribText != null) {
					if ("HORIZONTAL".equals(attribText)) {
						frameDefinition.add("SliderLayoutHorizontal");
					}
					else {
						frameDefinition.add("SliderLayoutVertical");
					}
				}
			}
		}
		else if ("EditBox".equals(baseXmlNodeName)) {
			for (final FrameDefinition innerFrame : frameDefinition.getInnerFrames()) {
				if ("TEXT".equals(innerFrame.getFrameType())) {
					frameDefinition.set("EditTextFrame", new StringFrameDefinitionField(innerFrame.getName()));
				}
			}
			frameDefinition.set("EditBorderSize", new FloatFrameDefinitionField(0));
		}
		if (frameDefinition != null) {
			frameDefinition.sortChildren();
			this.templates.put(name, frameDefinition);
		}
		return frameDefinition;
	}

	public void loadLuaFile(String fileName) {
		fileName = fileName.replace('/', '\\');
		if (this.dataSource.has(fileName)) {
			final StringBuilder fileContents = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(this.dataSource.getResourceAsStream(fileName)))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains("for ")) {
						System.out.println(line);
					}
					final String trimmedLine = line.trim();
					if (trimmedLine.startsWith("for ") && trimmedLine.endsWith(" do")) {
						final int inIndex = line.indexOf(IN_STRING);
						if (inIndex != -1) {
							final int replaceStart = inIndex + IN_STRING.length();
							final int replaceEnd = line.lastIndexOf(" do");
							final String toReplace = line.substring(replaceStart, replaceEnd);
							line = line.substring(0, replaceStart) + "ipairs(" + toReplace + ")"
									+ line.substring(replaceEnd);
						}
					}
					fileContents.append(line);
					fileContents.append('\n');
				}
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			this.luaGlobals.runLua(new StringReader(fileContents.toString()), fileName);
		}
	}

	private List<FrameDefinition> inflateMultipleXMLs(final String currentWorkingDirectory, final NodeList frameNodes) {
		final List<FrameDefinition> results = new ArrayList<>();
		for (int j = 0; j < frameNodes.getLength(); j++) {
			final Node frameNode = frameNodes.item(j);
			final FrameDefinition subFrameDef = inflateXMLToDef(currentWorkingDirectory, frameNode);
			if (subFrameDef != null) {
				results.add(subFrameDef);
			}
		}
		return results;
	}

	private static Node getAttributesNamedItem(final NamedNodeMap attributes, final String attr) {
		if (attributes == null) {
			return null;
		}
		return attributes.getNamedItem(attr);
	}

	private static Node firstChild(final Node node, final String type) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final Node item = childNodes.item(i);
			if (item.getNodeName().equals(type)) {
				return item;
			}
		}
		return null;
	}

	public FrameDefinitionField parseColorAttributes(final NamedNodeMap colorAttributes) {
		float r = 0, g = 0, b = 0, a = 1;
		final Node rAttr = colorAttributes.getNamedItem("r");
		if (rAttr != null) {
			r = convertXMLCoordX(Float.parseFloat(getAttributeText(rAttr)));
		}
		final Node gAttr = colorAttributes.getNamedItem("g");
		if (gAttr != null) {
			g = convertXMLCoordX(Float.parseFloat(getAttributeText(gAttr)));
		}
		final Node bAttr = colorAttributes.getNamedItem("b");
		if (bAttr != null) {
			b = convertXMLCoordX(Float.parseFloat(getAttributeText(bAttr)));
		}
		final Node aAttr = colorAttributes.getNamedItem("a");
		if (aAttr != null) {
			a = convertXMLCoordX(Float.parseFloat(getAttributeText(aAttr)));
			final Vector4Definition colorDefinition = new Vector4Definition(r, g, b, a);
			return new Vector4FrameDefinitionField(colorDefinition);
		}
		else {
			final Vector3Definition colorDefinition = new Vector3Definition(r, g, b);
			return new Vector3FrameDefinitionField(colorDefinition);
		}
	}

	private static Vector2Definition parseAbsDimension(final Node absDimensionChild) {
		float x = 0;
		float y = 0;
		if (absDimensionChild.getNodeName().equals("AbsDimension")) {
			final NamedNodeMap dimensionAttributes = absDimensionChild.getAttributes();
			final Node xAttr = dimensionAttributes.getNamedItem("x");
			if (xAttr != null) {
				x = convertXMLCoordX(Float.parseFloat(getAttributeText(xAttr)));
			}
			final Node yAttr = dimensionAttributes.getNamedItem("y");
			if (yAttr != null) {
				y = convertXMLCoordY(Float.parseFloat(getAttributeText(yAttr)));
			}
		}
		else {
			throw new RuntimeException("Not AbsDimension:" + absDimensionChild.getNodeName());
		}
		return new Vector2Definition(x, y);
	}

	public static float convertXMLCoordY(final float xmlY) {
		return (xmlY / 768) * 0.6f;
	}

	public static float convertXMLCoordX(final float xmlX) {
		return (xmlX / 1024) * 0.8f;
	}

	public static float unconvertXMLCoordY(final float xmlY) {
		return (xmlY * 768) / 0.6f;
	}

	public static float unconvertXMLCoordX(final float xmlX) {
		return (xmlX * 1024) / 0.8f;
	}

	private static String getAttributeText(final Node nameAttribute) {
		return nameAttribute.getNodeValue();
	}

	private UIFrameScripts inflateScriptBindings(final UIFrame thisFrame, final FrameDefinition scriptFrameDefinition) {
		final UIFrameScripts scripts = new UIFrameScripts();
		scripts.inflate(this.luaGlobals, scriptFrameDefinition, thisFrame);
		thisFrame.setScripts(scripts);
		return scripts;
	}

	public UIFrame inflate(final FrameDefinition frameDefinition, UIFrame parent,
			final FrameDefinition parentDefinitionIfAvailable, final boolean inDecorateFileNames) {
		final String xmlParent = getDefString(frameDefinition, parent, "XMLParent");
		if (xmlParent != null) {
			final UIFrame parentOverride = this.nameToFrame.get(xmlParent);
			if (parentOverride != null) {
				parent = parentOverride;
			}
		}
		UIFrame inflatedFrame = null;
		BitmapFont frameFont = null;
		Viewport viewport2 = this.viewport;
		final String frameDefinitionName = checkNameString(parent, frameDefinition.getName());
		switch (frameDefinition.getFrameClass()) {
		case Frame:
			if ("SIMPLEFRAME".equals(frameDefinition.getFrameType())) {
				final SimpleFrame simpleFrame = new SimpleFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, simpleFrame);
				inflateScriptsIfAvailable(frameDefinition, simpleFrame);
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					simpleFrame.add(inflate(childDefinition, simpleFrame, frameDefinition,
							inDecorateFileNames || childDefinition.has("DecorateFileNames")));
				}
				inflatedFrame = simpleFrame;
			}
			else if ("SIMPLESTATUSBAR".equals(frameDefinition.getFrameType())) {
				final boolean decorateFileNames = frameDefinition.has("DecorateFileNames")
						|| ((parentDefinitionIfAvailable != null)
								&& parentDefinitionIfAvailable.has("DecorateFileNames"));
				final SimpleStatusBarFrame simpleStatusBarFrame = new SimpleStatusBarFrame(frameDefinitionName, parent,
						decorateFileNames, false, 0.0f);
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					simpleStatusBarFrame.add(inflate(childDefinition, simpleStatusBarFrame, frameDefinition,
							inDecorateFileNames || childDefinition.has("DecorateFileNames")));
				}
				final String barTexture = getDefString(frameDefinition, parent, "BarTexture");
				if (barTexture != null) {
					simpleStatusBarFrame.getBarFrame().setTexture(barTexture, this);
					simpleStatusBarFrame.getBorderFrame().setTexture(barTexture + "Border", this);
				}
				inflatedFrame = simpleStatusBarFrame;
				inflateScriptsIfAvailable(frameDefinition, inflatedFrame);
			}
			else if ("SCROLLBAR".equals(frameDefinition.getFrameType())
					|| "SLIDER".equals(frameDefinition.getFrameType())) {
				final boolean vertical = frameDefinition.has("SliderLayoutVertical");
				final boolean horizontal = frameDefinition.has("SliderLayoutHorizontal");

				final boolean decorateFileNames = frameDefinition.has("DecorateFileNames")
						|| ((parentDefinitionIfAvailable != null)
								&& parentDefinitionIfAvailable.has("DecorateFileNames"));
				final ScrollBarFrame scrollBarFrame = new ScrollBarFrame(frameDefinitionName, parent, vertical);
				inflateScriptsIfAvailable(frameDefinition, scrollBarFrame);

				final Float sliderMinValue = frameDefinition.getFloat("SliderMinValue");
				if (sliderMinValue != null) {
					scrollBarFrame.setMinValue(sliderMinValue.intValue());
				}
				final Float sliderMaxValue = frameDefinition.getFloat("SliderMaxValue");
				if (sliderMaxValue != null) {
					scrollBarFrame.setMaxValue(sliderMaxValue.intValue());
				}
				final Float sliderStepSize = frameDefinition.getFloat("SliderStepSize");
				if (sliderStepSize != null) {
					scrollBarFrame.setStepSize(sliderStepSize.intValue());
				}

				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String incButtonFrameKey = getDefString(frameDefinition, parent, "ScrollBarIncButtonFrame");
				final String decButtonFrameKey = getDefString(frameDefinition, parent, "ScrollBarDecButtonFrame");
				final String thumbButtonFrameKey = getDefString(frameDefinition, parent, "SliderThumbButtonFrame");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, scrollBarFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						scrollBarFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(incButtonFrameKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, scrollBarFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.addSetPoint(new SetPoint(FramePoint.TOP, scrollBarFrame, FramePoint.TOP, 0, 0));
						scrollBarFrame.setIncButtonFrame(inflatedChild);
					}
					else if (childDefinition.getName().equals(decButtonFrameKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, scrollBarFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild
								.addSetPoint(new SetPoint(FramePoint.BOTTOM, scrollBarFrame, FramePoint.BOTTOM, 0, 0));
						scrollBarFrame.setDecButtonFrame(inflatedChild);
					}
					else if (childDefinition.getName().equals(thumbButtonFrameKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, scrollBarFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						scrollBarFrame.setThumbButtonFrame(inflatedChild);
						scrollBarFrame.setValue(this, viewport2, 50);
					}
				}
				final Float sliderInitialValue = frameDefinition.getFloat("SliderInitialValue");
				if (sliderInitialValue != null) {
					scrollBarFrame.setValue(this, viewport2, sliderInitialValue.intValue());
				}
				inflatedFrame = scrollBarFrame;
			}
			else if ("SPRITE".equals(frameDefinition.getFrameType())) {
				if (frameDefinition.getName().endsWith("Portrait")) {
					final SpriteFrame2 spriteFrame = new SpriteFrame2(frameDefinition.getName(), parent, viewport2,
							this.modelViewer);
					inflateScriptsIfAvailable(frameDefinition, spriteFrame);
					String backgroundArt = frameDefinition.getString("BackgroundArt");
					if (frameDefinition.has("DecorateFileNames") || inDecorateFileNames) {
						if (backgroundArt != null) {
							if (this.skin.hasField(backgroundArt)) {
								backgroundArt = this.skin.getField(backgroundArt);
							}
						}
					}
					if (backgroundArt != null) {
						setSpriteFrameModel(spriteFrame, backgroundArt);
					}
					viewport2 = this.viewport; // TODO was fdfCoordinateResolutionDummyViewport here previously, but is
												// that
					// a good idea?
					this.nameToFrame.put(frameDefinition.getName(), spriteFrame);
					for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
						spriteFrame.add(inflate(childDefinition, spriteFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames")));
					}
					inflatedFrame = spriteFrame;
				}
				else {
					final SpriteFrame spriteFrame = new SpriteFrame(frameDefinition.getName(), parent, this.uiScene,
							viewport2);
					inflateScriptsIfAvailable(frameDefinition, spriteFrame);
					String backgroundArt = frameDefinition.getString("BackgroundArt");
					if (frameDefinition.has("DecorateFileNames") || inDecorateFileNames) {
						if (backgroundArt != null) {
							if (this.skin.hasField(backgroundArt)) {
								backgroundArt = this.skin.getField(backgroundArt);
							}
						}
					}
					if (backgroundArt != null) {
						setSpriteFrameModel(spriteFrame, backgroundArt);
					}
					viewport2 = this.viewport; // TODO was fdfCoordinateResolutionDummyViewport here previously, but is
												// that
					// a good idea?
					this.nameToFrame.put(frameDefinition.getName(), spriteFrame);
					for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
						spriteFrame.add(inflate(childDefinition, spriteFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames")));
					}
					inflatedFrame = spriteFrame;
				}
			}
			else if ("FRAME".equals(frameDefinition.getFrameType())) {
				final SimpleFrame simpleFrame = new SimpleFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, simpleFrame);
				inflateScriptsIfAvailable(frameDefinition, simpleFrame);
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					simpleFrame.add(inflate(childDefinition, simpleFrame, frameDefinition,
							inDecorateFileNames || childDefinition.has("DecorateFileNames")));
				}
				inflatedFrame = simpleFrame;
			}
			else if ("DIALOG".equals(frameDefinition.getFrameType())) {
				final SimpleFrame simpleFrame = new SimpleFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				final String dialogBackdropKey = getDefString(frameDefinition, parent, "DialogBackdrop");
				this.nameToFrame.put(frameDefinitionName, simpleFrame);
				inflateScriptsIfAvailable(frameDefinition, simpleFrame);

				if (SHOW_BLACKNESS_BEHIND_DIALOGS) {
					final TextureFrame modalDialogBlacknessScreenCover = new ClickConsumingTextureFrame(null, parent,
							false, null);
					modalDialogBlacknessScreenCover.setTexture("Textures\\Black32.blp", this);
					modalDialogBlacknessScreenCover.setColor(1.0f, 1.0f, 1.0f, 0.5f);
					modalDialogBlacknessScreenCover.setSetAllPoints(true);
					simpleFrame.add(modalDialogBlacknessScreenCover);
				}
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(dialogBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, simpleFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						simpleFrame.add(inflatedChild);
					}
					else {
						simpleFrame.add(inflate(childDefinition, simpleFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames")));
					}
				}
				inflatedFrame = simpleFrame;
			}
			else if ("TEXT".equals(frameDefinition.getFrameType())) {
				final Float textLength = frameDefinition.getFloat("TextLength");
				TextJustify justifyH = frameDefinition.getTextJustify("FontJustificationH");
				if (justifyH == null) {
					justifyH = TextJustify.LEFT;
				}
				TextJustify justifyV = frameDefinition.getTextJustify("FontJustificationV");
				if (justifyV == null) {
					justifyV = TextJustify.MIDDLE;
				}
				for (final SetPointDefinition setPoint : frameDefinition.getSetPoints()) {
					if (((setPoint.getMyPoint() == FramePoint.TOP) && (setPoint.getOtherPoint() == FramePoint.TOP))
							|| ((setPoint.getMyPoint() == FramePoint.BOTTOM)
									&& (setPoint.getOtherPoint() == FramePoint.BOTTOM))) {
						justifyH = TextJustify.CENTER;
					}
				}

				String fontFlagsString = frameDefinition.getString("FontFlags");
				if (fontFlagsString == null) {
					fontFlagsString = "";
				}
				final EnumSet<FontFlags> fontFlags = FontFlags.parseFontFlags(fontFlagsString);

				Color fontColor;
				final Vector4Definition fontColorDefinition = frameDefinition.getVector4("FontColor");
				if (fontColorDefinition == null) {
					fontColor = Color.WHITE;
				}
				else {
					fontColor = new Color(fontColorDefinition.getX(), fontColorDefinition.getY(),
							fontColorDefinition.getZ(), fontColorDefinition.getW());
				}

				Color fontHighlightColor;
				final Vector4Definition fontHighlightColorDefinition = frameDefinition.getVector4("FontHighlightColor");
				if (fontHighlightColorDefinition == null) {
					fontHighlightColor = null;
				}
				else {
					fontHighlightColor = new Color(fontHighlightColorDefinition.getX(),
							fontHighlightColorDefinition.getY(), fontHighlightColorDefinition.getZ(),
							fontHighlightColorDefinition.getW());
				}

				Color fontDisabledColor;
				final Vector4Definition fontDisabledColorDefinition = frameDefinition.getVector4("FontDisabledColor");
				if (fontDisabledColorDefinition == null) {
					fontDisabledColor = null;
				}
				else {
					fontDisabledColor = new Color(fontDisabledColorDefinition.getX(),
							fontDisabledColorDefinition.getY(), fontDisabledColorDefinition.getZ(),
							fontDisabledColorDefinition.getW());
				}

				Color fontShadowColor;
				final Vector4Definition fontShadowColorDefinition = frameDefinition.getVector4("FontShadowColor");
				if (fontShadowColorDefinition == null) {
					fontShadowColor = null;
				}
				else {
					fontShadowColor = new Color(fontShadowColorDefinition.getX(), fontShadowColorDefinition.getY(),
							fontShadowColorDefinition.getZ(), fontShadowColorDefinition.getW());
				}
				FontDefinition font = frameDefinition.getFont("FrameFont");
				if ((font == null) && (parentDefinitionIfAvailable != null)) {
					font = parentDefinitionIfAvailable.getFont("FrameFont");
				}
				final Float height = frameDefinition.getFloat("Height");
				this.fontParam.size = (int) convertY(viewport2,
						(font == null ? (height == null ? 0.06f : height) : font.getFontSize()));
				if (this.fontParam.size == 0) {
					this.fontParam.size = 24;
				}
				if (font == null) {
					frameFont = this.font;
				}
				else {
					frameFont = this.dynamicFontGeneratorHolder.getFontGenerator(font.getFontName())
							.generateFont(this.fontParam);
				}
				String textString = frameDefinitionName.startsWith("#") ? "" : frameDefinitionName;
				String text = getDefString(frameDefinition, parent, "Text");
				if (text != null) {
					final String decoratedString = this.templates.getDecoratedString(text);
					if (decoratedString != text) {
						text = decoratedString;
					}
					textString = text;
				}
				final StringFrame stringFrame = new StringFrame(frameDefinitionName, parent, fontColor, justifyH,
						justifyV, frameFont, textString, fontHighlightColor, fontDisabledColor);
				if (fontFlags.contains(FontFlags.PASSWORDFIELD)) {
					stringFrame.setPasswordField(true);
				}
				if (fontShadowColor != null) {
					final Vector2Definition shadowOffset = frameDefinition.getVector2("FontShadowOffset");
					stringFrame.setFontShadowColor(fontShadowColor);
					stringFrame.setFontShadowOffsetX(convertX(viewport2, shadowOffset.getX()));
					stringFrame.setFontShadowOffsetY(convertY(viewport2, shadowOffset.getY()));
				}
				inflatedFrame = stringFrame;
				inflateScriptsIfAvailable(frameDefinition, inflatedFrame);
			}
			else if ("GLUETEXTBUTTON".equals(frameDefinition.getFrameType())) {
				// ButtonText & ControlBackdrop
				final GlueTextButtonFrame glueButtonFrame = new GlueTextButtonFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, glueButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, glueButtonFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String controlPushedBackdropKey = getDefString(frameDefinition, parent, "ControlPushedBackdrop");
				final String controlDisabledBackdropKey = getDefString(frameDefinition, parent,
						"ControlDisabledBackdrop");
				final String controlMouseOverHighlightKey = getDefString(frameDefinition, parent,
						"ControlMouseOverHighlight");
				final String buttonTextKey = getDefString(frameDefinition, parent, "ButtonText");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlPushedBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlPushedBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlDisabledBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlDisabledBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlMouseOverHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlMouseOverHighlight(inflatedChild);
					}
					else if (childDefinition.getName().equals(buttonTextKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setButtonText(inflatedChild);
					}
				}
				final String controlStyleString = frameDefinition.getString("ControlStyle");
				if (controlStyleString != null) {
					final EnumSet<ControlStyle> controlStyle = ControlStyle.parseControlStyle(controlStyleString);
					if (controlStyle.contains(ControlStyle.AUTOTRACK)
							&& controlStyle.contains(ControlStyle.HIGHLIGHTONMOUSEOVER)) {
						glueButtonFrame.setHighlightOnMouseOver(true);
					}
				}
				inflatedFrame = glueButtonFrame;
			}
			else if ("XMLBUTTON".equals(frameDefinition.getFrameType())) {
				// ButtonText & ControlBackdrop
				final XmlButtonFrame glueButtonFrame = new XmlButtonFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, glueButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, glueButtonFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String controlPushedBackdropKey = getDefString(frameDefinition, parent, "ControlPushedBackdrop");
				final String controlDisabledBackdropKey = getDefString(frameDefinition, parent,
						"ControlDisabledBackdrop");
				final String controlMouseOverHighlightKey = getDefString(frameDefinition, parent,
						"ControlMouseOverHighlight");
				final String buttonTextKey = getDefString(frameDefinition, parent, "ButtonText");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlPushedBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlPushedBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlDisabledBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlDisabledBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlMouseOverHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlMouseOverHighlight(inflatedChild);
					}
					else if (childDefinition.getName().equals(buttonTextKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setButtonText(inflatedChild);
					}
					else {
						glueButtonFrame.add(inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames")));
					}
				}
				final String controlStyleString = frameDefinition.getString("ControlStyle");
				if (controlStyleString != null) {
					final EnumSet<ControlStyle> controlStyle = ControlStyle.parseControlStyle(controlStyleString);
					if (controlStyle.contains(ControlStyle.AUTOTRACK)
							&& controlStyle.contains(ControlStyle.HIGHLIGHTONMOUSEOVER)) {
						glueButtonFrame.setHighlightOnMouseOver(true);
					}
				}
				inflatedFrame = glueButtonFrame;
			}
			else if ("POPUPMENU".equals(frameDefinition.getFrameType())) {
				// ButtonText & ControlBackdrop
				final PopupMenuFrame glueButtonFrame = new PopupMenuFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, glueButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, glueButtonFrame);

				final Float popupButtonInset = frameDefinition.getFloat("PopupButtonInset");
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String controlPushedBackdropKey = getDefString(frameDefinition, parent, "ControlPushedBackdrop");
				final String controlDisabledBackdropKey = getDefString(frameDefinition, parent,
						"ControlDisabledBackdrop");
				final String controlMouseOverHighlightKey = getDefString(frameDefinition, parent,
						"ControlMouseOverHighlight");
				final String buttonTextKey = getDefString(frameDefinition, parent, "PopupTitleFrame");
				final String popupArrowFrameKey = getDefString(frameDefinition, parent, "PopupArrowFrame");
				final String popupMenuFrameKey = getDefString(frameDefinition, parent, "PopupMenuFrame");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlPushedBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlPushedBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlDisabledBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlDisabledBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlMouseOverHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlMouseOverHighlight(inflatedChild);
					}
					else if (childDefinition.getName().equals(buttonTextKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.addSetPoint(new SetPoint(FramePoint.LEFT, glueButtonFrame, FramePoint.LEFT,
								convertX(viewport2, popupButtonInset), 0));
						glueButtonFrame.setButtonText(inflatedChild);
					}
					else if (childDefinition.getName().equals(popupArrowFrameKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.addSetPoint(new SetPoint(FramePoint.RIGHT, glueButtonFrame, FramePoint.RIGHT,
								convertX(viewport2, -popupButtonInset), 0));
						glueButtonFrame.setPopupArrowFrame(inflatedChild);
					}
					else if (childDefinition.getName().equals(popupMenuFrameKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						add(inflatedChild);
						inflatedChild.addSetPoint(
								new SetPoint(FramePoint.TOPLEFT, glueButtonFrame, FramePoint.BOTTOMLEFT, 0, 0));
						inflatedChild.addSetPoint(
								new SetPoint(FramePoint.TOPRIGHT, glueButtonFrame, FramePoint.BOTTOMRIGHT, 0, 0));
						if (inflatedChild instanceof MenuFrame) {
							((MenuFrame) inflatedChild).setOnClick(new MenuClickListener() {
								@Override
								public void onClick(final int button, final int menuItemIndex) {
									glueButtonFrame.onClickItem(button, menuItemIndex);
								}
							});
						}
						glueButtonFrame.setPopupMenuFrame(inflatedChild);
					}
				}
				final String controlStyleString = frameDefinition.getString("ControlStyle");
				if (controlStyleString != null) {
					final EnumSet<ControlStyle> controlStyle = ControlStyle.parseControlStyle(controlStyleString);
					if (controlStyle.contains(ControlStyle.AUTOTRACK)
							&& controlStyle.contains(ControlStyle.HIGHLIGHTONMOUSEOVER)) {
						glueButtonFrame.setHighlightOnMouseOver(true);
					}
				}
				inflatedFrame = glueButtonFrame;
			}
			else if ("SIMPLEBUTTON".equals(frameDefinition.getFrameType())) {
				// ButtonText & ControlBackdrop
				final SimpleButtonFrame simpleButtonFrame = new SimpleButtonFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, simpleButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, simpleButtonFrame);
				final StringPairFrameDefinitionField normalTextDefinition = frameDefinition.getStringPair("NormalText");
				final StringPairFrameDefinitionField disabledTextDefinition = frameDefinition
						.getStringPair("DisabledText");
				final StringPairFrameDefinitionField highlightTextDefinition = frameDefinition
						.getStringPair("HighlightText");
				final String normalTextureDefinition = getDefString(frameDefinition, parent, "NormalTexture");
				final String pushedTextureDefinition = getDefString(frameDefinition, parent, "PushedTexture");
				final String disabledTextureDefinition = getDefString(frameDefinition, parent, "DisabledTexture");
				final String useHighlightDefinition = getDefString(frameDefinition, parent, "UseHighlight");

				final boolean decorateFileNamesOnThisFrame = frameDefinition.has("DecorateFileNames")
						|| inDecorateFileNames;
				final UIFrame normalText = inflate(this.templates.getFrame(normalTextDefinition.getFirst()),
						simpleButtonFrame, frameDefinition, decorateFileNamesOnThisFrame);
				setDecoratedText((StringFrame) normalText, normalTextDefinition.getSecond());
				normalText.setSetAllPoints(true);
				final UIFrame disabledText = inflate(this.templates.getFrame(disabledTextDefinition.getFirst()),
						simpleButtonFrame, frameDefinition, decorateFileNamesOnThisFrame);
				setDecoratedText((StringFrame) disabledText, disabledTextDefinition.getSecond());
				disabledText.setSetAllPoints(true);
				final UIFrame highlightText = inflate(this.templates.getFrame(highlightTextDefinition.getFirst()),
						simpleButtonFrame, frameDefinition, decorateFileNamesOnThisFrame);
				setDecoratedText((StringFrame) highlightText, highlightTextDefinition.getSecond());
				highlightText.setSetAllPoints(true);
				final UIFrame normalTexture = inflate(this.templates.getFrame(normalTextureDefinition),
						simpleButtonFrame, frameDefinition, decorateFileNamesOnThisFrame);
				normalTexture.setSetAllPoints(true);
				final UIFrame pushedTexture = inflate(this.templates.getFrame(pushedTextureDefinition),
						simpleButtonFrame, frameDefinition, decorateFileNamesOnThisFrame);
				pushedTexture.setSetAllPoints(true);
				final UIFrame disabledTexture = inflate(this.templates.getFrame(disabledTextureDefinition),
						simpleButtonFrame, frameDefinition, decorateFileNamesOnThisFrame);
				disabledTexture.setSetAllPoints(true);
				final UIFrame useHighlight = inflate(this.templates.getFrame(useHighlightDefinition), simpleButtonFrame,
						frameDefinition, decorateFileNamesOnThisFrame);
				useHighlight.setSetAllPoints(true);
				simpleButtonFrame.setButtonText(normalText);
				simpleButtonFrame.setDisabledText(disabledText);
				simpleButtonFrame.setHighlightText(highlightText);
				simpleButtonFrame.setControlBackdrop(normalTexture);
				simpleButtonFrame.setControlDisabledBackdrop(disabledTexture);
				simpleButtonFrame.setControlMouseOverHighlight(useHighlight);
				simpleButtonFrame.setControlPushedBackdrop(pushedTexture);

				final Vector2Definition pushedTextOffset = frameDefinition.getVector2("ButtonPushedTextOffset");
				if (pushedTextOffset != null) {
					final UIFrame pushedNormalText = inflate(this.templates.getFrame(normalTextDefinition.getFirst()),
							simpleButtonFrame, frameDefinition, decorateFileNamesOnThisFrame);
					setDecoratedText((StringFrame) pushedNormalText, normalTextDefinition.getSecond());
					final UIFrame pushedHighlightText = inflate(
							this.templates.getFrame(highlightTextDefinition.getFirst()), simpleButtonFrame,
							frameDefinition, decorateFileNamesOnThisFrame);
					setDecoratedText((StringFrame) pushedHighlightText, highlightTextDefinition.getSecond());
					pushedNormalText.addSetPoint(new SetPoint(FramePoint.TOPLEFT, simpleButtonFrame, FramePoint.TOPLEFT,
							GameUI.convertX(viewport2, pushedTextOffset.getX()),
							GameUI.convertY(viewport2, pushedTextOffset.getY())));
					pushedNormalText.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, simpleButtonFrame,
							FramePoint.BOTTOMRIGHT, GameUI.convertX(viewport2, pushedTextOffset.getX()),
							GameUI.convertY(viewport2, pushedTextOffset.getY())));
					pushedHighlightText.addSetPoint(new SetPoint(FramePoint.TOPLEFT, simpleButtonFrame,
							FramePoint.TOPLEFT, GameUI.convertX(viewport2, pushedTextOffset.getX()),
							GameUI.convertY(viewport2, pushedTextOffset.getY())));
					pushedHighlightText.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, simpleButtonFrame,
							FramePoint.BOTTOMRIGHT, GameUI.convertX(viewport2, pushedTextOffset.getX()),
							GameUI.convertY(viewport2, pushedTextOffset.getY())));
					simpleButtonFrame.setPushedText(pushedNormalText);
					simpleButtonFrame.setPushedHighlightText(pushedHighlightText);
				}
				else {
					simpleButtonFrame.setPushedText(normalText);
					simpleButtonFrame.setPushedHighlightText(highlightText);
				}

				inflatedFrame = simpleButtonFrame;
			}
			else if ("GLUEBUTTON".equals(frameDefinition.getFrameType())) {
				// ButtonText & ControlBackdrop
				final GlueButtonFrame glueButtonFrame = new GlueButtonFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, glueButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, glueButtonFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String controlPushedBackdropKey = getDefString(frameDefinition, parent, "ControlPushedBackdrop");
				final String controlDisabledBackdropKey = getDefString(frameDefinition, parent,
						"ControlDisabledBackdrop");
				final String controlMouseOverHighlightKey = getDefString(frameDefinition, parent,
						"ControlMouseOverHighlight");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlPushedBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlPushedBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlDisabledBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlDisabledBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlMouseOverHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlMouseOverHighlight(inflatedChild);
					}
				}
				final EnumSet<ControlStyle> controlStyle = ControlStyle
						.parseControlStyle(frameDefinition.getString("ControlStyle"));
				if (controlStyle.contains(ControlStyle.AUTOTRACK)
						&& controlStyle.contains(ControlStyle.HIGHLIGHTONMOUSEOVER)) {
					glueButtonFrame.setHighlightOnMouseOver(true);
				}
				inflatedFrame = glueButtonFrame;
			}
			else if ("BUTTON".equals(frameDefinition.getFrameType())) {
				// ButtonText & ControlBackdrop
				final GlueButtonFrame glueButtonFrame = new GlueButtonFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, glueButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, glueButtonFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String controlPushedBackdropKey = getDefString(frameDefinition, parent, "ControlPushedBackdrop");
				final String controlDisabledBackdropKey = getDefString(frameDefinition, parent,
						"ControlDisabledBackdrop");
				final String controlMouseOverHighlightKey = getDefString(frameDefinition, parent,
						"ControlMouseOverHighlight");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlPushedBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlPushedBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlDisabledBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlDisabledBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlMouseOverHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlMouseOverHighlight(inflatedChild);
					}
				}
				inflatedFrame = glueButtonFrame;
			}
			else if ("CHECKBOX".equals(frameDefinition.getFrameType())
					|| "GLUECHECKBOX".equals(frameDefinition.getFrameType())) {
				final boolean fromXML = frameDefinition.has("FromXML");
				// ButtonText & ControlBackdrop
				final CheckBoxFrame glueButtonFrame = fromXML ? new XmlCheckBoxFrame(frameDefinitionName, parent)
						: new CheckBoxFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, glueButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, glueButtonFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String controlPushedBackdropKey = getDefString(frameDefinition, parent, "ControlPushedBackdrop");
				final String controlDisabledBackdropKey = getDefString(frameDefinition, parent,
						"ControlDisabledBackdrop");
				final String checkBoxCheckHighlightKey = getDefString(frameDefinition, parent,
						"CheckBoxCheckHighlight");
				final String checkBoxDisabledCheckHighlightKey = frameDefinition
						.getString("CheckBoxDisabledCheckHighlight");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlPushedBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlPushedBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlDisabledBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlDisabledBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(checkBoxCheckHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setCheckBoxCheckHighlight(inflatedChild);
					}
					else if (childDefinition.getName().equals(checkBoxDisabledCheckHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setCheckBoxDisabledCheckHighlight(inflatedChild);
					}
					else if (fromXML) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						((XmlCheckBoxFrame) glueButtonFrame).add(inflatedChild);
						inflatedChild.setSetAllPoints(true);
					}
				}
				inflatedFrame = glueButtonFrame;
			}
			else if ("TEXTBUTTON".equals(frameDefinition.getFrameType())) {
				// ButtonText & ControlBackdrop
				final TextButtonFrame glueButtonFrame = new TextButtonFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, glueButtonFrame);
				inflateScriptsIfAvailable(frameDefinition, glueButtonFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String controlPushedBackdropKey = getDefString(frameDefinition, parent, "ControlPushedBackdrop");
				final String controlDisabledBackdropKey = getDefString(frameDefinition, parent,
						"ControlDisabledBackdrop");
				final String controlMouseOverHighlightKey = getDefString(frameDefinition, parent,
						"ControlMouseOverHighlight");
				final Vector2Definition pushedTextOffset = frameDefinition.getVector2("ButtonPushedTextOffset");
				if (pushedTextOffset != null) {
					glueButtonFrame.setButtonPushedTextOffsetX(pushedTextOffset.getX());
					glueButtonFrame.setButtonPushedTextOffsetY(pushedTextOffset.getY());
				}
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlPushedBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlPushedBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlDisabledBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlDisabledBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(controlMouseOverHighlightKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, glueButtonFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						glueButtonFrame.setControlMouseOverHighlight(inflatedChild);
					}
				}
				final String controlStyleString = frameDefinition.getString("ControlStyle");
				if (controlStyleString != null) {
					final EnumSet<ControlStyle> controlStyle = ControlStyle.parseControlStyle(controlStyleString);
					if (controlStyle.contains(ControlStyle.AUTOTRACK)
							&& controlStyle.contains(ControlStyle.HIGHLIGHTONMOUSEOVER)) {
						glueButtonFrame.setHighlightOnMouseOver(true);
					}
				}
				inflatedFrame = glueButtonFrame;
			}
			else if ("EDITBOX".equals(frameDefinition.getFrameType())) {
				final float editBorderSize = convertX(viewport2, frameDefinition.getFloat("EditBorderSize"));
				final Vector4Definition editCursorColorDefinition = frameDefinition.getVector4("EditCursorColor");
				Color editCursorColor;
				if (editCursorColorDefinition == null) {
					editCursorColor = Color.WHITE;
				}
				else {
					editCursorColor = new Color(editCursorColorDefinition.getX(), editCursorColorDefinition.getY(),
							editCursorColorDefinition.getZ(), editCursorColorDefinition.getW());
				}
				final EditBoxFrame editBoxFrame = new EditBoxFrame(frameDefinitionName, parent, editBorderSize,
						editCursorColor);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, editBoxFrame);
				inflateScriptsIfAvailable(frameDefinition, editBoxFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String editTextFrameKey = getDefString(frameDefinition, parent, "EditTextFrame");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, editBoxFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						editBoxFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(editTextFrameKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, editBoxFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						final StringFrame editTextFrame = (StringFrame) inflatedChild;
						inflatedChild.setSetAllPoints(true, editBorderSize);
						editBoxFrame.setEditTextFrame(editTextFrame);
						setText(editTextFrame, "");
					}
				}
				inflatedFrame = editBoxFrame;
			}
			else if ("SLASHCHATBOX".equals(frameDefinition.getFrameType())) {
				final float editBorderSize = convertX(viewport2, frameDefinition.getFloat("EditBorderSize"));
				final Vector4Definition editCursorColorDefinition = frameDefinition.getVector4("EditCursorColor");
				Color editCursorColor;
				if (editCursorColorDefinition == null) {
					editCursorColor = Color.WHITE;
				}
				else {
					editCursorColor = new Color(editCursorColorDefinition.getX(), editCursorColorDefinition.getY(),
							editCursorColorDefinition.getZ(), editCursorColorDefinition.getW());
				}
				final EditBoxFrame editBoxFrame = new EditBoxFrame(frameDefinitionName, parent, editBorderSize,
						editCursorColor);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, editBoxFrame);
				inflateScriptsIfAvailable(frameDefinition, editBoxFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, editBoxFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						editBoxFrame.setControlBackdrop(inflatedChild);
					}
				}

				// TODO this is probably the only case where the inflate() call is hardcoding a
				// template by name, in the future probably this should not hardcode by name
				final FrameDefinition battleNetEditBoxTextTemplate = this.templates
						.getFrame("BattleNetEditBoxTextTemplate");
				final UIFrame inflatedChild = inflate(battleNetEditBoxTextTemplate, editBoxFrame, frameDefinition,
						inDecorateFileNames || battleNetEditBoxTextTemplate.has("DecorateFileNames"));
				final StringFrame editTextFrame = (StringFrame) inflatedChild;
				inflatedChild.setSetAllPoints(true, editBorderSize);
				editBoxFrame.setEditTextFrame(editTextFrame);
				setText(editTextFrame, "");

				inflatedFrame = editBoxFrame;
			}
			else if ("CONTROL".equals(frameDefinition.getFrameType())) {
				final ControlFrame controlFrame = new ControlFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, controlFrame);
				inflateScriptsIfAvailable(frameDefinition, controlFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						controlFrame.setControlBackdrop(inflatedChild);
					}
				}
				inflatedFrame = controlFrame;
			}
			else if ("LISTBOX".equals(frameDefinition.getFrameType())) {
				// TODO advanced components here
				final ListBoxFrame controlFrame = new ListBoxFrame(frameDefinitionName, parent, viewport2,
						this.dataSource);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, controlFrame);
				inflateScriptsIfAvailable(frameDefinition, controlFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String listBoxScrollBarKey = getDefString(frameDefinition, parent, "ListBoxScrollBar");
				final Float listBoxBorder = frameDefinition.getFloat("ListBoxBorder");
				if (listBoxBorder != null) {
					controlFrame.setListBoxBorder(convertX(viewport2, listBoxBorder));
				}
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						controlFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(listBoxScrollBarKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						controlFrame.setScrollBarFrame((ScrollBarFrame) inflatedChild);
					}
				}
				if (controlFrame.getScrollBarFrame() == null) {
					// TODO this is probably not how this should work
					for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
						if (childDefinition.getFrameType().equals("SCROLLBAR")) {
							final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
									inDecorateFileNames || childDefinition.has("DecorateFileNames"));
							controlFrame.setScrollBarFrame((ScrollBarFrame) inflatedChild);
						}
					}
				}
				inflatedFrame = controlFrame;
			}
			else if ("TEXTAREA".equals(frameDefinition.getFrameType())) {
				// TODO advanced components here
				final TextAreaFrame controlFrame = new TextAreaFrame(frameDefinitionName, parent, viewport2);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, controlFrame);
				inflateScriptsIfAvailable(frameDefinition, controlFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				final String listBoxScrollBarKey = getDefString(frameDefinition, parent, "TextAreaScrollBar");
				final Float textAreaLineHeight = frameDefinition.getFloat("TextAreaLineHeight");
				if (textAreaLineHeight != null) {
					controlFrame.setLineHeight(convertY(viewport2, textAreaLineHeight));
				}
				final Float textAreaLineGap = frameDefinition.getFloat("TextAreaLineGap");
				if (textAreaLineGap != null) {
					controlFrame.setLineGap(convertY(viewport2, textAreaLineGap));
				}
				final Float textAreaInset = frameDefinition.getFloat("TextAreaInset");
				if (textAreaInset != null) {
					controlFrame.setInset(convertY(viewport2, textAreaInset));
				}
				final Float textAreaMaxLines = frameDefinition.getFloat("TextAreaMaxLines");
				if (textAreaMaxLines != null) {
					controlFrame.setMaxLines(textAreaMaxLines.intValue());
				}

				FontDefinition font = frameDefinition.getFont("FrameFont");
				if ((font == null) && (parentDefinitionIfAvailable != null)) {
					font = parentDefinitionIfAvailable.getFont("FrameFont");
				}
				this.fontParam.size = (int) convertY(viewport2,
						(font == null ? (textAreaLineHeight == null ? 0.06f : textAreaLineHeight)
								: font.getFontSize()));
				if (this.fontParam.size == 0) {
					this.fontParam.size = 24;
				}
				frameFont = this.dynamicFontGeneratorHolder
						.getFontGenerator(font == null ? "MasterFont" : font.getFontName())
						.generateFont(this.fontParam);
				controlFrame.setFrameFont(frameFont);
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						controlFrame.setControlBackdrop(inflatedChild);
					}
					else if (childDefinition.getName().equals(listBoxScrollBarKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						controlFrame.setScrollBarFrame((ScrollBarFrame) inflatedChild);
					}
				}
				if (controlFrame.getScrollBarFrame() == null) {
					// TODO this is probably not how this should work
					for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
						if (childDefinition.getFrameType().equals("SCROLLBAR")) {
							final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
									inDecorateFileNames || childDefinition.has("DecorateFileNames"));
							controlFrame.setScrollBarFrame((ScrollBarFrame) inflatedChild);
						}
					}
				}
				inflatedFrame = controlFrame;
			}
			else if ("MENU".equals(frameDefinition.getFrameType())) {
				// TODO advanced components here
				final MenuFrame controlFrame = new MenuFrame(frameDefinitionName, parent);
				// TODO: we should not need to put ourselves in this map 2x, but we do
				// since there are nested inflate calls happening before the general case
				// mapping
				this.nameToFrame.put(frameDefinitionName, controlFrame);
				inflateScriptsIfAvailable(frameDefinition, controlFrame);
				final String controlBackdropKey = getDefString(frameDefinition, parent, "ControlBackdrop");
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					if (childDefinition.getName().equals(controlBackdropKey)) {
						final UIFrame inflatedChild = inflate(childDefinition, controlFrame, frameDefinition,
								inDecorateFileNames || childDefinition.has("DecorateFileNames"));
						inflatedChild.setSetAllPoints(true);
						controlFrame.setControlBackdrop(inflatedChild);
					}
				}
				final List<FrameDefinitionField> fields = frameDefinition.getFields("MenuItem");
				FontDefinition font = frameDefinition.getFont("FrameFont");
				if ((font == null) && (parentDefinitionIfAvailable != null)) {
					font = parentDefinitionIfAvailable.getFont("FrameFont");
				}
				final Float height = frameDefinition.getFloat("Height");
				this.fontParam.size = (int) convertY(viewport2,
						(font == null ? (height == null ? 0.06f : height) : font.getFontSize()));
				if (this.fontParam.size == 0) {
					this.fontParam.size = 24;
				}
				frameFont = this.dynamicFontGeneratorHolder.getFontGenerator(font.getFontName())
						.generateFont(this.fontParam);
				controlFrame.setFrameFont(frameFont);

				Color fontHighlightColor;
				final Vector4Definition fontHighlightColorDefinition = frameDefinition
						.getVector4("MenuTextHighlightColor");
				if (fontHighlightColorDefinition == null) {
					fontHighlightColor = null;
				}
				else {
					fontHighlightColor = new Color(fontHighlightColorDefinition.getX(),
							fontHighlightColorDefinition.getY(), fontHighlightColorDefinition.getZ(),
							fontHighlightColorDefinition.getW());
				}

				Color fontDisabledColor;
				// TODO the key "MenuTextDisabledColor" was not observed in any sample; I made
				// it up to fulfill the argument to my function call
				final Vector4Definition fontDisabledColorDefinition = frameDefinition
						.getVector4("MenuTextDisabledColor");
				if (fontDisabledColorDefinition == null) {
					fontDisabledColor = null;
				}
				else {
					fontDisabledColor = new Color(fontDisabledColorDefinition.getX(),
							fontDisabledColorDefinition.getY(), fontDisabledColorDefinition.getZ(),
							fontDisabledColorDefinition.getW());
				}

				controlFrame.setFontHighlightColor(fontHighlightColor);
				controlFrame.setFontDisabledColor(fontDisabledColor);

				final Float menuItemHeight = frameDefinition.getFloat("MenuItemHeight");
				if (menuItemHeight != null) {
					controlFrame.setItemHeight(convertY(viewport2, menuItemHeight.floatValue()));
				}
				final Float menuBorder = frameDefinition.getFloat("MenuBorder");
				if (menuBorder != null) {
					controlFrame.setBorder(convertY(viewport2, menuBorder));
				}

				if (fields != null) {
					final List<MenuItem> menuItems = new ArrayList<>();
					for (final FrameDefinitionField field : fields) {
						final MenuItem menuItem = field.visit(GetMenuItemFieldVisitor.INSTANCE);
						menuItems.add(new MenuItem(getTemplates().getDecoratedString(menuItem.getText()),
								menuItem.getNumericValue()));
					}
					controlFrame.setItems(viewport2, menuItems);
				}
				inflatedFrame = controlFrame;
			}
			else if ("HIGHLIGHT".equals(frameDefinition.getFrameType())) {
				final String highlightType = frameDefinition.getString("HighlightType");
				if (!"FILETEXTURE".equals(highlightType)) {
					throw new IllegalStateException(
							"Our engine does not know how to handle a non-FILETEXTURE highlight");
				}
				final String highlightAlphaFile = frameDefinition.getString("HighlightAlphaFile");
				final String highlightAlphaMode = frameDefinition.getString("HighlightAlphaMode");
				final FilterModeTextureFrame textureFrame = new FilterModeTextureFrame(frameDefinitionName, parent,
						inDecorateFileNames || frameDefinition.has("DecorateFileNames"), null);
				inflateScriptsIfAvailable(frameDefinition, textureFrame);
				textureFrame.setTexture(highlightAlphaFile, this);
				if ("ADD".equals(highlightAlphaMode)) {
					textureFrame.setFilterMode(FilterMode.ADDALPHA);
				}
				else if ("BLEND".equals(highlightAlphaMode)) {
					textureFrame.setFilterMode(FilterMode.BLEND);
				}
				return textureFrame;
			}
			else if ("BACKDROP".equals(frameDefinition.getFrameType())) {
				final boolean tileBackground = frameDefinition.has("BackdropTileBackground");
				final boolean mirrored = frameDefinition.has("BackdropMirrored");
				String backgroundString = frameDefinition.getString("BackdropBackground");
				String cornerFlagsString = frameDefinition.getString("BackdropCornerFlags");
				if (cornerFlagsString == null) {
					cornerFlagsString = "";
				}
				final EnumSet<BackdropCornerFlags> cornerFlags = BackdropCornerFlags
						.parseCornerFlags(cornerFlagsString);
				final Float cornerSizeNullable = frameDefinition.getFloat("BackdropCornerSize");
				final float cornerSize = GameUI.convertX(viewport2,
						cornerSizeNullable == null ? 0.0f : cornerSizeNullable);
				final Float backgroundSizeNullable = frameDefinition.getFloat("BackdropBackgroundSize");
				final float backgroundSize = GameUI.convertX(viewport2,
						backgroundSizeNullable == null ? 0.0f : backgroundSizeNullable);
				Vector4Definition backgroundInsets = frameDefinition.getVector4("BackdropBackgroundInsets");
				if (backgroundInsets != null) {
					backgroundInsets = new Vector4Definition(GameUI.convertX(viewport2, backgroundInsets.getX()),
							GameUI.convertY(viewport2, backgroundInsets.getW()),
							GameUI.convertX(viewport2, backgroundInsets.getZ()),
							GameUI.convertY(viewport2, backgroundInsets.getY()));
				}
				else {
					backgroundInsets = new Vector4Definition(0, 0, 0, 0);
				}
				final boolean decorateFileNames = frameDefinition.has("DecorateFileNames") || inDecorateFileNames;
				String edgeFileString = frameDefinition.getString("BackdropEdgeFile");
				if (DEBUG_LOG) {
					System.out.println(frameDefinitionName + " wants edge file: " + edgeFileString);
				}
				if (decorateFileNames && (edgeFileString != null)) {
					edgeFileString = trySkinField(edgeFileString);
				}
				if (decorateFileNames && (edgeFileString != null)) {
					backgroundString = trySkinField(backgroundString);
				}
				final Texture background = backgroundString == null ? null : loadTexture(backgroundString);
				final Texture edgeFile = edgeFileString == null ? null : loadTexture(edgeFileString);
				if (DEBUG_LOG) {
					System.out.println(frameDefinitionName + " got edge file: " + edgeFile);
				}

				final BackdropFrame backdropFrame = new BackdropFrame(frameDefinitionName, parent, decorateFileNames,
						tileBackground, background, cornerFlags, cornerSize, backgroundSize, backgroundInsets, edgeFile,
						mirrored);
				this.nameToFrame.put(frameDefinitionName, backdropFrame);
				inflateScriptsIfAvailable(frameDefinition, backdropFrame);
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					backdropFrame.add(inflate(childDefinition, backdropFrame, frameDefinition, decorateFileNames));
				}
				inflatedFrame = backdropFrame;
			}
			break;
		case Layer:
			final SimpleFrame simpleFrame = new SimpleFrame(frameDefinitionName, parent);
			simpleFrame.setSetAllPoints(true);
			this.nameToFrame.put(frameDefinitionName, simpleFrame);
			inflateScriptsIfAvailable(frameDefinition, simpleFrame);
			for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
				simpleFrame.add(inflate(childDefinition, simpleFrame, frameDefinition,
						inDecorateFileNames || childDefinition.has("DecorateFileNames")));
			}
			inflatedFrame = simpleFrame;
			break;
		case String:
			final Float textLength = frameDefinition.getFloat("TextLength");
			TextJustify justifyH = frameDefinition.getTextJustify("FontJustificationH");
			if (justifyH == null) {
				justifyH = TextJustify.CENTER;
			}
			TextJustify justifyV = frameDefinition.getTextJustify("FontJustificationV");
			if (justifyV == null) {
				justifyV = TextJustify.MIDDLE;
			}

			Color fontColor;
			final Vector4Definition fontColorDefinition = frameDefinition.getVector4("FontColor");
			if (fontColorDefinition == null) {
				fontColor = Color.WHITE;
			}
			else {
				fontColor = new Color(fontColorDefinition.getX(), fontColorDefinition.getY(),
						fontColorDefinition.getZ(), fontColorDefinition.getW());
			}
			final FontDefinition font = frameDefinition.getFont("Font");
			this.fontParam.size = (int) convertY(viewport2, font.getFontSize());
			if (this.fontParam.size == 0) {
				this.fontParam.size = 24;
			}
			frameFont = this.dynamicFontGeneratorHolder.getFontGenerator(font.getFontName())
					.generateFont(this.fontParam);
			String textString = frameDefinitionName.startsWith("#") ? "" : frameDefinitionName;
			String text = getDefString(frameDefinition, parent, "Text");
			if (text != null) {
				final String decoratedString = this.templates.getDecoratedString(text);
				if (decoratedString != text) {
					text = decoratedString;
				}
				textString = text;
			}
			final StringFrame stringFrame = new StringFrame(frameDefinitionName, parent, fontColor, justifyH, justifyV,
					frameFont, textString, null, null);
			inflatedFrame = stringFrame;
			inflateScriptsIfAvailable(frameDefinition, inflatedFrame);
			break;
		case Texture:
			final String file = frameDefinition.getString("File");
			final boolean decorateFileNames = frameDefinition.has("DecorateFileNames") || inDecorateFileNames;
			final Vector4Definition texCoord = frameDefinition.getVector4("TexCoord");
			TextureFrame textureFrame;
			final String alphaMode = frameDefinition.getString("AlphaMode");
			if ((alphaMode != null) && alphaMode.equals("ADD")) {
				final FilterModeTextureFrame filterModeTextureFrame = new FilterModeTextureFrame(frameDefinitionName,
						parent, decorateFileNames, texCoord);
				filterModeTextureFrame.setFilterMode(FilterMode.ADDALPHA);
				textureFrame = filterModeTextureFrame;
			}
			else {
				textureFrame = new TextureFrame(frameDefinitionName, parent, decorateFileNames, texCoord);
			}
			if (file != null) {
				textureFrame.setTexture(file, this);
			}
			inflatedFrame = textureFrame;
			inflateScriptsIfAvailable(frameDefinition, inflatedFrame);
			break;
		default:
			break;
		}
		if (inflatedFrame != null) {
			final UIFrame fInflatedFrame = inflatedFrame;
			final String id = frameDefinition.getString("ID");
			if (id != null) {
				inflatedFrame.setID(Integer.parseInt(id));
			}
			if (frameDefinition.has("SetAllPoints")) {
				inflatedFrame.setSetAllPoints(true);
			}
			if (frameDefinition.has("XMLHidden")) {
				fInflatedFrame.setVisible(false);
			}
			else {
				this.pendingScriptLoads.add(() -> {
					if (fInflatedFrame.isVisibleOnScreen()) {
						if (fInflatedFrame.getScripts() != null) {
							fInflatedFrame.getScripts().onLoad();
						}
					}
				});
			}
			if ((inflatedFrame instanceof FocusableFrame) && (frameDefinition.get("TabFocusNext") != null)) {
				this.focusableFrames.add((FocusableFrame) inflatedFrame);
			}
			Float width = frameDefinition.getFloat("Width");
			if (width != null) {
				inflatedFrame.setWidth(convertX(viewport2, width));
			}
			else {
				width = frameDefinition.getFloat("TextLength");
				if (width != null) {
					if (frameFont != null) {
						inflatedFrame.setWidth(convertX(viewport2, width * frameFont.getSpaceXadvance()));
					}
				}
			}
			final Float height = frameDefinition.getFloat("Height");
			if (height != null) {
				inflatedFrame.setHeight(convertY(viewport2, height));
			}
			for (final AnchorDefinition anchor : frameDefinition.getAnchors()) {
				inflatedFrame.addAnchor(new AnchorDefinition(anchor.getMyPoint(), anchor.getRelativePoint(),
						convertX(this.viewport, anchor.getX()), convertY(this.viewport, anchor.getY())));
			}
			for (final SetPointDefinition setPointDefinition : frameDefinition.getSetPoints()) {
				final String otherFrameName = checkNameString(parent, setPointDefinition.getOther());
				final UIFrame otherFrameByName = getFrameByName(otherFrameName, 0 /* TODO: createContext */);
				if (otherFrameByName == null) {
					System.err.println("Failing to pin " + frameDefinitionName + " to " + otherFrameName + " ('"
							+ setPointDefinition.getOther() + "')" + " because it was null!");
					if (PIN_FAIL_IS_FATAL) {
						throw new IllegalStateException(
								"Failing to pin " + frameDefinitionName + " to " + otherFrameName + " ('"
										+ setPointDefinition.getOther() + "')" + " because it was null!");
					}
				}
				else {
					inflatedFrame.addSetPoint(new SetPoint(setPointDefinition.getMyPoint(), otherFrameByName,
							setPointDefinition.getOtherPoint(), convertX(this.viewport, setPointDefinition.getX()),
							convertY(this.viewport, setPointDefinition.getY())));
				}
			}
			this.nameToFrame.put(frameDefinitionName, inflatedFrame);
		}
		else {
			// TODO in production throw some kind of exception here
		}
		checkInternalMappingSize();
		return inflatedFrame;
	}

	private String getDefString(final FrameDefinition frameDefinition, final UIFrame parent, final String string) {
		final String value = frameDefinition.getString(string);
		return checkNameString(parent, value);
	}

	private void inflateScriptsIfAvailable(final FrameDefinition frameDefinition, final UIFrame inflatedFrame) {
		final FrameDefinition scriptDefinition = frameDefinition.getScriptDefinition();
		if (scriptDefinition != null) {
			inflateScriptBindings(inflatedFrame, scriptDefinition);
		}
		else if (frameDefinition.has("FromXML")) {
			inflateScriptBindings(inflatedFrame, null);
		}
	}

	public String checkNameString(UIFrame parent, String frameDefinitionName) {
		if ((frameDefinitionName != null) && (parent != null)) {
			String parentName;
			do {
				parentName = parent.getName();
				parent = parent.getParent();
			}
			while (((parentName == null) || parentName.startsWith("#")) && (parent != null));
			if (parentName != null) {
				frameDefinitionName = frameDefinitionName.replace("$parent", parentName);
			}
		}
		return frameDefinitionName;
	}

	public void setSpriteFrameModel(final SpriteFrame2 spriteFrame, final String backgroundArt) {
		final MdxModel model = War3MapViewer.loadModelMdx(this.modelViewer.dataSource, this.modelViewer, backgroundArt,
				this.modelViewer.mapPathSolver, this.modelViewer.solverParams);
		spriteFrame.setModel(model);
	}

	public void setSpriteFrameModel(final SpriteFrame spriteFrame, final String backgroundArt) {
		final MdxModel model = War3MapViewer.loadModelMdx(this.modelViewer.dataSource, this.modelViewer, backgroundArt,
				this.modelViewer.mapPathSolver, this.modelViewer.solverParams);
		spriteFrame.setModel(model);
	}

	public UIFrame createFrameByType(final String typeName, final String name, final UIFrame owner,
			final String inherits, final int createContext) {
		// TODO idk what inherits is doing yet, and I didn't implement createContext yet
		// even though it looked like just mapping/indexing on int
		final FrameDefinition baseTemplateDef = this.templates.getFrame(name);
		final FrameDefinition frameDefinition = new FrameDefinition(FrameClass.Frame, typeName, name);
		if (baseTemplateDef != null) {
			frameDefinition.inheritFrom(baseTemplateDef, "WITHCHILDREN".equals(inherits));
		}
		final UIFrame inflatedFrame = inflate(frameDefinition, owner, null, frameDefinition.has("DecorateFileNames"));
		if (this == owner) {
			add(inflatedFrame);
		}
		return inflatedFrame;
	}

	public UIFrame getFrameByName(final String name, final int createContext) {
		return this.nameToFrame.get(name);
	}

	public static float convertX(final Viewport viewport, final float fdfX) {
		if (viewport instanceof ExtendViewport) {
			return (fdfX / 0.8f) * ((ExtendViewport) viewport).getMinWorldWidth();
		}
		return (fdfX / 0.8f) * viewport.getWorldWidth();
	}

	public static float convertY(final Viewport viewport, final float fdfY) {
		if (viewport instanceof ExtendViewport) {
			return (fdfY / 0.6f) * ((ExtendViewport) viewport).getMinWorldHeight();
		}
		return (fdfY / 0.6f) * viewport.getWorldHeight();
	}

	public static float unconvertX(final Viewport viewport, final float nonFdfX) {
		if (viewport instanceof ExtendViewport) {
			return (nonFdfX / ((ExtendViewport) viewport).getMinWorldWidth()) * 0.8f;
		}
		return (nonFdfX / viewport.getWorldWidth()) * 0.8f;
	}

	public static float unconvertY(final Viewport viewport, final float nonFdfY) {
		if (viewport instanceof ExtendViewport) {
			return (nonFdfY / ((ExtendViewport) viewport).getMinWorldHeight()) * 0.6f;
		}
		return (nonFdfY / viewport.getWorldHeight()) * 0.6f;
	}

	public Texture loadTexture(String path) {
		Texture texture = this.pathToTexture.get(path);
		if (texture == null) {
			final String originalPath = path;
			final int lastDotIndex = path.lastIndexOf('.');
			if (lastDotIndex == -1) {
				path = path + ".blp";
			}
			else {
				path = path.substring(0, lastDotIndex) + ".blp";
			}
			try {
				texture = ImageUtils.getAnyExtensionTexture(this.dataSource, path);
				this.pathToTexture.put(path, texture);
				this.pathToTexture.put(originalPath, texture);
			}
			catch (final Exception exc) {
			}
		}
		return texture;
	}

	@Override
	public final void positionBounds(final GameUI gameUI, final Viewport viewport) {
		innerPositionBounds(this, viewport);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
	}

	@Override
	public void add(final UIFrame childFrame) {
		super.add(childFrame);
		this.nameToFrame.put(childFrame.getName(), childFrame);
		checkInternalMappingSize();
	}

	public void checkInternalMappingSize() {
//		System.out.println("nameToFrame.size(): " + this.nameToFrame.size());
	}

	public Scene getUiScene() {
		return this.uiScene;
	}

	public FrameTemplateEnvironment getTemplates() {
		return this.templates;
	}

	public String getErrorString(final String key) {
		final String errorString = this.errorStrings.getField(key, this.racialCommandIndex);
		return getTrigStr(errorString);
	}

	public String getTrigStr(String errorString) {
		if (errorString.startsWith("TRIGSTR_")) {
			errorString = this.mapStrings.get(Integer.parseInt(errorString.substring(8)));
		}
		return errorString;
	}

	public GlyphLayout getGlyphLayout() {
		return this.glyphLayout;
	}

	public void setText(final StringFrame stringFrame, final String text) {
		stringFrame.setText(text, this, this.viewport);
	}

	public void setDecoratedText(final StringFrame stringFrame, final String text) {
		stringFrame.setText(this.templates.getDecoratedString(text), this, this.viewport);
	}

	public BitmapFont getFont() {
		return this.font;
	}

	public BitmapFont getFont20() {
		return this.font20;
	}

	public FontGeneratorHolder getFontGenerator() {
		return this.fontGenerator;
	}

	public void dispose() {
		this.dynamicFontGeneratorHolder.dispose();
	}

	public FocusableFrame getNextFocusFrame() {
		// TODO to support tabbing thru menus and stuff, we will have to implement this
		return null;
	}

	public void setMapStrings(final WTS mapStrings) {
		this.mapStrings = mapStrings;
	}

	public List<FocusableFrame> getFocusableFrames() {
		return this.focusableFrames;
	}

	public void setAutoPosition(final boolean autoPosition) {
		this.autoPosition = autoPosition;
	}

	public boolean isAutoPosition() {
		return this.autoPosition;
	}

	public void bindPawnUnit(final CUnit pawnUnit, final CAbilityPlayerPawn abilityPlayerPawn,
			final AbilityDataUI abilityDataUI) {
		this.pawnUnit = pawnUnit;
		this.abilityPlayerPawn = abilityPlayerPawn;
		this.abilityDataUI = abilityDataUI;

	}
}

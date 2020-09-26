package com.etheller.warsmash.parsers.fdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.fdfparser.FDFParser;
import com.etheller.warsmash.fdfparser.FrameDefinitionVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FontDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameClass;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;
import com.etheller.warsmash.parsers.fdf.datamodel.SetPointDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.frames.AbstractUIFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public final class GameUI extends AbstractUIFrame implements UIFrame {
	private final DataSource dataSource;
	private final Element skin;
	private final Viewport viewport;
	private final Scene uiScene;
	private final War3MapViewer modelViewer;
	private final FrameTemplateEnvironment templates;
	private final Map<String, Texture> pathToTexture = new HashMap<>();
	private final boolean autoPosition = false;
	private final FreeTypeFontGenerator fontGenerator;
	private final FreeTypeFontParameter fontParam;
	private final Map<String, UIFrame> nameToFrame = new HashMap<>();
	private final Viewport fdfCoordinateResolutionDummyViewport;

	public GameUI(final DataSource dataSource, final Element skin, final Viewport viewport,
			final FreeTypeFontGenerator fontGenerator, final Scene uiScene, final War3MapViewer modelViewer) {
		super("GameUI", null);
		this.dataSource = dataSource;
		this.skin = skin;
		this.viewport = viewport;
		this.uiScene = uiScene;
		this.modelViewer = modelViewer;
		if (viewport instanceof ExtendViewport) {
			this.renderBounds.set(0, 0, ((ExtendViewport) viewport).getMinWorldWidth(),
					((ExtendViewport) viewport).getMinWorldHeight());
		}
		else {
			this.renderBounds.set(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		}
		this.templates = new FrameTemplateEnvironment();
		this.fontGenerator = fontGenerator;
		this.fontParam = new FreeTypeFontParameter();
		this.fdfCoordinateResolutionDummyViewport = new FitViewport(0.8f, 0.6f);
	}

	public static Element loadSkin(final DataSource dataSource, final String skin) {
		final DataTable skinsTable = new DataTable(StringBundle.EMPTY);
		try (InputStream stream = dataSource.getResourceAsStream("UI\\war3skins.txt")) {
			skinsTable.readTXT(stream, true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
//		final Element main = skinsTable.get("Main");
//		final String skinsField = main.getField("Skins");
//		final String[] skins = skinsField.split(",");
		final Element defaultSkin = skinsTable.get("Default");
		final Element userSkin = skinsTable.get(skin);
		for (final String key : defaultSkin.keySet()) {
			if (!userSkin.hasField(key)) {
				userSkin.setField(key, defaultSkin.getField(key));
			}
		}
		return userSkin;
	}

	public static Element loadSkin(final DataSource dataSource, final int skinIndex) {
		final DataTable skinsTable = new DataTable(StringBundle.EMPTY);
		try (InputStream stream = dataSource.getResourceAsStream("UI\\war3skins.txt")) {
			skinsTable.readTXT(stream, true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		final Element main = skinsTable.get("Main");
		final String skinsField = main.getField("Skins");
		final String[] skins = skinsField.split(",");
		final Element defaultSkin = skinsTable.get("Default");
		final Element userSkin = skinsTable.get(skins[skinIndex]);
		for (final String key : defaultSkin.keySet()) {
			if (!userSkin.hasField(key)) {
				userSkin.setField(key, defaultSkin.getField(key));
			}
		}
		return userSkin;
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
				final FDFParser firstFileParser = dataSourceFDFParserBuilder.build(line);
				fdfVisitor.visit(firstFileParser.program());
				tocLines++;
			}
			System.out.println("TOC file loaded " + tocLines + " lines");
		}
	}

	public String getSkinField(String file) {
		if ((file != null) && this.skin.hasField(file)) {
			file = this.skin.getField(file);
		}
		else {
			throw new IllegalStateException("Decorated file name lookup not available: " + file);
		}
		return file;
	}

	public UIFrame createFrame(final String name, final UIFrame owner, final int priority, final int createContext) {
		final FrameDefinition frameDefinition = this.templates.getFrame(name);
		if (frameDefinition.getFrameClass() == FrameClass.Frame) {
			if ("SPRITE".equals(frameDefinition.getFrameType())) {
				final UIFrame inflated = inflate(frameDefinition, owner, null);
				if (this.autoPosition) {
					inflated.positionBounds(this.viewport);
				}
				add(inflated);
				return inflated;
			}
		}
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public UIFrame createSimpleFrame(final String name, final UIFrame owner, final int createContext) {
		final FrameDefinition frameDefinition = this.templates.getFrame(name);
		if (frameDefinition.getFrameClass() == FrameClass.Frame) {
			final UIFrame inflated = inflate(frameDefinition, owner, null);
			if (this.autoPosition) {
				inflated.positionBounds(this.viewport);
			}
			add(inflated);
			return inflated;
		}
		return null;
	}

	public UIFrame inflate(final FrameDefinition frameDefinition, final UIFrame parent,
			final FrameDefinition parentDefinitionIfAvailable) {
		UIFrame inflatedFrame = null;
		BitmapFont frameFont = null;
		Viewport viewport2 = this.viewport;
		switch (frameDefinition.getFrameClass()) {
		case Frame:
			if ("SIMPLEFRAME".equals(frameDefinition.getFrameType())) {
				final SimpleFrame simpleFrame = new SimpleFrame(frameDefinition.getName(), parent);
				// TODO: we should not need to put ourselves in this map 2x
				this.nameToFrame.put(frameDefinition.getName(), simpleFrame);
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					simpleFrame.add(inflate(childDefinition, simpleFrame, frameDefinition));
				}
				inflatedFrame = simpleFrame;
			}
			else if ("SPRITE".equals(frameDefinition.getFrameType())) {
				final SpriteFrame spriteFrame = new SpriteFrame(frameDefinition.getName(), parent, this.uiScene);
				String backgroundArt = frameDefinition.getString("BackgroundArt");
				if (frameDefinition.has("DecorateFileNames") || ((parentDefinitionIfAvailable != null)
						&& parentDefinitionIfAvailable.has("DecorateFileNames"))) {
					if (this.skin.hasField(backgroundArt)) {
						backgroundArt = this.skin.getField(backgroundArt);
					}
					else {
						throw new IllegalStateException("Decorated file name lookup not available: " + backgroundArt);
					}
				}
				if (backgroundArt.toLowerCase().endsWith(".mdl") || backgroundArt.toLowerCase().endsWith(".mdx")) {
					backgroundArt = backgroundArt.substring(0, backgroundArt.length() - 4);
				}
				backgroundArt += ".mdx";
				final MdxModel model = (MdxModel) this.modelViewer.load(backgroundArt, this.modelViewer.mapPathSolver,
						this.modelViewer.solverParams);
				spriteFrame.setModel(model);
				viewport2 = this.fdfCoordinateResolutionDummyViewport;
				inflatedFrame = spriteFrame;
			}
			break;
		case Layer:
			// NOT HANDLED YET
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
			frameFont = this.fontGenerator.generateFont(this.fontParam);
			final StringFrame stringFrame = new StringFrame(frameDefinition.getName(), parent, fontColor, justifyH,
					justifyV, frameFont);
			inflatedFrame = stringFrame;
			String text = frameDefinition.getString("Text");
			if (text != null) {
				final String decoratedString = this.templates.getDecoratedString(text);
				if (decoratedString != text) {
					text = decoratedString;
				}
				stringFrame.setText(text);
			}
			break;
		case Texture:
			final String file = frameDefinition.getString("File");
			final boolean decorateFileNames = frameDefinition.has("DecorateFileNames")
					|| ((parentDefinitionIfAvailable != null) && parentDefinitionIfAvailable.has("DecorateFileNames"));
			final Vector4Definition texCoord = frameDefinition.getVector4("TexCoord");
			final TextureFrame textureFrame = new TextureFrame(frameDefinition.getName(), parent, decorateFileNames,
					texCoord);
			textureFrame.setTexture(file, this);
			inflatedFrame = textureFrame;
			break;
		default:
			break;
		}
		if (inflatedFrame != null) {
			if (frameDefinition.has("SetAllPoints")) {
				inflatedFrame.setSetAllPoints(true);
			}
			Float width = frameDefinition.getFloat("Width");
			if (width != null) {
				inflatedFrame.setWidth(convertX(viewport2, width));
			}
			else {
				width = frameDefinition.getFloat("TextLength");
				if (width != null) {
					if (frameFont != null) {
						inflatedFrame.setWidth(convertX(viewport2, width * frameFont.getSpaceWidth()));
					}
				}
			}
			final Float height = frameDefinition.getFloat("Height");
			if (height != null) {
				inflatedFrame.setHeight(convertY(viewport2, height));
			}
			else if (frameDefinition.getFont("Font") != null) {
				inflatedFrame.setHeight(convertY(viewport2, frameDefinition.getFont("Font").getFontSize()));
			}
			for (final AnchorDefinition anchor : frameDefinition.getAnchors()) {
				inflatedFrame.addAnchor(new AnchorDefinition(anchor.getMyPoint(), convertX(viewport2, anchor.getX()),
						convertY(viewport2, anchor.getY())));
			}
			for (final SetPointDefinition setPointDefinition : frameDefinition.getSetPoints()) {
				inflatedFrame.addSetPoint(new SetPoint(setPointDefinition.getMyPoint(),
						getFrameByName(setPointDefinition.getOther(), 0 /* TODO: createContext */),
						setPointDefinition.getOtherPoint(), convertX(viewport2, setPointDefinition.getX()),
						convertY(viewport2, setPointDefinition.getY())));
			}
			this.nameToFrame.put(frameDefinition.getName(), inflatedFrame);
		}
		else {
			// TODO in production throw some kind of exception here
		}
		return inflatedFrame;
	}

	public UIFrame createFrameByType(final String typeName, final String name, final UIFrame owner,
			final String inherits, final int createContext) {
		throw new UnsupportedOperationException("Not yet implemented");
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

	public Texture loadTexture(String path) {
		if (!path.contains(".")) {
			path = path + ".blp";
		}
		Texture texture = this.pathToTexture.get(path);
		if (texture == null) {
			try {
				texture = ImageUtils.getBLPTexture(this.dataSource, path);
				this.pathToTexture.put(path, texture);
			}
			catch (final Exception exc) {

			}
		}
		return texture;
	}

	@Override
	public final void positionBounds(final Viewport viewport) {
		innerPositionBounds(viewport);
	}
}

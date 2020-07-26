package com.etheller.warsmash.parsers.fdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.fdfparser.FDFParser;
import com.etheller.warsmash.fdfparser.FrameDefinitionVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameClass;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.frames.AbstractUIFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.StringBundle;

public final class GameUI extends AbstractUIFrame implements UIFrame {

	private final DataSource dataSource;
	private final Element skin;
	private final Viewport viewport;
	private final FrameTemplateEnvironment templates;
	private final Map<String, Texture> pathToTexture = new HashMap<>();
	private final boolean autoPosition = false;

	public GameUI(final DataSource dataSource, final Element skin, final Viewport viewport) {
		super("GameUI", null);
		this.dataSource = dataSource;
		this.skin = skin;
		this.viewport = viewport;
		this.renderBounds.set(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		this.templates = new FrameTemplateEnvironment();

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

	public UIFrame createFrame(final String name, final UIFrame owner, final int priority, final int createContext) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public UIFrame createSimpleFrame(final String name, final UIFrame owner, final int createContext) {
		final FrameDefinition frameDefinition = this.templates.getFrame(name);
		if (frameDefinition.getFrameClass() == FrameClass.Frame) {
			if ("SIMPLEFRAME".equals(frameDefinition.getFrameType())) {
				final UIFrame inflated = inflate(frameDefinition, owner, null);
				if (this.autoPosition) {
					inflated.positionBounds(this.viewport);
				}
				add(inflated);
				return inflated;
			}
		}
		return null;
	}

	public UIFrame inflate(final FrameDefinition frameDefinition, final UIFrame parent,
			final FrameDefinition parentDefinitionIfAvailable) {
		UIFrame inflatedFrame = null;
		switch (frameDefinition.getFrameClass()) {
		case Frame:
			if ("SIMPLEFRAME".equals(frameDefinition.getFrameType())) {
				final SimpleFrame simpleFrame = new SimpleFrame(frameDefinition.getName(), parent);
				for (final FrameDefinition childDefinition : frameDefinition.getInnerFrames()) {
					simpleFrame.add(inflate(childDefinition, simpleFrame, frameDefinition));
				}
				inflatedFrame = simpleFrame;
			}
			break;
		case Layer:
			// NOT HANDLED YET
			break;
		case String:
			break;
		case Texture:
			String file = frameDefinition.getString("File");
			if (frameDefinition.has("DecorateFileNames") || ((parentDefinitionIfAvailable != null)
					&& parentDefinitionIfAvailable.has("DecorateFileNames"))) {
				if (this.skin.hasField(file)) {
					file = this.skin.getField(file);
				}
				else {
					throw new IllegalStateException("Decorated file name lookup not available: " + file);
				}
			}
			final Texture texture = loadTexture(file);
			final Vector4Definition texCoord = frameDefinition.getVector4("TexCoord");
			final TextureRegion texRegion;
			if (texCoord != null) {
				texRegion = new TextureRegion(texture, texCoord.getX(), texCoord.getZ(), texCoord.getY(),
						texCoord.getW());
			}
			else {
				texRegion = new TextureRegion(texture);
			}
			final TextureFrame textureFrame = new TextureFrame(frameDefinition.getName(), parent, texRegion);
			inflatedFrame = textureFrame;
			break;
		default:
			break;
		}
		if (inflatedFrame != null) {
			final Float width = frameDefinition.getFloat("Width");
			if (width != null) {
				inflatedFrame.setWidth(convertX(this.viewport, width));
			}
			final Float height = frameDefinition.getFloat("Height");
			if (height != null) {
				inflatedFrame.setHeight(convertY(this.viewport, height));
			}
			for (final AnchorDefinition anchor : frameDefinition.getAnchors()) {
				inflatedFrame.addAnchor(new AnchorDefinition(anchor.getMyPoint(),
						convertX(this.viewport, anchor.getX()), convertY(this.viewport, anchor.getY())));
			}
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

	public static float convertX(final Viewport viewport, final float fdfX) {
		return (fdfX / 0.8f) * viewport.getWorldWidth();
	}

	public static float convertY(final Viewport viewport, final float fdfY) {
		return (fdfY / 0.6f) * viewport.getWorldHeight();
	}

	private Texture loadTexture(String path) {
		if (!path.contains(".")) {
			path = path + ".blp";
		}
		Texture texture = this.pathToTexture.get(path);
		if (texture == null) {
			texture = ImageUtils.getBLPTexture(this.dataSource, path);
			this.pathToTexture.put(path, texture);
		}
		return texture;
	}

	@Override
	public final void positionBounds(final Viewport viewport) {
		innerPositionBounds(viewport);
	}
}

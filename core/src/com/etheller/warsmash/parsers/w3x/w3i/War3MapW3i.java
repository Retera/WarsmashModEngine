package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * war3map.w3i - the general map information file.
 */
public class War3MapW3i {
	private int version;
	private int saves;
	private int editorVersion;
	private int gameVersionMajor;
	private int gameVersionMinor;
	private int gameVersionPatch;
	private int gameVersionBuild;
	private String name;
	private String author;
	private String description;
	private String recommendedPlayers;
	private final float[] cameraBounds = new float[8];
	private final int[] cameraBoundsComplements = new int[4];
	private final int[] playableSize = new int[2];
	private long flags;
	private char tileset = 'A';
	private int campaignBackground;
	private String loadingScreenModel;
	private String loadingScreenText;
	private String loadingScreenTitle;
	private String loadingScreenSubtitle;
	private int gameDataSet;
	private String prologueScreenModel;
	private String prologueScreenText;
	private String prologueScreenTitle;
	private String prologueScreenSubtitle;
	private int useTerrainFog;
	private final float[] fogHeight = new float[2];
	private float fogDensity;
	private final short[] fogColor = new short[4];
	private int globalWeather;
	private String soundEnvironment;
	private char lightEnvironmentTileset;
	private final short[] waterVertexColor = new short[4];
	private final short[] unknown2ProbablyLua = new short[4];
	private long supportedModes;
	private long gameDataVersion;
	private final List<Player> players = new ArrayList<>();
	private final List<Force> forces = new ArrayList<>();
	private final List<UpgradeAvailabilityChange> upgradeAvailabilityChanges = new ArrayList<>();
	private final List<TechAvailabilityChange> techAvailabilityChanges = new ArrayList<>();
	private final List<RandomUnitTable> randomUnitTables = new ArrayList<>();
	private final List<RandomItemTable> randomItemTables = new ArrayList<>();

	public War3MapW3i(final LittleEndianDataInputStream stream) throws IOException {
		if (stream != null) {
			load(stream);
		}
	}

	private void load(final LittleEndianDataInputStream stream) throws IOException {
		this.version = stream.readInt();
		this.saves = stream.readInt();
		this.editorVersion = stream.readInt();

		if (this.version > 27) {
			this.gameVersionMajor = stream.readInt();
			this.gameVersionMinor = stream.readInt();
			this.gameVersionPatch = stream.readInt();
			this.gameVersionBuild = stream.readInt();
		}

		this.name = ParseUtils.readUntilNull(stream);
		this.author = ParseUtils.readUntilNull(stream);
		this.description = ParseUtils.readUntilNull(stream);
		this.recommendedPlayers = ParseUtils.readUntilNull(stream);
		ParseUtils.readFloatArray(stream, this.cameraBounds);
		ParseUtils.readInt32Array(stream, this.cameraBoundsComplements);
		ParseUtils.readInt32Array(stream, this.playableSize);
		this.flags = ParseUtils.readUInt32(stream);
		this.tileset = (char) stream.read();
		this.campaignBackground = stream.readInt();

		if (this.version > 24) {
			this.loadingScreenModel = ParseUtils.readUntilNull(stream);
		}

		this.loadingScreenText = ParseUtils.readUntilNull(stream);
		this.loadingScreenTitle = ParseUtils.readUntilNull(stream);
		this.loadingScreenSubtitle = ParseUtils.readUntilNull(stream);
		this.gameDataSet = stream.readInt();

		if (this.version > 24) {
			this.prologueScreenModel = ParseUtils.readUntilNull(stream);
		}

		this.prologueScreenText = ParseUtils.readUntilNull(stream);
		this.prologueScreenTitle = ParseUtils.readUntilNull(stream);
		this.prologueScreenSubtitle = ParseUtils.readUntilNull(stream);

		if (this.version > 24) {
			this.useTerrainFog = stream.readInt();
			ParseUtils.readFloatArray(stream, this.fogHeight);
			this.fogDensity = stream.readFloat();
			ParseUtils.readUInt8Array(stream, this.fogColor);
			this.globalWeather = stream.readInt(); // TODO probably war3id, right?
			this.soundEnvironment = ParseUtils.readUntilNull(stream);
			this.lightEnvironmentTileset = (char) stream.read();
			ParseUtils.readUInt8Array(stream, this.waterVertexColor);
		}

		if (this.version > 27) {
			ParseUtils.readUInt8Array(stream, this.unknown2ProbablyLua);
		}
		if (this.version > 30) {
			this.supportedModes = ParseUtils.readUInt32(stream);
			this.gameDataVersion = ParseUtils.readUInt32(stream);
		}
		else {
			this.gameDataVersion = -1; // indicate to the outside that this was unspecified
		}

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final Player player = new Player();

			player.load(stream, this.version);

			this.players.add(player);
		}

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final Force force = new Force();

			force.load(stream);

			this.forces.add(force);
		}

		if (stream.available() == 1) {
			// some kind of really stupid protected map???
			return;
		}
		if (stream.available() > 0) {
			for (int i = 0, l = stream.readInt(); i < l; i++) {
				final UpgradeAvailabilityChange upgradeAvailabilityChange = new UpgradeAvailabilityChange();

				upgradeAvailabilityChange.load(stream);

				this.upgradeAvailabilityChanges.add(upgradeAvailabilityChange);
			}
		}

		if (stream.available() > 0) {
			for (int i = 0, l = stream.readInt(); i < l; i++) {
				final TechAvailabilityChange techAvailabilityChange = new TechAvailabilityChange();

				techAvailabilityChange.load(stream);

				this.techAvailabilityChanges.add(techAvailabilityChange);
			}
		}

		if (stream.available() > 0) {
			for (int i = 0, l = stream.readInt(); i < l; i++) {
				final RandomUnitTable randomUnitTable = new RandomUnitTable();

				randomUnitTable.load(stream);

				this.randomUnitTables.add(randomUnitTable);
			}
		}

		if (this.version > 24) {
			if (stream.available() > 0) {
				for (int i = 0, l = stream.readInt(); i < l; i++) {
					final RandomItemTable randomItemTable = new RandomItemTable();

					randomItemTable.load(stream);

					this.randomItemTables.add(randomItemTable);
				}
			}
		}
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt(this.version);
		stream.writeInt(this.saves);
		stream.writeInt(this.editorVersion);

		if (this.version > 27) {
			stream.writeInt(this.gameVersionMajor);
			stream.writeInt(this.gameVersionMinor);
			stream.writeInt(this.gameVersionPatch);
			stream.writeInt(this.gameVersionBuild);
		}

		ParseUtils.writeWithNullTerminator(stream, this.name);
		ParseUtils.writeWithNullTerminator(stream, this.author);
		ParseUtils.writeWithNullTerminator(stream, this.description);
		ParseUtils.writeWithNullTerminator(stream, this.recommendedPlayers);
		ParseUtils.writeFloatArray(stream, this.cameraBounds);
		ParseUtils.writeInt32Array(stream, this.cameraBoundsComplements);
		ParseUtils.writeInt32Array(stream, this.playableSize);
		ParseUtils.writeUInt32(stream, this.flags);
		stream.write((byte) this.tileset);
		stream.writeInt(this.campaignBackground);

		if (this.version > 24) {
			ParseUtils.writeWithNullTerminator(stream, this.loadingScreenModel);
		}

		ParseUtils.writeWithNullTerminator(stream, this.loadingScreenText);
		ParseUtils.writeWithNullTerminator(stream, this.loadingScreenTitle);
		ParseUtils.writeWithNullTerminator(stream, this.loadingScreenSubtitle);
		stream.writeInt(this.gameDataSet);

		if (this.version > 24) {
			ParseUtils.writeWithNullTerminator(stream, this.prologueScreenModel);
		}

		ParseUtils.writeWithNullTerminator(stream, this.prologueScreenText);
		ParseUtils.writeWithNullTerminator(stream, this.prologueScreenTitle);
		ParseUtils.writeWithNullTerminator(stream, this.prologueScreenSubtitle);

		if (this.version > 24) {
			stream.writeInt(this.useTerrainFog);
			ParseUtils.writeFloatArray(stream, this.fogHeight);
			stream.writeFloat(this.fogDensity);
			ParseUtils.writeUInt8Array(stream, this.fogColor);
			stream.writeInt(this.globalWeather); // TODO War3ID???
			ParseUtils.writeWithNullTerminator(stream, this.soundEnvironment);
			stream.write((byte) this.lightEnvironmentTileset);
			ParseUtils.writeUInt8Array(stream, this.waterVertexColor);
		}

		if (this.version > 27) {
			ParseUtils.writeUInt8Array(stream, this.unknown2ProbablyLua);
		}

		if (this.version > 30) {
			ParseUtils.writeUInt32(stream, this.supportedModes);
			ParseUtils.writeUInt32(stream, this.gameDataVersion);
		}

		ParseUtils.writeUInt32(stream, this.players.size());

		for (final Player player : this.players) {
			player.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.forces.size());

		for (final Force force : this.forces) {
			force.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.upgradeAvailabilityChanges.size());

		for (final UpgradeAvailabilityChange change : this.upgradeAvailabilityChanges) {
			change.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.techAvailabilityChanges.size());

		for (final TechAvailabilityChange change : this.techAvailabilityChanges) {
			change.save(stream);
		}

		ParseUtils.writeUInt32(stream, this.randomUnitTables.size());

		for (final RandomUnitTable table : this.randomUnitTables) {
			table.save(stream);
		}

		if (this.version > 24) {
			ParseUtils.writeUInt32(stream, this.randomItemTables.size());

			for (final RandomItemTable table : this.randomItemTables) {
				table.save(stream);
			}
		}

	}

	public int getByteLength() {
		int size = 111 + this.name.length() + this.author.length() + this.description.length()
				+ this.recommendedPlayers.length() + this.loadingScreenText.length() + this.loadingScreenTitle.length()
				+ this.loadingScreenSubtitle.length() + this.prologueScreenText.length()
				+ this.prologueScreenTitle.length() + this.prologueScreenSubtitle.length();

		for (final Player player : this.players) {
			size += player.getByteLength();
		}

		for (final Force force : this.forces) {
			size += force.getByteLength();
		}

		size += this.upgradeAvailabilityChanges.size() * 16;

		size += this.techAvailabilityChanges.size() * 8;

		for (final RandomUnitTable table : this.randomUnitTables) {
			size += table.getByteLength();
		}

		if (this.version > 24) {
			size += 36 + this.loadingScreenModel.length() + this.prologueScreenModel.length()
					+ this.soundEnvironment.length();

			for (final RandomItemTable table : this.randomItemTables) {
				size += table.getByteLength();
			}
		}

		return size;
	}

	public int getVersion() {
		return this.version;
	}

	public int getSaves() {
		return this.saves;
	}

	public int getEditorVersion() {
		return this.editorVersion;
	}

	public String getName() {
		return this.name;
	}

	public String getAuthor() {
		return this.author;
	}

	public String getDescription() {
		return this.description;
	}

	public String getRecommendedPlayers() {
		return this.recommendedPlayers;
	}

	public float[] getCameraBounds() {
		return this.cameraBounds;
	}

	public int[] getCameraBoundsComplements() {
		return this.cameraBoundsComplements;
	}

	public int[] getPlayableSize() {
		return this.playableSize;
	}

	public long getFlags() {
		return this.flags;
	}

	public char getTileset() {
		return this.tileset;
	}

	public int getCampaignBackground() {
		return this.campaignBackground;
	}

	public String getLoadingScreenModel() {
		return this.loadingScreenModel;
	}

	public String getLoadingScreenText() {
		return this.loadingScreenText;
	}

	public String getLoadingScreenTitle() {
		return this.loadingScreenTitle;
	}

	public String getLoadingScreenSubtitle() {
		return this.loadingScreenSubtitle;
	}

	public int getGameDataSet() {
		return this.gameDataSet;
	}

	public void setGameDataSet(final int gameDataSet) {
		this.gameDataSet = gameDataSet;
	}

	public String getPrologueScreenModel() {
		return this.prologueScreenModel;
	}

	public String getPrologueScreenText() {
		return this.prologueScreenText;
	}

	public String getPrologueScreenTitle() {
		return this.prologueScreenTitle;
	}

	public String getPrologueScreenSubtitle() {
		return this.prologueScreenSubtitle;
	}

	public int getUseTerrainFog() {
		return this.useTerrainFog;
	}

	public float[] getFogHeight() {
		return this.fogHeight;
	}

	public float getFogDensity() {
		return this.fogDensity;
	}

	public short[] getFogColor() {
		return this.fogColor;
	}

	public int getGlobalWeather() {
		return this.globalWeather;
	}

	public String getSoundEnvironment() {
		return this.soundEnvironment;
	}

	public char getLightEnvironmentTileset() {
		return this.lightEnvironmentTileset;
	}

	public short[] getWaterVertexColor() {
		return this.waterVertexColor;
	}

	public short[] getUnknown2() {
		return this.unknown2ProbablyLua;
	}

	public long getSupportedModes() {
		return this.supportedModes;
	}

	public long getGameDataVersion() {
		return this.gameDataVersion;
	}

	public void setSupportedModes(final long supportedModes) {
		this.supportedModes = supportedModes;
	}

	public void setGameDataVersion(final long gameDataVersion) {
		this.gameDataVersion = gameDataVersion;
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	public List<Force> getForces() {
		return this.forces;
	}

	public List<UpgradeAvailabilityChange> getUpgradeAvailabilityChanges() {
		return this.upgradeAvailabilityChanges;
	}

	public List<TechAvailabilityChange> getTechAvailabilityChanges() {
		return this.techAvailabilityChanges;
	}

	public List<RandomUnitTable> getRandomUnitTables() {
		return this.randomUnitTables;
	}

	public List<RandomItemTable> getRandomItemTables() {
		return this.randomItemTables;
	}

	public int getGameVersionMajor() {
		return this.gameVersionMajor;
	}

	public int getGameVersionMinor() {
		return this.gameVersionMinor;
	}

	public boolean hasFlag(final int mapFlag) {
		return (this.flags & mapFlag) != 0;
	}

	public short[] getUnknown2ProbablyLua() {
		return this.unknown2ProbablyLua;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public void setSaves(final int saves) {
		this.saves = saves;
	}

	public void setEditorVersion(final int editorVersion) {
		this.editorVersion = editorVersion;
	}

	public void setGameVersionMajor(final int gameVersionMajor) {
		this.gameVersionMajor = gameVersionMajor;
	}

	public void setGameVersionMinor(final int gameVersionMinor) {
		this.gameVersionMinor = gameVersionMinor;
	}

	public void setGameVersionPatch(final int gameVersionPatch) {
		this.gameVersionPatch = gameVersionPatch;
	}

	public void setGameVersionBuild(final int gameVersionBuild) {
		this.gameVersionBuild = gameVersionBuild;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setRecommendedPlayers(final String recommendedPlayers) {
		this.recommendedPlayers = recommendedPlayers;
	}

	public void setFlags(final long flags) {
		this.flags = flags;
	}

	public void setTileset(final char tileset) {
		this.tileset = tileset;
	}

	public void setCampaignBackground(final int campaignBackground) {
		this.campaignBackground = campaignBackground;
	}

	public void setLoadingScreenModel(final String loadingScreenModel) {
		this.loadingScreenModel = loadingScreenModel;
	}

	public void setLoadingScreenText(final String loadingScreenText) {
		this.loadingScreenText = loadingScreenText;
	}

	public void setLoadingScreenTitle(final String loadingScreenTitle) {
		this.loadingScreenTitle = loadingScreenTitle;
	}

	public void setLoadingScreenSubtitle(final String loadingScreenSubtitle) {
		this.loadingScreenSubtitle = loadingScreenSubtitle;
	}

	public void setPrologueScreenModel(final String prologueScreenModel) {
		this.prologueScreenModel = prologueScreenModel;
	}

	public void setPrologueScreenText(final String prologueScreenText) {
		this.prologueScreenText = prologueScreenText;
	}

	public void setPrologueScreenTitle(final String prologueScreenTitle) {
		this.prologueScreenTitle = prologueScreenTitle;
	}

	public void setPrologueScreenSubtitle(final String prologueScreenSubtitle) {
		this.prologueScreenSubtitle = prologueScreenSubtitle;
	}

	public void setUseTerrainFog(final int useTerrainFog) {
		this.useTerrainFog = useTerrainFog;
	}

	public void setFogDensity(final float fogDensity) {
		this.fogDensity = fogDensity;
	}

	public void setGlobalWeather(final int globalWeather) {
		this.globalWeather = globalWeather;
	}

	public void setSoundEnvironment(final String soundEnvironment) {
		this.soundEnvironment = soundEnvironment;
	}

	public void setLightEnvironmentTileset(final char lightEnvironmentTileset) {
		this.lightEnvironmentTileset = lightEnvironmentTileset;
	}

	public void generateDefaultEmpty() {
		this.version = 26; // 26 is a good version
		this.saves = 1;
		this.editorVersion = 1337; // might need to change this

		this.name = "Just another Warsmash map";
		this.author = "Unknown";
		this.description = "A dynamically generated map or something";
		this.recommendedPlayers = "All";

		this.cameraBounds[0] = -17066.656f;
		this.cameraBounds[1] = -17066.656f;
		this.cameraBounds[2] = 17066.656f;
		this.cameraBounds[3] = 17066.656f;
		this.cameraBoundsComplements[0] = 0;
		this.cameraBoundsComplements[1] = 0;
		this.cameraBoundsComplements[2] = 0;
		this.cameraBoundsComplements[3] = 0;
		this.playableSize[0] = 134;
		this.playableSize[1] = 134;

		this.flags = War3MapW3iFlags.SHOW_WATER_WAVES_ON_CLIFF_SHORES
				| War3MapW3iFlags.SHOW_WATER_WAVES_ON_CLIFF_SHORES;
		this.tileset = 'A'; // some default, idk
		this.campaignBackground = 0;

		this.loadingScreenModel = "";

		this.loadingScreenText = "Generated from some WDT file or something.";
		this.loadingScreenTitle = "Just another Warcraft III map";
		this.loadingScreenSubtitle = "Generated map";
		this.gameDataSet = 1; // custom

		this.prologueScreenModel = "";

		this.prologueScreenText = "";
		this.prologueScreenTitle = "";
		this.prologueScreenSubtitle = "";

		this.useTerrainFog = 0; // disable for now
//			ParseUtils.readFloatArray(stream, this.fogHeight);
		this.fogDensity = 0;
//			ParseUtils.readUInt8Array(stream, this.fogColor);
		this.globalWeather = 0; //// TODO probably war3id, right?
		this.soundEnvironment = "";
		this.lightEnvironmentTileset = 'A';
		Arrays.fill(this.waterVertexColor, (short) 255);

		this.gameDataVersion = -1; // indicate to the outside that this was unspecified
	}
}

package com.etheller.warsmash.viewer5.handlers.mdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.etheller.warsmash.common.FetchDataTypeName;
import com.etheller.warsmash.common.LoadGenericCallback;
import com.etheller.warsmash.util.MappedData;
import com.etheller.warsmash.util.MappedDataRow;
import com.etheller.warsmash.viewer5.GenericResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.hiveworkshop.rms.parsers.mdlx.MdlxEventObject;
import com.hiveworkshop.rms.parsers.mdlx.MdlxParticleEmitter2;

public class EventObjectEmitterObject extends GenericObject implements EmitterObject {
	private static final class LoadGenericSoundCallback implements LoadGenericCallback {
		private final String filename;

		public LoadGenericSoundCallback(final String filename) {
			this.filename = filename;
		}

		@Override
		public Object call(final InputStream data) {
			final FileHandle temp = new FileHandle(this.filename) {
				@Override
				public InputStream read() {
					return data;
				}

				;
			};
			if (data != null) {
				return Gdx.audio.newSound(temp);
			}
			else {
				System.err.println("Warning: missing sound file: " + this.filename);
				return null;
			}
		}
	}

	private static final LoadGenericCallback mappedDataCallback = new LoadGenericCallback() {

		@Override
		public Object call(final InputStream data) {
			if (data == null) {
				return new MappedData();
			}
			final StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(data, "utf-8"))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append("\n");
				}
			}
			catch (final UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			return new MappedData(stringBuilder.toString());
		}
	};

	private int geometryEmitterType = -1;
	public final String type;
	private final String id;
	public final long[] keyFrames;
	private long globalSequence = -1;
	private final long[] defval = { 1 };
	public MdxModel internalModel;
	public Texture internalTexture;
	public float[][] colors;
	public float[] intervalTimes;
	public float scale;
	public int columns;
	public int rows;
	public float lifeSpan;
	public int blendSrc;
	public int blendDst;
	public float[][] intervals;
	public float distanceCutoff;
	private float maxDistance;
	public float minDistance;
	public float pitch;
	public float pitchVariance;
	public float volume;
	public boolean footprint;
	public List<Sound> decodedBuffers = new ArrayList<>();
	/**
	 * If this is an SPL/UBR emitter object, ok will be set to true if the tables
	 * are loaded.
	 * <p>
	 * This is because, like the other geometry emitters, it is fine to use them
	 * even if the textures don't load.
	 * <p>
	 * The particles will simply be black.
	 */
	private boolean ok = false;

	public EventObjectEmitterObject(final MdxModel model, final MdlxEventObject eventObject, final int index) {
		super(model, eventObject, index);

		final ModelViewer viewer = model.viewer;
		final String name = eventObject.getName();
		final String type = name.substring(0, 3);
		final String id = name.substring(4);

		// Same thing
		if ("FPT".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_SPLAT;
		}
		else if ("SPL".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_SPLAT;
		}
		else if ("UBR".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_UBERSPLAT;
		}
		else if ("SPN".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_SPN;
		}

		this.type = type;
		this.id = id;
		this.keyFrames = eventObject.getKeyFrames();

		final int globalSequenceId = eventObject.getGlobalSequenceId();
		if (globalSequenceId != -1) {
			this.globalSequence = model.getGlobalSequences().get(globalSequenceId);
		}

		final List<GenericResource> tables = new ArrayList<>();
		final PathSolver pathSolver = model.pathSolver;
		final Object solverParams = model.solverParams;

		if ("SPN".equals(type)) {
			tables.add(viewer.loadGeneric(pathSolver.solve("Splats\\SpawnData.slk", solverParams).finalSrc,
					FetchDataTypeName.SLK, mappedDataCallback));
		}
		else if ("SPL".equals(type)) {
			tables.add(viewer.loadGeneric(pathSolver.solve("Splats\\SplatData.slk", solverParams).finalSrc,
					FetchDataTypeName.SLK, mappedDataCallback));
		}
		else if ("FPT".equals(type)) {
			tables.add(viewer.loadGeneric(pathSolver.solve("Splats\\SplatData.slk", solverParams).finalSrc,
					FetchDataTypeName.SLK, mappedDataCallback));
		}
		else if ("UBR".equals(type)) {
			tables.add(viewer.loadGeneric(pathSolver.solve("Splats\\UberSplatData.slk", solverParams).finalSrc,
					FetchDataTypeName.SLK, mappedDataCallback));
		}
		else if ("SND".equals(type)) {
			if (!model.reforged) {
				tables.add(viewer.loadGeneric(pathSolver.solve("UI\\SoundInfo\\AnimLookups.slk", solverParams).finalSrc,
						FetchDataTypeName.SLK, mappedDataCallback));
				tables.add(viewer.loadGeneric(pathSolver.solve("UI\\SoundInfo\\AnimSounds.slk", solverParams).finalSrc,
						FetchDataTypeName.SLK, mappedDataCallback));
			}
			else {
				tables.add(viewer.loadGeneric(pathSolver.solve("UI\\SoundInfo\\AnimSounds.slk", solverParams).finalSrc,
						FetchDataTypeName.SLK, mappedDataCallback));
			}
		}
		else {
			// Units\Critters\BlackStagMale\BlackStagMale.mdx has an event object named
			// "Point01".
			return;
		}

		// TODO I am scrapping some async stuff with promises here from the JS and
		// calling load
		load(tables);
	}

	private float getFloat(final MappedDataRow row, final String name) {
		final Float x = (Float) row.get(name);
		if (x == null) {
			return Float.NaN;
		}
		else {
			return x.floatValue();
		}
	}

	private int getInt(final MappedDataRow row, final String name) {
		return getInt(row, name, Integer.MIN_VALUE);
	}

	private int getInt(final MappedDataRow row, final String name, final int defaultValue) {
		final Number x = (Number) row.get(name);
		if (x == null) {
			return defaultValue;
		}
		else {
			return x.intValue();
		}
	}

	private void load(final List<GenericResource> tables) {
		final MappedData firstTable = (MappedData) tables.get(0).data;
		if (firstTable == null) {
			return;
		}
		final MappedDataRow row = firstTable.getRow(this.id.trim());

		if (row != null) {
			final MdxModel model = this.model;
			final ModelViewer viewer = model.viewer;
			final PathSolver pathSolver = model.pathSolver;

			if ("SPN".equals(this.type)) {
				this.internalModel = War3MapViewer.loadModelMdx(viewer.dataSource, viewer, (String) row.get("Model"),
						pathSolver, model.solverParams);

				if (this.internalModel != null) {
					// TODO javascript async code removed here
//					this.internalModel.whenLoaded((model) => this.ok = model.ok)
					this.ok = this.internalModel.ok;
				}
			}
			else if ("SPL".equals(this.type) || "FPT".equals(this.type) || "UBR".equals(this.type)) {
				final String texturesExt = (model.reforged && false) ? ".dds" : ".blp";

				this.internalTexture = (Texture) viewer.load(row.get("Dir") + "\\" + row.get("file") + texturesExt,
						pathSolver, model.solverParams);

				this.scale = getFloat(row, "Scale");
				this.colors = new float[][] {
						{ getFloat(row, "StartR"), getFloat(row, "StartG"), getFloat(row, "StartB"),
								getFloat(row, "StartA") },
						{ getFloat(row, "MiddleR"), getFloat(row, "MiddleG"), getFloat(row, "MiddleB"),
								getFloat(row, "MiddleA") },
						{ getFloat(row, "EndR"), getFloat(row, "EndG"), getFloat(row, "EndB"),
								getFloat(row, "EndA") } };

				if ("SPL".equals(this.type) || "FPT".equals(this.type)) {
					this.footprint = "FPT".equals(this.type);
					this.columns = getInt(row, "Columns");
					this.rows = getInt(row, "Rows");
					this.lifeSpan = getFloat(row, "Lifespan") + getFloat(row, "Decay");
					this.intervalTimes = new float[] { getFloat(row, "Lifespan"), getFloat(row, "Decay") };
					this.intervals = new float[][] {
							{ getFloat(row, "UVLifespanStart"), getFloat(row, "UVLifespanEnd"),
									getFloat(row, "LifespanRepeat") },
							{ getFloat(row, "UVDecayStart"), getFloat(row, "UVDecayEnd"),
									getFloat(row, "DecayRepeat") }, };
				}
				else {
					this.columns = 1;
					this.rows = 1;
					this.lifeSpan = getFloat(row, "BirthTime") + getFloat(row, "PauseTime") + getFloat(row, "Decay");
					this.intervalTimes = new float[] { getFloat(row, "BirthTime"), getFloat(row, "PauseTime"),
							getFloat(row, "Decay") };
				}

				final int[] blendModes = FilterMode
						.emitterFilterMode(MdlxParticleEmitter2.FilterMode.fromId(getInt(row, "BlendMode")));

				this.blendSrc = blendModes[0];
				this.blendDst = blendModes[1];

				this.ok = true;
			}
			else if ("SND".equals(this.type)) {
				// Only load sounds if audio is enabled.
				// This is mostly to save on bandwidth and loading time, especially when loading
				// full maps.
				if (viewer.audioEnabled) {

					final MappedDataRow animSoundsRow;
					if (model.reforged) {
						animSoundsRow = row;
					}
					else {
						final MappedData animSounds = (MappedData) tables.get(1).data;
						animSoundsRow = animSounds.getRow((String) row.get("SoundLabel"));
					}

					if (animSoundsRow != null) {
						this.distanceCutoff = getFloat(animSoundsRow, "DistanceCutoff");
						this.maxDistance = getFloat(animSoundsRow, "MaxDistance");
						this.minDistance = getFloat(animSoundsRow, "MinDistance");
						this.pitch = getFloat(animSoundsRow, "Pitch");
						this.pitchVariance = getFloat(animSoundsRow, "PitchVariance");
						this.volume = getFloat(animSoundsRow, "Volume") / 127f;

						final String[] fileNames = ((String) animSoundsRow.get("FileNames")).split(",");
						final GenericResource[] resources = new GenericResource[fileNames.length];
						for (int i = 0; i < fileNames.length; i++) {
							final String directoryBase = (String) animSoundsRow.get("DirectoryBase");
							String path;
							if (directoryBase != null) {
								path = (directoryBase) + fileNames[i];
							}
							else {
								path = fileNames[i];
							}
							try {
								final String pathString = pathSolver.solve(path, model.solverParams).finalSrc;
								final GenericResource genericResource = viewer.loadGeneric(pathString,
										FetchDataTypeName.ARRAY_BUFFER, new LoadGenericSoundCallback(pathString));
								if (genericResource == null) {
									System.err.println("Null sound: " + fileNames[i]);
								}
								resources[i] = genericResource;
							}
							catch (final Exception exc) {
								System.err.println("Failed to load sound: " + path);
								exc.printStackTrace();
							}
						}

						// TODO JS async removed
						for (final GenericResource resource : resources) {
							if (resource != null) {
								this.decodedBuffers.add((Sound) resource.data);
							}
						}
						this.ok = true;
					}
				}
			}
			else {
				System.err.println("Unknown event object type: " + this.type + this.id);
			}
		}
		else {
			System.err.println("Unknown event object ID: " + this.type + this.id);
		}
	}

	public int getValue(final long[] out, final MdxComplexInstance instance) {
		if (this.globalSequence != -1) {

			return getValueAtTime(out, instance.counter % this.globalSequence, 0, this.globalSequence);
		}
		else if (instance.sequence != -1) {
			final long[] interval = this.model.getSequences().get(instance.sequence).getInterval();

			return getValueAtTime(out, instance.frame, interval[0], interval[1]);
		}
		else {
			out[0] = this.defval[0];

			return -1;
		}
	}

	public int getValueAtTime(final long[] out, final long frame, final long start, final long end) {
		if ((frame >= start) && (frame <= end)) {
			for (int i = this.keyFrames.length - 1; i > -1; i--) {
				if (this.keyFrames[i] < start) {
					out[0] = 0;

					return -1;
				}
				else if (this.keyFrames[i] <= frame) {
					out[0] = 1;

					return i;
				}
			}
		}

		out[0] = 0;

		return -1;
	}

	@Override
	public boolean ok() {
		return this.ok;
	}

	@Override
	public int getGeometryEmitterType() {
		return this.geometryEmitterType;
	}
}

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

public abstract class EventObjectEmitterObject extends GenericObject implements EmitterObject {
	private static final LoadGenericCallback mappedDataCallback = new LoadGenericCallback() {

		@Override
		public Object call(final InputStream data) {
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
	private static final LoadGenericCallback decodedDataCallback = new LoadGenericCallback() {
		@Override
		public Object call(final InputStream data) {
			final FileHandle temp = new FileHandle("sound.wav") {
				@Override
				public InputStream read() {
					return data;
				};
			};
			return Gdx.audio.newSound(temp);
		}
	};

	private int geometryEmitterType = -1;
	private final String type;
	private final String id;
	private final long[] keyFrames;
	private long globalSequence;
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
	private float pitch;
	private float pitchVariance;
	private float volume;
	public List<Sound> decodedBuffers;
	/**
	 * If this is an SPL/UBR emitter object, ok will be set to true if the tables
	 * are loaded.
	 *
	 * This is because, like the other geometry emitters, it is fine to use them
	 * even if the textures don't load.
	 *
	 * The particles will simply be black.
	 */
	private boolean ok = false;

	public EventObjectEmitterObject(final MdxModel model,
			final com.etheller.warsmash.parsers.mdlx.EventObject eventObject, final int index) {
		super(model, eventObject, index);

		final ModelViewer viewer = model.viewer;
		final String name = eventObject.getName();
		String type = name.substring(0, 3);
		final String id = name.substring(4);

		// Same thing
		if ("FPT".equals(type)) {
			type = "SPL";
		}

		if ("SPL".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_SPLAT;
		}
		else if ("UBR".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_UBERSPLAT;
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
			tables.add(viewer.loadGeneric(pathSolver.solve("Splats\\SpawnData.slk", solverParams).finalSrc,
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
			}
			tables.add(viewer.loadGeneric(pathSolver.solve("UI\\SoundInfo\\AnimSounds.slk", solverParams).finalSrc,
					FetchDataTypeName.SLK, mappedDataCallback));
		}
		else {
			// Units\Critters\BlackStagMale\BlackStagMale.mdx has an event object named
			// "Point01".
			return;
		}

		// TODO I am scrapping some async stuff with promises here from the JS and
		// calling load
		this.load(tables);
	}

	private void load(final List<GenericResource> tables) {
		final MappedData firstTable = (MappedData) tables.get(0).data;
		final MappedDataRow row = firstTable.getRow(this.id);

		if (row != null) {
			final MdxModel model = this.model;
			final ModelViewer viewer = model.viewer;
			final PathSolver pathSolver = model.pathSolver;

			if ("SPN".equals(this.type)) {
				this.internalModel = (MdxModel) viewer.load(((String) row.get("Model")).replace(".mdl", ".mdx"),
						pathSolver, model.solverParams);

				if (this.internalModel != null) {
					// TODO javascript async code removed here
//					this.internalModel.whenLoaded((model) => this.ok = model.ok)
					this.ok = this.internalModel.ok;
				}
			}
			else if ("SPL".equals(this.type) || "UBR".equals(this.type)) {
				final String texturesExt = model.reforged ? ".dds" : ".blp";

				this.internalTexture = (Texture) viewer.load(
						"ReplaceableTextures\\Splats\\" + row.get("file") + texturesExt, pathSolver,
						model.solverParams);

				this.scale = (Float) row.get("Scale");
				this.colors = new float[][] {
						{ ((Float) row.get("StartR")).floatValue(), ((Float) row.get("StartG")).floatValue(),
								((Float) row.get("StartB")).floatValue(), ((Float) row.get("StartA")).floatValue() },
						{ ((Float) row.get("MiddleR")).floatValue(), ((Float) row.get("MiddleG")).floatValue(),
								((Float) row.get("MiddleB")).floatValue(), ((Float) row.get("MiddleA")).floatValue() },
						{ ((Float) row.get("EndR")).floatValue(), ((Float) row.get("EndG")).floatValue(),
								((Float) row.get("EndB")).floatValue(), ((Float) row.get("EndA")).floatValue() } };

				if ("SPL".equals(this.type)) {
					this.columns = ((Number) row.get("Columns")).intValue();
					this.rows = ((Number) row.get("Rows")).intValue();
					this.lifeSpan = ((Number) row.get("Lifespan")).floatValue()
							+ ((Number) row.get("Decay")).floatValue();
					this.intervals = new float[][] {
							{ ((Float) row.get("UVLifespanStart")).floatValue(),
									((Float) row.get("UVLifespanEnd")).floatValue(),
									((Float) row.get("LifespanRepeat")).floatValue() },
							{ ((Float) row.get("UVDecayStart")).floatValue(),
									((Float) row.get("UVDecayEnd")).floatValue(),
									((Float) row.get("DecayRepeat")).floatValue() }, };
				}
				else {
					this.columns = 1;
					this.rows = 1;
					this.lifeSpan = ((Number) row.get("BirthTime")).floatValue()
							+ ((Number) row.get("PauseTime")).floatValue() + ((Number) row.get("Decay")).floatValue();
					this.intervalTimes = new float[] { ((Number) row.get("BirthTime")).floatValue(),
							((Number) row.get("PauseTime")).floatValue(), ((Number) row.get("Decay")).floatValue() };
				}

				final int[] blendModes = FilterMode
						.emitterFilterMode(com.etheller.warsmash.parsers.mdlx.ParticleEmitter2.FilterMode
								.fromId(((Number) row.get("BlendMode")).intValue()));

				this.blendSrc = blendModes[0];
				this.blendDst = blendModes[1];

				this.ok = true;
			}
			else if ("SND".equals(this.type)) {
				// Only load sounds if audio is enabled.
				// This is mostly to save on bandwidth and loading time, especially when loading
				// full maps.
				if (viewer.audioEnabled) {
					final MappedData animSounds = (MappedData) tables.get(1).data;

					final MappedDataRow animSoundsRow = animSounds.getRow((String) row.get("SoundLabel"));

					if (animSoundsRow != null) {
						this.distanceCutoff = ((Number) animSoundsRow.get("DistanceCutoff")).floatValue();
						this.maxDistance = ((Number) animSoundsRow.get("MaxDistance")).floatValue();
						this.minDistance = ((Number) animSoundsRow.get("MinDistance")).floatValue();
						this.pitch = ((Number) animSoundsRow.get("Pitch")).floatValue();
						this.pitchVariance = ((Number) animSoundsRow.get("PitchVariance")).floatValue();
						this.volume = ((Number) animSoundsRow.get("Volume")).floatValue();

						final String[] fileNames = ((String) animSoundsRow.get("FileNames")).split(",");
						final GenericResource[] resources = new GenericResource[fileNames.length];
						for (int i = 0; i < fileNames.length; i++) {
							resources[i] = viewer.loadGeneric(
									pathSolver.solve(((String) animSoundsRow.get("DirectoryBase")) + fileNames[i],
											model.solverParams).finalSrc,
									FetchDataTypeName.ARRAY_BUFFER, decodedDataCallback);
						}

						// TODO JS async removed
						for (final GenericResource resource : resources) {
							this.decodedBuffers.add((Sound) resource.data);
						}
						this.ok = true;
					}
				}
			}
			else {
				System.err.println("Unknown event object ID: " + this.type + this.id);
			}
		}
	}

	public int getValue(final long[] out, final MdxComplexInstance instance) {
		if (this.globalSequence != -1) {

			return this.getValueAtTime(out, instance.counter % this.globalSequence, 0, this.globalSequence);
		}
		else if (instance.sequence != -1) {
			final long[] interval = this.model.getSequences().get(instance.sequence).getInterval();

			return this.getValueAtTime(out, instance.frame, interval[0], interval[1]);
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

					return i;
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

}

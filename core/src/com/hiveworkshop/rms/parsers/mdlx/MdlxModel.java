package com.hiveworkshop.rms.parsers.mdlx;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

/**
 * A Warcraft 3 model. Supports loading from and saving to both the binary MDX
 * and text MDL file formats.
 */
public class MdlxModel {
	// Below, these can't call a function on a string to make their value
	// because
	// switch/case statements require the value to be compile-time defined in
	// order
	// to be legal, and it appears to only allow basic binary operators for
	// that.
	// I would love a clearer way to just type 'MDLX' in a character constant in
	// Java for this
	private static final int MDLX = ('M' << 24) | ('D' << 16) | ('L' << 8) | ('X');// War3ID.fromString("MDLX").getValue();
	private static final int VERS = ('V' << 24) | ('E' << 16) | ('R' << 8) | ('S');// War3ID.fromString("VERS").getValue();
	private static final int MODL = ('M' << 24) | ('O' << 16) | ('D' << 8) | ('L');// War3ID.fromString("MODL").getValue();
	private static final int SEQS = ('S' << 24) | ('E' << 16) | ('Q' << 8) | ('S');// War3ID.fromString("SEQS").getValue();
	private static final int GLBS = ('G' << 24) | ('L' << 16) | ('B' << 8) | ('S');// War3ID.fromString("GLBS").getValue();
	private static final int MTLS = ('M' << 24) | ('T' << 16) | ('L' << 8) | ('S');// War3ID.fromString("MTLS").getValue();
	private static final int TEXS = ('T' << 24) | ('E' << 16) | ('X' << 8) | ('S');// War3ID.fromString("TEXS").getValue();
	private static final int TXAN = ('T' << 24) | ('X' << 16) | ('A' << 8) | ('N');// War3ID.fromString("TXAN").getValue();
	private static final int GEOS = ('G' << 24) | ('E' << 16) | ('O' << 8) | ('S');// War3ID.fromString("GEOS").getValue();
	private static final int GEOA = ('G' << 24) | ('E' << 16) | ('O' << 8) | ('A');// War3ID.fromString("GEOA").getValue();
	private static final int BONE = ('B' << 24) | ('O' << 16) | ('N' << 8) | ('E');// War3ID.fromString("BONE").getValue();
	private static final int LITE = ('L' << 24) | ('I' << 16) | ('T' << 8) | ('E');// War3ID.fromString("LITE").getValue();
	private static final int HELP = ('H' << 24) | ('E' << 16) | ('L' << 8) | ('P');// War3ID.fromString("HELP").getValue();
	private static final int ATCH = ('A' << 24) | ('T' << 16) | ('C' << 8) | ('H');// War3ID.fromString("ATCH").getValue();
	private static final int PIVT = ('P' << 24) | ('I' << 16) | ('V' << 8) | ('T');// War3ID.fromString("PIVT").getValue();
	private static final int PREM = ('P' << 24) | ('R' << 16) | ('E' << 8) | ('M');// War3ID.fromString("PREM").getValue();
	private static final int PRE2 = ('P' << 24) | ('R' << 16) | ('E' << 8) | ('2');// War3ID.fromString("PRE2").getValue();
	private static final int CORN = ('C' << 24) | ('O' << 16) | ('R' << 8) | ('N');// War3ID.fromString("CORN").getValue();
	private static final int RIBB = ('R' << 24) | ('I' << 16) | ('B' << 8) | ('B');// War3ID.fromString("RIBB").getValue();
	private static final int CAMS = ('C' << 24) | ('A' << 16) | ('M' << 8) | ('S');// War3ID.fromString("CAMS").getValue();
	private static final int EVTS = ('E' << 24) | ('V' << 16) | ('T' << 8) | ('S');// War3ID.fromString("EVTS").getValue();
	private static final int CLID = ('C' << 24) | ('L' << 16) | ('I' << 8) | ('D');// War3ID.fromString("CLID").getValue();
	private static final int FAFX = ('F' << 24) | ('A' << 16) | ('F' << 8) | ('X');// War3ID.fromString("FAFX").getValue();
	private static final int BPOS = ('B' << 24) | ('P' << 16) | ('O' << 8) | ('S');// War3ID.fromString("BPOS").getValue();

	public int version = 800;
	public String name = "";
	/**
	 * (Comment copied from Ghostwolf JS) To the best of my knowledge, this should
	 * always be left empty. This is probably a leftover from the Warcraft 3 beta.
	 * (WS game note: No, I never saw any animation files in the RoC 2001-2002 Beta.
	 * So it must be from the Alpha)
	 *
	 * @member {string}
	 */
	public String animationFile = "";
	public MdlxExtent extent = new MdlxExtent();
	public long blendTime = 0;
	public List<MdlxSequence> sequences = new ArrayList<>();
	public List<Long /* UInt32 */> globalSequences = new ArrayList<>();
	public List<MdlxMaterial> materials = new ArrayList<>();
	public List<MdlxTexture> textures = new ArrayList<>();
	public List<MdlxTextureAnimation> textureAnimations = new ArrayList<>();
	public List<MdlxGeoset> geosets = new ArrayList<>();
	public List<MdlxGeosetAnimation> geosetAnimations = new ArrayList<>();
	public List<MdlxBone> bones = new ArrayList<>();
	public List<MdlxLight> lights = new ArrayList<>();
	public List<MdlxHelper> helpers = new ArrayList<>();
	public List<MdlxAttachment> attachments = new ArrayList<>();
	public List<float[]> pivotPoints = new ArrayList<>();
	public List<MdlxParticleEmitter> particleEmitters = new ArrayList<>();
	public List<MdlxParticleEmitter2> particleEmitters2 = new ArrayList<>();
	public List<MdlxParticleEmitterPopcorn> particleEmittersPopcorn = new ArrayList<>();
	public List<MdlxRibbonEmitter> ribbonEmitters = new ArrayList<>();
	public List<MdlxCamera> cameras = new ArrayList<>();
	public List<MdlxEventObject> eventObjects = new ArrayList<>();
	public List<MdlxCollisionShape> collisionShapes = new ArrayList<>();
	/**
	 * @since 900
	 */
	public List<MdlxFaceEffect> faceEffects = new ArrayList<>();
	/**
	 * @since 900
	 */
	public List<float[]> bindPose = new ArrayList<>();
	public List<MdlxUnknownChunk> unknownChunks = new ArrayList<>();

	public MdlxModel() {

	}

	public MdlxModel(final ByteBuffer buffer) {
		load(buffer);
	}

	public void load(final ByteBuffer buffer) {
		// MDX files start with "MDLX".
		if ((buffer.get(0) == 77) && (buffer.get(1) == 68) && (buffer.get(2) == 76) && (buffer.get(3) == 88)) {
			loadMdx(buffer);
		}
		else {
			loadMdl(buffer);
		}
	}

	public void loadMdx(final ByteBuffer buffer) {
		final BinaryReader reader = new BinaryReader(buffer);

		if (reader.readTag() != MDLX) {
			throw new IllegalStateException("WrongMagicNumber");
		}

		while (reader.remaining() > 0) {
			final int tag = reader.readTag();
			final int size = reader.readInt32();

			switch (tag) {
			case VERS:
				loadVersionChunk(reader);
				break;
			case MODL:
				loadModelChunk(reader);
				break;
			case SEQS:
				loadStaticObjects(this.sequences, MdlxBlockDescriptor.SEQUENCE, reader, size / 132);
				break;
			case GLBS:
				loadGlobalSequenceChunk(reader, size);
				break;
			case MTLS:
				loadDynamicObjects(this.materials, MdlxBlockDescriptor.MATERIAL, reader, size);
				break;
			case TEXS:
				loadStaticObjects(this.textures, MdlxBlockDescriptor.TEXTURE, reader, size / 268);
				break;
			case TXAN:
				loadDynamicObjects(this.textureAnimations, MdlxBlockDescriptor.TEXTURE_ANIMATION, reader, size);
				break;
			case GEOS:
				loadDynamicObjects(this.geosets, MdlxBlockDescriptor.GEOSET, reader, size);
				break;
			case GEOA:
				loadDynamicObjects(this.geosetAnimations, MdlxBlockDescriptor.GEOSET_ANIMATION, reader, size);
				break;
			case BONE:
				loadDynamicObjects(this.bones, MdlxBlockDescriptor.BONE, reader, size);
				break;
			case LITE:
				loadDynamicObjects(this.lights, MdlxBlockDescriptor.LIGHT, reader, size);
				break;
			case HELP:
				loadDynamicObjects(this.helpers, MdlxBlockDescriptor.HELPER, reader, size);
				break;
			case ATCH:
				loadDynamicObjects(this.attachments, MdlxBlockDescriptor.ATTACHMENT, reader, size);
				break;
			case PIVT:
				loadPivotPointChunk(reader, size);
				break;
			case PREM:
				loadDynamicObjects(this.particleEmitters, MdlxBlockDescriptor.PARTICLE_EMITTER, reader, size);
				break;
			case PRE2:
				loadDynamicObjects(this.particleEmitters2, MdlxBlockDescriptor.PARTICLE_EMITTER2, reader, size);
				break;
			case CORN:
				loadDynamicObjects(this.particleEmittersPopcorn, MdlxBlockDescriptor.PARTICLE_EMITTER_POPCORN, reader,
						size);
				break;
			case RIBB:
				loadDynamicObjects(this.ribbonEmitters, MdlxBlockDescriptor.RIBBON_EMITTER, reader, size);
				break;
			case CAMS:
				loadDynamicObjects(this.cameras, MdlxBlockDescriptor.CAMERA, reader, size);
				break;
			case EVTS:
				loadDynamicObjects(this.eventObjects, MdlxBlockDescriptor.EVENT_OBJECT, reader, size);
				break;
			case CLID:
				loadDynamicObjects(this.collisionShapes, MdlxBlockDescriptor.COLLISION_SHAPE, reader, size);
				break;
			case FAFX:
				loadStaticObjects(this.faceEffects, MdlxBlockDescriptor.FACE_EFFECT, reader, size / 340);
				break;
			case BPOS:
				loadBindPoseChunk(reader, size);
				break;
			default:
				this.unknownChunks.add(new MdlxUnknownChunk(reader, size, new War3ID(tag)));
				break;
			}
		}
	}

	private void loadVersionChunk(final BinaryReader reader) {
		this.version = reader.readInt32();
	}

	private void loadModelChunk(final BinaryReader reader) {
		this.name = reader.read(80);
		this.animationFile = reader.read(260);
		this.extent.readMdx(reader);
		this.blendTime = reader.readInt32();
	}

	private <E extends MdlxBlock> void loadStaticObjects(final List<E> out, final MdlxBlockDescriptor<E> constructor,
			final BinaryReader reader, final long count) {
		for (int i = 0; i < count; i++) {
			final E object = constructor.create();

			object.readMdx(reader, this.version);

			out.add(object);
		}
	}

	private void loadGlobalSequenceChunk(final BinaryReader reader, final long size) {
		for (long i = 0, l = size / 4; i < l; i++) {
			this.globalSequences.add(reader.readUInt32());
		}
	}

	private <E extends MdlxBlock & MdlxChunk> void loadDynamicObjects(final List<E> out,
			final MdlxBlockDescriptor<E> constructor, final BinaryReader reader, final long size) {
		long totalSize = 0;
		while (totalSize < size) {
			final E object = constructor.create();

			object.readMdx(reader, this.version);

			totalSize += object.getByteLength(this.version);

			out.add(object);
		}
	}

	private void loadPivotPointChunk(final BinaryReader reader, final long size) {
		for (long i = 0, l = size / 12; i < l; i++) {
			this.pivotPoints.add(reader.readFloat32Array(3));
		}
	}

	private void loadBindPoseChunk(final BinaryReader reader, final long size) {
		for (int i = 0, l = reader.readInt32(); i < l; i++) {
			this.bindPose.add(reader.readFloat32Array(12));
		}
	}

	public ByteBuffer saveMdx() {
		final BinaryWriter writer = new BinaryWriter(getByteLength());

		writer.writeTag(MDLX);
		saveVersionChunk(writer);
		saveModelChunk(writer);
		saveStaticObjectChunk(writer, SEQS, this.sequences, 132);
		saveGlobalSequenceChunk(writer);
		saveDynamicObjectChunk(writer, MTLS, this.materials);
		saveStaticObjectChunk(writer, TEXS, this.textures, 268);
		saveDynamicObjectChunk(writer, TXAN, this.textureAnimations);
		saveDynamicObjectChunk(writer, GEOS, this.geosets);
		saveDynamicObjectChunk(writer, GEOA, this.geosetAnimations);
		saveDynamicObjectChunk(writer, BONE, this.bones);
		saveDynamicObjectChunk(writer, LITE, this.lights);
		saveDynamicObjectChunk(writer, HELP, this.helpers);
		saveDynamicObjectChunk(writer, ATCH, this.attachments);
		savePivotPointChunk(writer);
		saveDynamicObjectChunk(writer, PREM, this.particleEmitters);
		saveDynamicObjectChunk(writer, PRE2, this.particleEmitters2);

		if (this.version > 800) {
			saveDynamicObjectChunk(writer, CORN, this.particleEmittersPopcorn);
		}

		saveDynamicObjectChunk(writer, RIBB, this.ribbonEmitters);
		saveDynamicObjectChunk(writer, CAMS, this.cameras);
		saveDynamicObjectChunk(writer, EVTS, this.eventObjects);
		saveDynamicObjectChunk(writer, CLID, this.collisionShapes);

		if (this.version > 800) {
			saveStaticObjectChunk(writer, FAFX, this.faceEffects, 340);
			saveBindPoseChunk(writer);
		}

		for (final MdlxUnknownChunk chunk : this.unknownChunks) {
			chunk.writeMdx(writer, this.version);
		}

		return writer.buffer;
	}

	private void saveVersionChunk(final BinaryWriter writer) {
		writer.writeTag(VERS);
		writer.writeUInt32(4);
		writer.writeUInt32(this.version);
	}

	private void saveModelChunk(final BinaryWriter writer) {
		writer.writeTag(MODL);
		writer.writeUInt32(372);
		writer.writeWithNulls(this.name, 80);
		writer.writeWithNulls(this.animationFile, 260);
		this.extent.writeMdx(writer);
		writer.writeUInt32(this.blendTime);
	}

	private <E extends MdlxBlock> void saveStaticObjectChunk(final BinaryWriter writer, final int name,
			final List<E> objects, final long size) {
		if (!objects.isEmpty()) {
			writer.writeTag(name);
			writer.writeUInt32(objects.size() * size);

			for (final E object : objects) {
				object.writeMdx(writer, this.version);
			}
		}
	}

	private void saveGlobalSequenceChunk(final BinaryWriter writer) {
		if (!this.globalSequences.isEmpty()) {
			writer.writeTag(GLBS);
			writer.writeUInt32(this.globalSequences.size() * 4);

			for (final Long globalSequence : this.globalSequences) {
				writer.writeUInt32(globalSequence);
			}
		}
	}

	private <E extends MdlxBlock & MdlxChunk> void saveDynamicObjectChunk(final BinaryWriter writer, final int name,
			final List<E> objects) {
		if (!objects.isEmpty()) {
			writer.writeTag(name);
			writer.writeUInt32(getObjectsByteLength(objects));

			for (final E object : objects) {
				object.writeMdx(writer, this.version);
			}
		}
	}

	private void savePivotPointChunk(final BinaryWriter writer) {
		if (this.pivotPoints.size() > 0) {
			writer.writeTag(PIVT);
			writer.writeUInt32(this.pivotPoints.size() * 12);

			for (final float[] pivotPoint : this.pivotPoints) {
				writer.writeFloat32Array(pivotPoint);
			}
		}
	}

	private void saveBindPoseChunk(final BinaryWriter writer) {
		if (this.bindPose.size() > 0) {
			writer.writeTag(BPOS);
			writer.writeUInt32(4 + (this.bindPose.size() * 48));
			writer.writeUInt32(this.bindPose.size());

			for (final float[] matrix : this.bindPose) {
				writer.writeFloat32Array(matrix);
			}
		}
	}

	public void loadMdl(final ByteBuffer buffer) {
		String token;
		final MdlTokenInputStream stream = new MdlTokenInputStream(buffer);

		while ((token = stream.read()) != null) {
			switch (token) {
			case MdlUtils.TOKEN_VERSION:
				loadVersionBlock(stream);
				break;
			case MdlUtils.TOKEN_MODEL:
				loadModelBlock(stream);
				break;
			case MdlUtils.TOKEN_SEQUENCES:
				loadNumberedObjectBlock(this.sequences, MdlxBlockDescriptor.SEQUENCE, MdlUtils.TOKEN_ANIM, stream);
				break;
			case MdlUtils.TOKEN_GLOBAL_SEQUENCES:
				loadGlobalSequenceBlock(stream);
				break;
			case MdlUtils.TOKEN_TEXTURES:
				loadNumberedObjectBlock(this.textures, MdlxBlockDescriptor.TEXTURE, MdlUtils.TOKEN_BITMAP, stream);
				break;
			case MdlUtils.TOKEN_MATERIALS:
				loadNumberedObjectBlock(this.materials, MdlxBlockDescriptor.MATERIAL, MdlUtils.TOKEN_MATERIAL, stream);
				break;
			case MdlUtils.TOKEN_TEXTURE_ANIMS:
				loadNumberedObjectBlock(this.textureAnimations, MdlxBlockDescriptor.TEXTURE_ANIMATION,
						MdlUtils.TOKEN_TVERTEX_ANIM, stream);
				break;
			case MdlUtils.TOKEN_GEOSET:
				loadObject(this.geosets, MdlxBlockDescriptor.GEOSET, stream);
				break;
			case MdlUtils.TOKEN_GEOSETANIM:
				loadObject(this.geosetAnimations, MdlxBlockDescriptor.GEOSET_ANIMATION, stream);
				break;
			case MdlUtils.TOKEN_BONE:
				loadObject(this.bones, MdlxBlockDescriptor.BONE, stream);
				break;
			case MdlUtils.TOKEN_LIGHT:
				loadObject(this.lights, MdlxBlockDescriptor.LIGHT, stream);
				break;
			case MdlUtils.TOKEN_HELPER:
				loadObject(this.helpers, MdlxBlockDescriptor.HELPER, stream);
				break;
			case MdlUtils.TOKEN_ATTACHMENT:
				loadObject(this.attachments, MdlxBlockDescriptor.ATTACHMENT, stream);
				break;
			case MdlUtils.TOKEN_PIVOT_POINTS:
				loadPivotPointBlock(stream);
				break;
			case MdlUtils.TOKEN_PARTICLE_EMITTER:
				loadObject(this.particleEmitters, MdlxBlockDescriptor.PARTICLE_EMITTER, stream);
				break;
			case MdlUtils.TOKEN_PARTICLE_EMITTER2:
				loadObject(this.particleEmitters2, MdlxBlockDescriptor.PARTICLE_EMITTER2, stream);
				break;
			case "ParticleEmitterPopcorn":
				loadObject(this.particleEmittersPopcorn, MdlxBlockDescriptor.PARTICLE_EMITTER_POPCORN, stream);
				break;
			case MdlUtils.TOKEN_RIBBON_EMITTER:
				loadObject(this.ribbonEmitters, MdlxBlockDescriptor.RIBBON_EMITTER, stream);
				break;
			case MdlUtils.TOKEN_CAMERA:
				loadObject(this.cameras, MdlxBlockDescriptor.CAMERA, stream);
				break;
			case MdlUtils.TOKEN_EVENT_OBJECT:
				loadObject(this.eventObjects, MdlxBlockDescriptor.EVENT_OBJECT, stream);
				break;
			case MdlUtils.TOKEN_COLLISION_SHAPE:
				loadObject(this.collisionShapes, MdlxBlockDescriptor.COLLISION_SHAPE, stream);
				break;
			case "FaceFX":
				loadObject(this.faceEffects, MdlxBlockDescriptor.FACE_EFFECT, stream);
				break;
			case "BindPose":
				loadBindPoseBlock(stream);
				break;
			default:
				throw new IllegalStateException("Unsupported block: " + token);
			}
		}
	}

	private void loadVersionBlock(final MdlTokenInputStream stream) {
		for (final String token : stream.readBlock()) {
			if (MdlUtils.TOKEN_FORMAT_VERSION.equals(token)) {
				this.version = stream.readInt();
			}
			else {
				throw new IllegalStateException("Unknown token in Version: " + token);
			}
		}
	}

	private void loadModelBlock(final MdlTokenInputStream stream) {
		this.name = stream.read();
		for (final String token : stream.readBlock()) {
			if (token.startsWith("Num")) {
				/*-
				 * Don't care about the number of things, the arrays will grow as they wish.
				 * This includes:
				 *      NumGeosets
				 *      NumGeosetAnims
				 *      NumHelpers
				 *      NumLights
				 *      NumBones
				 *      NumAttachments
				 *      NumParticleEmitters
				 *      NumParticleEmitters2
				 *      NumRibbonEmitters
				 *      NumEvents
				 */
				stream.read();
			}
			else {
				switch (token) {
				case MdlUtils.TOKEN_BLEND_TIME:
					this.blendTime = stream.readUInt32();
					break;
				case MdlUtils.TOKEN_MINIMUM_EXTENT:
					stream.readFloatArray(this.extent.min);
					break;
				case MdlUtils.TOKEN_MAXIMUM_EXTENT:
					stream.readFloatArray(this.extent.max);
					break;
				case MdlUtils.TOKEN_BOUNDSRADIUS:
					this.extent.boundsRadius = stream.readFloat();
					break;
				default:
					throw new IllegalStateException("Unknown token in Model: " + token);
				}
			}
		}
	}

	private <E extends MdlxBlock> void loadNumberedObjectBlock(final List<E> out,
			final MdlxBlockDescriptor<E> constructor, final String name, final MdlTokenInputStream stream) {
		stream.read(); // Don't care about the number, the array will grow

		for (final String token : stream.readBlock()) {
			if (token.equals(name)) {
				final E object = constructor.create();

				object.readMdl(stream, this.version);

				out.add(object);
			}
			else {
				throw new IllegalStateException("Unknown token in " + name + ": " + token);
			}
		}
	}

	private void loadGlobalSequenceBlock(final MdlTokenInputStream stream) {
		stream.read(); // Don't care about the number, the array will grow

		for (final String token : stream.readBlock()) {
			if (token.equals(MdlUtils.TOKEN_DURATION)) {
				this.globalSequences.add(stream.readUInt32());
			}
			else {
				throw new IllegalStateException("Unknown token in GlobalSequences: " + token);
			}
		}
	}

	private <E extends MdlxBlock> void loadObject(final List<E> out, final MdlxBlockDescriptor<E> descriptor,
			final MdlTokenInputStream stream) {
		final E object = descriptor.create();

		object.readMdl(stream, this.version);

		out.add(object);
	}

	private void loadPivotPointBlock(final MdlTokenInputStream stream) {
		final int count = stream.readInt();

		stream.read(); // {

		for (int i = 0; i < count; i++) {
			this.pivotPoints.add(stream.readFloatArray(new float[3]));
		}

		stream.read(); // }
	}

	private void loadBindPoseBlock(final MdlTokenInputStream stream) {
		for (final String token : stream.readBlock()) {
			if (token.equals("Matrices")) {
				final int matrices = stream.readInt();

				stream.read(); // {

				for (int i = 0; i < matrices; i++) {
					this.bindPose.add(stream.readFloatArray(new float[12]));
				}

				stream.read(); // }
			}
			else {
				throw new IllegalStateException("Unknown token in BindPose: " + token);
			}
		}
	}

	public ByteBuffer saveMdl() {
		final MdlTokenOutputStream stream = new MdlTokenOutputStream();

		saveVersionBlock(stream);
		saveModelBlock(stream);
		saveStaticObjectsBlock(stream, MdlUtils.TOKEN_SEQUENCES, this.sequences);
		saveGlobalSequenceBlock(stream);
		saveStaticObjectsBlock(stream, MdlUtils.TOKEN_TEXTURES, this.textures);
		saveStaticObjectsBlock(stream, MdlUtils.TOKEN_MATERIALS, this.materials);
		saveStaticObjectsBlock(stream, MdlUtils.TOKEN_TEXTURE_ANIMS, this.textureAnimations);
		saveObjects(stream, this.geosets);
		saveObjects(stream, this.geosetAnimations);
		saveObjects(stream, this.bones);
		saveObjects(stream, this.lights);
		saveObjects(stream, this.helpers);
		saveObjects(stream, this.attachments);
		savePivotPointBlock(stream);
		saveObjects(stream, this.particleEmitters);
		saveObjects(stream, this.particleEmitters2);

		if (this.version > 800) {
			saveObjects(stream, this.particleEmittersPopcorn);
		}

		saveObjects(stream, this.ribbonEmitters);
		saveObjects(stream, this.cameras);
		saveObjects(stream, this.eventObjects);
		saveObjects(stream, this.collisionShapes);

		if (this.version > 800) {
			saveObjects(stream, this.faceEffects);
			saveBindPoseBlock(stream);
		}

		return ByteBuffer.wrap(stream.buffer.toString().getBytes());
	}

	private void saveVersionBlock(final MdlTokenOutputStream stream) {
		stream.startBlock(MdlUtils.TOKEN_VERSION);
		stream.writeAttrib(MdlUtils.TOKEN_FORMAT_VERSION, this.version);
		stream.endBlock();
	}

	private void saveModelBlock(final MdlTokenOutputStream stream) {
		stream.startObjectBlock(MdlUtils.TOKEN_MODEL, this.name);
		stream.writeAttribUInt32(MdlUtils.TOKEN_BLEND_TIME, this.blendTime);
		this.extent.writeMdl(stream);
		stream.endBlock();
	}

	private void saveStaticObjectsBlock(final MdlTokenOutputStream stream, final String name,
			final List<? extends MdlxBlock> objects) {
		if (!objects.isEmpty()) {
			stream.startBlock(name, objects.size());

			for (final MdlxBlock object : objects) {
				object.writeMdl(stream, this.version);
			}

			stream.endBlock();
		}
	}

	private void saveGlobalSequenceBlock(final MdlTokenOutputStream stream) {
		if (!this.globalSequences.isEmpty()) {
			stream.startBlock(MdlUtils.TOKEN_GLOBAL_SEQUENCES, this.globalSequences.size());

			for (final Long globalSequence : this.globalSequences) {
				stream.writeAttribUInt32(MdlUtils.TOKEN_DURATION, globalSequence);
			}

			stream.endBlock();
		}
	}

	private void saveObjects(final MdlTokenOutputStream stream, final List<? extends MdlxBlock> objects) {
		for (final MdlxBlock object : objects) {
			object.writeMdl(stream, this.version);
		}
	}

	private void savePivotPointBlock(final MdlTokenOutputStream stream) {
		if (!this.pivotPoints.isEmpty()) {
			stream.startBlock(MdlUtils.TOKEN_PIVOT_POINTS, this.pivotPoints.size());

			for (final float[] pivotPoint : this.pivotPoints) {
				stream.writeFloatArray(pivotPoint);
			}

			stream.endBlock();
		}
	}

	private void saveBindPoseBlock(final MdlTokenOutputStream stream) {
		if (!this.bindPose.isEmpty()) {
			stream.startBlock("BindPose");

			stream.startBlock("Matrices", this.bindPose.size());

			for (final float[] matrix : this.bindPose) {
				stream.writeFloatArray(matrix);
			}

			stream.endBlock();

			stream.endBlock();
		}
	}

	public int getByteLength() {
		int size = 396;

		size += getStaticObjectsChunkByteLength(this.sequences, 132);
		size += getStaticObjectsChunkByteLength(this.globalSequences, 4);
		size += getDynamicObjectsChunkByteLength(this.materials);
		size += getStaticObjectsChunkByteLength(this.textures, 268);
		size += getDynamicObjectsChunkByteLength(this.textureAnimations);
		size += getDynamicObjectsChunkByteLength(this.geosets);
		size += getDynamicObjectsChunkByteLength(this.geosetAnimations);
		size += getDynamicObjectsChunkByteLength(this.bones);
		size += getDynamicObjectsChunkByteLength(this.lights);
		size += getDynamicObjectsChunkByteLength(this.helpers);
		size += getDynamicObjectsChunkByteLength(this.attachments);
		size += getStaticObjectsChunkByteLength(this.pivotPoints, 12);
		size += getDynamicObjectsChunkByteLength(this.particleEmitters);
		size += getDynamicObjectsChunkByteLength(this.particleEmitters2);

		if (this.version > 800) {
			size += getDynamicObjectsChunkByteLength(this.particleEmittersPopcorn);
		}

		size += getDynamicObjectsChunkByteLength(this.ribbonEmitters);
		size += getDynamicObjectsChunkByteLength(this.cameras);
		size += getDynamicObjectsChunkByteLength(this.eventObjects);
		size += getDynamicObjectsChunkByteLength(this.collisionShapes);
		size += getObjectsByteLength(this.unknownChunks);

		if (this.version > 800) {
			size += getStaticObjectsChunkByteLength(this.faceEffects, 340);
			size += getBindPoseChunkByteLength();
		}

		return size;
	}

	private <E extends MdlxChunk> long getObjectsByteLength(final List<E> objects) {
		long size = 0;
		for (final E object : objects) {
			size += object.getByteLength(this.version);
		}
		return size;
	}

	private <E extends MdlxChunk> long getDynamicObjectsChunkByteLength(final List<E> objects) {
		if (!objects.isEmpty()) {
			return 8 + getObjectsByteLength(objects);
		}

		return 0;
	}

	private <E> long getStaticObjectsChunkByteLength(final List<E> objects, final long size) {
		if (!objects.isEmpty()) {
			return 8 + (objects.size() * size);
		}

		return 0;
	}

	private long getBindPoseChunkByteLength() {
		if (this.bindPose.size() > 0) {
			return 12 + (this.bindPose.size() * 48);
		}

		return 0;
	}

	public List<MdlxGeoset> getGeosets() {
		return this.geosets;
	}

	public int getVersion() {
		return this.version;
	}

	public String getName() {
		return this.name;
	}

	public String getAnimationFile() {
		return this.animationFile;
	}

	public MdlxExtent getExtent() {
		return this.extent;
	}

	public long getBlendTime() {
		return this.blendTime;
	}

	public List<MdlxSequence> getSequences() {
		return this.sequences;
	}

	public List<Long> getGlobalSequences() {
		return this.globalSequences;
	}

	public List<MdlxMaterial> getMaterials() {
		return this.materials;
	}

	public List<MdlxTexture> getTextures() {
		return this.textures;
	}

	public List<MdlxTextureAnimation> getTextureAnimations() {
		return this.textureAnimations;
	}

	public List<MdlxGeosetAnimation> getGeosetAnimations() {
		return this.geosetAnimations;
	}

	public List<MdlxBone> getBones() {
		return this.bones;
	}

	public List<MdlxLight> getLights() {
		return this.lights;
	}

	public List<MdlxHelper> getHelpers() {
		return this.helpers;
	}

	public List<MdlxAttachment> getAttachments() {
		return this.attachments;
	}

	public List<float[]> getPivotPoints() {
		return this.pivotPoints;
	}

	public List<MdlxParticleEmitter> getParticleEmitters() {
		return this.particleEmitters;
	}

	public List<MdlxParticleEmitter2> getParticleEmitters2() {
		return this.particleEmitters2;
	}

	public List<MdlxParticleEmitterPopcorn> getParticleEmittersPopcorn() {
		return this.particleEmittersPopcorn;
	}

	public List<MdlxRibbonEmitter> getRibbonEmitters() {
		return this.ribbonEmitters;
	}

	public List<MdlxCamera> getCameras() {
		return this.cameras;
	}

	public List<MdlxEventObject> getEventObjects() {
		return this.eventObjects;
	}

	public List<MdlxCollisionShape> getCollisionShapes() {
		return this.collisionShapes;
	}

	public List<MdlxFaceEffect> getFaceEffects() {
		return this.faceEffects;
	}

	public List<float[]> getBindPose() {
		return this.bindPose;
	}

	public List<MdlxUnknownChunk> getUnknownChunks() {
		return this.unknownChunks;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setAnimationFile(final String animationFile) {
		this.animationFile = animationFile;
	}

	public void setExtent(final MdlxExtent extent) {
		this.extent = extent;
	}

	public void setBlendTime(final long blendTime) {
		this.blendTime = blendTime;
	}

	public void setSequences(final List<MdlxSequence> sequences) {
		this.sequences = sequences;
	}

	public void setGlobalSequences(final List<Long> globalSequences) {
		this.globalSequences = globalSequences;
	}

	public void setMaterials(final List<MdlxMaterial> materials) {
		this.materials = materials;
	}

	public void setTextures(final List<MdlxTexture> textures) {
		this.textures = textures;
	}

	public void setTextureAnimations(final List<MdlxTextureAnimation> textureAnimations) {
		this.textureAnimations = textureAnimations;
	}

	public void setGeosets(final List<MdlxGeoset> geosets) {
		this.geosets = geosets;
	}

	public void setGeosetAnimations(final List<MdlxGeosetAnimation> geosetAnimations) {
		this.geosetAnimations = geosetAnimations;
	}

	public void setBones(final List<MdlxBone> bones) {
		this.bones = bones;
	}

	public void setLights(final List<MdlxLight> lights) {
		this.lights = lights;
	}

	public void setHelpers(final List<MdlxHelper> helpers) {
		this.helpers = helpers;
	}

	public void setAttachments(final List<MdlxAttachment> attachments) {
		this.attachments = attachments;
	}

	public void setPivotPoints(final List<float[]> pivotPoints) {
		this.pivotPoints = pivotPoints;
	}

	public void setParticleEmitters(final List<MdlxParticleEmitter> particleEmitters) {
		this.particleEmitters = particleEmitters;
	}

	public void setParticleEmitters2(final List<MdlxParticleEmitter2> particleEmitters2) {
		this.particleEmitters2 = particleEmitters2;
	}

	public void setParticleEmittersPopcorn(final List<MdlxParticleEmitterPopcorn> particleEmittersPopcorn) {
		this.particleEmittersPopcorn = particleEmittersPopcorn;
	}

	public void setRibbonEmitters(final List<MdlxRibbonEmitter> ribbonEmitters) {
		this.ribbonEmitters = ribbonEmitters;
	}

	public void setCameras(final List<MdlxCamera> cameras) {
		this.cameras = cameras;
	}

	public void setEventObjects(final List<MdlxEventObject> eventObjects) {
		this.eventObjects = eventObjects;
	}

	public void setCollisionShapes(final List<MdlxCollisionShape> collisionShapes) {
		this.collisionShapes = collisionShapes;
	}

	public void setFaceEffects(final List<MdlxFaceEffect> faceEffects) {
		this.faceEffects = faceEffects;
	}

	public void setBindPose(final List<float[]> bindPose) {
		this.bindPose = bindPose;
	}

	public void setUnknownChunks(final List<MdlxUnknownChunk> unknownChunks) {
		this.unknownChunks = unknownChunks;
	}
}

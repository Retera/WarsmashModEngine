package com.etheller.warsmash.parsers.wdt;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MapChunkSoundEmitter implements MdlxBlock {

	private long soundPointId;
	private long soundNameId;
	private float[] pos = new float[3];
	private float maxDistance;
	private float minDistance;
	private float cutOffDistance;
	private int startTime;
	private int endTime;
	private int mode;
	private int groupSilenceMin;
	private int groupSilenceMax;
	private int playInstancesMin;
	private int playInstancesMax;
	private byte loopCountMin;
	private byte loopCountMax;
	private int interSoundGapMin;
	private int interSoundGapMax;

	@Override
	public void readMdx(BinaryReader reader, int version) {
		soundPointId = reader.readUInt32();
		soundNameId = reader.readUInt32();
		reader.readFloat32Array(pos);
		maxDistance = reader.readFloat32();
		minDistance = reader.readFloat32();
		cutOffDistance = reader.readFloat32();

		startTime = reader.readUInt16();
		endTime = reader.readUInt16();
		mode = reader.readUInt16();
		groupSilenceMin = reader.readUInt16();
		groupSilenceMax = reader.readUInt16();
		playInstancesMin = reader.readUInt16();
		playInstancesMax = reader.readUInt16();
		loopCountMin = reader.readInt8();
		loopCountMax = reader.readInt8();
		interSoundGapMin = reader.readUInt16();
		interSoundGapMax = reader.readUInt16();
	}

	@Override
	public void writeMdx(BinaryWriter writer, int version) {
		throw new RuntimeException();
	}

	@Override
	public void readMdl(MdlTokenInputStream stream, int version) {
		throw new RuntimeException();
	}

	@Override
	public void writeMdl(MdlTokenOutputStream stream, int version) {
		throw new RuntimeException();
	}

	public long getSoundPointId() {
		return soundPointId;
	}

	public long getSoundNameId() {
		return soundNameId;
	}

	public float[] getPos() {
		return pos;
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public float getMinDistance() {
		return minDistance;
	}

	public float getCutOffDistance() {
		return cutOffDistance;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public int getMode() {
		return mode;
	}

	public int getGroupSilenceMin() {
		return groupSilenceMin;
	}

	public int getGroupSilenceMax() {
		return groupSilenceMax;
	}

	public int getPlayInstancesMin() {
		return playInstancesMin;
	}

	public int getPlayInstancesMax() {
		return playInstancesMax;
	}

	public byte getLoopCountMin() {
		return loopCountMin;
	}

	public byte getLoopCountMax() {
		return loopCountMax;
	}

	public int getInterSoundGapMin() {
		return interSoundGapMin;
	}

	public int getInterSoundGapMax() {
		return interSoundGapMax;
	}

}

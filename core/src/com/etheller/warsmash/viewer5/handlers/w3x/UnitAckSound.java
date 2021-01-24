package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.viewer5.AudioBufferSource;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.AudioPanner;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;

public final class UnitAckSound {
	private static final UnitAckSound SILENT = new UnitAckSound(0, 0, 0, 0, 0, 0);

	private final List<Sound> sounds = new ArrayList<>();
	private final float volume;
	private final float pitch;
	private final float pitchVariance;
	private final float minDistance;
	private final float maxDistance;
	private final float distanceCutoff;

	private Sound lastPlayedSound;

	public static UnitAckSound create(final DataSource dataSource, final DataTable unitAckSounds,
			final String soundName, final String soundType) {
		final Element row = unitAckSounds.get(soundName + soundType);
		if (row == null) {
			return SILENT;
		}
		final String fileNames = row.getField("FileNames");
		String directoryBase = row.getField("DirectoryBase");
		if ((directoryBase.length() > 1) && !directoryBase.endsWith("\\")) {
			directoryBase += "\\";
		}
		final float volume = row.getFieldFloatValue("Volume") / 127f;
		final float pitch = row.getFieldFloatValue("Pitch");
		float pitchVariance = row.getFieldFloatValue("PitchVariance");
		if (pitchVariance == 1.0f) {
			pitchVariance = 0.0f;
		}
		final float minDistance = row.getFieldFloatValue("MinDistance");
		final float maxDistance = row.getFieldFloatValue("MaxDistance");
		final float distanceCutoff = row.getFieldFloatValue("DistanceCutoff");
		final UnitAckSound sound = new UnitAckSound(volume, pitch, pitchVariance, minDistance, maxDistance,
				distanceCutoff);
		for (final String fileName : fileNames.split(",")) {
			String filePath = directoryBase + fileName;
			if (!filePath.toLowerCase().endsWith(".wav")) {
				filePath += ".wav";
			}
			if (dataSource.has(filePath)) {
				sound.sounds.add(Gdx.audio.newSound(new DataSourceFileHandle(dataSource, filePath)));
			}
		}
		return sound;
	}

	public UnitAckSound(final float volume, final float pitch, final float pitchVariation, final float minDistance,
			final float maxDistance, final float distanceCutoff) {
		this.volume = volume;
		this.pitch = pitch;
		this.pitchVariance = pitchVariation;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.distanceCutoff = distanceCutoff;
	}

	public boolean play(final AudioContext audioContext, final RenderUnit unit) {
		return play(audioContext, unit, (int) (Math.random() * this.sounds.size()));
	}

	public boolean play(final AudioContext audioContext, final RenderUnit unit, final int index) {
		if (this.sounds.isEmpty()) {
			return false;
		}
		final long millisTime = TimeUtils.millis();
		if (millisTime < unit.lastUnitResponseEndTimeMillis) {
			return false;
		}

		final AudioPanner panner = audioContext.createPanner();
		final AudioBufferSource source = audioContext.createBufferSource();

		// Panner settings
		panner.setPosition(unit.location[0], unit.location[1], unit.location[2]);
		panner.setDistances(this.distanceCutoff, this.minDistance);
		panner.connect(audioContext.destination);

		// Source.
		source.buffer = this.sounds.get(index);
		source.connect(panner);

		// Make a sound.
		source.start(0, this.volume,
				(this.pitch + ((float) Math.random() * this.pitchVariance * 2)) - this.pitchVariance);
		this.lastPlayedSound = source.buffer;
		final float duration = Extensions.audio.getDuration(this.lastPlayedSound);
		unit.lastUnitResponseEndTimeMillis = millisTime + (long) (1000 * duration);
		return true;
	}

	public int getSoundCount() {
		return this.sounds.size();
	}
}
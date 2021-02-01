/* 
 * FLAC library (Java)
 * 
 * Copyright (c) Project Nayuki
 * https://www.nayuki.io/page/flac-library-java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (see COPYING.txt and COPYING.LESSER.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */

package io.nayuki.flac.app;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.metal.MetalSliderUI;
import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.FlacDecoder;


/**
 * Plays a single FLAC file to the system audio output, showing a GUI window with a seek bar.
 * The file to play is specified as a command line argument. The seek bar is responsible for both
 * displaying the current playback position, and allowing the user to click to seek to new positions.
 * <p>Usage: java SeekableFlacPlayerGui InFile.flac</p>
 */
public final class SeekableFlacPlayerGui {
	
	public static void main(String[] args) throws
		LineUnavailableException, IOException, InterruptedException {
		
		/*-- Initialization code --*/
		
		// Handle command line arguments
		if (args.length != 1) {
			System.err.println("Usage: java SeekableFlacPlayerGui InFile.flac");
			System.exit(1);
			return;
		}
		File inFile = new File(args[0]);
		
		// Process header metadata blocks
		FlacDecoder decoder = new FlacDecoder(inFile);
		while (decoder.readAndHandleMetadataBlock() != null);
		StreamInfo streamInfo = decoder.streamInfo;
		if (streamInfo.numSamples == 0)
			throw new IllegalArgumentException("Unknown audio length");
		
		// Start Java sound output API
		AudioFormat format = new AudioFormat(streamInfo.sampleRate,
			streamInfo.sampleDepth, streamInfo.numChannels, true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
		line.open(format);
		line.start();
		
		// Create GUI object, event handler, communication object
		final double[] seekRequest = {-1};
		AudioPlayerGui gui = new AudioPlayerGui("FLAC Player");
		gui.listener = new AudioPlayerGui.Listener() {
			public void seekRequested(double t) {
				synchronized(seekRequest) {
					seekRequest[0] = t;
					seekRequest.notify();
				}
			}
			public void windowClosing() {
				System.exit(0);
			}
		};
		
		/*-- Audio player loop --*/
		
		// Decode and write audio data, handle seek requests, wait for seek when end of stream reached
		int bytesPerSample = streamInfo.sampleDepth / 8;
		long startTime = line.getMicrosecondPosition();
		
		// Buffers for data created and discarded within each loop iteration, but allocated outside the loop
		int[][] samples = new int[streamInfo.numChannels][65536];
		byte[] sampleBytes = new byte[65536 * streamInfo.numChannels * bytesPerSample];
		while (true) {
			
			// Get and clear seek request, if any
			double seekReq;
			synchronized(seekRequest) {
				seekReq = seekRequest[0];
				seekRequest[0] = -1;
			}
			
			// Decode next audio block, or seek and decode
			int blockSamples;
			if (seekReq == -1)
				blockSamples = decoder.readAudioBlock(samples, 0);
			else {
				long samplePos = Math.round(seekReq * streamInfo.numSamples);
				seekReq = -1;
				blockSamples = decoder.seekAndReadAudioBlock(samplePos, samples, 0);
				line.flush();
				startTime = line.getMicrosecondPosition() - Math.round(samplePos * 1e6 / streamInfo.sampleRate);
			}
			
			// Set display position
			double timePos = (line.getMicrosecondPosition() - startTime) / 1e6;
			gui.setPosition(timePos * streamInfo.sampleRate / streamInfo.numSamples);
			
			// Wait when end of stream reached
			if (blockSamples == 0) {
				synchronized(seekRequest) {
					while (seekRequest[0] == -1)
						seekRequest.wait();
				}
				continue;
			}
			
			// Convert samples to channel-interleaved bytes in little endian
			int sampleBytesLen = 0;
			for (int i = 0; i < blockSamples; i++) {
				for (int ch = 0; ch < streamInfo.numChannels; ch++) {
					int val = samples[ch][i];
					for (int j = 0; j < bytesPerSample; j++, sampleBytesLen++)
						sampleBytes[sampleBytesLen] = (byte)(val >>> (j << 3));
				}
			}
			line.write(sampleBytes, 0, sampleBytesLen);
		}
	}
	
	
	
	/*---- User interface classes ----*/
	
	private static final class AudioPlayerGui {
		
		/*-- Fields --*/
		
		public Listener listener;
		private JSlider slider;
		private BasicSliderUI sliderUi;
		
		
		/*-- Constructor --*/
		
		public AudioPlayerGui(String windowTitle) {
			// Create and configure slider
			slider = new JSlider(SwingConstants.HORIZONTAL, 0, 10000, 0);
			sliderUi = new MetalSliderUI();
			slider.setUI(sliderUi);
			slider.setPreferredSize(new Dimension(800, 50));
			slider.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent ev) {
					moveSlider(ev);
				}
				public void mouseReleased(MouseEvent ev) {
					moveSlider(ev);
					listener.seekRequested((double)slider.getValue() / slider.getMaximum());
				}
			});
			slider.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent ev) {
					moveSlider(ev);
				}
			});
			
			// Create and configure frame (window)
			JFrame frame = new JFrame(windowTitle);
			frame.add(slider);
			frame.pack();
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent ev) {
					listener.windowClosing();
				}
			});
			frame.setResizable(false);
			frame.setVisible(true);
		}
		
		
		/*-- Methods --*/
		
		public void setPosition(double t) {
			if (Double.isNaN(t))
				return;
			final double val = Math.max(Math.min(t, 1), 0);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (!slider.getValueIsAdjusting())
						slider.setValue((int)Math.round(val * slider.getMaximum()));
				}
			});
		}
		
		
		private void moveSlider(MouseEvent ev) {
			slider.setValue(sliderUi.valueForXPosition(ev.getX()));
		}
		
		
		/*-- Helper interface --*/
		
		public interface Listener {
			
			public void seekRequested(double t);  // 0.0 <= t <= 1.0
			
			public void windowClosing();
			
		}
		
	}
	
}

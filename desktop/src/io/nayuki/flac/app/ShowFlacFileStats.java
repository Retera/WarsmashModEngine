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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import io.nayuki.flac.common.FrameInfo;
import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.DataFormatException;
import io.nayuki.flac.decode.FlacLowLevelInput;
import io.nayuki.flac.decode.FrameDecoder;
import io.nayuki.flac.decode.SeekableFileFlacInput;


/**
 * Reads a FLAC file, collects various statistics, and
 * prints human-formatted information to standard output.
 * <p>Usage: java ShowFlacFileStats InFile.flac</p>
 * <p>Example output from this program (abbreviated):</p>
 * <pre>===== Block sizes (samples) =====
 * 4096: * (11)
 * 5120: ***** (56)
 * 6144: *********** (116)
 * 7168: ************* (134)
 * 8192: ***************** (177)
 * 9216: ***************** (182)
 *10240: ***************** (179)
 *11264: ****************************** (318)
 *12288: ****************** (194)
 *
 *===== Frame sizes (bytes) =====
 *12000: ****** (20)
 *13000: ******* (24)
 *14000: ********** (34)
 *15000: **************** (51)
 *16000: ********************* (68)
 *17000: ******************* (63)
 *18000: ******************* (63)
 *19000: ************************ (77)
 *20000: ********************* (70)
 *21000: ****************** (60)
 *22000: ************************* (82)
 *23000: ********************* (69)
 *24000: *************************** (87)
 *25000: *************************** (88)
 *26000: ********************** (73)
 *27000: ************************** (84)
 *28000: ****************************** (98)
 *29000: ********************** (73)
 *30000: *********************** (75)
 *31000: ************ (39)
 *
 *===== Average compression ratio at block sizes =====
 * 4096: ********************** (0.7470)
 * 5120: ******************** (0.6815)
 * 6144: ******************** (0.6695)
 * 7168: ******************* (0.6438)
 * 8192: ******************* (0.6379)
 * 9216: ****************** (0.6107)
 *10240: ****************** (0.6022)
 *11264: ***************** (0.5628)
 *12288: ***************** (0.5724)
 *
 *===== Stereo coding modes =====
 *Independent: **** (83)
 *Left-side  :  (3)
 *Right-side : ************************ (574)
 *Mid-side   : ****************************** (708)</pre>
 */
public final class ShowFlacFileStats {
	
	/*---- Main application function ----*/
	
	public static void main(String[] args) throws IOException {
		// Handle command line arguments
		if (args.length != 1) {
			System.err.println("Usage: java ShowFlacFileStats InFile.flac");
			System.exit(1);
			return;
		}
		File inFile = new File(args[0]);
		
		// Data structures to hold statistics
		List<Integer> blockSizes = new ArrayList<>();
		List<Integer> frameSizes = new ArrayList<>();
		List<Integer> channelAssignments = new ArrayList<>();
		
		// Read input file
		StreamInfo streamInfo = null;
		try (FlacLowLevelInput input = new SeekableFileFlacInput(inFile)) {
			// Magic string "fLaC"
			if (input.readUint(32) != 0x664C6143)
				throw new DataFormatException("Invalid magic string");
			
			// Handle metadata blocks
			for (boolean last = false; !last; ) {
				last = input.readUint(1) != 0;
				int type = input.readUint(7);
				int length = input.readUint(24);
				byte[] data = new byte[length];
				input.readFully(data);
				if (type == 0)
					streamInfo = new StreamInfo(data);
			}
			
			// Decode every frame
			FrameDecoder dec = new FrameDecoder(input, streamInfo.sampleDepth);
			int[][] blockSamples = new int[8][65536];
			while (true) {
				FrameInfo meta = dec.readFrame(blockSamples, 0);
				if (meta == null)
					break;
				blockSizes.add(meta.blockSize);
				frameSizes.add(meta.frameSize);
				channelAssignments.add(meta.channelAssignment);
			}
		}
		
		// Build and print graphs
		printBlockSizeHistogram(blockSizes);
		printFrameSizeHistogram(frameSizes);
		printCompressionRatioGraph(streamInfo, blockSizes, frameSizes);
		if (streamInfo.numChannels == 2)
			printStereoModeGraph(channelAssignments);
	}
	
	
	
	/*---- Statistics-processing functions ----*/
	
	private static void printBlockSizeHistogram(List<Integer> blockSizes) {
		Map<Integer,Integer> blockSizeCounts = new TreeMap<>();
		for (int bs : blockSizes) {
			if (!blockSizeCounts.containsKey(bs))
				blockSizeCounts.put(bs, 0);
			int count = blockSizeCounts.get(bs) + 1;
			blockSizeCounts.put(bs, count);
		}
		List<String> blockSizeLabels = new ArrayList<>();
		List<Double> blockSizeValues = new ArrayList<>();
		for (Map.Entry<Integer,Integer> entry : blockSizeCounts.entrySet()) {
			blockSizeLabels.add(String.format("%5d", entry.getKey()));
			blockSizeValues.add((double)entry.getValue());
		}
		printNormalizedBarGraph("Block sizes (samples)", blockSizeLabels, blockSizeValues);
	}
	
	
	private static void printFrameSizeHistogram(List<Integer> frameSizes) {
		final int step = 1000;
		SortedMap<Integer,Integer> frameSizeCounts = new TreeMap<>();
		int maxKeyLen = 0;
		for (int fs : frameSizes) {
			int key = (int)Math.round((double)fs / step);
			maxKeyLen = Math.max(Integer.toString(key * step).length(), maxKeyLen);
			if (!frameSizeCounts.containsKey(key))
				frameSizeCounts.put(key, 0);
			frameSizeCounts.put(key, frameSizeCounts.get(key) + 1);
		}
		for (int i = frameSizeCounts.firstKey(); i < frameSizeCounts.lastKey(); i++) {
			if (!frameSizeCounts.containsKey(i))
				frameSizeCounts.put(i, 0);
		}
		List<String> frameSizeLabels = new ArrayList<>();
		List<Double> frameSizeValues = new ArrayList<>();
		for (Map.Entry<Integer,Integer> entry : frameSizeCounts.entrySet()) {
			frameSizeLabels.add(String.format("%" + maxKeyLen + "d", entry.getKey() * step));
			frameSizeValues.add((double)entry.getValue());
		}
		printNormalizedBarGraph("Frame sizes (bytes)", frameSizeLabels, frameSizeValues);
	}
	
	
	private static void printCompressionRatioGraph(StreamInfo streamInfo, List<Integer> blockSizes, List<Integer> frameSizes) {
		Map<Integer,Integer> blockSizeCounts = new TreeMap<>();
		Map<Integer,Long> blockSizeBytes = new TreeMap<>();
		for (int i = 0; i < blockSizes.size(); i++) {
			int bs = blockSizes.get(i);
			if (!blockSizeCounts.containsKey(bs)) {
				blockSizeCounts.put(bs, 0);
				blockSizeBytes.put(bs, 0L);
			}
			blockSizeCounts.put(bs, blockSizeCounts.get(bs) + 1);
			blockSizeBytes.put(bs, blockSizeBytes.get(bs) + frameSizes.get(i));
		}
		List<String> blockRatioLabels = new ArrayList<>();
		List<Double> blockRatioValues = new ArrayList<>();
		for (Map.Entry<Integer,Integer> entry : blockSizeCounts.entrySet()) {
			blockRatioLabels.add(String.format("%5d", entry.getKey()));
			blockRatioValues.add(blockSizeBytes.get(entry.getKey()) / ((double)entry.getValue() * entry.getKey() * streamInfo.numChannels * streamInfo.sampleDepth / 8));
		}
		printNormalizedBarGraph("Average compression ratio at block sizes", blockRatioLabels, blockRatioValues);
	}
	
	
	private static void printStereoModeGraph(List<Integer> channelAssignments) {
		List<String> stereoModeLabels = Arrays.asList("Independent", "Left-side", "Right-side", "Mid-side");
		List<Double> stereoModeValues = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			stereoModeValues.add(0.0);
		for (int mode : channelAssignments) {
			int index;
			switch (mode) {
				case  1:  index = 0;  break;
				case  8:  index = 1;  break;
				case  9:  index = 2;  break;
				case 10:  index = 3;  break;
				default:  throw new DataFormatException("Invalid mode in stereo stream");
			}
			stereoModeValues.set(index, stereoModeValues.get(index) + 1);
		}
		printNormalizedBarGraph("Stereo coding modes", stereoModeLabels, stereoModeValues);
	}
	
	
	
	/*---- Utility functions ----*/
	
	private static void printNormalizedBarGraph(String heading, List<String> labels, List<Double> values) {
		Objects.requireNonNull(heading);
		Objects.requireNonNull(labels);
		Objects.requireNonNull(values);
		if (labels.size() != values.size())
			throw new IllegalArgumentException();
		
		final int maxBarWidth = 100;
		System.out.printf("==================== %s ====================%n", heading);
		System.out.println();
		
		int maxLabelLen = 0;
		for (String s : labels)
			maxLabelLen = Math.max(s.length(), maxLabelLen);
		String spaces = new String(new char[maxLabelLen]).replace((char)0, ' ');
		
		double maxValue = 1;  // This avoids division by zero
		for (double val : values)
			maxValue = Math.max(val, maxValue);
		
		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			double value = values.get(i);
			int barWidth = (int)Math.round(value / maxValue * maxBarWidth);
			String bar = new String(new char[barWidth]).replace((char)0, '*');
			System.out.printf("%s%s: %s (%s)%n", label, spaces.substring(label.length()),
				bar, (long)value == value ? Long.toString((long)value) : Double.toString(value));
		}
		System.out.println();
		System.out.println();
	}
	
}

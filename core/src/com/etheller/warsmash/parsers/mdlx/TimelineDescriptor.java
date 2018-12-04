package com.etheller.warsmash.parsers.mdlx;

import com.etheller.warsmash.parsers.mdlx.timeline.FloatArrayTimeline;
import com.etheller.warsmash.parsers.mdlx.timeline.FloatTimeline;
import com.etheller.warsmash.parsers.mdlx.timeline.Timeline;
import com.etheller.warsmash.parsers.mdlx.timeline.UInt32Timeline;

public interface TimelineDescriptor {
	Timeline createTimeline();

	public static final TimelineDescriptor UINT32_TIMELINE = new TimelineDescriptor() {
		@Override
		public Timeline createTimeline() {
			return new UInt32Timeline();
		}
	};

	public static final TimelineDescriptor FLOAT_TIMELINE = new TimelineDescriptor() {
		@Override
		public Timeline createTimeline() {
			return new FloatTimeline();
		}
	};

	public static final TimelineDescriptor VECTOR3_TIMELINE = new TimelineDescriptor() {
		@Override
		public Timeline createTimeline() {
			return new FloatArrayTimeline(3);
		}
	};

	public static final TimelineDescriptor VECTOR4_TIMELINE = new TimelineDescriptor() {
		@Override
		public Timeline createTimeline() {
			return new FloatArrayTimeline(4);
		}
	};
}

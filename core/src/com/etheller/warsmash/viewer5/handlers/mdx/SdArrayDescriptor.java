package com.etheller.warsmash.viewer5.handlers.mdx;

public interface SdArrayDescriptor<TYPE> {
	SdArrayDescriptor<Object> GENERIC = new SdArrayDescriptor<Object>() {
		@Override
		public Object[] create(final int size) {
			return new Object[size];
		}
	};
	SdArrayDescriptor<float[]> FLOAT_ARRAY = new SdArrayDescriptor<float[]>() {
		@Override
		public float[][] create(final int size) {
			return new float[size][];
		}
	};
	SdArrayDescriptor<long[]> LONG_ARRAY = new SdArrayDescriptor<long[]>() {
		@Override
		public long[][] create(final int size) {
			return new long[size][];
		}
	};

	TYPE[] create(int size);
}

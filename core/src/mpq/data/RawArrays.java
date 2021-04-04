package mpq.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class RawArrays {
	public static int[] getArray(ByteBuffer source){
		source.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer in = source.asIntBuffer();
		
		IntBuffer out = IntBuffer.allocate(in.capacity());
		out.put(in);
		
		return out.array();
	}
	
	public static void getArray(ByteBuffer source, int[] destination){
		source.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer in = source.asIntBuffer();
		
		IntBuffer out = IntBuffer.wrap(destination);
		out.put(in);
	}
	
	public static short[] getShortArray(ByteBuffer source){
		source.order(ByteOrder.LITTLE_ENDIAN);
		ShortBuffer in = source.asShortBuffer();
		
		ShortBuffer out = ShortBuffer.allocate(in.capacity());
		out.put(in);
		
		return out.array();
	}
}

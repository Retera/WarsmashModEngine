package mpq.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Raw {
	protected ByteBuffer data;
	
	public Raw(ByteBuffer source){
		move(source);
	}
	
	public Raw(){
		data = null;
	}
	
	public void move(ByteBuffer source){
		data = source.slice().order(ByteOrder.LITTLE_ENDIAN);
	}
}

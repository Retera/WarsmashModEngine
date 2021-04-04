package mpq.compression.adpcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ADPCM {

	private static class Channel{
		public short value;
		public byte rate;
	}
	
	private final Channel[] state;
	
	public ADPCM(int channelmax){
		state = new Channel[channelmax];
		for( int i = 0 ; i < state.length ; i+= 1 ) state[i] = new Channel();
	}
	
	public void decompress(ByteBuffer in, ByteBuffer out, int channeln){
		// prepare buffers
		in.order(ByteOrder.LITTLE_ENDIAN);
		out.order(ByteOrder.LITTLE_ENDIAN);
		
		byte stepshift = (byte) (in.getShort() >>> 8);
		
		// initialize channels
		for( int i = 0 ; i < channeln ; i+= 1 ){
			Channel chan = state[i];
			chan.rate = 0x2C;
			chan.value = in.getShort();
			out.putShort(chan.value);
		}
		
		int current = 0;
		Channel chan = state[current];
		boolean multichannel = channeln > 1;
		
		// decompress
		while( in.hasRemaining() ){
			byte op = in.get();
			
			if( (op & 0x80) > 0 ){
				switch( op & 0x7F ){
				// write current value
				case 0 :
					if( chan.rate != 0 ) chan.rate-= 1;
					out.putShort(chan.value);
					if( multichannel ) chan = state[++current % channeln];
					break;
				// increment period
				case 1 :
					chan.rate+= 8;
					if( chan.rate > 0x58 ) chan.rate = 0x58;
					break;
				// skip channel
				case 2 :
					if( multichannel ) chan = state[++current % channeln];
					break;
				// all other values
				default :
					chan.rate-= 8;
					if( chan.rate < 0 ) chan.rate = 0;
				}
			}else{
				// adjust value
				short stepunit = STEP_TABLE[chan.rate];
				short stepsize = (short) (stepunit >>> stepshift);
				int value = chan.value;
				
				for( int i = 0 ; i < 6 ; i+= 1 ){
					if( (op & 1 << i) > 0 ) stepsize+= stepunit >> i;
				}
				
				if( (op & 0x40) > 0 ){
					value-= stepsize;
					if( value < Short.MIN_VALUE ) value = Short.MIN_VALUE;
				}else{
					value+= stepsize;
					if( value > Short.MAX_VALUE ) value = Short.MAX_VALUE;
				}
				chan.value = (short) value;
				
				out.putShort(chan.value);
				
				chan.rate+= CHANGE_TABLE[op & 0x1F];
				if( chan.rate < 0 ) chan.rate = 0;
				else if( chan.rate > 0x58 ) chan.rate = 0x58;
				
				if( multichannel ) chan = state[++current % channeln];
			}
		}
	}
	
	private static final byte CHANGE_TABLE[] =
		{
		    0xFFFFFFFF, 0x00000000, 0xFFFFFFFF, 0x00000004, 0xFFFFFFFF, 0x00000002, 0xFFFFFFFF, 0x00000006,
		    0xFFFFFFFF, 0x00000001, 0xFFFFFFFF, 0x00000005, 0xFFFFFFFF, 0x00000003, 0xFFFFFFFF, 0x00000007,
		    0xFFFFFFFF, 0x00000001, 0xFFFFFFFF, 0x00000005, 0xFFFFFFFF, 0x00000003, 0xFFFFFFFF, 0x00000007,  
		    0xFFFFFFFF, 0x00000002, 0xFFFFFFFF, 0x00000004, 0xFFFFFFFF, 0x00000006, 0xFFFFFFFF, 0x00000008  
		};

	private static final short STEP_TABLE[] =
		{
		    0x00000007, 0x00000008, 0x00000009, 0x0000000A, 0x0000000B, 0x0000000C, 0x0000000D, 0x0000000E,
		    0x00000010, 0x00000011, 0x00000013, 0x00000015, 0x00000017, 0x00000019, 0x0000001C, 0x0000001F,
		    0x00000022, 0x00000025, 0x00000029, 0x0000002D, 0x00000032, 0x00000037, 0x0000003C, 0x00000042,
		    0x00000049, 0x00000050, 0x00000058, 0x00000061, 0x0000006B, 0x00000076, 0x00000082, 0x0000008F,
		    0x0000009D, 0x000000AD, 0x000000BE, 0x000000D1, 0x000000E6, 0x000000FD, 0x00000117, 0x00000133,
		    0x00000151, 0x00000173, 0x00000198, 0x000001C1, 0x000001EE, 0x00000220, 0x00000256, 0x00000292,
		    0x000002D4, 0x0000031C, 0x0000036C, 0x000003C3, 0x00000424, 0x0000048E, 0x00000502, 0x00000583,
		    0x00000610, 0x000006AB, 0x00000756, 0x00000812, 0x000008E0, 0x000009C3, 0x00000ABD, 0x00000BD0,
		    0x00000CFF, 0x00000E4C, 0x00000FBA, 0x0000114C, 0x00001307, 0x000014EE, 0x00001706, 0x00001954,
		    0x00001BDC, 0x00001EA5, 0x000021B6, 0x00002515, 0x000028CA, 0x00002CDF, 0x0000315B, 0x0000364B,
		    0x00003BB9, 0x000041B2, 0x00004844, 0x00004F7E, 0x00005771, 0x0000602F, 0x000069CE, 0x00007462,
		    0x00007FFF
		};
}

package mpq.compression.pkware;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class PKExploder {
	private static final byte CMP_BINARY = 0;
	private static final byte CMP_ASCII = 1;
	
	private static final byte LEN_SIZE = 16;
	private static final byte[] LEN_BITS = {
	    0x03, 0x02, 0x03, 0x03, 0x04, 0x04, 0x04, 0x05, 0x05, 0x05, 0x05, 0x06, 0x06, 0x06, 0x07, 0x07
	    };
	private static final short[] LEN_CODES = {
	    0x05, 0x03, 0x01, 0x06, 0x0A, 0x02, 0x0C, 0x14, 0x04, 0x18, 0x08, 0x30, 0x10, 0x20, 0x40, 0x00
		};
	private static final byte[] LENGTH_CODES = new byte[0x100];
	static {
		denDecodeTabs(LENGTH_CODES, LEN_CODES, LEN_BITS, LEN_SIZE);
	}
	
	private static final byte[] EX_LEN_BITS = {
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
		};

	private static final short[] LEN_BASE ={
		    0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007,
		    0x0008, 0x000A, 0x000E, 0x0016, 0x0026, 0x0046, 0x0086, 0x0106
		};
	
	private static final byte DIST_SIZE = 64;
	private static final byte[] DIST_BITS = {
		    0x02, 0x04, 0x04, 0x05, 0x05, 0x05, 0x05, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06,
		    0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07,
		    0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07,
		    0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08
		};
	private static final short[] DIST_CODES = 
		{
		    0x03, 0x0D, 0x05, 0x19, 0x09, 0x11, 0x01, 0x3E, 0x1E, 0x2E, 0x0E, 0x36, 0x16, 0x26, 0x06, 0x3A,
		    0x1A, 0x2A, 0x0A, 0x32, 0x12, 0x22, 0x42, 0x02, 0x7C, 0x3C, 0x5C, 0x1C, 0x6C, 0x2C, 0x4C, 0x0C,
		    0x74, 0x34, 0x54, 0x14, 0x64, 0x24, 0x44, 0x04, 0x78, 0x38, 0x58, 0x18, 0x68, 0x28, 0x48, 0x08,
		    0xF0, 0x70, 0xB0, 0x30, 0xD0, 0x50, 0x90, 0x10, 0xE0, 0x60, 0xA0, 0x20, 0xC0, 0x40, 0x80, 0x00
		};
	private static final byte[] DIST_POS_CODES = new byte[0x100];
	static {
		 denDecodeTabs(DIST_POS_CODES, DIST_CODES, DIST_BITS, DIST_SIZE);
	}
	
	private static void denDecodeTabs(byte[] pos, short[] sindex, byte[] len, int size){
		for( byte i = 0 ; i < size ; i++ ){
	        short length = (short) (1 << len[i]);

	        for( short index = sindex[i] ; index < pos.length ; index+= length )
	        {
	        	pos[index] = i;
	        }
		}
	}
	
	private ByteBuffer in;
	private ByteBuffer out;
	private byte ctype;
	private byte dsize_bits;
	private short bit_buff;
	private byte extra_bits;
	private byte dsize_mask;
	
	private void WasteBits(byte nbits){
		bit_buff = (short) ((bit_buff & 0xFFFF) >>> nbits);
		if( nbits <= extra_bits ){
	        extra_bits-= nbits;
		}else{
			bit_buff = (short) ((bit_buff | ((in.get() & 0xFF ) << (8 - nbits + extra_bits))) & 0xFFFF);
			extra_bits-= nbits - 8;
		}
	}
	
	private void expand() throws PKException{
		for( ; ; ){
			if( (bit_buff & 0x01) != 0 ){
				WasteBits((byte) 1);
				
				// --- repeat bytes
				
				// get length
				short length_code = (short) LENGTH_CODES[bit_buff & 0xFF];
				WasteBits((byte) LEN_BITS[length_code]);
				
				byte extra_length_bits;
		        if((extra_length_bits = EX_LEN_BITS[length_code]) != 0)
		        {
		            byte extra_length = (byte) (bit_buff & ((1 << extra_length_bits) - 1));

		            try{
		            	WasteBits(extra_length_bits);
		            }catch( BufferUnderflowException e ){
		            	if( (length_code + (extra_length & 0xFF)) == 0x10E ) return;
		            	else throw e;
		            }
		            length_code = (short) (LEN_BASE[length_code] + (extra_length & 0xFF));

		        }
		        
		        length_code+= 2;
		        
		        // get distance
		        byte dist_pos_code = DIST_POS_CODES[bit_buff & 0xFF];
		        byte dist_pos_bits = DIST_BITS[dist_pos_code];
		        WasteBits(dist_pos_bits);
		        
		        short distance;
		        
		        if(length_code == 2){
		            // If the repetition is only 2 bytes length,
		            // then take 2 bits from the stream in order to get the distance
		            distance = (short) ((dist_pos_code << 2) | (bit_buff & 0x03));
		            WasteBits((byte) 2);
		        }else{
		            // If the repetition is more than 2 bytes length,
		            // then take "dsize_bits" bits in order to get the distance
		            distance = (short) ((dist_pos_code << dsize_bits) | (bit_buff & dsize_mask));
		            WasteBits(dsize_bits);
		        }
		        distance+= 1;
		        
		        // do the copying
		        int target = out.position();
		        int source = target - distance;
		        
		        while( length_code > 0 ){
		        	if(source >= 0)
		        		out.put(target++, out.get(source++));
		        	else{
		        		throw new PKException("distance pointing before output");
		        	}
		        	length_code-= 1;
		        }
		        
		        out.position(target);
		        
			}else{
				WasteBits((byte) 1);
				
				// --- raw byte
				
				switch( ctype ){
				case CMP_BINARY:
					// read raw byte
					byte uncompressed_byte = (byte) (bit_buff & 0xFF);
					WasteBits((byte) 8);

					// write raw byte
					out.put(uncompressed_byte);
					break;
				case CMP_ASCII:
					// TODO add ASCII decompression stuff here
					System.err.println("pkware ascii compression not supported");
					return;
				default: throw new PKException("invalid compression mode");
				}
			}
		}
	}
	
	public void explode(ByteBuffer in, ByteBuffer out) throws PKException{
		// set in and out buffers
		this.in = in;
		this.out = out;
		if( in.remaining() <= 4 ) throw new PKException("received bad data");
		
		// initialize state with compression header
		ctype = in.get();
		dsize_bits = in.get();
		bit_buff = (short) (in.get() & 0xFF);
		extra_bits = 0;
		
		// dictionary size mask
		if( dsize_bits < 4 || 6 < dsize_bits  ) throw new PKException("invalid dictionary size");
		dsize_mask = (byte) (0xFFFF >> (0x10 - dsize_bits));
		
		// setup compression type dependent data
		switch( ctype ){
		case CMP_BINARY: break;
		case CMP_ASCII:
			// TODO add ASCII decompression stuff here
			System.err.println("pkware ascii compression not supported");
			return;
		default: throw new PKException("invalid compression mode");
		}
		
		// perform explode
        try{
        	expand();
        }catch( BufferUnderflowException e ){
        	throw new PKException("unexpected end of data");
        }
		
	}
}

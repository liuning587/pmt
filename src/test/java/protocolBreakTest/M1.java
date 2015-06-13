package protocolBreakTest;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Hex;

public class M1 {
	public static void main(String[] args){
//		String s="a  a";
//		System.out.print(s.replaceAll("\\s+", "%"));
		ByteBuffer b=ByteBuffer.allocate(6);
		b.put((byte)1);
		b.put((byte)2);
		b.put((byte)3);
		b.put((byte)4);
		byte[] by=new byte[]{(byte)0xff,(byte)0xff};
		b.put(by,0,2);
		b.put(0,(byte)0xff);
		System.out.print(Hex.encodeHex(b.array()));
		
	}

}

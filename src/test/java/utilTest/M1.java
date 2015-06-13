package utilTest;

import com.sx.mmt.fep.FepResponse;
import com.sx.mmt.internal.util.SimpleBytes;


public class M1 {
	public static void main(String[] args) {
		String pnfn="p3f91";
		int pn=Integer.valueOf(pnfn.substring(pnfn.indexOf("p")+1, pnfn.indexOf("f")));
		int fn=Integer.valueOf(pnfn.substring(pnfn.indexOf("f")+1));
//		System.out.println(fn);
		SimpleBytes s=new SimpleBytes("68 32 00 32 00 68 C9 05 21 B2 A1 00 02 79 00 00 01 00 BE 16",16,true);
		SimpleBytes sb=FepResponse.getComfirmPacket(s, 1);
		System.out.println(sb.toReverseHexString(" "));
		
	}
}

package utilTest;

import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.util.SimpleBytes;



public class M7 {
	public static void main(String[] args) {
		SimpleBytes sb=new SimpleBytes("68 42 10 42 10 68 44 00 10 01 00 2C 13 61 00 00 04 00 10 00 00 04 01 22 8D F8 06 40 02 AC 04 F8 03 2D 03 21 05 46 01 AA 02 20 FF F7 C2 FF 02 20 21 46 FF F7 18 FF 29 68 4F F0 F8 03 08 78 8D F8 05 30 10 F0 40 0F 01 D0 C4 23 00 E0 84 23 03",16,true);
		System.out.println(sb.toReverseHexString(""));
		Address address=new Address();
		address.setRawValue(new SimpleBytes("0521b2a13e",16,true));
		address.decode("", "");
		System.out.println(address.getMsa());
		address.setDistrict("2105");
		address.setTerminalAddress("A1B2");
		address.setMsa(31);
		address.encode("", "");
		System.out.println(address.getRawValue().toReverseHexString(""));
	}
}

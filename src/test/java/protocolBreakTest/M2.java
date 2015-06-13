package protocolBreakTest;

import org.apache.commons.lang3.StringUtils;

import com.sx.mmt.internal.protocol.afn.AFN13Hp0f3;
import com.sx.mmt.internal.util.SimpleBytes;

public class M2 {
	public static void main(String[] args) {
		AFN13Hp0f3 aFN13Hp0f3=new AFN13Hp0f3();
		
		aFN13Hp0f3.setRawValue(new SimpleBytes("57020000FF0300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",16,true));
		aFN13Hp0f3.decode("", "");
		System.out.println(aFN13Hp0f3.getFileTotalSegment());
		System.out.println(aFN13Hp0f3.getNotReceiveSegment().toString());
		SimpleBytes sb=new SimpleBytes((byte)3);
		System.out.println(sb.toReverseBinString(""));
		System.out.println(sb.toBinString(""));
		System.out.println(sb.toReverseHexString(""));
		System.out.println(StringUtils.reverse("123456789"));
		StringUtils.reverse("123456789");
	}
}

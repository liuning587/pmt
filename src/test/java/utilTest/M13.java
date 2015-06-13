package utilTest;

import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.afn.AFN09Hp0f25;
import com.sx.mmt.internal.util.SimpleBytes;

public class M13 {
	public static void main(String[] args) {
		AFN09Hp0f25 c=new AFN09Hp0f25();
		c.setRawValue(new SimpleBytes("5345545223061424000101",16,true));
		c.decode(Head.PROTOCOL_GB09, "");
		System.out.println(c.getFactoryCode());
		System.out.println(c.getCardVersion());
		System.out.println(c.getRouteVersion());
		System.out.println(c.getAppDate().toLocaleString());
	}
}

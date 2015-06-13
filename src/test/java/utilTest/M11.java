package utilTest;

import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.afn.AFN09Hp0f1;
import com.sx.mmt.internal.protocol.afn.AFN0CHp0f1;
import com.sx.mmt.internal.util.SimpleBytes;

public class M11 {
	public static void main(String[] args) {
		//4E425358313030323030303143513032200713475A33444A475A3233001C4351303443513031260710
		//4E425358313030323030303143513133080814475A33444A475A32330000002933092400
		SimpleBytes sb=new SimpleBytes("4E425358313030323030303143513133080814475A33444A475A32330000000125212400",16,true);
		AFN09Hp0f1 a=new AFN09Hp0f1();
		AFN0CHp0f1 b=new AFN0CHp0f1();
		a.setRawValue(sb);
		a.decode(Head.PROTOCOL_GB05, "");
		b.setRawValue(sb);
		b.decode(Head.PROTOCOL_GB05, "");
		System.out.println(b.getFactoryCode());
		System.out.println(b.getCoreVersion());
		System.out.println(b.getAppDate().toLocaleString());
		System.out.println(b.getTerminalCode());
		System.out.println(b.getModuleVersion());
		System.out.println(b.getAppVersion());
	}
}

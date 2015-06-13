package utilTest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sx.mmt.internal.util.SimpleBytes;

public class M21 {
	public static void main(String[] args) {
		Map<String,String> aa=new ConcurrentHashMap<String, String>();
		SimpleBytes sb=new SimpleBytes("73787274756170702E6F7574",16,true);
		System.out.println(new String(sb.getBytesArray()));
		System.out.println(aa);
	}
}

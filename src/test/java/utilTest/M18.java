package utilTest;

import java.util.Date;

import com.sx.mmt.internal.util.SimpleBytes;

public class M18 {
	public static void main(String[] args) {
		SimpleBytes sb=new SimpleBytes("60560919",16,true);
//		System.out.println(sb.getAt(0).toBCD());
//		System.out.println(sb.getAt(1).toBCD());
//		System.out.println(sb.getAt(2).toBCD());
//		System.out.println(sb.getAt(3).toBCD());
		Date d=new Date();
		System.out.println(d.getSeconds());
		System.out.println(d.getMinutes());
		System.out.println(d.getHours());
		System.out.println(d.getDate());
		System.out.println(String.format("%s%s%s%s", d.getSeconds(),d.getMinutes(),d.getHours(),d.getDate()));
	}
}

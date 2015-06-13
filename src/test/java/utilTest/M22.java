package utilTest;

import java.lang.reflect.Field;
import java.util.Date;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.SimpleBytes;

public class M22 {
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Date Dfrom=DateTool.getDateFromString("2014-10-9");
		Date dTo=DateTool.getDateFromString("2014-10-10");
		Date a=DateTool.getDateFromString("2014-10-10");
//		System.out.println(a.after(Dfrom) && a.before(dTo));
		String apn=null;
//		System.out.println(apn==null?"":apn);
		SimpleBytes rawValue=new SimpleBytes();
		rawValue.add(new SimpleBytes((apn==null?"":apn).trim(),16,"utf-8"));
//		System.out.println(rawValue);
		Field field=A.class.getDeclaredField("c");
		M22 m=new M22();
		A testa=m.new A();
//		for(int i=0;i<field.length;i++){
//			System.out.print(field[i].getGenericType()+"\n");
//		}
		System.out.print("class java.lang.String".equals(field.getGenericType().toString())+"\n");
		field.set(testa, "aa");
		System.out.print(testa.getC());
	}
	
	
	class A{
		private int a;
		private boolean b;
		private String c;
		public int getA() {
			return a;
		}
		public void setA(int a) {
			this.a = a;
		}
		public boolean isB() {
			return b;
		}
		public void setB(boolean b) {
			this.b = b;
		}
		public String getC() {
			return c;
		}
		public void setC(String c) {
			this.c = c;
		}
		
	}
}

package utilTest;

import java.lang.reflect.Field;

import com.sx.mmt.internal.protocol.afn.AFN00Hp0f3;
import com.sx.mmt.internal.protocol.afn.AFN13Hp0f1;
import com.sx.mmt.internal.protocol.afn.AFN13Hp0f3;

public class M4 {
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		AFN13Hp0f1 a=new AFN13Hp0f1();
		Field[] f=AFN13Hp0f3.class.getDeclaredFields();
		System.out.println(f.length);
		for(Field ff:f){
			System.out.println(ff.getGenericType().toString());
		}
		
	}
}

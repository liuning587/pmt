package utilTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;




public class Main1 {
	public static void main(String[] args) {
		//	double a=0.1;
		//	System.out.print(Math.log10(a));
		//	System.out.print(String.format("%02x",15));
		String a="${ip1}.${ip3}.${ip4}.${ip5}:${port}";
		String b="1.2.3.4:5";
		Map<String,Object> map=new HashMap<String, Object>();
		String out=new String(a);
		for(String s:map.keySet()){
			out=out.replace(String.format("${%s}", s),map.get(s).toString());
		}
		//System.out.print(out);
		Pattern ptn=Pattern.compile("\\$\\{[0-9a-zA-Z]+\\}");
		String[] sp=ptn.split(a);
		StringBuilder sa=new StringBuilder(a.replace(sp[0], ""));
		StringBuilder sb=new StringBuilder(b.replace(sp[0], ""));
		//System.out.print(Arrays.toString(sp));
		for(int i=0;i<sp.length-1;i++){
			map.put(sa.substring(2,sa.indexOf(sp[i+1])-1),sb.substring(0,sb.indexOf(sp[i+1])));
			sa.delete(0, sa.indexOf(sp[i+1])+1);
			sb.delete(0, sb.indexOf(sp[i+1])+1);
		}
		map.put(sa.substring(2,sa.length()-1),sb.substring(0));
		System.out.print("\n");
		System.out.print(map);
	}
}

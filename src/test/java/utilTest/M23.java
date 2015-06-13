package utilTest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class M23 {
	public static void main(String[] args) {
		Map<String,Object> aa=new HashMap<String,Object>();
		aa.put("length", "6");
		ExpressionParser parser = new SpelExpressionParser();  
	    EvaluationContext context = new StandardEvaluationContext(); 
	    context.setVariable("data", aa);
	    String s = parser.parseExpression("(#data['length']-8)/4").getValue(context, String.class);  
	    System.out.print(s);
	}
}

package protocolBreakTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.sx.mmt.internal.util.SimpleBytes;

public class M5 {
	public static void main(String[] args) throws JDOMException, IOException {
		SAXBuilder builder=new SAXBuilder();
		StringBuilder sb=new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"gbk\" ?>");
		sb.append("<result>");
		sb.append("<terminalId>terminalId</terminalId>");
		sb.append("<replyContent>申请成功信息</replyContent>");
		sb.append("</result>");
		String gbk=new String(sb.toString().getBytes("UTF-8"),"gbk");
		InputStream in=new ByteArrayInputStream(gbk.getBytes("gbk"));
		Document doc=builder.build(in);
		Element rootEl = doc.getRootElement();
		//System.out.println(doc.getDocType().toString());
		System.out.println(new String(rootEl.getChildText("replyContent").getBytes("gbk"),"UTF-8"));
		System.out.println(rootEl.getChildText("exceptionContent"));
		SimpleBytes head=new SimpleBytes("<?xml version",13,"gb2312");
		System.out.println(head.getLength());
	}
}

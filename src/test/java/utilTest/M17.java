package utilTest;

public class M17 {
	public static void main(String[] args) {
		String a="0<?xml version=\"1.0\" encoding=\"gb2312\" ?><result><terminalId>169091289</terminalId><replyContent>通道申请成功</replyContent></result>";
		a=a.substring(a.indexOf('<'));
		System.out.println(a);
	}
}

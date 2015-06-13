package utilTest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class M20 {
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(System.getProperty("user.dir"));
		System.out.println(URLDecoder.decode(System.getProperty("user.dir"),"UTF-8"));
		System.out.println(URLDecoder.decode(System.getProperty("user.dir"),"GBK"));
		System.out.println(URLDecoder.decode(System.getProperty("user.dir"),"gb2312"));
	}
}

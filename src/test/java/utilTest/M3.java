package utilTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sx.mmt.application.MainApp;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;


public class M3 {
	private static Logger logger = LoggerFactory.getLogger(MainApp.class); 
	private static ApplicationContext context=null;
	public static void main(String[] args) {
		System.out.println("程序启动中,请等待...");

		try{
			context=new ClassPathXmlApplicationContext("classpath:appcontext.xml");
			GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
					SpringBeanUtil.getBean("gBProtocolBreakerPool");
			DataPacketParser dataPacketParser=pbPool.getDataPacketParser();
			System.out.println(dataPacketParser==null);
			SimpleBytes packet=new SimpleBytes("68 32 00 32 00 68 C9 21 05 AA AA 00 02 79 00 00 01 00 BF 16",16,true);
			String afn=dataPacketParser.getAfn(packet);
			String terminalAddress=dataPacketParser.getTerminalAddress(packet);
			int fn=dataPacketParser.getFn(packet);
			System.out.println(afn);
			System.out.println(terminalAddress);
			System.out.println(fn);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(ErrorTool.getErrorInfoFromException(e));
		}
	}
}

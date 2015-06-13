package demoClient;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.util.SimpleBytes;

public class ConcurrencyHandler extends IoHandlerAdapter{
	private Controller controller;
	
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	private static Logger logger = LoggerFactory.getLogger(ConcurrencyHandler.class);
	@Override
	public void messageReceived(final IoSession session, final Object message)
			throws Exception {
		Address address=new Address(((SimpleBytes)message )
				.getSubByteArray(0, 1));//Address.INDEX_BEGIN, Address.INDEX_END
		address.decode("","");
		if(controller.getTerminal().get(address.getRawValue().toHexString(""))==null){
			controller.getTerminal().put(address.getRawValue().toHexString(""), session);
			controller.getTerminal1().put(address.getRawValue().toHexString(""), session);
		}
		if((Boolean)session.getAttribute("isLogin")){
			System.out.println(address.getDistrict()+address.getTerminalAddress()+"登录回复耗时"+
				(System.currentTimeMillis()-(long)session.getAttribute("login"))+"毫秒");
			session.setAttribute("isLogin",false);
		}
		if((Boolean)session.getAttribute("isData")){
			System.out.println(address.getDistrict()+address.getTerminalAddress()+"数据发送耗时"+
				(System.currentTimeMillis()-(long)session.getAttribute("data"))+"毫秒");
			session.setAttribute("isData",false);
			SimpleBytes sb=new SimpleBytes("68 5A 01 5A 01 68 A8 99 34 DB 28 02 13 62 00 00 04 00 1F 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 5A 40 AE 16 68 4A 00 4A 00 68 0B",16,true);
			SimpleBytes sb1=new SimpleBytes("05 64 56 61 00 00 69 00 00 04 00 02 00",16,true);
			SimpleBytes sb3=new SimpleBytes("00 01 00 00 9B 16 68 4A 00 4A 00 68 0B 12 7A 1D 87 00 00 69 00 00 04 00 02 00 00 01 00 00 AB 16",16,true);
			session.write(IoBuffer.wrap(sb.getBytesArray()));
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.write(IoBuffer.wrap(sb1.getBytesArray()));
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.write(IoBuffer.wrap(sb3.getBytesArray()));
		}
		
	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception{
		session.setAttribute("login",System.currentTimeMillis());
		session.setAttribute("isLogin",true);

		
	}
	@Override
    public void sessionOpened(IoSession session) throws Exception {
		session.setAttribute("login",System.currentTimeMillis());
		session.setAttribute("data",System.currentTimeMillis());
		session.setAttribute("isLogin",false);
		session.setAttribute("isData",false);
    }
}

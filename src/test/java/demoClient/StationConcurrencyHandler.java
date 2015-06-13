package demoClient;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StationConcurrencyHandler extends IoHandlerAdapter {
	private Controller controller;
	
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	private static Logger logger = LoggerFactory.getLogger(StationConcurrencyHandler.class);
	private static int count=0;
	
	@Override
	public void messageReceived(final IoSession session, final Object message)
			throws Exception {
		System.out.println(String.format("receive from terminal %s,packet is %s",count,message));
		count++;
		
	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception{
		
		
	}
	@Override
    public void sessionOpened(IoSession session) throws Exception {
        
    }
}

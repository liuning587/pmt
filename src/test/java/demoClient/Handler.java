package demoClient;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class Handler extends IoHandlerAdapter{
	@Override
	public void messageReceived(final IoSession session, final Object message)
			throws Exception {
		System.out.println(message);
	}
}

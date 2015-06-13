package cdtest;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class QuickConnect {
	public static void main(String[] args) throws InterruptedException {
		Handler handler=new Handler();
		NioSocketConnector connector=new NioSocketConnector();
		connector.setConnectTimeoutMillis(30 * 1000L);

		connector.getSessionConfig().setUseReadOperation(true);
		connector.getSessionConfig().setSendBufferSize(4096);
		connector.setHandler(handler);
		
		InetSocketAddress remote=new InetSocketAddress("127.0.0.1", 8415);
		connector.setDefaultRemoteAddress(remote);
		
		while(true){
			Thread.sleep(1000);
			ConnectFuture connectFuture = connector.connect();
			connectFuture.awaitUninterruptibly(30000);
			IoSession ioSession = connectFuture.getSession();
			Thread.sleep(100);
			ioSession.close(false);
		}
	}
}

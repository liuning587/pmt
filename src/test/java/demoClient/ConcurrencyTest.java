package demoClient;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sx.mmt.internal.connection.filter.GBProtocolCodecFactory;

public class ConcurrencyTest {
	public void start(IoHandlerAdapter handler) {
		NioSocketConnector connector;
		connector = new NioSocketConnector();

		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new GBProtocolCodecFactory()));
		
		connector.setConnectTimeoutMillis(30 * 1000L);

		connector.getSessionConfig().setUseReadOperation(true);
		connector.getSessionConfig().setSendBufferSize(4096);

		connector.setHandler(handler);
		
		InetSocketAddress remote=new InetSocketAddress("127.0.0.1", 6103);
		

		
		for(int i=0;i<500;i++){
			ConnectFuture connectFuture = connector.connect(remote);
			connectFuture.awaitUninterruptibly(30000);
			
			IoSession ioSession = connectFuture.getSession();
			ioSession.write(IoBuffer.wrap(PacketFactory.getLoginPacket().getBytesArray()));
		}
	}

}

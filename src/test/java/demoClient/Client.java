package demoClient;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sx.mmt.internal.util.SimpleBytes;

public class Client {
	public static void main(String[] args) {
		NioSocketConnector connector;
		connector = new NioSocketConnector();

		connector.getFilterChain().addLast("threadPool",
				new ExecutorFilter(Executors.newCachedThreadPool()));
//		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
//				new GbProtocolCodecFactory()));
		
		connector.setConnectTimeoutMillis(30 * 1000L);

		connector.getSessionConfig().setUseReadOperation(true);
		connector.getSessionConfig().setSendBufferSize(4096);

		connector.setHandler(new Handler());
		
		InetSocketAddress remote=new InetSocketAddress("127.0.0.1", 6001);
		
		ConnectFuture connectFuture = connector.connect(remote);
		connectFuture.awaitUninterruptibly(30000);
		
		IoSession ioSession = connectFuture.getSession();
		
		SimpleBytes sb=new SimpleBytes("68 5A 01 5A 01 68 A8 99 34 DB 28 02 13 62 00 00 04 00 1F 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 5A 40 AE 16 68 4A 00 4A 00 68 0B",16,true);
		SimpleBytes sb1=new SimpleBytes("05 64 56 61 00 00 69 00 00 04 00 02 00 00 01 00 00 9B 16",16,true);
		ioSession.write(IoBuffer.wrap(sb.getBytesArray()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ioSession.write(IoBuffer.wrap(sb1.getBytesArray()));
	}
}

package com.sx.mmt.testTerminal;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sx.mmt.internal.connection.filter.GBProtocolCodecFactory;


public class TerminalConnector {
	private NioSocketConnector connector;
	public void start(TerminalHandler handler) {
		
		connector = new NioSocketConnector();

		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new GBProtocolCodecFactory()));
		connector.setConnectTimeoutMillis(30 * 1000L);

		connector.getSessionConfig().setUseReadOperation(true);
		connector.getSessionConfig().setSendBufferSize(4096);

		connector.setHandler(handler);
		
		InetSocketAddress remote=new InetSocketAddress("127.0.0.1", 8415);
		connector.setDefaultRemoteAddress(remote);

		
		
	}
	
	public IoSession getSession(){
		ConnectFuture connectFuture = connector.connect();
		connectFuture.awaitUninterruptibly(30000);
		
		IoSession ioSession = connectFuture.getSession();
		return ioSession;
	}
}

package com.sx.mmt.internal.connection.fepModel;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.connection.filter.FrontModelLog;
import com.sx.mmt.internal.connection.filter.GBProtocolCodecFactory;
import com.sx.mmt.internal.util.PropertiesUtil;


public class TerminalConnectServer {
	private SocketAcceptor acceptor = null;
	
	
	private TerminalMessageHandler terminalMessageHandler;
	public void setTerminalMessageHandler(
			TerminalMessageHandler terminalMessageHandler) {
		this.terminalMessageHandler = terminalMessageHandler;
	}

	public void init(ConnectConfig config) throws IOException{
		acceptor=new NioSocketAcceptor(Runtime.getRuntime()
				.availableProcessors()+1);
		
		SocketSessionConfig sessionConfig=acceptor.getSessionConfig();
		sessionConfig.setReadBufferSize(4096);
		sessionConfig.setReceiveBufferSize(4096);
		sessionConfig.setSendBufferSize(4096);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSoLinger(0);
		sessionConfig.setIdleTime(IdleStatus.BOTH_IDLE, 600);
		sessionConfig.setReuseAddress(true);
		acceptor.setHandler(terminalMessageHandler);
		DefaultIoFilterChainBuilder chainBuilder= acceptor.getFilterChain();
		
		chainBuilder.addLast("codec", 
				new ProtocolCodecFilter(new GBProtocolCodecFactory()));
		chainBuilder.addLast("log", new FrontModelLog());
		int ListenerPort=Integer.valueOf(
				config.getAttr().get((ConfigConstants.ListenerPort)));
		acceptor.bind(new InetSocketAddress(ListenerPort));
	}
	
	public void dispose(){
		if(acceptor!=null){
			acceptor.unbind();
			acceptor.dispose();
		}
	}
}

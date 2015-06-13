package com.sx.mmt.fep;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.sx.mmt.constants.FepConstants;
import com.sx.mmt.internal.connection.filter.FrontModelLog;
import com.sx.mmt.internal.connection.filter.GBProtocolCodecFactory;

public class TerminalServer {
	private SocketAcceptor acceptor = null;
	
	private TerminalHandler terminalHandler;
	
	public TerminalServer(TerminalHandler terminalHandler){
		this.terminalHandler=terminalHandler;
	}
	
	public void StartServer(int port){
		try{
			acceptor=new NioSocketAcceptor(Runtime.getRuntime()
					.availableProcessors()+1);
			SocketSessionConfig sessionConfig=acceptor.getSessionConfig();
			sessionConfig.setReadBufferSize(4096);
			sessionConfig.setReceiveBufferSize(4096);
			sessionConfig.setSendBufferSize(4096);
			sessionConfig.setTcpNoDelay(true);
			sessionConfig.setSoLinger(0);
			sessionConfig.setReaderIdleTime(FepConstants.TIME_OUT/1000);
			sessionConfig.setWriterIdleTime(FepConstants.TIME_OUT/1000);
			sessionConfig.setReuseAddress(true);
			
			acceptor.setHandler(terminalHandler);
			DefaultIoFilterChainBuilder chainBuilder= acceptor.getFilterChain();
			
			chainBuilder.addLast("codec", 
					new ProtocolCodecFilter(new GBProtocolCodecFactory()));
			chainBuilder.addLast("log", new FrontModelLog());
			acceptor.bind(new InetSocketAddress(port));
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("server start failed ,please check port is in use");
		}
	}
	
	public void StopServer(){
		if(acceptor!=null){
			acceptor.unbind();
			acceptor.dispose();
		}
	}
}

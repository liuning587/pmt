package com.sx.mmt.fep;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.sx.mmt.constants.FepConstants;
import com.sx.mmt.internal.connection.filter.GBProtocolCodecFactory;
import com.sx.mmt.internal.connection.filter.StationModelLog;

public class StationServer {
	private SocketAcceptor acceptor = null;
	
	private StationHandler stationHandler;
	
	public StationServer(StationHandler stationHandler){
		this.stationHandler=stationHandler;
	}
	
	public void StartServer(int port){
		try{
			acceptor=new NioSocketAcceptor();
			acceptor.setHandler(stationHandler);
			DefaultIoFilterChainBuilder chainBuilder= acceptor.getFilterChain();
			SocketSessionConfig sessionConfig=acceptor.getSessionConfig();
			sessionConfig.setReadBufferSize(4096);
			sessionConfig.setReceiveBufferSize(4096);
			sessionConfig.setSendBufferSize(4096);
			sessionConfig.setTcpNoDelay(true);
			sessionConfig.setSoLinger(0);
			sessionConfig.setReaderIdleTime(FepConstants.TIME_OUT/1000);
			sessionConfig.setWriterIdleTime(FepConstants.TIME_OUT/1000);
			sessionConfig.setReuseAddress(true);
			
			chainBuilder.addLast("codec", 
					new ProtocolCodecFilter(new GBProtocolCodecFactory()));
			chainBuilder.addLast("log1", new StationModelLog());
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

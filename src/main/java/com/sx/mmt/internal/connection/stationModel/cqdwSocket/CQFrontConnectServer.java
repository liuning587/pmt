package com.sx.mmt.internal.connection.stationModel.cqdwSocket;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.connection.stationModel.transparent.FrontConnectServer;
import com.sx.mmt.internal.util.PropertiesUtil;

public class CQFrontConnectServer {
	private static Logger logger = LoggerFactory.getLogger(FrontConnectServer.class);
	private SocketConnector connector=null;
	private CQFrontMessageHandler handler;
	
	public void setHandler(CQFrontMessageHandler handler) {
		this.handler = handler;
	}

	public void init(ConnectConfig config){
		connector=new NioSocketConnector();
		connector.setConnectTimeoutMillis(PropertiesUtil
				.parseToInt(ConfigConstants.ConnectionTimeout));
		
		InetSocketAddress address=new InetSocketAddress(
				config.getAttr().get(ConfigConstants.FrontAddress),
				Integer.parseInt(config.getAttr().get(ConfigConstants.FrontPort)));
		
		connector.setDefaultRemoteAddress(address);
		SocketSessionConfig sessionConfig=connector.getSessionConfig();
		sessionConfig.setReadBufferSize(4096);
		sessionConfig.setReceiveBufferSize(4096);
		sessionConfig.setSendBufferSize(4096);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSoLinger(0);
		sessionConfig.setWriterIdleTime(
				PropertiesUtil.parseToInt(ConfigConstants.IdleTimeReconnectionWriter));
		connector.setHandler(handler);
		DefaultIoFilterChainBuilder chainBuilder= connector.getFilterChain();
		chainBuilder.addLast("codec", 
				new ProtocolCodecFilter(new CQProtocolCodecFactory()));
		chainBuilder.addLast("log", new CQLog());	
	}
	
	public synchronized IoSession getNewConnection(){
		ConnectFuture future = connector.connect();
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		return session;
	}
	
	public synchronized IoSession reConnection(){
		while(true){
			try{
				Thread.sleep(5000);
				ConnectFuture future = connector.connect();
				future.awaitUninterruptibly();
				IoSession session = future.getSession();
				if(session.isConnected()){
					logger.info("连接前置机成功");
					return session;
				}
				
			}catch(Exception e){
				logger.error("连接前置机失败，5秒后重试");
			}
		}
	}
}

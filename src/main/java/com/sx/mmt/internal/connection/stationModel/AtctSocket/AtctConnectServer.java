package com.sx.mmt.internal.connection.stationModel.AtctSocket;

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
import com.sx.mmt.constants.ConnectionConstants;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.connection.filter.GBProtocolCodecFactory;
import com.sx.mmt.internal.connection.filter.StationModelLog;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.swingUI.ActionNowDisplay;

public class AtctConnectServer {
	private static Logger logger = LoggerFactory.getLogger(AtctConnectServer.class);
	private SocketConnector connector=null;
	private AtctMessageHandler handler;
	
	public void setHandler(AtctMessageHandler handler) {
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
				new ProtocolCodecFilter(new AtctProtocolCodecFactory()));
		chainBuilder.addLast("log", new StationModelLog());	
	}
	
	public synchronized IoSession getNewConnection(){
		ConnectFuture future = connector.connect();
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		return session;
	}
	
	public synchronized IoSession getNewConnection(String terminalAddress){
		try{
			ConnectFuture future = connector.connect();
			future.awaitUninterruptibly();
			IoSession session = future.getSession();
			if(session.isConnected()){
				logger.info(terminalAddress+" 连接前置机成功");
				ActionNowDisplay.appendTerminalShow(terminalAddress+" 连接前置机成功");
				session.setAttribute(ConnectionConstants.TerminalAddress, terminalAddress);
				String request=String.format("%s%s%s%s%s", "ATCT",
						terminalAddress.substring(0,4),"+",terminalAddress.substring(4),"+3001");
				session.write(new SimpleBytes(request.getBytes()));
				return session;
			}
		}catch(Exception e){
			logger.info(terminalAddress+" 连接前置机失败");
			ActionNowDisplay.appendTerminalShow(terminalAddress+" 连接前置机失败");
		}
		return null;
	}
}

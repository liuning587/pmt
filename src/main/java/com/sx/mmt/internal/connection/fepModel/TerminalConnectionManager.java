package com.sx.mmt.internal.connection.fepModel;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.ConnectService;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.connection.filter.FrontModelLog;
import com.sx.mmt.internal.connection.filter.GBProtocolCodecFactory;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.task.TaskImplDao;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TerminalConnectionManager implements ConnectService{
	private static Logger logger = LoggerFactory.getLogger(TerminalConnectionManager.class);
	private Map<String,Long> terminalSessionMapping;
//	private Map<Long,IoSession> openedTerminalSession;
//	private TerminalConnectServer terminalConnectServer;
	private TerminalMessageHandler terminalMessageHandler;
	private SocketAcceptor acceptor = null;
	private TaskManager taskManager;
	private EncodedDataSendingDelayQueue sendQueue;
	private SendingPacketRobot sendingPacketRobot;
	
	private UncaughtExceptionHandler handler=new myUncaughtExceptionHandler();
	
	public TerminalConnectionManager(){
		terminalSessionMapping=new ConcurrentHashMap<String, Long>();
//		openedTerminalSession=new ConcurrentHashMap<Long, IoSession>();
//		terminalConnectServer=new TerminalConnectServer();
		terminalMessageHandler=new TerminalMessageHandler();
//		terminalConnectServer.setTerminalMessageHandler(terminalMessageHandler);
		terminalMessageHandler.setTerminalConnection(this);
		
		GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
				SpringBeanUtil.getBean("gBProtocolBreakerPool");
		TaskImplDao taskImplDao=(TaskImplDao)SpringBeanUtil.getBean("taskImplDao");
		taskManager=(TaskManager)SpringBeanUtil.getBean("taskManager");
		terminalMessageHandler.setPbPool(pbPool);
		terminalMessageHandler.setTaskImplDao(taskImplDao);
		terminalMessageHandler.setTaskManager(taskManager);
		
		sendQueue=
				(EncodedDataSendingDelayQueue)SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
	}
	
	public void initializeAcceptor(ConnectConfig config) throws IOException{
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
		
		DefaultIoFilterChainBuilder chainBuilder= acceptor.getFilterChain();
		
		chainBuilder.addLast("codec", 
				new ProtocolCodecFilter(new GBProtocolCodecFactory()));
		chainBuilder.addLast("threadPool",
				new ExecutorFilter(Executors.newCachedThreadPool()));
		chainBuilder.addLast("log", new FrontModelLog());
		acceptor.setHandler(terminalMessageHandler);
		int ListenerPort=Integer.valueOf(
				config.getAttr().get((ConfigConstants.ListenerPort)));
		acceptor.bind(new InetSocketAddress(ListenerPort));
	}
	

	public Map<String, Long> getTerminalSessionMapping() {
		return terminalSessionMapping;
	}
//
//	public Map<Long, IoSession> getOpenedTerminalSession() {
//		return openedTerminalSession;
//	}

	public void processReceivePacket(SimpleBytes packet,String terminalAddress){
		EncodedPacket message=new EncodedPacket();
		message.setTerminalAddress(terminalAddress);
		message.setTaskId(terminalAddress);
		message.setPacket(packet);
		taskManager.receivePacket(message);
		
	}
	
	private class myUncaughtExceptionHandler implements UncaughtExceptionHandler{
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error(t.getName()+"线程失效");
			logger.error(ErrorTool.getErrorInfoFromException(e));
			Thread SendingPacketThread=new Thread(sendingPacketRobot);
			SendingPacketThread.setName("SendingPacketThread");
			SendingPacketThread.setUncaughtExceptionHandler(handler);
			SendingPacketThread.setDaemon(true);
			SendingPacketThread.start();
			logger.error(t.getName()+"线程恢复");
		}
	}
	@Override
	public void startServer(ConnectConfig config) throws Exception{
		initializeAcceptor(config);
		terminalMessageHandler.setConfig(config);
		sendingPacketRobot=new SendingPacketRobot();
		Thread SendingPacketThread=new Thread(sendingPacketRobot);
		SendingPacketThread.setName("SendingPacketThread");
		SendingPacketThread.setUncaughtExceptionHandler(handler);
		SendingPacketThread.setDaemon(true);
		SendingPacketThread.start();

	}
	@Override
	public void stopServer() throws Exception{
		if(sendingPacketRobot!=null){
			sendingPacketRobot.stopRobot();
		}
		if(acceptor!=null){
			acceptor.unbind();
			acceptor.dispose();
		}
		
	}
	@Override
	public boolean testConnection(ConnectConfig config) throws Exception {
		try{
			if(acceptor==null || !acceptor.isActive()){
				initializeAcceptor(config);
			}
			if(acceptor.isActive()){
				return true;
			}else{
				return false;
			}
			
		}catch(Exception e){
			return false;
		}
		
	};

	private class SendingPacketRobot implements Runnable{
		private volatile boolean stopRequested;
		private Thread runThread;
		@Override
		public void run() {
			stopRequested = false;
			runThread = Thread.currentThread();
			while(!stopRequested){
				EncodedPacket packet=null;
				try {
					packet = sendQueue.getPacket();
				} catch (InterruptedException e) {
				}
				Long sesseionId=-1L;
				if(packet!=null && !StringUtils.isBlank(packet.getTerminalAddress())){
					sesseionId=terminalSessionMapping.get(packet.getTerminalAddress());
				}
				if(sesseionId!=null && sesseionId>0){
					IoSession session=acceptor.getManagedSessions().get(sesseionId);
					if(session!=null && session.isConnected()){
						if(packet.getPacket()!=null){
							session.write(packet.getPacket());
						}
					}else{
						taskManager.getOnlineList().put(packet.getTerminalAddress(), ViewConstants.Offline);
						logger.info(packet.getTerminalAddress()+"terminal is offline ,discard packet");
					}
				}else{
					if(packet!=null && !StringUtils.isBlank(packet.getTerminalAddress())){
						taskManager.getOnlineList().put(packet.getTerminalAddress(), ViewConstants.Offline);
						logger.info(packet.getTerminalAddress()+"terminal is offline ,discard packet");
					}
					
				}	
			}
						
		}
		
		public void stopRobot(){
			stopRequested=true;
	        if ( runThread != null ) {
	            runThread.interrupt();
	        }
		}
	}
	public SocketAcceptor getAcceptor() {
		return acceptor;
	}

	public void setAcceptor(SocketAcceptor acceptor) {
		this.acceptor = acceptor;
	}
	
	
}

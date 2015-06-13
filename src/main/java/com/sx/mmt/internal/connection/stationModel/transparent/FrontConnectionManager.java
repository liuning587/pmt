package com.sx.mmt.internal.connection.stationModel.transparent;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.ConnectService;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class FrontConnectionManager implements ConnectService{
	private static Logger logger = LoggerFactory.getLogger(FrontConnectionManager.class);
	private volatile IoSession session;
	private FrontConnectServer frontConnectServer;
	private FrontMessageHandler frontMessageHandler;
	private TaskManager taskManager;
	private EncodedDataSendingDelayQueue sendQueue;
	private SendingPacketRobot sendingPacketRobot;
	private UncaughtExceptionHandler handler=new myUncaughtExceptionHandler();
	
	public FrontConnectionManager(){
		frontMessageHandler=new FrontMessageHandler();
		frontMessageHandler.setFrontConnectionManager(this);
		frontConnectServer=new FrontConnectServer();
		frontConnectServer.setHandler(frontMessageHandler);
		taskManager=(TaskManager)SpringBeanUtil.getBean("taskManager");
		sendQueue=
				(EncodedDataSendingDelayQueue)SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		
	}
	
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
	public void startServer(ConnectConfig config){
		frontConnectServer.init(config);
		session=frontConnectServer.getNewConnection();
		sendingPacketRobot=new SendingPacketRobot();
		Thread SendingPacketThread=new Thread(sendingPacketRobot);
		SendingPacketThread.setName("SendingPacketThread");
		SendingPacketThread.setUncaughtExceptionHandler(handler);
		SendingPacketThread.setDaemon(true);
		SendingPacketThread.start();
	}
	@Override
	public void stopServer(){
		if(sendingPacketRobot!=null){
			sendingPacketRobot.stopRobot();
		}
		if(session!=null){
			session.close(false);
		}

		
	}
	@Override
	public boolean testConnection(ConnectConfig config) throws Exception {
		try{
			frontConnectServer.init(config);
			IoSession sess=frontConnectServer.getNewConnection();
			if(sess!=null && sess.isConnected()){
				sess.close(false);
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
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
				if(packet!=null){
					if(session!=null){
						session.write(packet.getPacket());
					}else{
						logger.info(packet.getTerminalAddress()+"与前置机连接丢失，丢弃报文");
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

	public void reconnect(){
		if(session!=null){
			session.close(false);
		}
		session=frontConnectServer.reConnection();
	}

	public IoSession getSession() {
		return session;
	}



	public void setSession(IoSession session) {
		this.session = session;
	}



	public FrontConnectServer getFrontConnectServer() {
		return frontConnectServer;
	}



	public void setFrontConnectServer(FrontConnectServer frontConnectServer) {
		this.frontConnectServer = frontConnectServer;
	}



	public FrontMessageHandler getFrontMessageHandler() {
		return frontMessageHandler;
	}



	public void setFrontMessageHandler(FrontMessageHandler frontMessageHandler) {
		this.frontMessageHandler = frontMessageHandler;
	}
	
	
	

}

package com.sx.mmt.internal.connection.stationModel.AtctSocket;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.ConnectService;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.task.TaskImplDao;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class AtctConnectionManager implements ConnectService{
	private static Logger logger = LoggerFactory.getLogger(AtctConnectionManager.class);
	private volatile Map<String,IoSession> sessions;
	private AtctConnectServer atctConnectServer;
	private AtctMessageHandler atctMessageHandler;
	private TaskManager taskManager;
	private ExecutorService executorPool=Executors.newSingleThreadExecutor();
	private EncodedDataSendingDelayQueue sendQueue;
	private SendingPacketRobot sendingPacketRobot;
	private UncaughtExceptionHandler handler=new myUncaughtExceptionHandler();
	
	public AtctConnectionManager(){
		sendQueue=
				(EncodedDataSendingDelayQueue)SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		TaskImplDao taskImplDao=(TaskImplDao)SpringBeanUtil.getBean("taskImplDao");
		taskManager=(TaskManager)SpringBeanUtil.getBean("taskManager");
		
		atctMessageHandler=new AtctMessageHandler();
		atctMessageHandler.setAtctConnectionManager(this);
		atctConnectServer=new AtctConnectServer();
		sessions=new ConcurrentHashMap<String, IoSession>();
		atctConnectServer.setHandler(atctMessageHandler);
		
		
		atctMessageHandler.setTaskManager(taskManager);
		atctMessageHandler.setEncodedDataSendingDelayQueue(sendQueue);
		atctMessageHandler.setTaskImplDao(taskImplDao);

		
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
		atctConnectServer.init(config);
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
		if(sessions!=null){
			for(IoSession sess:sessions.values()){
				sess.close(false);
			}
		}

		
	}
	@Override
	public boolean testConnection(ConnectConfig config) throws Exception {
		try{
			atctConnectServer.init(config);
			IoSession sess=atctConnectServer.getNewConnection();
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
				IoSession sess=sessions.get(packet.getTerminalAddress());
				if(sess==null){
					final String terminalAddress=packet.getTerminalAddress();
					executorPool.execute(new Runnable() {
						@Override
						public void run() {
							IoSession sess=atctConnectServer.getNewConnection(terminalAddress);
							if(sess!=null){
								sessions.put(terminalAddress, sess);
							}
						}
					});
					sendQueue.addPacket(packet,System.currentTimeMillis()+5000);
				}else{
					sess.write(packet.getPacket());
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


	public void reconnect(final String terminalAddress){
		executorPool.execute(new Runnable() {
			@Override
			public void run() {
				IoSession sess=atctConnectServer.getNewConnection(terminalAddress);
				if(sess!=null){
					sessions.put(terminalAddress, sess);
				}
			}
		});
	}

	public Map<String, IoSession> getSessions() {
		return sessions;
	}

	public void setSessions(Map<String, IoSession> sessions) {
		this.sessions = sessions;
	}

	public AtctConnectServer getAtctConnectServer() {
		return atctConnectServer;
	}

	public void setAtctConnectServer(AtctConnectServer atctConnectServer) {
		this.atctConnectServer = atctConnectServer;
	}

	public AtctMessageHandler getAtctMessageHandler() {
		return atctMessageHandler;
	}

	public void setAtctMessageHandler(AtctMessageHandler atctMessageHandler) {
		this.atctMessageHandler = atctMessageHandler;
	}



	
	
	
	
}

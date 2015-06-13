package com.sx.mmt.internal.connection.stationModel.cqdwSocket;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IoSession;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.ConnectService;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SpringBeanUtil;


public class CQFrontConnectionManager implements ConnectService{
	private static Logger logger = LoggerFactory.getLogger(CQFrontConnectionManager.class);
	private volatile IoSession session;
	private CQFrontConnectServer frontConnectServer;
	private CQFrontMessageHandler frontMessageHandler;
	private TaskManager taskManager;
	private EncodedDataSendingDelayQueue sendQueue;
	private SendingPacketRobot sendingPacketRobot;
	private ExecutorService executorPool;
	private ConnectConfig config;
	private UncaughtExceptionHandler handler=new myUncaughtExceptionHandler();
	
	public CQFrontConnectionManager(){
		frontMessageHandler=new CQFrontMessageHandler();
		frontMessageHandler.setFrontConnectionManager(this);
		frontConnectServer=new CQFrontConnectServer();
		frontConnectServer.setHandler(frontMessageHandler);
		executorPool=Executors.newCachedThreadPool();
		taskManager=(TaskManager)SpringBeanUtil.getBean("taskManager");
		sendQueue=
				(EncodedDataSendingDelayQueue)SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
	}
	
	public void processReceivePacket(final Document doc){
		executorPool.execute(new Runnable() {
			@Override
			public void run() {
				taskManager.receiveCqPacket(doc);
				
			}
		});
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
		this.config=config;
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
		if(session!=null){
			session.close(false);
		}
		if(sendingPacketRobot!=null){
			sendingPacketRobot.stopRobot();
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
			runThread = Thread.currentThread();
			stopRequested = false;
			while(!stopRequested){
				EncodedPacket packet=null;
				try {
					packet = sendQueue.getPacket();
				} catch (InterruptedException e) {
				}
				if(session!=null){
					if(packet.getPacket()==null && packet.getAdditionalInfo()!=null){
						session.write(packet.getAdditionalInfo());
					}else{
						Document doc=new Document();
						Element rootEl=new Element(OtherConstants.para);
						doc.setRootElement(rootEl);
						rootEl.addContent(new Element(OtherConstants.terminalId)
							.setText(packet.getTaskId()));
						rootEl.addContent(new Element(OtherConstants.recordNo)
							.setText(config.getAttr().get(ConfigConstants.ChannelNo)));
						rootEl.addContent(new Element(OtherConstants.frameContent)
									.setText(packet.getPacket().toReverseHexString("")));
						rootEl.addContent(new Element(OtherConstants.timeOut)
								.setText("50000"));
						rootEl.addContent(new Element(OtherConstants.needResponse)
									.setText(packet.isNeedResponse()?"1":"0"));
						session.write(doc);
					}
				}else{
					logger.info(packet.getTerminalAddress()+"与前置机连接丢失，丢弃报文");
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

	public CQFrontConnectServer getFrontConnectServer() {
		return frontConnectServer;
	}

	public void setFrontConnectServer(CQFrontConnectServer frontConnectServer) {
		this.frontConnectServer = frontConnectServer;
	}

	public CQFrontMessageHandler getFrontMessageHandler() {
		return frontMessageHandler;
	}

	public void setFrontMessageHandler(CQFrontMessageHandler frontMessageHandler) {
		this.frontMessageHandler = frontMessageHandler;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
}

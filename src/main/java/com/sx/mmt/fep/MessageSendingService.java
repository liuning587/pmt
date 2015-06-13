package com.sx.mmt.fep;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sx.mmt.constants.FepConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.SimpleBytes;

@Service
public final class MessageSendingService {
	private Map<String,IoSession> sessions;;
	private volatile IoSession session;
	private SendingPacketRobot messageSendingRobot;
	private ExecutorService ThreadPool;
	private boolean IsMultipleMode;
	private EncodedDataSendingDelayQueue encodedDataSendingQueue;
	
	private static Logger logger = LoggerFactory.getLogger(MessageSendingService.class);
	public MessageSendingService(){
		IsMultipleMode=false;
		messageSendingRobot=new SendingPacketRobot();
		encodedDataSendingQueue=new EncodedDataSendingDelayQueue();
		ThreadPool=Executors.newSingleThreadExecutor();
	}
	public MessageSendingService(boolean IsMultipleMode){
		this.IsMultipleMode=IsMultipleMode;
		if(IsMultipleMode){
			sessions=new ConcurrentHashMap<String,IoSession>();
		}
		messageSendingRobot=new SendingPacketRobot();
		encodedDataSendingQueue=new EncodedDataSendingDelayQueue();
		ThreadPool=Executors.newSingleThreadExecutor();
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
					packet = encodedDataSendingQueue.getPacket();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(packet!=null){
					if(IsMultipleMode){
						String terminalId=packet.getTerminalAddress();
						IoSession sessionLocal=sessions.get(terminalId);
						if(sessionLocal!=null && 
								System.currentTimeMillis()-(long)sessionLocal.getAttribute(FepConstants.LAST_READ_TIME)<FepConstants.TIME_OUT
								&& (Boolean)sessionLocal.getAttribute(FepConstants.IS_LOGIN)){
							try{
								sessionLocal.write(packet);
							}catch(Exception e){
								logger.error(String.format("terminal %s is offline,packet %s will discard",
										terminalId,packet));
								sessions.remove(terminalId);
								sessionLocal.close(true);
							}
						}else{
							logger.info(String.format("terminal %s is offline,packet %s will discard",
									terminalId,packet));
							sessions.remove(terminalId);
						}
					}else{
						if(session!=null){
							try{
								session.write(packet);
							}catch(Exception e){
								logger.error("session for send message is closed unexpectly");
								session=null;
								encodedDataSendingQueue.addPacket(packet);
							}
						}
					}
				}
				try{
					Thread.sleep(1);
				}catch(InterruptedException e){
					Thread.currentThread().interrupt();
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
	
	public void stopService(){
		messageSendingRobot.stopRobot();
	}
	
	public void putSession(String name,IoSession session){
		if(IsMultipleMode){
			sessions.put(name, session);
		}else{
			this.session=session;
		}
	}
	public void removeSession(String name){
		if(IsMultipleMode){
			sessions.remove(name);
		}else{
			this.session=null;
		}
	}
	public List<String> getSessionKeyList(){
		if(IsMultipleMode){
			return Lists.newArrayList(sessions.keySet());
		}else{
			return Lists.newArrayList(String.valueOf(session.getId()));
		}
	}
	public IoSession getSession(String name){
		if(IsMultipleMode){
			return sessions.get(name);
		}else{
			return this.session;
		}
	}
	public void dispose(){
		if(session!=null && session.isConnected()){
			session.close(false);
		}
		if(sessions!=null){
			for(IoSession session:sessions.values()){
				if(session!=null && session.isConnected()){
					session.close(false);
				}
			}
		}
		messageSendingRobot.stopRobot();
	}
	public void setSession(IoSession session){
		this.session=session;
	}
	
	public void startService(){
		ThreadPool.execute(messageSendingRobot);
	}
	
	public EncodedDataSendingDelayQueue getEncodedDataSendingQueue() {
		return encodedDataSendingQueue;
	}
	
	public synchronized int getCurrentSessionSize(){
		if(IsMultipleMode){
			return sessions.size();
		}else{
			if(this.session!=null){
				return 1;
			}else return 0;
		}
	}
	
	public boolean isAlive(){
		return !ThreadPool.isTerminated();
	}
}

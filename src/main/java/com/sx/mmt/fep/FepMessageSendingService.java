package com.sx.mmt.fep;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sx.mmt.constants.FepConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.EncodedDataSendingPriorityQueue;
import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.SimpleBytes;

public class FepMessageSendingService {
	private Map<String,IoSession> sessions;;
	private SendingPacketRobot messageSendingRobot;
	private CarryPacketRobot messageCarryRobot;
	private ExecutorService ThreadPool;
	private EncodedDataSendingDelayQueue encodedDataSendingDelayQueue;
	private EncodedDataSendingPriorityQueue encodedDataSendingPriorityQueue;
	
	private static Logger logger = LoggerFactory.getLogger(FepMessageSendingService.class);
	public FepMessageSendingService(){
		sessions=new ConcurrentHashMap<String,IoSession>();
		messageSendingRobot=new SendingPacketRobot();
		messageCarryRobot=new CarryPacketRobot();
		encodedDataSendingDelayQueue=new EncodedDataSendingDelayQueue();
		encodedDataSendingPriorityQueue=new EncodedDataSendingPriorityQueue();
		ThreadPool=Executors.newFixedThreadPool(2);
	}
	private class CarryPacketRobot implements Runnable{
		private volatile boolean stopRequested;
		private Thread runThread;
		@Override
		public void run() {
			runThread = Thread.currentThread();
			stopRequested = false;
			while(!stopRequested){
				EncodedPacket packet=null;
				try {
					packet = encodedDataSendingDelayQueue.getPacket();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(packet!=null){
					encodedDataSendingPriorityQueue.addPacket(packet, 5);
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
	
	private class SendingPacketRobot implements Runnable{
		private volatile boolean stopRequested;
		private Thread runThread;
		@Override
		public void run() {
			runThread = Thread.currentThread();
			stopRequested = false;
			while(!stopRequested){
				EncodedPacket priority=encodedDataSendingPriorityQueue.getPacket();
				if(priority!=null){
					String terminalId=priority.getTerminalAddress();
					IoSession sessionLocal=sessions.get(terminalId);
					if(sessionLocal!=null && 
							System.currentTimeMillis()-(long)sessionLocal.getAttribute(FepConstants.LAST_READ_TIME)<FepConstants.TIME_OUT
							&& (Boolean)sessionLocal.getAttribute(FepConstants.IS_LOGIN)){
						try{
							sessionLocal.write(priority);
						}catch(Exception e){
							logger.error(String.format("terminal %s is offline,packet %s will discard",
									terminalId,priority));
							sessions.remove(terminalId);
							sessionLocal.close(true);
						}
					}else{
						logger.info(String.format("terminal %s is offline,packet %s will discard",
								terminalId,priority));
						sessions.remove(terminalId);
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
	
	public void stopService(){
		messageSendingRobot.stopRobot();
		messageCarryRobot.stopRobot();
	}
	
	public void putSession(String name,IoSession session){
		sessions.put(name, session);
	}
	public void removeSession(String name){
		sessions.remove(name);
	}
	public List<String> getSessionKeyList(){
		return Lists.newArrayList(sessions.keySet());
	}
	public IoSession getSession(String name){
		return sessions.get(name);
	}
	public void dispose(){
		if(sessions!=null){
			for(IoSession session:sessions.values()){
				if(session!=null && session.isConnected()){
					session.close(false);
				}
			}
		}
		messageSendingRobot.stopRobot();
	}

	
	public void startService(){
		ThreadPool.execute(messageSendingRobot);
		ThreadPool.execute(messageCarryRobot);
	}
	
	public EncodedDataSendingDelayQueue getEncodedDataSendingDelayQueue() {
		return encodedDataSendingDelayQueue;
	}
	
	public synchronized int getCurrentSessionSize(){
		return sessions.size();
	}
	
	public boolean isAlive(){
		return !ThreadPool.isTerminated();
	}

	public EncodedDataSendingPriorityQueue getEncodedDataSendingPriorityQueue() {
		return encodedDataSendingPriorityQueue;
	}
	
	
}

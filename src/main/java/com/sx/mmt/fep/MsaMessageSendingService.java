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
import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.SimpleBytes;

public class MsaMessageSendingService {
	private Map<Integer,IoSession> sessions;;
	private volatile IoSession session;
	private SendingPacketRobot messageSendingRobot;
	private ExecutorService ThreadPool;
	private boolean IsMultipleMode;
	private EncodedDataSendingDelayQueue encodedDataSendingQueue;
	
	private static Logger logger = LoggerFactory.getLogger(MessageSendingService.class);
	public MsaMessageSendingService(){
		IsMultipleMode=false;
		messageSendingRobot=new SendingPacketRobot();
		encodedDataSendingQueue=new EncodedDataSendingDelayQueue();
		ThreadPool=Executors.newSingleThreadExecutor();
	}
	public MsaMessageSendingService(boolean IsMultipleMode){
		this.IsMultipleMode=IsMultipleMode;
		if(IsMultipleMode){
			sessions=new ConcurrentHashMap<Integer,IoSession>();
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
						Address address=new Address((packet.getPacket())
								.getSubByteArray(0, 1));//Address.INDEX_BEGIN, Address.INDEX_END
						address.decode("","");
						int msa=address.getMsa();
						IoSession sessionLocal=sessions.get(msa);
						if(sessionLocal!=null && 
								System.currentTimeMillis()-(long)sessionLocal.getAttribute(FepConstants.LAST_READ_TIME)<FepConstants.TIME_OUT){
							try{
								sessionLocal.write(packet);
							}catch(Exception e){
								logger.error(String.format("msa %s is offline,packet %s will discard",
										String.valueOf(msa),packet));
								sessions.remove(msa);
								sessionLocal.close(true);
							}
						}else{
							logger.info(String.format("msa %s is offline,packet %s will discard",
									String.valueOf(msa),packet));
							sessions.remove(msa);
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
	
	public void putSession(int msaid,IoSession session){
		if(IsMultipleMode){
			sessions.put(msaid, session);
		}else{
			this.session=session;
		}
	}
	public void removeSession(Integer msaid){
		if(IsMultipleMode){
			sessions.remove(msaid);
		}else{
			this.session=null;
		}
	}
	public List<Integer> getSessionKeyList(){
		if(IsMultipleMode){
			return Lists.newArrayList(sessions.keySet());
		}else{
			return Lists.newArrayList((int)session.getId());
		}
	}
	public IoSession getSession(Integer msaid){
		if(IsMultipleMode){
			return sessions.get(msaid);
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
	
	public boolean isAlive(){
		return !ThreadPool.isTerminated();
	}
	
}

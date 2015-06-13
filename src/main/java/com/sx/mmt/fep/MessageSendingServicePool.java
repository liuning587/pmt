package com.sx.mmt.fep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.FepConstants;
import com.sx.mmt.internal.util.SimpleBytes;

public class MessageSendingServicePool {
//	private Map<Integer,MessageSendingService> servicePool;
//	private Map<String,Context> addressToServiceMapping;
//	private ExecutorService moniterThread;
//	private Moniter moniter;
//	private ExecutorService moniterGuardThread1;
//	private ExecutorService moniterGuardThread2;
//	private ExecutorService moniterGuardThread3;
//	private MoniterGuard MG1;
//	private MoniterGuard MG2;
//	private MoniterGuard MG3;
//	private static Logger logger = LoggerFactory.getLogger(MessageSendingServicePool.class);
//	
//	public void Start(int poolSize){
//		addressToServiceMapping=new ConcurrentHashMap<String, Context>(); 
//		servicePool=new ConcurrentHashMap<Integer, MessageSendingService>();
//		for(int i=0;i<poolSize;i++){
//			servicePool.put(i, new MessageSendingService(true));
//			servicePool.get(i).startService();
//		}
//		moniterThread=Executors.newSingleThreadExecutor();
//		moniter=new Moniter();
//		moniterThread.execute(moniter);
//		iniMoniterGuard();
//	}
//	
//	private void iniMoniterGuard(){
//		MG1=new MoniterGuard();
//		MG2=new MoniterGuard();
//		MG3=new MoniterGuard();
//		moniterGuardThread1=Executors.newSingleThreadExecutor();
//		moniterGuardThread2=Executors.newSingleThreadExecutor();
//		moniterGuardThread3=Executors.newSingleThreadExecutor();
//		moniterGuardThread1.execute(MG1);
//		moniterGuardThread2.execute(MG2);
//		moniterGuardThread3.execute(MG3);
//	}
//	public void putSession(String name,IoSession session){
//		int id=findMinLoadService();
//		Context c=new Context(id,0);
//		addressToServiceMapping.put(name, c);
//		servicePool.get(id).putSession(name, session);
//	}
//	public IoSession getSession(String name){
//		Context id=addressToServiceMapping.get(name);
//		if(id!=null){
//			MessageSendingService service=servicePool.get(id.getServiceId());
//			if(service!=null){
//				return service.getSession(name);
//			}
//		}
//		return null;
//	}
//	
//	public void removeSession(String name){
//		Context id=addressToServiceMapping.get(name);
//		if(id!=null){
//			MessageSendingService service=servicePool.get(id.getServiceId());
//			if(service!=null){
//				service.removeSession(name);
//			}
//		}
//		addressToServiceMapping.remove(name);
//	}
//	
//	public List<String> getSessionKeyList(){
//		List<String> list=new ArrayList<String>();
//		if(servicePool==null){
//			return list;
//		}
//		for(MessageSendingService s:servicePool.values()){
//			list.addAll(s.getSessionKeyList());
//		}
//		return list;
//	}
//	public MessageSendingService getService(String name){
//		Context id=addressToServiceMapping.get(name);
//		if(id!=null){
//			return servicePool.get(id.getServiceId());
//		}
//		return null;
//	}
//	
//	public void dispose(){
//		MG1.stopMoniterGuard();
//		MG2.stopMoniterGuard();
//		MG3.stopMoniterGuard();
//		moniter.stopMoniter();
//		for(MessageSendingService s:servicePool.values()){
//			s.dispose();
//		}		
//	}
//	private int findMinLoadService(){
//		
//		//将采用基因遗传算法，快速找到局部最优解
//		int sessionInService=1000;
//		int minId=0;
//		for(Integer Int:servicePool.keySet()){
//			int sessionSize=servicePool.get(Int).getCurrentSessionSize();
//			if(sessionSize<sessionInService){
//				minId=Int;
//				sessionInService=sessionSize;
//			}
//		}
//		return minId;
//	}
//	
//	public void addPacket(String name,SimpleBytes packet){
//		Context context=addressToServiceMapping.get(name);
//		if(context!=null){
//			MessageSendingService sendService=servicePool.get(context.getServiceId());
//			if(sendService!=null){
//				if(System.currentTimeMillis()-context.getLastPacketSendTime()<FepConstants.PACKET_SEND_INTERVAL){
//					sendService.getEncodedDataSendingQueue()
//						.addPacket(packet,context.getLastPacketSendTime()+FepConstants.PACKET_SEND_INTERVAL);
//					context.setLastPacketSendTime(context.getLastPacketSendTime()+FepConstants.PACKET_SEND_INTERVAL);
//				}else{
//					sendService.getEncodedDataSendingQueue().addPacket(packet);
//					context.setLastPacketSendTime(System.currentTimeMillis());
//				}
//			}else{
//				logger.info(String.format("terminal %s is offline. packet %s will discard", name,packet));
//			}
//		}
//	}
//	
//	private class Context{
//		int serviceId;
//		long lastPacketSendTime;
//		public Context(int serivceId,long lastPacketSendTime){
//			this.serviceId=serivceId;
//			this.lastPacketSendTime=lastPacketSendTime;
//		}
//		public int getServiceId() {
//			return serviceId;
//		}
//		public long getLastPacketSendTime() {
//			return lastPacketSendTime;
//		}
//		public void setLastPacketSendTime(long lastPacketSendTime) {
//			this.lastPacketSendTime = lastPacketSendTime;
//		}
//		@Override
//		public int hashCode() {
//			return serviceId;
//		}
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			Context other = (Context) obj;
//			if (serviceId != other.serviceId)
//				return false;
//			return true;
//		}
//	}
//	
//	public synchronized void recoverMG1(){
//		moniterGuardThread1=Executors.newSingleThreadExecutor();
//		moniterGuardThread1.execute(MG1);
//	}
//	
//	public synchronized void recoverMG2(){
//		moniterGuardThread1=Executors.newSingleThreadExecutor();
//		moniterGuardThread1.execute(MG2);
//	}
//	
//	public synchronized void recoverMG3(){
//		moniterGuardThread1=Executors.newSingleThreadExecutor();
//		moniterGuardThread1.execute(MG3);
//	}
//	
//	public synchronized void recoverMoniter(){
//		moniterThread=Executors.newSingleThreadExecutor();
//		moniterThread.execute(moniter);
//	}
//	
//	private class MoniterGuard implements Runnable{
//		private volatile boolean stopRequested;
//		private Thread runThread;
//		@Override
//		public void run() {
//			runThread = Thread.currentThread();
//			stopRequested = false;
//			while(!stopRequested){
//				if(moniterGuardThread1.isTerminated()){
//					recoverMG1();
//				}
//				if(moniterGuardThread2.isTerminated()){
//					recoverMG2();
//				}
//				if(moniterGuardThread3.isTerminated()){
//					recoverMG3();
//				}
//				if(moniterThread.isTerminated()){
//					recoverMoniter();
//				}
//				
//			}
//			try{
//				Thread.sleep(120000+(int)(Math.random()*10000));
//			}catch(InterruptedException e){
//				Thread.currentThread().interrupt();
//			}
//			
//		}
//		
//		public void stopMoniterGuard(){
//			stopRequested=true;
//	        if ( runThread != null ) {
//	            runThread.interrupt();
//	        }
//		}
//	}
//	
//	private class Moniter implements Runnable{
//		private volatile boolean stopRequested;
//		private Thread runThread;
//		@Override
//		public void run() {
//			runThread = Thread.currentThread();
//			stopRequested = false;
//			while(!stopRequested){
//				for(int i=0;i<servicePool.size();i++){
//					if(servicePool.get(i).isAlive()){
//						continue;
//					}else{
//						servicePool.remove(i);
//						servicePool.put(i, new MessageSendingService(true));
//						servicePool.get(i).startService();
//						Collection<Context> contexts=addressToServiceMapping.values();
//						Context con=new Context(i,0);
//						while(contexts.contains(con)){
//							contexts.remove(con);
//						}
//					}
//				}
//				try{
//					Thread.sleep(600000);
//				}catch(InterruptedException e){
//					Thread.currentThread().interrupt();
//				}
//			}			
//		}
//		
//		public void stopMoniter(){
//			stopRequested=true;
//	        if ( runThread != null ) {
//	            runThread.interrupt();
//	        }
//		}
//	}
	
}



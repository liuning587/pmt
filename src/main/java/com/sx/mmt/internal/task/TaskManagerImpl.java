package com.sx.mmt.internal.task;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.mina.util.ConcurrentHashSet;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectXmlResolver;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.Tail;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.task.command.CommandState;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.swingUI.ActionNowDisplay;


@Component("taskManager")
public final class TaskManagerImpl implements TaskManager{
	private static Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);
	private TaskExecuter taskExecuter;
//	private TaskUpdater taskUpdater;
	private Object taskExecuterLoker=new Object();
//	private Object taskUpdaterLoker=new Object();
	private Map<String,String> onlineList=new ConcurrentHashMap<String, String>();
	private Set<String> onlineEvent=new ConcurrentHashSet<String>();
	private LinkedBlockingQueue<EncodedPacket> receivePacketqueue=
			new LinkedBlockingQueue<EncodedPacket>();
	@Autowired
	private ConnectXmlResolver ConnectXmlResolver;
	
	private UncaughtExceptionHandler handler=new myUncaughtExceptionHandler();
	@Autowired
	private volatile TaskImplDao taskImplDao;
	@Autowired
	private EncodedDataSendingDelayQueue encodedDataSendingDelayQueue;
	@Autowired
	private GBProtocolBreakerPool pbPool;
	@Autowired
	private TaskXmlResolver taskXmlResolver;
	
	private int maxRetry;
	
	private Map<String,TaskImpl> taskcache;
	public void setTaskImplDao(TaskImplDao taskImplDao) {
		this.taskImplDao = taskImplDao;
	}
	public void setPbPool(GBProtocolBreakerPool pbPool) {
		this.pbPool = pbPool;
	}
	public void setTaskXmlResolver(TaskXmlResolver taskXmlResolver) {
		this.taskXmlResolver = taskXmlResolver;
	}
	

	public void setEncodedDataSendingDelayQueue(
			EncodedDataSendingDelayQueue encodedDataSendingDelayQueue) {
		this.encodedDataSendingDelayQueue = encodedDataSendingDelayQueue;
	}
	public void receiveCqPacket(Document doc){
		Element rootEl=doc.getRootElement();
		String terminalid=rootEl.getChildText(OtherConstants.terminalId);
		String replyContent=rootEl.getChildText(OtherConstants.replyContent);
		String frameContent=rootEl.getChildText(OtherConstants.frameContent);
		String exceptionContent=rootEl.getChildText(OtherConstants.exceptionContent);
		if(frameContent==null){
			DecodedPacket dp=new DecodedPacket();
			if(replyContent!=null){
				dp.put(OtherConstants.replyContent, replyContent);
			}
			if(exceptionContent!=null){
				dp.put(OtherConstants.exceptionContent,exceptionContent);
			}
			TaskImpl task=taskcache.get(terminalid);
			task.messageComeHandle(dp);
		}else{
			if(checkFrameContent(frameContent)){
				SimpleBytes packet=new SimpleBytes(frameContent,16,true);
				EncodedPacket message=new EncodedPacket();
				message.setTaskId(terminalid);
				message.setPacket(packet);
				receivePacket(message);
			}
		}
	}
	
	public void receiveSdPacket(Document doc){
		Element rootEl=doc.getRootElement();
		String terminalid=rootEl.getChildText(OtherConstants.rtuAddress);
		String frameContent=rootEl.getChildText(OtherConstants.frameContent).replace(" ", "");
		if(frameContent!=null && checkFrameContent(frameContent)){
			SimpleBytes packet=new SimpleBytes(frameContent,16,true);
			EncodedPacket message=new EncodedPacket();
			message.setTaskId(terminalid);
			message.setPacket(packet);
			receivePacket(message);
		}
	}
	
	private boolean checkFrameContent(String msg){
		String message=msg.replace(" ", "");
		boolean result=true;
		int size=message.length();
		if(size<12 || size%2!=0){
			result=false;
		}else{
			if(message.substring(0,2).equals("68") && message.substring(10,12).equals("68")){
				Head head=new Head(new SimpleBytes(message.substring(0, 12),16,true));
				head.decode("","");
				if(head.check()){
					
					int length=head.getTotalDataLength()
					+Head.SEGMENT_LENGTH+Tail.SEGMENT_LENGTH;
					if((size/2)!=length){
						result=false;
					}
				}
			}
		}
		if(!message.substring(size-2,size).equals("16")){
			
			result=false;
		}
		return result;
	}
	
	public void receivePacket(EncodedPacket encodedPacket){
		receivePacketqueue.offer(encodedPacket);
	}
	
	/**
	 * 程序开启将所有任务置为停止
	 */
	@Override
	public void iniTaskStatus(){
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				taskImplDao.changeAllTaskStatus(
						new String[]{ViewConstants.RUNNING,ViewConstants.WAITING},ViewConstants.STOPED, "000");
				ActionNowDisplay.updateTable();
			}
		});

		taskcache=new ConcurrentHashMap<String, TaskImpl>();
		maxRetry=PropertiesUtil.parseToInt(ConfigConstants.MaxRetryCount);
	}
	
	private class myUncaughtExceptionHandler implements UncaughtExceptionHandler{
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error(t.getName()+"线程失效");
			logger.error(ErrorTool.getErrorInfoFromException(e));
			if(t.getName().equals("taskExecuterThread")){
				Thread taskExecuterThread=new Thread(taskExecuter);
				taskExecuterThread.setUncaughtExceptionHandler(handler);
				taskExecuterThread.setName("taskExecuterThread");
				taskExecuterThread.setDaemon(true);
				taskExecuterThread.start();
			}
//			if(t.getName().equals("taskUpdaterThread")){
//				Thread taskUpdaterThread=new Thread(taskUpdater);
//				taskUpdaterThread.setUncaughtExceptionHandler(handler);
//				taskUpdaterThread.setName("taskUpdaterThread");
//				taskUpdaterThread.start();
//			}
			logger.error(t.getName()+"线程恢复");
		}
		
	}
	
	@Override
	public void startTaskManager(){
		taskExecuter=new TaskExecuter();
		Thread taskExecuterThread=new Thread(taskExecuter);
		taskExecuterThread.setUncaughtExceptionHandler(handler);
		taskExecuterThread.setName("taskExecuterThread");
		taskExecuterThread.setDaemon(true);
		taskExecuterThread.start();
		
//		taskUpdater=new TaskUpdater();
//		Thread taskUpdaterThread=new Thread(taskUpdater);
//		taskUpdaterThread.setUncaughtExceptionHandler(handler);
//		taskUpdaterThread.setName("taskUpdaterThread");
//		taskUpdaterThread.start();
	}
	@Override
	public void stopTaskManager(){
		taskExecuter.stopRobot();
//		taskUpdater.stopRobot();
	}
	
	/**
	 * 执行任务
	 * @author peter
	 *
	 */
	private class TaskExecuter implements Runnable{
		private volatile boolean stopRequested;
		private Thread runThread;
		private List<TaskImpl> finishedTask;
		private List<TaskImpl> badTask;
		@Override
		public void run() {
			runThread = Thread.currentThread();
			stopRequested = false;
			finishedTask=new Vector<TaskImpl>();
			badTask=new Vector<TaskImpl>();
			synchronized (taskExecuterLoker) {
				while(!stopRequested){
					//任务为空，sleep
					if(taskcache.size()==0){
						try {
							taskExecuterLoker.wait(5000);
						} catch (InterruptedException e) {
						}
					}
					Long time=System.currentTimeMillis();
					//首先执行收到回复的报文
					EncodedPacket message=receivePacketqueue.poll();
					while(message!=null){
						TaskImpl messageReceivedtask=taskcache.get(message.getTaskId());
						if(messageReceivedtask!=null){
							DataPacketParser dataPacketParser=pbPool.getDataPacketParser();
							DecodedPacket dp=null;
							TaskConfig tc=taskXmlResolver.getTasks().get(messageReceivedtask.getTaskName());
							try {
								dp=dataPacketParser.parse(message.getPacket(), tc.getProtocolArea());
								pbPool.returnObject(dataPacketParser);
							} catch (Exception e) {
								logger.error("parser packet error:"+message.getPacket().toString());
							}
							if(dp!=null){
								messageReceivedtask.messageComeHandle(dp);
							}
						}
						message=receivePacketqueue.poll();
					}
					
					finishedTask.clear();
					badTask.clear();
					for(TaskImpl task:taskcache.values()){
						if(onlineEvent.contains(task.getTerminalAddress())){
							task.TerminalOnHandle();
							onlineEvent.remove(task.getTerminalAddress());
						}
						if(task.getNextActionTime()>time) continue;
						if(task.isStatusTransitionLoker()) continue;
						if(task.getStepStatus().equals(CommandState.Start)){
							task.startHandle();
						}else if(task.getStepStatus().equals(CommandState.Finish)){
							task.finishHandle();
						}else if(task.getStepStatus().equals(CommandState.Failed)){
							task.setTaskStatus(ViewConstants.FAILED);
						}else{
							if(maxRetry>0 && task.getRetryCount()>maxRetry){
								badTask.add(task);
							}else{
								task.timeUpHandle();
							}
						}
						if(task.getTaskStatus().equals(ViewConstants.FINISHED) ||
								task.getTaskStatus().equals(ViewConstants.FAILED)){
							finishedTask.add(task);
							task.setFinishTime(new Date());
						}

					}
					
					for(TaskImpl t:finishedTask){
						taskcache.remove(t.getId());
						ActionNowDisplay.update(t);
					}
					for(TaskImpl t:badTask){
						taskcache.remove(t.getId());
						t.setPriority(t.getPriority()+1);
						t.setTaskStatus(ViewConstants.WAITING);
						t.setRetryCount(0);
						ActionNowDisplay.update(t);
					}
					
					if(finishedTask.size()>0 || badTask.size()>0){
						if(finishedTask.size()>0){
							taskImplDao.update(finishedTask);
						}
						if(badTask.size()>0){
							taskImplDao.update(badTask);
						}
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
						}
						updateCacheNotice();
					}
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
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
	
//	/**
//	 * 后台保存任务
//	 * @author peter
//	 *
//	 */
//	private class TaskUpdater implements Runnable{
//		private volatile boolean stopRequested;
//		private Thread runThread;
//		private List<TaskImpl> tasks;
//		@Override
//		public void run() {
//			runThread = Thread.currentThread();
//			stopRequested = false;
//			synchronized (taskUpdaterLoker) {
//				while(!stopRequested){		
//					tasks=Lists.newArrayList(taskcache.values());
//					taskImplDao.execute(new Runnable() {
//						@Override
//						public void run() {
//							taskImplDao.update(tasks);
//						}
//					});
//					
//					try {
//						taskUpdaterLoker.wait(10000);
//					} catch (InterruptedException e) {
//						
//					}
//					
//				}
//			}
//		}
//		public void stopRobot(){
//			stopRequested=true;
//	        if ( runThread != null ) {
//	            runThread.interrupt();
//	        }
//		}
//	}
	

	@Override
	public void removeAllFromCache(String groupTag){
//		Stream<TaskImpl> s=taskcache.values().stream();
		List<TaskImpl> runningTasks=new ArrayList<TaskImpl>();
//				s.filter(t->t.getTaskGroupTag().equals(groupTag)).collect(Collectors.toList());
		for(TaskImpl ts:taskcache.values()){
			if(ts.getTaskGroupTag().equals(groupTag)){
				runningTasks.add(ts);
			}
		}
		for(TaskImpl ss:runningTasks){
			taskcache.remove(ss.getId());
			encodedDataSendingDelayQueue.removePacket(ss.getId());
		}
		taskImplDao.update(runningTasks);
	}
	@Override
	public TaskImpl removeFromCache(String id){
		if(taskcache.containsKey(id)){
			TaskImpl task=taskcache.get(id);
			taskcache.remove(id);
			return task;
		}
		return null;
	}
	@Override
	public synchronized void updateCacheNotice(){
		int maxTask=PropertiesUtil.parseToInt(ConfigConstants.TaskMaxNumber);
		List<TaskImpl> runningTasks=Lists.newArrayList(taskcache.values());
		taskImplDao.update(runningTasks);
		List<TaskImpl> tasks=
				taskImplDao.getTaskList("000", 1, maxTask, "priority", "asc", ViewConstants.WAITING);
		for(TaskImpl t:tasks){
			if(taskcache.size()>=maxTask){
				break;
			}
			if(!taskcache.containsKey(t.getId())){
				taskcache.put(t.getId(), t);
				t.setTaskStatus(ViewConstants.RUNNING);
				t.setCreateTime(new Date());
				taskImplDao.update(Lists.newArrayList(t));
			}
		}
	}
	@Override
	public void clearCache(){
		List<TaskImpl> runningTasks=Lists.newArrayList(taskcache.values());
		taskImplDao.update(runningTasks);
		taskcache.clear();
		encodedDataSendingDelayQueue.clearQueue();
	}

	public Map<String, String> getOnlineList() {
		return onlineList;
	}
	public void setOnlineList(Map<String, String> onlineList) {
		this.onlineList = onlineList;
	}
	@Override
	public Map<String, TaskImpl> getTaskcache() {
		return taskcache;
	}
	@Override
	public Set<String> getOnlineEvent() {
		return onlineEvent;
	}
	
	
	
}

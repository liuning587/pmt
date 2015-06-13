package com.sx.mmt.swingUI;



import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Component;

import com.sx.mmt.internal.task.TaskImpl;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.TaskManagerBodyView.MyDefaultTableModel;


@Component
public class ActionNowDisplay {

	private static MyDefaultTableModel tasklist;
	private static JTable taskListTable;
	private static TaskManagerController taskManagerController;
	private static ShowTerminalController showTerminalController;
	private ExecutorService executorPool=Executors.newSingleThreadExecutor();
	private static UpdateTableManager updateTableManager;
	private static BlockingQueue<Runnable> tableWorkQueue=new ArrayBlockingQueue<Runnable>(5);
	private static BlockingQueue<Runnable> treeWorkQueue=new ArrayBlockingQueue<Runnable>(5);
	public static void ini(){
		taskManagerController=
				(TaskManagerController) SpringBeanUtil.getBean("taskManagerController");
		tasklist=((MyDefaultTableModel)taskManagerController
				.getTaskManagerBodyView().getTableModel());
		taskListTable=taskManagerController.getTaskManagerBodyView().getTaskListTable();
		showTerminalController=
				(ShowTerminalController)SpringBeanUtil.getBean("showTerminalController");
		updateTableManager=new ActionNowDisplay().new UpdateTableManager();
		UncaughtExceptionHandler handler=new UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Thread tableUpdate=new Thread(updateTableManager);
				tableUpdate.setName("tableUpdate");
				tableUpdate.setUncaughtExceptionHandler(this);
				tableUpdate.setDaemon(true);
				tableUpdate.start();
			}
		};
		Thread tableUpdate=new Thread(updateTableManager);
		tableUpdate.setName("tableUpdate");
		tableUpdate.setUncaughtExceptionHandler(handler);
		tableUpdate.setDaemon(true);
		tableUpdate.start();
	}
	
	public static void refreshTable(){
		tableWorkQueue.clear();
		tableWorkQueue.add(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {	
					@Override
					public void run() {
						taskListTable.validate();
						taskListTable.updateUI();
					}
				});
			}
		});
	}
	
	public static void update(TaskImpl task){
		tasklist.updateValue(task);
		
	}
	
	public static void stop(){
		if(updateTableManager!=null){
			updateTableManager.stopRobot();
		}
	}
	
	public static void updateTable(){
		taskManagerController.loadTaskTable();
		
	}
	
	public static void updateTree(){
		treeWorkQueue.clear();
		treeWorkQueue.add(new Runnable() {
			
			@Override
			public void run() {
				taskManagerController.loadTree();
				
			}
		});
		
	}
	
	public static void appendTerminalShow(String message){
		showTerminalController.appendMessage(message);
		
	}
	
	public class UpdateTableManager implements Runnable{
		private volatile boolean stopRequested;
		private Thread runThread;
		private long lastTableUpdateTime=0L;
		private long lastTreeUpdateTime=0L;
		private final long minUpdateInterval=1000;
		private final long maxUpdateInterval=20000;
		@Override
		public void run() {
			stopRequested = false;
			runThread = Thread.currentThread();
			while(!stopRequested){
				try {
					Thread.sleep(minUpdateInterval);
				} catch (InterruptedException e) {
				}
				long currTime=System.currentTimeMillis();
				Runnable tableWork=tableWorkQueue.poll();
				if(tableWork!=null){
					lastTableUpdateTime=currTime;
					executorPool.execute(tableWork);
					taskManagerController.refreshFootLabel();
				}
				Runnable treeWork=treeWorkQueue.poll();
				if(tableWork!=null){
					lastTreeUpdateTime=currTime;
					executorPool.execute(treeWork);
				}
				
				if(currTime>lastTableUpdateTime+maxUpdateInterval && 
						currTime>lastTreeUpdateTime+maxUpdateInterval){
					executorPool.execute(new Runnable() {
						
						@Override
						public void run() {
							taskManagerController.loadTaskTable();
							taskManagerController.loadTree();
							
						}
					});
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
	
}

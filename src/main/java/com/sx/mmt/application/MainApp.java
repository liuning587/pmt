package com.sx.mmt.application;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectionManager;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.JDBCHelp;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.ActionNowDisplay;
import com.sx.mmt.swingUI.ApplicationStartMask;
import com.sx.mmt.swingUI.TaskManagerView;


public class MainApp {
	private static Logger logger = LoggerFactory.getLogger(MainApp.class); 
	private static ApplicationContext context=null;
	public static void main(String[] args) {
		ApplicationStartMask mask=new ApplicationStartMask("程序启动中,请稍等...");
		mask.setVisible(true);
		TaskManagerView tmv=null;
		try{
			//初始化数据库
			JDBCHelp.createConnection();
			JDBCHelp.checkTable();
		}catch(Exception e){
			if(e instanceof SQLException){
				mask.setLabel("<html>数据初始化失败<br/>请检查是否开启多个程序</html>");
				logger.error(ErrorTool.getErrorInfoFromException(e));
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
				mask.dispose();
			}
			return;
		}
		
		//注册spring的bean
		context=new ClassPathXmlApplicationContext("classpath:appcontext.xml");
		
		try{
			//加载连接方式
			ConnectionManager connectionManager=
					(ConnectionManager)SpringBeanUtil.getBean("connectionManager");
			connectionManager.initConnect();
		}catch(Exception e){
			mask.setLabel("<html>连接方式初始化失败<br/>请检查配置或机器情况</html>");
			logger.error(ErrorTool.getErrorInfoFromException(e));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
			}
		}
		
		try{
			TaskManager taskManager=(TaskManager)context.getBean("taskManager");
			//初始化主窗口
			tmv=new TaskManagerView();
			//初始化状态显示接口
			ActionNowDisplay.ini();
			//初始化所有任务状态
			taskManager.iniTaskStatus();
			//开启任务调度线程
			taskManager.startTaskManager();
			mask.dispose();
			tmv.setVisible(true);
		}catch(Exception e){
			logger.error(ErrorTool.getErrorInfoFromException(e));
			if(tmv!=null){
				tmv.dispose();
			}
			mask.dispose();
		}
	}
	public static ApplicationContext getContext() {
		return context;
	}
	
	public static void dispose(){
		if(context!=null){
			((AbstractApplicationContext)context).close();
		}
		JDBCHelp.dispose();
	}
	
}

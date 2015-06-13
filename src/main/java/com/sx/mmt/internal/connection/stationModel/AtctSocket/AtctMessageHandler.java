package com.sx.mmt.internal.connection.stationModel.AtctSocket;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sx.mmt.constants.ConnectionConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.task.TaskImpl;
import com.sx.mmt.internal.task.TaskImplDao;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.swingUI.ActionNowDisplay;

public class AtctMessageHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(AtctMessageHandler.class);
	private AtctConnectionManager atctConnectionManager;
	private TaskManager taskManager;
	private TaskImplDao taskImplDao;
	private EncodedDataSendingDelayQueue encodedDataSendingDelayQueue;
	

	public void setTaskImplDao(TaskImplDao taskImplDao) {
		this.taskImplDao = taskImplDao;
	}

	public void setEncodedDataSendingDelayQueue(
			EncodedDataSendingDelayQueue encodedDataSendingDelayQueue) {
		this.encodedDataSendingDelayQueue = encodedDataSendingDelayQueue;
	}

	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public void setAtctConnectionManager(AtctConnectionManager atctConnectionManager) {
		this.atctConnectionManager = atctConnectionManager;
	}

	public void sessionCreated(IoSession session) throws Exception {
    }

    public void sessionClosed(IoSession session) throws Exception {
    	String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
    	ActionNowDisplay.appendTerminalShow(terminalAddress+" Connection with fep lost");
    	Object isNeedReconnect=session.getAttribute(ConnectionConstants.IsNeedReconnect);
    	if(isNeedReconnect!=null && !(boolean)isNeedReconnect){
    		return;
    	}
    	atctConnectionManager.reconnect(terminalAddress);
    	ActionNowDisplay.appendTerminalShow(terminalAddress+" reconnect");
    	taskManager.getOnlineList().put(terminalAddress, ViewConstants.Offline);
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    	session.close(false);
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	logger.error(ErrorTool.getErrorInfoFromException(cause));
    	ActionNowDisplay.appendTerminalShow("Connection with fep lost with exception");
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
    	String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
    	if(message instanceof String){
    		logger.info((String)message);
    		ActionNowDisplay.appendTerminalShow((String)message);
    		if("ATCT0001".equals(((String) message))){
    			taskManager.getOnlineList().put(terminalAddress, ViewConstants.Online);
    		}else
			{
    			TaskImpl task=taskManager.removeFromCache(terminalAddress);
    			encodedDataSendingDelayQueue.removePacket(terminalAddress);
    			atctConnectionManager.getSessions().remove(terminalAddress);
    			if(task!=null){
    				if("0002".equals(((String) message).substring(4))){
    					task.setActionNow("终端不在线");
    					taskManager.getOnlineList().put(terminalAddress, ViewConstants.Offline);
    					task.setPriority(task.getPriority()+1);
    					task.setTaskStatus(ViewConstants.WAITING);
    				}else if("0003".equals(((String) message).substring(4))){
    					task.setActionNow("通道繁忙，稍候再试");
    					task.setTaskStatus(ViewConstants.WAITING);
    					task.setPriority(task.getPriority()+1);
    	    		}else if("0004".equals(((String) message).substring(4))){
    	    			task.setActionNow("升级未被许可");
    	    			task.setTaskStatus(ViewConstants.STOPED);
    	    		}else if("EROR".equals(((String) message).substring(0, 4))){
    	    			task.setActionNow("通道申请错误");
    	    			task.setTaskStatus(ViewConstants.STOPED);
    	    		}else{
    	    			task.setTaskStatus(ViewConstants.WAITING);
    	    		}
    				
    				taskImplDao.update(Lists.newArrayList(task));
    				ActionNowDisplay.update(task);
    			}
				
				taskManager.updateCacheNotice();
				session.setAttribute(ConnectionConstants.IsNeedReconnect, false);
				atctConnectionManager.getSessions().remove(session);
				session.close(false);

			}
    		return;
    	}
    	if(message instanceof SimpleBytes){
    		atctConnectionManager.processReceivePacket((SimpleBytes) message, terminalAddress);
    	}	
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        // Empty handler
    }
}

package com.sx.mmt.internal.connection.fepModel;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ConnectionConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.DataPacketBuilder;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocol.afn.AFN00Hp0f3;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBDecodedPacketFactory;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.task.TaskImpl;
import com.sx.mmt.internal.task.TaskImplDao;
import com.sx.mmt.internal.task.command.CommandState;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.swingUI.ActionNowDisplay;


public class TerminalMessageHandler extends IoHandlerAdapter{
	private static Logger logger = LoggerFactory.getLogger(TerminalMessageHandler.class);
	
	
	private TerminalConnectionManager terminalConnection;
	private TaskImplDao taskImplDao;
	private GBProtocolBreakerPool pbPool;
	private TaskManager taskManager;
	private ConnectConfig config;
	
	
	public void setConfig(ConnectConfig config) {
		this.config = config;
	}
	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public void setTaskImplDao(TaskImplDao taskImplDao) {
		this.taskImplDao = taskImplDao;
	}

	public void setPbPool(GBProtocolBreakerPool pbPool) {
		this.pbPool = pbPool;
	}

	public void setTerminalConnection(TerminalConnectionManager terminalConnection) {
		this.terminalConnection = terminalConnection;
	}

	@Override   
	public void messageReceived(IoSession session, Object message)  throws Exception{	
		ProcessPacket(session,(SimpleBytes)message);
	}
	
	private void ProcessPacket(IoSession session,SimpleBytes packet){
		DataPacketParser dataPacketParser=pbPool.getDataPacketParser();
		String afn=dataPacketParser.getAfn(packet);
		int fn=dataPacketParser.getFn(packet);
		if(session.getAttribute(ConnectionConstants.TerminalAddress)==null){
			String terminalAddress=dataPacketParser.getTerminalAddress(packet);
			session.setAttribute(ConnectionConstants.TerminalAddress,terminalAddress);
		}
		pbPool.returnObject(dataPacketParser);
		if(afn.equals(Afn.AFN_REQUEST_LEVEL_THREE_DATA_EVENT)){
			return;
		}
		if(afn.equals(Afn.AFN_LINK_INTERFACE_CHECK)){
			if(fn==1){
				ProcessLogin(session,packet);
			}else if(fn==2){
				ProcessLogout(session,packet);
			}else if(fn==3){
				ProcessHeartBeat(session,packet);
			}else{
				logger.error(String.format("unsupport afn fn combination.afn=%s,fn=%s",
						afn,fn));
			}
		}else{
			terminalConnection.processReceivePacket(packet,
					(String) session.getAttribute(ConnectionConstants.TerminalAddress));
		}
		
	}
	
	private void ProcessHeartBeat(IoSession session,SimpleBytes packet){
		SimpleBytes heartBeatReply=getComfirmPacket(packet, 3);
		session.write(heartBeatReply);
		String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
		if(terminalAddress!=null){
			terminalConnection.getTerminalSessionMapping().put(terminalAddress, session.getId());
			taskManager.getOnlineList().put(terminalAddress, ViewConstants.Online);
			createNewTask(terminalAddress);
			logger.info(String.format("up %s heartBeat",terminalAddress));
		}	
	}
	
	private void ProcessLogin(IoSession session,SimpleBytes packet){
		SimpleBytes loginReply=getComfirmPacket(packet, 1);
//		session.getWriteRequestQueue().clear(session);
		session.write(loginReply);
		InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
	    String clientIp = remoteAddress.getAddress().getHostAddress()+":"+remoteAddress.getPort();
	    session.setAttribute(ConnectionConstants.TerminalIp, clientIp);
	    logger.info(String.format("%s receive terminal : %s connection. Session Id 为 %s", 
	    		   DateTool.getDateString(new Date()),clientIp,session.getId()));
		
		String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
		if(terminalAddress!=null){
			terminalConnection.getTerminalSessionMapping().put(terminalAddress, session.getId());
			taskManager.getOnlineList().put(terminalAddress, ViewConstants.Online);
			createNewTask(terminalAddress);
			logger.info(String.format("up %s login",terminalAddress));
		}
		
	}
	
	private void ProcessLogout(IoSession session,SimpleBytes packet){
		String terminalAddress=(String) 
				session.getAttribute(ConnectionConstants.TerminalAddress);
		logger.info(String.format("%s receive from terminal %s logout",
						DateTool.getDateString(new Date()),terminalAddress));
		SimpleBytes logoutReply=getComfirmPacket(packet, 2);
		session.write(logoutReply);
		terminalConnection.getTerminalSessionMapping().remove(terminalAddress);
		taskManager.getOnlineList().put(terminalAddress, ViewConstants.Offline);
		session.close(false);
	}
	
	private void createNewTask(final String terminalAddress){
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				if(taskImplDao.getTaskImpl(terminalAddress)==null){
					TaskImpl task=new TaskImpl();
					task.setId(terminalAddress);
					task.setTerminalAddress(terminalAddress);
					task.setTaskName(config.getAttr().get(ConfigConstants.CreateTaskType));
					task.setCurrentStepIndex(0);
					task.setNextActionTime(System.currentTimeMillis()+3000);
					task.setCounter(0);
					if(Boolean.parseBoolean(config.getAttr().get(ConfigConstants.AutoCreateTask))){
						task.setTaskStatus(ViewConstants.WAITING);
					}else{
						task.setTaskStatus(ViewConstants.NEW);
					}
					task.setStepStatus(CommandState.Start);
					task.setPfc(0);
					task.setTaskGroupTag("000000");
					task.setRetryCount(0);
					task.setPacketIndex(0);
					task.setPriority(10);
					task.setCreateTime(new Date());
					taskImplDao.add(Lists.newArrayList(task));
					if(Boolean.parseBoolean(config.getAttr().get(ConfigConstants.AutoCreateTask))){
						taskManager.updateCacheNotice();
					}
					ActionNowDisplay.updateTree();
					ActionNowDisplay.updateTable();
					
				}else{
					taskManager.getOnlineEvent().add(terminalAddress);
				}
			}
		});

	}
	
	@Override
    public void sessionCreated(IoSession session) {

    }
	
	@Override
	public void exceptionCaught(final IoSession session, final Throwable error) {
		String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
		String terminalIp=(String) session.getAttribute(ConnectionConstants.TerminalIp);
		if(terminalAddress!=null){
			terminalConnection.getTerminalSessionMapping().remove(terminalAddress);
			taskManager.getOnlineList().put(terminalAddress, ViewConstants.Offline);
		}
		logger.error(String.format("terminal Address %s ,id %s ,Ip %s session closed with exception! %s", 
				terminalAddress,
				session.getId(),
				terminalIp,
				ErrorTool.getErrorInfoFromException(error)));
	}
	
	@Override
	public void sessionClosed(final IoSession session) {
		String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
		String terminalIp=(String) session.getAttribute(ConnectionConstants.TerminalIp);
		if(terminalAddress!=null){
			terminalConnection.getTerminalSessionMapping().remove(terminalAddress);
			taskManager.getOnlineList().put(terminalAddress, ViewConstants.Offline);
		}
		if(terminalIp!=null){
			logger.info(String.format("terminal Address %s ,id %s ,Ip %s session connection with terminal closed",
					terminalAddress,
					session.getId(),
					terminalIp));
		}
		
	}
	
	@Override
	public void sessionIdle(final IoSession session, final IdleStatus status) {
		if(IdleStatus.BOTH_IDLE.equals(status)){
			session.close(false);
		}
		
	}
	
	private SimpleBytes getComfirmPacket(SimpleBytes packet,int fn){
		try{
			DataPacketParser dataPacketParser=pbPool.getDataPacketParser();
			DecodedPacket reveived=dataPacketParser.parse(packet, "");
			pbPool.returnObject(dataPacketParser);
			DecodedPacket response=GBDecodedPacketFactory.getResponseFrame(reveived);
			DataPacketBuilder dataPacketBuilder=pbPool.getDataPacketBuilder();
			Map<String,String> confirmDetail=new HashMap<String,String>();
			String pnfn=String.format("p0f%s", fn);
			confirmDetail.put(pnfn, AFN00Hp0f3.CONFIRM);
			response.put(ProtocolAttribute.AFN00H_CONFIRMDETAIL, confirmDetail);
			EncodedPacket encodedPacket=dataPacketBuilder.build(response, "");
			return encodedPacket.getPacket();
		}catch(Exception e){
			logger.error(ErrorTool.getErrorInfoFromException(e));
		}
		return null;
		
	}
}

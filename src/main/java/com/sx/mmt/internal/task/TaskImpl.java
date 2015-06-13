package com.sx.mmt.internal.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.StateMachine.TerminateEvent;
import org.squirrelframework.foundation.fsm.StateMachine.TerminateListener;
import org.squirrelframework.foundation.fsm.StateMachine.TransitionCompleteEvent;
import org.squirrelframework.foundation.fsm.StateMachine.TransitionCompleteListener;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.Command;
import com.sx.mmt.internal.api.Task;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandEvent;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandState;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.ActionNowDisplay;

@SuppressWarnings({"unchecked","rawtypes"})
public class TaskImpl implements Task{
	private static Logger logger = LoggerFactory.getLogger(TaskImpl.class);
	private String id;
	private String terminalAddress;
	private String taskName;
	private String taskStatus;
	private Date createTime;
	private Date finishTime;
	//当前操作提示
	private String actionNow;
	//电表返回值
	private String terminalReturn;
	//当前步骤，根据index推算得出
	private String currentStep;
	//分组标记
	private String taskGroupTag;
	//超时唤醒时间
	private long nextActionTime;
	private long counter=0;
	//等待回复的帧序号
	private int pfc;
	//发送帧计数
	private int packetIndex=0;
	//重试次数计数
	private int retryCount=0;
	//优先级
	private int priority=1;
	
	
	//备用字段1,2,3
	private String additionalParam1;
	private String additionalParam2;
	private String additionalParam3;
	private int currentStepIndex=0;
	private String stepStatus;
	private CommandConfig currentCommand;
	
	private boolean statusTransitionLoker=false;
	public TaskImpl(){}
	public TaskImpl(String terminalAddress){
		this.terminalAddress=terminalAddress;
	}
	
	public TaskImpl(TaskImpl task) {
		this.terminalAddress = task.getTerminalAddress();
		this.taskName = task.getTaskName();
		this.taskStatus = task.getTaskStatus();
		this.createTime = task.getCreateTime();
		this.actionNow = task.getActionNow();
		this.terminalReturn = task.getTerminalReturn();
		this.currentStep = task.getCurrentStep();
		this.taskGroupTag = task.getTaskGroupTag();
		this.nextActionTime = task.getNextActionTime();
		this.counter = task.getCounter();
		this.pfc = task.getPfc();
		this.currentStepIndex = task.getCurrentStepIndex();
		this.stepStatus = task.getStepStatus();
	}
	
	@Override
	public void messageComeHandle(DecodedPacket packet){
		setStatusTransitionLoker(true);
		if(packet.get(ProtocolAttribute.SEQ_SERIAL_NO)!=null){
			if(getSerialNoFromPfc()!=(Integer)packet.get(ProtocolAttribute.SEQ_SERIAL_NO)){
				updateActionNow("收到终端应答，但帧序号不一致"+"pfc="+getSerialNoFromPfc()+"seq="+packet.get(ProtocolAttribute.SEQ_SERIAL_NO));
				setStatusTransitionLoker(false);
				return;
			}
		}
//		if(packet.get(ProtocolAttribute.AUX_PFC)!=null){
//			if((byte)getPfc()!=(Integer)packet.get(ProtocolAttribute.AUX_PFC)){
//				updateActionNow("收到终端应答，但pfc不一致");
//				setStatusTransitionLoker(false);
//				return;
//			}
//		}
		if(taskStatus.equals(ViewConstants.RUNNING)){
			AbstractStateMachine command=getAbstractStateMachine();
			if(command!=null){
				command.fire(CommandEvent.ResponseCome,packet);
				retryCount=0;
			}
			
		}else{
			updateActionNow("收到终端报文，但任务已停止");
		}
		setStatusTransitionLoker(false);
	}
	@Override
	public void timeUpHandle(){
		setStatusTransitionLoker(true);
		if(taskStatus.equals(ViewConstants.RUNNING)){
			AbstractStateMachine command=getAbstractStateMachine();
			if(command!=null){
				command.fire(CommandEvent.TimeUp);
				retryCount++;
			}
		}
		setStatusTransitionLoker(false);
	}
	@Override
	public void TerminalOnHandle(){
		setStatusTransitionLoker(true);
		if(taskStatus.equals(ViewConstants.RUNNING)){
			AbstractStateMachine command=getAbstractStateMachine();
			if(command!=null){
				command.fire(CommandEvent.TerminalLogin);
				retryCount++;
			}
		}
		setStatusTransitionLoker(false);
	}
	@Override
	public void startHandle(){
		setStatusTransitionLoker(true);
		AbstractStateMachine command=getAbstractStateMachine();
		if(command!=null){
			command.fire(CommandEvent.Start);
		}
		
		setStatusTransitionLoker(false);
	}
	@Override
	public void finishHandle(){
		TaskXmlResolver taskXmlResolver=
				(TaskXmlResolver)SpringBeanUtil.getBean("taskXmlResolver");
		currentStepIndex=currentStepIndex+1;
		int totalstep=taskXmlResolver.getTasks().get(taskName).getCommands().size();
		if(currentStepIndex<totalstep){
			stepStatus=CommandState.Start;
		}else{
			currentStepIndex=totalstep-1;
			taskStatus=ViewConstants.FINISHED;
		}
	}
	@Override
	public TaskConfig getTaskConfig(){
		TaskXmlResolver taskXmlResolver=
				(TaskXmlResolver)SpringBeanUtil.getBean("taskXmlResolver");
		return taskXmlResolver.getTasks().get(taskName);
	}
	@Override
	public void setNewTask(String taskName){
		TaskXmlResolver taskXmlResolver=
				(TaskXmlResolver)SpringBeanUtil.getBean("taskXmlResolver");
		TaskConfig tc=taskXmlResolver.getTasks().get(taskName);
		if(tc==null){
			logger.error("设置新任务"+taskName+"时,该任务不存在");
		}else{
			this.taskName=taskName;
			this.actionNow="";
			this.currentStepIndex=0;
			this.nextActionTime=System.currentTimeMillis();
			this.counter=0;
			this.pfc=0;
			this.stepStatus=CommandState.Start;
			
		}
	}
	
	private AbstractStateMachine getAbstractStateMachine(){
		TaskXmlResolver taskXmlResolver=
				(TaskXmlResolver)SpringBeanUtil.getBean("taskXmlResolver");
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		List<String> commandList=taskXmlResolver.getTasks().get(taskName).getCommands();
		currentCommand=commandXmlResolver.get(commandList.get(currentStepIndex));
		CommandFactory commandFactory=(CommandFactory) 
				SpringBeanUtil.getBean("commandFactory");
		try {
			Method method = CommandFactory.class
					.getMethod("get"+currentCommand.getClazz(), String.class);
			AbstractStateMachine command=(AbstractStateMachine)
					method.invoke(commandFactory, stepStatus);
			command.addTransitionCompleteListener(new TransitionCompleteListener() {
				@Override
				public void transitionComplete(TransitionCompleteEvent event) {
					stepStatus=(String) event.getStateMachine().getCurrentState();
					
				}
			});
			((Command)command).setAttr(currentCommand.getAttr());
			((Command)command).setTask(this);
			return command;
		} catch (NoSuchMethodException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		} catch (SecurityException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return null;

	}

	private int getSerialNo(){
		counter++;
		SimpleBytes c=new SimpleBytes(counter);
		return c.getSubBitsValue(0, 4).toInt();
	}
	
	
	
	public int getSerialNoFromPfc(){
		SimpleBytes c=new SimpleBytes(pfc);
		return c.getSubBitsValue(0, 4).toInt();
	}
	
	@Override
	public void setTerminalAddressAndSeq(DecodedPacket decodedPacket){
		int seq=getSerialNo();
		setPfc(seq);
		decodedPacket.put(ProtocolAttribute.ADDRESS_DISTRICT, getTerminalAddress().substring(0, 4))
		.put(ProtocolAttribute.ADDRESS_TERMINAL_ADDRESS, getTerminalAddress().substring(4))
		.put(ProtocolAttribute.SEQ_SERIAL_NO, seq)
		.put(ProtocolAttribute.AUX_PFC, pfc)
		.put(ProtocolAttribute.HEAD_PROTOCOL_TYPE, getTaskConfig().getProtocolType());
		
	}
	@Override
	public String getTerminalAddress() {
		return terminalAddress;
	}

	public void setTerminalAddress(String terminalAddress) {
		this.terminalAddress = terminalAddress;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	@Override
	public String getTaskStatus() {
		return taskStatus;
	}
	@Override
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public String getActionNow() {
		return actionNow;
	}
	@Override
	public void setActionNow(String actionNow) {
		this.actionNow = actionNow;
	}
	@Override
	public void updateActionNow(String actionNow){
		this.actionNow = actionNow;
		ActionNowDisplay.update(this);
	}
	@Override
	public String getTerminalReturn() {
		return terminalReturn;
	}
	@Override
	public void setTerminalReturn(String terminalReturn) {
		this.terminalReturn = terminalReturn;
	}
	@Override
	public void appendTerminalReturn(String terminalReturn){
		if(StringUtils.isBlank(this.terminalReturn)){
			this.terminalReturn=terminalReturn;
		}else{
			this.terminalReturn=String.format("%s;%s", this.terminalReturn,terminalReturn);
		}
		
	}
	
	@Override
	public void setErrorCode(DecodedPacket context){
		if((int)context.get(ProtocolAttribute.DATAUNITIDENTIFY_FN)==3){
			StringBuilder sb=new StringBuilder();
			for(String s:((Map<String,String>)context.get(ProtocolAttribute.AFN00H_CONFIRMDETAIL)).values()){
				sb.append(s).append("#");
			}
			sb.deleteCharAt(sb.lastIndexOf("#"));
			appendTerminalReturn("Error:"+sb.toString());
		}
	}
	public String getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}
	@Override
	public long getNextActionTime() {
		return nextActionTime;
	}
	@Override
	public void setNextActionTime(long nextActionTime) {
		this.nextActionTime = nextActionTime;
	}
	@Override
	public void setDeadlineTime(){
		this.nextActionTime=System.currentTimeMillis()
				+PropertiesUtil.parseToInt(ConfigConstants.ResponseTimeout)*1000;
	}
	@Override
	public int getPacketIndex() {
		return packetIndex;
	}
	@Override
	public int getAndMovePacketIndex() {
		int i=packetIndex;
		packetIndex++;
		return i;
	}
	@Override
	public void setPacketIndex(int packetIndex) {
		this.packetIndex = packetIndex;
	}
	@Override
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public String getStepStatus() {
		return stepStatus;
	}
	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}
	public int getCurrentStepIndex() {
		return currentStepIndex;
	}

	public void setCurrentStepIndex(int currentStepIndex) {
		this.currentStepIndex = currentStepIndex;
	}
	@Override
	public int getPfc() {
		return pfc;
	}
	@Override
	public void setPfc(int pfc) {
		this.pfc = pfc;
	}
	@Override
	public String getTaskGroupTag() {
		return taskGroupTag;
	}
	@Override
	public void setTaskGroupTag(String taskGroupTag) {
		this.taskGroupTag = taskGroupTag;
	}
	public long getCounter() {
		return counter;
	}
	public void setCounter(long counter) {
		this.counter = counter;
	}
	
	
	public synchronized boolean isStatusTransitionLoker() {
		return statusTransitionLoker;
	}
	public synchronized void setStatusTransitionLoker(boolean statusTransitionLoker) {
		this.statusTransitionLoker = statusTransitionLoker;
	}
	@Override
	public String getAdditionalParam1() {
		return additionalParam1;
	}
	@Override
	public void setAdditionalParam1(String additionalParam1) {
		this.additionalParam1 = additionalParam1;
	}
	public String getAdditionalParam2() {
		return additionalParam2;
	}
	public void setAdditionalParam2(String additionalParam2) {
		this.additionalParam2 = additionalParam2;
	}
	public String getAdditionalParam3() {
		return additionalParam3;
	}
	public void setAdditionalParam3(String additionalParam3) {
		this.additionalParam3 = additionalParam3;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((terminalAddress == null) ? 0 : terminalAddress.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskImpl other = (TaskImpl) obj;
		if (terminalAddress == null) {
			if (other.terminalAddress != null)
				return false;
		} else if (!terminalAddress.equals(other.terminalAddress))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "TaskImpl [id=" + id + ", terminalAddress=" + terminalAddress
				+ ", taskName=" + taskName + ", taskStatus=" + taskStatus
				+ ", isOnline=" + ", createTime=" + createTime
				+ ", actionNow=" + actionNow + ", terminalReturn="
				+ terminalReturn + ", currentStep=" + currentStep
				+ ", taskGroupTag=" + taskGroupTag + ", nextActionTime="
				+ nextActionTime + ", counter=" + counter + ", pfc=" + pfc
				+ ", packetIndex=" + packetIndex + ", retryCount=" + retryCount
				+ ", additionalParam1=" + additionalParam1
				+ ", additionalParam2=" + additionalParam2
				+ ", additionalParam3=" + additionalParam3
				+ ", currentStepIndex=" + currentStepIndex + ", stepStatus="
				+ stepStatus + ", currentCommand=" + currentCommand
				+ ", statusTransitionLoker=" + statusTransitionLoker + "]";
	}
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
	

	
	
	
	
}
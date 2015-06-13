package com.sx.mmt.internal.task.command;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.squirrelframework.foundation.fsm.TransitionPriority;
import org.squirrelframework.foundation.fsm.TransitionType;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.Command;
import com.sx.mmt.internal.api.DataPacketBuilder;
import com.sx.mmt.internal.api.Task;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ControlField;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBDecodedPacketFactory;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.SpringBeanUtil;

@States({
    @State(name=CommandState.Start), 
    @State(name=CommandState.QueryAPN),
    @State(name=CommandState.SetPort),
    @State(name=CommandState.Finish),
    @State(name=CommandState.Failed),
})

@Transitions({
	//任务开始，开始发送APN查询命令
    @Transit(from=CommandState.Start, to=CommandState.QueryAPN, 
    		on=CommandEvent.Start, callMethod="queryAPN"),
    //收到APN查询回复，开始设置端口
    @Transit(from=CommandState.QueryAPN, to=CommandState.SetPort, 
			on=CommandEvent.ResponseCome, callMethod="setPort"),
	//查询APN超时，重发查询命令
    @Transit(from=CommandState.QueryAPN, to=CommandState.QueryAPN, 
    		on=CommandEvent.TimeUp, callMethod="queryAPN",
    		type=TransitionType.INTERNAL),
	//终端上线，重发查询命令
    @Transit(from=CommandState.QueryAPN, to=CommandState.QueryAPN, 
    		on=CommandEvent.TerminalLogin, callMethod="queryAPN",
    		type=TransitionType.INTERNAL),
    //设置端口超时，重发设置命令
    @Transit(from=CommandState.SetPort, to=CommandState.SetPort,
    		on=CommandEvent.TimeUp, callMethod="setPort",
    		type=TransitionType.INTERNAL),
    //终端上线，重发设置命令
    @Transit(from=CommandState.SetPort, to=CommandState.SetPort,
    		on=CommandEvent.TerminalLogin, callMethod="setPort",
    		type=TransitionType.INTERNAL),
    //发送命令收到确认应答，任务完成
    @Transit(from=CommandState.SetPort, to=CommandState.Finish, 
    		on=CommandEvent.ResponseCome, callMethod="finish",
    		priority=TransitionPriority.HIGH,
    		when=ComfirmCondition.class),
    //发送命令收到否认应答，任务失败
    @Transit(from=CommandState.SetPort, to=CommandState.Failed, 
    		on=CommandEvent.ResponseCome,callMethod="failed",
    		when=DenyCondition.class),
})

public class PortShiftCommand
			extends AbstractStateMachine<PortShiftCommand,String,String,DecodedPacket> 
			implements Command{
	
	private Task task;
	public void setTask(Task task) {
		this.task = task;
	}
	protected Map<String,String> attr;
	@Override
	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}
	
	public void queryAPN(String from, String to, String event, DecodedPacket context) throws Exception {
		
		if(Boolean.parseBoolean(attr.get(ConfigConstants.IsNotChangeAPN)) ||
				Boolean.parseBoolean(attr.get(ConfigConstants.IsSetMainBySub))){
			DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultGBSingleFrame();
			task.setTerminalAddressAndSeq(decodedPacket);
			
			decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_TWO_DATA);
			decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_QUERY_PARAMETER);

			decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 3);
			decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0);
			
			GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
					SpringBeanUtil.getBean("gBProtocolBreakerPool");
			DataPacketBuilder dataPacketBuilder=pbPool.getDataPacketBuilder();
			EncodedPacket encodedPacket= dataPacketBuilder
					.build(decodedPacket,task.getTaskConfig().getProtocolArea());
			pbPool.returnObject(dataPacketBuilder);
			encodedPacket.setTaskId(task.getId());
			EncodedDataSendingDelayQueue sendQueue=
					(EncodedDataSendingDelayQueue) SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
			sendQueue.addPacket(encodedPacket);
			task.setDeadlineTime();
			task.updateActionNow("发送查询终端IP和APN报文");
		}else{
			this.fire(CommandEvent.ResponseCome);
		}
	}
	
	public void setPort(String from, String to, String event, DecodedPacket context) throws Exception {
		if(Boolean.parseBoolean(attr.get(ConfigConstants.IsNotChangeAPN)) ||
				Boolean.parseBoolean(attr.get(ConfigConstants.IsSetMainBySub))){
			if(context!=null && !context.get(ProtocolAttribute.AFN_FUNCTION).equals(Afn.AFN_QUERY_PARAMETER)){
				return ;
			}else{
				if(context!=null && (Integer)context.get(ProtocolAttribute.DATAUNITIDENTIFY_FN)!=3){
					return;
				}
			}
		}
		String apn="";
		int[] mainIp=new int[4];
		String mainIpString=null;
		
		String subIpString=null;
		int[] subIp=new int[4];
		
		String mainPortString=null;
		int mainPort=0;
		
		String subPortString=null;
		int subPort=0;
		
		if(Boolean.parseBoolean(attr.get(ConfigConstants.IsNotChangeAPN))){
			String terminalReturn=task.getTerminalReturn();
			if(terminalReturn!=null){
				String[] values=terminalReturn.split(";");
				for(String s:values){
					if(s.startsWith("APN")){
						apn=s.substring(s.indexOf(":")+1, s.length());
					}
				}
			}
			if(context!=null && StringUtils.isBlank(apn)){
				apn=(String) context.get(ProtocolAttribute.AFN04_0AH_APN);
				task.appendTerminalReturn("APN:"+apn);
			}
		}else{
			apn=attr.get(ConfigConstants.APN);
		}
		
		if(Boolean.parseBoolean(attr.get(ConfigConstants.IsSetMainBySub))){
			String terminalReturn=task.getTerminalReturn();
			if(terminalReturn!=null){
				String[] values=terminalReturn.split(";");
				for(String s:values){
					if(s.startsWith("备用IP")){
						subIpString=s.substring(s.indexOf(":")+1, s.length());
					}
					if(s.startsWith("备用端口")){
						subPortString=s.substring(s.indexOf(":")+1, s.length());
					}
				}
			}
			if(context!=null && StringUtils.isBlank(subIpString) && StringUtils.isBlank(subPortString)){
				subIpString=context.get(ProtocolAttribute.AFN04_0AH_SUB_IP1)+"."+
						context.get(ProtocolAttribute.AFN04_0AH_SUB_IP2)+"."+
						context.get(ProtocolAttribute.AFN04_0AH_SUB_IP3)+"."+
						context.get(ProtocolAttribute.AFN04_0AH_SUB_IP4);
				subPortString=String.valueOf(context.get(ProtocolAttribute.AFN04_0AH_SUB_PORT));
				task.appendTerminalReturn("备用IP:"+subIpString);
				task.appendTerminalReturn("备用端口:"+subPortString);
			}
			mainIpString=subIpString;
			mainPortString=subPortString;
		}else{
			mainIpString=attr.get(ConfigConstants.MainIP);
			subIpString=attr.get(ConfigConstants.SubIP);
			mainPortString=attr.get(ConfigConstants.MainPort);
			subPortString=attr.get(ConfigConstants.SubPort);
		}

		
		int i=0;
		for(String s:mainIpString.split("\\.")){
			try{
				mainIp[i]=Integer.parseInt(s);
			}catch(Exception e){
				mainIp[i]=0;
			}
			i++;
		}
		i=0;
		for(String s:subIpString.split("\\.")){
			try{
				subIp[i]=Integer.parseInt(s);
			}catch(Exception e){
				subIp[i]=0;
			}
			i++;
		}
		
		try{
			mainPort=Integer.parseInt(mainPortString);
		}catch(Exception e){
			mainPort=0;
		}
		
		try{
			subPort=Integer.parseInt(subPortString);
		}catch(Exception e){
			subPort=0;
		}

		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultGBSingleFrame();
		task.setTerminalAddressAndSeq(decodedPacket);
		decodedPacket.put(ProtocolAttribute.AUX_IS_USE_PW, true);
		decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_ONE_DATA);
		decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_SET_PARAMETER);
		decodedPacket.put(ProtocolAttribute.SEQ_IS_NEED_CONFIRM, true);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 3);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_MAIN_IP1, mainIp[0]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_MAIN_IP2, mainIp[1]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_MAIN_IP3, mainIp[2]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_MAIN_IP4, mainIp[3]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_MAIN_PORT,mainPort);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_SUB_IP1, subIp[0]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_SUB_IP2, subIp[1]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_SUB_IP3, subIp[2]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_SUB_IP4, subIp[3]);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_SUB_PORT,subPort);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_APN,apn==null?"":apn);
		
		GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
				SpringBeanUtil.getBean("gBProtocolBreakerPool");
		DataPacketBuilder dataPacketBuilder=pbPool.getDataPacketBuilder();
		EncodedPacket encodedPacket= dataPacketBuilder
				.build(decodedPacket,task.getTaskConfig().getProtocolArea());
		pbPool.returnObject(dataPacketBuilder);
		encodedPacket.setTaskId(task.getId());
		EncodedDataSendingDelayQueue sendQueue=
				(EncodedDataSendingDelayQueue) SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		sendQueue.addPacket(encodedPacket);
		task.setDeadlineTime();
		task.updateActionNow("设置终端IP和APN");
	}
	
	public void finish(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("IP切换成功");
		task.setNextActionTime(System.currentTimeMillis());
	}
	
	public void failed(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("IP切换失败");
		task.setErrorCode(context);
		task.setNextActionTime(System.currentTimeMillis());
	}

}

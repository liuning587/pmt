package com.sx.mmt.internal.task.command;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.squirrelframework.foundation.fsm.TransitionPriority;
import org.squirrelframework.foundation.fsm.TransitionType;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.Command;
import com.sx.mmt.internal.api.DataPacketBuilder;
import com.sx.mmt.internal.api.Task;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ControlField;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBDecodedPacketFactory;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.SpringBeanUtil;

@States({
    @State(name=CommandState.Start), 
    @State(name=CommandState.OrderSended),
    @State(name=CommandState.Finish),
    @State(name=CommandState.Failed),
})

@Transitions({
	//任务开始，开始发送命令
    @Transit(from=CommandState.Start, to=CommandState.OrderSended, 
    		on=CommandEvent.Start, callMethod="sendOrder"),
    //发送命令收到确认应答，任务完成
    @Transit(from=CommandState.OrderSended, to=CommandState.OrderSended, 
    		on=CommandEvent.ResponseCome, callMethod="parserVersion",
    		priority=TransitionPriority.HIGH),
    //
    @Transit(from=CommandState.OrderSended, to=CommandState.Finish, 
    		on=CommandEvent.AllDataReceived),		
    //发送命令超时，重发命令
    @Transit(from=CommandState.OrderSended, to=CommandState.OrderSended, 
			on=CommandEvent.TimeUp, callMethod="sendOrder", 
			type=TransitionType.INTERNAL),
	//终端上线，重发命令
	@Transit(from=CommandState.OrderSended, to=CommandState.OrderSended, 
			on=CommandEvent.TerminalLogin, callMethod="sendOrder", 
			type=TransitionType.INTERNAL),
})



public class QueryVersionCommand extends AbstractStateMachine<QueryVersionCommand,String,String,DecodedPacket>
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
	public void sendOrder(String from, String to, String event, DecodedPacket context) throws Exception{
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultGBSingleFrame();
		task.setTerminalAddressAndSeq(decodedPacket);
		if(attr.get(ConfigConstants.VersionType).equals(ViewConstants.AppVersion) ||
				attr.get(ConfigConstants.VersionType).equals(ViewConstants.CoreVersion)){
			if(task.getTaskConfig().getProtocolType().equals(Head.PROTOCOL_GB05)){
				decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_TWO_DATA);
				decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_REQUEST_LEVEL_ONE_DATA_REAL);
			}else{
				decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_TWO_DATA);
				decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_REQUEST_TERMINAL_CONFIG);
			}
			
			decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 1);
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
			task.updateActionNow("发送查询app版本报文");
			
			
		}else if(attr.get(ConfigConstants.VersionType).equals(ViewConstants.RouterVersion)){
			
			decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_TWO_DATA);
			decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_REQUEST_TERMINAL_CONFIG);
			decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 25);
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
			task.updateActionNow("发送查询路由版本报文");
		}
		
		task.setDeadlineTime();
	}
	
	public void parserVersion(String from, String to, String event, DecodedPacket context){
		boolean in=true;
		boolean AppVersionCkeck=false;
		boolean AppDateCkeck=false;
		if(context==null){
			return;
		}
		if(!context.get(ProtocolAttribute.AFN_FUNCTION).equals(Afn.AFN_REQUEST_TERMINAL_CONFIG)
				&& !context.get(ProtocolAttribute.AFN_FUNCTION).equals(Afn.AFN_REQUEST_LEVEL_ONE_DATA_REAL)){
			return ;
		}else{
			if(((Integer)context.get(ProtocolAttribute.DATAUNITIDENTIFY_FN)!=1)){
				return ;
			}
		}
		String version=(String) context.get(ProtocolAttribute.AFN09_0C_APP_VERSION);
		
		if(attr.get(ConfigConstants.VersionType).equals(ViewConstants.AppVersion) ||
				attr.get(ConfigConstants.VersionType).equals(ViewConstants.CoreVersion)){
			task.updateActionNow("收到查询版本应答");
			if(attr.get(ConfigConstants.VersionType).equals(ViewConstants.AppVersion)){
				task.appendTerminalReturn("软件版本:"+version);
			}else if(attr.get(ConfigConstants.VersionType).equals(ViewConstants.CoreVersion)){
				version=(String) context.get(ProtocolAttribute.AFN09_0C_CORE_VERSION);
				task.appendTerminalReturn("核心版本:"+version);
			}
			
			if(Boolean.parseBoolean(attr.get(ConfigConstants.UseList))){
				String versionString=attr.get(ConfigConstants.VersionList);
				if(StringUtils.isBlank(versionString)){
					AppVersionCkeck=true;
				}else{
					String[] versionList=versionString.split(";");
					for(String s:versionList){
						if(s.equals(version)){
							AppVersionCkeck=true;
						}
					}
				}

			}else{
				AppVersionCkeck=true;
			}
			if(Boolean.parseBoolean(attr.get(ConfigConstants.UseDate))){
				Date d=(Date) context.get(ProtocolAttribute.AFN09_0C_APPDATE);
				task.appendTerminalReturn("软件版本日期:"+DateTool.getDateStringNoTime(d));
				if(StringUtils.isBlank(attr.get(ConfigConstants.DateFrom)) ||
						StringUtils.isBlank(attr.get(ConfigConstants.DateTo))){
					AppDateCkeck=true;
				}else{
					Date dFrom=DateTool.getDateFromString(attr.get(ConfigConstants.DateFrom));
					Date dTo=DateTool.getDateFromString(attr.get(ConfigConstants.DateTo));
					if(d.after(dFrom) && d.before(dTo)){
						AppDateCkeck=true;
					}
				}
			}else{
				AppDateCkeck=true;
			}
			if(!Boolean.parseBoolean(attr.get(ConfigConstants.UseList))
					&& !Boolean.parseBoolean(attr.get(ConfigConstants.UseDate))){
				task.setNextActionTime(System.currentTimeMillis());
				this.fire(CommandEvent.AllDataReceived);
				return;
			}

		}else if(attr.get(ConfigConstants.VersionType).equals(ViewConstants.RouterVersion)){
			task.updateActionNow("收到查询路由版本应答");
			task.appendTerminalReturn("软件版本:"+version);
			if(Boolean.parseBoolean(attr.get(ConfigConstants.UseList))){
				String versionString=attr.get(ConfigConstants.VersionList);
				if(StringUtils.isBlank(versionString)){
					AppVersionCkeck=true;
				}else{
					String[] versionList=versionString.split(";");
					for(String s:versionList){
						if(s.equals(version)){
							AppVersionCkeck=true;
						}
					}
				}
			}else{
				AppVersionCkeck=true;
			}
			if(Boolean.parseBoolean(attr.get(ConfigConstants.UseDate))){
				Date d=(Date) context.get(ProtocolAttribute.AFN09_0C_APPDATE);
				task.appendTerminalReturn("软件版本日期:"+DateTool.getDateStringNoTime(d));
				if(StringUtils.isBlank(attr.get(ConfigConstants.DateFrom)) ||
						StringUtils.isBlank(attr.get(ConfigConstants.DateTo))){
					AppDateCkeck=true;
				}else{
					Date dFrom=DateTool.getDateFromString(attr.get(ConfigConstants.DateFrom));
					Date dTo=DateTool.getDateFromString(attr.get(ConfigConstants.DateTo));
					if(d.after(dFrom) && d.before(dTo)){
						AppDateCkeck=true;
					}
				}
			}else{
				AppDateCkeck=true;
			}
			if(!Boolean.parseBoolean(attr.get(ConfigConstants.UseList))
					&& !Boolean.parseBoolean(attr.get(ConfigConstants.UseDate))){
				task.setNextActionTime(System.currentTimeMillis());
				this.fire(CommandEvent.AllDataReceived);
				return;
			}
		}
		
		if(Boolean.parseBoolean(attr.get(ConfigConstants.UseList))){
			if(!AppVersionCkeck){
				in=false;
			}
		}
		if(in){
			if(Boolean.parseBoolean(attr.get(ConfigConstants.UseDate))){
				if(!AppDateCkeck){
					in=false;
				}
			}
		}
		
		
		if(in){
			task.updateActionNow("版本已相符");
			String inexecute=attr.get(ConfigConstants.InExecute);
			if(!StringUtils.isBlank(inexecute)){
				task.updateActionNow("开始执行任务："+inexecute);
				task.setNewTask(inexecute);
				this.removeAllListeners();
				return;
			}
		}else{
			task.updateActionNow("版本不相符");
			String outexecute=attr.get(ConfigConstants.OutExecute);
			if(!StringUtils.isBlank(outexecute)){
				task.updateActionNow("开始执行任务："+outexecute);
				task.setNewTask(outexecute);
				this.removeAllListeners();
				return;
			}
		}
		task.setNextActionTime(System.currentTimeMillis());
		this.fire(CommandEvent.AllDataReceived);
		System.out.println(task.toString());
	}
	
}
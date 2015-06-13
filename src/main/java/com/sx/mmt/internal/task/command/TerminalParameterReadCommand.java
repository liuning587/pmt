package com.sx.mmt.internal.task.command;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.squirrelframework.foundation.fsm.TransitionPriority;
import org.squirrelframework.foundation.fsm.TransitionType;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
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
import com.sx.mmt.internal.task.TaskImpl;
import com.sx.mmt.internal.task.TaskImplDao;
import com.sx.mmt.internal.util.SimpleBytes;
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
    		on=CommandEvent.ResponseCome, callMethod="parserParameter",
    		priority=TransitionPriority.HIGH),
    //
    @Transit(from=CommandState.OrderSended, to=CommandState.Finish, 
    		on=CommandEvent.AllDataReceived),		
    //发送命令超时，重发命令
    @Transit(from=CommandState.OrderSended, to=CommandState.OrderSended, 
			on=CommandEvent.TimeUp, callMethod="sendOrder", 
			type=TransitionType.INTERNAL),		
})

public class TerminalParameterReadCommand extends 
						AbstractStateMachine<TerminalParameterReadCommand,String,String,DecodedPacket>
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
		decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_TWO_DATA);
		decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_QUERY_PARAMETER);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 10);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0);
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_QUERY_NUMBER, Integer.parseInt(attr.get(ConfigConstants.QueryNumber)));
		
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
		task.updateActionNow("发送读取终端参数报文");
		task.setDeadlineTime();
	}
	
	public void parserParameter(String from, String to, String event, DecodedPacket context){
		if(context!=null && !context.get(ProtocolAttribute.AFN_FUNCTION).equals(Afn.AFN_QUERY_PARAMETER)){
			return ;
		}else{
			if(context!=null && ((Integer)context.get(ProtocolAttribute.DATAUNITIDENTIFY_FN)!=10)){
				return ;
			}
		}
		List<Map<String,Object>> measuringPointDataList=(List<Map<String,Object>>) 
				context.get(ProtocolAttribute.AFN04_0AH_MEASURING_POINT_DATALIST);
		int portShift=Integer.parseInt(attr.get(ConfigConstants.PortShift));
		for(Map<String,Object> b:measuringPointDataList){
			int port=(int) b.get("port");
			port=port+portShift;
			b.put("port", port);
		}
		Gson gson = new Gson();
		task.setAdditionalParam1(gson.toJson(measuringPointDataList));
		task.setNextActionTime(System.currentTimeMillis());
		//保存档案
		final TaskImplDao taskImplDao=(TaskImplDao)SpringBeanUtil.getBean("taskImplDao");
		taskImplDao.execute(new Runnable() {
			
			@Override
			public void run() {
				taskImplDao.update(Lists.newArrayList((TaskImpl)task));
				
			}
		});
		this.fire(CommandEvent.AllDataReceived);
	}
}

package com.sx.mmt.internal.task.command;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.TransitionPriority;
import org.squirrelframework.foundation.fsm.TransitionType;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

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
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;

@States({
    @State(name=CommandState.Start), 
    @State(name=CommandState.DataSended),
    @State(name=CommandState.Finish),
    @State(name=CommandState.Failed),
})

@Transitions({
	//开始升级，发送数据
    @Transit(from=CommandState.Start, to=CommandState.DataSended, 
    		on=CommandEvent.Start, callMethod="sendData"),
    //发送数据收到确认应答，继续发送数据
    @Transit(from=CommandState.DataSended, to=CommandState.DataSended, 
    		on=CommandEvent.ResponseCome, callMethod="sendData", 
    		priority=TransitionPriority.HIGH,
    		when=ComfirmCondition.class),
    //发送数据收到收到否认应答，任务失败
    @Transit(from=CommandState.DataSended, to=CommandState.Failed, 
    		on=CommandEvent.ResponseCome, callMethod="failed", 
    		priority=TransitionPriority.MIDDLE,
    		when=DenyCondition.class),
    //发送数据等待超时，重发数据
    @Transit(from=CommandState.DataSended, to=CommandState.DataSended, 
			on=CommandEvent.TimeUp, callMethod="reSendData", 
			type=TransitionType.INTERNAL),
    //终端重连，重发数据
    @Transit(from=CommandState.DataSended, to=CommandState.DataSended, 
			on=CommandEvent.TerminalLogin, callMethod="reSendData", 
			type=TransitionType.INTERNAL),		
	//发送数据收到确认报文，文件发送完毕
    @Transit(from=CommandState.DataSended, to=CommandState.Finish, 
			on=CommandEvent.AllDataReceived, callMethod="finish"),

})


public class GBFileTransferCommand		
		extends AbstractStateMachine<GBFileTransferCommand,String,String,DecodedPacket> 
		implements Command{
	private static Logger logger = LoggerFactory.getLogger(GBFileTransferCommand.class);
	private Task task;
	public void setTask(Task task) {
		this.task = task;
	}
	protected Map<String,String> attr;
	@Override
	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}
	
	public void sendData(String from, String to, String event, DecodedPacket context) throws Exception {
		
		int index=task.getAndMovePacketIndex();
		send(index);
	}
	
	public void reSendData(String from, String to, String event, DecodedPacket context) throws Exception {
		int index=task.getPacketIndex();
		send(index-1);
	}
	
	private void send(int index) throws Exception{
		FileManager file=FileManager.getFile(attr);
		if(file==null){
			task.updateActionNow("加载升级文件失败");
			return;
		}
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultGBSingleFrame();
		task.setTerminalAddressAndSeq(decodedPacket);
		
		if(index>=file.getTotalSegment()){
			this.fire(CommandEvent.AllDataReceived);
			return;
		}
		SimpleBytes data=file.getDataSegments().get(index);
		decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_TWO_DATA);
		decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_FILE_TRANSFER);
		decodedPacket.put(ProtocolAttribute.SEQ_IS_NEED_CONFIRM, true);
		decodedPacket.put(ProtocolAttribute.AUX_IS_USE_PW, true);
		decodedPacket.put(ProtocolAttribute.SEQ_IS_HAVE_TIMETAG, true);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 1);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0);
		decodedPacket.put(ProtocolAttribute.AFN13H_SEGMENT_INDEX, index+1);
		decodedPacket.put(ProtocolAttribute.AFN13H_SEGMENT_LENGTH, data.getLength());
		decodedPacket.put(ProtocolAttribute.AFN13H_FILE_TOTAL_SEGMENT, file.getTotalSegment());
		decodedPacket.put(ProtocolAttribute.AFN13H_DATA_SECTION, data);
		
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
		task.updateActionNow("发送数据"+(index+1)+"，共"+file.getTotalSegment()+"包");
		task.setDeadlineTime();
		if((index+1)==file.getTotalSegment()){
			if(task.getRetryCount()>1){
				this.fire(CommandEvent.AllDataReceived);
				return;
			}else{
				task.setNextActionTime(System.currentTimeMillis()+180*1000);
			}
			
		}
	}
	
	public void failed(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("数据发送"+(task.getPacketIndex()+1)+"包时收到否认应答，请尝试重新开始");
	}
	
	public void finish(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("数据发送完毕");
		task.setNextActionTime(System.currentTimeMillis());
	}

}
package com.sx.mmt.internal.task.command;


import java.util.List;
import java.util.Map;

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
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocol.afn.AFN00Hp0f3;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBDecodedPacketFactory;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;


@States({
    @State(name=CommandState.Start), 
    @State(name=CommandState.StartOrderSended),
    @State(name=CommandState.DataSended),
    @State(name=CommandState.LastDataSended),
    @State(name=CommandState.RequestReceiveOrderSended),
    @State(name=CommandState.FinishOrderSended),
    @State(name=CommandState.Exception),
    @State(name=CommandState.Finish),
    @State(name=CommandState.Failed),
})

@Transitions({
	//开始升级，发送开始升级命令
    @Transit(from=CommandState.Start, to=CommandState.StartOrderSended, 
    		on=CommandEvent.Start, callMethod="startUpdate"),
    //启动升级收到确认应答，开始发送数据
    @Transit(from=CommandState.StartOrderSended, to=CommandState.DataSended, 
    		on=CommandEvent.ResponseCome, callMethod="beginSendData", 
    		priority=TransitionPriority.HIGH,
    		when=ComfirmCondition.class),
    //启动升级收到否认应答		
    @Transit(from=CommandState.StartOrderSended, to=CommandState.Exception, 
    		on=CommandEvent.ResponseCome, callMethod="startDeny", 
    		priority=TransitionPriority.MIDDLE,
    		when=DenyCondition.class),
    //发送开始升级命令超时，重发开始升级命令
    @Transit(from=CommandState.StartOrderSended, to=CommandState.StartOrderSended, 
			on=CommandEvent.TimeUp, callMethod="startUpdate", 
			type=TransitionType.INTERNAL),
	//终端登录，重发开始升级命令
    @Transit(from=CommandState.StartOrderSended, to=CommandState.StartOrderSended, 
			on=CommandEvent.TerminalLogin, callMethod="startUpdate", 
			type=TransitionType.INTERNAL),
    //
    @Transit(from=CommandState.DataSended, to=CommandState.LastDataSended, 
			on=CommandEvent.SendLastData),
	//发送最后一个报文
    @Transit(from=CommandState.LastDataSended, to=CommandState.LastDataSended, 
			on=CommandEvent.TimeUp, callMethod="sendRequestReceiveOrder",
			type=TransitionType.INTERNAL,priority=TransitionPriority.NORMAL),
			
	//发送最后一个报文
    @Transit(from=CommandState.LastDataSended, to=CommandState.Finish, 
			on=CommandEvent.ResponseCome, callMethod="finish",
			priority=TransitionPriority.HIGHEST,
			when=ComfirmCondition.class),
	//发送最后一个报文，文件发送失败
    @Transit(from=CommandState.LastDataSended, to=CommandState.Exception, 
			on=CommandEvent.ResponseCome, callMethod="finishDeny",
			priority=TransitionPriority.HIGHEST-1,
			when=DenyCondition.class),
    @Transit(from=CommandState.LastDataSended, to=CommandState.DataSended, 
			on=CommandEvent.ResponseCome, callMethod="sendData",
			priority=TransitionPriority.MIDDLE),
	//发送最后一个报文，文件发送失败	
    @Transit(from=CommandState.LastDataSended, to=CommandState.FinishOrderSended, 
			on=CommandEvent.AllDataReceived, callMethod="sendFinishOrder",
			priority=TransitionPriority.MIDDLE),			
    //发送数据时收到文件接收进度报文，发送补包
    @Transit(from=CommandState.DataSended, to=CommandState.DataSended, 
			on=CommandEvent.ResponseCome, callMethod="sendData",
			type=TransitionType.INTERNAL,priority=TransitionPriority.HIGH),
    //发送数据时接收文件接收进度报文超时，重发查询报文
    @Transit(from=CommandState.DataSended, to=CommandState.DataSended, 
			on=CommandEvent.TimeUp, callMethod="sendRequestReceiveOrder", 
			type=TransitionType.INTERNAL,priority=TransitionPriority.MIDDLE),
	//终端登录，重发查询报文
    @Transit(from=CommandState.DataSended, to=CommandState.DataSended, 
			on=CommandEvent.TerminalLogin, callMethod="sendRequestReceiveOrderWhenTerminalLogin", 
			type=TransitionType.INTERNAL,priority=TransitionPriority.MIDDLE),	
			
    //发送数据时收到文件接收进度报文，所有文件都已接收，发送完成升级命令
    @Transit(from=CommandState.DataSended, to=CommandState.FinishOrderSended, 
			on=CommandEvent.AllDataReceived, callMethod="sendFinishOrder"),
    //发送完成升级命令收到确认应答，文件发送完成
    @Transit(from=CommandState.FinishOrderSended, to=CommandState.Finish, 
			on=CommandEvent.ResponseCome, callMethod="finish", 
			priority=TransitionPriority.HIGH,
			when=ComfirmCondition.class),
	//发送完成升级命令收到否认应答，任务失败
    @Transit(from=CommandState.FinishOrderSended, to=CommandState.Exception, 
			on=CommandEvent.ResponseCome, callMethod="finishDeny", 
			priority=TransitionPriority.MIDDLE,
			when=DenyCondition.class),
	//发送完成升级命令超时，重发完成升级命令
    @Transit(from=CommandState.FinishOrderSended, to=CommandState.FinishOrderSended, 
			on=CommandEvent.TimeUp, callMethod="sendFinishOrder",
			type=TransitionType.INTERNAL),
			
    //异常处理	
    @Transit(from=CommandState.Exception, to=CommandState.DataSended, 
    		on=CommandEvent.RecoveryToDataSended),
    //异常处理	
    @Transit(from=CommandState.Exception, to=CommandState.Failed, 
    		on=CommandEvent.ToFailed),
    //异常处理	
    @Transit(from=CommandState.Exception, to=CommandState.Finish, 
    		on=CommandEvent.ToFinish),
			
})

public class UpdateFileCommand 
		extends AbstractStateMachine<UpdateFileCommand,String,String,DecodedPacket> 
				implements Command{

	private static final int maxPacketNumber=100;
	private Task task;
	public void setTask(Task task) {
		this.task = task;
	}
	protected Map<String,String> attr;
	@Override
	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}
	/**
	 * //开始升级,发送启动命令
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 * @throws Exception 
	 */
	public void startUpdate(String from, String to, String event, DecodedPacket context) throws Exception {
		FileManager file=FileManager.getFile(attr);
		if(file==null){
			task.updateActionNow("加载升级文件失败");
			return;
		}
		task.updateActionNow("准备启动升级报文");
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultSinglePacketForTerminalUpdate();
		task.setTerminalAddressAndSeq(decodedPacket);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 1)
			.put(ProtocolAttribute.AFN13H_FILE_NAME, file.getFileName())
			.put(ProtocolAttribute.AFN13H_FILE_TYPE, file.getFileType())
			.put(ProtocolAttribute.AFN13H_FILE_VERSION, file.getVersion())
			.put(ProtocolAttribute.AFN13H_FILE_MD5, file.getFileMD5())
			.put(ProtocolAttribute.AFN13H_ISCOMPRESSED, file.isCompressed())
			.put(ProtocolAttribute.AFN13H_FILE_COMPRESSED_LENGTH, file.getCompressedLength())
			.put(ProtocolAttribute.AFN13H_FILE_LENGTH, file.getLength())
			.put(ProtocolAttribute.AFN13H_ISREBOOT, file.isReboot())
			.put(ProtocolAttribute.AFN13H_REBOOT_DELAY_TIME, file.getRebootWaitTime())
			.put(ProtocolAttribute.AFN13H_FILE_TOTAL_SEGMENT, file.getTotalSegment())
			.put(ProtocolAttribute.AFN13H_SEGMENT_LENGTH, file.getSegmentDataLength())
			.put(ProtocolAttribute.AFN13H_FILE_SEGMENT_DATA_LENGTH, file.getSegmentDataLength());
		
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
		task.updateActionNow("发送启动升级报文"+(task.getRetryCount()==0?"":task.getRetryCount()));
	}
	/**
	 * 直接开始全部数据发送数据
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 * @throws Exception 
	 */
	public void beginSendData(String from, String to, String event, DecodedPacket context) throws Exception {
		FileManager file=FileManager.getFile(attr);
		if(file==null){
			task.updateActionNow("加载升级文件失败");
			return;
		}
		task.updateActionNow("准备数据报文");
		int interval=PropertiesUtil.parseToInt(ConfigConstants.PacketSendInterval)*1000;
		int packetnum=Math.min(maxPacketNumber, file.getTotalSegment());
		for(int i=0;i<packetnum;i++){
			send(i,interval*i,file,false);
		}
		task.setNextActionTime(System.currentTimeMillis()+(interval+1)*packetnum);
		task.updateActionNow("发送数据"+packetnum+"包,共"+file.getTotalSegment()+"包");
		
	}
	/**
	 * 根据收到的文件接收情况的报文补发数据
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 * @throws Exception 
	 */
	
	@SuppressWarnings("unchecked")
	public void sendData(String from, String to, String event, DecodedPacket context) throws Exception {
		
		if(context!=null && context.get(ProtocolAttribute.AFN_FUNCTION).equals(Afn.AFN_FILE_TRANSFER_FOR_UPDATE)){
			if((Integer)context.get(ProtocolAttribute.DATAUNITIDENTIFY_FN)==3){
				List<Integer> notreveived=(List<Integer>) context.get(ProtocolAttribute.AFN13H_NOT_RECEIVE_SEGMENT);
				if(notreveived.size()==0){
					this.fire(CommandEvent.AllDataReceived);
					task.updateActionNow("文件发送完毕");
					task.setNextActionTime(System.currentTimeMillis());
				}else{
					int interval=PropertiesUtil.parseToInt(ConfigConstants.PacketSendInterval)*1000;
					FileManager file=FileManager.getFile(attr);
					if(file==null){
						task.updateActionNow("加载升级文件失败");
						return;
					}
					
					if(notreveived.size()==1){
						send(notreveived.get(0),interval,file,true);
						task.updateActionNow("发送数据最后一包");
						task.setDeadlineTime();
						this.fire(CommandEvent.SendLastData);
						return;
					}
					if(notreveived.size()>maxPacketNumber){
						for(int i=0;i<maxPacketNumber;i++){
							send(notreveived.get(i),i*interval,file,false);
						}
						task.updateActionNow("发送数据"+maxPacketNumber+"包,剩余"+notreveived.size()+"包");
						task.setNextActionTime(System.currentTimeMillis()+interval*(maxPacketNumber+1));
					}else{
						for(int i=0;i<notreveived.size()-1;i++){
							send(notreveived.get(i),i*interval,file,false);
						}
						task.updateActionNow("发送数据"+(notreveived.size()-1)+"包,剩余"+notreveived.size()+"包");
						task.setNextActionTime(System.currentTimeMillis()+interval*(notreveived.size()+1));
					}
				}
			}
		}
	}
	
	private void send(int i,int delay,FileManager file,boolean needResponse) throws Exception{
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultSinglePacketForTerminalUpdate();
		task.setTerminalAddressAndSeq(decodedPacket);
		SimpleBytes data=file.getDataSegments().get(i);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 3);
		decodedPacket.put(ProtocolAttribute.AFN13H_SEGMENT_LENGTH,data.getLength());
		decodedPacket.put(ProtocolAttribute.AFN13H_SEGMENT_INDEX,i+1);
		decodedPacket.put(ProtocolAttribute.AFN13H_DATA_SECTION, data);
		
		GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
				SpringBeanUtil.getBean("gBProtocolBreakerPool");
		DataPacketBuilder dataPacketBuilder=pbPool.getDataPacketBuilder();
		EncodedPacket encodedPacket= dataPacketBuilder
				.build(decodedPacket,task.getTaskConfig().getProtocolArea());
		pbPool.returnObject(dataPacketBuilder);
		//不需要应答报文
		encodedPacket.setNeedResponse(needResponse);
		encodedPacket.setTaskId(task.getId());
		EncodedDataSendingDelayQueue sendQueue=
				(EncodedDataSendingDelayQueue) SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		sendQueue.addPacket(encodedPacket,delay);
	}
	
	/**
	 * 发送完成升级命令
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 * @throws Exception 
	 */
	public void sendFinishOrder(String from, String to, String event, DecodedPacket context) throws Exception {
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultSinglePacketForTerminalUpdate();
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 4);
		task.setTerminalAddressAndSeq(decodedPacket);

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
		task.updateActionNow("发送完成升级报文"+(task.getRetryCount()==0?"":task.getRetryCount()));
	}
	
	/**
	 * 发送文件接收情况查询
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 * @throws Exception 
	 */
	public void sendRequestReceiveOrder(String from, String to, String event, DecodedPacket context) throws Exception{
		FileManager file=FileManager.getFile(attr);
		if(file==null){
			task.updateActionNow("加载升级文件失败");
			return;
		}
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultSinglePacketForTerminalUpdate();
		task.setTerminalAddressAndSeq(decodedPacket);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 3);
		decodedPacket.put(ProtocolAttribute.AFN13H_SEGMENT_LENGTH,file.getSegmentDataLength());
		decodedPacket.put(ProtocolAttribute.AFN13H_SEGMENT_INDEX,0);
		decodedPacket.put(ProtocolAttribute.AFN13H_ISRESPONSE,true);
		decodedPacket.put(ProtocolAttribute.AFN13H_DATA_SECTION, new SimpleBytes());
		
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
		task.updateActionNow("查询文件接收情况"+(task.getRetryCount()==0?"":task.getRetryCount()));
	}
	
	public void sendRequestReceiveOrderWhenTerminalLogin(String from, String to, String event, DecodedPacket context) throws Exception{
		if(System.currentTimeMillis()-task.getNextActionTime()<PropertiesUtil.parseToInt(ConfigConstants.ResponseTimeout)*1000){
			sendRequestReceiveOrder(from,to,event,context);
		}
	}
	
	/**
	 * 完成升级，流程终点
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 */
	public void finish(String from, String to, String event, DecodedPacket context) {
		task.updateActionNow("文件升级成功");
		task.setNextActionTime(System.currentTimeMillis());
	}
	/**
	 * 开始升级收到否认应答，任务失败状态至failed
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 */
	public void startDeny(String from, String to, String event, DecodedPacket context) {
		task.updateActionNow("启动升级收到否认应答");
		for(String s:((Map<String,String>)context.get(ProtocolAttribute.AFN00H_CONFIRMDETAIL)).values()){
			if(s.equals(AFN00Hp0f3.AlreadyInUpdate)){
				this.fire(CommandEvent.RecoveryToDataSended);
				task.setNextActionTime(System.currentTimeMillis()+3000);
				task.updateActionNow("3秒后查询文件接收进度");
				return;
			}
		}
		this.fire(CommandEvent.ToFailed);
		task.setNextActionTime(System.currentTimeMillis());
		task.setErrorCode(context);
		
	}
	/**
	 * 完成升级收到否认应答，任务失败状态至failed
	 * @param from
	 * @param to
	 * @param event
	 * @param context
	 */
	public void finishDeny(String from, String to, String event, DecodedPacket context) {
		task.updateActionNow("完成升级收到否认应答");
		for(String s:((Map<String,String>)context.get(ProtocolAttribute.AFN00H_CONFIRMDETAIL)).values()){
			if(s.equals(AFN00Hp0f3.ParameterFileMissing)){
				this.fire(CommandEvent.ToFinish);
				task.setNextActionTime(System.currentTimeMillis()+3000);
				task.updateActionNow("文件发送完毕");
				return;
			}
		}
		this.fire(CommandEvent.ToFailed);
		task.setNextActionTime(System.currentTimeMillis());
		task.setErrorCode(context);
		
	}
	
	

}

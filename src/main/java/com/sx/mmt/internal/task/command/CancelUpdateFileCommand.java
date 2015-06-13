package com.sx.mmt.internal.task.command;

import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.DataPacketBuilder;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBDecodedPacketFactory;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class CancelUpdateFileCommand extends SinglePacketCommand<CancelUpdateFileCommand>{
	
	@Override
	public void sendOrder(String from, String to, String event, DecodedPacket context) throws Exception {
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultSinglePacketForTerminalUpdate();
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 2);
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
		task.updateActionNow("发送取消升级报文");
		task.setDeadlineTime();
	}
	@Override
	public void finish(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("取消升级成功");
		task.setNextActionTime(System.currentTimeMillis());
	}
	@Override
	public void orderDeny(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("取消升级失败");
		task.setNextActionTime(System.currentTimeMillis());
		
	}

}

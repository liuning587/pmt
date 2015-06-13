package com.sx.mmt.internal.task.command.cqdw;

import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.squirrelframework.foundation.fsm.TransitionPriority;
import org.squirrelframework.foundation.fsm.TransitionType;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.Command;
import com.sx.mmt.internal.api.Task;
import com.sx.mmt.internal.connection.ConnectXmlResolver;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.task.command.CommandEvent;
import com.sx.mmt.internal.task.command.CommandState;
import com.sx.mmt.internal.util.PropertiesUtil;
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
    @Transit(from=CommandState.OrderSended, to=CommandState.Finish, 
    		on=CommandEvent.ResponseCome, callMethod="finish", 
    		priority=TransitionPriority.HIGH,
    		when=CQComfirmCondition.class),
    //发送命令收到否认应答		
    @Transit(from=CommandState.OrderSended, to=CommandState.Failed, 
    		on=CommandEvent.ResponseCome, callMethod="orderDeny", 
    		priority=TransitionPriority.MIDDLE,
    		when=CQDenyCondition.class),
    //发送命令超时，重发命令
    @Transit(from=CommandState.OrderSended, to=CommandState.OrderSended, 
			on=CommandEvent.TimeUp, callMethod="sendOrder", 
			type=TransitionType.INTERNAL),		
})


public class CQFinishCommand extends AbstractStateMachine<CQFinishCommand,String,String,DecodedPacket>
					implements Command{
	
	
	protected Task task;
	
	public void setTask(Task task) {
		this.task = task;
	}
	protected Map<String,String> attr;
	@Override
	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}
	
	public void sendOrder(String from, String to, String event, DecodedPacket context) throws Exception {
		Document doc=new Document();
		Element rootEl=new Element(OtherConstants.para);
		doc.setRootElement(rootEl);
		rootEl.addContent(new Element(OtherConstants.terminalId)
				.setText(task.getId()));
		
		ConnectXmlResolver connectXmlResolver=(ConnectXmlResolver) 
				SpringBeanUtil.getBean("connectXmlResolver");

		rootEl.addContent(new Element(OtherConstants.recordNo)
				.setText(connectXmlResolver.getInUseConfig().getAttr().get(ConfigConstants.ChannelNo)));
		rootEl.addContent(new Element(OtherConstants.sucFlag).setText("1"));
		EncodedPacket encodedPacket=new EncodedPacket();
		encodedPacket.setAdditionalInfo(doc);
		EncodedDataSendingDelayQueue sendQueue=
				(EncodedDataSendingDelayQueue) SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		sendQueue.addPacket(encodedPacket);
		task.updateActionNow("发送升级完成命令");
		task.setDeadlineTime();
	}
	
	public void finish(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow((String) context.get(OtherConstants.replyContent));
		task.setNextActionTime(System.currentTimeMillis());
	}
	
	public void orderDeny(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow((String) context.get(OtherConstants.exceptionContent));
		task.setNextActionTime(System.currentTimeMillis());
		
	}
	
}

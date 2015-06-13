package com.sx.mmt.internal.task.command;

import java.util.Map;

import org.squirrelframework.foundation.fsm.StateMachine;
import org.squirrelframework.foundation.fsm.TransitionPriority;
import org.squirrelframework.foundation.fsm.TransitionType;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;










import com.sx.mmt.internal.api.Command;
import com.sx.mmt.internal.api.Task;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;


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
    		when=ComfirmCondition.class),
    //发送命令收到否认应答		
    @Transit(from=CommandState.OrderSended, to=CommandState.Failed, 
    		on=CommandEvent.ResponseCome, callMethod="orderDeny", 
    		priority=TransitionPriority.MIDDLE,
    		when=DenyCondition.class),
    //发送命令超时，重发命令
    @Transit(from=CommandState.OrderSended, to=CommandState.OrderSended, 
			on=CommandEvent.TimeUp, callMethod="sendOrder", 
			type=TransitionType.INTERNAL),		
})

public abstract class SinglePacketCommand<T extends StateMachine<T, String,String,DecodedPacket>> extends AbstractStateMachine<T,String,String,DecodedPacket>
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
		
	}
	
	public void finish(String from, String to, String event, DecodedPacket context) throws Exception {
		
	}
	
	public void orderDeny(String from, String to, String event, DecodedPacket context) throws Exception {
		
		
	}
	

}

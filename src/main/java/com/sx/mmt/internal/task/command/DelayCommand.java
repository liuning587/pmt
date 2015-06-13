package com.sx.mmt.internal.task.command;

import java.util.Map;

import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.api.Command;
import com.sx.mmt.internal.api.Task;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;

@States({
    @State(name=CommandState.Start), 
    @State(name=CommandState.Waiting),
    @State(name=CommandState.Finish),
})

@Transitions({
	//任务开始，开始等待
    @Transit(from=CommandState.Start, to=CommandState.Waiting, 
    		on=CommandEvent.Start, callMethod="wait"),
    //时间到，等待完成
    @Transit(from=CommandState.Waiting, to=CommandState.Finish, 
    		on=CommandEvent.TimeUp, callMethod="finish"),	
})

public class DelayCommand extends AbstractStateMachine<DelayCommand,String,String,DecodedPacket>
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
	public void wait(String from, String to, String event, DecodedPacket context) throws Exception {
		String delayTime=attr.get(ConfigConstants.DelayTime);
		task.setNextActionTime(System.currentTimeMillis()+Integer.parseInt(delayTime)*1000);
		task.updateActionNow("设置延时"+delayTime+"秒");
	}

	public void finish(String from, String to, String event, DecodedPacket context) throws Exception {
		String delayTime=attr.get(ConfigConstants.DelayTime);
		task.updateActionNow("延时"+delayTime+"秒结束");
	}
}

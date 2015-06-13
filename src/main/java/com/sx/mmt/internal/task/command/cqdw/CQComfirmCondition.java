package com.sx.mmt.internal.task.command.cqdw;

import org.squirrelframework.foundation.fsm.Condition;

import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;

public class CQComfirmCondition implements Condition<DecodedPacket>{
	@Override
	public boolean isSatisfied(DecodedPacket context) {
		boolean isComfirm=false;
		if(context.get(OtherConstants.replyContent)!=null){
			isComfirm=true;
		}
		return isComfirm;
	}

	@Override
	public String name() {
		return "CQComfirmCondition";
	}
}

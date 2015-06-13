package com.sx.mmt.internal.task.command.cqdw;

import org.squirrelframework.foundation.fsm.Condition;

import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;

public class CQDenyCondition implements Condition<DecodedPacket> {
	@Override
	public boolean isSatisfied(DecodedPacket context) {
		boolean isDeny=false;
		if(context.get(OtherConstants.exceptionContent)!=null){
			isDeny=true;
		}
		return isDeny;
	}

	@Override
	public String name() {
		return "CQDenyCondition";
	}
}

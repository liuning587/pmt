package com.sx.mmt.internal.task.command;

import java.util.Map;

import org.squirrelframework.foundation.fsm.Condition;

import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocol.afn.AFN00Hp0f3;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;

public class DenyCondition  implements Condition<DecodedPacket>{

	@Override
	public boolean isSatisfied(DecodedPacket context) {
		boolean isDeny=false;
		if(context.get(ProtocolAttribute.AFN_FUNCTION).equals(Afn.AFN_CONFIRM_OR_DENY)){
			if((int)context.get(ProtocolAttribute.DATAUNITIDENTIFY_FN)!=3){
				if(!(boolean)context.get(ProtocolAttribute.AFN00H_ISCONFIRM)){
					isDeny=true;
				}
			}else{
				for(String s:((Map<String,String>)context.get(ProtocolAttribute.AFN00H_CONFIRMDETAIL)).values()){
					if(!s.equals(AFN00Hp0f3.CONFIRM)){
						isDeny=true;
						break;
					}
				}
			}
		}
		return isDeny;
	}

	@Override
	public String name() {
		return "DenyCondition";
	}

}

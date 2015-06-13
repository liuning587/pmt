package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.protocol.DataBody;

public class AFN00Hp0f1 extends DataBody{
	public static final String NAME=AFN00H.CONFIRM_ALL;
	private boolean isConfirm=true;

	public boolean getIsConfirm() {
		return isConfirm;
	}
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 0;
	}
	
}

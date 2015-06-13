package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.protocol.DataBody;

public class AFN00Hp0f2 extends DataBody{
	public static final String NAME=AFN00H.DENY_ALL;
	private boolean isConfirm=false;

	public boolean getIsConfirm() {
		return isConfirm;
	}

	public void setIsConfirm(boolean isConfirm) {
	}
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 0;
	}
}

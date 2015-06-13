package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.protocol.DataBody;

public class AFN02Hp0f1 extends DataBody{
	public static final String NAME=AFN02H.Login;
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 0;
	}
}

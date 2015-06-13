package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.protocol.DataBody;

public class AFN01Hp0f2 extends DataBody{
	public static final String NAME=AFN01H.dataInitial;
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 0;
	}
}

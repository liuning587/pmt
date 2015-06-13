package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.protocol.DataBody;

public final class AFN13Hp0f2 extends DataBody{
	public static final String NAME=AFN13H.CANCEL_UPDATE;
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 0;
	}
}

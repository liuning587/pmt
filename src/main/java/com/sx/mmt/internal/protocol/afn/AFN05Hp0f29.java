package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.protocol.DataBody;

public class AFN05Hp0f29 extends DataBody{
	public static final String NAME=AFN05H.AllowedAotuReport;
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 0;
	}
}

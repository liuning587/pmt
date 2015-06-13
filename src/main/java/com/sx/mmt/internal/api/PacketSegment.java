package com.sx.mmt.internal.api;

import com.sx.mmt.internal.util.SimpleBytes;

public interface PacketSegment extends GBEncode,GBDecode,CheckRawData{

	SimpleBytes getRawValue();
	PacketSegment setRawValue(SimpleBytes rawValue);
	int getSegmentLength(String protocolType,String protocolArea);
	void clear();
	
	
}

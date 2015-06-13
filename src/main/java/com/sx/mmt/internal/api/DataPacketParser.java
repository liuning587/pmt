package com.sx.mmt.internal.api;

import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.util.SimpleBytes;

public interface DataPacketParser {
	DecodedPacket parse(SimpleBytes encodedPacket,String protocolArea) throws Exception;
	String getTerminalAddress(SimpleBytes packet);
	String getAfn(SimpleBytes packet);
	int getFn(SimpleBytes packet);
}

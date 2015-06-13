package com.sx.mmt.internal.api;

import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.SimpleBytes;

public interface DataPacketBuilder {
	EncodedPacket build(DecodedPacket decodedPacket,String protocolArea) throws Exception;
}

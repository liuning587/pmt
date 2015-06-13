package com.sx.mmt.internal.connection.stationModel.cqdwSocket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CQProtocolCodecFactory implements ProtocolCodecFactory{

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return new CQDecode();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return new CQEncode();
	}
}

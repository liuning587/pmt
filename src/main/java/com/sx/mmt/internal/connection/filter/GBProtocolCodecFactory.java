package com.sx.mmt.internal.connection.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.stereotype.Component;

public class GBProtocolCodecFactory implements ProtocolCodecFactory{

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return new GBDecode();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return new GBEncode();
	}

}

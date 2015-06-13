package com.sx.mmt.internal.connection.stationModel.AtctSocket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.sx.mmt.internal.connection.filter.GBEncode;

public class AtctProtocolCodecFactory implements ProtocolCodecFactory {
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return new AtctDecode();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return new GBEncode();
	}
}

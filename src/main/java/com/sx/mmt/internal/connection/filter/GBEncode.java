package com.sx.mmt.internal.connection.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.sx.mmt.internal.util.SimpleBytes;

public class GBEncode extends ProtocolEncoderAdapter{

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {
		SimpleBytes packet=(SimpleBytes)message;
		out.write(IoBuffer.wrap(packet.getBytesArray()));
		
	}

}

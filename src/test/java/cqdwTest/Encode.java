package cqdwTest;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class Encode extends ProtocolEncoderAdapter{
	private static final String charset="gb2312";
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {
		out.write(IoBuffer.wrap(((String)message).getBytes(charset)));
		
	}
}

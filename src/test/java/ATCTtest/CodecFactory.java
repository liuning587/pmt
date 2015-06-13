package ATCTtest;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.sx.mmt.internal.connection.filter.GBEncode;


public class CodecFactory implements ProtocolCodecFactory{
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return new Decode();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return new GBEncode();
	}
}

package com.sx.mmt.internal.connection.stationModel.cqdwSocket;

import java.io.ByteArrayOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class CQEncode extends ProtocolEncoderAdapter{
	private static final String charset="gb2312";
	private static XMLOutputter out;
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		getXMLOutputter().output((Document)message, os);
		out.write(IoBuffer.wrap(os.toByteArray()));
		os.close();
	}
	
	private XMLOutputter getXMLOutputter(){
		if(out==null){
			Format format = Format.getCompactFormat();
			format.setEncoding(charset);
			format.setLineSeparator("");
			out=new XMLOutputter(format);
			return out;
		}else{
			return out;
		}
	}
}

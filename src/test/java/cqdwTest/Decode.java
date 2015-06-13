package cqdwTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import com.sx.mmt.internal.util.SimpleBytes;

public class Decode extends CumulativeProtocolDecoder{
	private static final String charset="gbk";
	private static final SAXBuilder builder=new SAXBuilder();
	private final AttributeKey contextKey = new AttributeKey(
			getClass(), "CONTEXT");
	
	private Context getContext(final IoSession session) {
		Context context = (Context) session.getAttribute(contextKey);
		if (context == null) {
			context = new Context();
			session.setAttribute(contextKey, context);
		}
		return context;
	}
	
	private enum DecodeStatus{
		TAIL,NEXT
	}
	
	private class Context {
		private DecodeStatus decodeStatus;
		private String lastFiveString;
		private SimpleBytes packet=new SimpleBytes();
		public Context() {
			decodeStatus = DecodeStatus.TAIL;
			lastFiveString="";
		}
		public DecodeStatus getDecodeStatus() {
			return decodeStatus;
		}
		public void setDecodeStatus(DecodeStatus decodeStatus) {
			this.decodeStatus = decodeStatus;
		}
		public SimpleBytes getPacket() {
			return packet;
		}
		
		public String getLastFiveString() {
			return lastFiveString;
		}
		public void putCharToLast(byte b){
			packet.add(b);
			if(lastFiveString.length()<5){
				lastFiveString=String.format("%s%s", lastFiveString,(char)b);
			}else{
				lastFiveString=String.format("%s%s", lastFiveString.substring(1),(char)b);
			}
		}
		
	}
	
	@Override
	protected boolean doDecode(final IoSession session, final IoBuffer buffer,
			final ProtocolDecoderOutput out) {
		Context context = getContext(session);
		if (context.getDecodeStatus() == DecodeStatus.TAIL) {
			doDecodePacket(context, buffer, out);
		}
		boolean isArrived;
		if (context.getDecodeStatus() == DecodeStatus.NEXT) {
			isArrived = true;
			context.setDecodeStatus(DecodeStatus.TAIL);
		} else {
			isArrived = false;
		}
		return isArrived;
	}
	
	private void doDecodePacket(Context context, IoBuffer buffer,
			ProtocolDecoderOutput out){
		while(buffer.hasRemaining()){
			try{
				byte b=buffer.get();
				context.putCharToLast(b);
				if(b=='>' && context.getLastFiveString().equals("para>")){
					SimpleBytes p=context.getPacket();
					String recode=new String(p.getBytesArray(),charset);
					if(recode.endsWith("</para>")){
						InputStream in=new ByteArrayInputStream(recode.trim().getBytes(charset));
						Document doc=builder.build(in);
						out.write(doc);
						p.clear();
						context.setDecodeStatus(DecodeStatus.NEXT);
						break;
					}
				}
			}catch(Exception e){
				
			}
		}
	}
}

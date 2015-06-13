package cqdwTest;

import java.io.ByteArrayOutputStream;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocol.Seq;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.SimpleBytes;

public class Handler extends IoHandlerAdapter{
	private static final String charset="gb2312";
	private static XMLOutputter out;
	private static GBProtocolBreakerPool gBProtocolBreakerPool=new GBProtocolBreakerPool();
	public Handler(){
		gBProtocolBreakerPool.createPool();
	}
	private XMLOutputter getXMLOutputter(){
		if(out==null){
			Format format = Format.getCompactFormat();
			format.setEncoding(charset);
			out=new XMLOutputter(format);
			return out;
		}else{
			return out;
		}
	}
	@Override
	public void messageReceived(final IoSession session, final Object message)
			throws Exception {
		Document doc=(Document)message;
		Element rootEl=doc.getRootElement();
//		System.out.println("terminalId:"+rootEl.getChildText(OtherConstants.terminalId));
//		System.out.println("recordNo:"+rootEl.getChildText(OtherConstants.recordNo));
		if(rootEl.getChildText(OtherConstants.terminalId)!=null
				&&rootEl.getChildText(OtherConstants.recordNo)!=null
				&&rootEl.getChildText(OtherConstants.frameContent)==null
				&&rootEl.getChildText(OtherConstants.sucFlag)==null){
			Document repdoc=new Document();
			Element repRoot=new Element(OtherConstants.result);
			repRoot.addContent(new Element(OtherConstants.terminalId)
					.setText(rootEl.getChildText(OtherConstants.terminalId)));
			repRoot.addContent(new Element(OtherConstants.replyContent)
					.setText("通道申请成功"));
			repdoc.setRootElement(repRoot);
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			getXMLOutputter().output(repdoc, os);
			String re=String.format("%s%s", 0,new String(os.toByteArray(),charset));
			session.write(re);
		}
		
		if(rootEl.getChildText(OtherConstants.terminalId)!=null
				&&rootEl.getChildText(OtherConstants.recordNo)!=null
				&&rootEl.getChildText(OtherConstants.frameContent)==null
				&&rootEl.getChildText(OtherConstants.sucFlag)!=null){
			Document repdoc=new Document();
			Element repRoot=new Element(OtherConstants.result);
			repRoot.addContent(new Element(OtherConstants.terminalId)
					.setText(rootEl.getChildText(OtherConstants.terminalId)));
			repRoot.addContent(new Element(OtherConstants.replyContent)
					.setText("终端升级结束"));
			repdoc.setRootElement(repRoot);
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			getXMLOutputter().output(repdoc, os);
			String re=String.format("%s%s", 2,new String(os.toByteArray(),charset));
			session.write(re);
		}
		
		if(rootEl.getChildText(OtherConstants.terminalId)!=null
				&&rootEl.getChildText(OtherConstants.recordNo)!=null
				&&rootEl.getChildText(OtherConstants.frameContent)!=null
				&&rootEl.getChildText(OtherConstants.sucFlag)==null){
			DataPacketParser DataPacketParser=gBProtocolBreakerPool.getDataPacketParser();
			SimpleBytes packet=new SimpleBytes(rootEl.getChildText(OtherConstants.frameContent).trim(),16,true);
			DecodedPacket decodedPacket=DataPacketParser.parse(packet, "");
			gBProtocolBreakerPool.returnObject(DataPacketParser);
			SimpleBytes result=null;
			int seq=(int) decodedPacket.get(ProtocolAttribute.SEQ_SERIAL_NO);
			if(decodedPacket.get(ProtocolAttribute.AFN_FUNCTION)
					.equals(Afn.AFN_REQUEST_LEVEL_ONE_DATA_REAL)){
				
				result=packRequest(new SimpleBytes("68C100C100688802374523700CE0000001004E425358313030323030303143513033080814475A33444A475A323300000033091524003D16",16,true),seq);
				
			}
			if(decodedPacket.get(ProtocolAttribute.AFN_FUNCTION)
					.equals(Afn.AFN_FILE_TRANSFER)){
				result=packRequest1(new SimpleBytes("684A004A006888023745237000E0000001000009532312000B16",16,true),seq);
				
			}
			if(decodedPacket.get(ProtocolAttribute.AFN_FUNCTION)
					.equals(Afn.AFN_REQUEST_TERMINAL_CONFIG)){
				
				result=packRequest(new SimpleBytes("68D600D6006888023745237C096C000001004E425358313030323030303143513032080814475A33444A475A3233000043513133435130392607108E16",16,true),seq);
				
			}
			Document repdoc=new Document();
			Element repRoot=new Element(OtherConstants.result);
			repRoot.addContent(new Element(OtherConstants.terminalId)
					.setText(rootEl.getChildText(OtherConstants.terminalId)));
			repRoot.addContent(new Element(OtherConstants.frameContent)
					.setText(result.toReverseHexString(" ")));
			repdoc.setRootElement(repRoot);
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			getXMLOutputter().output(repdoc, os);
			String re=String.format("%s%s", 1,new String(os.toByteArray(),charset));
			session.write(re);
		}
	}
	
	public SimpleBytes packRequest(SimpleBytes packet,int seqn){
		SimpleBytes re=packet.getSubByteArray(0, 13);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		re.add(seq.getRawValue());
		re.add(packet.getSubByteArray(14, packet.getLength()-2));
		re.add(re.getCheckSum());
		re.add((byte)0x16);
		return re;
	}
	
	public SimpleBytes packRequest1(SimpleBytes packet,int seqn){
		SimpleBytes re=packet.getSubByteArray(0, 13);
		Seq seq=new Seq();
		seq.setIsHaveTimeTag(true);
		seq.setSerialNo(seqn);
		seq.encode("", "");
		re.add(seq.getRawValue());
		re.add(packet.getSubByteArray(14, packet.getLength()-2));
		re.add(re.getCheckSum());
		re.add((byte)0x16);
		return re;
	}
	

}

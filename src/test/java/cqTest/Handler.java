package cqTest;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocol.Seq;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.SimpleBytes;

public class Handler extends IoHandlerAdapter {
	private static GBProtocolBreakerPool gBProtocolBreakerPool=new GBProtocolBreakerPool();
	public Handler(){
		gBProtocolBreakerPool.createPool();
	}
	
	@Override
	public void messageReceived(final IoSession session, final Object message)
			throws Exception {
			SimpleBytes packet=(SimpleBytes)message;
			Thread.sleep(1000);
			DataPacketParser DataPacketParser=gBProtocolBreakerPool.getDataPacketParser();
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
				byte pfc=packet.getAt(packet.getLength()-6).toByte();
				result=packRequest1(new SimpleBytes("684A004A006888023745237000E0000001000B16",16,true),seq,pfc);
				
			}
			if(decodedPacket.get(ProtocolAttribute.AFN_FUNCTION)
					.equals(Afn.AFN_REQUEST_TERMINAL_CONFIG)){
				
				result=packRequest(new SimpleBytes("68D600D6006888023745237C096C000001004E425358313030323030303143513032080814475A33444A475A3233000043513133435130392607108E16",16,true),seq);
				
			}
			System.out.println(result);
			session.write(result);
		
	}
	
	public SimpleBytes packRequest(SimpleBytes packet,int seqn){
		SimpleBytes re=packet.getSubByteArray(0, 7);
		//SimpleBytes add
		
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		re.add(seq.getRawValue());
		re.add(packet.getSubByteArray(14, packet.getLength()-2));
		re.add(re.getCheckSum());
		re.add((byte)0x16);
		return re;
	}
	
	public SimpleBytes packRequest1(SimpleBytes packet,int seqn,byte pfc){
		SimpleBytes re=packet.getSubByteArray(0, 13);
		Seq seq=new Seq();
		seq.setIsHaveTimeTag(true);
		seq.setSerialNo(seqn);
		seq.encode("", "");
		re.add(seq.getRawValue());
		re.add(packet.getSubByteArray(14, packet.getLength()-2));
		Date day=new Date();
		String dayString=
				String.format("%s%s%s%s", 
						StringUtils.leftPad(String.valueOf(day.getSeconds()), 2,'0'),
						StringUtils.leftPad(String.valueOf(day.getMinutes()), 2,'0'),
						StringUtils.leftPad(String.valueOf(day.getHours()), 2,'0'),
						StringUtils.leftPad(String.valueOf(day.getDate()), 2,'0'));
		re.add((byte)pfc);
		re.add(new SimpleBytes(dayString,16,true));
		re.add((byte)5);
		re.add(re.getCheckSum());
		re.add((byte)0x16);
		return re;
	}
}

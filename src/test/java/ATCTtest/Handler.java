package ATCTtest;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.sx.mmt.constants.ConnectionConstants;
import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.DataUnitIdentify;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.SimpleBytes;

public class Handler  extends IoHandlerAdapter{
	private Controller controller;
	private DataUnitIdentify dataUnitIdentify=new DataUnitIdentify();
	private static GBProtocolBreakerPool gBProtocolBreakerPool=new GBProtocolBreakerPool();
	public Handler(){
		gBProtocolBreakerPool.createPool();
	}
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println(message);
		Thread.sleep(3000);
		if(message instanceof String){
			int i=(int) (Math.random()*5);
			SimpleBytes sb=new SimpleBytes("ATCT0001".getBytes());
			if(i==0){
				sb=new SimpleBytes("ATCT0001".getBytes());
			}else if(i==1){
				sb=new SimpleBytes("EROR0001".getBytes());
			}else if(i==2){
				sb=new SimpleBytes("ATCT0002".getBytes());
			}else if(i==3){
				sb=new SimpleBytes("ATCT0003".getBytes());
			}else if(i==4){
				sb=new SimpleBytes("ATCT0004".getBytes());
			}

			session.write(sb);
			return;
		}
		DataPacketParser DataPacketParser=gBProtocolBreakerPool.getDataPacketParser();
		DecodedPacket decodedPacket=DataPacketParser.parse((SimpleBytes) message, "");
		gBProtocolBreakerPool.returnObject(DataPacketParser);
		String terminaladdress=""+decodedPacket.get(ProtocolAttribute.ADDRESS_DISTRICT)+
				decodedPacket.get(ProtocolAttribute.ADDRESS_TERMINAL_ADDRESS);
		int seqn=(int) decodedPacket.get(ProtocolAttribute.SEQ_SERIAL_NO);
		int afn=Afn.getAfnCode((String) decodedPacket.get(ProtocolAttribute.AFN_FUNCTION));
		int fn=(int) decodedPacket.get(ProtocolAttribute.DATAUNITIDENTIFY_FN);
		if(afn==0x09){
			
			SimpleBytes sb=controller.getVersionPacket(terminaladdress,seqn);
			session.write(sb);
		}
		
		if(afn==0x13){
			if(fn==1){
				SimpleBytes sb=controller.getComfirmPacket(terminaladdress,seqn);
				session.write(sb);
				
			}
		}
		if(afn==0x13){
			if(fn==3 ){
				int index=controller.getReceived().get(terminaladdress);
				if(index==8){
					SimpleBytes sb=controller.getComfirmPacket(terminaladdress,seqn);
					session.write(sb);
					return;
				}
				SimpleBytes sb=controller.getReceivedPacket(terminaladdress,seqn,
						index);
				index++;
				controller.getReceived().put(terminaladdress, index);
				
				session.write(sb);
			}
		}

		if(afn==0x13){
			if(fn==4){
				SimpleBytes sb=controller.getComfirmPacket(terminaladdress,seqn);
				session.write(sb);
			}
		}
		if(afn==0x0A){
			if(fn==3){
				SimpleBytes sb=controller.getAPNPacket();
				session.write(sb);
			}
		}
		
		if(afn==0x01){
			SimpleBytes sb=controller.getComfirmPacket(terminaladdress,seqn);
			session.write(sb);
		}
		
		if(afn==0x04){
			SimpleBytes sb=controller.getComfirmPacket(terminaladdress,seqn);
			session.write(sb);
		}
		message=null;

	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception{
		
		
	}
	@Override
    public void sessionOpened(IoSession session) throws Exception {
        
    }
	
	@Override
    public void sessionClosed(IoSession session) throws Exception {
		System.out.println("session down"+session.getId());
		//controller.sendLogin((String) session.getAttribute(ConnectionConstants.TerminalAddress));
    }
}

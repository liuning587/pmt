package com.sx.mmt.testTerminal;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.sx.mmt.constants.ConnectionConstants;
import com.sx.mmt.internal.protocol.DataUnitIdentify;
import com.sx.mmt.internal.util.SimpleBytes;


public class TerminalHandler extends IoHandlerAdapter{
	private Controller controller;
	private DataUnitIdentify dataUnitIdentify=new DataUnitIdentify();
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String terminaladdress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
		SimpleBytes pa=(SimpleBytes)message;
		int seqn=pa.getAt(13).getSubBitsValue(0, 4).toInt();
		int afn=pa.getAt(12).toInt();
		dataUnitIdentify.clear();
		dataUnitIdentify.setRawValue(pa.getSubByteArray(14, 18));
		dataUnitIdentify.decode("", "");
		int fn=dataUnitIdentify.getFn();
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
			if(fn==3 && pa.getAt(19).toInt()==128){
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
				SimpleBytes sb=controller.getAPNPacket(terminaladdress, seqn);
				session.write(sb);
			}else if(afn==10){
				SimpleBytes sb=controller.getFile(terminaladdress, seqn);
				session.write(sb);
			}
		}
		if(afn==0x0c){
			if(fn==2){
				SimpleBytes sb=controller.getTerminalDate(terminaladdress,seqn);
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
		if(afn==0x0f){
			SimpleBytes sb=controller.getCQComfirmPacket(terminaladdress,seqn);
			Thread.sleep(1000);
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
		controller.sendLogin((String) session.getAttribute(ConnectionConstants.TerminalAddress));
    }
}
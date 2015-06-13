package com.sx.mmt.testTerminal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;

import com.sx.mmt.constants.ConnectionConstants;
import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.Seq;
import com.sx.mmt.internal.util.SimpleBytes;

public class Controller {
	private TerminalHandler handler;
	private TerminalConnector connector;
	private Map<String,Integer> received=new ConcurrentHashMap<String,Integer>();
	private static int count=0;
	
	public Controller(){
		handler=new TerminalHandler();
		handler.setController(this);
		connector=new TerminalConnector();
		connector.start(handler);
	}
	
	public void sendLogins(){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<20;i++){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					IoSession sess=connector.getSession();
					String terminaladdress=createTerminalAddress();
					SimpleBytes packet=getLoginPacket(terminaladdress);
					sess.setAttribute(ConnectionConstants.TerminalAddress, terminaladdress);
					received.put(terminaladdress, 1);
					sess.write(packet);
				}
			}
		}).start();

	}
	
	public void sendLogin(String terminaladdress){
		IoSession sessi=getConnector().getSession();
		sessi.setAttribute(ConnectionConstants.TerminalAddress,
				terminaladdress);
		SimpleBytes packet=getLoginPacket(terminaladdress);
		sessi.write(packet);
	}
	
	public String createTerminalAddress(){
		count++;
		return "2110"+StringUtils.leftPad(String.valueOf(count), 4, '0');
	}
	
	public SimpleBytes getFile(String terminalAddress,int seqn){
		
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)0xA);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		body.add(seq.getRawValue()).add((short)0).add((short)258);
		body.add(new SimpleBytes("020102611E9800009400000000000000004F0203611E9600009400000000000000004F1E5C",16,true));
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB05);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	
	public SimpleBytes getLoginPacket(String terminalAddress){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(0);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)2).add((byte)0x79).add((short)0).add((short)1);
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	public SimpleBytes getVersionPacket(String terminalAddress,int seqn){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)9);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		body.add(seq.getRawValue()).add((short)0).add((short)1);
		body.add(new SimpleBytes("73786471464b47413433000030653763201114383532463136523136533633373600332e32310101103dff",16,true));
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	public SimpleBytes getComfirmPacket(String terminalAddress,int seqn){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)0);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		body.add(seq.getRawValue()).add((short)0).add((short)1);
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	public SimpleBytes getReceivedPacket(String terminalAddress,int seqn,int index){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)0x13);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		body.add(seq.getRawValue()).add((short)0).add((short)4);
		if(index==1){
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFF0F000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",16,true));
		}else if(index==2){
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",16,true));
		}else if(index==3){
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0F0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",16,true));
		}else if(index==4){
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0000000000000000000000000000000000000000000000000000000000000000000000000000",16,true));
		}else if(index==5){
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0F00000000000000000000000000000000000000000000000000",16,true));
		}else if(index==6){
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000000000000000000000",16,true));
		}else if(index==7){
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00",16,true));
		}else{
			body.add(new SimpleBytes("B9020000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF01",16,true));
		}
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	//684a004a0068cb91910400000c64000002000039071743151216
	public SimpleBytes getTerminalDate(String terminalAddress,int seqn){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)0x0C);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		body.add(seq.getRawValue()).add((short)0).add((short)2);
		body.add(new SimpleBytes("053907174315",16,true));
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	
	public SimpleBytes getAPNPacket(String terminalAddress,int seqn){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)0x0A);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		body.add(seq.getRawValue()).add((short)0).add((short)4);
		body.add(new SimpleBytes("0000000000003CBE171E2823636D6E65740000000000000000000000",16,true));
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	public SimpleBytes getCQComfirmPacket(String terminalAddress,int seqn){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes(terminalAddress.substring(0, 4),16);
		SimpleBytes add=new SimpleBytes(terminalAddress.substring(4),16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)0);
		Seq seq=new Seq();
		seq.setSerialNo(seqn);
		seq.encode("", "");
		body.add(seq.getRawValue()).add((short)0).add((short)1);
		body.add(new SimpleBytes("014759121600",16,true));
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	public TerminalHandler getHandler() {
		return handler;
	}
	public void setHandler(TerminalHandler handler) {
		this.handler = handler;
	}
	public TerminalConnector getConnector() {
		return connector;
	}
	public void setConnector(TerminalConnector connector) {
		this.connector = connector;
	}



	public Map<String, Integer> getReceived() {
		return received;
	}

	public void setReceived(Map<String, Integer> received) {
		this.received = received;
	}
	
	
	
	
	
	
	
}
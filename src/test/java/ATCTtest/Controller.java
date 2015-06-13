package ATCTtest;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.Seq;
import com.sx.mmt.internal.util.SimpleBytes;


public class Controller {
	private Connector connector;
	private Handler handler;
	private Map<String,Integer> received=new ConcurrentHashMap<String,Integer>();
	private static int count=0;
	public Controller(){
		handler=new Handler();
		handler.setController(this);
		connector=new Connector();
		connector.setHandler(handler);
		
	}
	public void start(){
		try {
			connector.StartServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Controller controller=new Controller();
		controller.start();
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
		body.add(new SimpleBytes("4E425358313030323030303143513032200713475A33444A475A3233001C4351303443513031260710",16,true));
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
	
	
	public SimpleBytes getAPNPacket(){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes("2105",16);
		SimpleBytes add=new SimpleBytes("A1B2",16);
		address.setDistrict(dis.toHexString(""));
		address.setTerminalAddress(add.toHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(62);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)0x0A).add((byte)0x79).add((short)0).add((short)4);
		body.add(new SimpleBytes("0000000000003CBE171E2823636D6E65740000000000000000000000",16,true));
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public Map<String, Integer> getReceived() {
		return received;
	}
	public void setReceived(Map<String, Integer> received) {
		this.received = received;
	}
	
	
}

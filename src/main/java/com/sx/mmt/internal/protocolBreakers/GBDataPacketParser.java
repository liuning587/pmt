package com.sx.mmt.internal.protocolBreakers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ControlField;
import com.sx.mmt.internal.protocol.DataUnitIdentify;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.Seq;
import com.sx.mmt.internal.protocol.Tail;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;

public final class GBDataPacketParser implements DataPacketParser{
	private Map<String,PacketSegment> dataBodySegmentCache=new HashMap<String,PacketSegment>();
	private static Logger logger = LoggerFactory.getLogger(GBDataPacketBuilder.class);
	private Head head=new Head();
	private ControlField controlField=new ControlField();
	private Address address=new Address();;
	private Afn afn=new Afn();
	private Seq seq=new Seq();
	private DataUnitIdentify dataUnitIdentify=new DataUnitIdentify();
	private Tail tail=new Tail();
	private List<PacketSegment> baseSegment=new ArrayList<PacketSegment>();
	private static final String classPath="com.sx.mmt.internal.protocol.afn";
	public GBDataPacketParser(){		
		baseSegment.add(head);
		baseSegment.add(controlField);
		baseSegment.add(address);
		baseSegment.add(afn);
		baseSegment.add(seq);
		baseSegment.add(dataUnitIdentify);
		baseSegment.add(tail);
		
	}
	@Override
	public synchronized DecodedPacket parse(SimpleBytes packet,String protocolArea) throws Exception {
		clear();
		DecodedPacket decodedPacket=new DecodedPacket();
		head.setRawValue(packet.poll(Head.SEGMENT_LENGTH));
		head.decode("", "");
		controlField.setRawValue(packet.poll(controlField
				.getSegmentLength(head.getProtocolType(), protocolArea)));
		address.setRawValue(packet.poll(address
				.getSegmentLength(head.getProtocolType(), protocolArea)));
		afn.setRawValue(packet.poll(afn
				.getSegmentLength(head.getProtocolType(), protocolArea)));
		seq.setRawValue(packet.poll(seq
				.getSegmentLength(head.getProtocolType(), protocolArea)));
		dataUnitIdentify.setRawValue(packet.poll(dataUnitIdentify
				.getSegmentLength(head.getProtocolType(), protocolArea)));
		tail.setRawValue(packet.pollRear(Tail.SEGMENT_LENGTH));
		for(PacketSegment sg:baseSegment){
			sg.decode(head.getProtocolType(),protocolArea);
		}
		getBaseSegmentValue(decodedPacket);
		String afnClass=String.format("%s.AFN%sHp%sf%s", classPath,
				afn.getRawValue().toHexString("").trim(),dataUnitIdentify.getPn(),dataUnitIdentify.getFn());
		PacketSegment dataBody=dataBodySegmentCache.get(afnClass);
		if(dataBody==null){
			try{
				Class<?> dataBodyClass=
						Class.forName(afnClass);
				dataBody=(PacketSegment) dataBodyClass.newInstance();
				dataBodySegmentCache.put(afnClass, dataBody);
			}catch(ClassNotFoundException e){
				logger.error("not support "+afnClass+"decode");
			}
			
		}
		if(dataBody!=null){
			dataBody.setRawValue(packet);
			dataBody.decode(head.getProtocolType(),protocolArea);
			getSegmentValue(dataBody,decodedPacket);
		}
		return decodedPacket;
	}
	
	private void getBaseSegmentValue(DecodedPacket decodedPacket){
		for(PacketSegment sg:baseSegment){
			getSegmentValue(sg,decodedPacket);
		}
	}
	
	
	private void getSegmentValue(PacketSegment sg,DecodedPacket decodedPacket){
		Field[] field=sg.getClass().getDeclaredFields();
		try{
			for(int j=0;j<field.length;j++){
				String name = field[j].getName();
				if(field[j].toGenericString().indexOf("final")==-1 
						&& field[j].toGenericString().indexOf("static")==-1
						&& name.indexOf("dic")==-1){
					String methodName = name.substring(0, 1).toUpperCase() + name.substring(1);
					Method method = sg.getClass().getMethod("get" + methodName);
					Object value=method.invoke(sg);
					if(value!=null){
						decodedPacket.put(name, value);
					}
				}
			}
		}catch(Exception e){
			logger.error(ErrorTool.getErrorInfoFromException(e));
		}
	}


	private void clear(){
		for(PacketSegment sg:baseSegment){
			sg.clear();
		}
	}
	
	@Override
	public String getTerminalAddress(SimpleBytes packet){
		int terminalAddressLength= PropertiesUtil.parseToInt(ConfigConstants.ProtocolTerminalAddressLength);
		address.clear();
		int begin=Head.SEGMENT_LENGTH+controlField.getSegmentLength("","");
		address.setRawValue(packet.getSubByteArray(begin, begin+terminalAddressLength));
		if(terminalAddressLength==5){	
			address.decode("","");
		}else if(terminalAddressLength==7){
			address.decode("",ViewConstants.JiLing);
		}else{
			logger.error("终端地址长度配置错误");
		}
		return address.getDistrict()+address.getTerminalAddress();
	}
	
	@Override
	public String getAfn(SimpleBytes packet){
		afn.clear();
		int begin=Head.SEGMENT_LENGTH+controlField.getSegmentLength("","")
				+PropertiesUtil.parseToInt(ConfigConstants.ProtocolTerminalAddressLength);
		afn.setRawValue(packet.getAt(begin));
		afn.decode("", "");
		return afn.getFunction();
	}
	
	@Override
	public int getFn(SimpleBytes packet){
		dataUnitIdentify.clear();
		int begin=Head.SEGMENT_LENGTH+controlField.getSegmentLength("","")
				+PropertiesUtil.parseToInt(ConfigConstants.ProtocolTerminalAddressLength)
				+afn.getSegmentLength("","")+seq.getSegmentLength("","");
		int end=begin+dataUnitIdentify.getSegmentLength("", "");
		dataUnitIdentify.setRawValue(packet.getSubByteArray(begin, end));
		dataUnitIdentify.decode("", "");
		return dataUnitIdentify.getFn();
	}
}

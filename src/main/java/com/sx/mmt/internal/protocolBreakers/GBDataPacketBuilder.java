package com.sx.mmt.internal.protocolBreakers;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.api.DataPacketBuilder;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.Auxiliary;
import com.sx.mmt.internal.protocol.ControlField;
import com.sx.mmt.internal.protocol.DataUnitIdentify;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocol.Seq;
import com.sx.mmt.internal.protocol.Tail;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;

public final class GBDataPacketBuilder implements DataPacketBuilder{
	private Map<String,PacketSegment> dataBodySegmentCache=new HashMap<String,PacketSegment>();
	private static Logger logger = LoggerFactory.getLogger(GBDataPacketBuilder.class);
	private Head head=new Head();
	private ControlField controlField=new ControlField();
	private Address address=new Address();;
	private Afn afn=new Afn();
	private Seq seq=new Seq();
	private DataUnitIdentify dataUnitIdentify=new DataUnitIdentify();
	private Auxiliary auxiliary=new Auxiliary();
	private Tail tail=new Tail();
	private List<PacketSegment> baseSegment=new ArrayList<PacketSegment>();
	private static final String classPath="com.sx.mmt.internal.protocol.afn";
	public GBDataPacketBuilder(){		
		baseSegment.add(head);
		baseSegment.add(controlField);
		baseSegment.add(address);
		baseSegment.add(afn);
		baseSegment.add(seq);
		baseSegment.add(dataUnitIdentify);
		baseSegment.add(auxiliary);
		baseSegment.add(tail);
		
	}
	@Override
	public synchronized EncodedPacket build(DecodedPacket decodedPacket,String protocolArea) throws Exception{
		clear();
		SimpleBytes packetbody=new SimpleBytes();
		//装配参数并编码
		setBaseSegmentValue(decodedPacket);
		for(PacketSegment sg:baseSegment){
			sg.encode((String) decodedPacket.get(ProtocolAttribute.HEAD_PROTOCOL_TYPE),protocolArea);
		}
		
		//数据体编码
		if(decodedPacket.get(ProtocolAttribute.CustomDataBody)!=null){
			packetbody.add(controlField.getRawValue())
						.add(address.getRawValue())
						.add(afn.getRawValue())
						.add(seq.getRawValue())
						.add(dataUnitIdentify.getRawValue())
						.add((SimpleBytes)decodedPacket.get(ProtocolAttribute.CustomDataBody))
						.add(auxiliary.getRawValue());
			
		}else{
			String afnClass=String.format("%s.AFN%sHp%sf%s", classPath,
					afn.getRawValue().toHexString("").trim(),dataUnitIdentify.getPn(),dataUnitIdentify.getFn());
			PacketSegment dataBody=dataBodySegmentCache.get(afnClass);
			if(dataBody==null){
				Class<?> dataBodyClass=
						Class.forName(afnClass);
				dataBody=(PacketSegment) dataBodyClass.newInstance();
				dataBodySegmentCache.put(afnClass, dataBody);
			}
			setSegmentValue(dataBody,decodedPacket);
			dataBody.encode((String) decodedPacket.get(ProtocolAttribute.HEAD_PROTOCOL_TYPE),protocolArea);
			
			packetbody.add(controlField.getRawValue())
						.add(address.getRawValue())
						.add(afn.getRawValue())
						.add(seq.getRawValue())
						.add(dataUnitIdentify.getRawValue())
						.add(dataBody.getRawValue())
						.add(auxiliary.getRawValue());
		}
		//组装报文头尾
		head.setTotalDataLength(packetbody.getLength());
		head.encode((String) decodedPacket.get(ProtocolAttribute.HEAD_PROTOCOL_TYPE),protocolArea);
		tail.setCheckSum(packetbody.getCheckSum());
		tail.encode((String) decodedPacket.get(ProtocolAttribute.HEAD_PROTOCOL_TYPE),protocolArea);

		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(packetbody).add(tail.getRawValue());
		EncodedPacket encodedPacket=
				new EncodedPacket(String.format("%s%s", address.getDistrict(),address.getTerminalAddress()),
						packet,afn.getFunction(),
						String.format("p%sf%s",dataUnitIdentify.getPn(),dataUnitIdentify.getFn())
						);
		return encodedPacket;
		
	}
	
	private void setBaseSegmentValue(DecodedPacket decodedPacket){
		for(PacketSegment sg:baseSegment){
			setSegmentValue(sg,decodedPacket);
		}	
	}
	
	private void setSegmentValue(PacketSegment sg,DecodedPacket decodedPacket){
		Field[] field=sg.getClass().getDeclaredFields();
		try{
			for(int j=0;j<field.length;j++){
				String name = field[j].getName();
				if(field[j].toGenericString().indexOf("final")==-1 
						&& field[j].toGenericString().indexOf("static")==-1
						&& name.indexOf("dic")==-1){
					Object value=decodedPacket.get(name);
					if(value!=null){
						name = name.substring(0, 1).toUpperCase() + name.substring(1);
						String type = field[j].getGenericType().toString();
						if (type.equals("class java.lang.String")){
							Method method = sg.getClass().getMethod("set" + name,String.class);
							method.invoke(sg,(String)value);
							continue;
						}
						if(type.equals("int")){
							Method method = sg.getClass().getMethod("set" + name,int.class);
							method.invoke(sg,(int)value);
							continue;
						}
						if(type.equals("boolean")){
							Method method = sg.getClass().getMethod("set" + name,boolean.class);
							method.invoke(sg,(Boolean)value);
							continue;
						}
						if(type.equals("long")){
							Method method = sg.getClass().getMethod("set" + name,long.class);
							method.invoke(sg,(Long)value);
							continue;
						}
						if(type.equals("byte")){
							Method method = sg.getClass().getMethod("set" + name,byte.class);
							method.invoke(sg,(Byte)value);
							continue;
						}
						if(type.equals("java.util.List<java.util.Map<java.lang.String, java.lang.Object>>")){
							Method method = sg.getClass().getMethod("set" + name,List.class);
							method.invoke(sg, value);
							continue;
						}
						if(type.equals("class com.sx.mmt.internal.util.SimpleBytes")){
							Method method = sg.getClass().getMethod("set" + name,SimpleBytes.class);
							method.invoke(sg,(SimpleBytes) value);
							continue;
						}
						if(type.indexOf("java.util.Map")!=-1){
							Method method = sg.getClass().getMethod("set" + name,Map.class);
							method.invoke(sg, value);
							continue;
						}
						if(type.indexOf("java.util.List")!=-1){
							Method method = sg.getClass().getMethod("set" + name,List.class);
							method.invoke(sg, value);
							continue;
						}

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
		for(PacketSegment sg:dataBodySegmentCache.values()){
			sg.clear();
		}
	}
	
	
}

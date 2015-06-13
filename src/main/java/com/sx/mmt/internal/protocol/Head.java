package com.sx.mmt.internal.protocol;

import static com.google.common.base.Preconditions.checkNotNull;






import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;

public class Head extends PacketSegmentBase{
	public static final String NAME="Head";
	public static final int SEGMENT_LENGTH=6;
	public static final int LENGTH_CALCULATE_BEGIN=6;
	public static final int LENGTH_CALCULATE_END=-1;
	public static final byte HEAD_TAG=0x68;
	
	private static DualHashBidiMap dic=new DualHashBidiMap();
	//控制域、地址域、链路用户数据的总数据长度，为字节数
	private int totalDataLength=0;
	private String ProtocolType=PROTOCOL_GB09;
	
	public Head(){}
	public Head(SimpleBytes bytes){
		rawValue=bytes;
	}
	/**
	 * 协议类型
	 */
	public static final String PROTOCOL_GB05="Q/GDW.130-2005";
	public static final String PROTOCOL_GB09="Q/GDW.376.1-2009";
	public static final String PROTOCOL_GB13="Q/GDW.1376.1-2013";
	
	static{
		dic.put(0x1, PROTOCOL_GB05);
		dic.put(0x2, PROTOCOL_GB09);
		dic.put(0x3, PROTOCOL_GB13);
	}
	
	
	
	public static int getTotalDataLength(SimpleBytes data){
		return data.getSubByteArray(0,2).getSubBitsValue(2, 16).toInt();
	}
	
	
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		ProtocolType= (String) dic.get(rawValue.getAt(1).getSubBitsValue(0, 2).toInt());
		totalDataLength=rawValue.getSubByteArray(1,3).getSubBitsValue(2, 16).toInt();
		return this;
	}
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) {
		checkNotNull(totalDataLength);
		rawValue.clear();
		if(ProtocolType!=null){
			short b1=(short)((totalDataLength<<2)+(int)dic.getKey(ProtocolType));
			rawValue.add(HEAD_TAG).add(b1).add(b1).add(HEAD_TAG);
		}else{
			short b1=(short)((totalDataLength<<2)+(int)dic.getKey(protocolType));
			rawValue.add(HEAD_TAG).add(b1).add(b1).add(HEAD_TAG);
		}
		return this;
	}
	@Override
	public boolean check() {
		if(rawValue.getLength()==0 || rawValue.getLength()!=SEGMENT_LENGTH){
			return false;
		}
		if(rawValue.getAt(0).toByte()!=HEAD_TAG 
				|| rawValue.getAt(5).toByte()!=HEAD_TAG){
			return false;
		}
		if(rawValue.getAt(1).toByte()!=rawValue.getAt(3).toByte() 
				|| rawValue.getAt(2).toByte()!=rawValue.getAt(4).toByte()){
			return false;
		}
		return true;
	}
	
	@Override
	public void clear(){
		totalDataLength=0;
		ProtocolType=null;
		this.rawValue.clear();;
	}
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return Head.SEGMENT_LENGTH;
	}
	
	public String getProtocolType() {
		return ProtocolType;
	}
	public void setProtocolType(String protocolType) {
		ProtocolType = protocolType;
	}
	public void setTotalDataLength(int totalDataLength) {
		this.totalDataLength = totalDataLength;
	}
	public int getTotalDataLength() {
		return totalDataLength;
	}
}

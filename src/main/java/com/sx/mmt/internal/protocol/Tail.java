package com.sx.mmt.internal.protocol;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;

public class Tail extends PacketSegmentBase{
	public static final String NAME="Tail";
	public static final int SEGMENT_LENGTH=2;
	public static final int CHECKSUM_CALCULATE_BEGIN=6;
	public static final int CHECKSUM_CALCULATE_END=-1;
	public static final byte END_TAG=0x16;
	private byte checkSum=0;
	
	public Tail(){}
	public Tail(byte[] bytes){
		rawValue.add(bytes);
	}
	
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		this.checkSum=rawValue.getAt(0).toByte();
		return this;
	}
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) {
		rawValue.clear();
		this.rawValue.add(checkSum).add(END_TAG);
		return this;
	}
	
	@Override
	public boolean check() {
		if(rawValue.getLength()==0){
			return false;
		}
		if(rawValue.getAt(1).toByte()==END_TAG){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public void clear(){
		checkSum=0;
		rawValue.clear();
	}
	public boolean checkCheckSum(){
		return checkSum==rawValue.getAt(0).toByte();
	}
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return SEGMENT_LENGTH;
	}
	
	public byte getCheckSum() {
		return checkSum;
	}
	public void setCheckSum(byte checkSum) {
		this.checkSum=checkSum;
	}
}

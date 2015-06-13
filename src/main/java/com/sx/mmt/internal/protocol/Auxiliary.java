package com.sx.mmt.internal.protocol;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.exception.InvalidPacketException;
import com.sx.mmt.internal.api.CheckRawData;
import com.sx.mmt.internal.api.GBDecode;
import com.sx.mmt.internal.api.GBEncode;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;

public class Auxiliary extends PacketSegmentBase implements GBEncode,GBDecode,CheckRawData{
	public static final String NAME="Auxiliary";
	public boolean isUsePw=false;
	public boolean IsHaveTimeTag=false;
	//计数0-255
	private int pfc;

	@Override
	public boolean check() throws InvalidPacketException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		if(IsHaveTimeTag){
			// TODO Auto-generated method stub
		}
		return this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) throws InvalidPacketException {
		rawValue.clear();
		if(isUsePw){
			if(protocolType.endsWith(Head.PROTOCOL_GB05)){
				String passwd=PropertiesUtil.parseToStr(ConfigConstants.ProtocalPassword05);
				if(!StringUtils.isBlank(passwd)){
					rawValue.add(new SimpleBytes(passwd,16,true));
				}
				
			}else{
				String passwd=PropertiesUtil.parseToStr(ConfigConstants.ProtocalPassword);
				if(!StringUtils.isBlank(passwd)){
					rawValue.add(new SimpleBytes(passwd,16,true));
				}
			}
		}
		if(IsHaveTimeTag){
			Date day=new Date();
			String dayString=
					String.format("%s%s%s%s", 
							StringUtils.leftPad(String.valueOf(day.getSeconds()), 2,'0'),
							StringUtils.leftPad(String.valueOf(day.getMinutes()), 2,'0'),
							StringUtils.leftPad(String.valueOf(day.getHours()), 2,'0'),
							StringUtils.leftPad(String.valueOf(day.getDate()), 2,'0'));
			rawValue.add((byte)pfc);
			rawValue.add(new SimpleBytes(dayString,16,true));
			rawValue.add((byte)5);
		}
		return this;
	}
	@Override
	public void clear(){
		isUsePw=false;
		IsHaveTimeTag=false;
		rawValue.clear();
	}
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		if(isUsePw){
			if(protocolType.endsWith(Head.PROTOCOL_GB05)){
				return 2;
			}else{
				return 16;
			}
		}else{
			return 0;
		}
	}

	public boolean getIsUsePw() {
		return isUsePw;
	}

	public void setIsUsePw(boolean isUsePw) {
		this.isUsePw = isUsePw;
	}

	public boolean getIsHaveTimeTag() {
		return IsHaveTimeTag;
	}
	public void setIsHaveTimeTag(boolean isHaveTimeTag) {
		IsHaveTimeTag = isHaveTimeTag;
	}

	public int getPfc() {
		return pfc;
	}

	public void setPfc(int pfc) {
		this.pfc = pfc;
	}

	
	
	
	

}

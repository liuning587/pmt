package com.sx.mmt.internal.protocol;

import org.apache.commons.lang3.StringUtils;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.exception.InvalidPacketException;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;


/**
 * 行政区划码A1
 * 行政区划码按GB 2260—91的规定执行。
 * 终端地址A2
 * 终端地址A2选址范围为1～65535。A2=0000H为无效地址，A2=FFFFH且A3的D0位为“1”时表示系统广播地址。
 * 4.3.3.3.4　主站地址和组地址标志A3
 * A3的D0位为终端组地址标志，D0=0表示终端地址A2为单地址；D0=1表示终端地址A2为组地址；A3的D1～D7组成0～127个主站地址MSA。
 * ——主站启动的发送帧的MSA应为非零值，其终端响应帧的MSA应与主站发送帧的MSA相同。
 * ——终端启动发送帧的MSA应为零，其主站响应帧的MSA也应为零。
 * @author 王瑜甲
 *
 */
public class Address extends PacketSegmentBase{
	public static final String NAME="Address";
	
	//终端地址,前两个字节为行政区，后两个为终端地址，0000H为无效地址，FFFFH为广播地址
	private String district;
	private String terminalAddress;
	//主站地址
	//D0 为0表示终端地址为单地址，D0为1表示终端地址为组地址
	private boolean IsGroupAddress=false;
	//主站编号0-127，0表示终端启动帧，发送帧和响应帧msa相同
	private int msa;	
	public Address(){}
	public Address(SimpleBytes bytes){
		this.rawValue.add(bytes);
	}
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		if(protocolArea.endsWith(ViewConstants.JiLing)){
			district=rawValue.toSubHexString(5, 7,"");
			terminalAddress=rawValue.toSubHexString(1, 5,"");
			IsGroupAddress=(rawValue.getAt(6).toBits()[0]==1);
			msa=(new SimpleBytes(rawValue.getAt(6).toSubBits(1,7))).toInt();
		}else{
			district=rawValue.toSubHexString(3,5,"");
			terminalAddress=rawValue.toSubHexString(1,3,"");
			IsGroupAddress=(rawValue.getAt(4).toBits()[0]==1);
			msa=(new SimpleBytes(rawValue.getAt(4).toSubBits(1,7))).toInt();
		}
		return this;
	}
	
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) {
		rawValue.clear();
		if(protocolArea.equals(ViewConstants.JiLing)){
			SimpleBytes a1=new SimpleBytes(StringUtils.leftPad(district, 4,'0'),16);
			SimpleBytes a2=new SimpleBytes(StringUtils.leftPad(terminalAddress, 8,'0'),16);
			SimpleBytes a3=(new SimpleBytes((byte)msa)).insertBits(new int[]{IsGroupAddress?1:0}, 0);
			rawValue.add(a1).add(a2).add(a3.toByte());
		}else{
			SimpleBytes a1=new SimpleBytes(StringUtils.leftPad(district, 4,'0'),16);
			SimpleBytes a2=new SimpleBytes(StringUtils.leftPad(terminalAddress, 4,'0'),16);
			SimpleBytes a3=(new SimpleBytes((byte)msa)).insertBits(new int[]{IsGroupAddress?1:0}, 0);
			rawValue.add(a1).add(a2).add(a3.toByte());
		}
		return this;
	}
	
	@Override
	public boolean check() throws InvalidPacketException {
		if(rawValue.getLength()==0){
			return false;
		}
		if(terminalAddress.substring(4, 8).endsWith("0000")){
			return false;
		}
		if(terminalAddress.substring(4, 8).endsWith("FFFF")){
			if(!IsGroupAddress){
				throw new InvalidPacketException("FFFF address must group address");
			}
		}
		return true;
	}
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		if(protocolArea.equals(ViewConstants.JiLing)){
			return 7;
		}else{
			return 5;
		}
	}
	
	@Override
	public void clear(){
		district=null;
		terminalAddress=null;
		IsGroupAddress=false;
		msa=0;
		rawValue.clear();
	}
	
	public String getTerminalAddress() {
		return terminalAddress;
	}
	public void setTerminalAddress(String terminalAddress) {
		this.terminalAddress = terminalAddress;
	}
	public boolean getIsGroupAddress() {
		return IsGroupAddress;
	}
	public void setIsGroupAddress(boolean isGroupAddress) {
		IsGroupAddress = isGroupAddress;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public int getMsa() {
		return msa;
	}
	public void setMsa(int msa) {
		this.msa = msa;
	}
	
	
	
	


	

	
}

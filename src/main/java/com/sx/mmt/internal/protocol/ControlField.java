package com.sx.mmt.internal.protocol;

import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;
public class ControlField extends PacketSegmentBase{
	public static final String NAME="ControlField";

	public static final String DIR_PACKET_FROM_STATION="PacketFromStation";
	public static final String DIR_PACKET_FROM_TERMINAL="PacketFromTerminal";
	public static final String PRM_PACKET_FROM_MASTER="PacketFromMaster";
	public static final String PRM_PACKET_FROM_SLAVE="PacketFromSlave";
	public static final String ACD_TERMINAL_BUSY="TerminalBusy";
	public static final String ACD_TERMINAL_IDLE="TerminalIdle";
	
	public static final String PRM1_NONE="none";
	public static final String PRM1_RESET="Reset";
	public static final String PRM1_USER_DATA="UserData1";
	public static final String PRM1_LINK_TEST="LinkTest";
	public static final String PRM1_REQUEST_LEVEL_ONE_DATA="RequestLevelOneData";
	public static final String PRM1_REQUEST_LEVEL_TWO_DATA="RequestLevelTwoData";
	public static final String PRM0_ACCEPT="Accept";
	public static final String PRM0_USER_DATA="UserData0";
	public static final String PRM0_NO_REQUEST_DATA="NoRequestData";
	public static final String PRM0_LINK_STATUS="LinkStatus";
	
	public static DualHashBidiMap DIRdic=new DualHashBidiMap();
	public static DualHashBidiMap PRMdic=new DualHashBidiMap();
	public static DualHashBidiMap ACDdic=new DualHashBidiMap();
	public static DualHashBidiMap functionCodeForPRM1dic=new DualHashBidiMap();
	public static DualHashBidiMap functionCodeForPRM0dic=new DualHashBidiMap();
	
	static{
		DIRdic.put(0x0, DIR_PACKET_FROM_STATION);
		DIRdic.put(0x1, DIR_PACKET_FROM_TERMINAL);
		
		PRMdic.put(0x0, PRM_PACKET_FROM_SLAVE);
		PRMdic.put(0x1, PRM_PACKET_FROM_MASTER);
		
		ACDdic.put(0x0, ACD_TERMINAL_IDLE);
		ACDdic.put(0x1, ACD_TERMINAL_BUSY);
		
		functionCodeForPRM1dic.put(0x0, PRM1_NONE);
		functionCodeForPRM1dic.put(0x1, PRM1_RESET);
		functionCodeForPRM1dic.put(0x4, PRM1_USER_DATA);
		functionCodeForPRM1dic.put(0x9, PRM1_LINK_TEST);
		functionCodeForPRM1dic.put(0xA, PRM1_REQUEST_LEVEL_ONE_DATA);
		functionCodeForPRM1dic.put(0xB, PRM1_REQUEST_LEVEL_TWO_DATA);
		
		functionCodeForPRM0dic.put(0x0, PRM0_ACCEPT);
		functionCodeForPRM0dic.put(0x8, PRM0_USER_DATA);
		functionCodeForPRM0dic.put(0x9, PRM0_NO_REQUEST_DATA);
		functionCodeForPRM0dic.put(0xB, PRM0_LINK_STATUS);
	}
	
	/**
	 * 传输方向位DIR
	 * DIR=0：表示此帧报文是由主站发出的下行报文；	DIR=1：表示此帧报文是由终端发出的上行报文。
	 */
	private String DIR;
	/**
	 * 启动标志位PRM
	 * PRM =1：表示此帧报文来自启动站；PRM =0：表示此帧报文来自从动站。
	 */
	private String PRM;
	/**
	 * 帧计数位FCB
	 * 当帧计数有效位FCV=1时，FCB表示每个站连续的发送
	 * 确认或者请求/响应服务的变化位。FCB位用来防止信息传输的丢失和重复。
	 * 启动站向同一从动站传输新的发送/确认或请求/响应传输服务时，将FCB取相反值。
	 * 启动站保存每一个从动站FCB值，若超时未收到从动站的报文，或接收出现差错，
	 * 则启动站不改变FCB的状态，重复原来的发送/确认或者请求/响应服务。
	 * 复位命令中的FCB=0，从动站接收复位命令后将FCB置“0”。
	 */
	private int FCB=0;
	/**
	 * 请求访问位ACD
	 * ACD位用于上行响应报文中。ACD=1表示终端有重要事件等待访问，
	 * 则附加信息域中带有事件计数器EC（EC见本部分4.3.4.6.3）；ACD=0表示终端无事件数据等待访问。
	 * ACD置“1”和置“0”规则：
	 * ——自上次收到报文后发生新的重要事件，ACD位置“1”；
	 * ——收到主站请求事件报文并执行后，ACD位置“0”。
	 */
	private String ACD;
	/**
	 * 帧计数有效位FCV
	 * FCV=1：表示FCB位有效；FCV=0：表示FCB位无效。
	 */
	private boolean IsFCBValid=false;
	/**
	 * ——启动站功能码10（请求1级数据）用于应用层请求确认（CON=1）的链路传输，
	 * 应用层请求确认标志见本部分4.3.4.3.4。
	 * ——启动站功能码11（请求2级数据）用于应用层请求数据的链路传输。
	 */
	
	private String functionCode;

	

	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		int[] i=rawValue.toBits();
		DIR=(String) DIRdic.get(i[7]);
		PRM=(String) PRMdic.get(i[6]);
		ACD=(String) ACDdic.get(i[5]);
		if(i[6]==1){
			functionCode=(String) functionCodeForPRM1dic
					.get(new SimpleBytes(new int[]{i[0],i[1],i[2],i[3], 0,0,0,0}).toInt());
		}else if(i[6]==0){
			functionCode=(String) functionCodeForPRM0dic
					.get(new SimpleBytes(new int[]{i[0],i[1],i[2],i[3], 0,0,0,0}).toInt());	
		}
		
		return this;
	}
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) {
		rawValue.clear();
		byte b;
		if((int)DIRdic.getKey(DIR)==0){
			b=new SimpleBytes(new int[]{0,0,0,0,
					IsFCBValid?1:0,FCB,(int)PRMdic.getKey(PRM),(int)DIRdic.getKey(DIR)}).toByte();
		}else{
			b=new SimpleBytes(new int[]{0,0,0,0,
					IsFCBValid?1:0,(int)ACDdic.getKey(ACD),(int)PRMdic.getKey(PRM),(int)DIRdic.getKey(DIR)}).toByte();
		}
		if((int)PRMdic.getKey(PRM)==1){
			b+=(int)(functionCodeForPRM1dic.getKey(functionCode));
		}else if((int)PRMdic.getKey(PRM)==0){
			b+=(int)(functionCodeForPRM0dic.getKey(functionCode));
		}
		
		this.rawValue.add(b);
		return this;
	}

	public void setFunctionCodeByAfnWithEncode(int afn){
		switch (afn) {
		case 1:
			functionCode = PRM1_RESET;
			break;
		case 13:
			functionCode = PRM1_USER_DATA;
			break;
		case 2:
			functionCode = PRM1_LINK_TEST;
			break;
		case 5:
		case 6:
			functionCode = PRM1_REQUEST_LEVEL_ONE_DATA;
			break;
		default:
			functionCode = PRM1_REQUEST_LEVEL_TWO_DATA;
		}
	}
	
	@Override
	public boolean check() {

		return true;
	}
	@Override
	public void clear(){
		DIR=null;
		PRM=null;
		FCB=0;
		ACD=null;
		IsFCBValid=false;
		functionCode=null;
		rawValue.clear();
	}
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 1;
	}
	
	public String getDIR() {
		return DIR;
	}
	public String getPRM() {
		return PRM;
	}
	public int getFCB() {
		return FCB;
	}
	public void setFCB(int fCB) {
		FCB = fCB;
	}
	public String getACD() {
		return ACD;
	}
	public void setACD(String aCD) {
		ACD = aCD;
	}
	public boolean getIsFCBValid() {
		return IsFCBValid;
	}
	public void setIsFCBValid(boolean isFCBValid) {
		IsFCBValid = isFCBValid;
	}
	public String getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}
	public void setDIR(String dIR) {
		DIR = dIR;
	}
	public void setPRM(String pRM) {
		PRM = pRM;
	}
	
	
	

}

package com.sx.mmt.internal.protocol;

import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.sx.mmt.exception.InvalidPacketException;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;
/**
 * 
 * @author 王瑜甲
 *
 */
public class Afn extends PacketSegmentBase{
	public static final String NAME="Afn";
	public static DualHashBidiMap dic=new DualHashBidiMap();
	
	/**
	 * AFN应用层功能
	 */
	public static final String AFN_CONFIRM_OR_DENY="AfnConfirmOrDeny";
	public static final String AFN_RESET="AfnReset";
	public static final String AFN_LINK_INTERFACE_CHECK="AfnLinkInterfaceCheck";
	public static final String AFN_RELAY_STATION_COMMAND="AfnRelayStationCommand";
	public static final String AFN_SET_PARAMETER="AfnSetParameter";
	public static final String AFN_CONTROL_COMMAND="AfnControlCommand";
	public static final String AFN_AUTHENTICATION_AND_ENCRYPTION="AfnAuthenticationAndEncryption";
	public static final String AFN_REQUEST_CASCADE_TERMINAL_AUTO_REPORT="AfnRequestCascadeTerminalAutoReport";
	public static final String AFN_REQUEST_TERMINAL_CONFIG="AfnRequestTerminalConfig";
	public static final String AFN_QUERY_PARAMETER="AfnQueryParameter";
	public static final String AFN_REQUEST_TASK_DATA="AfnRequestTaskData";
	public static final String AFN_REQUEST_LEVEL_ONE_DATA_REAL="AfnRequestLevelOneData(real)";
	public static final String AFN_REQUEST_LEVEL_TWO_DATA_HISTORY="AfnRequestLevelTwoData(history)";
	public static final String AFN_REQUEST_LEVEL_THREE_DATA_EVENT="AfnRequestLevelThreeData(event)";
	public static final String AFN_FILE_TRANSFER="AfnFileTransfer";
	public static final String AFN_DATA_FORWARDING="AfnDataForwarding";
	//extended
	public static final String AFN_FILE_TRANSFER_FOR_UPDATE="AfnFileTransferForUpdate";
	
	private String function;
	static{
		dic.put(0x0,  AFN_CONFIRM_OR_DENY);
		dic.put(0x1,  AFN_RESET);
		dic.put(0x2,  AFN_LINK_INTERFACE_CHECK);
		dic.put(0x3,  AFN_RELAY_STATION_COMMAND);
		dic.put(0x4,  AFN_SET_PARAMETER);
		dic.put(0x5,  AFN_CONTROL_COMMAND);
		dic.put(0x6,  AFN_AUTHENTICATION_AND_ENCRYPTION);
		dic.put(0x8,  AFN_REQUEST_CASCADE_TERMINAL_AUTO_REPORT);
		dic.put(0x9,  AFN_REQUEST_TERMINAL_CONFIG);
		dic.put(0xA,  AFN_QUERY_PARAMETER);
		dic.put(0xB,  AFN_REQUEST_TASK_DATA);
		dic.put(0xC,  AFN_REQUEST_LEVEL_ONE_DATA_REAL);
		dic.put(0xD,  AFN_REQUEST_LEVEL_TWO_DATA_HISTORY);
		dic.put(0xE,  AFN_REQUEST_LEVEL_THREE_DATA_EVENT);
		dic.put(0xF,  AFN_FILE_TRANSFER);
		dic.put(0x10, AFN_DATA_FORWARDING);
		dic.put(0x13, AFN_FILE_TRANSFER_FOR_UPDATE);
	}
	
	public Afn(){}
	public Afn(SimpleBytes b){
		this.rawValue=b;
	}
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		function=(String) dic.get(rawValue.toInt());
		return this;
	}
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) {
		rawValue.clear();
		rawValue.add(((Integer)dic.getKey(function)).byteValue());
		return this;
	}
	public static String getAfnSymbol(int bte){
		return (String) dic.get(bte);
	}
	public static int getAfnCode(String Symbol){
		return (int) dic.getKey(Symbol);
	}
	@Override
	public boolean check() throws InvalidPacketException {
		if(rawValue.getLength()==0){
			return false;
		}
		String fuc=(String) dic.get(rawValue.toInt());
		if(fuc==null){
			throw new InvalidPacketException("AFN function not support");
		}else{
			return true;
		}
	}
	@Override
	public void clear(){
		function=null;
		rawValue.clear();
	}
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 1;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}

	
	

	
}

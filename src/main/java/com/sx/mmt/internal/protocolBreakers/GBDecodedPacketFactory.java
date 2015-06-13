package com.sx.mmt.internal.protocolBreakers;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ControlField;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.util.PropertiesUtil;

public class GBDecodedPacketFactory {
	public static DecodedPacket getDefaultSinglePacketForTerminalUpdate(){
		DecodedPacket packet=getDefaultGBSingleFrame();
		packet.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_USER_DATA)
			.put(ProtocolAttribute.AFN_FUNCTION, Afn.AFN_FILE_TRANSFER_FOR_UPDATE)
			.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0);
		return packet;	
	}
	
	public static DecodedPacket getDefaultGBPacketFromStation(){
		DecodedPacket packet=new DecodedPacket();
		packet.put(ProtocolAttribute.CONTROLFIELD_DIR, ControlField.DIR_PACKET_FROM_STATION)
			.put(ProtocolAttribute.CONTROLFIELD_PRM, ControlField.PRM_PACKET_FROM_MASTER)
			.put(ProtocolAttribute.CONTROLFIELD_FCB, 0)
			.put(ProtocolAttribute.CONTROLFIELD_ISFCBVALID, false);	
		return packet;
	}
	
	public static DecodedPacket getDefaultGBSingleFrame(){
		DecodedPacket packet=getDefaultGBPacketFromStation();
		packet.put(ProtocolAttribute.ADDRESS_MSA, 
				PropertiesUtil.parseToInt(ConfigConstants.ConnectionServerMsa));
		return packet;
	}
	
	public static DecodedPacket getResponseFrame(DecodedPacket received){
		DecodedPacket packet=new DecodedPacket();
		packet.put(ProtocolAttribute.HEAD_PROTOCOL_TYPE, received.get(ProtocolAttribute.HEAD_PROTOCOL_TYPE))
			.put(ProtocolAttribute.CONTROLFIELD_DIR, ControlField.DIR_PACKET_FROM_STATION)
			.put(ProtocolAttribute.CONTROLFIELD_PRM, ControlField.PRM_PACKET_FROM_SLAVE)
			.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM0_LINK_STATUS)
			.put(ProtocolAttribute.ADDRESS_DISTRICT, received.get(ProtocolAttribute.ADDRESS_DISTRICT))
			.put(ProtocolAttribute.ADDRESS_TERMINAL_ADDRESS, received.get(ProtocolAttribute.ADDRESS_TERMINAL_ADDRESS))
			.put(ProtocolAttribute.AFN_FUNCTION, Afn.AFN_CONFIRM_OR_DENY)
			.put(ProtocolAttribute.SEQ_SERIAL_NO, received.get(ProtocolAttribute.SEQ_SERIAL_NO))
			.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 3)
			.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0)
			.put(ProtocolAttribute.AFN00H_CONFIRMAFN, received.get(ProtocolAttribute.AFN_FUNCTION));
		return packet;
		
			
			
			
			
	}
}

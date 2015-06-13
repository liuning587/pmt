package com.sx.mmt.internal.protocol;

public class ProtocolAttribute {
	/**
	 * BASE
	 */
	public static final String HEAD_PROTOCOL_TYPE="ProtocolType";
	public static final String HEAD_TOTAL_DATA_LENGTH="totalDataLength";
	public static final String CONTROLFIELD_DIR="DIR";
	public static final String CONTROLFIELD_PRM="PRM";
	public static final String CONTROLFIELD_FCB="FCB";
	public static final String CONTROLFIELD_ACD="ACD";
	public static final String CONTROLFIELD_ISFCBVALID="IsFCBValid";
	public static final String CONTROLFIELD_FUNCTION_CODE="functionCode";
	public static final String ADDRESS_DISTRICT="district";
	public static final String ADDRESS_TERMINAL_ADDRESS="terminalAddress";
	public static final String ADDRESS_ISGROUPADDRESS="IsGroupAddress";
	public static final String ADDRESS_MSA="msa";
	public static final String AFN_FUNCTION="function";
	public static final String SEQ_IS_HAVE_TIMETAG="IsHaveTimeTag";
	public static final String SEQ_IS_FIRST_FRAME="IsFirstFrame";
	public static final String SEQ_IS_LAST_FRAME="IsLastFrame";
	public static final String SEQ_IS_NEED_CONFIRM="IsNeedConfirm";
	public static final String SEQ_SERIAL_NO="serialNo";
	public static final String DATAUNITIDENTIFY_PN="pn";
	public static final String DATAUNITIDENTIFY_FN="fn";
	public static final String AUX_IS_USE_PW="isUsePw";
	public static final String AUX_IS_USE_TP="isUseTp";
	public static final String TAIL_CHECKSUM="checkSum";
	public static final String AUX_PFC="pfc";
	
	public static final String CustomDataBody="customDataBody";
	
	/**
	 * AFN00H
	 */
	
	public static final String AFN00H_ISCONFIRM="isConfirm";
	public static final String AFN00H_CONFIRMAFN="confirmAfn";
	public static final String AFN00H_CONFIRMDETAIL="confirmDetail";
	
	
	/**
	 * AFN13H
	 */
	
	public static final String AFN13H_FILE_NAME="fileName";
	public static final String AFN13H_FILE_TYPE="fileType";
	public static final String AFN13H_FILE_VERSION="fileVersion";
	public static final String AFN13H_FILE_MD5="fileMD5";
	public static final String AFN13H_FILE_LENGTH="fileLength";
	public static final String AFN13H_FILE_COMPRESSED_LENGTH="fileCompressedLength";
	public static final String AFN13H_ISCOMPRESSED="isCompressed";
	public static final String AFN13H_ISREBOOT="isReboot";
	public static final String AFN13H_REBOOT_DELAY_TIME="rebootDelayTime";
	public static final String AFN13H_FILE_TOTAL_SEGMENT="fileTotalSegment";
	public static final String AFN13H_FILE_SEGMENT_DATA_LENGTH="fileSegmentDataLength";
	public static final String AFN13H_SEGMENT_INDEX="segmentIndex";
	public static final String AFN13H_SEGMENT_LENGTH="segmentLength";
	public static final String AFN13H_ISRESPONSE="isResponse";
	public static final String AFN13H_NOT_RECEIVE_SEGMENT="notReceiveSegment";
	public static final String AFN13H_DATA_SECTION="dataSection";
	
	/**
	 * port shift
	 */
	public static final String AFN04_0AH_MAIN_IP1="mainIp1";
	public static final String AFN04_0AH_MAIN_IP2="mainIp2";
	public static final String AFN04_0AH_MAIN_IP3="mainIp3";
	public static final String AFN04_0AH_MAIN_IP4="mainIp4";
	public static final String AFN04_0AH_MAIN_PORT="mainPort";
	public static final String AFN04_0AH_SUB_IP1="subIp1";
	public static final String AFN04_0AH_SUB_IP2="subIp2";
	public static final String AFN04_0AH_SUB_IP3="subIp3";
	public static final String AFN04_0AH_SUB_IP4="subIp4";
	public static final String AFN04_0AH_SUB_PORT="subPort";
	public static final String AFN04_0AH_APN="apn";
	
	
	/**
	 * read Parameter
	 */
	public static final String AFN04_0AH_QUERY_NUMBER="queryNumber";
	public static final String AFN04_0AH_MEASURING_POINT_DATALIST="measuringPointDataList";
	
	/**
	 * versionQuery
	 */
	public static final String AFN09_0C_FACTORY_CODE="factoryCode";
	public static final String AFN09_0C_TERMINAL_CODE="terminalCode";
	public static final String AFN09_0C_APP_VERSION="appVersion";
	public static final String AFN09_0C_MODULE_VERSION="moduleVersion";
	public static final String AFN09_0C_CORE_VERSION="coreVersion";
	public static final String AFN09_0C_APPDATE="appDate";
	public static final String AFN09_CARD_VERSION="cardVersion";
	public static final String AFN09_ROUTE_VERSION="routeVersion";
	
	
	public static final String AFN_0C_TERMINALDATE="terminalDate";
	
}

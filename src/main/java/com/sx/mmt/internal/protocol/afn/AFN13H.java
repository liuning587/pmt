package com.sx.mmt.internal.protocol.afn;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
public class AFN13H{	
	public static final String START_UPDATE="start update";
	public static final String CANCEL_UPDATE="cancel update";
	public static final String SEND_FILE_DATA="send file data";
	public static final String FINISH_UPDATE="finish update";
	public static DualHashBidiMap dic=new DualHashBidiMap();
	static{
		dic.put("p0f1", START_UPDATE);
		dic.put("p0f2", CANCEL_UPDATE);
		dic.put("p0f3", SEND_FILE_DATA);
		dic.put("p0f4", FINISH_UPDATE);
	}	
}

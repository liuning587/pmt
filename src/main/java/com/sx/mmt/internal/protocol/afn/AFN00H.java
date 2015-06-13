package com.sx.mmt.internal.protocol.afn;

import org.apache.commons.collections.bidimap.DualHashBidiMap;


public class AFN00H{
	public static final String CONFIRM_ALL="confirm all";
	public static final String DENY_ALL="deny all";
	public static final String CONFIRM_ONE_BY_ONE="confirm one by one";
	public static final String DATA_CHECK_ERROR="dataCheckError";
	public static DualHashBidiMap dic=new DualHashBidiMap();
	static{
		dic.put("p0f1", CONFIRM_ALL);
		dic.put("p0f2", DENY_ALL);
		dic.put("p0f3", CONFIRM_ONE_BY_ONE);
	}
}

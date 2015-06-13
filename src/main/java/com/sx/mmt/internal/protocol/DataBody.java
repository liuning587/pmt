package com.sx.mmt.internal.protocol;

import java.lang.reflect.Method;

import com.sx.mmt.exception.InvalidPacketException;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;

public class DataBody extends PacketSegmentBase {
	public static final String NAME="DataBody";
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {

		return this;
	}
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) throws InvalidPacketException {

		return this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static DataBody getAFNDataBody(byte code,int pn,int fn){
		if(Afn.getAfnSymbol(code)==null){
			return null;
		}
		try {
			Class AFNEntery= Class.forName("AFN"+Integer.toHexString(code).toUpperCase()+"H");
			Method method=AFNEntery.getMethod("getDataBody", new Class[]{int.class,int.class});
			return (DataBody) method.invoke(null,pn,fn);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static DataBody getAFNDataBody(String symbol,String pnfn){
		if(symbol==null){
			return null;
		}
		int code=Afn.getAfnCode(symbol);
		try {
			Class AFNEntery= Class.forName("AFN"+Integer.toHexString(code).toUpperCase()+"H");
			Method method=AFNEntery.getMethod("getDataBody", new Class[]{String.class});
			return (DataBody) method.invoke(null,pnfn);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	@Override
	public boolean check() {

		return true;
	}
	
	@Override
	public void clear(){
		rawValue.clear();
	}

	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 0;
	}


}

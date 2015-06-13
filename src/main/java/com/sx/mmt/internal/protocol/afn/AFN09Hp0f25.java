package com.sx.mmt.internal.protocol.afn;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.ErrorTool;

public class AFN09Hp0f25 extends DataBody{
	public static final String NAME=AFN09H.RouteVersion;
	private static Logger logger = LoggerFactory.getLogger(AFN09Hp0f25.class);
	private String factoryCode;
	private String cardVersion;
	private String routeVersion;
	private Date appDate;
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		try{
			factoryCode=rawValue.toAscii(0, 2);
			cardVersion=rawValue.toAscii(2,4);
			String ad="20"+rawValue.getAt(6).toBCD()+"-"+
					rawValue.getAt(5).toBCD()+"-"+rawValue.getAt(4).toBCD();
			appDate=DateTool.getDateFromString(ad);
			routeVersion=rawValue.toSubHexString(7, 9, "");
		}catch(ArrayIndexOutOfBoundsException e){
			logger.error("IndexOutOfBounds");
		}
		return this;
	}
	
	@Override
	public void clear() {
		factoryCode=null;
		cardVersion=null;
		routeVersion=null;
		appDate=null;
		rawValue.clear();
	};
	public String getFactoryCode() {
		return factoryCode;
	}
	public void setFactoryCode(String factoryCode) {
		this.factoryCode = factoryCode;
	}
	public String getCardVersion() {
		return cardVersion;
	}
	public void setCardVersion(String cardVersion) {
		this.cardVersion = cardVersion;
	}
	public String getRouteVersion() {
		return routeVersion;
	}
	public void setRouteVersion(String routeVersion) {
		this.routeVersion = routeVersion;
	}
	public Date getAppDate() {
		return appDate;
	}
	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}
	
	
	
	
}

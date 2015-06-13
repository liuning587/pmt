package com.sx.mmt.internal.protocol.afn;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.ErrorTool;

public class AFN0CHp0f1 extends DataBody{
	public static final String NAME=AFN0CH.TerminalVersion05;
//	private static Logger logger = LoggerFactory.getLogger(AFN0CHp0f1.class);
	private String factoryCode;
	private String terminalCode;
	private String appVersion;
	private String moduleVersion;
	private String coreVersion;
	private Date appDate;
	
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
			int size=rawValue.getLength();
			if(protocolArea.equals(ViewConstants.GuangXi)){
				factoryCode=size>=4?rawValue.toAscii(0, 4):"";
				terminalCode=size>=12?rawValue.toAscii(4,12):"";
				appVersion=size>=16?rawValue.toAscii(12, 16):"";
				moduleVersion="";
				coreVersion="";
				if(size>=19){
					String ad="20"+rawValue.getAt(18).toBCD()+"-"+
							rawValue.getAt(17).toBCD()+"-"+rawValue.getAt(16).toBCD();
					appDate=DateTool.getDateFromString(ad);
				}
			}else if(protocolArea.equals(ViewConstants.ShangXi)){
				factoryCode=size>=4?rawValue.toAscii(0, 4):"";
				terminalCode=size>=18?rawValue.toAscii(4,18):"";
				appVersion=size>=22?rawValue.toAscii(18, 22):"";
				if(size>=25){
					String ad="20"+rawValue.getAt(24).toBCD()+"-"+
							rawValue.getAt(23).toBCD()+"-"+rawValue.getAt(22).toBCD();
					appDate=DateTool.getDateFromString(ad);
				}
				moduleVersion=size>=36?rawValue.toAscii(25, 36):"";
				coreVersion=size>=44?rawValue.toAscii(40, 44):"";
			}else{
				factoryCode=size>=4?rawValue.toAscii(0, 4):"";
				terminalCode=size>=12?rawValue.toAscii(4,12):"";
				appVersion=size>=16?rawValue.toAscii(12, 16):"";
				if(size>=19){
					String ad="20"+rawValue.getAt(18).toBCD()+"-"+
							rawValue.getAt(17).toBCD()+"-"+rawValue.getAt(16).toBCD();
					appDate=DateTool.getDateFromString(ad);
				}
				moduleVersion=size>=30?rawValue.toAscii(19, 30):"";
				coreVersion=size>=38?rawValue.toAscii(34, 38):"";
				
			}
		

		return this;
	}
	
	@Override
	public void clear(){
		factoryCode=null;
		terminalCode=null;
		appVersion=null;
		moduleVersion=null;
		coreVersion=null;
		appDate=null;
		rawValue.clear();
				
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public String getCoreVersion() {
		return coreVersion;
	}

	public void setCoreVersion(String coreVersion) {
		this.coreVersion = coreVersion;
	}

	public String getFactoryCode() {
		return factoryCode;
	}

	public void setFactoryCode(String factoryCode) {
		this.factoryCode = factoryCode;
	}

	public String getTerminalCode() {
		return terminalCode;
	}

	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	
	
	

}



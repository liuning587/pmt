package com.sx.mmt.internal.protocol.afn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;

public class AFN0AHp0f3 extends DataBody{
	private static Logger logger = LoggerFactory.getLogger(AFN0AHp0f3.class);
	private int mainIp1;
	private int mainIp2;
	private int mainIp3;
	private int mainIp4;
	private int mainPort;
	private int subIp1;
	private int subIp2;
	private int subIp3;
	private int subIp4;
	private int subPort;
	private String apn;
	
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		if(ViewConstants.GuangXi.equals(protocolArea)){
			try{
				mainIp1=rawValue.poll(1).toInt();
				mainIp2=rawValue.poll(1).toInt();
				mainIp3=rawValue.poll(1).toInt();
				mainIp4=rawValue.poll(1).toInt();
				mainPort=rawValue.poll(2).toInt();
				subIp1=rawValue.poll(1).toInt();
				subIp2=rawValue.poll(1).toInt();
				subIp3=rawValue.poll(1).toInt();
				subIp4=rawValue.poll(1).toInt();
				subPort=rawValue.poll(2).toInt();
				rawValue.poll(12);
				apn=rawValue.toAscii(0, 16).trim();
			}catch(ArrayIndexOutOfBoundsException e){
				logger.error("IndexOutOfBounds");
			}
		}else{
			try{
				mainIp1=rawValue.poll(1).toInt();
				mainIp2=rawValue.poll(1).toInt();
				mainIp3=rawValue.poll(1).toInt();
				mainIp4=rawValue.poll(1).toInt();
				mainPort=rawValue.poll(2).toInt();
				subIp1=rawValue.poll(1).toInt();
				subIp2=rawValue.poll(1).toInt();
				subIp3=rawValue.poll(1).toInt();
				subIp4=rawValue.poll(1).toInt();
				subPort=rawValue.poll(2).toInt();
				apn=rawValue.toAscii(0, 16).trim();
			}catch(ArrayIndexOutOfBoundsException e){
				logger.error("IndexOutOfBounds");
			}
		}
		
		return this;
	}
	
	@Override
	public void clear() {
		mainIp1=0;
		mainIp2=0;
		mainIp3=0;
		mainIp4=0;
		mainPort=0;
		subIp1=0;
		subIp2=0;
		subIp3=0;
		subIp4=0;
		subPort=0;
		rawValue.clear();
	};
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 28;
	}

	public int getMainIp1() {
		return mainIp1;
	}

	public void setMainIp1(int mainIp1) {
		this.mainIp1 = mainIp1;
	}

	public int getMainIp2() {
		return mainIp2;
	}

	public void setMainIp2(int mainIp2) {
		this.mainIp2 = mainIp2;
	}

	public int getMainIp3() {
		return mainIp3;
	}

	public void setMainIp3(int mainIp3) {
		this.mainIp3 = mainIp3;
	}

	public int getMainIp4() {
		return mainIp4;
	}

	public void setMainIp4(int mainIp4) {
		this.mainIp4 = mainIp4;
	}

	public int getMainPort() {
		return mainPort;
	}

	public void setMainPort(int mainPort) {
		this.mainPort = mainPort;
	}

	public int getSubIp1() {
		return subIp1;
	}

	public void setSubIp1(int subIp1) {
		this.subIp1 = subIp1;
	}

	public int getSubIp2() {
		return subIp2;
	}

	public void setSubIp2(int subIp2) {
		this.subIp2 = subIp2;
	}

	public int getSubIp3() {
		return subIp3;
	}

	public void setSubIp3(int subIp3) {
		this.subIp3 = subIp3;
	}

	public int getSubIp4() {
		return subIp4;
	}

	public void setSubIp4(int subIp4) {
		this.subIp4 = subIp4;
	}

	public int getSubPort() {
		return subPort;
	}

	public void setSubPort(int subPort) {
		this.subPort = subPort;
	}

	public String getApn() {
		return apn;
	}

	public void setApn(String apn) {
		this.apn = apn;
	}
	
	
	
}

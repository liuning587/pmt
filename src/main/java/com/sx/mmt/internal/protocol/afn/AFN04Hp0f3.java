package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.util.SimpleBytes;

public class AFN04Hp0f3 extends DataBody{
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
	public PacketSegment encode(String protocolType,String protocolArea){
		rawValue.clear();
		if(ViewConstants.GuangXi.equals(protocolArea)){
			rawValue.add((byte) mainIp1);
			rawValue.add((byte) mainIp2);
			rawValue.add((byte) mainIp3);
			rawValue.add((byte) mainIp4);
			rawValue.add((short) mainPort);
			rawValue.add((byte) subIp1);
			rawValue.add((byte) subIp2);
			rawValue.add((byte) subIp3);
			rawValue.add((byte) subIp4);
			rawValue.add((short) subPort);
			//网关
			rawValue.add(0);
			rawValue.add((short)0);
			//代理服务器
			rawValue.add(0);
			rawValue.add((short)0);
			rawValue.add(new SimpleBytes(apn.trim(),16,"utf-8"));
			
			//username
			rawValue.add(new SimpleBytes("",32,"utf-8"));
			//passwd
			rawValue.add(new SimpleBytes("",32,"utf-8"));
		}else{
			rawValue.add((byte) mainIp1);
			rawValue.add((byte) mainIp2);
			rawValue.add((byte) mainIp3);
			rawValue.add((byte) mainIp4);
			rawValue.add((short) mainPort);
			rawValue.add((byte) subIp1);
			rawValue.add((byte) subIp2);
			rawValue.add((byte) subIp3);
			rawValue.add((byte) subIp4);
			rawValue.add((short) subPort);
			rawValue.add(new SimpleBytes(apn.trim(),16,"utf-8"));
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

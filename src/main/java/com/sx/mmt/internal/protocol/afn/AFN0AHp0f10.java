package com.sx.mmt.internal.protocol.afn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.SimpleBytes;

/**
 * 测量点查询
 * @author peter
 *
 */
public class AFN0AHp0f10 extends DataBody{
	public static final String DeviceIndex="deviceIndex";
	public static final String MeasuringPointIndex="measuringPointIndex";
	public static final String Speed="speed";
	public static final String Port="port";
	public static final String Protocol="protocol";
	public static final String Address="address";
	public static final String Password="password";
	public static final String TariffNumber="tariffNumber";
	public static final String NumberLength1="numberLength1";
	public static final String NumberLength2="numberLength2";
	public static final String TerminalAddress="terminalAddress";
	public static final String UserNumber="userNumber";
	private List<Map<String,Object>> measuringPointDataList;
	private int queryNumber;
	private static Logger logger = LoggerFactory.getLogger(AFN0AHp0f3.class);
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		if(protocolType.equals(Head.PROTOCOL_GB05)){
			queryNumber=rawValue.poll(1).toInt();
			measuringPointDataList=new ArrayList<Map<String,Object>>();
			for(int i=0;i<queryNumber;i++){
				Map<String,Object> config=new HashMap<String,Object>();
				config.put(DeviceIndex, rawValue.poll(1).toInt());
				config.put(MeasuringPointIndex, rawValue.poll(1).toInt());
				int[] SpeedAndPort=rawValue.poll(1).toBits();
				config.put(Speed, new SimpleBytes(new int[]{SpeedAndPort[5],SpeedAndPort[6],SpeedAndPort[7],0,0,0,0,0}).toInt());
				config.put(Port, new SimpleBytes(new int[]{SpeedAndPort[0],SpeedAndPort[1],SpeedAndPort[2],SpeedAndPort[3],SpeedAndPort[4],0,0,0}).toInt());
				config.put(Protocol, rawValue.poll(1).toInt());
				config.put(Address, rawValue.poll(6).toHexString(""));
				config.put(Password, rawValue.poll(6).toLong());
				int[] last=rawValue.poll(1).toBits();
				config.put(NumberLength1, new SimpleBytes(new int[]{last[0],last[1],0,0,0,0,0,0}).toInt());
				config.put(NumberLength2, new SimpleBytes(new int[]{last[2],last[3],0,0,0,0,0,0}).toInt());
				config.put(TariffNumber, new SimpleBytes(new int[]{last[4],last[5],last[6],last[7],0,0,0,0}).toInt());
				measuringPointDataList.add(config);
			}
		}else{
			queryNumber=rawValue.poll(2).toInt();
			measuringPointDataList=new ArrayList<Map<String,Object>>();
			for(int i=0;i<queryNumber;i++){
				Map<String,Object> config=new HashMap<String,Object>();
				config.put(DeviceIndex, rawValue.poll(2).toInt());
				config.put(MeasuringPointIndex, rawValue.poll(2).toInt());
				int[] SpeedAndPort=rawValue.poll(1).toBits();
				config.put(Speed, new SimpleBytes(new int[]{SpeedAndPort[5],SpeedAndPort[6],SpeedAndPort[7],0,0,0,0,0}).toInt());
				config.put(Port, new SimpleBytes(new int[]{SpeedAndPort[0],SpeedAndPort[1],SpeedAndPort[2],SpeedAndPort[3],SpeedAndPort[4],0,0,0}).toInt());
				config.put(Protocol, rawValue.poll(1).toInt());
				config.put(Address, rawValue.poll(6).toHexString(""));
				config.put(Password, rawValue.poll(6).toLong());
				int[] tariff=rawValue.poll(1).toBits();
				config.put(TariffNumber, new SimpleBytes(new int[]{tariff[0],tariff[1],
						tariff[2],tariff[3],tariff[4],tariff[5],0,0}).toInt());
				int[] Number=rawValue.poll(1).toBits();
				config.put(NumberLength1, new SimpleBytes(new int[]{Number[0],Number[1],0,0,0,0,0,0}).toInt());
				config.put(NumberLength2, new SimpleBytes(new int[]{Number[2],Number[3],0,0,0,0,0,0}).toInt());
				config.put(TerminalAddress, rawValue.poll(6).toHexString(""));
				config.put(UserNumber, rawValue.poll(1).toHexString(""));
				measuringPointDataList.add(config);
			}
		}
		
		return this;
	}
	@Override
	public PacketSegment encode(String protocolType,String protocolArea){
		rawValue.clear();
		rawValue.add((short)queryNumber);
		for(int i=1;i<=queryNumber;i++){
			rawValue.add((short)i);
		}
		return this;
	}
	
	@Override
	public void clear() {
		measuringPointDataList=null;
		rawValue.clear();
	}
	
	public List<Map<String, Object>> getMeasuringPointDataList() {
		return measuringPointDataList;
	}
	public void setMeasuringPointDataList(
			List<Map<String, Object>> measuringPointDataList) {
		this.measuringPointDataList = measuringPointDataList;
	}
	public int getQueryNumber() {
		return queryNumber;
	}
	public void setQueryNumber(int queryNumber) {
		this.queryNumber = queryNumber;
	};
	
	
}

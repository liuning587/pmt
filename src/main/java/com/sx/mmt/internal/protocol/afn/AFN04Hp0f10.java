package com.sx.mmt.internal.protocol.afn;

import java.util.List;
import java.util.Map;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.util.SimpleBytes;

/**
 * 设置测量点参数
 * @author peter
 *
 */
public class AFN04Hp0f10 extends DataBody{
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
	@Override
	public PacketSegment encode(String protocolType,String protocolArea){
		rawValue.clear();
		if(measuringPointDataList==null) return this;
		int  queryNumber=measuringPointDataList.size();
		if(protocolType.equals(Head.PROTOCOL_GB05)){
			rawValue.add((byte)queryNumber);
			for(int i=0;i<queryNumber;i++){
				Map<String,Object> config=measuringPointDataList.get(i);
				rawValue.add(((Integer)config.get(DeviceIndex)).byteValue());
				rawValue.add(((Integer)config.get(MeasuringPointIndex)).byteValue());
				byte SpeedAndPort=(byte)(((Integer)config.get(Speed)<<5)+(Integer)config.get(Port));
				
				rawValue.add(SpeedAndPort);
				rawValue.add(((Integer)config.get(Protocol)).byteValue());
				rawValue.add(new SimpleBytes((String)config.get(Address),16));
				rawValue.add(new SimpleBytes((long)config.get(Password)).getSubByteArray(0, 6));
				byte number=(byte)(((Integer)config.get(TariffNumber)<<4)
						+((Integer)config.get(NumberLength2)<<2)+(Integer)config.get(NumberLength1));
				rawValue.add(number);
			}
		}else{
			rawValue.add((short)queryNumber);
			for(int i=0;i<queryNumber;i++){
				Map<String,Object> config=measuringPointDataList.get(i);
				rawValue.add(((Integer)config.get(DeviceIndex)).shortValue());
				rawValue.add(((Integer)config.get(MeasuringPointIndex)).shortValue());
				byte SpeedAndPort=(byte)(((Integer)config.get(Speed)<<5)+(Integer)config.get(Port));
				rawValue.add(SpeedAndPort);
				rawValue.add(((Integer)config.get(Protocol)).byteValue());
				rawValue.add(new SimpleBytes((String)config.get(Address),16));
				rawValue.add(new SimpleBytes((Long)config.get(Password)).getSubByteArray(0, 6));
				byte tariff=((Integer)config.get(TariffNumber)).byteValue();
				rawValue.add(tariff);
				byte number=(byte) (((Integer)config.get(NumberLength2)<<2)+(Integer)config.get(NumberLength1));
				rawValue.add(number);
				if(config.get(TerminalAddress)==null){
					rawValue.add(new byte[6]);
				}else{
					rawValue.add(
						new SimpleBytes((String)config.get(TerminalAddress),16));
				}
				if(config.get(UserNumber)==null){
					rawValue.add(new byte[1]);
				}else{
					rawValue.add(
							new SimpleBytes((String)config.get(UserNumber),16));
				}
			}
		}
		return this;
	}
	public List<Map<String, Object>> getMeasuringPointDataList() {
		return measuringPointDataList;
	}
	public void setMeasuringPointDataList(
			List<Map<String, Object>> measuringPointDataList) {
		this.measuringPointDataList = measuringPointDataList;
	}
	
	
	
}

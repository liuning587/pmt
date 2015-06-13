package com.sx.mmt.internal.protocol.afn;

import java.util.List;
import java.util.Map;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.util.SimpleBytes;

public class AFN04Hp0f97 extends DataBody{
	private List<Map<String,Object>> measuringPointDataList;
	
	@Override
	public PacketSegment encode(String protocolType,String protocolArea){
		rawValue.clear();
		if(measuringPointDataList==null) return this;
		int  queryNumber=measuringPointDataList.size();
		rawValue.add((byte)1);//设置有效，采集测量点
		rawValue.add((byte)0);//间隔0分钟
		rawValue.add((byte)5);//限制5分钟
		rawValue.add((short)queryNumber);
		for(int i=0;i<queryNumber;i++){
			Map<String,Object> config=measuringPointDataList.get(i);
			short measuringPointNumber= ((Integer)config.get(AFN04Hp0f10.MeasuringPointIndex)).shortValue();
			rawValue.add(measuringPointNumber);
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

package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;

public class AFN0CHp0f2 extends DataBody{
	
	private String terminalDate;
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		int second=rawValue.getAt(0).toBCD();
		int minute=rawValue.getAt(1).toBCD();
		int hour=rawValue.getAt(2).toBCD();
		int day=rawValue.getAt(3).toBCD();
		int month=rawValue.getAt(4).getSubBitsValue(0, 5).toBCD();
		int week=rawValue.getAt(4).getSubBitsValue(5, 8).toBCD();
		int year=rawValue.getAt(5).toBCD();
			
		terminalDate=String.format("%02d-%02d-%02d %02d:%02d:%02d", year,
				month,day,hour,minute,second);
		return this;
	}
	public String getTerminalDate() {
		return terminalDate;
	}
	public void setTerminalDate(String terminalDate) {
		this.terminalDate = terminalDate;
	}

	
}

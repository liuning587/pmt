package com.sx.mmt.internal.protocol.afn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.util.SimpleBytes;

public class AFN0FHp0f1 extends DataBody{
	public static final String NAME=AFN0FH.FileTransfer;
	private static Logger logger = LoggerFactory.getLogger(AFN0FHp0f1.class);
	private int segmentIndex;
	private int segmentLength;
	private int fileTotalSegment;
	private SimpleBytes dataSection;
	@Override
	public PacketSegment encode(String protocolType,String protocolArea){
		rawValue.clear();
		rawValue.add(new byte[3]);
		rawValue.add((short)fileTotalSegment);
		rawValue.add(segmentIndex);
		rawValue.add((short)segmentLength);
		rawValue.add(dataSection);
		return this;
	}
	@Override
	public void clear() {
		rawValue.clear();
		segmentIndex=0;
		segmentLength=0;
		fileTotalSegment=0;
		dataSection=null;
	}
	public int getSegmentIndex() {
		return segmentIndex;
	}
	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}
	public int getSegmentLength() {
		return segmentLength;
	}
	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}
	public int getFileTotalSegment() {
		return fileTotalSegment;
	}
	public void setFileTotalSegment(int fileTotalSegment) {
		this.fileTotalSegment = fileTotalSegment;
	}
	public SimpleBytes getDataSection() {
		return dataSection;
	}
	public void setDataSection(SimpleBytes dataSection) {
		this.dataSection = dataSection;
	}
	
	
}

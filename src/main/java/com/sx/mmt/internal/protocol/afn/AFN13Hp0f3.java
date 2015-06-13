package com.sx.mmt.internal.protocol.afn;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.util.SimpleBytes;
/**
 * 数据内容	数据格式	字节数
第i段标识（i=0~n）	BIN	2
第i段数据长度Lf	BIN	2
文件数据	BIN	Lf

第i段标识域由2字节组成：
用D15位表示报文接收情况查询位，当D15位置“1”，表示主站问询终端当前报文接收情况，
终端应上报报文接收情况帧（上行报文F3），应答主站；置“0”，表示终端不需要应答主站。
第i段标识由D0-D14组成，采用BIN编码，范围为1-32767。


数据内容	数据格式	字节数
总段数m	BIN	2
段序号PS	BSm	n
说明：
1、如果 (m%8)等于0，n=m/8； 否则n=m/8+1。
2、D0-Dm按顺序对位表示一个升级文件的分段序号0-m。置“0”表示终端没有收到此段号报文，置“1”表示终端收到此段号报文。
 * @author 王瑜甲
 *
 */
public final class AFN13Hp0f3 extends DataBody{
	public static final String NAME=AFN13H.SEND_FILE_DATA;
	private static Logger logger = LoggerFactory.getLogger(AFN13Hp0f3.class);
	private int segmentIndex;
	private int segmentLength;
	private boolean isResponse=false;
	private int fileTotalSegment;
	private List<Integer> notReceiveSegment;
	private SimpleBytes dataSection;
	@Override
	public PacketSegment encode(String protocolType,String protocolArea){
		rawValue.clear();
		rawValue.add((byte) segmentIndex);
		rawValue.add((byte)(((segmentIndex>>>8) & (0x7f))+(isResponse?1<<7:0)));
		rawValue.add((byte) segmentLength);
		rawValue.add((byte) (segmentLength>>>8));
		rawValue.add(dataSection);
		return this;
	}
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		if(rawValue.getLength()<8){
			return this;
		}
		try{
			notReceiveSegment=new ArrayList<Integer>();
			fileTotalSegment=rawValue.poll(2).toInt();
			rawValue.poll(2);
			StringBuilder list=new StringBuilder();
			for(int i=0;i<rawValue.getLength();i++){
				list.append(StringUtils.reverse(rawValue.getAt(i).toBinString("")));
			}
			int size=list.length();
			for(int i=0;i<fileTotalSegment;i++){
				//if(i<size){
					if(list.charAt(i)=='0'){
						notReceiveSegment.add(i);
					}
				//}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("IndexOutOfBounds");
		}
		return this;
	}
	
	@Override
	public void clear() {
		rawValue.clear();
		segmentIndex=0;
		segmentLength=0;
		isResponse=false;
		fileTotalSegment=0;
		notReceiveSegment=null;
		dataSection=null;
	};
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return segmentLength+2+2;
	}
	public int getSegmentIndex() {
		return segmentIndex;
	}
	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}

	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}

	public int getSegmentLength(){
		return segmentLength;
	}
	public SimpleBytes getDataSection() {
		return dataSection;
	}
	public void setDataSection(SimpleBytes dataSection) {
		this.dataSection = dataSection;
	}
	public int getFileTotalSegment() {
		return fileTotalSegment;
	}
	public void setFileTotalSegment(int fileTotalSegment) {
		this.fileTotalSegment = fileTotalSegment;
	}
	public List<Integer> getNotReceiveSegment() {
		return notReceiveSegment;
	}
	public void setNotReceiveSegment(List<Integer> notReceiveSegment) {
		this.notReceiveSegment = notReceiveSegment;
	}
	public void setIsResponse(boolean isResponse) {
		this.isResponse = isResponse;
	}
	public boolean getIsResponse() {
		return isResponse;
	}
	
	

	
}

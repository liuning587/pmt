package com.sx.mmt.internal.protocol.afn;


/**
 * ID	文件类型
0	主文件
1	规约库文件
2	参数文件
3	字库文件asc12
4	字库文件asc16
5	字库文件hzk12
6	字库文件hzk16

数据内容	数据格式	字节数
当前升级文件名（ascii）	ASCII	25
升级文件类型（见文件类型表）	BIN	1
当前升级文件版本号	ASCII	4
当前升级文件md5码	BIN	16
当前升级原文件大小	BIN	4
当前升级文件压缩后大小	BIN	4
是否压缩	BIN	0：否；1：是
升级完成是否重新启动	BIN	1：是，0：否
延时重新启动时间	BIN	单位为秒，1字节
总段数n	BIN	2
每段报文传输数据长度	BIN	2
 */

import com.sx.mmt.exception.InvalidPacketException;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.util.SimpleBytes;

public final class AFN13Hp0f1 extends DataBody{
	public static final String NAME=AFN13H.START_UPDATE;
	public static final int SEGMENT_LENGTH=61;
	

	private String fileName;
	private int fileType;
	private String fileVersion;
	private String fileMD5;
	private int fileLength=0;
	private int fileCompressedLength=0;
	private boolean isCompressed=true;
	private boolean isReboot=true;
	private int rebootDelayTime=5;
	private int fileTotalSegment=0;
	private int fileSegmentDataLength=0;


	@Override
	public PacketSegment encode(String protocolType,String protocolArea) throws InvalidPacketException {
		rawValue.clear();
		rawValue.add(new SimpleBytes(fileName,25,"utf-8"));
		rawValue.add((byte)fileType);
		rawValue.add(new SimpleBytes(fileLength));
		rawValue.add(new SimpleBytes(fileCompressedLength));
		rawValue.add(new SimpleBytes(isCompressed));
		rawValue.add(new SimpleBytes(fileVersion,4,"utf-8"));
		rawValue.add(new SimpleBytes(fileMD5,16,true));
		rawValue.add(new SimpleBytes(isReboot));
		rawValue.add((byte) rebootDelayTime);
		rawValue.add(new SimpleBytes((short)fileTotalSegment));
		rawValue.add(new SimpleBytes((short)fileSegmentDataLength));
		return this;
	}
	
	@Override
	public void clear() {
		rawValue.clear();
		fileName=null;
		fileType=0;
		fileVersion=null;
		fileMD5=null;
		fileLength=0;
		fileCompressedLength=0;
		isCompressed=true;
		isReboot=true;
		rebootDelayTime=5;
		fileTotalSegment=0;
		fileSegmentDataLength=0;
	};
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return SEGMENT_LENGTH;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public String getFileMD5() {
		return fileMD5;
	}
	public void setFileMD5(String fileMD5) {
		this.fileMD5 = fileMD5;
	}
	public boolean getIsCompressed() {
		return isCompressed;
	}
	public void setIsCompressed(boolean isCompressed) {
		this.isCompressed = isCompressed;
	}
	public boolean getIsReboot() {
		return isReboot;
	}
	public void setIsReboot(boolean isReboot) {
		this.isReboot = isReboot;
	}
	public int getRebootDelayTime() {
		return rebootDelayTime;
	}
	public void setRebootDelayTime(int rebootDelayTime) {
		this.rebootDelayTime = rebootDelayTime;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public int getFileLength() {
		return fileLength;
	}

	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}

	public int getFileCompressedLength() {
		return fileCompressedLength;
	}

	public void setFileCompressedLength(int fileCompressedLength) {
		this.fileCompressedLength = fileCompressedLength;
	}

	public int getFileTotalSegment() {
		return fileTotalSegment;
	}

	public void setFileTotalSegment(int fileTotalSegment) {
		this.fileTotalSegment = fileTotalSegment;
	}

	public int getFileSegmentDataLength() {
		return fileSegmentDataLength;
	}

	public void setFileSegmentDataLength(int fileSegmentDataLength) {
		this.fileSegmentDataLength = fileSegmentDataLength;
	}

	
	
	
}

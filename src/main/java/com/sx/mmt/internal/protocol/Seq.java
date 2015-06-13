package com.sx.mmt.internal.protocol;


import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;
/**
 * 帧序列域SEQ.
 * 由于受报文长度限制，数据无法在一帧内传输，需要分成多帧传输
 * 
 * @author 王瑜甲
 *
 */
public class Seq extends PacketSegmentBase{
	public static final String NAME="Seq";
	
	/**
	 * //帧时间标签有效位TpV
	 */
	private boolean IsHaveTimeTag=false;
	/**
	 * //首帧标志FIR
	 */
	private boolean IsFirstFrame=true;
	/**
	 * //末帧标志FIN
	 */
	private boolean IsLastFrame=true;
	/**
	 * //请求确认标志位CON
	 */
	private boolean IsNeedConfirm=false;
	/**
	 * 启动帧序号PSEQ/响应帧序号RSEQ
	 *  a）	启动帧序号PSEQ
	 * PSEQ取自1字节的启动帧计数器PFC的低4位计数值0～15。
	 * b）	启动帧帧序号计数器PFC
	 * 每一对启动站和从动站之间均有1个独立的、由1字节构成的计数范围为0～255的启动帧帧序号计数器PFC，
	 * 用于记录当前启动帧的序号。启动站每发送1帧报文，该计数器加1，从0～255循环加1递增；重发帧则不加1。
	 * c）	响应帧序号RSEQ
	 * 响应帧序号RSEQ以启动报文中的PSEQ作为第一个响应帧序号，后续响应帧序号在RSEQ的基础上循环加1递增，数值范围为0～15。
	 * d）	帧序号改变规则
	 * 1）	启动站发送报文后，当一个期待的响应在超时规定的时间内没有被收到，如果允许启动站重发，
	 * 则该重发的启动帧序号PSEQ不变。重发次数可设置，最多3次；重发次数为0，则不允许重发。
	 * 2）	当TpV=0时，如果从动站连续收到两个具有相同启动帧序号PSEQ的启动报文，通常意味着报文的响应未被对方站收到。
	 * 在这种情况下，则重发响应（不必重新处理该报文）。
	 * 3 ）	当TpV=0时，如果启动站连续收到两个具有相同响应帧序号RSEQ的响应帧，则不处理第二个响应。
	 * 4）终端在开始响应第二个请求之前，必须将前一个请求处理结束。终端不能同时处理多个请求。
	 */
	private int serialNo; 
	
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		int[] i=rawValue.toBits();
		serialNo= (new SimpleBytes(new int[]{i[0],i[1],i[2],i[3], 0,0,0,0})).toInt();
		IsNeedConfirm=(i[4]==1);
		IsLastFrame  =(i[5]==1);
		IsFirstFrame =(i[6]==1);
		IsHaveTimeTag=(i[7]==1);
		return this;
	}
	@Override
	public PacketSegment encode(String protocolType,String protocolArea) {
		rawValue.clear();
		SimpleBytes b1=new SimpleBytes(new int[]{0,0,0,0,IsNeedConfirm?1:0,IsLastFrame?1:0,IsFirstFrame?1:0,IsHaveTimeTag?1:0});
		SimpleBytes b2=new SimpleBytes(serialNo);
		byte b=(byte) (b1.toByte()+b2.toByte());
		rawValue.add(b);
		return this;
	}
	
	@Override
	public void clear(){
		IsHaveTimeTag=false;
		IsFirstFrame=true;
		IsLastFrame=true;
		IsNeedConfirm=false;
		serialNo=0;
		rawValue.clear();
	}
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 1;
	}
	public boolean getIsHaveTimeTag() {
		return IsHaveTimeTag;
	}
	public void setIsHaveTimeTag(boolean isHaveTimeTag) {
		IsHaveTimeTag = isHaveTimeTag;
	}
	public boolean getIsFirstFrame() {
		return IsFirstFrame;
	}
	public void setIsFirstFrame(boolean isFirstFrame) {
		IsFirstFrame = isFirstFrame;
	}
	public boolean getIsLastFrame() {
		return IsLastFrame;
	}
	public void setIsLastFrame(boolean isLastFrame) {
		IsLastFrame = isLastFrame;
	}
	public boolean getIsNeedConfirm() {
		return IsNeedConfirm;
	}
	public void setIsNeedConfirm(boolean isNeedConfirm) {
		IsNeedConfirm = isNeedConfirm;
	}
	public int getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}
	@Override
	public boolean check() {
		return true;
	}
	
	

}

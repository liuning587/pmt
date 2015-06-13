package com.sx.mmt.internal.protocolBreakers;

import com.sx.mmt.internal.util.SimpleBytes;

public final class EncodedPacket {
	private String TaskId;
	private String terminalAddress;
	private SimpleBytes packet;
	private String function;
	private String pnfn;
	private boolean needResponse=true;
	private Object  additionalInfo;
	public EncodedPacket(){}
	public EncodedPacket(String terminalAddress,SimpleBytes packet,String function,String pnfn){
		this.terminalAddress=terminalAddress;
		this.packet=packet;
		this.function=function;
		this.pnfn=pnfn;
	}
	public String getTerminalAddress() {
		return terminalAddress;
	}
	public void setTerminalAddress(String terminalAddress) {
		this.terminalAddress = terminalAddress;
	}
	public SimpleBytes getPacket() {
		return packet;
	}
	public void setPacket(SimpleBytes packet) {
		this.packet = packet;
	}
	
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getPnfn() {
		return pnfn;
	}
	public void setPnfn(String pnfn) {
		this.pnfn = pnfn;
	}
	public boolean isNeedResponse() {
		return needResponse;
	}
	public void setNeedResponse(boolean needResponse) {
		this.needResponse = needResponse;
	}
	
	public Object getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(Object additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	public String getTaskId() {
		return TaskId;
	}
	public void setTaskId(String taskId) {
		TaskId = taskId;
	}
	@Override
	public String toString(){
		return String.format("%s:%s", terminalAddress,packet.toString());
	}
	
}

package com.sx.mmt.internal.task.command;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.DataPacketBuilder;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ControlField;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocol.afn.AFN04Hp0f10;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBDecodedPacketFactory;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TerminalParameterWriteCommand extends SinglePacketCommand<TerminalParameterWriteCommand>{
	public void sendOrder(String from, String to, String event, DecodedPacket context) throws Exception {
		DecodedPacket decodedPacket=GBDecodedPacketFactory.getDefaultGBSingleFrame();
		task.setTerminalAddressAndSeq(decodedPacket);
		decodedPacket.put(ProtocolAttribute.AUX_IS_USE_PW, true);
		decodedPacket.put(ProtocolAttribute.CONTROLFIELD_FUNCTION_CODE, ControlField.PRM1_REQUEST_LEVEL_ONE_DATA);
		decodedPacket.put(ProtocolAttribute.AFN_FUNCTION,Afn.AFN_SET_PARAMETER);
		decodedPacket.put(ProtocolAttribute.SEQ_IS_NEED_CONFIRM, true);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 10);
		decodedPacket.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0);
		if(StringUtils.isBlank(task.getAdditionalParam1())){
			task.updateActionNow("无参数可下发");
			return;
		}

		Gson gson = new Gson();
		List<config> measuringPointDataList=gson.fromJson(task.getAdditionalParam1(), new TypeToken<List<config>>(){}.getType());
		List<Map<String,Object>> measuringPointDataList1=new ArrayList<Map<String,Object>>();
		for(config c:measuringPointDataList){
			Map<String,Object> con=new HashMap<String, Object>();
			con.put(AFN04Hp0f10.DeviceIndex, c.getDeviceIndex());
			con.put(AFN04Hp0f10.MeasuringPointIndex, c.getMeasuringPointIndex());
			con.put(AFN04Hp0f10.Speed, c.getSpeed());
			con.put(AFN04Hp0f10.Port, c.getPort());
			con.put(AFN04Hp0f10.Protocol, c.getProtocol());
			con.put(AFN04Hp0f10.Address, c.getAddress());
			con.put(AFN04Hp0f10.Password, c.getPassword());
			con.put(AFN04Hp0f10.TariffNumber, c.getTariffNumber());
			con.put(AFN04Hp0f10.NumberLength1, c.getNumberLength1());
			con.put(AFN04Hp0f10.NumberLength2, c.getNumberLength2());
			con.put(AFN04Hp0f10.TerminalAddress, c.getTerminalAddress());
			con.put(AFN04Hp0f10.UserNumber, c.getUserNumber());
			measuringPointDataList1.add(con);
		}
		
		
		decodedPacket.put(ProtocolAttribute.AFN04_0AH_MEASURING_POINT_DATALIST, measuringPointDataList1);
		
		GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
				SpringBeanUtil.getBean("gBProtocolBreakerPool");
		DataPacketBuilder dataPacketBuilder=pbPool.getDataPacketBuilder();
		EncodedPacket encodedPacket= dataPacketBuilder
				.build(decodedPacket,task.getTaskConfig().getProtocolArea());
		pbPool.returnObject(dataPacketBuilder);
		encodedPacket.setTaskId(task.getId());
		EncodedDataSendingDelayQueue sendQueue=
				(EncodedDataSendingDelayQueue) SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		sendQueue.addPacket(encodedPacket);
		task.updateActionNow("发送设置参数报文");
		task.setDeadlineTime();
	}
	
	public void finish(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("设置参数成功");
		task.setNextActionTime(System.currentTimeMillis());
	}
	
	public void orderDeny(String from, String to, String event, DecodedPacket context) throws Exception {
		task.updateActionNow("设置参数失败");
		task.setNextActionTime(System.currentTimeMillis());
		
	}
	
	public class config{
		public int deviceIndex;
		public int measuringPointIndex;
		public int speed;
		public int port;
		public int protocol;
		public String address;
		public long password;
		public int tariffNumber;
		public int numberLength1;
		public int numberLength2;
		public String terminalAddress;
		public String userNumber;
		public int getDeviceIndex() {
			return deviceIndex;
		}
		public void setDeviceIndex(int deviceIndex) {
			this.deviceIndex = deviceIndex;
		}
		public int getMeasuringPointIndex() {
			return measuringPointIndex;
		}
		public void setMeasuringPointIndex(int measuringPointIndex) {
			this.measuringPointIndex = measuringPointIndex;
		}
		public int getSpeed() {
			return speed;
		}
		public void setSpeed(int speed) {
			this.speed = speed;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public int getProtocol() {
			return protocol;
		}
		public void setProtocol(int protocol) {
			this.protocol = protocol;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public long getPassword() {
			return password;
		}
		public void setPassword(long password) {
			this.password = password;
		}
		public int getTariffNumber() {
			return tariffNumber;
		}
		public void setTariffNumber(int tariffNumber) {
			this.tariffNumber = tariffNumber;
		}
		public int getNumberLength1() {
			return numberLength1;
		}
		public void setNumberLength1(int numberLength1) {
			this.numberLength1 = numberLength1;
		}
		public int getNumberLength2() {
			return numberLength2;
		}
		public void setNumberLength2(int numberLength2) {
			this.numberLength2 = numberLength2;
		}
		public String getTerminalAddress() {
			return terminalAddress;
		}
		public void setTerminalAddress(String terminalAddress) {
			this.terminalAddress = terminalAddress;
		}
		public String getUserNumber() {
			return userNumber;
		}
		public void setUserNumber(String userNumber) {
			this.userNumber = userNumber;
		}
		@Override
		public String toString() {
			return "config [deviceIndex=" + deviceIndex
					+ ", measuringPointIndex=" + measuringPointIndex
					+ ", speed=" + speed + ", port=" + port + ", protocol="
					+ protocol + ", address=" + address + ", password="
					+ password + ", tariffNumber=" + tariffNumber
					+ ", numberLength1=" + numberLength1 + ", numberLength2="
					+ numberLength2 + ", terminalAddress=" + terminalAddress
					+ ", userNumber=" + userNumber + "]";
		}
		
	}
}

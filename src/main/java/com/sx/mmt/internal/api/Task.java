package com.sx.mmt.internal.api;

import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.task.TaskConfig;


public interface Task {
	String getTerminalAddress();
	long getNextActionTime();
	void setNextActionTime(long nextActionTime);
	void setDeadlineTime();
	void startHandle();
	void messageComeHandle(DecodedPacket packet);
	void timeUpHandle();
	void finishHandle();
	String getTaskGroupTag();
	void setTaskGroupTag(String taskGroupTag);
	void setTerminalAddressAndSeq(DecodedPacket decodedPacket);
	String getActionNow();
	void setActionNow(String actionNow);
	void updateActionNow(String actionNow);
	String getTerminalReturn();
	void appendTerminalReturn(String terminalReturn);
	void setTerminalReturn(String terminalReturn);
	TaskConfig getTaskConfig();
	void setNewTask(String taskName);
	void setErrorCode(DecodedPacket context);
	int getPacketIndex();
	int getAndMovePacketIndex();
	int getRetryCount();
	String getAdditionalParam1();
	void setAdditionalParam1(String additionalParam1);
	int getSerialNoFromPfc();
	void setPacketIndex(int packetIndex);
	int getPfc();
	void setPfc(int pfc);
	String getId();
	String getTaskStatus();
	void setTaskStatus(String taskStatus);
	void TerminalOnHandle();
}

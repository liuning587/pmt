package com.sx.mmt.internal.api;

import java.util.Map;
import java.util.Set;

import org.jdom2.Document;

import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.task.TaskImpl;

public interface TaskManager {
	void receivePacket(EncodedPacket encodedPacket);
	void startTaskManager();
	void stopTaskManager();
	void iniTaskStatus();
	void removeAllFromCache(String groupTag);
	TaskImpl removeFromCache(String terminalAddress);
	void updateCacheNotice();
	void clearCache();
	void receiveCqPacket(Document doc);
	void receiveSdPacket(Document doc);
	Map<String, String> getOnlineList();
	Map<String, TaskImpl> getTaskcache();
	Set<String> getOnlineEvent();
}

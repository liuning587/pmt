package com.sx.mmt.internal.api;

import java.util.Map;

public interface Command {
	void setAttr(Map<String, String> attr);
	void setTask(Task task);
}

package com.sx.mmt.internal.task;

import java.util.List;

public class TaskConfig {
	private String name;
	private String protocolArea;
	private String protocolType;
	private List<String> commands;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProtocolArea() {
		return protocolArea;
	}
	public void setProtocolArea(String protocolArea) {
		this.protocolArea = protocolArea;
	}
	public String getProtocolType() {
		return protocolType;
	}
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
	public List<String> getCommands() {
		return commands;
	}
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}
	
}

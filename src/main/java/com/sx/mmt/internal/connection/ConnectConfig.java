package com.sx.mmt.internal.connection;

import java.util.Map;

public class ConnectConfig {
	private String name;
	private String clazz;
	private boolean isUse;
	private Map<String,String> attr;
	public ConnectConfig(){}
	public ConnectConfig(String name,String clazz,boolean isUse){
		this.name=name;
		this.clazz=clazz;
		this.isUse=isUse;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public boolean isUse() {
		return isUse;
	}
	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	public Map<String, String> getAttr() {
		return attr;
	}
	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}
	@Override
	public String toString() {
		return name;
	}
	
	
	
}



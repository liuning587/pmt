package com.sx.mmt.internal.task.command;

import java.util.Map;
import java.util.Map.Entry;

public final class CommandConfig {
	private String name;
	private String clazz;
	private boolean isUse;
	private Map<String,String> attr;
	public CommandConfig(){}
	public CommandConfig(String name,String clazz,boolean isUse){
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
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("name=").append(name).append("\n")
		.append("class=").append(clazz).append("\n")
		.append("isUse=").append(isUse).append("\n");
		if(attr!=null){
			sb.append("attr=");
			for(Entry<String,String> e:attr.entrySet()){
				sb.append(e.getKey()).append(":").append(e.getValue()).append(";");
			}
		}
		return sb.toString();
	}
	
}

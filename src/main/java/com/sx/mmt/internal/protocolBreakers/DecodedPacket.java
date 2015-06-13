package com.sx.mmt.internal.protocolBreakers;

import java.util.HashMap;
import java.util.Map;

public final class DecodedPacket {
	private Map<String,Object> attributes=new HashMap<String,Object>();

	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public DecodedPacket put(String attribute,Object value){
		attributes.put(attribute, value);
		return this;
	}
	
	public Object get(String attribute){
		return attributes.get(attribute);
	}
}

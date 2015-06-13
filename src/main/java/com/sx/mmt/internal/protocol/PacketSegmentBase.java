package com.sx.mmt.internal.protocol;

import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;

public abstract class PacketSegmentBase implements PacketSegment{
	protected SimpleBytes rawValue=new SimpleBytes();

	
	@Override
	public SimpleBytes getRawValue() {
		return rawValue;
	}
	@Override
	public PacketSegment setRawValue(SimpleBytes rawValue) {
		this.rawValue = rawValue;
		return this;
	}	
}

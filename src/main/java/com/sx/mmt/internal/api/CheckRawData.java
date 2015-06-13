package com.sx.mmt.internal.api;

import com.sx.mmt.exception.InvalidPacketException;



public interface CheckRawData {
	boolean check() throws InvalidPacketException;
}

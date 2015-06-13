package com.sx.mmt.internal.protocol.afn;

import com.sx.mmt.internal.protocol.DataBody;

public class AFN00Hp0f4 extends DataBody{
	public static final String NAME=AFN00H.DATA_CHECK_ERROR;
	private boolean isConfirm=false;

	public boolean getIsConfirm() {
		return isConfirm;
	}
}

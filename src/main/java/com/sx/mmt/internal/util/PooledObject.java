package com.sx.mmt.internal.util;

public class PooledObject {
	private Object objection=null;
	private boolean busy=false;
	public PooledObject(Object objection) {
		this.objection = objection;
	}

	public Object getObjection() {
		return objection;
	}

	public void setObjection(Object objection) {
		this.objection = objection;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean isbusy) {
		this.busy = isbusy;
	}
	
	
}

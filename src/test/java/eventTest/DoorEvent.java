package eventTest;

import java.util.EventObject;

public class DoorEvent extends EventObject{
	

	private static final long serialVersionUID = 1L;
	private String doorState="";
	public DoorEvent(Object source,String doorState) {
		super(source);
		this.doorState=doorState;
	}
	public String getDoorState() {
		return doorState;
	}
	public void setDoorState(String doorState) {
		this.doorState = doorState;
	}

	
	
}

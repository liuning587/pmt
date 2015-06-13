package eventTest;

import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;

public class DoorManager {
	private Collection<EventListener> listeners; 
	public void addDoorListener(DoorListener listener) {  
        if (listeners == null) {  
            listeners = new HashSet<EventListener>();  
        }  
        listeners.add(listener);  
    }
	
	public void removeDoorListener(DoorListener listener) {  
        if (listeners == null)  
            return;  
        listeners.remove(listener);  
    }
	
	protected void fireWorkspaceOpened() {  
        if (listeners == null)  
            return;  
        DoorEvent event = new DoorEvent(this, "open");  
        notifyListeners(event);  
    }
	
	protected void fireWorkspaceClosed() {  
        if (listeners == null)  
            return;  
        DoorEvent event = new DoorEvent(this, "close");  
        notifyListeners(event);  
    } 
	
	private void notifyListeners(DoorEvent event) {  
        Iterator<EventListener> iter = listeners.iterator();  
        while (iter.hasNext()) {  
            DoorListener listener = (DoorListener) iter.next();  
            listener.doorEvent(event);  
        }  
    }  
	
}

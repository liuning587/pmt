package statemachineTest;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import statemachineTest.ATMStateMachine.ATMState;

public class ATMStateMachine extends AbstractStateMachine<ATMStateMachine,ATMState,String,String> implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -8173650666620776728L;
	private static Logger logger = LoggerFactory.getLogger(ATMStateMachine.class); 
    void postConstruct() {
        System.out.println("ATMStateMachine PostConstruct Touched!");
    }
    
    public enum ATMState {
        Start, Loading, OutOfService, Disconnected, InService
    }
    
    public void transitFromStartToLoadingOnConnectedWhenCon(ATMState from, ATMState to, String event,String context) {
        addOptionalDot();
        //this.fire("LoadSuccess");
 
    }
    
    public void transitFromStartToStartOnConnected(ATMState from, ATMState to, String event,String context) {
        System.out.println(context);
        logger.debug("StartToStart");
    }
    
    public void entryLoading(ATMState from, ATMState to, String event) {
        addOptionalDot();
        
    }
    
    public void exitLoading(ATMState from, ATMState to, String event) {
        addOptionalDot();
        //System.out.println("machine:exitLoading");
    }
    
    public void transitFromLoadingToInServiceOnLoadSuccess(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromLoadingToInServiceOnLoadSuccess");
        System.out.println("machine:InService");
    }
    
    public void transitFromLoadingToOutOfServiceOnLoadFail(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromLoadingToOutOfServiceOnLoadFail");
    }
    
    public void transitFromLoadingToDisconnectedOnConnectionClosed(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromLoadingToDisconnectedOnConnectionClosed");
    }
    
    public void entryOutOfService(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("entryOutOfService");
    }
    
    public void transitFromOutOfServiceToDisconnectedOnConnectionLost(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromOutOfServiceToDisconnectedOnConnectionLost");
    }
    
    public void transitFromOutOfServiceToInServiceOnStartup(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromOutOfServiceToInServiceOnStartup");
    }
    
    public void exitOutOfService(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("exitOutOfService");
    }
    
    public void entryDisconnected(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("entryDisconnected");
    }
    
    public void transitFromDisconnectedToInServiceOnConnectionRestored(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromDisconnectedToInServiceOnConnectionRestored");
    }
    
    public void exitDisconnected(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("exitDisconnected");
    }
    
    public void entryInService(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("entryInService");
    }
    
    public void transitFromInServiceToOutOfServiceOnShutdown(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromInServiceToOutOfServiceOnShutdown");
    }
    
    public void transitFromInServiceToDisconnectedOnConnectionLost(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("transitFromInServiceToDisconnectedOnConnectionLost");
    }
    
    public void exitInService(ATMState from, ATMState to, String event) {
        addOptionalDot();
        logger.debug("exitInService");
    }
    
    private void addOptionalDot() {
        return;
    }
    

}
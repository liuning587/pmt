package statemachineTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.squirrelframework.foundation.fsm.Condition;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import statemachineTest.ATMStateMachine.ATMState;




public class Main {
	
	public static void main(String[] args) throws Exception{
		AbstractStateMachine<ATMStateMachine,ATMState,String,String> stateMachine;
	    StateMachineBuilder<ATMStateMachine,ATMState,String,String> builder = 
	    		StateMachineBuilderFactory.create(
	            ATMStateMachine.class, ATMState.class, String.class, String.class);
	    Condition<String> c=new Condition<String>(){

			@Override
			public boolean isSatisfied(String context) {
				if("1".equals(context)){
					return true;
				}
				return false;
			}

			@Override
			public String name() {
				// TODO Auto-generated method stub
				return "Con";
			}
	    	
	    };

	    
	    builder.externalTransition().from(ATMState.Start).to(ATMState.Loading).on("Connected").when(c);
	    builder.internalTransition().within(ATMState.Start).on("Connected");
	    builder.externalTransition().from(ATMState.Loading).to(ATMState.Disconnected).on("ConnectionClosed");
	    builder.externalTransition().from(ATMState.Loading).to(ATMState.InService).on("LoadSuccess");
	    builder.externalTransition().from(ATMState.Loading).to(ATMState.OutOfService).on("LoadFail");
	    builder.externalTransition().from(ATMState.OutOfService).to(ATMState.Disconnected).on("ConnectionLost");
	    builder.externalTransition().from(ATMState.OutOfService).to(ATMState.InService).on("Startup");
	    builder.externalTransition().from(ATMState.InService).to(ATMState.OutOfService).on("Shutdown");
	    builder.externalTransition().from(ATMState.InService).to(ATMState.Disconnected).on("ConnectionLost");
	    builder.externalTransition().from(ATMState.Disconnected).to(ATMState.InService).on("ConnectionRestored");
	    stateMachine =  (ATMStateMachine) builder.newStateMachine(ATMState.Start) ;

	    stateMachine.start();
	    stateMachine.dumpSavedData();
	    stateMachine.fire("Connected","1");
	    
	    System.out.println(stateMachine.getCurrentState());

	    //System.out.println();
//        SCXMLVisitor visitor = SquirrelProvider.getInstance().newInstance(SCXMLVisitor.class);
//        stateMachine.accept(visitor);
//        visitor.convertSCXMLFile("ATMStateMachine", true);
	}
}

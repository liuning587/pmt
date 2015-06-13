package com.sx.mmt.testTerminal;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.connection.filter.FrontModelLog;
import com.sx.mmt.internal.util.SimpleBytes;

public class TestTerminalLog extends IoFilterAdapter{
	private static Logger logger = LoggerFactory.getLogger(FrontModelLog.class); 
	public void messageReceived(NextFilter nextFilter, IoSession session,  
            Object message) throws Exception {
		if(message instanceof SimpleBytes){
			String terminalAddress="";
			String mess=((SimpleBytes) message).toReverseHexString("");
			if(mess.length()>100){
				mess=mess.substring(0, 100)+" ...";
			}
			String recordMessage=String.format("up   %s %s",terminalAddress,mess );
			logger.info(recordMessage);

			
		}
        nextFilter.messageReceived(session, message);  
    }
	
    public void messageSent(NextFilter nextFilter, IoSession session,  
            WriteRequest writeRequest) throws Exception { 
    	if(writeRequest.getMessage() instanceof SimpleBytes){
	    	SimpleBytes message=(SimpleBytes)writeRequest.getMessage();
	    	String terminalAddress="";
			String mess=((SimpleBytes) message).toReverseHexString("");
			if(mess.length()>100){
				mess=mess.substring(0, 100)+" ...";
			}
	    	String recordMessage=String.format("down %s %s",terminalAddress, mess);
			logger.info(recordMessage);

			
		}
        nextFilter.messageSent(session, writeRequest);  
    } 
}

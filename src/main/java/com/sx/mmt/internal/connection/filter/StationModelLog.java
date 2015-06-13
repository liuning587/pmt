package com.sx.mmt.internal.connection.filter;

import java.util.Date;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.protocolBreakers.GBProtocolBreakerPool;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.ActionNowDisplay;

public class StationModelLog extends IoFilterAdapter{
	private static Logger logger = LoggerFactory.getLogger(StationModelLog.class);
	private static Boolean islog=null;
	public void messageReceived(NextFilter nextFilter, IoSession session,  
            Object message) throws Exception {
		if(message instanceof SimpleBytes){
			GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
					SpringBeanUtil.getBean("gBProtocolBreakerPool");
			DataPacketParser dataPacketParser=pbPool.getDataPacketParser();
			String terminalAddress=dataPacketParser.getTerminalAddress((SimpleBytes) message);
			pbPool.returnObject(dataPacketParser);
			String mess=((SimpleBytes) message).toReverseHexString("");
			String recordMessage=String.format("up   %s %s",terminalAddress,mess );
			if(islog==null){
				islog=PropertiesUtil.parseToBoolean(ConfigConstants.IsLog);
			}
			
			if(islog){
	    		logger.info(recordMessage);
	    	}
			logger.info(recordMessage);
			ActionNowDisplay.appendTerminalShow(
					String.format("%s %s", DateTool.getTimeString(new Date()),recordMessage));
			
		}
        nextFilter.messageReceived(session, message); 
    }
	
    public void messageSent(NextFilter nextFilter, IoSession session,  
            WriteRequest writeRequest) throws Exception {
    	if(writeRequest.getMessage() instanceof SimpleBytes){
	    	SimpleBytes message=(SimpleBytes)writeRequest.getMessage();
			GBProtocolBreakerPool pbPool=(GBProtocolBreakerPool) 
					SpringBeanUtil.getBean("gBProtocolBreakerPool");
			DataPacketParser dataPacketParser=pbPool.getDataPacketParser();
			String terminalAddress=dataPacketParser.getTerminalAddress((SimpleBytes) message);
			pbPool.returnObject(dataPacketParser);
			String mess=((SimpleBytes) message).toReverseHexString("");
			if(mess.length()>10000){
				mess=mess.substring(0, 10000)+" ...";
			}
	    	String recordMessage=String.format("down %s %s",terminalAddress, mess);
			if(islog==null){
				islog=PropertiesUtil.parseToBoolean(ConfigConstants.IsLog);
			}
	    	if(islog){
	    		logger.info(recordMessage);
	    	}
			ActionNowDisplay.appendTerminalShow(
					String.format("%s %s", DateTool.getTimeString(new Date()),recordMessage));
			
		}
        nextFilter.messageSent(session, writeRequest);   
    } 
}

package com.sx.mmt.internal.connection.filter;

import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ConnectionConstants;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.swingUI.ActionNowDisplay;

/**
 * 拦截记录日志
 * @author 王瑜甲
 *
 */
public class FrontModelLog extends IoFilterAdapter{
	private static Logger logger = LoggerFactory.getLogger(FrontModelLog.class);
	private static Boolean islog=null;
	public void messageReceived(NextFilter nextFilter, IoSession session,  
            Object message) throws Exception {
		if(message instanceof SimpleBytes){
			String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
			if(StringUtils.isBlank(terminalAddress)){
				InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
				terminalAddress = remoteAddress.getAddress().getHostAddress();
			}
			String mess=((SimpleBytes) message).toReverseHexString("");
			String recordMessage=String.format("up   %s %s",terminalAddress,mess );
			if(islog==null){
				islog=PropertiesUtil.parseToBoolean(ConfigConstants.IsLog);
			}
			if(islog){
	    		logger.info(recordMessage);
	    	}
			ActionNowDisplay.appendTerminalShow(
					String.format("%s %s", DateTool.getTimeString(new Date()),recordMessage));
			
		}
        nextFilter.messageReceived(session, message);  
    }
	
    public void messageSent(NextFilter nextFilter, IoSession session,  
            WriteRequest writeRequest) throws Exception { 
    	if(writeRequest.getMessage() instanceof SimpleBytes){
	    	SimpleBytes message=(SimpleBytes)writeRequest.getMessage();
	    	String terminalAddress=(String) session.getAttribute(ConnectionConstants.TerminalAddress);
			String mess=((SimpleBytes) message).toReverseHexString("");
			if(mess.length()>100){
				mess=mess.substring(0, 100)+" ...";
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

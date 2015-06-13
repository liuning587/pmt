package com.sx.mmt.internal.connection.stationModel.cqdwSocket;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.connection.stationModel.transparent.FrontMessageHandler;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.swingUI.ActionNowDisplay;

public class CQFrontMessageHandler extends IoHandlerAdapter{
	private static Logger logger = LoggerFactory.getLogger(FrontMessageHandler.class);
	private CQFrontConnectionManager frontConnectionManager;
	public void setFrontConnectionManager(
			CQFrontConnectionManager frontConnectionManager) {
		this.frontConnectionManager = frontConnectionManager;
	}
	
	public void sessionCreated(IoSession session) throws Exception {
    }

    public void sessionClosed(IoSession session) throws Exception {
    	ActionNowDisplay.appendTerminalShow("Connection with fep lost");
    	frontConnectionManager.reconnect();
    	ActionNowDisplay.appendTerminalShow("reconnect");
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    	session.close(false);
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	logger.error(ErrorTool.getErrorInfoFromException(cause));
    	ActionNowDisplay.appendTerminalShow("Connection with fep lost with exception");
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
		if(message instanceof Document){
			frontConnectionManager.processReceivePacket((Document)message);
		}
		
		
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        // Empty handler
    }
}

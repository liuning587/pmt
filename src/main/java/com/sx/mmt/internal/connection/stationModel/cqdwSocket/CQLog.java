package com.sx.mmt.internal.connection.stationModel.cqdwSocket;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.swingUI.ActionNowDisplay;

public class CQLog  extends IoFilterAdapter{
	private static Logger logger = LoggerFactory.getLogger(CQLog.class);
	public void messageReceived(NextFilter nextFilter, IoSession session,  
            Object message) throws Exception {
		if(message instanceof Document){
			Document doc=(Document) message;
			Element rootEl=doc.getRootElement();
			
			String terminalId=rootEl.getChildText(OtherConstants.terminalId);
			String replyContent=rootEl.getChildText(OtherConstants.replyContent);
			String exceptionContent=rootEl.getChildText(OtherConstants.exceptionContent);
			String frameContent=rootEl.getChildText(OtherConstants.frameContent);
			String recordMessage=String.format("up   %s %s %s %s",terminalId,
					frameContent==null?"":frameContent.replaceAll(" ", ""),
					replyContent==null?"":replyContent,
					exceptionContent==null?"":exceptionContent);
			logger.info(recordMessage);
			ActionNowDisplay.appendTerminalShow(
					String.format("%s %s", DateTool.getTimeString(new Date()),recordMessage));
			
		}
        nextFilter.messageReceived(session, message); 
    }
	
    public void messageSent(NextFilter nextFilter, IoSession session,  
            WriteRequest writeRequest) throws Exception {
    	if(writeRequest.getMessage() instanceof Document){
    		Document doc=(Document)writeRequest.getMessage();
    		Element rootEl=doc.getRootElement();
    		String terminalId=rootEl.getChildText(OtherConstants.terminalId);
    		String frameContent=rootEl.getChildText(OtherConstants.frameContent);
    		String sucFlag=rootEl.getChildText(OtherConstants.sucFlag);
    		String startfinish=null;
			if(!StringUtils.isBlank(frameContent) && frameContent.length()>100){
				frameContent=frameContent.substring(0, 100)+" ...";
			}
			if(StringUtils.isBlank(frameContent) && StringUtils.isBlank(sucFlag)){
				startfinish="申请通道";
			}else if(StringUtils.isBlank(frameContent) && !StringUtils.isBlank(sucFlag)){
				startfinish="完成升级";
			}
	    	String recordMessage=String.format("down %s %s %s",terminalId, 
	    			frameContent==null?"":frameContent,
	    			startfinish==null?"":startfinish );
			logger.info(recordMessage);
			ActionNowDisplay.appendTerminalShow(
					String.format("%s %s", DateTool.getTimeString(new Date()),recordMessage));
			
		}
        nextFilter.messageSent(session, writeRequest);   
    } 
}

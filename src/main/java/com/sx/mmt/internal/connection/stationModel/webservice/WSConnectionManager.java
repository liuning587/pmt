package com.sx.mmt.internal.connection.stationModel.webservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import wsc.FinishRtuUpdate;
//import wsc.FinishRtuUpdateResponse;
//import wsc.SendSourceFrameByRtuId;
//import wsc.SendSourceFrameByRtuIdResponse;
//import wsc.ServiceStub;
//import wsc.StartRtuUpdate;
//import wsc.StartRtuUpdateResponse;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.ConnectService;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.ActionNowDisplay;
/*
public class WSConnectionManager implements ConnectService{
	private static Logger logger = LoggerFactory.getLogger(WSConnectionManager.class);
	private static final String charset="gb2312";
	private static XMLOutputter out;
	private static SAXBuilder builder=new SAXBuilder();
	private TaskManager taskManager;
	private EncodedDataSendingDelayQueue sendQueue;
	private ExecutorService executorPool;
	private ServiceStub stub;
	private SendingPacketRobot sendingPacketRobot;
	private ConnectConfig config;
	private UncaughtExceptionHandler handler=new myUncaughtExceptionHandler();
	
	public WSConnectionManager() throws Exception{
		taskManager=(TaskManager)SpringBeanUtil.getBean("taskManager");
		sendQueue=
				(EncodedDataSendingDelayQueue)SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		executorPool=Executors.newSingleThreadExecutor();
		
		
	}
	
	private class myUncaughtExceptionHandler implements UncaughtExceptionHandler{
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error(t.getName()+"线程失效");
			logger.error(ErrorTool.getErrorInfoFromException(e));
			Thread SendingPacketThread=new Thread(sendingPacketRobot);
			SendingPacketThread.setName("SendingPacketThread");
			SendingPacketThread.setUncaughtExceptionHandler(handler);
			SendingPacketThread.start();
			logger.error(t.getName()+"线程恢复");
		}
	}
	@Override
	public void startServer(ConnectConfig config) throws Exception{
		this.config=config;
		stub=new ServiceStub(config.getAttr().get(ConfigConstants.Wsdl));
		sendingPacketRobot=new SendingPacketRobot();
		Thread SendingPacketThread=new Thread(sendingPacketRobot);
		SendingPacketThread.setName("SendingPacketThread");
		SendingPacketThread.setUncaughtExceptionHandler(handler);
		SendingPacketThread.start();
	}
	@Override
	public void stopServer(){
		if(sendingPacketRobot!=null){
			sendingPacketRobot.stopRobot();
		}
		stub=null;
	}
	@Override
	public boolean testConnection(ConnectConfig config) throws Exception {
		try
        {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(config.getAttr().get(ConfigConstants.Wsdl));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
                return true;
			}else{
				return false;
			}
			
        }catch (Exception e)
        {
            return false;
        }
	}
	
	public void processReceivePacket(String response){
		if(StringUtils.isBlank(response)) return;
		try{
			InputStream in=new ByteArrayInputStream(response.getBytes(charset));
			Document doc=builder.build(in);
			logReceive(doc);
			taskManager.receiveCqPacket(doc);
		}catch(Exception e){
			ErrorTool.getErrorInfoFromException(e);
		}
	}
	
	private XMLOutputter getXMLOutputter(){
		if(out==null){
			Format format = Format.getCompactFormat();
			format.setEncoding(charset);
			format.setLineSeparator("");
			out=new XMLOutputter(format);
			return out;
		}else{
			return out;
		}
	}
	
	private String getStringFromDoc(Document doc){
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		try {
			getXMLOutputter().output(doc, os);
			return new String(os.toByteArray(),charset);
		} catch (IOException e) {
			ErrorTool.getErrorInfoFromException(e);
		}finally{
			try {
				os.close();
			} catch (IOException e) {
				ErrorTool.getErrorInfoFromException(e);
			}
		}
		return "";
	}
	
	private void logReceive(Document message){
		Document doc=message;
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
	
	private void logSend(Document message){
		Document doc=message;
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
	
	public String sendStart(Document doc){
		StartRtuUpdate startRtuUpdate=new StartRtuUpdate();
		String message=getStringFromDoc(doc);
		startRtuUpdate.setStrCommand(message);
		try{
			StartRtuUpdateResponse StartRtuUpdateResponse=
					stub.startRtuUpdate(startRtuUpdate);
			String responseString=StartRtuUpdateResponse.get_return();
			return responseString;
		}catch(Exception e){
			ErrorTool.getErrorInfoFromException(e);
		}
		return "";
	}
	
	public String sendFinish(Document doc){
		FinishRtuUpdate finishRtuUpdate=new FinishRtuUpdate();
		String message=getStringFromDoc(doc);
		finishRtuUpdate.setStrCommand(message);
		try{
			FinishRtuUpdateResponse finishRtuUpdateResponse=
					stub.finishRtuUpdate(finishRtuUpdate);
			String responseString=finishRtuUpdateResponse.get_return();
			return responseString;
		}catch(Exception e){
			ErrorTool.getErrorInfoFromException(e);
		}
		return "";
	}
	
	public String sendPacket(Document doc){
		SendSourceFrameByRtuId sendSourceFrameByRtuId=
				new SendSourceFrameByRtuId();
		String message=getStringFromDoc(doc);
		sendSourceFrameByRtuId.setStrCommand(message);
		try{
			SendSourceFrameByRtuIdResponse sendSourceFrameByRtuIdResponse=
					stub.sendSourceFrameByRtuId(sendSourceFrameByRtuId);
			String responseString=sendSourceFrameByRtuIdResponse.get_return();
			return responseString;
		}catch(Exception e){
			ErrorTool.getErrorInfoFromException(e);
		}
		return "";
	}
	
	private class SendingPacketRobot implements Runnable{
		private volatile boolean stopRequested;
		private Thread runThread;
		@Override
		public void run() {
			stopRequested = false;
			runThread = Thread.currentThread();
			while(!stopRequested){
				EncodedPacket packet=null;
				try {
					packet = sendQueue.getPacket();
				} catch (InterruptedException e) {
				}
				if(packet.getPacket()==null && packet.getAdditionalInfo()!=null){
					Document doc=(Document)packet.getAdditionalInfo();
					logSend(doc);
					Element rootEl=doc.getRootElement();
					if(rootEl.getChildText(OtherConstants.sucFlag)==null){
						executorPool.execute(new Runnable() {
							@Override
							public void run() {
								String response=sendStart(doc);
								processReceivePacket(response);
							}
						});
					}else{
						executorPool.execute(new Runnable() {
							@Override
							public void run() {
								String response=sendFinish(doc);
								processReceivePacket(response);
							}
						});
					}
				}else{
					Document doc=new Document();
					Element rootEl=new Element(OtherConstants.para);
					doc.setRootElement(rootEl);
					rootEl.addContent(new Element(OtherConstants.terminalId)
						.setText(packet.getTaskId()));
					rootEl.addContent(new Element(OtherConstants.recordNo)
						.setText(config.getAttr().get(ConfigConstants.ChannelNo)));
					rootEl.addContent(new Element(OtherConstants.frameContent)
								.setText(packet.getPacket().toReverseHexString("")));
					rootEl.addContent(new Element(OtherConstants.timeOut)
							.setText("50000"));
					rootEl.addContent(new Element(OtherConstants.needResponse)
								.setText(packet.isNeedResponse()?"1":"0"));
					logSend(doc);
					executorPool.execute(new Runnable() {
						@Override
						public void run() {
							String response=sendPacket(doc);
							processReceivePacket(response);
						}
					});
				}
			}		
		}
		
		public void stopRobot(){
			stopRequested=true;
	        if ( runThread != null ) {
	            runThread.interrupt();
	        }
		}
	}      
}

*/

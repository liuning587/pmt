package com.sx.mmt.internal.connection.stationModel.cqdwWebservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import com.dareway.webservice.yzxf.YzxfDelegatorCQCallbackHandler;
import com.dareway.webservice.yzxf.YzxfDelegatorCQStub;
import com.dareway.yzxf.xsd.FinishRtuUpdate;
import com.dareway.yzxf.xsd.FinishRtuUpdateResponse;
import com.dareway.yzxf.xsd.SendSourceFrameByRtuId;
import com.dareway.yzxf.xsd.SendSourceFrameByRtuIdResponse;
import com.dareway.yzxf.xsd.StartRtuUpdate;
import com.dareway.yzxf.xsd.StartRtuUpdateResponse;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.OtherConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.ConnectService;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.ActionNowDisplay;

public class CQDWConnectionManager implements ConnectService{
	private static Logger logger = LoggerFactory.getLogger(CQDWConnectionManager.class);
	private static final String charset="gb2312";
	private static XMLOutputter out;
	private static SAXBuilder builder=new SAXBuilder();
	private TaskManager taskManager;
	private EncodedDataSendingDelayQueue sendQueue;
	private CQStubPool stubpool;
	private Map<YzxfDelegatorCQStub,CallbackHandler> callbackPool;
	private SendingPacketRobot sendingPacketRobot;
	private ConnectConfig config;
	private static Boolean islog=null;
	private UncaughtExceptionHandler handler=new myUncaughtExceptionHandler();
	
	public CQDWConnectionManager() throws Exception{
		taskManager=(TaskManager)SpringBeanUtil.getBean("taskManager");
		sendQueue=
				(EncodedDataSendingDelayQueue)SpringBeanUtil.getBean("encodedDataSendingDelayQueue");
		callbackPool=new ConcurrentHashMap<YzxfDelegatorCQStub, CallbackHandler>();
		
		
	}
	
	private class myUncaughtExceptionHandler implements UncaughtExceptionHandler{
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error(t.getName()+"线程失效");
			logger.error(ErrorTool.getErrorInfoFromException(e));
			Thread SendingPacketThread=new Thread(sendingPacketRobot);
			SendingPacketThread.setName("SendingPacketThread");
			SendingPacketThread.setUncaughtExceptionHandler(handler);
			SendingPacketThread.setDaemon(true);
			SendingPacketThread.start();
			logger.error(t.getName()+"线程恢复");
		}
	}
	@Override
	public void startServer(ConnectConfig config) throws Exception{
		this.config=config;
		stubpool=new CQStubPool(config.getAttr().get(ConfigConstants.Wsdl));
		stubpool.createPool();
		sendingPacketRobot=new SendingPacketRobot();
		Thread SendingPacketThread=new Thread(sendingPacketRobot);
		SendingPacketThread.setName("SendingPacketThread");
		SendingPacketThread.setUncaughtExceptionHandler(handler);
		SendingPacketThread.setDaemon(true);
		SendingPacketThread.start();
	}
	@Override
	public void stopServer(){
		if(sendingPacketRobot!=null){
			sendingPacketRobot.stopRobot();
		}
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
		if(islog==null){
			islog=PropertiesUtil.parseToBoolean(ConfigConstants.IsLog);
		}
		if(islog){
			logger.info(recordMessage);
    	}
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
		if(!StringUtils.isBlank(frameContent) && frameContent.length()>300){
			frameContent=frameContent.substring(0, 300)+" ...";
		}
		if(StringUtils.isBlank(frameContent) && StringUtils.isBlank(sucFlag)){
			startfinish="申请通道";
		}else if(StringUtils.isBlank(frameContent) && !StringUtils.isBlank(sucFlag)){
			startfinish="完成升级";
		}
    	String recordMessage=String.format("down %s %s %s",terminalId, 
    			frameContent==null?"":frameContent,
    			startfinish==null?"":startfinish );
		if(islog==null){
			islog=PropertiesUtil.parseToBoolean(ConfigConstants.IsLog);
		}
		if(islog){
			logger.info(recordMessage);
    	}
		ActionNowDisplay.appendTerminalShow(
				String.format("%s %s", DateTool.getTimeString(new Date()),recordMessage));	
	}
	
	public class CallbackHandler extends YzxfDelegatorCQCallbackHandler{
		YzxfDelegatorCQStub stub;
		
		public void setStub(YzxfDelegatorCQStub stub) {
			this.stub = stub;
		}

		@Override
		public void receiveResultstartRtuUpdate(StartRtuUpdateResponse startRtuUpdateResponse){
			String responseString=startRtuUpdateResponse.get_return();
			processReceivePacket(responseString);
			stubpool.returnObject(stub);
		}
		
		@Override
		public void receiveResultfinishRtuUpdate(FinishRtuUpdateResponse finishRtuUpdateResponse){
			String responseString=finishRtuUpdateResponse.get_return();
			processReceivePacket(responseString);
			stubpool.returnObject(stub);
		}
		
		@Override
		public void receiveResultsendSourceFrameByRtuId(SendSourceFrameByRtuIdResponse sendSourceFrameByRtuIdResponse){
			String responseString=sendSourceFrameByRtuIdResponse.get_return();
			processReceivePacket(responseString);
			stubpool.returnObject(stub);
		}
		
		@Override
		public void receiveErrorstartRtuUpdate(Exception e) {
			stubpool.returnObject(stub);
        }
		
		@Override
		public void receiveErrorfinishRtuUpdate(Exception e) {
			stubpool.returnObject(stub);
        }
		
		@Override
		public void receiveErrorsendSourceFrameByRtuId(Exception e) {
			stubpool.returnObject(stub);
			
        }
	}
	
	public void sendStart(Document doc){
		try{
			YzxfDelegatorCQStub stub=stubpool.getStub();
			StartRtuUpdate startRtuUpdate=new StartRtuUpdate();
			String message=getStringFromDoc(doc);
			startRtuUpdate.setSourceXml(message);
			CallbackHandler callback=callbackPool.get(stub);
			if(callback==null){
				callback=new CallbackHandler();
				callback.setStub(stub);
				callbackPool.put(stub, callback);
			}
			stub.startstartRtuUpdate(startRtuUpdate, callback);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	public void sendFinish(Document doc){
		try{
			YzxfDelegatorCQStub stub=stubpool.getStub();
			FinishRtuUpdate finishRtuUpdate=new FinishRtuUpdate();
			String message=getStringFromDoc(doc);
			finishRtuUpdate.setSourceXml(message);
			CallbackHandler callback=callbackPool.get(stub);
			if(callback==null){
				callback=new CallbackHandler();
				callback.setStub(stub);
				callbackPool.put(stub, callback);
			}
			stub.startfinishRtuUpdate(finishRtuUpdate, callback);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	public void sendPacket(Document doc){
		try{
			YzxfDelegatorCQStub stub=stubpool.getStub();
			SendSourceFrameByRtuId sendSourceFrameByRtuId=
					new SendSourceFrameByRtuId();
			String message=getStringFromDoc(doc);
			sendSourceFrameByRtuId.setSourceXml(message);
			CallbackHandler callback=callbackPool.get(stub);
			if(callback==null){
				callback=new CallbackHandler();
				callback.setStub(stub);
				callbackPool.put(stub, callback);
			}
			stub.startsendSourceFrameByRtuId(sendSourceFrameByRtuId, callback);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	private class SendingPacketRobot implements Runnable{
		private volatile boolean stopRequested;
		private Thread runThread;
		private long lastsendTime=0L;
		private final long sendInterval=10;
		@Override
		public void run() {
			runThread = Thread.currentThread();
			stopRequested = false;
			while(!stopRequested){
				long currTime=System.currentTimeMillis();
				if(currTime<lastsendTime+sendInterval){
					try {
						Thread.sleep(sendInterval);
					} catch (InterruptedException e) {
					}
					continue;
				}
				EncodedPacket packet=null;
				try {
					packet = sendQueue.getPacket();
				} catch (InterruptedException e) {
				}
				if(packet.getPacket()==null && packet.getAdditionalInfo()!=null){
					Document doc=(Document)packet.getAdditionalInfo();
					Element rootEl=doc.getRootElement();
					logSend(doc);
					if(rootEl.getChildText(OtherConstants.sucFlag)==null){
						sendStart(doc);
					}else{
						sendFinish(doc);
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
								.setText(packet.getPacket().toReverseHexString(" ")));
					rootEl.addContent(new Element(OtherConstants.timeOut)
							.setText("50000"));
					rootEl.addContent(new Element(OtherConstants.needResponse)
								.setText(packet.isNeedResponse()?"1":"0"));
					logSend(doc);
					sendPacket(doc);
					lastsendTime=System.currentTimeMillis();
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

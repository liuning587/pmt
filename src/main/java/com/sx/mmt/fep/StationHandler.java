package com.sx.mmt.fep;

import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.FepConstants;
import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;

public class StationHandler extends IoHandlerAdapter{

	private Controller controller;
	
	
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	private static Logger logger = LoggerFactory.getLogger(StationHandler.class);
	@Override   
	public void messageReceived(IoSession session, Object message)  throws Exception{
		Address address=new Address(((SimpleBytes)message )
				.getSubByteArray(0, 1));//Address.INDEX_BEGIN, Address.INDEX_END
		address.decode("","");
		Integer msa=address.getMsa();
		String terminalId=address.getDistrict()+address.getTerminalAddress();
		if(session.getAttribute(FepConstants.MSA_ID)==null){
			session.setAttribute(FepConstants.MSA_ID,msa);
		}
		
		//注册主站session
		IoSession local=controller.getOpenedStationSesssion().get(session.getId());
		if(local!=null){
			controller.getStationSessionMapping().remove(msa);
			controller.getStationSessionMapping().put(msa, session);
			controller.getOpenedStationSesssion().remove(session.getId());
		}
		//发送来自主站的数据到终端
		IoSession terminalSession=controller.getTerminalSessionMapping().get(terminalId);
		if(terminalSession!=null){
			terminalSession.write(message);
		}else{
			logger.info(String.format("terminal %s is offline.packet %s is discard", terminalId,message));
		}
		
	}
	
	@Override
    public void sessionCreated(IoSession session) {
       InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
       String clientIp = remoteAddress.getAddress().getHostAddress();  
       logger.info(String.format("%s receive from station : %s connection. Session Id 为  %s", 
    		   DateTool.getDateString(new Date()),clientIp,session.getId()));
       controller.getOpenedStationSesssion().put(session.getId(), session);
    }
	
	@Override
	public void exceptionCaught(final IoSession session, final Throwable error) {
		logger.error(String.format("id  %s session exception！%s",session.getId(),
				ErrorTool.getErrorInfoFromException(error)));
		Integer msa=(Integer) session.getAttribute(FepConstants.MSA_ID);
		if(msa!=null){
			controller.getStationSessionMapping().remove(msa);
		}
		controller.getOpenedStationSesssion().remove(session.getId());
	}
	
	@Override
	public void sessionClosed(final IoSession session) {
	    InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
	    String clientIp = remoteAddress.getAddress().getHostAddress();
		logger.info(String.format("id  %s  session ,connection with station  %s is closed",session.getId(),clientIp));
		Integer msa=(Integer) session.getAttribute(FepConstants.MSA_ID);
		if(msa!=null){
			controller.getStationSessionMapping().remove(msa);
		}
		controller.getOpenedStationSesssion().remove(session.getId());
	}
	
	@Override
	public void sessionIdle(final IoSession session, final IdleStatus status) {
		session.close(false);
	}
}

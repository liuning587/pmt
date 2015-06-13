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
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.DataUnitIdentify;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SimpleBytes;

public class TerminalHandler extends IoHandlerAdapter{
	
	private static Logger logger = LoggerFactory.getLogger(TerminalHandler.class);
	
	private Controller controller;
	
	
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	@Override   
	public void messageReceived(IoSession session, Object message)  throws Exception{
		Address address=new Address(((SimpleBytes)message )
				.getSubByteArray(0, 1));//Address.INDEX_BEGIN, Address.INDEX_END
		address.decode("","");
		Integer msa=address.getMsa();
		String terminalId=address.getDistrict()+address.getTerminalAddress();
		if(session.getAttribute(FepConstants.TERMINAL_ID)==null){
			session.setAttribute(FepConstants.TERMINAL_ID,terminalId);
		}
		ProcessPacket(session,msa,(SimpleBytes)message);
	}
	
	private void ProcessPacket(IoSession session,int msa,SimpleBytes packet){
		IoSession stationSession=controller.getStationSessionMapping().get(msa);
		Afn afn=new Afn(packet.getSubByteArray(0, 1));//Afn.INDEX_BEGIN, Afn.INDEX_END
		afn.decode("","");
		if(afn.getFunction().equals(Afn.AFN_LINK_INTERFACE_CHECK)){
			DataUnitIdentify du=new DataUnitIdentify(packet
					.getSubByteArray(0,1));//DataUnitIdentify.INDEX_BEGIN,DataUnitIdentify.INDEX_END
			du.decode("","");
			if(du.getFn()==1){
				ProcessLogin(session,packet);
			}else if(du.getFn()==2){
				ProcessLogout(session,packet);
			}else if(du.getFn()==3){
				ProcessHeartBeat(session,packet);
			}else{
				logger.error(String.format("unsupport afn fnpn combination.afn=%s,fn=%s,pn=%s",
						afn.getRawValue().toInt(),du.getFn(),du.getPn()));
			}
		}else{
			if(stationSession!=null){
				stationSession.write(packet);
			}else{
				logger.info(String.format("station %s is offline . packet %s is discard", 
						msa,packet));
			}
		}
	}
	
	private void ProcessHeartBeat(IoSession session,SimpleBytes packet){
		logger.info(String.format("%s reveive terminal address  %s heartBeat",
				DateTool.getDateString(new Date()),session.getAttribute(FepConstants.TERMINAL_ID)));
		SimpleBytes heartBeatReply=FepResponse.getComfirmPacket(packet, 3);
		session.write(heartBeatReply);
	}
	
	private void ProcessLogin(IoSession session,SimpleBytes packet){
		String terminalId=(String) session.getAttribute(FepConstants.TERMINAL_ID);
		logger.info(String.format("%s receive terminal address  %s login",
						DateTool.getDateString(new Date()),terminalId));
		IoSession reg=controller.getOpenedTerminalSession().get(session.getId());
		if(reg!=null){
			controller.getTerminalSessionMapping().remove(terminalId);
			controller.getTerminalSessionMapping().put(terminalId, reg);
			controller.getOpenedTerminalSession().remove(session.getId());
		}
		SimpleBytes loginReply=FepResponse.getComfirmPacket(packet, 1);
		session.getWriteRequestQueue().clear(session);
		session.write(loginReply);
	}
	
	private void ProcessLogout(IoSession session,SimpleBytes packet){
		String terminalId=(String) session.getAttribute(FepConstants.TERMINAL_ID);
		logger.info(String.format("%s receive from terminal  %s logout",
						DateTool.getDateString(new Date()),terminalId));
		SimpleBytes logoutReply=FepResponse.getComfirmPacket(packet, 2);
		session.write(logoutReply);
		controller.getTerminalSessionMapping().remove(terminalId);
		session.close(false);
	}
	
	@Override
    public void sessionCreated(IoSession session) {
       InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
       String clientIp = remoteAddress.getAddress().getHostAddress();  
       logger.info(String.format("%s receive terminal : %s connection. Session Id 为 %s", 
    		   DateTool.getDateString(new Date()),clientIp,session.getId()));

       controller.getOpenedTerminalSession().put(session.getId(), session);

    }
	
	@Override
	public void exceptionCaught(final IoSession session, final Throwable error) {
		logger.error(String.format("id  %s   session connection with terminal exception! %s", session.getId(),
				ErrorTool.getErrorInfoFromException(error)));
		String terminalId=(String) session.getAttribute(FepConstants.TERMINAL_ID);
		if(terminalId!=null){
			controller.getTerminalSessionMapping().remove(terminalId);
		}
		controller.getOpenedTerminalSession().remove(session.getId());
	}
	
	@Override
	public void sessionClosed(final IoSession session) {
	    InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
	    String clientIp = remoteAddress.getAddress().getHostAddress();
		logger.info(String.format("id  %s  session connection with terminal  %s closed",session.getId(),clientIp));
		String terminalId=(String) session.getAttribute(FepConstants.TERMINAL_ID);
		if(terminalId!=null){
			controller.getTerminalSessionMapping().remove(terminalId);
		}
		controller.getOpenedTerminalSession().remove(session.getId());
	}
	
	@Override
	public void sessionIdle(final IoSession session, final IdleStatus status) {
		session.close(false);
	}
}

package com.sx.mmt.fep;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

public class Controller {
	//主站session存储池
	private Map<Integer,IoSession> stationSessionMapping;
	
	//终端session存储池
	private Map<String,IoSession> terminalSessionMapping;
	
	private Map<Long,IoSession> openedTerminalSession;
	
	private Map<Long,IoSession> openedStationSesssion;

	
	private StationHandler stationHandler;
	
	private TerminalHandler terminalHandler;
	
	private StationServer stationServer;
	
	private TerminalServer terminalServer;

	public void start(int terminalPort,int stationPort){
		stationSessionMapping=new ConcurrentHashMap<Integer,IoSession>();
		terminalSessionMapping=new ConcurrentHashMap<String,IoSession>();
		openedTerminalSession=new ConcurrentHashMap<Long,IoSession>();
		openedStationSesssion=new ConcurrentHashMap<Long,IoSession>();
		stationHandler=new StationHandler();
		terminalHandler=new TerminalHandler();
		stationHandler.setController(this);
		terminalHandler.setController(this);
		stationServer=new StationServer(stationHandler);
		terminalServer=new TerminalServer(terminalHandler);
		stationServer.StartServer(stationPort);
		terminalServer.StartServer(terminalPort);
	}
	




	

	public Map<Integer, IoSession> getStationSessionMapping() {
		return stationSessionMapping;
	}







	public void setStationSessionMapping(
			Map<Integer, IoSession> stationSessionMapping) {
		this.stationSessionMapping = stationSessionMapping;
	}







	public Map<String, IoSession> getTerminalSessionMapping() {
		return terminalSessionMapping;
	}







	public void setTerminalSessionMapping(
			Map<String, IoSession> terminalSessionMapping) {
		this.terminalSessionMapping = terminalSessionMapping;
	}







	public StationHandler getStationHandler() {
		return stationHandler;
	}





	public void setStationHandler(StationHandler stationHandler) {
		this.stationHandler = stationHandler;
	}





	public TerminalHandler getTerminalHandler() {
		return terminalHandler;
	}





	public void setTerminalHandler(TerminalHandler terminalHandler) {
		this.terminalHandler = terminalHandler;
	}





	public StationServer getStationServer() {
		return stationServer;
	}





	public void setStationServer(StationServer stationServer) {
		this.stationServer = stationServer;
	}





	public TerminalServer getTerminalServer() {
		return terminalServer;
	}





	public void setTerminalServer(TerminalServer terminalServer) {
		this.terminalServer = terminalServer;
	}





	public Map<Long, IoSession> getOpenedTerminalSession() {
		return openedTerminalSession;
	}

	public void setOpenedTerminalSession(Map<Long, IoSession> openedTerminalSession) {
		this.openedTerminalSession = openedTerminalSession;
	}

	public Map<Long, IoSession> getOpenedStationSesssion() {
		return openedStationSesssion;
	}

	public void setOpenedStationSesssion(Map<Long, IoSession> openedStationSesssion) {
		this.openedStationSesssion = openedStationSesssion;
	}
}

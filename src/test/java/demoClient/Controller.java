package demoClient;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.internal.util.SimpleBytes;

public class Controller {
	private ConcurrencyHandler concurrencyHandler;
	private ConcurrencyTest terminaltest;
	private StationConcurrencyTest stationTest;
	private StationConcurrencyHandler stationConcurrencyHandler;
	private Map<String,IoSession> terminal;
	private Map<String,IoSession> terminal1;
	private IoSession stationSession;
	private static Logger logger = LoggerFactory.getLogger(Controller.class);
	public void ini(){
		concurrencyHandler=new ConcurrencyHandler();
		stationConcurrencyHandler=new StationConcurrencyHandler();
		terminaltest=new ConcurrencyTest();
		stationTest=new StationConcurrencyTest();
		concurrencyHandler.setController(this);
		stationConcurrencyHandler.setController(this);
		terminal=new ConcurrentHashMap<String,IoSession>();
		terminal1=new ConcurrentHashMap<String,IoSession>();
	}
	public void startlogin(){
		terminaltest.start(concurrencyHandler);
	}
	
	public void getsession(){
		stationTest.start(stationConcurrencyHandler);
	}
	public void sendData(){
		boolean loop=true;
		while(loop){
			for(int i=0;i<10;i++){
				for(Entry<String,IoSession> entry :terminal.entrySet()){
					entry.getValue().setAttribute("data",System.currentTimeMillis());
					entry.getValue().setAttribute("isData",true);
					stationSession.
						write(IoBuffer.wrap(PacketFactory
								.getPacket(new SimpleBytes(entry.getKey(),16)).getBytesArray()));
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.info("开始发送登录请求");
			sendLogin();
			loop=true;
		}
	}
	
	public void sendLogin(){
		for(Entry<String,IoSession> entry :terminal1.entrySet()){
			entry.getValue().setAttribute("login",System.currentTimeMillis());
			entry.getValue().setAttribute("isLogin",true);
			entry.getValue().
				write(IoBuffer.wrap(PacketFactory
						.getLoginPacket(new SimpleBytes(entry.getKey(),16)).getBytesArray()));
			logger.info(String.format("address:%s,packet:%s", new SimpleBytes(entry.getKey(),16),PacketFactory
					.getLoginPacket(new SimpleBytes(entry.getKey(),16))));
		}
	}

	
	
	
	
	public ConcurrencyHandler getConcurrencyHandler() {
		return concurrencyHandler;
	}
	public void setConcurrencyHandler(ConcurrencyHandler concurrencyHandler) {
		this.concurrencyHandler = concurrencyHandler;
	}
	
	public ConcurrencyTest getTerminaltest() {
		return terminaltest;
	}
	public void setTerminaltest(ConcurrencyTest terminaltest) {
		this.terminaltest = terminaltest;
	}
	public StationConcurrencyTest getStationTest() {
		return stationTest;
	}
	public void setStationTest(StationConcurrencyTest stationTest) {
		this.stationTest = stationTest;
	}
	public Map<String, IoSession> getTerminal() {
		return terminal;
	}
	public void setTerminal(Map<String, IoSession> terminal) {
		this.terminal = terminal;
	}
	public StationConcurrencyHandler getStationConcurrencyHandler() {
		return stationConcurrencyHandler;
	}
	public void setStationConcurrencyHandler(
			StationConcurrencyHandler stationConcurrencyHandler) {
		this.stationConcurrencyHandler = stationConcurrencyHandler;
	}
	public IoSession getStationSession() {
		return stationSession;
	}
	public void setStationSession(IoSession stationSession) {
		this.stationSession = stationSession;
	}
	public Map<String, IoSession> getTerminal1() {
		return terminal1;
	}
	public void setTerminal1(Map<String, IoSession> terminal1) {
		this.terminal1 = terminal1;
	}
	
}

package com.sx.mmt.internal.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sx.mmt.internal.api.ConnectService;

@Component
public class ConnectionManager {
	private ConnectService currentService;
	@Autowired
	private ConnectXmlResolver ConnectXmlResolver;
	
	public void initConnect() throws Exception{
		for(ConnectConfig config:ConnectXmlResolver.getconnects().values()){
			if(config.isUse()){
				currentService=(ConnectService) 
						Class.forName(config.getClazz().trim()).newInstance();
				currentService.startServer(config);
				break;
			}
		}
	}

	public boolean testConnect(ConnectConfig config){
		try{
			ConnectService service=(ConnectService) 
					Class.forName(config.getClazz().trim()).newInstance();
			return service.testConnection(config);
		}catch(Exception e){
			return false;
		}
		
	}
	
	public void switchConnection(ConnectConfig config) throws Exception{
		if(currentService!=null){
			currentService.stopServer();
		}
		currentService=(ConnectService) 
				Class.forName(config.getClazz().trim()).newInstance();
		currentService.startServer(config);
	}

	public void stopCurrentService(){
		if(currentService!=null){
			try {
				currentService.stopServer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}

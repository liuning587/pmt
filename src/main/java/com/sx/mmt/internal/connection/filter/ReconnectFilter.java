package com.sx.mmt.internal.connection.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconnectFilter extends IoFilterAdapter{
	private static Logger logger = LoggerFactory.getLogger(ReconnectFilter.class); 
	//private ConnectionService taskService;
//	public ReconnectFilter(ConnectionService taskService){
//		this.taskService=taskService;
//	}
	@Override
	public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception{
		//IoConnector connector=taskService.getConnector();
		IoConnector connector=null;
		while(connector!=null){
			try{
				logger.debug("wait three seconds to reconnect");
				Thread.sleep(3000);
				ConnectFuture future = connector.connect();
				future.awaitUninterruptibly();
				IoSession session = future.getSession();
				if(session.isConnected()){  
                    logger.debug("Reconnect ["+ connector.getDefaultRemoteAddress().toString()+"] success");  
                    break;  
                } 
			}catch(Exception e){
				logger.info("Reconnect server failed. %s",e.getMessage());
			}
		}
	}
}

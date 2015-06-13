package com.sx.mmt.internal.api;

import com.sx.mmt.internal.connection.ConnectConfig;


public interface ConnectService {
	void startServer(ConnectConfig config) throws Exception;
	void stopServer() throws Exception;
	boolean testConnection(ConnectConfig config) throws Exception;
}

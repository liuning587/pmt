package com.sx.mmt.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.fep.Controller;
import com.sx.mmt.internal.util.ErrorTool;

public class Fep {

	private static Logger logger = LoggerFactory.getLogger(Fep.class); 
	
	public static void main(String[] args) {
		try{
			int terminalPort=Integer.valueOf(args[0]);
			int StationPort=Integer.valueOf(args[1]);
			System.out.println("terminalPort："+terminalPort+";StationPort："+StationPort);
			System.out.println("Waiting...");
			Controller c=new Controller();
			c.start(terminalPort,StationPort);
			System.out.println("start finished");
		}catch(Exception e){ 
			logger.error(ErrorTool.getErrorInfoFromException(e));
		}
	}	
}

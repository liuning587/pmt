package com.sx.mmt.testTerminal;


public class TestTerminal {
	public static void main(String[] args) {
		Controller c=new Controller();
		c.sendLogins();
		while(true){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}

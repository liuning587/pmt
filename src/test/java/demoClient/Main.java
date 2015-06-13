package demoClient;

public class Main {
	public static void main(String[] args) {
		Controller c=new Controller();
		c.ini();
		c.startlogin();
		c.getsession();
		while(c.getTerminal().size()<500){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		c.sendData();

		
		
	}
}

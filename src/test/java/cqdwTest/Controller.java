package cqdwTest;

import java.io.IOException;

public class Controller {
	private Connector connector;
	private Handler handler;
	public Controller(){
		handler=new Handler();
		connector=new Connector();
		connector.setHandler(handler);
		
	}
	public void start(){
		try {
			connector.StartServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Controller controller=new Controller();
		controller.start();
	}
}

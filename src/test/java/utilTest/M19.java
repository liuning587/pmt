package utilTest;

import org.apache.mina.core.session.IoSession;

import com.sx.mmt.internal.protocolBreakers.EncodedPacket;

public class M19 {
	public static void main(String[] args) throws InterruptedException {
		SendingPacketRobot sendingPacketRobot=new SendingPacketRobot();
		new Thread(sendingPacketRobot).start();
		while(true){
			Thread.sleep(2000);
			System.out.println("stop");
			sendingPacketRobot.stopRobot();
		}
	}
	

}


class SendingPacketRobot implements Runnable{
	private volatile boolean stopRequested;
	private Thread runThread;
	@Override
	public void run() {
		runThread = Thread.currentThread();
		stopRequested = false;
		int i=0;
		while(!stopRequested){
			System.out.println(i);
			i++;
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
					
	}
	
	public void stopRobot(){
		stopRequested=true;
        if ( runThread != null ) {
            runThread.interrupt();
        }
	}
}
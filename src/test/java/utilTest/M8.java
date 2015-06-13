package utilTest;

public class M8 {
	private Object lock=new Object();
	public static void main(String[] args) {
		M8 M8=new M8();
		M8.test1();
	}
	
	public void test1(){
		Thread thread=new Thread(new Runnable() {	
			@Override
			public void run() {
				synchronized (this) {
					while(true){
						System.out.println("send begin"+System.currentTimeMillis());
						for(int i=0;i<10;i++){
							System.out.println("send"+i);
						}
						try {
							System.out.println("send end"+System.currentTimeMillis());
							this.wait(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		thread.start();
	}
	
	public void test(){
		Thread thread=new Thread(new Runnable() {	
			@Override
			public void run() {
				synchronized (lock) {
					while(true){
						System.out.println("send begin"+System.currentTimeMillis());
						for(int i=0;i<10;i++){
							System.out.println("send"+i);
						}
						try {
							System.out.println("send end"+System.currentTimeMillis());
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		thread.start();
		Thread thread1=new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized(lock){
						lock.notifyAll();
					}
				}
			}
		});
		thread1.start();
	}
}

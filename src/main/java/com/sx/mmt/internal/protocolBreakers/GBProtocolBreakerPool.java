package com.sx.mmt.internal.protocolBreakers;

/**
 * 采用对象池缓存解码类
 */

import java.util.Enumeration;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.sx.mmt.internal.api.DataPacketBuilder;
import com.sx.mmt.internal.api.DataPacketParser;
import com.sx.mmt.internal.util.PooledObject;


@Component(value="gBProtocolBreakerPool")
public class GBProtocolBreakerPool {
	private static int num=25;
	private static int increassePerOnce=5;
	private static int max=10000;
	private Vector<PooledObject> pool=null;

	
	@PostConstruct
	public synchronized void createPool(){
		pool=new Vector<PooledObject>();
		for(int i=0;i<num;i++){
			DataPacketBuilder dpb=new GBDataPacketBuilder();
			DataPacketParser dpp=new GBDataPacketParser();
			pool.add(new PooledObject(dpb));
			pool.add(new PooledObject(dpp));
		}
	}
	
	public synchronized DataPacketParser getDataPacketParser(){
		if(pool==null){
			return null;
		}
		DataPacketParser apb=findFreeParser();
		if(apb==null && pool.size()<max){
			for(int i=0;i<increassePerOnce;i++){
				DataPacketBuilder dpb=new GBDataPacketBuilder();
				DataPacketParser dpp=new GBDataPacketParser();
				pool.add(new PooledObject(dpb));
				pool.add(new PooledObject(dpp));
			}
		}
		while(apb==null){
			wait(250);
			apb=findFreeParser();
		}
		return apb;
	}
	
	public synchronized DataPacketBuilder getDataPacketBuilder(){
		if(pool==null){
			return null;
		}
		DataPacketBuilder apb=findFreeBuilder();
		if(apb==null && pool.size()<max){
			for(int i=0;i<increassePerOnce;i++){
				DataPacketBuilder dpb=new GBDataPacketBuilder();
				DataPacketParser dpp=new GBDataPacketParser();
				pool.add(new PooledObject(dpb));
				pool.add(new PooledObject(dpp));
			}
		}
		while(apb==null){
			wait(250);
			apb=findFreeBuilder();
		}
		return apb;
	}
	
	private DataPacketParser findFreeParser(){     
	    
		DataPacketParser dpp=null;    
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();     
        // 遍历所有的对象，看是否有可用的对象     
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();     
            if (!pObj.isBusy() && (pObj.getObjection() instanceof DataPacketParser)) {     
            	dpp = (DataPacketParser) pObj.getObjection();     
                pObj.setBusy(true);
                break;
           }
        }
        return dpp;// 返回找到到的可用对象     
    }
	
	private DataPacketBuilder findFreeBuilder(){     
	    
		DataPacketBuilder dpb=null;    
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();     
        // 遍历所有的对象，看是否有可用的对象     
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();     
            if (!pObj.isBusy() && (pObj.getObjection() instanceof DataPacketBuilder)) {     
            	dpb = (DataPacketBuilder) pObj.getObjection();     
                pObj.setBusy(true); 
                break;
           }
        }
        return dpb;// 返回找到到的可用对象     
    }
	
	public void returnObject(Object obj) {
//		System.out.println("return begin");
//		show();
        if (pool == null) {     
            return;     
        }     
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();     
        // 遍历对象池中的所有对象，找到这个要返回的对象对象     
        while (enumerate.hasMoreElements()) {     
            pObj = (PooledObject) enumerate.nextElement();
            // 先找到对象池中的要返回的对象对象     
            if (obj == pObj.getObjection()) {      
                pObj.setBusy(false);     
                break;     
            }     
        }
//        System.out.println("return end");
//        show();
    } 
	
	public synchronized void closeObjectPool() {     
	    
        if (pool == null) {     
            return;     
        }     
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();     
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();      
            if (pObj.isBusy()) {     
                wait(5000);
            }     
            pool.removeElement(pObj);     
        }
        pool = null;     
    }
	
	private void wait(int mSeconds) {     
        try {     
            Thread.sleep(mSeconds);     
        }
       catch (InterruptedException e) {     
        }     
    }
	
	private void show(){
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();
        int i=0;
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();
            String s=String.format("%s\t%s\t%s\t%s", i,pObj,(pObj.getObjection() instanceof DataPacketBuilder)?"builder":"parser",pObj.isBusy());
            i++;
               
        }
	}
	
	
}

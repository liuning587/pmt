package com.sx.mmt.internal.connection.stationModel.cqdwWebservice;

import java.util.Enumeration;
import java.util.Vector;

import javax.annotation.PostConstruct;

import com.dareway.webservice.yzxf.YzxfDelegatorCQStub;
import com.sx.mmt.internal.util.PooledObject;

public class CQStubPool {
	private static int num=20;
	private static int increassePerOnce=5;
	private static int max=100;
	private Vector<PooledObject> pool=null;
	private String wsdl;
	
	public CQStubPool(String wsdl){
		this.wsdl=wsdl;
	}
	
	@PostConstruct
	public synchronized void createPool() throws Exception{
		pool=new Vector<PooledObject>();
		for(int i=0;i<num;i++){
			YzxfDelegatorCQStub stub=new YzxfDelegatorCQStub(wsdl);
			pool.add(new PooledObject(stub));
		}
	}
	
	public synchronized YzxfDelegatorCQStub getStub() throws Exception{
		if(pool==null){
			return null;
		}
		YzxfDelegatorCQStub stub=findStub();
		if(stub==null && pool.size()<max){
			for(int i=0;i<increassePerOnce;i++){
				YzxfDelegatorCQStub newstub=new YzxfDelegatorCQStub(wsdl);
				pool.add(new PooledObject(newstub));
			}
		}
		while(stub==null){
			wait(250);
			stub=findStub();
		}
		
		return stub;
	}
	
	
	private YzxfDelegatorCQStub findStub(){     
		YzxfDelegatorCQStub stub=null;    
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();     
        // 遍历所有的对象，看是否有可用的对象     
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();     
            if (!pObj.isBusy()) {     
            	stub = (YzxfDelegatorCQStub) pObj.getObjection();     
                pObj.setBusy(true); 
                break;
           }
        }
        return stub;// 返回找到到的可用对象     
    }
	
	public void returnObject(Object obj) {
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
} 
     
package com.sx.mmt.internal.connection.stationModel.sddwWebservice;

import java.util.Enumeration;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.apache.axis2.AxisFault;

import com.dareway.webservice.yzxf.YzxfDelegatorStub;
import com.sx.mmt.internal.util.PooledObject;

public class SDStubPool {
	private static int num=20;
	private static int increassePerOnce=5;
	private static int max=100;
	private Vector<PooledObject> pool=null;
	private String wsdl;
	
	public SDStubPool(String wsdl){
		this.wsdl=wsdl;
	}
	
	@PostConstruct
	public synchronized void createPool() throws Exception{
		pool=new Vector<PooledObject>();
		for(int i=0;i<num;i++){
			YzxfDelegatorStub stub=new YzxfDelegatorStub(wsdl);
			pool.add(new PooledObject(stub));
		}
	}
	
	public synchronized YzxfDelegatorStub getStub() throws Exception{
		if(pool==null){
			return null;
		}
		YzxfDelegatorStub stub=findStub();
		if(stub==null && pool.size()<max){
			for(int i=0;i<increassePerOnce;i++){
				YzxfDelegatorStub newstub=new YzxfDelegatorStub(wsdl);
				pool.add(new PooledObject(newstub));
			}
		}
		while(stub==null){
			wait(250);
			stub=findStub();
		}
		
		return stub;
	}
	
	private void resetPool(){
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();     
        // 遍历对象池中的所有对象，找到这个要返回的对象对象     
        while (enumerate.hasMoreElements()) {     
            pObj = (PooledObject) enumerate.nextElement();
            YzxfDelegatorStub stub = (YzxfDelegatorStub) pObj.getObjection();
            try {
				stub._getServiceClient().cleanupTransport();
			} catch (AxisFault e) {
			}
            pObj.setBusy(false);
      
        }
	}
	
	private YzxfDelegatorStub findStub(){     
		YzxfDelegatorStub stub=null;    
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();     
        // 遍历所有的对象，看是否有可用的对象     
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();     
            if (!pObj.isBusy()) {     
            	stub = (YzxfDelegatorStub) pObj.getObjection();     
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
	
	private void show(){
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();
        int i=0;
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();
            String s=String.format("%s\t%s\t\t%s", i,pObj,pObj.isBusy());
            System.out.println(s);
            i++; 
        }
	}
	
	private void sta(){
        PooledObject pObj = null;     
        Enumeration<PooledObject> enumerate = pool.elements();
        int ibusy=0;
        int ifree=0;
        while (enumerate.hasMoreElements()) {     
            pObj =enumerate.nextElement();
            if(pObj.isBusy()){
            	ibusy++;
            }else{
            	ifree++;
            }
        }
        System.out.println("busy="+ibusy+"ifree="+ifree);
	}
}
    
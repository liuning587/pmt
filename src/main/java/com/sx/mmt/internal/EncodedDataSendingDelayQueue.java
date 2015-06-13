package com.sx.mmt.internal;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;

@Component
public class EncodedDataSendingDelayQueue {
	private DelayQueue<DelayItem> queue=new DelayQueue<DelayItem>();
	private static final int maxSize=100000;

	private class DelayItem implements Delayed{
		private EncodedPacket encodedDataPacket;
		private long deadline;
		public DelayItem(EncodedPacket encodedDataPacket,long deadline){
			this.encodedDataPacket=encodedDataPacket;
			this.deadline=deadline;
		}
		@Override
		public int compareTo(Delayed other) {
			if (other == this)
				return 0;
			long timespan = getDelay(TimeUnit.MILLISECONDS)
					- other.getDelay(TimeUnit.MILLISECONDS);
			return (timespan == 0) ? 0 : ((timespan < 0) ? -1 : 1);
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(deadline - System.currentTimeMillis(),
					TimeUnit.MILLISECONDS);
		}
		public EncodedPacket getEncodedDataPacket() {
			return encodedDataPacket;
		}	
	}
	
	
	public void addPacket(EncodedPacket encodedDataPacket,Date deadline){
		if(queue.size()>maxSize){
			return;
		}
		queue.put(new DelayItem(encodedDataPacket,deadline.getTime()));

		
	}
	
	public void addPacket(EncodedPacket encodedDataPacket,int delayedMillisecondTime){
		if(queue.size()>maxSize){
			return;
		}
		queue.put(new DelayItem(encodedDataPacket,(new Date()).getTime()+delayedMillisecondTime));

	}
	
	public void addPacket(EncodedPacket encodedDataPacket,long deadline){
		if(queue.size()>maxSize){
			return;
		}
		queue.put(new DelayItem(encodedDataPacket,deadline));
	}
	
	
	public void addPacket(EncodedPacket encodedDataPacket){
		if(queue.size()>maxSize){
			return;
		}
		addPacket(encodedDataPacket,0L);
	}
	
	
	public void addPacket(List<EncodedPacket> encodedDataPackets,Date deadline,long interval){
		if(queue.size()>maxSize){
			return;
		}
		int length=encodedDataPackets.size();
		for(int i=0;i<length;i++){
			queue.put(new DelayItem(encodedDataPackets.get(i),deadline.getTime()+interval*i));
		}

	}
	
	
	public void addPacket(List<EncodedPacket> encodedDataPackets,long delayedMillisecondTime,long interval){
		if(queue.size()>maxSize){
			return;
		}
		int length=encodedDataPackets.size();
		for(int i=0;i<length;i++){
			queue.put(new DelayItem(encodedDataPackets.get(i),
					(new Date()).getTime()+delayedMillisecondTime+interval*i));
		}

	}
	
	
	public void addPacket(List<EncodedPacket> encodedDataPackets,long interval){
		if(queue.size()>maxSize){
			return;
		}
		addPacket(encodedDataPackets,0L,interval);
	}
	
	public void removePacket(String id){
		List<DelayItem> removeList=Lists.newArrayList();
		Iterator<DelayItem> it=queue.iterator();
		while(it.hasNext()){
			DelayItem item=it.next();
			if(item.getEncodedDataPacket().getTaskId().equals(id)){
				removeList.add(item);
			}
		}
		queue.removeAll(removeList);
	}

	
	
	public void clearQueue(){
		queue.clear();
	}
	
	
	
	public EncodedPacket getPacket() throws InterruptedException{
		DelayItem delayItem=null;
		delayItem = queue.take();
		if(delayItem!=null){
			return delayItem.getEncodedDataPacket();
		}else{
			return null;
		}
	}
	
	public int getQueueSize(){
		return queue.size();
	}


	
	
}



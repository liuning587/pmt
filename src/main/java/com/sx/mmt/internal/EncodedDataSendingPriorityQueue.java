package com.sx.mmt.internal;

import java.util.concurrent.PriorityBlockingQueue;

import com.sx.mmt.internal.protocolBreakers.EncodedPacket;

public class EncodedDataSendingPriorityQueue {
	private PriorityBlockingQueue<PriorityItem> queue = new PriorityBlockingQueue<PriorityItem>();
	
	private class PriorityItem implements Comparable<PriorityItem>{
		private EncodedPacket encodedDataPacket;
		private int priority;
		public PriorityItem(EncodedPacket encodedDataPacket,int priority){
			this.encodedDataPacket=encodedDataPacket;
			this.priority=priority;
		}
		public EncodedPacket getEncodedDataPacket() {
			return encodedDataPacket;
		}
		@Override
		public int compareTo(PriorityItem other) {
			if(this.priority>other.priority) return 1;
			else if(this.priority<other.priority) return -1;
			else return 0;
		}
	}
	
	public void addPacket(EncodedPacket encodedDataPacket,int priority){
		queue.put(new PriorityItem(encodedDataPacket,priority));
	}
	
	public EncodedPacket getPacket(){
		PriorityItem priorityItem=null;
		try {
			priorityItem = queue.poll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(priorityItem!=null){
			return priorityItem.getEncodedDataPacket();
		}else{
			return null;
		}
	}
	
	public int getQueueSize(){
		return queue.size();
	}
	
	
}

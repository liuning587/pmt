package com.sx.mmt.internal.task;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;

public class TaskDelayQueue {
	private DelayQueue<DelayItem> queue=new DelayQueue<DelayItem>();
	
	private class DelayItem implements Delayed{
		private TaskImpl task;
		public DelayItem(TaskImpl task){
			this.task=task;
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
			return unit.convert(task.getNextActionTime() - System.currentTimeMillis(),
					TimeUnit.MILLISECONDS);
		}
		public TaskImpl getTask() {
			return task;
		}
	}
	
	
}

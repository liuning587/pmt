package com.sx.mmt.internal.task;


public class TaskGroup {
	private String name;
	private String nameWithNumber;
	private String taskTag;
	private String parentTag;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTaskTag() {
		return taskTag;
	}
	public void setTaskTag(String taskTag) {
		this.taskTag = taskTag;
	}

	
	public String getParentTag() {
		return parentTag;
	}
	public void setParentTag(String parentTag) {
		this.parentTag = parentTag;
	}
	public String getNameWithNumber() {
		return nameWithNumber;
	}
	public void setNameWithNumber(String nameWithNumber) {
		this.nameWithNumber = nameWithNumber;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taskTag == null) ? 0 : taskTag.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskGroup other = (TaskGroup) obj;
		if (taskTag == null) {
			if (other.taskTag != null)
				return false;
		} else if (!taskTag.equals(other.taskTag))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return nameWithNumber;
	}
	
	public boolean isRoot(){
		if(parentTag==null){
			return true;
		}
		return false;
	}
	
	
}

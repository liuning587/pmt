package com.sx.mmt.internal.task.command;

public class CommandState {
	/**
	 * common
	 */
	public static final String Start="Start";
	public static final String Finish="Finish";
	public static final String Failed="Failed";
	public static final String Exception="Exception";
	
	/**
	 * file update
	 */
	public static final String StartOrderSended="StartOrderSended";
	public static final String DataSended="DataSended";
	public static final String RequestReceiveOrderSended="RequestReceiveOrderSended";
	public static final String FinishOrderSended="FinishOrderSended";
	public static final String CancelOrderSended="CancelOrderSended";
	public static final String LastDataSended="LastDataSended";
	
	/**
	 * query version
	 */
	public static final String OrderSended="OrderSended";
	/**
	 * DelayCommand
	 */
	public static final String Waiting="Waiting";
	/**
	 * port shift
	 */
	public static final String QueryAPN="quertAPN";
	public static final String SetPort="setPort";

	
}

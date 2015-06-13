package com.sx.mmt.internal.task.command;

import org.springframework.stereotype.Component;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;

import com.sx.mmt.internal.task.command.cqdw.CQFinishCommand;
import com.sx.mmt.internal.task.command.cqdw.CQRegisterCommand;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;


@Component("commandFactory")
public class CommandFactory {

	/**
	 * 升级文件命令
	 */
	public static final String UpdateFileCommand="UpdateFileCommand";
	
	private StateMachineBuilder<UpdateFileCommand,String,String,DecodedPacket>
						updateFileCommandBuilder;
	
	public UpdateFileCommand getUpdateFileCommand(String initialStateId){
		UpdateFileCommand command=null;
	    if(updateFileCommandBuilder==null){		
			updateFileCommandBuilder=StateMachineBuilderFactory.create(
					UpdateFileCommand.class, String.class, String.class, DecodedPacket.class);
	    }
		command =updateFileCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 版本查询命令
	 * @param initialStateId
	 * @return
	 */
	public static final String QueryVersionCommand="QueryVersionCommand";
	
	private StateMachineBuilder<QueryVersionCommand,String,String,DecodedPacket>
						queryVersionCommandBuilder;
	
	public QueryVersionCommand getQueryVersionCommand(String initialStateId){
		QueryVersionCommand command=null;
		if(queryVersionCommandBuilder==null){
			queryVersionCommandBuilder=StateMachineBuilderFactory.create(
					QueryVersionCommand.class,String.class,String.class,DecodedPacket.class);
		}
		command=queryVersionCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 端口切换命令
	 * @param initialStateId
	 * @return
	 */
	public static final String PortShiftCommand="PortShiftCommand";
	
	private StateMachineBuilder<PortShiftCommand,String,String,DecodedPacket>
				portShiftCommandBuilder;
	
	public PortShiftCommand getPortShiftCommand(String initialStateId){
		PortShiftCommand command=null;
		if(portShiftCommandBuilder==null){
			portShiftCommandBuilder=StateMachineBuilderFactory.create(
					PortShiftCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(PortShiftCommand) portShiftCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 取消升级命令
	 * @param initialStateId
	 * @return
	 */
	public static final String CancelUpdateFileCommand="CancelUpdateFileCommand";
	
	private StateMachineBuilder<CancelUpdateFileCommand,String,String,DecodedPacket>
					cancelUpdateFileCommandBuilder;
	
	public CancelUpdateFileCommand getCancelUpdateFileCommand(String initialStateId){
		CancelUpdateFileCommand command=null;
		if(cancelUpdateFileCommandBuilder==null){
			cancelUpdateFileCommandBuilder=StateMachineBuilderFactory.create(
					CancelUpdateFileCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(CancelUpdateFileCommand) cancelUpdateFileCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 硬件初始化命令
	 * @param initialStateId
	 * @return
	 */
	public static final String HardwareInitCommand="HardwareInitCommand";
	
	private StateMachineBuilder<HardwareInitCommand,String,String,DecodedPacket>
				hardwareInitCommandBuilder;
	
	public HardwareInitCommand getHardwareInitCommand(String initialStateId){
		HardwareInitCommand command=null;
		if(hardwareInitCommandBuilder==null){
			hardwareInitCommandBuilder=StateMachineBuilderFactory.create(
					HardwareInitCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(HardwareInitCommand) hardwareInitCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 数据初始化命令
	 * @param initialStateId
	 * @return
	 */
	public static final String DataInitCommand="DataInitCommand";
	
	private StateMachineBuilder<DataInitCommand,String,String,DecodedPacket>
				dataInitCommandBuilder;
	
	public DataInitCommand getDataInitCommand(String initialStateId){
		DataInitCommand command=null;
		if(dataInitCommandBuilder==null){
			dataInitCommandBuilder=StateMachineBuilderFactory.create(
					DataInitCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(DataInitCommand) dataInitCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 设置终端主动上报命令
	 * @param initialStateId
	 * @return
	 */
	public static final String SetAutoReportCommand="SetAutoReportCommand";
	
	private StateMachineBuilder<SetAutoReportCommand,String,String,DecodedPacket>
				setAutoReportCommandBuilder;
	
	public SetAutoReportCommand getSetAutoReportCommand(String initialStateId){
		SetAutoReportCommand command=null;
		if(setAutoReportCommandBuilder==null){
			setAutoReportCommandBuilder=StateMachineBuilderFactory.create(
					SetAutoReportCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(SetAutoReportCommand) setAutoReportCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 延时命令
	 * @param initialStateId
	 * @return
	 */
	public static final String DelayCommand="DelayCommand";
	
	private StateMachineBuilder<DelayCommand,String,String,DecodedPacket>
				delayCommandBuilder;
	
	public DelayCommand getDelayCommand(String initialStateId){
		DelayCommand command=null;
		if(delayCommandBuilder==null){
			delayCommandBuilder=StateMachineBuilderFactory.create(
					DelayCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(DelayCommand) delayCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 0F传文件命令
	 * @param initialStateId
	 * @return
	 */
	public static final String GBFileTransferCommand="GBFileTransferCommand";
	
	private StateMachineBuilder<GBFileTransferCommand,String,String,DecodedPacket>
				gBFileTransferCommandBuilder;
	
	public GBFileTransferCommand getGBFileTransferCommand(String initialStateId){
		GBFileTransferCommand command=null;
	    if(gBFileTransferCommandBuilder==null){		
	    	gBFileTransferCommandBuilder=StateMachineBuilderFactory.create(
	    			GBFileTransferCommand.class, String.class, String.class, DecodedPacket.class);
	    }
		command =gBFileTransferCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 重庆通道注册命令
	 * @param initialStateId
	 * @return
	 */
	
	public static final String CQRegisterCommand="CQRegisterCommand";
	
	private StateMachineBuilder<CQRegisterCommand,String,String,DecodedPacket>
					cQRegisterCommandBuilder;
	
	public CQRegisterCommand getCQRegisterCommand(String initialStateId){
		CQRegisterCommand command=null;
		if(cQRegisterCommandBuilder==null){
			cQRegisterCommandBuilder=StateMachineBuilderFactory.create(
					CQRegisterCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(CQRegisterCommand) cQRegisterCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 重庆升级完成命令
	 * @param initialStateId
	 * @return
	 */
	
	public static final String CQFinishCommand="CQFinishCommand";
	
	private StateMachineBuilder<CQFinishCommand,String,String,DecodedPacket>
						cQFinishCommandBuilder;
	
	public CQFinishCommand getCQFinishCommand(String initialStateId){
		CQFinishCommand command=null;
		if(cQFinishCommandBuilder==null){
			cQFinishCommandBuilder=StateMachineBuilderFactory.create(
					CQFinishCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(CQFinishCommand) cQFinishCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	/**
	 * 自定义报文下发命令
	 */
	public static final String CustomMessageCommand="CustomMessageCommand";
	
	private StateMachineBuilder<CustomMessageCommand,String,String,DecodedPacket>
				customMessageCommandBuilder;

	public CustomMessageCommand getCustomMessageCommand(String initialStateId){
		CustomMessageCommand command=null;
		if(customMessageCommandBuilder==null){
			customMessageCommandBuilder=StateMachineBuilderFactory.create(
					CustomMessageCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(CustomMessageCommand) customMessageCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 终端参数读取命令
	 */
	public static final String TerminalParameterReadCommand="TerminalParameterReadCommand";
	
	private StateMachineBuilder<TerminalParameterReadCommand,String,String,DecodedPacket>
					terminalParameterReadCommandBuilder;

	public TerminalParameterReadCommand getTerminalParameterReadCommand(String initialStateId){
		TerminalParameterReadCommand command=null;
		if(terminalParameterReadCommandBuilder==null){		
			terminalParameterReadCommandBuilder=StateMachineBuilderFactory.create(
					TerminalParameterReadCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command =terminalParameterReadCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * 终端参数下发命令
	 */
	public static final String TerminalParameterWriteCommand="TerminalParameterWriteCommand";
	
	private StateMachineBuilder<TerminalParameterWriteCommand,String,String,DecodedPacket>
			terminalParameterWriteCommandBuilder;

	public TerminalParameterWriteCommand getTerminalParameterWriteCommand(String initialStateId){
		TerminalParameterWriteCommand command=null;
		if(terminalParameterWriteCommandBuilder==null){
			terminalParameterWriteCommandBuilder=StateMachineBuilderFactory.create(
					TerminalParameterWriteCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(TerminalParameterWriteCommand) terminalParameterWriteCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	/**
	 * PowerDownEventDataSetCommand
	 */
	public static final String PowerDownEventDataSetCommand="PowerDownEventDataSetCommand";
	private StateMachineBuilder<PowerDownEventDataSetCommand,String,String,DecodedPacket>
				powerDownEventDataSetCommandBuilder;

	public PowerDownEventDataSetCommand getPowerDownEventDataSetCommand(String initialStateId){
		PowerDownEventDataSetCommand command=null;
		if(powerDownEventDataSetCommandBuilder==null){
			powerDownEventDataSetCommandBuilder=StateMachineBuilderFactory.create(
					PowerDownEventDataSetCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(PowerDownEventDataSetCommand) powerDownEventDataSetCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
	
	public static final String ReadTerminalTimeCommand="ReadTerminalTimeCommand";
	private StateMachineBuilder<ReadTerminalTimeCommand,String,String,DecodedPacket>
				readTerminalTimeCommandBuilder;

	public ReadTerminalTimeCommand getReadTerminalTimeCommand(String initialStateId){
		ReadTerminalTimeCommand command=null;
		if(readTerminalTimeCommandBuilder==null){
			readTerminalTimeCommandBuilder=StateMachineBuilderFactory.create(
					ReadTerminalTimeCommand.class, String.class, String.class, DecodedPacket.class);
		}
		command=(ReadTerminalTimeCommand) readTerminalTimeCommandBuilder.newStateMachine(initialStateId);
		command.start();
		return command;
	}
	
}

package utilTest;

import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.EncodedPacket;
import com.sx.mmt.internal.task.command.CommandEvent;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandState;
import com.sx.mmt.internal.task.command.HardwareInitCommand;

public class M14 {
	public static void main(String[] args) {
		CommandFactory commandFactory=new CommandFactory();
		HardwareInitCommand hardwareInitCommand=commandFactory.getHardwareInitCommand(CommandState.Start);
		hardwareInitCommand.fire(CommandEvent.Start,new DecodedPacket());
		
	}
}

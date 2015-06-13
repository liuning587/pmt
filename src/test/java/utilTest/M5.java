package utilTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.api.Command;
import com.sx.mmt.internal.task.command.CommandEvent;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandState;

public class M5 {
public static void main(String[] args) throws Exception{
	CommandFactory commandFactory=new CommandFactory();
	//AbstractStateMachine command=commandFactory.getHardwareInitCommand(CommandEvent.Start);
	Method method = CommandFactory.class
			.getMethod("getHardwareInitCommand", String.class);
	AbstractStateMachine command=(AbstractStateMachine)
			method.invoke(commandFactory, CommandState.Start);
	Map con=new HashMap<String,String>();
	con.put(ConfigConstants.DelayTime, "999");
	((Command)command).setAttr(con);
	command.fire(CommandEvent.Start);
	System.out.println(command.getCurrentState());
	command.fire("TimeUp");
	
}
}

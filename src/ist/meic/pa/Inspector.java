package ist.meic.pa;


import ist.meic.pa.exception.CommandNotFound;
import ist.meic.pa.graph.Graph;
import ist.meic.pa.commands.Command;
import ist.meic.pa.commands.exception.CommandException;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Inspector {
	
	private Graph<Object> graph;	
	private InputStream in = System.in;
	private PrintStream out = System.err;
	private Scanner scanner = new Scanner(System.in);
	
	// Used to store the commands already executed. Avoids creating them again.
	private Map<String,Command> executedCommands = new HashMap<String,Command>();
	
	private boolean keepInspecting = true;
	
	public Inspector() {
		super();
		this.graph = new Graph<Object>();
	}

	public void inspect(Object object) {
		modifyCurrentObj(object);
		String[] splitedLine;
		String cmdName;
		List<String> cmdArguments;
		while(keepInspecting()) {
			print("> ");
			splitedLine = scanLine().split(" ");
			cmdName = splitedLine[0];
			
			// transform the arguments into a list
			cmdArguments = new LinkedList<String>();
			for(String str: splitedLine) {
				cmdArguments.add(str);
			}
			cmdArguments.remove(0);
			
			try {
				executeCommand(cmdName, cmdArguments);
			} catch(CommandNotFound e) {
				println("The command " + e.getCommandName() + " does not exist.");
			} catch(CommandException e2) {
				println(e2.toString());
			}
		}
	}
	
	private  void executeCommand(String commandName, List<String> cmdArgs) throws CommandNotFound, CommandException {
		if(this.executedCommands.containsKey(commandName)) {
			Command command = this.executedCommands.get(commandName);
			command.execute(this,cmdArgs);
		} else {
			try {
				java.lang.Class<? extends Command> c = (java.lang.Class<? extends Command>) java.lang.Class
						.forName("ist.meic.pa.commands.Cmd_" + commandName).asSubclass(Command.class);
				Command command = c.getConstructor().newInstance();
				command.execute(this, cmdArgs);
				this.executedCommands.put(commandName, command);
			} catch (ClassNotFoundException e) {
				throw new CommandNotFound(commandName);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public Object obtainCurrentObj() {
		return this.graph.getCurrentObject();
	}
	
	public void modifyCurrentObj(Object obj) {
		this.graph.add(obj);
	}
	
	public boolean keepInspecting() {
		return this.keepInspecting;
	}
	
	public void stopInspecting() {
		this.keepInspecting = false;
	}
	
	public void println(String message) {
		this.out.println(message);
	}
	
	public void print(String message) {
		this.out.print(message);
	}
	
	public String scanLine() {
		return this.scanner.nextLine();
	}

}

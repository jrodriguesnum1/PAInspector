package ist.meic.pa.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ist.meic.pa.Inspector;

import java.lang.reflect.Field;




import java.lang.reflect.Modifier;

import ist.meic.pa.commands.exception.CommandException;
import ist.meic.pa.commands.exception.FieldNotFoundException;
import ist.meic.pa.commands.exception.InvalidArgumentsException;
import ist.meic.pa.commands.exception.InvalidOptionException;
import ist.meic.pa.commands.util.ReflectionHelper;

public class Cmd_i extends FieldCommand {

	public Cmd_i() {
		super();
	}

	private boolean checkArgs(List<String> args) {
		return (args.size() == 1);
	}

	@Override
	public void execute(Inspector insp, List<String> args)
			throws CommandException {
		if (!this.checkArgs(args)) {
			throw new InvalidArgumentsException("i <name_of_the_field>");
		}

		String fieldName = args.get(0);
		try {
			Object obj = insp.obtainCurrentObj();
			Field field = findField(insp, obj.getClass(), fieldName);
			field.setAccessible(true);
			insp.modifyCurrentObj(field.get(obj));

			insp.printCurrentObj();
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
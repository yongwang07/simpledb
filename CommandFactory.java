import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CommandFactory {
	public static abstract class Command {
		private final Pattern integerPattern = Pattern.compile("^[-\\+]?[\\d]*$");
		protected boolean isInteger(String str) {
			return integerPattern.matcher(str).matches();
		}
		private List<String> parseCommand(String commandStr) {
			List<String> args = new ArrayList<String>();
			for (String str : commandStr.split(" ")) {
				String argument = str.trim();
				args.add(argument);
			}
			return args;
		}
		public abstract  void execute(MemoryDB db, List<String> args);
		public boolean isValidateCommand(String commandString) {
			List<String> args = parseCommand(commandString);
			return isValidate(args);
		}
		
		protected  boolean isValidate(List<String> args) {
			return args.size() == 2 || (args.size() == 1 && "BEGIN ROLLBACK COMMIT".indexOf(args.get(0).toUpperCase()) >= 0);
		}
	}
	
	static Map<String, Command> commands = new HashMap<String, Command>();
	public CommandFactory() {
		commands.put("SET", new Command() {
			@Override
			public void execute(MemoryDB db, List<String> args) {
				db.set(args.get(1), Integer.parseInt(args.get(2)));
			}
			@Override
			public boolean isValidate(List<String> args) {
				return args.size() == 3 &&  isInteger(args.get(2));
			}
		});		
		commands.put("GET", new Command() {
			@Override
			public void execute(MemoryDB db, List<String> args) {
				db.get(args.get(1));
			}
		});
		commands.put("UNSET", new Command() {
			@Override
			public void execute(MemoryDB db, List<String> args) {
				db.unSet(args.get(1));
			}
		});
		commands.put("NUMEQUALTO", new Command() {
			@Override
			public void execute(MemoryDB db, List<String> args) {
				db.equalTo(Integer.parseInt(args.get(1)));
			}			
			@Override
			public boolean isValidate(List<String> args) {
				return args.size() == 2 && isInteger(args.get(1));
			}
		});
		commands.put("BEGIN", new Command() {
			@Override
			public void execute(MemoryDB db, List<String> args) {
				db.startTransaction();
			}
		});
		commands.put("ROLLBACK", new Command() {
			@Override
			public void execute(MemoryDB db, List<String> args) {
				db.rollbackTransaction();
			}
		});
		commands.put("COMMIT", new Command() {
			@Override
			public void execute(MemoryDB db, List<String> args) {
				db.commitTransaction();
			}
		});
	}
	
	public Command parseCommand(List<String> args) {
		Command command = commands.get(args.get(0).toUpperCase());
		if (command == null || !command.isValidate(args)) throw new IllegalArgumentException();
		return command;
	}

}

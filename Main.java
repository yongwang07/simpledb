import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	static final CommandFactory  factory = new CommandFactory();
	static final MemoryDB db = new MemoryDB();
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
        System.out.print(">:");
        while (sc.hasNext()) {
        	String commandStr = sc.nextLine();
        	if (commandStr == null  || commandStr.equalsIgnoreCase("end")) break;
        	try {
        		commandStr = commandStr.trim();
        		if (commandStr.length() == 0) throw new IllegalArgumentException();
        		List<String> arguments = parseCommandString(commandStr);
        		CommandFactory.Command command = factory.parseCommand(arguments);
            	command.execute(db, arguments);
        	} catch(IllegalArgumentException e) {
        		System.out.println("Unknow command");
        	} catch(IllegalStateException e) {
        		System.out.println("Illegal commit/rollback state");
        	}
        	System.out.print(">:");
        }
	}
	
	private static List<String> parseCommandString(String commandStr) {
		List<String> args = new ArrayList<String>();
		for (String str : commandStr.split(" ")) {
			String argument = str.trim();
			args.add(argument);
		}
		return args;		
	}
}

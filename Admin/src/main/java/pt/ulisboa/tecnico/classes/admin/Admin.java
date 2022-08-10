package pt.ulisboa.tecnico.classes.admin;


import java.util.Scanner;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.*;
import pt.ulisboa.tecnico.classes.Stringify;

public class Admin {
	private static final String ACTIVATE = "activate";
	private static final String DEACTIVATE = "deactivate";
	private static final String ACTIVATE_GOSSIP = "activateGossip";
	private static final String DEACTIVATE_GOSSIP = "deactivateGossip";
	private static final String DUMP = "dump";
	private static final String GOSSIP = "gossip";
	private static final String EXIT_CMD = "exit";
	

	public static void main(String[] args) {
		System.out.println(Admin.class.getSimpleName());
    	System.out.printf("Received %d Argument(s)%n", args.length);
		for (int i = 0; i < args.length; i++) {
		  System.out.printf("args[%d] = %s%n", i, args[i]);
		}
		String rank = "P";
		boolean debug = false;
		if(args.length == 1){
			debug = true;
		}
		Scanner scanner = new Scanner(System.in);
		ClassServerFrontend classServerFrontend = new ClassServerFrontend(debug);

		while (true) {
			System.out.printf(">");
			String line = scanner.nextLine();
			String[] cmd = line.split(" ");
			if(cmd.length == 2) {
				rank = cmd[1];
			}
			//exit
			if (EXIT_CMD.equals(line)) {
				scanner.close();
				break;
			}
			else if (ACTIVATE.equals(cmd[0])) {
				ActivateResponse response = classServerFrontend.activate(rank);
				System.out.println(Stringify.format(response.getCode()));
			}
			else if(DEACTIVATE.equals(cmd[0])){
				DeactivateResponse response = classServerFrontend.deactivate(rank);
				System.out.println(Stringify.format(response.getCode()));
			}
			else if(DUMP.equals(cmd[0])){
				DumpResponse response = classServerFrontend.dump(rank);
				System.out.println(Stringify.format(response.getClassState()));
			}
			else if(ACTIVATE_GOSSIP.equals(cmd[0])){
				ActivateGossipResponse response = classServerFrontend.activateGossip(rank);
				System.out.println(Stringify.format(response.getCode()));
			}
			else if(DEACTIVATE_GOSSIP.equals(cmd[0])){
				DeactivateGossipResponse response = classServerFrontend.deactivateGossip(rank);
				System.out.println(Stringify.format(response.getCode()));
			}
			else if(GOSSIP.equals(cmd[0])){
				GossipResponse response = classServerFrontend.gossip(rank);
				System.out.println(Stringify.format(response.getCode()));
			}
			System.out.printf("\n");
		}
	}
}

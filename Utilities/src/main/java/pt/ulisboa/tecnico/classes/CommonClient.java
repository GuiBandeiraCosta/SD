package pt.ulisboa.tecnico.classes;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommonClient {
    private static final String openEnrollments_CMD = "openEnrollments";
    private static final String closeEnrollments_CMD = "closeEnrollments";
    private static final String cancelEnrollment_CMD = "cancelEnrollment";
    private static final String LISTCLASS = "list";
    private static final String ENROLL = "enroll";
    private static final String TURMAS = "TURMAS";
    private static final List<String> writes = new ArrayList<String>(Arrays.asList(openEnrollments_CMD, closeEnrollments_CMD, cancelEnrollment_CMD));
    private static final List<String> reads = new ArrayList<String>(Arrays.asList(LISTCLASS, ENROLL));


    public static String selectEntry(List<ClassesDefinitions.ServerEntry> available_entries) {
        Random random = new Random();
        return available_entries.get(random.nextInt(0,available_entries.size())).getPort();
    }

    public static List<ClassesDefinitions.ServerEntry> init(String command) {
        List<String> qualifiers = new ArrayList<String>();
        if (writes.contains(command)) {
            qualifiers.add("P");
        } else if (reads.contains(command)) {
            qualifiers.add("P");
            qualifiers.add("S");
        } else if (command.equals("P") || command.equals("S")) {
            qualifiers.add(command);
        }
        try {
            NamingServerFrontend namingServerFrontend = new NamingServerFrontend("localhost", 5000);
            return namingServerFrontend.lookup(TURMAS, qualifiers);
        } catch (IOException ioException){
            ioException.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}

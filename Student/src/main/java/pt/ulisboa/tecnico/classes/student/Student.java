package pt.ulisboa.tecnico.classes.student;

import java.util.*;



import io.grpc.Server;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.*;

public class Student {

  private static final String LISTCLASS = "list";
  private static final String ENROLL = "enroll";
  private static final String EXIT_CMD = "exit";
  private static String studentName;
  private static String studentID;

  public static void main(String[] args) {


    // Check arguments.
    if (args.length < 2) {
      System.err.println("Argument(s) missing!");
      System.err.printf("Usage: java %s port%n", Server.class.getName());
      return;
    }
    if(  args[0].length() !=9 ||args[0].startsWith("aluno")== false || args[0].matches(".*\\d{4}")== false ){
      System.err.println("Student ID invalid must be of type alunoxxxx!");
      return;
    }

    boolean debug  = false;
    studentID = args[0];
    studentName = args[1];
    
    for(int i = 2; i < args.length;i++){
      if(args[i].equals("-debug") || args[i].equals("-Debug")){
        debug = true;
        continue;
      }
      studentName += " " + args[i];
    }
    
    if( args[1].length()<3 || studentName.length()>30 ) {
      System.err.println("Invalid Student Name!");
      return;
    }

    Scanner scanner = new Scanner(System.in);
    ClassServerFrontend classServerFrontend = new ClassServerFrontend(debug);

    while (true) {
      System.out.printf(">");
      String line = scanner.nextLine();
      // exit
      if (EXIT_CMD.equals(line)) {
        scanner.close();
        break;
      }

      else if (ENROLL.equals(line)) {
        EnrollResponse response = classServerFrontend.enroll(studentID, studentName);
        if(response == null){System.err.println("No server was ACTIVE");}
        else{
          System.out.println(Stringify.format(response.getCode()));
        }
      }

      else if(LISTCLASS.equals(line)){
        String response = classServerFrontend.listClass();
        System.out.println(response);
      }
      System.out.printf("\n");
    }
  }
}
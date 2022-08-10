package pt.ulisboa.tecnico.classes.professor;


import java.util.Scanner;

import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.*;
import pt.ulisboa.tecnico.classes.Stringify;


public class Professor {

  private static final String openEnrollments_CMD = "openEnrollments";
  private static final String closeEnrollments_CMD = "closeEnrollments";
  private static final String listClass_CMD = "list";
  private static final String cancelEnrollment_CMD = "cancelEnrollment";
  private static final String EXIT_CMD = "exit";
  private boolean debug = false;

  public void checkDebug(String action){
        if(debug){System.err.println(action);}
    }
  
  public Professor(boolean debug) {
        this.debug = debug;
    }
  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    
    while (true) {
      System.out.print("> ");
      String line = scanner.nextLine();
      String[] cmd = line.split(" ");

      boolean debug = false;
      if (args.length == 1) {
        debug = true;
      }
      ClassServerFrontend classServerFrontend = new ClassServerFrontend(debug);
      
      if (EXIT_CMD.equals(cmd[0])) {
        scanner.close();
        break;
      }
      else if (openEnrollments_CMD.equals(cmd[0])) {
        if(cmd.length==2) {
          try {
            OpenEnrollmentsResponse response = classServerFrontend.openEnrollments(Integer.parseInt(cmd[1]));
            System.out.println(Stringify.format(response.getCode()));
          }
          catch(NumberFormatException e){System.out.println("Capacity must be an integer");}
        }
        else{System.out.println("Missing capacity");}

      }
      else if (closeEnrollments_CMD.equals(cmd[0])) {
        if(cmd.length== 1) {
          CloseEnrollmentsResponse response = classServerFrontend.closeEnrollments();
          System.out.println(Stringify.format(response.getCode()));
        }
        else{System.out.println("Wrong Format");}
      }
      else if (listClass_CMD.equals(cmd[0])) {
        if(cmd.length== 1) {
          String response = classServerFrontend.listClass();
          System.out.println(response);
        }
        else{System.out.println("Wrong Format");}
      }
      else if (cancelEnrollment_CMD.equals(cmd[0])) {
        if (cmd.length == 2) {
          CancelEnrollmentResponse response = classServerFrontend.cancelEnrollment(cmd[1]);
          System.out.println(Stringify.format(response.getCode()));
        }
        else{System.out.println("Missing Student ID");}
      }
      System.out.printf("\n");
    }
  }
}
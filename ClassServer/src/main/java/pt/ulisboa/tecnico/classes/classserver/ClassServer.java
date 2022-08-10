package pt.ulisboa.tecnico.classes.classserver;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.StatusRuntimeException;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class ClassServer {
	  private static String port;
	  private static String rank;
	  private static String host_port;
	  private static final String TURMAS = "TURMAS";

	  public static void Gossip(Class class1){
		  if(!class1.isActive()){return;}
		  if(!class1.isGossipActive()){return;}
		  try {
			  NamingServerFrontend namingServerFrontend = new NamingServerFrontend("localhost",5000);
			  List<ClassesDefinitions.ServerEntry> server_entries = namingServerFrontend.lookup(TURMAS, Collections.emptyList());
			  List<ClassesDefinitions.ServerEntry> propagate_entries = server_entries.stream().filter(x -> !x.getPort().equals(host_port)).collect(Collectors.toList());
			  for (ClassesDefinitions.ServerEntry se : propagate_entries) {
				  ClassServerFrontend classServerFrontend = new ClassServerFrontend(se.getPort());
				  classServerFrontend.propagateState(class1.getClassState());
			  }
		  }catch(StatusRuntimeException e){
			  System.err.println("Error " + e);
		  }catch (IOException e){
			  System.err.println("Error " + e);
		  }
	  }

  public static void main(String[] args) throws Exception, IOException {

	  System.out.println(ClassServer.class.getSimpleName());

	  // Print received arguments.
	  System.out.printf("Received %d arguments%n", args.length);
	  for (int i = 0; i < args.length; i++) {
		  System.out.printf("arg[%d] = %s%n", i, args[i]);
	  }

	  // Check arguments.
	  if (args.length < 3) {
		  System.err.println("Argument(s) missing!");
		  System.err.printf("Usage: java %s port%n", Server.class.getName());
		  return;
	  }
	  boolean debug = false;

	  port = args[1];
	  host_port = args[0] + ":" + port;
	  rank = args[2];

	  List<String> qualifiers = new ArrayList<>();
	  qualifiers.add(rank);
	  if (args.length == 4) {
		  debug = true;
	  }
	  Class class1 = new Class(debug);
	  class1.setRank(rank);

	  try {
		  final BindableService classImpl = new ClassServiceImpl(class1);
		  final BindableService adminImpl = new AdminServiceImpl(class1);
		  final BindableService studentImpl = new StudentServiceImpl(class1);
		  final BindableService professorImpl = new ProfessorServiceImpl(class1);
		  // Create a new server to listen on port.
		  Server server = ServerBuilder.forPort(Integer.valueOf(port)).addService(classImpl).addService(adminImpl)
				  .addService(studentImpl).addService(professorImpl).build();
		  // Start the server.
		  server.start();
		  // Server threads are running in the background.

		  try{
			  NamingServerFrontend namingServerFrontend = new NamingServerFrontend("localhost", 5000);
			  namingServerFrontend.register(TURMAS, host_port,qualifiers);
		  }
		  catch (StatusRuntimeException e) {
			  System.err.println("Error " + e);
		  }
		  System.out.println("Server started");

		  Runtime.getRuntime().addShutdownHook(new Thread() {
			  public void run() {
				  try{
				  	NamingServerFrontend namingServerFrontend = new NamingServerFrontend("localhost", 5000);
					  namingServerFrontend.delete(TURMAS, host_port);
				  }
				  catch (StatusRuntimeException e) {
					  System.err.println("Error " + e);
				  }
				  server.shutdownNow( );
			  }
		  });
		  if (rank.equals("P") || rank.equals("S")) {
			  while (true) {
				  Thread.sleep(60000);
				  Gossip(class1);
			  }
		  } else {
			  server.awaitTermination();
		  }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }

}
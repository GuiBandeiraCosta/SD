package pt.ulisboa.tecnico.classes.namingserver;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class NamingServer {
    public static void main(String[] args) throws Exception {

      // Print received arguments.
      System.out.printf("Received %d arguments%n", args.length);
      for (int i = 0; i < args.length; i++) {
        System.out.printf("arg[%d] = %s%n", i, args[i]);
      }
      int port = 5000;
      boolean debug = false;
      if (args.length == 1) {
        debug = true;
      }
      try {
          final BindableService namingServerImpl = new NamingServerServiceImpl(debug);

          // Create a new server to listen on port.
          Server server = ServerBuilder.forPort(port).addService(namingServerImpl).build();
          // Start the server.
          server.start();
          // Server threads are running in the background.

          System.out.println("Server started");

          // Do not exit the main thread. Wait until server is terminated.
          server.awaitTermination();
      } catch (Exception e){
        e.printStackTrace();
      }
    }

}
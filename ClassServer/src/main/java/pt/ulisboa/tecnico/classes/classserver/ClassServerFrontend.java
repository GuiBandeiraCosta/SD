package pt.ulisboa.tecnico.classes.classserver;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateRequest;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateResponse;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;
import io.grpc.ManagedChannel;


public class ClassServerFrontend {
    private ManagedChannel channel;
    private  ClassServerServiceGrpc.ClassServerServiceBlockingStub stub;

    public ClassServerFrontend(String server_host_port1){
        try {
            String[] server_host_port = server_host_port1.split(":");
            String server_host = server_host_port[0];
            int server_port = Integer.parseInt(server_host_port[1]);
            channel = ManagedChannelBuilder.forAddress(server_host, server_port).usePlaintext().build();
            stub = ClassServerServiceGrpc.newBlockingStub(channel);
        }catch (StatusRuntimeException e){
            System.err.println("Error" + e);
            System.exit(1);
        }
    }
    public ClassesDefinitions.ResponseCode propagateState(ClassesDefinitions.ClassState new_classState){
        PropagateStateRequest request = PropagateStateRequest.newBuilder().setClassState(new_classState).build();
        PropagateStateResponse response = stub.propagateState(request);
        channel.shutdown();
        return response.getCode();
    }

}

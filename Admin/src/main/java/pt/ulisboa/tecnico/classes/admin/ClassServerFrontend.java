package pt.ulisboa.tecnico.classes.admin;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.*;
import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;
import java.util.List;

import static pt.ulisboa.tecnico.classes.CommonClient.init;
import static pt.ulisboa.tecnico.classes.CommonClient.selectEntry;


public class ClassServerFrontend {

    private ManagedChannel channel;
    private AdminServiceGrpc.AdminServiceBlockingStub stub;
    private boolean debug;

    public ClassServerFrontend(boolean debug) {
        this.debug = debug;
    }

    public void checkDebug(String action){
        if(debug){System.err.println(action);}
    }

    public void setClassServerFrontend(String server_host_port1) {
        String[] server_host_port = server_host_port1.split(":");
        String server_host = server_host_port[0];
        int server_port = Integer.parseInt(server_host_port[1]);
        try{
            channel = ManagedChannelBuilder.forAddress(server_host, server_port).usePlaintext().build();
            stub = AdminServiceGrpc.newBlockingStub(channel);
        } catch(StatusRuntimeException e){
            System.err.println("Error" + e);
            System.exit(1);
        }
    }
    public void setFromNamingServer(String rank){
        List<ClassesDefinitions.ServerEntry> available_entries;
        available_entries = init(rank);
        String chosenPort = selectEntry(available_entries);
        checkDebug("This was the Chosen Port: " + chosenPort);
        setClassServerFrontend(chosenPort);
    }

    public DumpResponse dump(String rank){
        setFromNamingServer(rank);
        checkDebug("for DUMP");
        DumpRequest request = DumpRequest.newBuilder().build();
        DumpResponse response = stub.dump(request);
        channel.shutdown();
        return response;
    }
    public ActivateResponse activate(String rank){
        setFromNamingServer(rank);
        checkDebug("for ACTIVATE");
        ActivateRequest request = ActivateRequest.newBuilder().build();
        ActivateResponse response = stub.activate(request);
        channel.shutdown();
        return response;
    }

    public DeactivateResponse deactivate(String rank) {
        setFromNamingServer(rank);
        checkDebug("for DEACTIVATE");
        DeactivateRequest request = DeactivateRequest.newBuilder().build();
        DeactivateResponse response = stub.deactivate(request);
        channel.shutdown();
        return response;
    }

    public DeactivateGossipResponse deactivateGossip(String rank) {
        setFromNamingServer(rank);
        checkDebug("for DEACTIVATE_GOSSIP");
        DeactivateGossipRequest request = DeactivateGossipRequest.newBuilder().build();
        DeactivateGossipResponse response = stub.deactivateGossip(request);
        channel.shutdown();
        return response;
    }

    public ActivateGossipResponse activateGossip(String rank) {
        setFromNamingServer(rank);
        checkDebug("for ACTIVATE_GOSSIP");
        ActivateGossipRequest request = ActivateGossipRequest.newBuilder().build();
        ActivateGossipResponse response = stub.activateGossip(request);
        channel.shutdown();
        return response;
    }
    public GossipResponse gossip(String rank) {
        setFromNamingServer(rank);
        checkDebug("to send the state");
        DumpRequest dump_request = DumpRequest.newBuilder().build();
        DumpResponse dump_response = stub.dump(dump_request);
        channel.shutdown();
        if(rank.equals("P")){
            setFromNamingServer("S");
            checkDebug("to receive the state");
        }
        else if(rank.equals("S")){
            setFromNamingServer(("P"));
            checkDebug("to receive the state");
        }
        ClassesDefinitions.ClassState class_state_tosend = dump_response.getClassState();
        GossipRequest gossip_request = GossipRequest.newBuilder().setClassState(class_state_tosend).build();
        GossipResponse gossip_response = stub.gossip(gossip_request);
        channel.shutdown();
        return gossip_response;
    }
}


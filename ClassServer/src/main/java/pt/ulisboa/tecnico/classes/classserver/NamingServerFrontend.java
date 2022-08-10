package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.*;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;

import java.io.IOException;
import java.util.List;

public class NamingServerFrontend {
    private final ManagedChannel channel;
    private final ClassServerServiceGrpc.ClassServerServiceBlockingStub stub;


    public NamingServerFrontend(final String host, final int port) throws StatusRuntimeException {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = ClassServerServiceGrpc.newBlockingStub(channel);
    }

    public void register(String service, String host_port , List<String> qualifiers){
        RegisterRequest request = RegisterRequest.newBuilder().setHostPort(host_port).setService(service).addAllQualifier(qualifiers).build();
        stub.register(request);
        channel.shutdown();
    }
    public List<ClassesDefinitions.ServerEntry> lookup(String service, List<String> qualifiers) throws IOException{
        LookupRequest request = LookupRequest.newBuilder().setService(service).addAllQualifiers(qualifiers).build();
        LookupResponse response = stub.lookup(request);
        List<ClassesDefinitions.ServerEntry> available_entries = response.getEntriesList();
        channel.shutdown();
        return available_entries;
    }
    public void delete(String service, String host_port){
        DeleteRequest request = DeleteRequest.newBuilder().setHostPort(host_port).setService(service).build();
        stub.delete(request);
        channel.shutdown();
    }
}

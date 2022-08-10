package pt.ulisboa.tecnico.classes;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.LookupRequest;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.LookupResponse;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerServiceGrpc;


import java.io.IOException;
import java.util.List;


public class NamingServerFrontend {

    private final ManagedChannel channel;
    private final ClassServerServiceGrpc.ClassServerServiceBlockingStub stub;


    public NamingServerFrontend(final String host, final int port) throws IOException {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = ClassServerServiceGrpc.newBlockingStub(channel);
    }

    public List<ClassesDefinitions.ServerEntry> lookup(String service, List<String> qualifiers){
        LookupRequest request = LookupRequest.newBuilder().setService(service).addAllQualifiers(qualifiers).build();
        LookupResponse response = stub.lookup(request);
        List<ClassesDefinitions.ServerEntry> available_entries = response.getEntriesList();

        channel.shutdown();

        return available_entries;

    }
}
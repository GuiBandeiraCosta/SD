package pt.ulisboa.tecnico.classes.namingserver;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.RegisterRequest;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.RegisterResponse;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.DeleteRequest;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.DeleteResponse;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.LookupRequest;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerNamingServer.LookupResponse;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.naming.ClassServerServiceGrpc;
import java.util.List;

public class NamingServerServiceImpl extends ClassServerServiceGrpc.ClassServerServiceImplBase{
    NamingServices namingServices = new NamingServices();
    boolean debug = false;
    public NamingServerServiceImpl(boolean debug){
        this.debug = debug;
    }
    public void checkDebug(String action){
        if(debug){System.err.println(action);}
    }

    @Override
    public void register(RegisterRequest registerRequest, StreamObserver<RegisterResponse> registerObserver) {
            namingServices.register(registerRequest.getService(),registerRequest.getHostPort(),registerRequest.getQualifierList());
            registerObserver.onNext(RegisterResponse.newBuilder().build());
            checkDebug("Server with Port: " + registerRequest.getHostPort() + "registered");
            registerObserver.onCompleted();
    }

    @Override
    public void lookup(LookupRequest lookupRequest, StreamObserver<LookupResponse> lookupObserver) {
        List<ServerEntry> servers = namingServices.lookup(lookupRequest.getService(),lookupRequest.getQualifiersList());
        LookupResponse.Builder response_entries = LookupResponse.newBuilder();
        checkDebug("Lookup for service: " + lookupRequest.getService() + "and qualifiersList: " + lookupRequest.getQualifiersList());
        for(ServerEntry s: servers){
            ClassesDefinitions.ServerEntry.Builder entry = ClassesDefinitions.ServerEntry.newBuilder();
            entry.setPort(s.getHost_port()).addAllQualifiers(s.getQualifiers()).build();
            response_entries.addEntries(entry);
        }
        lookupObserver.onNext(response_entries.build());
        lookupObserver.onCompleted();
    }

    @Override
    public void delete(DeleteRequest deleteRequest, StreamObserver<DeleteResponse> deleteObserver) {
        checkDebug("Server with HostPort: " + deleteRequest.getHostPort() + "was deleted");
        namingServices.delete(deleteRequest.getService(),deleteRequest.getHostPort());
        deleteObserver.onNext(DeleteResponse.newBuilder().build());
        deleteObserver.onCompleted();
    }
}
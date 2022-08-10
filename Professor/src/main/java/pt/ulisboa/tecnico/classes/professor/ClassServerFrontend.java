package pt.ulisboa.tecnico.classes.professor;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.classes.CommonClient.init;
import static pt.ulisboa.tecnico.classes.CommonClient.selectEntry;


public class ClassServerFrontend {
    private static final String openEnrollments_CMD = "openEnrollments";
    private static final String closeEnrollments_CMD = "closeEnrollments";
    private static final String listClass_CMD = "list";
    private static final String cancelEnrollment_CMD = "cancelEnrollment";
    private ManagedChannel channel;
    private ProfessorServiceGrpc.ProfessorServiceBlockingStub stub;
    private boolean debug = false;

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
            stub = ProfessorServiceGrpc.newBlockingStub(channel);
        } catch(StatusRuntimeException e){
            System.err.println("Error" + e);
            System.exit(1);
        }
    }

    public void setFromNamingServer(String flag){
        List<ClassesDefinitions.ServerEntry> available_entries;
        available_entries = init(flag);
        String chosenPort = selectEntry(available_entries);
        checkDebug("This was the chosen Port: " + chosenPort);
        setClassServerFrontend(chosenPort);
    }

    public OpenEnrollmentsResponse openEnrollments(int capacity){
        setFromNamingServer(openEnrollments_CMD);
        checkDebug("for OPEN_ENROLLMENTS");
        OpenEnrollmentsRequest request = OpenEnrollmentsRequest.newBuilder().setCapacity(capacity).build();
        OpenEnrollmentsResponse response = stub.openEnrollments(request);
        channel.shutdown();
        return response;
    }
    public CloseEnrollmentsResponse closeEnrollments(){
        setFromNamingServer(closeEnrollments_CMD);
        checkDebug("for CLOSE_ENROLLMENTS");
        CloseEnrollmentsRequest request = CloseEnrollmentsRequest.newBuilder().build();
        CloseEnrollmentsResponse response = stub.closeEnrollments(request);
        channel.shutdown();
        return response;
    }

    public String listClass() {
        ListClassRequest request = ListClassRequest.newBuilder().build();
        String formatted_response = null;
        List<ClassesDefinitions.ServerEntry> available_entries;
        List<ClassesDefinitions.ServerEntry> available_entries2 = new ArrayList<>();
        available_entries = init(listClass_CMD);
        available_entries2.addAll(available_entries);
        while (!available_entries2.isEmpty()) {
            String chosenPort = selectEntry(available_entries2);
            checkDebug("Tried this Port " + chosenPort + "for LIST");
            setClassServerFrontend(chosenPort);
            ListClassResponse response = stub.listClass(request);
            channel.shutdown();
            if (response.getCode().equals(ClassesDefinitions.ResponseCode.OK)) {
                formatted_response = Stringify.format(response.getClassState());
                break;
            }
            checkDebug("Server with Port " + chosenPort + "was INACTIVE");
            for (int i = 0; i < available_entries2.size(); i++) {
                if (available_entries2.get(i).getPort().equals(chosenPort)) {
                    available_entries2.remove(i);
                }
            }
            if (available_entries2.isEmpty()) {
                formatted_response = Stringify.format(response.getCode());
            }
        }
        return formatted_response;

    }
    public CancelEnrollmentResponse cancelEnrollment(String studentID) {
        setFromNamingServer(cancelEnrollment_CMD);
        checkDebug("for CANCEL_ENROLLMENT");
        CancelEnrollmentRequest request = CancelEnrollmentRequest.newBuilder().setStudentId(studentID).build();
        CancelEnrollmentResponse response = stub.cancelEnrollment(request);
        channel.shutdown();
        return response;

    }
}
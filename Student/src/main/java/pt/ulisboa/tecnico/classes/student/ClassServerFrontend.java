package pt.ulisboa.tecnico.classes.student;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.classes.Stringify;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;
import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.*;

import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.classes.CommonClient.init;
import static pt.ulisboa.tecnico.classes.CommonClient.selectEntry;

public class ClassServerFrontend {

    private static final String LISTCLASS = "list";
    private static final String ENROLL = "enroll";
    private ManagedChannel channel;
    private StudentServiceGrpc.StudentServiceBlockingStub stub;
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
            stub = StudentServiceGrpc.newBlockingStub(channel);
        } catch(StatusRuntimeException e){
            System.err.println("Error" + e);
            System.exit(1);
        }
    }


    public EnrollResponse enroll(String studentID, String studentName) {
        ClassesDefinitions.Student studentBuilder = ClassesDefinitions.Student.newBuilder().setStudentName(studentName).setStudentId((studentID)).build();
        EnrollRequest request = EnrollRequest.newBuilder().setStudent(studentBuilder).build();
        EnrollResponse response = null;
        List<ClassesDefinitions.ServerEntry> available_entries;
        List<ClassesDefinitions.ServerEntry> available_entries2 = new ArrayList<>();
        available_entries = init(ENROLL);
        available_entries2.addAll(available_entries);
        while (!available_entries2.isEmpty()) {
            String chosenPort = selectEntry(available_entries2);
            checkDebug("Tried this Port " + chosenPort + " for ENROLL");
            setClassServerFrontend(chosenPort);
            response = stub.enroll(request);
            channel.shutdown();
            if (!response.getCode().equals(ClassesDefinitions.ResponseCode.INACTIVE_SERVER)) {
                return response;
            }
            checkDebug("Server with Port " + chosenPort + " was INACTIVE");
            for (int i = 0; i < available_entries2.size(); i++) {
                if (available_entries2.get(i).getPort().equals(chosenPort)) {
                    available_entries2.remove(i);
                }
            }
        }
        return response;
    }
    

    public String listClass() {
        ListClassRequest request = ListClassRequest.newBuilder().build();
        String formatted_response = null;
        List<ClassesDefinitions.ServerEntry> available_entries;
        List<ClassesDefinitions.ServerEntry> available_entries2 = new ArrayList<>();
        available_entries = init(LISTCLASS);
        available_entries2.addAll(available_entries);
        while (!available_entries2.isEmpty()) {
            String chosenPort = selectEntry(available_entries2);
            checkDebug("Tried this Port " + chosenPort + " for LIST");
            setClassServerFrontend(chosenPort);
            ListClassResponse response = stub.listClass(request);
            channel.shutdown();
            if (response.getCode().equals(ClassesDefinitions.ResponseCode.OK)) {
                formatted_response = Stringify.format(response.getClassState());
                break;
            }
            checkDebug("Server with Port " + chosenPort + " was INACTIVE");
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
}
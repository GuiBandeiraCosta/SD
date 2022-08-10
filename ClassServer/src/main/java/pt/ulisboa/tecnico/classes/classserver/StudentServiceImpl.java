package pt.ulisboa.tecnico.classes.classserver;

import io.grpc.stub.StreamObserver;


import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ClassState;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ResponseCode;

import pt.ulisboa.tecnico.classes.contract.student.StudentClassServer.*;
import pt.ulisboa.tecnico.classes.contract.student.StudentServiceGrpc;


public class StudentServiceImpl  extends StudentServiceGrpc.StudentServiceImplBase {
    private static final int FULL_CLASS = -1;
    private static final int STUDENT_ALREADY_ENROLLED = -2;
    private static final int ENROLLMENTS_ALREADY_CLOSED = -3;
    private Class class1;

    public StudentServiceImpl(Class class1) {
        this.class1 = class1;
    }

    @Override
    public void listClass(ListClassRequest listClassRequest, StreamObserver<ListClassResponse> listClassObserver) {
        synchronized (class1) {
            if (!class1.isActive()) {
                listClassObserver.onNext(ListClassResponse.newBuilder().setCode(ResponseCode.INACTIVE_SERVER).build());
            }
            else{
                CommonServiceImpl method = new CommonServiceImpl(class1);
                ClassState.Builder classStateBuilder = method.ListClassBuilder();
                listClassObserver.onNext(ListClassResponse.newBuilder().setCode(ResponseCode.OK).setClassState(classStateBuilder).build());
            }
            listClassObserver.onCompleted();
        }
    }

    @Override
    public void enroll(EnrollRequest enrollRequest, StreamObserver<EnrollResponse> enrollObserver) {
        synchronized (class1) {

            if (!class1.isActive()) {
                enrollObserver.onNext(EnrollResponse.newBuilder().setCode(ResponseCode.INACTIVE_SERVER).build());
            } else {
                int validation = class1.enroll(enrollRequest.getStudent().getStudentId(), enrollRequest.getStudent().getStudentName());
                if (validation == ENROLLMENTS_ALREADY_CLOSED) {
                    enrollObserver.onNext(EnrollResponse.newBuilder().setCode(ResponseCode.ENROLLMENTS_ALREADY_CLOSED).build());
                } else if (validation == STUDENT_ALREADY_ENROLLED) {
                    enrollObserver.onNext(EnrollResponse.newBuilder().setCode(ResponseCode.STUDENT_ALREADY_ENROLLED).build());
                } else if (validation == FULL_CLASS) {
                    enrollObserver.onNext(EnrollResponse.newBuilder().setCode(ResponseCode.FULL_CLASS).build());
                } else {
                    enrollObserver.onNext(EnrollResponse.newBuilder().setCode(ResponseCode.OK).build());
                }

            }
            enrollObserver.onCompleted();
        }
    }
}

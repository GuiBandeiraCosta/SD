package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ResponseCode;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorClassServer.*;
import pt.ulisboa.tecnico.classes.contract.professor.ProfessorServiceGrpc;
import io.grpc.stub.StreamObserver;


public class ProfessorServiceImpl extends ProfessorServiceGrpc.ProfessorServiceImplBase {
    private Class class1;

    public ProfessorServiceImpl(Class class1) {
        this.class1 = class1;
    }

    private static final int FULL_CLASS = -1;
    private static final int ENROLLMENTS_ALREADY_CLOSED = -3;
    private static final int ENROLLMENTS_ALREADY_OPEN = -4;
    private static final int NON_EXISTING_STUDENT = -5;

    @Override
    public void openEnrollments(OpenEnrollmentsRequest openEnrollmentsRequest, StreamObserver<OpenEnrollmentsResponse> openEnrollmentsObserver) {
        synchronized (class1) {
            if (!class1.isActive()) {
                openEnrollmentsObserver.onNext(OpenEnrollmentsResponse.newBuilder().setCode(ResponseCode.INACTIVE_SERVER).build());
            } else {
                int flag = class1.openEnrollments(openEnrollmentsRequest.getCapacity());

                if (flag == ENROLLMENTS_ALREADY_OPEN) {
                    openEnrollmentsObserver.onNext(OpenEnrollmentsResponse.newBuilder().setCode(ResponseCode.ENROLLMENTS_ALREADY_OPENED).build());
                } else {
                    if (flag == FULL_CLASS) {
                        openEnrollmentsObserver.onNext(OpenEnrollmentsResponse.newBuilder().setCode(ResponseCode.FULL_CLASS).build());
                    } else {
                        openEnrollmentsObserver.onNext(OpenEnrollmentsResponse.newBuilder().setCode(ResponseCode.OK).build());
                    }
                }
                openEnrollmentsObserver.onCompleted();
            }
        }
    }

    @Override
    public void closeEnrollments(CloseEnrollmentsRequest closeEnrollmentsRequest, StreamObserver<CloseEnrollmentsResponse> closeEnrollmentsObserver) {
        synchronized (class1) {
            if (!class1.isActive()) {
                closeEnrollmentsObserver.onNext(CloseEnrollmentsResponse.newBuilder().setCode(ResponseCode.INACTIVE_SERVER).build());
            } else {
                int flag = class1.closeEnrollments();

                if (flag == ENROLLMENTS_ALREADY_CLOSED) {
                    closeEnrollmentsObserver.onNext(CloseEnrollmentsResponse.newBuilder().setCode(ResponseCode.ENROLLMENTS_ALREADY_CLOSED).build());
                } else {
                    closeEnrollmentsObserver.onNext(CloseEnrollmentsResponse.newBuilder().setCode(ResponseCode.OK).build());
                }
                closeEnrollmentsObserver.onCompleted();
            }
        }
    }

    @Override
    public void listClass(ListClassRequest listClassRequest, StreamObserver<ListClassResponse> listClassObserver) {
        synchronized (class1) {
            if (!class1.isActive()) {
                listClassObserver.onNext(ListClassResponse.newBuilder().setCode(ResponseCode.INACTIVE_SERVER).build());
            } else {
                CommonServiceImpl method = new CommonServiceImpl(class1);
                ClassesDefinitions.ClassState.Builder classStateBuilder = method.ListClassBuilder();
                listClassObserver.onNext(ListClassResponse.newBuilder().setCode(ResponseCode.OK).setClassState(classStateBuilder).build());
                listClassObserver.onCompleted();
            }
        }
    }

    @Override
    public void cancelEnrollment(CancelEnrollmentRequest cancelEnrollmentRequest, StreamObserver<CancelEnrollmentResponse> cancelEnrollmentObserver) {
        synchronized (class1) {
            if (!class1.isActive()) {
                cancelEnrollmentObserver.onNext(CancelEnrollmentResponse.newBuilder().setCode(ResponseCode.INACTIVE_SERVER).build());
            } else {
                int validation = class1.cancelEnrollment(cancelEnrollmentRequest.getStudentId());
                if (validation == NON_EXISTING_STUDENT) {
                    cancelEnrollmentObserver.onNext(CancelEnrollmentResponse.newBuilder().setCode(ResponseCode.NON_EXISTING_STUDENT).build());
                } else {
                    cancelEnrollmentObserver.onNext(CancelEnrollmentResponse.newBuilder().setCode(ResponseCode.OK).build());
                }
                cancelEnrollmentObserver.onCompleted();
            }
        }
    }
}

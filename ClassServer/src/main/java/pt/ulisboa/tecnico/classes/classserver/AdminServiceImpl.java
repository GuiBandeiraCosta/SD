package pt.ulisboa.tecnico.classes.classserver;


import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.*;
import pt.ulisboa.tecnico.classes.contract.admin.AdminServiceGrpc;

import pt.ulisboa.tecnico.classes.contract.admin.AdminClassServer.*;



import io.grpc.stub.StreamObserver;


public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {
    private Class class1;
    public AdminServiceImpl(Class class1){
        this.class1 = class1;
    }
    @Override
    public void activate(ActivateRequest activateRequest, StreamObserver<ActivateResponse> activateObserver) {
        synchronized (class1) {
            class1.activate();
            activateObserver.onNext(ActivateResponse.newBuilder().setCode(ResponseCode.OK).build());
            activateObserver.onCompleted();
        }
    }

    @Override
    public void deactivate(DeactivateRequest deactivateRequest, StreamObserver<DeactivateResponse> deactivateObserver) {
        synchronized (class1) {
            class1.deactivate();
            deactivateObserver.onNext(DeactivateResponse.newBuilder().setCode(ResponseCode.OK).build());
            deactivateObserver.onCompleted();
        }
    }
    @Override
    public void dump(DumpRequest dumpRequest, StreamObserver<DumpResponse> dumpObserver) {
        synchronized (class1) {
            CommonServiceImpl method = new CommonServiceImpl(class1);
            ClassState.Builder classStateBuilder = method.ListClassBuilder();
            dumpObserver.onNext(DumpResponse.newBuilder().setCode(ResponseCode.OK).setClassState(classStateBuilder).build());
            dumpObserver.onCompleted();
        }
    }
    @Override
    public void activateGossip(ActivateGossipRequest activateGossipRequest, StreamObserver<ActivateGossipResponse> activateGossipObserver) {
        synchronized (class1) {
            class1.activateGossip();
            activateGossipObserver.onNext(ActivateGossipResponse.newBuilder().setCode(ResponseCode.OK).build());
            activateGossipObserver.onCompleted();
        }
    }
    @Override
    public void deactivateGossip(DeactivateGossipRequest deactivateGossipRequest, StreamObserver<DeactivateGossipResponse> deactivateGossipObserver) {
        synchronized (class1) {
            class1.deactivateGossip();
            deactivateGossipObserver.onNext(DeactivateGossipResponse.newBuilder().setCode(ResponseCode.OK).build());
            deactivateGossipObserver.onCompleted();
        }
    }

    @Override
    public void gossip(GossipRequest gossipRequest, StreamObserver<GossipResponse> GossipObserver) {
        synchronized (class1) {
            class1.propagateState(gossipRequest.getClassState());
            GossipObserver.onNext(GossipResponse.newBuilder().setCode(ResponseCode.OK).build());
            GossipObserver.onCompleted();
        }
    }
}
package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateRequest;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerClassServer.PropagateStateResponse;
import pt.ulisboa.tecnico.classes.contract.classserver.ClassServerServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ClassServiceImpl extends ClassServerServiceGrpc.ClassServerServiceImplBase {
    private Class class1;
    public ClassServiceImpl(Class class1) {
        this.class1 = class1;
    }
    @Override
    public void propagateState(PropagateStateRequest propagateRequest,StreamObserver<PropagateStateResponse> propagateObserver){
        synchronized (class1){
            if(!class1.isActive() || !class1.isGossipActive()){
                propagateObserver.onNext(PropagateStateResponse.newBuilder().setCode(ClassesDefinitions.ResponseCode.INACTIVE_SERVER).build());
            }
            else{
                ClassesDefinitions.ResponseCode code =  class1.propagateState(propagateRequest.getClassState());
                propagateObserver.onNext(PropagateStateResponse.newBuilder().setCode(code).build());

            }
            propagateObserver.onCompleted();
        }
    }
}

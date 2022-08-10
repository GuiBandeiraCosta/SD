package pt.ulisboa.tecnico.classes.classserver;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;

public class CommonServiceImpl {
    private Class class1;
    public CommonServiceImpl(Class class1){
        this.class1 = class1;
    }
    public   ClassesDefinitions.ClassState.Builder ListClassBuilder(){

        Class.State class1State = class1.listClass();

        ClassesDefinitions.ClassState.Builder classStateBuilder = ClassesDefinitions.ClassState.newBuilder();

        classStateBuilder.setCapacity(class1State.getCapacity()).setOpenEnrollments(class1State.getOpen());
        
        for(ClassesDefinitions.Timestamp ts : class1State.getTimestamps_list()){
            classStateBuilder.addTimestamps(ts);
        }

        class1State.getStudentsEnrolled().forEach((id,Student)
                -> classStateBuilder.addEnrolled(ClassesDefinitions.Student.newBuilder().setStudentId(id).setStudentName(Student.name)));

        class1State
                .getStudentsNotEnrolled()
                .forEach(
                        (id, Student) ->
                                classStateBuilder.addDiscarded(
                                        ClassesDefinitions.Student.newBuilder()
                                                .setStudentId(id)
                                                .setStudentName(Student.name)));
        classStateBuilder.build();
        
        return classStateBuilder;
    }
}

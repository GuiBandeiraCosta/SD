package pt.ulisboa.tecnico.classes.classserver;


import java.util.List;
import java.util.ArrayList;
import java.util.stream.*;
import java.util.concurrent.ConcurrentHashMap;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Class {
    private int active = 1;
    private  int activeGossip = 1;
    private boolean debug = false;
    private static final int OK = 0;
    private static final int FULL_CLASS = -1;
    private static final int STUDENT_ALREADY_ENROLLED = -2;
    private static final int ENROLLMENTS_ALREADY_CLOSED = -3;
    private static final int ENROLLMENTS_ALREADY_OPEN = -4;
    private static final int NON_EXISTING_STUDENT = -5;
    
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public Class(boolean debug) {
        this.debug = debug;
    }
    public void checkDebug(String action){
        if(debug){System.err.println(action);}
    }

    public class State {
       
        int capacity;
        int slots_occupied;
        boolean open;
        String rank;
        ConcurrentHashMap<String,Student> studentsEnrolled = new ConcurrentHashMap<String,Student>();
        ConcurrentHashMap<String,Student> studentsNotEnrolled = new ConcurrentHashMap<String,Student>();
        List<ClassesDefinitions.Timestamp> timestamps_list = new ArrayList<ClassesDefinitions.Timestamp>();

        public State(){
            this.capacity = 0;
            this.slots_occupied = 0;
            this.open = false;
            this.rank = "";
        }
        public List<ClassesDefinitions.Timestamp> getTimestamps_list(){
            return this.timestamps_list;
        }    
        public class Student{
            String ID;
            String name;

            public Student(String ID,String name){
                this.ID = ID;
                this.name  = name;
            }
            public String getID() {return ID;}
            public void setID(String ID) {this.ID = ID;}
            public String getName() {return name;}
            public void setName(String name) {this.name = name;}
        }

        public void setCapacity(int capacity){this.capacity = capacity;}

        public void setOpen(boolean open){this.open= open;}
        public void setSlots_occupied(int slots_occupied){this.slots_occupied = slots_occupied;}

        public int getCapacity() {return this.capacity;}
        public int getSlotsOccupied(){return this.slots_occupied;}
        public String getRank(){return this.rank;}
        
        public void addStudent(String ID, String name){
            Student student = new State.Student(ID, name);
            studentsEnrolled.put(ID,student);
            this.slots_occupied++;
        }
        public void addStudentNotEnrolled(String ID, String name){
            Student student = new State.Student(ID, name);
            studentsNotEnrolled.put(ID,student);
        }

        public boolean getOpen(){return this.open;}
        public ConcurrentHashMap<String, Student> getStudentsEnrolled() {return this.studentsEnrolled;}
        public ConcurrentHashMap<String, Student> getStudentsNotEnrolled() {return this.studentsNotEnrolled;}
        public void cancelStudent(String ID){
            Student student = studentsEnrolled.get(ID);
            studentsNotEnrolled.put(ID, student);
            studentsEnrolled.remove(ID);
            this.slots_occupied--;
        }

    }
    private State state = new State();

    public boolean getOpen(){return this.state.getOpen();}
    public String getRank(){return this.state.getRank();}

    public void setRank(String rank){
        this.state.rank = rank;
    }
    public void activate() {
        checkDebug("activate: Server is active");
        active = 1;
        activeGossip = 1;
    }

    public void deactivate() { // Still Unfinished
        checkDebug("deactivate: Server is deactivated");
        active = 0;
        activeGossip = 0;
    }
    public void deactivateGossip(){
        checkDebug("deactivateGossip: Server gossip is deactivated");
        activeGossip = 0;
    }

    public void activateGossip(){
        checkDebug("activateGossip: Server gossip is active");
        activeGossip = 1;
    }

     public  boolean isGossipActive(){return this.activeGossip == 1;}

    public boolean isActive(){return this.active == 1;}

    public synchronized ClassesDefinitions.ClassState getClassState(){
        ClassesDefinitions.ClassState.Builder classStateBuilder = ClassesDefinitions.ClassState.newBuilder();
        classStateBuilder.setCapacity(this.state.getCapacity()).setOpenEnrollments(this.state.getOpen());
        this.state.getStudentsEnrolled().forEach((id,Student)
                -> classStateBuilder.addEnrolled(ClassesDefinitions.Student.newBuilder().setStudentId(id).setStudentName(Student.name)));
        
        for(ClassesDefinitions.Timestamp ts : this.state.getTimestamps_list()){
            classStateBuilder.addTimestamps(ts);
        }
        this.state.getStudentsNotEnrolled()
                .forEach(
                        (id, Student) ->
                                classStateBuilder.addDiscarded(
                                        ClassesDefinitions.Student.newBuilder()
                                                .setStudentId(id)
                                                .setStudentName(Student.name)));
        return classStateBuilder.build();
    }

    
    public ClassesDefinitions.ResponseCode propagateState(ClassesDefinitions.ClassState new_classState){
        checkDebug("Server called PROPAGATE_STATE from Gossip");
        this.state.setSlots_occupied(0);
        this.state.studentsEnrolled.clear();
        this.state.studentsNotEnrolled.clear();
        
        if(this.state.getRank().equals("S")){ //Receives ClassState From P
            this.state.setCapacity(new_classState.getCapacity());
            this.state.setOpen(new_classState.getOpenEnrollments());
        }
        List<ClassesDefinitions.Timestamp> timestamps_merged = Stream.concat(this.state.getTimestamps_list().stream(), new_classState.getTimestampsList().stream()).collect(Collectors.toList());
        List<ClassesDefinitions.Timestamp> timestamps_sorted = timestamps_merged.stream().sorted((i1, i2) -> i1.getTime().compareTo(i2.getTime())).collect(Collectors.toList());
        for (ClassesDefinitions.Timestamp ts : timestamps_sorted){
            //get last open enrollments
            if (ts.getCommand().split(" ")[0].equals("openEnrollments")){
                String args[] = ts.getCommand().split(" ");
                this.state.setCapacity(Integer.parseInt(args[1]));
            }
        }
        for (ClassesDefinitions.Timestamp ts : timestamps_sorted){
            if (ts.getCommand().split(" ")[0].equals("closeEnrollments")){
                this.state.setOpen(false);
            }
            else if (ts.getCommand().split(" ")[0].equals("openEnrollments")){
                this.state.setOpen(true);
            }
            else if (ts.getCommand().contains("enroll") && this.state.getSlotsOccupied() < this.state.getCapacity() && this.state.getOpen()){
                String args[] = ts.getCommand().split(" ", 3);
                this.state.addStudent(args[1], args[2]);
            }
            else if(ts.getCommand().contains("enroll") && (this.state.getSlotsOccupied() >= this.state.getCapacity() || !this.state.getOpen())){
                String args[] = ts.getCommand().split(" ",3);
                this.state.addStudent(args[1], args[2]);
                this.state.cancelStudent(args[1]);
            }
            else if (ts.getCommand().split(" ")[0].equals("cancelEnrollment")){
                String args[] = ts.getCommand().split(" ", 2);
                this.state.cancelStudent(args[1]);
            }
        }
        for(ClassesDefinitions.Student s: new_classState.getDiscardedList()){
            this.state.addStudentNotEnrolled(s.getStudentId(),s.getStudentName());
        }
        return ClassesDefinitions.ResponseCode.OK;
    }

    public synchronized State listClass(){
        checkDebug("Server called LIST_CLASS: Returned class state");
        return this.state;
    } // Works as dump

    public synchronized int enroll(String studentID, String studentName){
        checkDebug("ENROLL: Adds a student with studentID:"+studentID +" studentname:" + studentName +" to ConcurrentHashMap studentsEnrolled");
        if(getOpen() == false){return ENROLLMENTS_ALREADY_CLOSED;}
        else if(state.getStudentsEnrolled().containsKey(studentID)){return STUDENT_ALREADY_ENROLLED;}
        else if(state.getSlotsOccupied() >= state.getCapacity()) {return FULL_CLASS;}    
        
        else{
            state.addStudent(studentID, studentName);
            ClassesDefinitions.Timestamp ts = ClassesDefinitions.Timestamp.newBuilder().setTime(LocalDateTime.now().format(formatter)).setCommand("enroll " + studentID + " " + studentName).build();
            this.state.getTimestamps_list().add(ts);
        }
        return OK;
    }


    public int openEnrollments(int capacity){
        checkDebug("OPEN_ENROLLMENTS: Open Enrollements with capacity: " + capacity);
        if(getOpen() == true){return ENROLLMENTS_ALREADY_OPEN;}
        else if((this.state.getCapacity() > capacity) && (this.state.getSlotsOccupied() > capacity)){
            return FULL_CLASS;
        }
        else{
            this.state.setOpen(true);
            this.state.setCapacity(capacity);
            
            ClassesDefinitions.Timestamp ts = ClassesDefinitions.Timestamp.newBuilder().setTime(LocalDateTime.now().format(formatter)).setCommand("openEnrollments " + capacity).build();
            this.state.getTimestamps_list().add(ts);
            
        }
        return OK;
    }

    public int closeEnrollments(){
        checkDebug("CLOSE_ENROLLMENTS: Closed Enrollements");
        if(getOpen() == false){return ENROLLMENTS_ALREADY_CLOSED;}
        else {
            this.state.setOpen(false);

            ClassesDefinitions.Timestamp ts = ClassesDefinitions.Timestamp.newBuilder().setTime(LocalDateTime.now().format(formatter)).setCommand("closeEnrollments").build();
            this.state.getTimestamps_list().add(ts);

        }
        return OK;
    }
    public synchronized int cancelEnrollment(String studentID){
        if(!state.getStudentsEnrolled().containsKey(studentID)){ return NON_EXISTING_STUDENT; }
        checkDebug("CANCEL_ENROLLMENTS: Removes a student with studentID:"+ studentID +" from studentsEnrolled and adds to studentsNotEnrolled");
        
        ClassesDefinitions.Timestamp ts = ClassesDefinitions.Timestamp.newBuilder().setTime(LocalDateTime.now().format(formatter)).setCommand("cancelEnrollment " + studentID).build();
        this.state.getTimestamps_list().add(ts);
        
        state.cancelStudent(studentID);
        return OK;
    }
}

package General;

import Util.CRUD;

import instructor.Instructor;
import instructor.Repository;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MAClass implements Repository<MAClass>{
    private final static String MA_CLASS ="data/class.txt";
    
    private Instructor inst;
    
    
    private String classID;
    private String className;
    private String level;
    private String discipline;
    private int maxStud;
    
    private LocalTime sTime;
    private LocalTime eTime;
    private LocalDate sDate;
    private LocalDate eDate;
    private Room room;
    private DayOfWeek dow;
    private double price;
    private ScheStats stat;
    
    private Repository<Instructor> instRep = new Instructor();
    //private List<Booking> bkks = Booking.loadRepo();        
    
    public MAClass(String classID, Instructor inst, String discipline, String className, String level, int max){
        this.classID = classID;
        this.inst =  inst;
        this.discipline = discipline;
        this.className = className;
        this.level = level;
        this.maxStud = max;
    }
    
    public MAClass(){}
    
    public MAClass(String classID, Instructor inst, String discipline, String className, String level, int maxStud, Room room, 
            LocalDate sDate, LocalDate eDate, DayOfWeek dow, LocalTime sTime, LocalTime eTime) {
        this(classID, inst, discipline, className, level, maxStud);
        this.room = room;
        this.sDate = sDate;
        this.eDate = eDate;
        this.eTime = eTime;
        this.dow = dow;
        this.sTime = sTime;
    }
    private String roomid;
    public MAClass(String classID, Instructor inst, String discipline, String className, String level, int maxStud, String room, 
            LocalDate sDate, LocalDate eDate, LocalTime sTime, LocalTime eTime, ScheStats stat, double price) {
        this(classID, inst, discipline, className, level, maxStud);
        this.roomid = room;
        this.sDate = sDate;
        this.eDate = eDate;
        this.eTime = eTime;
        //this.dow = dow;
        this.sTime = sTime;
        this.stat = stat;
        this.price = price;
        
    }
    
    //accessor
    public String getID(){return this.classID;}
    public String getDiscipline(){return this.discipline;}
    public String getClassName(){return this.className;}
    public String getLevel(){return this.level;}
    public int getMaxStudent(){return this.maxStud;}
    public Instructor getInstructor(){return this.inst;}
    public Room getRoom(){return this.room;}
    public LocalTime getStartTime(){ return this.sTime;}
    public LocalTime getEndTime(){ return this.eTime;}
    public LocalDate getStartDate(){return this.sDate;}
    public LocalDate getEndDate(){ return this.eDate;}
    public DayOfWeek getDayOfWeek(){return this.dow;}
    
    //can be change (mutators)
    public void setMaxStudent(int maxStudent){this.maxStudent = maxStudent;}
    public void setClassName (String name){this.className = name;    }
    public void setLevel (String level){this.level = level;}
    //for creating a class base
    public void setInstructor (String instructor){this.inst = instructor;}
    
    /*
    //this can be use mainly for set a limitaiton when it hits the max
    public int countStudent(String classID){
        int count = 0;
        //TAKE IT FROM ATTENDANCE FILE FIRST...
        for(Booking b : bkks){
            if(b.getClassID().equals(classID)){ //
                count++;
            }
        }
        return count;
    }
    */
    @Override
    public List<MAClass> loadRepo(){
        File file = new File(MA_CLASS);
        List<String[]> read = CRUD.readFile(file, false);
        
        List<MAClass> classes = new ArrayList<>();
        for(String[] c : read){
            
            
            classes.add(new MAClass(c[0], instRep.findID(c[1]), c[2], c[3], c[4], Integer.parseInt(c[5]), c[6], 
                    LocalDate.parse(c[7]), LocalDate.parse(c[8]), LocalTime.parse(c[9]), LocalTime.parse(c[10]), 
                    ScheStats.valueOf(c[11]), Double.parseDouble(c[12])));
        }
        return classes;
    }
    
    @Override
    public MAClass findID( String id ){
        List<MAClass> list = loadRepo();
        
        for(MAClass l :list){
            if(l.getID().equals(id)){
                return l; //returns a student information FULL 
            }
        }
        return new MAClass();
    
    }
    
}

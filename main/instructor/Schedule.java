package General;

import static General.Room.MIN_CAPACITY;
import Util.CRUD;
import Util.CRUDFunc;
import Util.Date;
import Util.Time;
import attendoff.Attendance;
import instructor.Instructor;
import instructor.Repository;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


//Things to validate = room cap input didnt exceed the max cap of room 


public class Schedule implements Repository<Schedule> {
    private final String SCHEDULE = "data/schedule.txt";
    
    private final int classInterval = 15;
    private final int slotInterval = 5;
    private final LocalTime OPEN = LocalTime.of(14, 1); //academy starts open time
    private final LocalTime CLOSE = LocalTime.of(21, 2); // academy close time
    private final int MIN_DURATION = 45;
    private final int MAX_DURATION =150;
    
    //UPDATE THE CLASS AS WELL DONT FORGETTTTT.. 
    private String clsID;
    private String scheID;
    private String discipline;
    private LocalTime sTime;
    private LocalTime eTime;
    private LocalDate date;
    private ScheStats status;
    
    private Instructor inst;
    private Room room;
    private MAClass classID;
    
    private int duration;
    
   // private ArrayList<Schedule> cs = read; // put it inside or outside mybe
   // private ArrayList<Attendance> atts = repo;
    
    
    private Repository<MAClass> classRep = new MAClass();
    
    public Schedule( String scheID, MAClass classID, Room room, LocalDate date, LocalTime sTime, LocalTime eTime, ScheStats status){
        this.date = date;
        this.classID = classID;
        this.scheID = scheID;
        this.sTime = sTime;
        this.eTime = eTime;
        this.room = room;
        this.status = status; //considering enums
    }
    
    public Schedule(){}
    //THIS ONE MAYBE CAN CHANGE THE STATUS TO ENUM
    private String roomID;
    public Schedule(String s, MAClass c, LocalDate d, LocalTime sT, LocalTime eT, String r, ScheStats status){
        this.scheID = s;
        this.classID = c;
        this.date = d;
        this.sTime=sT;
        this.eTime =eT;
        this.roomID = r;
        this.status = status;
    }
    
    public String getID(){return scheID;}
    public MAClass getClassID(){return classID;}
    public Room getRoom(){return room;}

    public LocalTime getStartTime(){
        return sTime;}
    public LocalTime getEndTime(){return eTime;}
    public LocalDate getDate(){return date;}
    public ScheStats getStatus(){return status;}
    public Instructor getInstructor() {return inst;}
    
    
    public void setDuration(int duration){ //class duration, this wont be included.. mainly to set the duration in GUI.. later put it in classMA class 
        this.duration = duration;}
    public void setStartTime(LocalTime time){this.sTime = time;}
    public void setEndTime(LocalTime time){this.eTime = time;}
    public void setDate(LocalDate date){this.date = date;}
    
    
    
    /*========================================================================================================================================
                            Hihi THIS IS FOR THE TIME THINGY HANDLING hihhihihi
    */
    
    //CREATE A VALIATION WHERE DURATION MUST BE SET FIRST BEFORE CHOOSING THE TIME..
    public boolean isFreeSlot(LocalDate date, String instID, LocalTime s1, LocalTime e1){
        for(Schedule s : cs ){
            if(!s.getClassID().getInstructor().getID().equals(instID)){
                continue;
            }
            
            if(!s.getDate().equals(date)){
                continue;
            }
            
            if(Time.isOverlapTime(s1, e1, s.getStartTime(), s.getEndTime())){
                return false;
            }
        }
        return true;
    }

    //either use Schedule class or imediate date.... but the date must be string
    //CONSIDER: String date and instID, because if using Schedule object, I might need to set the value first
    //THis one is to obtain the free time slot for the instructor itself..
    public List<String> dispFreeTimeSlot(LocalDate date, String instID, int duration){
        List<String> timeSlot = new ArrayList<>();
        if (duration < MIN_DURATION || duration > MAX_DURATION) {
            return timeSlot;
        }
        LocalTime start = OPEN; 
        while(!start.plusMinutes(duration).isAfter(CLOSE)){
            //idk yawr
            
            LocalTime end = start.plusMinutes(duration);
            
            
            if(isFreeSlot(date, instID, start, end)){
                timeSlot.add(start+ "-"+ end);
            }
            
            start = start.plusMinutes(slotInterval);
            
        }
        
        return timeSlot;   
    }
    
    //Validation On dates
    //non clickable event for the GUI if false
    public boolean isFreeDate(LocalDate date, String instID, int duration){
        return !dispFreeTimeSlot(date, instID, duration).isEmpty();
    }
    
    /*================================================================================================================================= 
                                                For Room handling
    */
    
    public List<Room> validRoomCap(int cap){
        List<Room> roomList = roomRep.loadRepo();
        
        List<Room> valid = new ArrayList<>();
        
        for(Room r : roomList){
            //Or maybe I put a JPanel later..
            if(cap <= r.getMaxCap() && cap >= MIN_CAPACITY){
                valid.add(r);
            }
        }
        
        return valid;
    }
        
    public List<Room> scheAvailRoom(LocalDate date, LocalTime sT, LocalTime eT, int cap){
        //load all the room from the file
        List<Room> roomList = validRoomCap(cap);
        //handle if room empty 
        if(roomList.isEmpty()){
            return null;
        }
        
        List<Schedule> scheList = loadRepo();
        
        List<Room> avail = new ArrayList<>();
        
        for(Room r: roomList){
            
            for(Schedule s : scheList){
                if(!r.getID().equals(s.getRoom().getID())){
                    continue;
                }
                
                if(!s.getDate().equals(date)){
                    continue;
                }
                
                if(!Time.isOverlapTime(s.getStartTime(), s.getEndTime(), sT, eT)){
                    avail.add(r);
                }
            }
        }
        
        
        return avail;
    }
    
        
    public boolean isConlfict(LocalDate date, LocalTime sT, LocalTime eT, String roomID){
        List<Schedule> scheList = loadRepo();// no need to call the using interface cuz the constructor itself inside here
        //it can cause an recursive error, keep calling the constructor again and again;
        for(Schedule s:scheList ){
            if()
        }
    }
    
    
    /*======
                  FILE HANDLING \
    =====================*/
    @Override
    public List<Schedule> loadRepo(){
        Schedule sche = new Schedule();
        File file = new File(SCHEDULE);
        List<String[]> read = CRUD.readFile(file, false);
        
        List<Schedule> output = new ArrayList<>();
        for(String[] r : read){
            
            
            //output.add(new Schedule(r[0], classRep.findID(r[1]), LocalDate.parse(r[2]), LocalTime.parse(r[3]), LocalTime.parse(r[4]),
            //Room.findID(r[5]), ScheStats.valueOf(r[6])));
            output.add(new Schedule(r[0], classRep.findID(r[1]), LocalDate.parse(r[2]), LocalTime.parse(r[3]), LocalTime.parse(r[4]),
            r[5], ScheStats.valueOf(r[6])));
        }
        
        return output;
    }
    
    @Override
    public Schedule findID(String id){
        
        List<Schedule> vals = loadRepo();
        
        for(Schedule v: vals){
            if(v.getID().equals(id)){
                return v; //returns a student information FULL 
            }
        }
        return new Schedule();
    }
    /*
    @Override
    public boolean overwrite(Schedule sche){
        List<Schedule> old = loadRepo();
        boolean f = false;
        
        String header = "scheID | classID | className | desc | discipline | level | maxStudent | date | timeStart | timeEnd | roomID";
        List<Schedule> updated = new ArrayList<>();
        
        for(Schedule s : old){
            if(s.getID().equals(sche.getID())){
                if(s.equals(sche)){
                    return false; //this is if it has same content, it's no use of opverwritting it, but if u overwrite it it's not a biggie either
                }
                updated.add(sche);
                f = true;
            } else{
                updated.add(s);
            }
        }
        
        if(!f){
            //this is if the file that need to be overwritten is not found, to know whether the overwrite succesful or not
            return false;
        }
        return overWriteFile(updated, SCHEDULE, header);
    }
    */
    
    //sorting all schedules from the earliest schedule to the latest..
    public static void sortScheDateTime(List<Schedule> l) {
        l.sort(Comparator.comparing(Schedule::getDate).thenComparing(Schedule::getStartTime));
    }
    
 
}

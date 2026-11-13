package attendoff;
import General.Schedule;
import Util.CRUDFunc;
import Util.Date;
import Util.Time;
import instructor.Student;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Attendance {
    private AttendStats stats;
    private LocalDateTime dateTime;
    private Student student;
    private Schedule schedule;
    
    
    private ArrayList<Student> students = Repo.StudRepo(); //this is loadStud()
    
    //better use this one cuz I can immedietaley called the stuent name
    public Attendance(Schedule scheid, Student studid, AttendStats status, String dateTime){
        this.student = studid;
        this.stats = status;
        this.schedule = scheid;
        this.dateTime = LocalDateTime.parse(dateTime);
    }
    
    public Attendance(){
        
    }
    
    //accessor (getter)
    public Student getStudent(){return student;}
    public Schedule getSchedule(){return schedule;}
    public AttendStats getStatus(){return stats;    }
    public LocalDateTime getDateTime(){        return dateTime;    }
    
    
    //mutator(setter)
    public void setStatus(AttendStats stats){        this.stats = stats;    }
    public void setDateTime(String date){        this.dateTime= LocalDateTime.parse(date);}

    
    
           
    public List<Attendance> loadAtds(){
        List<String[]> atdLoad = CRUDFunc.readFile(ATTENDANCE);
        
        
        List<Attendance> atds = new ArrayList<>();
        
        
        for(String[] a : atdLoad){
            AttendStats status = AttendStats.valueOf(a[2]);
            LocalDateTime dT = LocalDateTime.parse(a[3]);
            
            //schedule and student will return in
            atds.add(new Attendance(Schedule.findID(a[0]), Student.findID(a[1]), status, dT));
            
            
        }
        return atds;
    }
    
}

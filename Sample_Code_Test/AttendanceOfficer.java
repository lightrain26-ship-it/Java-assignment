package instructor;


import Util.CRUD;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceOfficer extends User{
   private String role;

   private final static String ATTOFFICER = "src/instructor/attendanceOfficer.txt";
    
    public AttendanceOfficer(String userID, String password, String name,String email,String phone, LocalDate dob,String gender, LocalDate dateJoined, String role){
        super(userID, password, name,email,phone,dob,gender,dateJoined);
        this.role = role;
    }
    public AttendanceOfficer(){};
    public String getRole(){return role;}

    //any setter method?
    
    
    
    
    public static List<AttendanceOfficer> loadAO() throws IllegalArgumentException{
        
        File file = new File(ATTOFFICER); 
        List<String[]> read = CRUD.readFile(file); 
        read.remove(0);
        List<AttendanceOfficer> vals = new ArrayList<>(); 
        
        for(String[] s : read){
            vals.add(new AttendanceOfficer(s[0],s[1],s[3],s[4], s[5],LocalDate.parse(s[6]),s[7],LocalDate.parse(s[8]), s[2]));
        }
        
        return vals;
    }
    
    
    public static AttendanceOfficer findAO(String id){
        
        for(AttendanceOfficer l :loadAO()){
            if(l.getId().equals(id)){
                return l; //returns a student information FULL 
            }
        }
        return new AttendanceOfficer();
    }
}

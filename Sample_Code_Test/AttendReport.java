package attendoff;

import General.Schedule;
import java.util.ArrayList;
import java

public class AttendReport {
    private ArrayList<Attendance> atts = Read the repo;
    
    //can be use for both class and student
    public double attRate(int present, int total){ //maybe set inside GUI set the present = calcstupresent();?
        if(total == 0){ // guard 0 division that can cause error, if it somehow pass
            return 0.0;
        }
        return (double) present / total * 100;
    }
    
    
    //this for student
    private ArrayList<Attendance> StudentAttendances(Student student){
        ArrayList<Attendance> stuAtt = new ArrayList<>();
        
        for(Attendance a : atts){
            if(a.getStudent().getID().equals(student.getID())){
                stuAtt.add(a);
            }
        }
        
        return stuAtt;
    }
    //attendance per schedule
    public ArrayList<Attendance> ClassScheduleAttendance(Schedule sche){
        ArrayList<Attendance> classAtt = new ArrayList<>();
        
        for(Attendance a : atts){
            if(a.getSchedule().getID().equals(sche.getID())){
                classAtt.add(a);
            }
        }
        
        return classAtt;
    }
    
    //calc student attendance status
    public int calcStudentAttendance(AttendStats status, Student student){
        int total = 0;
        
        for(Attendance s : StudentAttendances(student)){//this return 1 studentID with different schedule
            if(s.getStatus()==status){
                total++;
            }
        }
        
        return total;
    }

    public double studentAttRate(Student student){
        int present = calcStudentAttendance(AttendStats.PRESENT, student);
        int absent = calcStudentAttendance(AttendStats.ABSENT, student);
        int late = calcStudentAttendance(AttendStats.LATE, student);
        
        //PUT ON CONSIDERATION
        int totalClass = present+late+absent;
        int additionalAbsent = absent + late/3;
        
        return attRate(totalClass - additionalAbsent, totalClass);
    }
    
    
    
    
    //calc class attendance status
    public int calcClasstAttendance(AttendStats status, Schedule sche){
        
        int total = 0;
        
        for(Attendance s : ClassScheduleAttendance(sche)){ //this return 1 scheID with different students
            if(s.getStatus() == status){
                total++;
            }
        }
        
        return total;
    }
    
    public double classAttRate(Schedule sche){
        int present = calcClassAttendance(AttendStats.PRESENT, sche);
        int absent = calcClassAttendance(AttendStats.ABSENT, sche);
        int late = calcClassAttendance(AttendStats.LATE, sche);
        
        return attRate(present + late, present + absent + late);
    }
    
    
    public PieChart pieChartDiagram(){
        PieChart pie = new PIeChartBuilder().width(300).height(300);
    }
    
    
}

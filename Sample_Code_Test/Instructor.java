/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package instructor;

import Util.CRUD;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class Instructor extends User{
    private String role;
    private String speciality;
    private String grade;
    
    private final static String INSTRUCTOR = "src/instructor/instructor.txt";
    
    public Instructor(String userID, String password, String name,String email,String phone, LocalDate dob,String gender, LocalDate dateJoined, String speciality, String grade,String role){
        super(userID, password, name,email,phone,dob,gender,dateJoined);
        this.speciality = speciality;
        this.grade = grade;
        this.role = role;
    }
    public Instructor(){};
    public String getRole(){return role;}
    public String getSpeciality(){return speciality;}
    public String getGrade(){return grade;}
    
    //any setter method?

    public static List<Instructor> loadInst(){
        
        File file = new File(INSTRUCTOR); 
        List<String[]> read = CRUD.readFile(file); 
        read.remove(0);
        List<Instructor> instructors = new ArrayList<>(); 
        
        for(String[] s : read){
            instructors.add(new Instructor(s[0],s[1],s[3],s[4], s[5],LocalDate.parse(s[6]),s[7],LocalDate.parse(s[10]),s[8], s[9], s[2]));
        }
        
        return instructors;
    }

    public static Instructor findInst(String id) {
        for(Instructor l :loadInst()){
            if(l.getId().equals(id)){
                return l; //returns a student information FULL 
            }
        }
        return new Instructor();
    }
    
    
    
}

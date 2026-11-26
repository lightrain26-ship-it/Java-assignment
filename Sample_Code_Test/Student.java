package instructor;


import Util.CRUD;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class Student extends User{
    private String speciality;
    private String grade;
    private final static String STUDENT = "src/instructor/student.txt";
    
    
    public Student(){};
    public Student(String userID, String password, String name,String email,String phone, LocalDate dob,String gender, LocalDate dateJoined, String speciality, String grade){
        super(userID, password, name,email,phone,dob,gender,dateJoined);
        this.speciality = speciality;
        this.grade = grade;
    }
    private String name;
    public Student(String name){
        this.name = name;
    }
    
    public Student(String[] stud){
        super(stud[0], stud[1], stud[2], stud[3], stud[4], LocalDate.parse(stud[5]), stud[6], LocalDate.parse(stud[9]));
        this.speciality = stud[7];
        this.grade = stud[8];  
    }
    
    public String getGrade(){return grade;}
    public String getSpeciality(){return speciality;}
    
    
    public static List<Student> loadStudent(){
        File file = new File(STUDENT); // can perhaps remove this and change it through fileRead
        List<String[]> read = CRUD.readFile(file); //change the var in File reader to String
        read.remove(0);
        List<Student> students = new ArrayList<>(); //create a new list consist of List of Object 'Student'
        
        try{
            for(String[] s : read){
                students.add(new Student(s[0],s[1],s[2],s[3],s[4],LocalDate.parse(s[5]),s[6],LocalDate.parse(s[9]),s[7], s[8]));//just immediately add student cuz it doesnt contain other  "OBJECT" like Attendance
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Array Index Out Of Bound occurs, Student");
        } catch(DateTimeParseException e){
            System.out.println("Invalid parsing the time format, Student");
        } catch (NullPointerException e){
            System.out.println("There is Null value, Student");
        }
        
        return students;
    }
    //??
    public static Student findStudent(String ID){
        for(Student s: loadStudent()){
            if(s.getId().equals(ID)){
                return s; //returns a student information FULL 
            }
        }
        return new Student();
    }

  
}

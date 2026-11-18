/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package General;

import instructor.Repository;
import instructor.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class Login implements Repository{
    public enum LoginResult{
        INVALID_EMAIL,
        INVALID_PASSWORD_FORMAT,
        SUCCESS,
        INCORRECT_PASSWORD_EMAIL
    }
    private Student student = new Student();
    private Instructor inst = new Instructor();
    private AttendanceOfficer attOff = new AttendanceOfficer();
    private FacilitiesOfficer facOff = new FacilitiesOfficer();
    private Manager manager = new Manager();
    private Administrator administrator = new Administrator();

    private List<User> users;
    private Repository<User> userRep = new Login();

    private String userID;
    
    public Login() {
        
    }

    // Load users from all role repositories
    @Override
    public List<User> loadRepo() {

        List<User> users = new ArrayList<>();

        users.addAll(student.loadRepo());
        users.addAll(inst.loadRepo());
        users.addAll(attOff.loadRepo());
        users.addAll(facOff.loadFacOfficers());
        users.addAll(manager.loadManager());
        users.addAll(administrator.loadAdmin());

        return users;
    }

    // Validate email formating...
    public boolean validEmail(String email){
        if(email == null || email.contains(" ")) {
            return false;
        }
        return email.endsWith("@mail.sda.edu.my");
    }

    // Check whether the email is typed correclty or not..
    public boolean emailExists(String email){
        for(User u : users) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }

    // Validate password format...
    public boolean validPassword(String pw) {

        return pw != null && !pw.contains(" ") && pw.length() >= 8;
    }

    // Authenticate the user...
    public LoginResult validLogin(String email, String pw){
        List<User> userList = userRep.loadRepo();
        
        if(!validEmail(email)) {
            return LoginResult.INVALID_EMAIL;
        }
        email =email.trim();
        if(!emailExists(email)){
            return LoginResult.INCORRECT_PASSWORD_EMAIL;
        }
        if(!validPassword(pw)){
            return LoginResult.INVALID_PASSWORD_FORMAT;
        }

        for(User u : userList){

            if(u.getEmail().equalsIgnoreCase(email)&& u.getPassword().equals(pw)){
                
                return LoginResult.SUCCESS;
            }
        }

        return LoginResult.INCORRECT_PASSWORD_EMAIL;
    }
    
    //cuz each email must be unique can use email to search for id
    public String getID(String email){
        List<User> userList = userRep.loadRepo();
        
        for(User u : userList){
            if(u.getEmail().equals(email)){
                return u.getEmail();
            }
            
        }
        return null;
    }
    
    

}
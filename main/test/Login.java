package General;

import common_code.*;
import instructor.Repository;
import instructor.User;
import java.util.ArrayList;
import java.util.List;

public class Login{
    public enum LoginResult{
        INVALID_EMAIL,
        INVALID_PASSWORD_FORMAT,
        SUCCESS,
        INCORRECT_PASSWORD_EMAIL
    }

    private String userID;
    private String email;
    private String pw;
    
    public Login(String email, String pw) {
        this.email = email;
        this.pw = pw;
    }

    // Load users from all role repositories
    
    public List<User> loadUser() {

        List<User> users = new ArrayList<>();

        users.addAll(Student.loadStudent());
        users.addAll(Instructor.loadInst());
        users.addAll(AttendanceOfficer.loadAO());
        users.addAll(FacilityOfficer.loadFacOfficers());
        users.addAll(Manager.loadManager());
        users.addAll(Administrator.loadAdmin());

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
    public boolean emailExists(String email, List<User> users){
        for(User u : users) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }

    // Validate password format...
    public boolean validPassword(String pw) {

        return !pw.contains(" ") && pw.length() >= 8;
    }

    // Authenticate the user...
    public LoginResult validLogin(){
        List<User> userList = loadUser();
        
        if(!validEmail(email)) {
            return LoginResult.INVALID_EMAIL;
        }
        email =email.trim();
        if(!emailExists(email, userList)){
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
    
    //cuz each email must be unique can use email to retrieve the id
    public String getID(String email){
        for(User u : loadUser()){
            if(u.getEmail().equals(email)){
                return u.getId();
            }
            
        }
        return "";
    }
    
    

}    
/* this is the logic for switching between users
       
        String email = emailtxt.getText();
        char[] rawpw = pwtxt.getPassword();
        String pw = String.valueOf(rawpw);
        
        
        Login log = new Login(email, pw);
        LoginResult s =log.validLogin();
        switch(s){
            case LoginResult.INCORRECT_PASSWORD_EMAIL:
                emailtxt.setText("Incorrect Email or Password!");
                emailtxt.setForeground(Color.red);
                break;
                
            case LoginResult.INVALID_EMAIL:
                emailtxt.setText("INVALID EMAIL!");
                emailtxt.setForeground(Color.red);
                break;
                
            case LoginResult.INVALID_PASSWORD_FORMAT:
                emailtxt.setText("Password must be atleast 8 Characters");
                emailtxt.setForeground(Color.red);
                break;
                
            case LoginResult.SUCCESS:
                //This one get the USER'S ID
                String id = log.getID(email);
                String role = id.substring(0, 3);
                
                if(id.isBlank()){
                    JOptionPane.showMessageDialog(null, "Cannot retrieve user ID");
                }
                
                if(id.isBlank()){
                    JOptionPane.showMessageDialog(null, "Cannot retrieve id");
                    return;
                }
                // here u guys will put ur 
                switch(role){
                    case "STU" -> {
                        Student_Main_UI stu = new Student_Main_UI(id);
                        stu.setVisible(true);
                }
                        
                    case "MAN" -> {
                }
                        
                    case "ADM" -> {
                }
                        
                    case "INS" -> {
                }
                        
                    case "FAC" -> {
                }
                        
                    case "ATT" -> {
                }
                    default -> JOptionPane.showMessageDialog( null,No selectable option");
                        
                }
                this.dispose();
                parent.dispose();// this one to close the main menu..
// this one to close the main menu..
            default:
                JOptionPane.showMessageDialog(null,"ERRORR");
*/    

}

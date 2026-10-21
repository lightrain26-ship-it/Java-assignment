package common_code;
import java.time.LocalDate;

abstract class User {
    private String userID;
    private String password;
    private String name;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private LocalDate dateJoined;

    public User(String userID, String password, String name,String email,String phone, LocalDate dob,String gender, LocalDate dateJoined ){
        this.userID = userID;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.dateJoined = dateJoined;

    }
    public String getId(){
        return userID;
    }
    public void setId(String userID){
        this.userID = userID;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public LocalDate getDob(){
        return dob;
    }

    public void setDob(LocalDate dob){
        this.dob = dob;
    }

    public String getGender(){
        return gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public LocalDate getDateJoined(){
        return dateJoined;
    }

    public void setDateJoined(LocalDate dateJoined){
        this.dateJoined = dateJoined;
    }
}
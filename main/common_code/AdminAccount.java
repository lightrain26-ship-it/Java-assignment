package common_code;

import java.time.LocalDate;

public class AdminAccount extends User{
    private String role;

    public AdminAccount(String userID, String password, String name,String email,String phone, LocalDate dob,String gender, LocalDate dateJoined , String role){
        super(userID, password, name, email, phone, dob, gender, dateJoined);
        this.role = role;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }
    public String[] toFileRow(){
        return new String[]{getId(), getPassword(),getRole(), getName(), getEmail(), getPhone(), getDob().toString(), getGender(), getDateJoined().toString()};
    }

}

package common_code;

import java.time.LocalDate;

public class AdminAccount extends User{

    public AdminAccount(String userID, String password, String name,String email,String phone, LocalDate dob,String gender, LocalDate dateJoined ){
        super(userID, password, name, email, phone, dob, gender, dateJoined);
    }
    @Override 
    public String getRole(){
        return "Administrator";
    }
    @Override 
    public String toString(){
        return getId() + " | " + getRole() + " | " + getName() + " | " + getEmail() + " | "
             + getPhone() + " | " + getDob() + " | " + getGender() + " | " + getDateJoined();
    };

    public String[] toFileRow(){
        return new String[]{getId(), getPassword(),getRole(), getName(), getEmail(), getPhone(), getDob().toString(), getGender(), getDateJoined().toString()};
    }

    
    public static AdminAccount fromRow(String[] row){
        return new AdminAccount(
            row[0],                    // userID
            row[1],                    // password
            row[3],                    // name   (row[2] is role, skipped)
            row[4],                    // email
            row[5],                    // phone
            LocalDate.parse(row[6]),   // dob
            row[7],                    // gender
            LocalDate.parse(row[8])    // dateJoined
        );
    }

}

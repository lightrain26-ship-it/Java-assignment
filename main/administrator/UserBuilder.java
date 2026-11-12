package administrator;

import java.time.LocalDate;

public class UserBuilder {
    //Automatically create password for account (role + id number)
    public static String generatePassword(String rolename, String userID) {
        String roleLower = rolename.toLowerCase();
        String idNumber = userID.substring(3);
        return roleLower + idNumber;
    }
    //Method to validate and not allow '|' from input
    private static void validateNoPipe(String[] data) {
        for (String value : data) {             //loop
            if (value.contains("|")) {
                throw new IllegalArgumentException("Value cannot contain the '|' character: " + value);
            }
        }
    }
    //Method for instructor and student with specialty and grade
    public static String[] buildStudInsRow(String id, String password, String role, String name, String email, String phone, LocalDate dob, String gender, String specialty, String grade) {
        String[] data = {id, password, role, name, email, phone, dob.toString(), gender, specialty, grade, LocalDate.now().toString()
        };

        validateNoPipe(data);
        return data;
    }
    //Method for staff with no specialty and grade
    public static String[] buildStaffRow(String id, String password, String role, String name, String email, String phone, LocalDate dob, String gender) {
        String[] data = {id, password, role, name, email, phone, dob.toString(), gender, LocalDate.now().toString()
        };
        validateNoPipe(data);
        return data;    
    }
}
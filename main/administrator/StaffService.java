package administrator;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import common_code.InputHelper;
import common_code.WriteFunction;
import java.io.File;
import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;

public class StaffService {
    private static final int MIN_AGE = 18; //Minimum age for staff
    
    //Store userID currently logging in
    private String userID;          //The userID logged in
    private String userRole;        //Administrator or Manager
    private File logFile;           //The log file for the current user
    
    public StaffService(String userID, String userRole) {
        this.userRole = userRole;
        this.userRole = userRole;
        if(userRole.equals("Managers")) {
            this.logFile = new File("data", FileMenu.ADMINRECORD);
        } else if(userRole.equals("Administrators")) {
            this.logFile = new File("data", FileMenu.ADMINRECORD);
        }
    }

    //Stop other roles from accessing this service
    public void checkRoleAllowed(String role) {
        if (userRole.equals("Administrator") && userRole.equals("Managers")) {
            throw new SecurityException("Access denied. Only Administrators and Managers can access this service.");
        }
    }

  //HELPER METHODS
  //Check specialty: cannot be empty & cannot have '|'
    public String checkSpecialty (String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty cannot be empty");
        }
        if (specialty.contains("|")) {
            throw new IllegalArgumentException("Specialty cannot contain the '|' character.");
        }
        return specialty.trim();
    }

    //HELPER METHODS
    //Check grade: grade cannot be empty & must be either BEGINNER, INTERMEDIATE, or ADVANCED
    public String checkGrade (String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade cannot be empty");
        }
        grade = grade.trim().toUpperCase();
        if (!grade.equals("BEGINNER") && !grade.equals("INTERMEDIATE") && !grade.equals("ADVANCED")) {
            throw new IllegalArgumentException("Invalid grade. Grades must be one of: BEGINNER, INTERMEDIATE, ADVANCED");
        }
        return grade;
    }

    //HELPER METHODS
    //Write one ADD line into Admin_record.txt
    private void LogAdd(StaffAccount staff) {
        String recordID = IDGenerator.generateNextID(logFile, "REC");
        String[] row = {recordID, "ADD", staff.getId(), staff.getName(), staff.getRole(), userID, LocalDate.now().toString(), FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN};
        WriteFunction.logChange(userRole, row);
    }

    //HELPER METHODS
    //Specify txt file, ID prefix, password and Instructor for each role
    public StaffAccount addStaff(String role, String name, String phone, LocalDate dob, String gender, String specialty, String grade) {
        File targetFile;
        String prefix;
        String passwordWord;
        boolean isInstructor = false;

        if(role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }

        //Check if the role is allowed to be added by the current user
        checkRoleAllowed(role); 

        switch (role) {
            case "Instructor":
                targetFile = new File("data", FileMenu.INSTRUCTOR);
                prefix = "INS";
                passwordWord = "instructor";
                isInstructor = true;
                break;
            case "FacilitiesOfficer":
                targetFile = new File("data", FileMenu.FACILITYOFFICE);
                prefix = "FAC";
                passwordWord = "facilities";
                break;
            case "AttendanceOfficer":
                targetFile = new File("data", FileMenu.ATTENDANCEOFFICE);
                prefix = "ATT";
                passwordWord = "attendance";
                break;
            case "Manager":
                targetFile = new File("data", FileMenu.MANAGER);
                prefix = "MAN";
                passwordWord = "manager";
                break;
            case "Administrator":
                targetFile = new File("data", FileMenu.ADMIN);
                prefix = "ADM";
                passwordWord = "admin";
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }

        //Check input values
        if (dob == null) {
            throw new IllegalArgumentException("Date of Birth cannot be empty");
        }
        name = name == null ? null : name.trim();
        phone = phone == null ? null : phone.trim();
        gender = gender == null ? null : gender.trim();

        InputHelper.checkName(name);
        InputHelper.checkphonenumber(phone);
        InputHelper.checkDob(dob.toString(), MIN_AGE);
        gender = InputHelper.checkGender(gender);

        if (isInstructor) {
            specialty = checkSpecialty(specialty);
            grade = checkGrade(grade);
        } 

        //Prepare the values that's not typed by admin
        String id = IDGenerator.generateNextID(targetFile, prefix);
        String email = IDGenerator.generateEmail(id);
        String password = passwordWord + id.substring(3);

        //Build the StaffAccount object
        StaffAccount staff;
        if (isInstructor) {
            staff = new InstructorAccount(id, password, name, email, phone, dob, gender, specialty, grade, LocalDate.now());
        } else {
            staff = new StaffAccount(role, id, password, name, email, phone, dob, gender, LocalDate.now());
        }

        //Save to txt file
        CRUD.addRecord(userRole, targetFile, staff.toFileRow());
        LogAdd(staff); //Log the ADD to Admin_record.txt
        return staff;
    }

      //HELPER METHOD
      //


}
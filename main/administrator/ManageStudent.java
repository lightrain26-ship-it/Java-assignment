package administrator;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import common_code.InputHelper;
import common_code.WriteFunction;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManageStudent {
    private static final int MIN_AGE = 5;

    private String userID;
    private String userRole;
    private File studentFile;
    private File logFile = new File("data", FileMenu.ADMINRECORD);

    public ManageStudent(String userID, String userRole) {
        if (!userRole.equals("Administrators")) {
            throw new SecurityException("Access denied. Only administrators can manage students");
        }
        this.userID = userID;
        this.userRole = userRole;
    }

    //HELPER METHODS
    //Check specialty cannot be empty & cannot have '|'
    public String checkSpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty cannot be empty");
        }
        if (specialty.contains("|")) {
            throw new IllegalArgumentException("Specialty cannot contain the character '|");
        }
        return specialty.trim();
    }

    //HELPER METHODS
    //Check that grade must be either BEGINNER, INTERMEDIATE, ADVANCED
    public String checkGrade(String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade cannot be empty");
        }
        grade = grade.trim().toUpperCase();
        if (!grade.equals("BEGINNER") && !grade.equals("INTERMEDIATE") && !grade.equals("ADVANCED")) {
            throw new IllegalArgumentException("Invalid grade. Grade must be either BEGINNER, INTERMEDIATE, or ADVANCED");
        }
        return grade;
    }

    //Write one ADD line into Admin_record.txt
    private void logAdd(StudentAccount student) {
        String recordID = IDGenerator.generateNextID(logFile, "REC");
        String[] row = {recordID, "ADD", student.getId(), student.getName(), student.getRole(), userID, LocalDate.now().toString(), FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN};
        WriteFunction.logChange(userRole, row);
    }

    //===================================================================================================================================
    //                                                  ADD STUDENT
    //===================================================================================================================================
    public StudentAccount addStudent(String name, String phone, LocalDate dob, String gender, String specialty, String grade) {
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
        specialty = checkSpecialty(specialty);
        grade = checkGrade(grade);

        //Prepare values that are not typed by admin
        String id = IDGenerator.generateNextID(studentFile, "STU");
        String email = IDGenerator.generateEmail(id);
        String password = "student" + id.substring(3);

        //Build the object, then save and log it
        StudentAccount student = new StudentAccount(id, password, name, email, phone, dob, gender, specialty, grade, LocalDate.now());
        CRUD.addRecord(userRole, studentFile, student.toFileRow());
        logAdd(student);
        return student;
    }

    //===================================================================================================================================
    //                                                  VIEW STUDENT
    //===================================================================================================================================

    //Return every active student
    public List<StudentAccount> getAllStudent() {
        List<StudentAccount> allStudents = new ArrayList<>();

        List<String[]> rows = CRUD.readFile(studentFile);
        if (rows.isEmpty()) {
            return allStudents;
        }
        int columns = rows.get(0).length;

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length != columns) {                    //blank or broken line
                continue;
            }
            if (row[1].equals(FileMenu.EMPTY_DOMAIN)) {     //deleted account
                continue;
            }
            allStudents.add(StudentAccount.fromRow(row));
        }
        return allStudents;
    }
}

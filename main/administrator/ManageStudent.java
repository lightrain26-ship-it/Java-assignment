package administrator;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

//OBJECT ORIENTED
//Inheritance: same as ManageStaff, ManageStudent extends ManageAccount and gets the validation + log methods from the parent
//student.txt: studentID | password | name | email | phone | dob | gender | specialty | grade | dateJoined  (no role column)
public class ManageStudent extends ManageAccount {
    private static final int MIN_AGE = 5;           //Minimum age for student
    private static final String ID_PATTERN = "STU\\d{3}";

    private File studentFile = new File("data", FileMenu.STUDENT);

    //CONSTRUCTOR
    //Only Administrators can manage students. Save the user that is logged in by sending them to the parent class
    public ManageStudent(String userID, String userRole) {
        super(userID, userRole);
        if (!userRole.equals("Administrators")) {
            throw new SecurityException("Access denied. Only administrators can manage students");
        }
    }

    //OBJECT ORIENTED
    //Method overriding: the parent has an abstract getMinAge(), student must be at least 5
    @Override
    protected int getMinAge() {
        return MIN_AGE;
    }

    //===================================================================================================================================
    //                                                  HELPER METHODS
    //===================================================================================================================================
    //HELPER METHODS
    //Check the student ID format (e.g. STU001) and change it to upper case
    private String checkStudentID(String studentID) {
        if (studentID == null || studentID.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        }
        studentID = studentID.trim().toUpperCase();
        if (!studentID.matches(ID_PATTERN)) {
            throw new IllegalArgumentException("Invalid student ID. Use STU followed by 3 digits (e.g. STU001)");
        }
        return studentID;
    }

    //HELPER METHODS
    //Change one txt row into a StudentAccount. Return null if the row is broken (wrong number of columns, wrong ID, wrong date...)
    private StudentAccount rowToStudent(String[] row, int columns) {
        if (row.length != columns || !row[0].matches(ID_PATTERN)) {
            return null;
        }
        try {
            return StudentAccount.fromRow(row);
        } catch (RuntimeException e) {
            return null;            //for example the date is not a real date
        }
    }

    //HELPER METHODS
    //Take ID from email login
    public static String extractUserIDFromEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Login email cannot be empty");
        }
        String cleaned = email.trim();
        int atIndex = cleaned.indexOf('@');
        String idPart = (atIndex != -1) ? cleaned.substring(0, atIndex).trim() : cleaned;
        return idPart.toUpperCase();
    }

    //HELPER METHODS
    //Create Object ManageStudent directly from email currently logged in
    public static ManageStudent fromLoginEmail(String email, String userRole) {
        String loggedInID = extractUserIDFromEmail(email);
        return new ManageStudent(loggedInID, userRole);
    }

    //HELPER METHODS
    //Check largest ID number for certain prefix in student.txt and Admin_record.txt
    private String nextStudentID(File targetFile, String prefix) {
        int maxNumber = 0;

        maxNumber = Math.max(maxNumber, findMaxNumberForPrefix(CRUD.readFile(targetFile), prefix));

        if (logFile != null && logFile.exists()) {
            maxNumber = Math.max(maxNumber, findMaxNumberForPrefix(CRUD.readFile(logFile), prefix));
        }

        return prefix + String.format("%03d", maxNumber + 1);
    }

    //HELPER METHODS
    //Scan all cells from rows to search for ID with given prefix
    private int findMaxNumberForPrefix(List<String[]> rows, String prefix) {
        int max = 0;
        if (rows == null || rows.isEmpty()) {
            return max;
        }
        String pattern = prefix + "(\\d{3})";
        java.util.regex.Pattern compiled = java.util.regex.Pattern.compile(pattern);

        for (String[] row : rows) {
            if (row == null) {
                continue;
            }
            for (String cell : row) {
                if (cell == null) {
                    continue;
                }
                java.util.regex.Matcher matcher = compiled.matcher(cell.trim());
                while (matcher.find()) {
                    int num = Integer.parseInt(matcher.group(1));
                    if (num > max) {
                        max = num;
                    }
                }
            }
        }
        return max;
    }

    //===================================================================================================================================
    //                                                  ADD STUDENT
    //===================================================================================================================================
    //dob is the text typed by admin (yyyy-MM-dd)
    public StudentAccount addStudent(String name, String phone, String dob, String gender, String specialty, String grade) {
        checkCurrentUserActive();

        //Check input values (every value typed by admin)
        name = checkName(name);
        phone = checkPhone(phone, "");
        LocalDate dobDate = checkDob(dob);
        gender = checkGender(gender);
        specialty = checkSpecialty(specialty);
        grade = checkGrade(grade);

        //Prepare the values that's not typed by admin
        String id = nextStudentID(studentFile, "STU");
        String email = IDGenerator.generateEmail(id);
        String password = "student" + id.substring(3);

        //Build the object, then save and log it
        StudentAccount student = new StudentAccount(id, password, name, email, phone, dobDate, gender, specialty, grade, LocalDate.now());
        CRUD.addRecord(userRole, studentFile, student.toFileRow());
        logAction("ADD", student, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN);
        return student;
    }

    //===================================================================================================================================
    //                                                  VIEW STUDENT
    //===================================================================================================================================
    //View all ACTIVE students. Deleted students (N/A) are skipped. A broken line is ignored (and counted), the other lines are still read
    public List<StudentAccount> getAllStudents() {
        checkCurrentUserActive();
        List<StudentAccount> allStudents = new ArrayList<>();
        brokenLines = 0;

        List<String[]> rows = CRUD.readFile(studentFile);
        if (rows.isEmpty()) {
            return allStudents;
        }
        int columns = rows.get(0).length;
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length > 1 && row.length == columns && row[1].equals(FileMenu.EMPTY_DOMAIN)) {
                continue; //Skip deleted students
            }
            StudentAccount student = rowToStudent(row, columns);
            if (student == null) {
                brokenLines++;
            } else {
                allStudents.add(student);
            }
        }
        return allStudents;
    }

    //HELPER METHODS
    //Find one active student by ID, or throw an error if the ID does not exist / already deleted. Used by update and delete
    public StudentAccount findActiveStudent(String studentID) {
        studentID = checkStudentID(studentID);
        checkCurrentUserActive();

        List<String[]> rows = CRUD.readFile(studentFile);
        int columns = rows.get(0).length;
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length > 1 && row[0].equals(studentID) && !row[1].equals(FileMenu.EMPTY_DOMAIN)) {
                StudentAccount student = rowToStudent(row, columns);
                if (student == null) {
                    throw new IllegalStateException("The record of " + studentID + " in the txt file is broken. Please fix the file first");
                }
                return student;
            }
        }
        throw new NoSuchElementException("No active student with ID: " + studentID);
    }

    //===================================================================================================================================
    //                                                  SEARCH STUDENT
    //===================================================================================================================================
    //Returns every ACTIVE student where the keyword exist. An empty list means "keyword not found"
    public List<StudentAccount> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        keyword = keyword.trim();

        List<StudentAccount> results = new ArrayList<>();
        for (StudentAccount student : getAllStudents()) {
            if (student.matches(keyword)) {
                results.add(student);
            }
        }
        return results;
    }

    //===================================================================================================================================
    //                                                  UPDATE STUDENT
    //===================================================================================================================================
    //ID, password, email and date joined cannot be changed
    public StudentAccount updateStudent(String studentID, String name, String phone, String dob, String gender, String specialty, String grade) {
        StudentAccount oldStudent = findActiveStudent(studentID);   //error here if the ID does not exist

        //Check input values (same rules as add)
        name = checkName(name);
        phone = checkPhone(phone, oldStudent.getId());              //its own phone number is allowed
        LocalDate dobDate = checkDob(dob);
        gender = checkGender(gender);
        specialty = checkSpecialty(specialty);
        grade = checkGrade(grade);

        StudentAccount newStudent = new StudentAccount(oldStudent.getId(), oldStudent.getPassword(), name, oldStudent.getEmail(),
                phone, dobDate, gender, specialty, grade, oldStudent.getDateJoined());

        //Compare the old row and new row, if they are the same then nothing is updated
        String[] oldRow = oldStudent.toFileRow();
        String[] newRow = newStudent.toFileRow();
        if (Arrays.equals(oldRow, newRow)) {
            throw new IllegalStateException("Nothing has been changed.");
        }

        CRUD.updateRecord(userRole, studentFile, newRow);

        //Log every changed field as one line in Admin_record.txt (the field name is taken from the txt header)
        String[] header = CRUD.readFile(studentFile).get(0);
        for (int i = 0; i < newRow.length; i++) {
            if (!oldRow[i].equals(newRow[i])) {
                logAction("UPDATE", newStudent, header[i], oldRow[i], newRow[i]);
            }
        }
        return newStudent;
    }

    //===================================================================================================================================
    //                                                  DELETE STUDENT
    //===================================================================================================================================
    //Find the student to delete. Call this BEFORE asking "are you sure?"
    public StudentAccount checkDeletable(String studentID) {
        return findActiveStudent(studentID);        //error here if the ID does not exist
    }

    //The ID row stays in the txt file and everything else becomes N/A, so the same ID will never be used again
    public StudentAccount deleteStudent(String studentID) {
        StudentAccount student = checkDeletable(studentID);
        CRUD.deactivateRecord(student.getId(), userRole, studentFile);
        logAction("DELETE", student, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN);
        return student;
    }
}
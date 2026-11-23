package administrator;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import common_code.User;
import common_code.WriteFunction;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;

//OBJECT ORIENTED
//Abstract class: the things that Manage Staff and Manage Student both need (validation, log, current user check) are written ONLY ONCE here.
//ManageStaff and ManageStudent extend this class, so they do not repeat the code. We cannot create "new ManageAccount()" directly
public abstract class ManageAccount {
    //Options for the dropdown (JComboBox) in GUI. The check methods below also use them, so every screen follows the same list
    public static final String[] GENDERS = {"Male", "Female"};
    public static final String[] SPECIALTIES = {"Karate", "Judo", "Taekwondo", "MMA", "MuayThai", "KungFu"};
    public static final String[] GRADES = {"BEGINNER", "INTERMEDIATE", "ADVANCED"};

    private static final int MAX_AGE = 100;         //Older than this is most likely a typing mistake
    private static final int MAX_NAME_LENGTH = 50;

    //OBJECT ORIENTED
    //Encapsulation + protected: private for outsiders, but the child classes (ManageStaff, ManageStudent) can use them
    protected String userID;          //The userID logged in
    protected String userRole;        //Administrators or Managers
    protected File logFile = new File("data", FileMenu.ADMINRECORD);           //The log file for the current user
    protected int brokenLines = 0;    //How many broken lines were ignored in the last time the file was read

    //CONSTRUCTOR
    //Save who is logged in (ID and role). The child classes call this with super(userID, userRole)
    public ManageAccount(String userID, String userRole) {
        this.userID = userID;
        this.userRole = userRole;
    }

    //OBJECT ORIENTED
    //Abstract method: the parent does not know the minimum age, each child must write its own (staff = 18, student = 5)
    protected abstract int getMinAge();

    //How many broken lines were ignored the last time the file was read (for the "x broken lines ignored" text)
    public int getBrokenLines() {
        return brokenLines;
    }

    //===================================================================================================================================
    //                                                  VALIDATION (everything typed by the admin)
    //===================================================================================================================================
    //HELPER METHODS
    //Check a value that comes from a dropdown (role, gender, specialty, grade). Must be one of the options, return it in the correct spelling
    public String checkOption(String value, String[] options, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        for (String option : options) {
            if (option.equalsIgnoreCase(value.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid " + fieldName.toLowerCase() + ". Choose one of: " + String.join(", ", options));
    }

    //HELPER METHODS
    //Check specialty: must be one of SPECIALTIES
    public String checkSpecialty(String specialty) {
        return checkOption(specialty, SPECIALTIES, "Specialty");
    }

    //HELPER METHODS
    //Check grade: must be BEGINNER, INTERMEDIATE, or ADVANCED
    public String checkGrade(String grade) {
        return checkOption(grade, GRADES, "Grade");
    }

    //HELPER METHODS
    //Check gender: must be Male or Female
    public String checkGender(String gender) {
        return checkOption(gender, GENDERS, "Gender");
    }

    //HELPER METHODS
    //Check name: only letters and single spaces (no numbers, no special characters). Each word is changed to Capital first letter (Aaaa Bbbb Cccc)
    public String checkName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        name = name.trim().replaceAll("\\s+", " ");           //many spaces become one space
        if (!name.matches("[A-Za-z]+( [A-Za-z]+)*")) {
            throw new IllegalArgumentException("Name can only contain letters and spaces (no numbers or special characters)");
        }
        if (name.length() < 2 || name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must be between 2 and " + MAX_NAME_LENGTH + " letters");
        }
        String result = "";
        for (String word : name.split(" ")) {
            if (!result.isEmpty()) {
                result += " ";
            }
            result += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        }
        return result;
    }

    //HELPER METHODS
    //Check phone: digits only, starts with 01, 10 or 11 digits in total (same as every phone in the txt files), and not used by other account
    //ignoreID is the account that is being updated (so its own phone is not counted as "used"). Use "" when adding
    public String checkPhone(String phone, String ignoreID) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        phone = phone.trim();
        if (!phone.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number can only contain digits (no spaces, '-' or '+')");
        }
        if (!phone.startsWith("01")) {
            throw new IllegalArgumentException("Phone number must start with 01");
        }
        if (phone.length() < 10 || phone.length() > 11) {
            throw new IllegalArgumentException("Phone number must be 10 or 11 digits (01 + 8 or 9 digits)");
        }
        if (isPhoneUsed(phone, ignoreID)) {
            throw new IllegalArgumentException("This phone number is already used by another account.");
        }
        return phone;
    }

    //HELPER METHODS
    //Look for the phone in every staff file and student file. Deleted accounts (N/A) and broken lines are skipped
    private boolean isPhoneUsed(String phone, String ignoreID) {
        String[] fileNames = {FileMenu.INSTRUCTOR, FileMenu.FACILITYOFFICE, FileMenu.ATTENDANCEOFFICE,
                              FileMenu.MANAGER, FileMenu.ADMIN, FileMenu.STUDENT};
        int[] phoneColumns = {5, 5, 5, 5, 5, 4};         //student.txt has no role column, so its phone is in column 4

        for (int f = 0; f < fileNames.length; f++) {
            List<String[]> rows = CRUD.readFile(new File("data", fileNames[f]));
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length <= phoneColumns[f] || row[1].equals(FileMenu.EMPTY_DOMAIN)) {
                    continue;
                }
                if (row[phoneColumns[f]].equals(phone) && !row[0].equals(ignoreID)) {
                    return true;
                }
            }
        }
        return false;
    }

    //HELPER METHODS
    //Check date of birth text: must be yyyy-MM-dd with numbers only, a real date (no 2023-02-30), not in the future, age between minimum age and 100
    public LocalDate checkDob(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Date of birth cannot be empty");
        }
        text = text.trim();
        if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Date of birth must be in yyyy-MM-dd with numbers only (e.g. 2000-01-31)");
        }
        LocalDate dob;
        try {
            dob = LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date of birth is not a real date (check the month and day)");
        }
        if (dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < getMinAge()) {                    //OBJECT ORIENTED: getMinAge() is different for staff and student (polymorphism)
            throw new IllegalArgumentException("Must be at least " + getMinAge() + " years old");
        }
        if (age > MAX_AGE) {
            throw new IllegalArgumentException("Date of birth is too old, please check the year");
        }
        return dob;
    }

    //===================================================================================================================================
    //                                                  OTHER HELPER METHODS
    //===================================================================================================================================
    //HELPER METHODS
    //If this user was deleted by someone else while still logged in, stop every action
    protected void checkCurrentUserActive() {
        File ownFile = new File("data", FileMenu.ADMIN);
        if (userRole.equals("Managers")) {
            ownFile = new File("data", FileMenu.MANAGER);
        }
        List<String[]> rows = CRUD.readFile(ownFile);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length > 1 && row[0].equals(userID) && !row[1].equals(FileMenu.EMPTY_DOMAIN)) {
                return;
            }
        }
        throw new SecurityException("Your account is no longer active. Please log in again.");
    }

    //HELPER METHODS
    //Write one line into Admin_record.txt. Used by add, update and delete (staff and student)
    //For ADD and DELETE: fieldChanged, oldValue, newValue are all N/A. Only UPDATE has real values
    //OBJECT ORIENTED: the parameter is User, so a StaffAccount, InstructorAccount and StudentAccount can all be used here (polymorphism)
    protected void logAction(String actionType, User account, String field, String oldValue, String newValue) {
        String recordID = IDGenerator.generateNextID(logFile, "REC");
        String[] row = {recordID, actionType, account.getId(), account.getName(), account.getRole(), userID,
                        LocalDate.now().toString(), field, oldValue, newValue};
        WriteFunction.logChange(userRole, row);
    }
}

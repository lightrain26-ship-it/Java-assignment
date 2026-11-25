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
//Inheritance: ManageStaff extends ManageAccount, so it can use checkName, checkPhone, checkDob, logAction, etc. without writing them again
//The Manager (friend's part) can also use this class: new ManageStaff("MAN001", "Managers")  (a Manager can only manage Administrator)
public class ManageStaff extends ManageAccount {
    //Options for the Role dropdown (JComboBox) in GUI
    public static final String[] ROLES = {"Instructor", "FacilitiesOfficer", "AttendanceOfficer", "Manager", "Administrator"};

    private static final int MIN_AGE = 18;          //Minimum age for staff
    private static final String ID_PATTERN = "(INS|FAC|ATT|MAN|ADM)\\d{3}";

    //CONSTRUCTOR
    //Save the user that is logged in (ID and role: "Administrators" or "Managers") by sending them to the parent class
    public ManageStaff(String userID, String userRole) {
        super(userID, userRole);
    }

    //OBJECT ORIENTED
    //Method overriding: the parent has an abstract getMinAge(), staff must be at least 18
    @Override
    protected int getMinAge() {
        return MIN_AGE;
    }

    //Stop other roles from accessing this service
    public void checkRoleAllowed(String role) {
        if (!userRole.equals("Administrators") && !userRole.equals("Managers")) {
            throw new SecurityException("Access denied. Only Administrators and Managers can access this service");
        }
        if (userRole.equals("Managers") && !role.equals("Administrator")) {
            throw new SecurityException("Access denied. Managers can only manage Administrator accounts");
        }
        checkCurrentUserActive();
    }

    //The roles that can be shown in the Role dropdown (a Manager can only see Administrator)
    public String[] getAllowedRoles() {
        if (userRole.equals("Managers")) {
            return new String[]{"Administrator"};
        }
        return ROLES;
    }

    //===================================================================================================================================
    //                                                  HELPER METHODS
    //===================================================================================================================================
    //HELPER METHODS
    //Check the staff ID format (e.g. ADM001) and change it to upper case. The 3 letters must belong to a staff role
    private String checkStaffID(String staffID) {
        if (staffID == null || staffID.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty");
        }
        staffID = staffID.trim().toUpperCase();
        if (!staffID.matches(ID_PATTERN)) {
            throw new IllegalArgumentException("Invalid staff ID. Use INS, FAC, ATT, MAN or ADM followed by 3 digits (e.g. ADM001)");
        }
        return staffID;
    }

    //HELPER METHODS
    //Return the txt file of a role (used by add, update, delete)
    private File getFileByRole(String role) {
        switch (role) {
            case "Instructor":
                return new File("data", FileMenu.INSTRUCTOR);
            case "FacilitiesOfficer":
                return new File("data", FileMenu.FACILITYOFFICE);
            case "AttendanceOfficer":
                return new File("data", FileMenu.ATTENDANCEOFFICE);
            case "Manager":
                return new File("data", FileMenu.MANAGER);
            case "Administrator":
                return new File("data", FileMenu.ADMIN);
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    //HELPER METHODS
    //Find out the role from the first 3 letters of the ID (ADM001 -> Administrator)
    private String getRoleByID(String staffID) {
        switch (staffID.substring(0, 3)) {
            case "INS":
                return "Instructor";
            case "FAC":
                return "FacilitiesOfficer";
            case "ATT":
                return "AttendanceOfficer";
            case "MAN":
                return "Manager";
            default:
                return "Administrator";
        }
    }

    //HELPER METHODS
    //Change one txt row into a StaffAccount. Return null if the row is broken (wrong number of columns, wrong ID/role, wrong date...)
    private StaffAccount rowToStaff(String[] row, int columns) {
        if (row.length != columns || !row[0].matches(ID_PATTERN) || !row[2].equals(getRoleByID(row[0]))) {
            return null;
        }
        try {
            if (row[2].equals("Instructor")) {
                return InstructorAccount.fromInstructorRow(row);
            }
            return StaffAccount.fromRow(row);
        } catch (RuntimeException e) {
            return null;            //for example the date is not a real date
        }
    }

    //HELPER METHODS
    //Check largest ID number for certain prefix in role txt file and Admin_record.txt 
    private String nextStaffID(File targetFile, String prefix) {
        int maxNumber = 0;

        maxNumber = Math.max(maxNumber, findMaxNumberForPrefix(CRUD.readFile(targetFile), prefix));

        if (logFile != null && logFile.exists()) {
            maxNumber = Math.max(maxNumber, findMaxNumberForPrefix(CRUD.readFile(logFile), prefix));
        }

        return prefix + String.format("%03d", maxNumber + 1);
    }

    //HELPER METHODS
    //Scan all cell from rows to search for roleID
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
    //Create Object ManageStaff directly from email currently logged in
    public static ManageStaff fromLoginEmail(String email, String userRole) {
        String loggedInID = extractUserIDFromEmail(email);
        return new ManageStaff(loggedInID, userRole);
    }

    //===================================================================================================================================
    //                                                  ADD STAFF
    //===================================================================================================================================
    //Specify txt file, ID prefix, password and Instructor for each role. dob is the text typed by admin (yyyy-MM-dd)
    public StaffAccount addStaff(String role, String name, String phone, String dob, String gender, String specialty, String grade) {
        String prefix;
        String passwordWord;
        boolean isInstructor = false;

        role = checkOption(role, ROLES, "Role");

        //Check if the role is allowed to be added by the current user
        checkRoleAllowed(role);

        File targetFile = getFileByRole(role);
        switch (role) {
            case "Instructor":
                prefix = "INS";
                passwordWord = "instructor";
                isInstructor = true;
                break;
            case "FacilitiesOfficer":
                prefix = "FAC";
                passwordWord = "facilities";
                break;
            case "AttendanceOfficer":
                prefix = "ATT";
                passwordWord = "attendance";
                break;
            case "Manager":
                prefix = "MAN";
                passwordWord = "manager";
                break;
            default:
                prefix = "ADM";
                passwordWord = "admin";
                break;
        }

        //Check input values (every value typed by admin)
        name = checkName(name);
        phone = checkPhone(phone, "");
        LocalDate dobDate = checkDob(dob);
        gender = checkGender(gender);
        if (isInstructor) {
            specialty = checkSpecialty(specialty);
            grade = checkGrade(grade);
        }

        //Prepare the values that's not typed by admin
        String id = nextStaffID(targetFile, prefix);
        String email = IDGenerator.generateEmail(id);
        String password = passwordWord + id.substring(3);

        //OBJECT ORIENTED
        //Polymorphism: both InstructorAccount and StaffAccount are a StaffAccount, so the variable "staff" can keep either one
        StaffAccount staff;
        if (isInstructor) {
            staff = new InstructorAccount(id, password, name, email, phone, dobDate, gender, specialty, grade, LocalDate.now());
        } else {
            staff = new StaffAccount(role, id, password, name, email, phone, dobDate, gender, LocalDate.now());
        }

        //Save to txt file
        CRUD.addRecord(userRole, targetFile, staff.toFileRow());
        logAction("ADD", staff, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN); //Log the ADD to Admin_record.txt
        return staff;
    }

    //===================================================================================================================================
    //                                                  VIEW STAFF
    //===================================================================================================================================
    //HELPER METHODS
    //Check userRole for searching and viewing txt file
    public File[] getManagedFiles() {
        if (!userRole.equals("Administrators") && !userRole.equals("Managers")) {
            throw new SecurityException("Access denied. Only Administrators and Managers can access this service");
        }
        checkCurrentUserActive();
        if (userRole.equals("Managers")) {
            return new File[]{new File("data", FileMenu.ADMIN)};
        }
        return new File[]{
                new File("data", FileMenu.INSTRUCTOR),
                new File("data", FileMenu.FACILITYOFFICE),
                new File("data", FileMenu.ATTENDANCEOFFICE),
                new File("data", FileMenu.MANAGER),
                new File("data", FileMenu.ADMIN)
        };
    }

    //View all ACTIVE staff. Deleted staff (N/A) are skipped. A broken line is ignored (and counted), the other lines are still read
    public List<StaffAccount> getAllStaff() {
        List<StaffAccount> allStaff = new ArrayList<>();
        brokenLines = 0;

        for (File file : getManagedFiles()) {
            List<String[]> rows = CRUD.readFile(file);
            if (rows.isEmpty()) {
                continue;
            }
            int columns = rows.get(0).length;
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length > 1 && row.length == columns && row[1].equals(FileMenu.EMPTY_DOMAIN)) {
                    continue; //Skip deleted staff
                }
                StaffAccount staff = rowToStaff(row, columns);
                if (staff == null) {
                    brokenLines++;
                } else {
                    allStaff.add(staff);
                }
            }
        }
        return allStaff;
    }

    //View only one role (e.g. all Instructors), active only
    public List<StaffAccount> getStaffByRole(String role) {
        role = checkOption(role, ROLES, "Role");
        checkRoleAllowed(role);
        List<StaffAccount> results = new ArrayList<>();
        for (StaffAccount staff : getAllStaff()) {
            if (staff.getRole().equals(role)) {
                results.add(staff);
            }
        }
        return results;
    }

    //HELPER METHODS
    //Find one active staff by ID, or throw an error if the ID does not exist / already deleted. Used by update and delete
    public StaffAccount findActiveStaff(String staffID) {
        staffID = checkStaffID(staffID);
        String role = getRoleByID(staffID);
        checkRoleAllowed(role);

        List<String[]> rows = CRUD.readFile(getFileByRole(role));
        int columns = rows.get(0).length;
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length > 1 && row[0].equals(staffID) && !row[1].equals(FileMenu.EMPTY_DOMAIN)) {
                StaffAccount staff = rowToStaff(row, columns);
                if (staff == null) {
                    throw new IllegalStateException("The record of " + staffID + " in the txt file is broken. Please fix the file first");
                }
                return staff;
            }
        }
        throw new NoSuchElementException("No active staff with ID: " + staffID);
    }

    //===================================================================================================================================
    //                                       SEARCH STAFF
    //===================================================================================================================================
    //Returns every ACTIVE staff where the keyword exist. An empty list means "keyword not found"
    //roleFilter is the Role dropdown beside the search box: use "All roles" (or null) to search every role
    public List<StaffAccount> searchStaff(String keyword, String roleFilter) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        keyword = keyword.trim();

        boolean allRoles = roleFilter == null || roleFilter.trim().isEmpty() || roleFilter.equalsIgnoreCase("All roles");
        if (!allRoles) {
            roleFilter = checkOption(roleFilter, ROLES, "Role");
            checkRoleAllowed(roleFilter);
        }

        List<StaffAccount> results = new ArrayList<>();
        for (StaffAccount staff : getAllStaff()) {
            if (!allRoles && !staff.getRole().equals(roleFilter)) {
                continue;
            }
            if (staff.matches(keyword)) {
                results.add(staff);
            }
        }
        return results;
    }

    //OBJECT ORIENTED
    //Method overloading: same name searchStaff, but this one has only the keyword and searches in every role
    public List<StaffAccount> searchStaff(String keyword) {
        return searchStaff(keyword, "All roles");
    }

    //===================================================================================================================================
    //                                                  UPDATE STAFF
    //===================================================================================================================================
    //ID, role, password, email and date joined cannot be changed. specialty and grade are only used when the staff is an Instructor
    public StaffAccount updateStaff(String staffID, String name, String phone, String dob, String gender, String specialty, String grade) {
        StaffAccount oldStaff = findActiveStaff(staffID);   //error here if the ID does not exist
        boolean isInstructor = oldStaff.getRole().equals("Instructor");

        //Check input values (same rules as add)
        name = checkName(name);
        phone = checkPhone(phone, oldStaff.getId());        //its own phone number is allowed
        LocalDate dobDate = checkDob(dob);
        gender = checkGender(gender);

        //Build the new object with the same ID, password, email and date joined
        StaffAccount newStaff;
        if (isInstructor) {
            specialty = checkSpecialty(specialty);
            grade = checkGrade(grade);
            newStaff = new InstructorAccount(oldStaff.getId(), oldStaff.getPassword(), name, oldStaff.getEmail(),
                    phone, dobDate, gender, specialty, grade, oldStaff.getDateJoined());
        } else {
            newStaff = new StaffAccount(oldStaff.getRole(), oldStaff.getId(), oldStaff.getPassword(), name,
                    oldStaff.getEmail(), phone, dobDate, gender, oldStaff.getDateJoined());
        }

        //Compare the old row and new row, if they are the same then nothing is updated
        String[] oldRow = oldStaff.toFileRow();
        String[] newRow = newStaff.toFileRow();
        if (Arrays.equals(oldRow, newRow)) {
            throw new IllegalStateException("Nothing has been changed.");
        }

        //Save to txt file
        File file = getFileByRole(oldStaff.getRole());
        CRUD.updateRecord(userRole, file, newRow);

        //Log every changed field as one line in Admin_record.txt (the field name is taken from the txt header)
        String[] header = CRUD.readFile(file).get(0);
        for (int i = 0; i < newRow.length; i++) {
            if (!oldRow[i].equals(newRow[i])) {
                logAction("UPDATE", newStaff, header[i], oldRow[i], newRow[i]);
            }
        }
        return newStaff;
    }

    //===================================================================================================================================
    //                                                  DELETE STAFF
    //===================================================================================================================================
    //Find the staff to delete, and stop if it is the account that is logged in. Call this BEFORE asking "are you sure?"
    public StaffAccount checkDeletable(String staffID) {
        StaffAccount staff = findActiveStaff(staffID);      //error here if the ID does not exist
        if (staff.getId().equals(userID)) {
            throw new IllegalStateException("You cannot delete your own account.");
        }
        return staff;
    }

    //delete everything in the row
    public StaffAccount deleteStaff(String staffID) {
        StaffAccount staff = checkDeletable(staffID);
        CRUD.deleteRecord(staff.getId(), userRole, getFileByRole(staff.getRole()));
        logAction("DELETE", staff, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN);
        return staff;
    }
}

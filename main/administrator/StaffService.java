package administrator;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import common_code.WriteFunction;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffService {
    private String adminID; //Store adminID currently log in
    //File that's handled from ManageStaff
    private File fileInstructor = new File("data", FileMenu.INSTRUCTOR);
    private File fileFacilities = new File("data", FileMenu.FACILITYOFFICE);
    private File fileAttendance = new File("data", FileMenu.ATTENDANCEOFFICE);
    private File fileManager = new File("data", FileMenu.MANAGER);
    private File fileAdministrator = new File("data", FileMenu.ADMIN);

    //Store adminID currently logging in
    public StaffService(String adminID) {
        this.adminID = adminID;
    }

    //============================================================================
    //ADD STAFF
    //============================================================================
    public void addStaff(String role, String name, String phone, LocalDate dob, String gender, String specialty, String grade) {
        File targetFile;
        String prefix;
        boolean isInstructor = false;       //flag if the role is Instructor

        //Determine the file, prefix and role
        if(role.equals("Instructor")) {
            targetFile = fileInstructor;
            prefix = "INS";
            isInstructor = true;
        } else if(role.equals("Facility Office")) {
            targetFile = fileFacilities;
            prefix = "FAC";
        } else if(role.equals("Attendance Office")) {
            targetFile = fileAttendance;
            prefix = "ATT";
        } else if(role.equals("Manager")) {
            targetFile = fileManager;
            prefix = "MAN";
        } else if(role.equals("Administrator")) {
            targetFile = fileAdministrator;
            prefix = "ADM";
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        try {
            //Generate data
            String staffID = IDGenerator.generateNextID(targetFile, prefix);
            String email = IDGenerator.generateEmail(staffID);
            String password = UserBuilder.generatePassword(role, staffID);

            //Select a different row if Instructor = true
            String[] newRow;
            if (isInstructor) {
                newRow = UserBuilder.buildStudInsRow(staffID, password, role, name, email, phone, dob, gender, specialty, grade);
            } else {
                newRow = UserBuilder.buildStaffRow(staffID, password, role, name, email, phone, dob, gender);
            }

            //Save to txt file
            CRUD.addRecord("Administrator", targetFile, newRow);

            //Record the action for record.txt
            File recordFile = new File("data", FileMenu.ADMINRECORD);
            //Generate RecordID
            String recordID = IDGenerator.generateNextID(recordFile, "REC");
            String[] recordRow = {recordID, "ADD", staffID, name, role, this.adminID, LocalDate.now().toString(), "N/A", "N/A", "N/A"};

            //Save to Admin_record.txt
            WriteFunction.logChange("Administrators", recordRow);   

            //print success message
            System.out.println("SUCCESS: Staff added successfully. Staff ID: " + staffID);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to add staff. " + e.getMessage());
            throw e;
        }
    }

    //============================================================================
    //SEARCH STAFF
    //============================================================================
    public List<String[]> searchStaff(String keyword) {
        List<String[]> searchResult = new ArrayList<>();
        String search = keyword.toLowerCase().trim();

        if(search.isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }

        try {
            searchFile(fileInstructor, search, searchResult);
            searchFile(fileFacilities, search, searchResult);
            searchFile(fileAttendance, search, searchResult);
            searchFile(fileManager, search, searchResult);
            searchFile(fileAdministrator, search, searchResult);
            return searchResult;
        } catch (Exception e) {
            System.err.println("Search Failed: " + e.getMessage());
            throw e;
        }
    }

    //SearchFile Method
    private void searchFile(File file, String keyword, List<String[]> searchResult) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file.getName());
        }

        List<String[]> data = CRUD.readFile(file);

        for(int i = 1; i < data.size(); i++) {
            String[] record = data.get(i);
            //Ignore Row with missing value except ID
            if(record[1].equals(FileMenu.EMPTY_DOMAIN)) {
                continue; // Skip empty records
            }

            //Check all columns for the keyword
            for(String value : record) {
                if(value.toLowerCase().contains(keyword)) {
                    searchResult.add(record);
                    break; // Break inner loop to avoid duplicate entries
                }
            }
        }
        

    }


}
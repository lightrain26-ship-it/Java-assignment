package facility_officer;

import java.time.LocalDate;
import common_code.CRUD;
import common_code.FileMenu;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class issue {
    private final String issueId;
    private final String roomId;
    private final String equipmentId;
    private int affected;
    private final String reportedBy;
    private final LocalDate dateReported;
    private String status;
    private final String briefDescription;

    // Constructor
    public issue(String issueId, String roomId, String equipmentId, int affected, String reportedBy, LocalDate dateReported, String status, String briefDescription) {
        if (affected < 0) {
            throw new IllegalArgumentException("Affected quantity cannot be negative");
        }

        if (dateReported == null){
            throw new IllegalArgumentException("Date cannot be empty");
        }

        String text = briefDescription.trim();

        if (text.length() < 5 || text.length() > 70) {
            throw new IllegalArgumentException("Brief description must be between 5 to 70 characters.");
        }
        for (int b = 0; b < text.length(); b++) {
            char ch = text.charAt(b);
            if (!Character.isLetter(ch) && ch != ' ') {
                throw new IllegalArgumentException("Description can only have letters and spaces.");
            }
        }

        this.issueId = issueId;
        this.roomId = roomId;
        this.equipmentId = equipmentId;
        this.affected = affected;
        this.reportedBy = reportedBy;
        this.dateReported = dateReported;
        setStatus(status);  // Validate and set status
        this.briefDescription = briefDescription.trim();
    }

    // Getters
    public String getIssueId() {
        return issueId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }   

    public int getAffected() {
        return affected;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public LocalDate getDateReported() {
        return dateReported;
    }

    public String getStatus() {
        return status;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    // Setter
    public void setStatus(String status) {
        if ("In Progress".equals(status) || "Resolved".equals(status) || "Disposed".equals(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Status must be either 'In Progress', 'Resolved', or 'Disposed'");
        }
    }

    // Turn it into a row of String
    public String[] issueRows() {
        return new String[]{
            issueId, 
            roomId, 
            equipmentId, 
            String.valueOf(affected), // Convert int to String
            reportedBy, 
            dateReported.toString(), // turn the date into a String
            status, 
            briefDescription};
    }

    // ---------------------------------------------------------------------------------------------------
    // Read the issue.txt file and turn each row into an object
    public static List<issue> loadIssue() {
        File issueFile = new File("data", FileMenu.ISSUE);
        
        List<String[]> row = CRUD.readFile(issueFile);
        List<issue> issueList = new ArrayList<>();
    
        for (int i = 1; i < row.size(); i++) {
        // Get the current line of the file as an array of strings
        String[] data = row.get(i);

        int affected = Integer.parseInt(data[3]);
        LocalDate dateReported = LocalDate.parse(data[5]); // Example: This string "2023-08-15" will become a date that java can read
        issue is = new issue(data[0], data[1], data[2],affected, data[4], dateReported, data[6], data[7]);
        issueList.add(is);
        }
        
        return issueList;  
    }

    public static List<issue> viewissue() {
        return loadIssue();
    }

    public static List<issue> searchIssue(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be empty");
        }

        String searchKeyword = keyword.trim().toLowerCase();
        List<issue> searchResults = new ArrayList<>();

        // For ID, reportedby, and affected must match exactly
        // Date search starts from the beginning: year "2026", month "2026-10", or a full date "2026-10-13"
        // Status and description search can be partial
        for (issue is : loadIssue()) {
            if (is.getIssueId().toLowerCase().equals(searchKeyword) ||
                is.getRoomId().toLowerCase().equals(searchKeyword) ||
                is.getEquipmentId().toLowerCase().equals(searchKeyword) ||
                String.valueOf(is.getAffected()).equals(searchKeyword) || //Changed int to string for comparison
                is.getReportedBy().toLowerCase().equals(searchKeyword) ||
                is.getDateReported().toString().startsWith(searchKeyword) || //Changed the date into string
                is.getStatus().toLowerCase().contains(searchKeyword) ||
                is.getBriefDescription().toLowerCase().contains(searchKeyword)) {
                searchResults.add(is);
            }
        }
        return searchResults;
    }

    // Helper: To find the issue by its exact ID or it will return null if it doesn't exist
    public static issue findIssue(String issueId) {
        for (issue is : loadIssue()) {
            if (is.getIssueId().equals(issueId)) {
                return is;
            }
        }
        return null; // Return null if no matching issue is found
    }

    // Change the "In Progress" status to "Resolved"
    public static issue updateIssue(String issueId, String newStatus) {
        File isFile = new File("data", FileMenu.ISSUE);

        issue iss = findIssue(issueId);

        if (iss == null) {
            throw new IllegalArgumentException(issueId + " not found.");
        }

        // A "Resolved" and "Disposed" issue cannot be changed again
        if(!"In Progress".equals(iss.getStatus())) {
            throw new IllegalStateException(issueId + " is already " + iss.getStatus() +  " and it cannot be changed.");
        }

        if (!"Resolved".equals(newStatus) && !"Disposed".equals(newStatus)) {
            throw new IllegalArgumentException("New status must be 'Resolved' or 'Disposed'.");
        }

        int roomQty = 0;

        if ("Disposed".equals(newStatus)) {
            if ("-".equals(iss.getEquipmentId())){
                throw new IllegalArgumentException("A room problem can only be resolved.");
            }

            // Find how many equipments are in that room
            roomEquipment.loadRoomEquipment();
            roomQty = -1;
            for (roomEquipment r : roomEquipment.getRoomsForEquipment(iss.getEquipmentId())) {
                if (r.getRoomId().equals(iss.getRoomId())) {
                    roomQty = r.getQty();
                }
            }

            if (roomQty < iss.getAffected()) {
                throw new IllegalArgumentException("The room doesn't have enough quantity to dispose.");
            }
        }

        // Lower down the room quantity and the equipment total quantity
        if ("Disposed".equals(newStatus)) {
            roomEquipment.updateRoomQty(iss.getRoomId(), iss.getEquipmentId(), roomQty - iss.getAffected());
        }

        iss.setStatus(newStatus);
        CRUD.updateRecord("Facilities Officers", isFile, iss.issueRows());
        return iss;
    }
}
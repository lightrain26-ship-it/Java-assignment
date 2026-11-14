package facility_officer;

import common_code.CRUD;
import common_code.FileMenu;
import java.time.LocalDate;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class issuelogic {
    // Read the issue.txt file and turn each row into an object
    public static List<issue> Loadissue() {
        File issueFile = new File("data", FileMenu.ISSUE);
        
        List<String[]> row = CRUD.readFile(issueFile);
        List<issue> issueList = new ArrayList<>();
    
        for (int i = 1; i < row.size(); i++) {
        // Get the current line of the file as an array of strings
        String[] Data = row.get(i);

        int affected = Integer.parseInt(Data[3]);
        LocalDate dateReported = LocalDate.parse(Data[5]); // Example: This string "2023-08-15" will become a date that java can read
        issue is = new issue(Data[0], Data[1], Data[2],affected, Data[4], dateReported, Data[6], Data[7]);
        issueList.add(is);
        }
        
        return issueList;  
    }

    public static List<issue> viewissue() {
        return Loadissue();
    }

    public static List<issue> searchIssue(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be empty");
        }

        String searchKeyword = keyword.trim().toLowerCase();
        List<issue> searchResults = new ArrayList<>();

        // For ID, reportedby, and affected must match exactly
        // Date must begin with a  year "2026", month "2026-10", or a full date "2026-10-13"
        // Status and description search can be partial
        for (issue is : Loadissue()) {
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
        for (issue is : Loadissue()) {
            if (is.getIssueId().equals(issueId)) {
                return is;
            }
        }
        return null; // Return null if no matching issue is found
    }

    // Change the "In Progress" status to "Resolved"
    public static issue updateIssue(String issueId) {
        File isFile = new File("data", FileMenu.ISSUE);

        issue iss = findIssue(issueId);

        if (iss == null) {
            throw new IllegalArgumentException(issueId + " not found.");
        }

        // A "Resolved" issue cannot be changed again
        if("Resolved".equals(iss.getStatus())) {
            throw new IllegalStateException(issueId + " has already been resolved.");
        }

        iss.setStatus("Resolved");

        CRUD.updateRecord("Facilities Officers", isFile, iss.issueRows());

        return iss;
    }
}

package instructor;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import facility_officer.issue;
import facility_officer.roomEquipment; 
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class facilityProblem {
    // HELPER METHOD: To find how many equipments in one room are "In Progress"
    private static int findAffected(String roomId, String equipmentId, String excludeIssueId) {
        int totalA = 0;

        for (issue is : issue.loadIssue()) {
            if (is.getRoomId().equals(roomId) && is.getEquipmentId().equals(equipmentId)
                && "In Progress".equals(is.getStatus()) && !is.getIssueId().equals(excludeIssueId)) {
                totalA += is.getAffected();
            }
        }
        return totalA;
    }

    public static issue addComplaint(String roomId, String equipmentId, int affected, String reportedBy, String description) {
        if (reportedBy == null || reportedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID cannot be empty.");
        }

        // The room must exist
        roomEquipment.loadRoomEquipment();
        boolean roomExists = false;

        for (roomEquipment r : roomEquipment.getRoomRow()) {
            if (r.getRoomId().equals(roomId)) {
                roomExists = true;
            }
        }

        if (!roomExists) {
            throw new IllegalArgumentException("Room not found.");
        }

        int roomAffected = 0; // A room problem always has "0" affected

        if (!"-".equals(equipmentId)) {
            int roomQty = -1;
            for (roomEquipment re : roomEquipment.getRoomsForEquipment(equipmentId)) {
                if (re.getRoomId().equals(roomId)) {
                    roomQty = re.getQty();
                }
            }

            if (roomQty < 0) {
                throw new IllegalArgumentException("This equipment is not in the room.");
            }
            if (affected < 1) {
                throw new IllegalArgumentException("Affected quantity must be at least 1.");
            }
            if (affected + findAffected(roomId, equipmentId, "") > roomQty) {
                throw new IllegalArgumentException("Affected quantity is more than the one in the room.");
            }

            roomAffected = affected;
        }

        File iFile = new File("data", FileMenu.ISSUE);
        String newId = IDGenerator.generateNextID(iFile, "IS");

        // The issue constructor checks the description, and the status starts as "In Progress"
        issue newIssue = new issue(newId, roomId, equipmentId, roomAffected, reportedBy.trim(), LocalDate.now(), "In Progress", description);

        CRUD.addRecord("Instructors", iFile, newIssue.issueRows());
        return newIssue;
    }

    // Instructor view their own issues
    public static List<issue> viewComplaint(String instructorId) {
        if (instructorId == null || instructorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID cannot be empty.");
        }

        List<issue> view = new ArrayList<>();
        for (issue is : issue.loadIssue()) {
            if (is.getReportedBy().equals(instructorId.trim())) {
                view.add(is);
            }
        }
        return view;
    }

    // Instructor search their own issues
    public static List<issue> searchComplaint(String instructorId, String key) {
        if (instructorId == null || instructorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID cannot be empty.");
        }

        List<issue> show = new ArrayList<>();
        for (issue sh : issue.searchIssue(key)) {
            if (sh.getReportedBy().equals(instructorId.trim())) {
                show.add(sh);
            }
        }
        return show;
    }

    // HELPER METHOD: To find how many of one equipment are in the room
    private static int findRoomQty(String roomId, String equipmentId) {
        roomEquipment.loadRoomEquipment();

        for (roomEquipment re : roomEquipment.getRoomsForEquipment(equipmentId)) {
            if (re.getRoomId().equals(roomId)) {
                return re.getQty();
            }
        }
        return -1;
    }

    public static issue updateComplaint(String instructorId, String issueId, int newAffected, String newDescription) {
        if (instructorId == null || instructorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID cannot be empty.");
        }

        issue old = issue.findIssue(issueId);
        if (old == null) {
            throw new IllegalArgumentException("Issue ID not found");
        }
        if (!old.getReportedBy().equals((instructorId.trim()))) {
            throw new IllegalArgumentException("Sorry, you cannot update other instructor reports.");
        }
        if (!"In Progress".equals(old.getStatus())) {
            throw new IllegalArgumentException("Only an 'In Progress' report can be updated.");
        }
    
        int save = 0;

        if (!"-".equals(old.getEquipmentId())) {
            if (newAffected < 1) {
                throw new IllegalArgumentException("Affected quantity must be at least 1");
            }
            
            int roomQty = findRoomQty(old.getRoomId(), old.getEquipmentId());
        
            if (newAffected + findAffected(old.getRoomId(), old.getEquipmentId(), issueId) > roomQty) {
                throw new IllegalArgumentException("Affected quantity is more than the one in the room.");
            }
            save = newAffected;
        }

        issue updated = new issue(old.getIssueId(), old.getRoomId(), old.getEquipmentId(), save, old.getReportedBy(), old.getDateReported(), old.getStatus(), newDescription);
        
        CRUD.updateRecord("Instructors", new File("data", FileMenu.ISSUE), updated.issueRows());
        return updated;
    }

    public static void deleteComplaint(String instructorId, String issueId) {
        if (instructorId == null || instructorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor ID cannot be empty.");
        }
        
        issue report = issue.findIssue(issueId);

        if (report == null) {
            throw new IllegalArgumentException("Issue ID not found.");
        }
        if (!report.getReportedBy().equals(instructorId.trim())) {
            throw new IllegalArgumentException("Sorry, you cannot delete other instructor reports!");
        }
        if (!"In Progress".equals(report.getStatus())) {
            throw new IllegalArgumentException("Only an 'In Progress' report can be deleted.");
        }

        File delFile = new File("data", FileMenu.ISSUE);
        CRUD.deleteRecord(issueId, "Instructors", delFile);
    }
}
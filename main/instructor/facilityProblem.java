package instructor;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import facility_officer.issue;
import facility_officer.roomEquipment;

import java.io.File;
import java.time.LocalDate;

public class facilityProblem {

    // HELPER METHOD: Find how many equipments in one room are "In Progress"
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

    public static issue addIssue(String roomId, String equipmentId, int affected, String reportedBy, String description) {
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
}
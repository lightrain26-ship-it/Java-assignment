package facility_officer;

import common_code.FileMenu;
import common_code.IDGenerator;
import common_code.WriteFunction;
import java.io.File;
import java.time.LocalDate;
import java.util.List;


// The actions of the logged-in Facilities Officer. Also writes the history rows into Facilities_record.txt.
public class officerHistory {
    private static final String ROLE = "Facilities Officers";
    private static final File LOG_FILE = new File("data", FileMenu.FACILITIESRECORD);
    private static final String NA = "N/A";
    private final String officerId; // Facilities officer who logged in to the system

    // Constructor
    public officerHistory(String officerId) {
        if (officerId == null || officerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer ID cannot be empty.");
        }
        this.officerId = officerId.trim();
    }

    // --------------------HELPER METHODS----------------------------------------------------------------
    // Helper: To writes the history row
    private void log(String actionType, String targetType, String targetId, String targetName,
                    String fieldChanged, String oldValue, String newValue) {
        String logId = IDGenerator.generateNextID(LOG_FILE, "LOG");
        String[] rows = {
            logId,
            actionType,
            targetType,
            targetId,
            targetName,
            officerId,
            LocalDate.now().toString(),
            fieldChanged,
            oldValue,
            newValue
        };
        WriteFunction.logChange(ROLE, rows);
    }

    // Helper: finds the equipment by its ID
    private static equipment findEquipment(String id) {
        for (equipment e : equipment.loadequipment()) {
            if (e.getEquipmentId().equals(id)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Equipment not found!");
    }

    // Helper: finds the row of the equipment in the room
    private static roomEquipment findRoom(String roomId, String equipmentId) {
        roomEquipment.loadRoomEquipment();
        for (roomEquipment r : roomEquipment.getRoomsForEquipment(equipmentId)) {
            if (r.getRoomId().equals(roomId)) {
                return r;
            }
        }
        throw new IllegalArgumentException("This equipment is not in the room");
    }

    // Helper: the name shown for a room stock row, e.g. "Floor Mat in Karate Dojo"
    private static String stockName(roomEquipment r) {
        return findEquipment(r.getEquipmentId()).getEquipmentName() + " in " + r.getRoomName();
    }

    // ---------------------------------------------------------------------------------
    //Equipment
    //ADD (the rooms are part of the add, so there is one row)
    public equipment addEquipment(String id, String name, int totalQty, int minQty, String category, List<roomEquipment> assign) {
        equipment added = equipment.addEquipment(id, name, totalQty, minQty, category, assign);
        log("ADD", "Equipment", id, added.getEquipmentName(), NA, NA, NA);
        return added;
    }

    //UPDATE (one row for each field that changed)
    public equipment updateEquipment(String id, String newName, int newMinQty, String newCategory) {
        // Store the current value before updating
        equipment before = findEquipment(id);

        // Update the equipment, then read back what was really saved
        equipment after = equipment.updateEquipment(id, newName, newMinQty, newCategory);
        String fresh = after.getEquipmentName();

        // If the name is not the same as the new one, record it
        if (!before.getEquipmentName().equals(fresh)) {
            log("UPDATE", "Equipment", id, fresh, "name", before.getEquipmentName(), fresh);
        }

        // If the minimum quantity is not the same as the new one, record it
        if (before.getMinQty() != after.getMinQty()) {
            log("UPDATE", "Equipment", id, fresh, "minQty",
                String.valueOf(before.getMinQty()), String.valueOf(after.getMinQty()));
        }

        // if the category is not the same as the new one, record it
        if (!before.getCategory().equals(after.getCategory())) {
            log("UPDATE", "Equipment", id, fresh, "category", before.getCategory(), after.getCategory());
        }
        return after;
    }

    //DELETE (the equipment name is read first to store it temporarily, because the equipment will be deleted)
    public void deleteEquipment(String id) {
        equipment current = findEquipment(id);
        equipment.deleteEquipment(id);
        log("DELETE", "Equipment", id, current.getEquipmentName(), NA, NA, NA);
    }

    // RoomEquipment: UPDATE (quantity of one equipment in one room)
    public void updateRoomQty(String roomId, String equipmentId, int newQty) {
        roomEquipment before = findRoom(roomId, equipmentId);
        int oldQty = before.getQty();
        String label = stockName(before);

        roomEquipment.updateRoomQty(roomId, equipmentId, newQty);

        if (oldQty != newQty) {
            log("UPDATE", "RoomEquipment", roomId + "-" + equipmentId, label, "quantity",
                String.valueOf(oldQty), String.valueOf(newQty));
        }
    }

    // Issue: UPDATE (In Progress to Resolved or Disposed)
    public issue updateIssue(String issueId, String newStatus) {
        issue bf = issue.findIssue(issueId);
        if (bf == null) {
            throw new IllegalArgumentException(issueId + " not found.");
        }

        issue updated = issue.updateIssue(issueId, newStatus);
        log("UPDATE", "Issue", issueId, bf.getBriefDescription(), "status", bf.getStatus(), updated.getStatus());
        return updated;
    }
}
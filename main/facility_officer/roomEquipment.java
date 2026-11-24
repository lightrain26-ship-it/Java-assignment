package facility_officer;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.WriteFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class roomEquipment {
    private final String roomId;
    private final String roomName;
    private final String equipmentId;
    private int qty;

    private static final File ROOM_FILE = new File("data", FileMenu.ROOMEQUIPMENT);
    private static final File EQUIPMENT_FILE = new File("data", FileMenu.EQUIPMENT);
    private static final String ROLE = "Facilities Officers";

    private static ArrayList<roomEquipment> rmRows = new ArrayList<>();

    // Constructor
    public roomEquipment(String roomId, String roomName, String equipmentId, int qty) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be blank.");
        }

        String rName = roomName.trim();

        for (int n = 0; n < rName.length(); n++) {
            char chr = rName.charAt(n);
            if (!Character.isLetter(chr) && chr != ' ') {
                throw new IllegalArgumentException("Room name can only have letters and spaces.");
            }
        }

        if (equipmentId == null || equipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment ID cannot be blank.");
        }
        if (equipmentId.contains("|")) {
            throw new IllegalArgumentException("Equipment ID cannot contain '|'.");
        }

        this.roomId = roomId.trim();
        this.roomName = roomName.trim();
        this.equipmentId = equipmentId.trim();
        setQty(qty);
    }

    // Getters
    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public int getQty() {
        return qty;
    }

    // Setters 
    public void setQty(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Equipments quantity in the room cannot be negative.");
        }
        this.qty = qty;
    }

    public String[] toFileLine() {
        return new String[]{
        roomId,
        roomName,
        equipmentId,
        String.valueOf(qty)
        };
    }

    //-------------------------------------------------------------------------------------------------------------------
    public static void loadRoomEquipment() {
        rmRows.clear(); //starts empty, so that loading twice will never duplicate the rows

        List<String[]> rows = CRUD.readFile(ROOM_FILE);

        for (int i =1; i < rows.size(); i++) {
            String[] parts = rows.get(i);

            int qty = Integer.parseInt(parts[3]);
            rmRows.add(new roomEquipment(parts[0], parts[1], parts[2], qty));
        }
    }
    
    // Other classes can read the list
    public static ArrayList<roomEquipment> getRoomRow() {
        return rmRows;
    }

    // The total quantity of each equipment is summed across all rooms
    public static int getTotalQty(String equipmentId) {
        int total = 0;

        for (roomEquipment r : rmRows) {
            if (r.getEquipmentId().equals(equipmentId)) {
                total += r.getQty(); 
            }
        }
        return total;
    }

    // Return only the room that belongs to that one equipment
    public static List<roomEquipment> getRoomsForEquipment(String equipmentId) {
        List <roomEquipment> result = new ArrayList<>();

        for (roomEquipment r : rmRows) {
            if (r.getEquipmentId().equals(equipmentId)) {
                result.add(r);
            }
        }
        return result;
    }

    // Recalculates the equipment's total from the room rows and saves it to equipment.txt
    public static void syncTotalQty(String equipmentId) {
        int newTotal = getTotalQty(equipmentId);

        List<String[]> rows = CRUD.readFile(EQUIPMENT_FILE);

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);

            if (row[0].equals(equipmentId)) {
                row[2] = String.valueOf(newTotal);    // column 2 = total_qty
                CRUD.updateRecord(ROLE, EQUIPMENT_FILE, row);
                return; // it's done and it will stop looping
            }
        }
        throw new IllegalArgumentException("Equipment not found: " + equipmentId);
    }

    // If changing the room's quantity, it will recalculates the equipment's total quantity
    public static void updateRoomQty(String roomId, String equipmentId, int newQty) {
        if (newQty < 0) {
            throw new IllegalArgumentException("Room quantity cannot be negative.");
        }

        List<String[]> rows = CRUD.readFile(ROOM_FILE);

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);

        // Both columns must match: room_id is column 0, equipment_id is column 2
            if (row[0].equals(roomId) && row[2].equals(equipmentId)) {
                row[3] = String.valueOf(newQty);         
                WriteFile.overWriteFile(ROOM_FILE, rows); 

                loadRoomEquipment();         // refresh the in-memory list
                syncTotalQty(equipmentId);   // recalculate total_qty in equipment.txt
                return;
            }
        }
        throw new IllegalArgumentException("This equipment is not in that room");
    }
}
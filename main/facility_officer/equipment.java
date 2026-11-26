package facility_officer;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import common_code.WriteFile;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class equipment {
    private final String equipmentId;
    private String equipmentName;
    private int totalQty;
    private int minQty;
    private String category;

    // Constructor
    public equipment(String equipmentId, String equipmentName, int totalQty, int minQty, String category) {
        this.equipmentId = equipmentId;
        setEquipmentName(equipmentName); // Validate and set equipment name
        setTotalQty(totalQty); // Validate and set quantity
        setMinQty(minQty);
        setCategory(category); // Validate and set category
    }

    // Getters
    public String getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public int getMinQty(){
        return minQty;
    }

    public String getCategory() {
        return category;
    }

    public String getStockStatus() {
        if (totalQty == 0) {
            return "Out of Stock";
        }
        if (totalQty <= minQty) {
            return "Needs Restock";
        }
        return "In Stock";
    }

    // HELPER METHOD: to check the equipment Name
    public static String checkName(String equipmentName) {
        if (equipmentName == null || equipmentName.trim().isEmpty()){
            throw new IllegalArgumentException("Equipment name cannot be empty");
        }
        
        String txt = equipmentName.trim();

        // Only letters and spaces are allowed
        for (int a = 0; a < txt.length(); a++) {
            char c = txt.charAt(a);
            if (!Character.isLetter(c) && c != ' ') {
                throw new IllegalArgumentException("Equipment name can only have letters and spaces.");
            }
        }
        return txt;
    }

    // Setters
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = checkName(equipmentName);
    }

    public void setTotalQty(int totalQty) {
        if(totalQty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.totalQty = totalQty;
    }

    public void setMinQty(int minQty) {
        if(minQty < 0) {
            throw new IllegalArgumentException("Minimum quantity cannot be negative");
        }
        this.minQty = minQty;
    }

    public void setCategory(String category) {
        if("Training Gear".equals(category) || "Electrical".equals(category)) {
            this.category = category;
        } 
        else {    
            throw new IllegalArgumentException("Category must be \"Training Gear\" or \"Electrical\".");
        }
    }   

    // Convert the equipment object to a row of strings to save into a file
    public String[] toRow() {
        return new String[] {
            equipmentId,
            equipmentName,
            String.valueOf(totalQty), // convert int to string
            String.valueOf(minQty),
            category
        };
    }

    // -----------------------------------------------------------------------------------------
    // Read equipment.txt and turn the row into an equipment object
    public static List<equipment> loadequipment() {
        File equipmentFile = new File("data", FileMenu.EQUIPMENT); 
        
        // Read the file and make each row into a list of string arrays
        List<String[]> rows = CRUD.readFile(equipmentFile);
        List<equipment> equipmentList = new ArrayList<>();
        
        for (int i = 1; i < rows.size(); i++) {
            // Get the current line of the file as an array of strings
            String[] rowData = rows.get(i);

            int totalQty = Integer.parseInt(rowData[2]);
            int minQty = Integer.parseInt(rowData[3]);
            equipment d = new equipment(rowData[0], rowData[1], totalQty, minQty, rowData[4]);
            equipmentList.add(d);
        }
        return equipmentList;
    }
    
    // HELPER METHOD: To generate the next ID 
    public static String generateID() {
        File eFile = new File("data", FileMenu.EQUIPMENT);
        File logFile = new File("data", FileMenu.FACILITIESRECORD);
        return IDGenerator.generateNextID(eFile, logFile, "EQ");
    }

    public static equipment addEquipment(String id, String name, int totalQty, int minQty, String category, List<roomEquipment> assign) {
        equipment newEquipment = new equipment(id, name, totalQty, minQty, category);

        if (totalQty < 1) {
            throw new IllegalArgumentException("Total quantity must be 1 or more.");
        }

        if (minQty > totalQty) {
            throw new IllegalArgumentException("Minimum quantity cannot be more than total quantity.");
        }

        if (assign == null || assign.isEmpty()) {
            throw new IllegalArgumentException("Assign the equipment to at least one room.");
        }

        int sum = 0;
        List<String> sRm = new ArrayList<>(); // rooms that have already been seen

        for (roomEquipment r : assign) {   
            if (!r.getEquipmentId().equals(id)) {
                throw new IllegalArgumentException("The room row has the wrong equipment Id.");
            }
            if (sRm.contains(r.getRoomId())) {
                throw new IllegalArgumentException("Each room can only be added once.");
            }
            sRm.add(r.getRoomId());

            if (r.getQty() < 1) {
                throw new IllegalArgumentException("Each room must have at least 1.");
            }

            sum += r.getQty();
        }
        if (sum != totalQty) {
            throw new IllegalArgumentException("Quantities in the room must add up to the total quantity");
        }

        File equipFile = new File("data", FileMenu.EQUIPMENT);
        File roomFile = new File("data", FileMenu.ROOMEQUIPMENT);

        CRUD.addRecord("Facilities Officers", equipFile, newEquipment.toRow());
        for (roomEquipment r : assign) {
            CRUD.addRecord("Facilities Officers", roomFile, r.toFileLine());
        }

        roomEquipment.loadRoomEquipment(); // refresh the in-memory room list
        return newEquipment;
    }

    public static equipment updateEquipment(String equipmentId, String newName, int newMinQty, String newCategory) {
        File ef = new File("data", FileMenu.EQUIPMENT);

        roomEquipment.loadRoomEquipment();
        int total = roomEquipment.getTotalQty(equipmentId);

        // find the old minimum quantity
        int oldMin = -1;
        for(equipment m : loadequipment()) {
            if (m.getEquipmentId().equals(equipmentId)) {
                oldMin = m.getMinQty();
            }
        }

        if (newMinQty != oldMin && newMinQty > total) {
            throw new IllegalArgumentException("Minimum quantity cannot be more than the total quantity.");
        }

        equipment updatedEquipment = new equipment(equipmentId, newName, total, newMinQty, newCategory);

        CRUD.updateRecord("Facilities Officers", ef, updatedEquipment.toRow());
        return updatedEquipment;
    }

    // HELPER METHOD: To remove every row from a file that has the same equipmentID
    private static void removeRows(String fileName, String id) {
        File rFile = new File("data", fileName);
        List <String[]> rows = CRUD.readFile(rFile);

        List <String[]> keptrows = new ArrayList<>();
        keptrows.add(rows.get(0)); // keep the header row

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            // If it doesn't have the same ID that the user search then keep the row
            if (!row[2].equals(id)) { // ROOMEQUIPMENT and ISSUE file has equipmentID in index 2
                keptrows.add(row);
            }
        }

        WriteFile.overWriteFile(rFile, keptrows); // rewrite the whole file without the deleted rows
    }

    public static void deleteEquipment(String equipmentId) {
        File etFile = new File("data", FileMenu.EQUIPMENT);

        CRUD.deleteRecord(equipmentId, "Facilities Officers", etFile);
        removeRows(FileMenu.ROOMEQUIPMENT, equipmentId); 
        removeRows(FileMenu.ISSUE, equipmentId);

        roomEquipment.loadRoomEquipment();
    }
}
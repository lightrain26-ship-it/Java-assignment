package facility_officer;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
import common_code.WriteFile;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class equipmentLogic {
    // Read equipment.txt and turn the row into an equipment object
    public static List<equipment> loadequipment() {
        File equipmentFile = new File("data", FileMenu.EQUIPMENT); 
        
        // Read the file and make each row into a list of string arrays
        List<String[]> rows = CRUD.readFile(equipmentFile);
        List<equipment> equipmentList = new ArrayList<>();
        
        for (int i = 1; i < rows.size(); i++) {
            // Get the current line of the file as an array of strings
            String[] rowData = rows.get(i);

            int qty = Integer.parseInt(rowData[2]);
            equipment d = new equipment(rowData[0], rowData[1], qty, rowData[3]);
            equipmentList.add(d);
        }

        return equipmentList;
    }
    
    public static String generateID() {
        File eFile = new File("data", FileMenu.EQUIPMENT);
        return IDGenerator.generateNextID(eFile, "EQ");
    }

    public static equipment addEquipment(String equipmentName, int qty, String category) {
        File equipFile = new File("data", FileMenu.EQUIPMENT);
        
        String newId = generateID();
        equipment newEquipment = new equipment(newId, equipmentName, qty, category);
        
        // Save the new equipment to the file
        CRUD.addRecord("Facilities Officers", equipFile, newEquipment.toRow());
        
        return newEquipment;
    }

    public static List<equipment> searchEquipment(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }

        List<equipment> result = new ArrayList<>();

        String search = answer.trim().toLowerCase();

        // Find all id, equipment, category that has the same keyword (it can be partial or whole match)
        for (equipment e : loadequipment()) {
            if (e.getEquipmentId().toLowerCase().equals(search)
                || e.getEquipmentName().toLowerCase().contains(search)
                || String.valueOf(e.getQty()).equals(search)
                || e.getCategory().toLowerCase().contains(search)) {
                result.add(e);
            }
        }
        return result;
    }

    public static equipment updateEquipment(String equipmentId, String newName, int newQty, String newCategory) {
        File ef = new File("data", FileMenu.EQUIPMENT);

        equipment updatedEquipment = new equipment(equipmentId, newName, newQty, newCategory);

        CRUD.updateRecord("Facilities Officers", ef, updatedEquipment.toRow());
        return updatedEquipment;
    }

    //helper to remove every row from a file that has the same equipmentID
    private static void removeRows(String fileName, String id) {
        File rFile = new File("data", fileName);
        List <String[]> rows = CRUD.readFile(rFile);

        List <String[]> keptrows = new ArrayList<>();
        keptrows.add(rows.get(0)); // keep the header row

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
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
    }
}

package facility_officer;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;
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
}

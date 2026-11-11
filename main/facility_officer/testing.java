package facility_officer;


public class testing {
    public static void main(String[] args) {
        equipment added = equipmentLogic.addEquipment("Test Gloves", 10, "Training Gear");
        System.out.println("Added: " + added.getEquipmentId());
    }
}
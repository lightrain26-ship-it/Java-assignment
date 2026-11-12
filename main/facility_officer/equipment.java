package facility_officer;

public class equipment {
    private final String equipmentId;
    private String equipmentName;
    private String category;
    private int qty;

    // Constructor
    public equipment(String equipmentId, String equipmentName, int qty, String category) {
        this.equipmentId = equipmentId;
        setEquipmentName(equipmentName); // Validate and set equipment name
        setQty(qty); // Validate and set quantity
        setCategory(category); // Validate and set category
    }

    // Getters
    public String getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public int getQty() {
        return qty;
    }
    public String getCategory() {
        return category;
    }

    // Setters
    public void setEquipmentName(String equipmentName) {
        if (equipmentName == null || equipmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment name cannot be empty.");
        }
        else if (equipmentName.contains("|")){
            throw new IllegalArgumentException("Equipment name cannot contain the character \"|\".");
        }
        this.equipmentName = equipmentName.trim();
    }

    public void setQty(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.qty = qty;
    }

    public void setCategory(String category) {
        if (category.equals("Training Gear") || category.equals("Electrical")) {
            this.category = category;
        } else {    
            throw new IllegalArgumentException("Category must be \"Training Gear\" or \"Electrical\".");
        }
    }   

    // Convert the equipment object to a row of strings to save into a file
    public String[] toRow() {
        return new String[] {
            equipmentId,
            equipmentName,
            String.valueOf(qty), // convert int to string
            category
        };
    }
}

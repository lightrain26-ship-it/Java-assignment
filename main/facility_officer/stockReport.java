package facility_officer;

import java.util.ArrayList;
import java.util.List;

public class stockReport {
    public static List<equipment> viewStock() {
        return equipment.loadequipment();
    }

    public static List<equipment> searchStock(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }

        String search = key.trim().toLowerCase();
        List<equipment> show = new ArrayList<>();

        for (equipment p : equipment.loadequipment()) {
            if (p.getEquipmentId().toLowerCase().equals(search)
                || p.getEquipmentName().toLowerCase().contains(search)
                || String.valueOf(p.getTotalQty()).equals(search)
                || String.valueOf(p.getMinQty()).equals(search)
                || p.getCategory().toLowerCase().contains(search) 
                || p.getStockStatus().toLowerCase().contains(search)) {
                show.add(p);
            }
        }
        return show;
    }
}
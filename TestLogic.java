import administrator.*;

public class TestLogic {
    public static void main(String[] args) {
        System.out.println("=== MULAI TEST: ADD MAN004 -> DELETE MAN004 -> ADD LAGI ===");

        ManageStaff staffService = new ManageStaff("ADM001", "Administrators");

        // 1. Add Manager pertama (ekspektasi: MAN004)
        System.out.println("\n--- 1. Add Manager pertama ---");
        StaffAccount m1 = staffService.addStaff(
                "Manager", "Test Manager Satu", "0198811001",
                "1990-05-10", "Female", null, null
        );
        System.out.println("Terbuat ID: " + m1.getId() + " | Nama: " + m1.getName());

        // 2. Hapus akun yang baru dibuat (m1.getId())
        System.out.println("\n--- 2. Delete " + m1.getId() + " ---");
        staffService.checkDeletable(m1.getId());
        StaffAccount deleted = staffService.deleteStaff(m1.getId());
        System.out.println("Terhapus ID: " + deleted.getId());

        // 3. Add Manager lagi untuk melihat apakah ID keluar 004 atau 005
        System.out.println("\n--- 3. Add Manager setelah dihapus ---");
        StaffAccount m2 = staffService.addStaff(
                "Manager", "Test Manager Dua", "0198811002",
                "1991-06-15", "Male", null, null
        );
        System.out.println("ID baru yang keluar: " + m2.getId() + " | Nama: " + m2.getName());

        System.out.println("\n=== SELESAI ===");
    }
}
import administrator.*;
import java.util.List;
import java.util.NoSuchElementException;

public class TestLogic {
    public static void main(String[] args) {
        System.out.println("=== MULAI TEST: DELETE MAN007 LALU ADD MANAGER BARU ===");

        String loginEmail = "ADM001@mail.sda.edu.my";
        ManageStaff staffService = ManageStaff.fromLoginEmail(loginEmail, "Administrators");

        String targetDeleteID = "MAN007";

        // 1. Delete MAN007
        System.out.println("\n--- Proses DELETE ID: " + targetDeleteID + " ---");
        try {
            StaffAccount calonHapus = staffService.checkDeletable(targetDeleteID);
            System.out.println("checkDeletable OK -> Target: " + calonHapus.getName() + " (" + calonHapus.getRole() + ")");

            StaffAccount terhapus = staffService.deleteStaff(targetDeleteID);
            System.out.println("BERHASIL DIHAPUS! ID=" + terhapus.getId() + " | Nama=" + terhapus.getName());

            try {
                staffService.findActiveStaff(targetDeleteID);
                System.out.println("PERINGATAN: ID " + targetDeleteID + " masih terdeteksi aktif!");
            } catch (NoSuchElementException e) {
                System.out.println("Verifikasi OK: ID " + targetDeleteID + " sudah tidak aktif / terhapus.");
            }
        } catch (Exception e) {
            System.out.println("GAGAL DELETE (" + targetDeleteID + "): " + e.getMessage());
        }

        // 2. Add Manager 1 lagi
        System.out.println("\n--- Proses ADD Manager Baru ---");
        try {
            // Pastikan nomor HP berbeda dari yang sudah pernah dipakai sebelumnya
            StaffAccount manBaru = staffService.addStaff(
                    "Manager",
                    "Rizky Pratama",
                    "0111111199",
                    "1989-04-12",
                    "Male",
                    null,
                    null
            );

            System.out.println("BERHASIL DITAMBAHKAN! ID=" + manBaru.getId()
                    + " | Role=" + manBaru.getRole()
                    + " | Nama=" + manBaru.getName()
                    + " | Email=" + manBaru.getEmail()
                    + " | Pass=" + manBaru.getPassword());

            StaffAccount ditemukan = staffService.findActiveStaff(manBaru.getId());
            System.out.println("Verifikasi OK: ID " + ditemukan.getId() + " terdeteksi aktif.");
        } catch (Exception e) {
            System.out.println("GAGAL ADD Manager: " + e.getMessage());
        }

        // Cek total staff aktif setelah delete & add
        System.out.println("\n--- Cek Total Staff Aktif ---");
        List<StaffAccount> semuaAktif = staffService.getAllStaff();
        System.out.println("Total staff aktif sekarang: " + semuaAktif.size());
        System.out.println("Broken lines: " + staffService.getBrokenLines());

        System.out.println("\n=== SELESAI ===");
    }
}
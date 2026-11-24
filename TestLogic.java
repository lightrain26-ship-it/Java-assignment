import administrator.*;

public class TestLogic {
    public static void main(String[] args) {
        System.out.println("=== MULAI TEST: ADD STAFF SAJA ===");
        
        // Inisialisasi service
        ManageStaff staffService = new ManageStaff("ADM001", "Administrators");
        
        // Eksekusi ADD
        System.out.println("Sedang menambahkan staff...");
        StaffAccount staffBaru = staffService.addStaff(
                "Instructor",       // role
                "Andi Add",         // name
                "0123456789",       // phone
                "1995-05-20",       // dob
                "Male",             // gender
                "Karate",           // specialty
                "BEGINNER"          // grade
        );
        
        // Tampilkan hasil di terminal
        System.out.println("BERHASIL DITAMBAH!");
        System.out.println("ID    : " + staffBaru.getId());
        System.out.println("Nama  : " + staffBaru.getName());
        System.out.println("Email : " + staffBaru.getEmail());
        
        System.out.println("\n=== SELESAI ===");
    }
}
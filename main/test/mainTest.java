package test;
import common_code.*;

public class mainTest {
    public static void main(String[] args) {

        // Test 1: role with a log -> should add a line to Instructors_record.txt
        System.out.println("=== Test 1: Instructors ===");
        WriteFunction.logChange("Instructors", new String[] {"INS001", "UPDATE", "test"});
        System.out.println("Check Instructors_record.txt for: INS001 | UPDATE | test");

        // Test 2: roles without a log -> nothing should be written
        System.out.println("\n=== Test 2: Students and Managers ===");
        WriteFunction.logChange("Students", new String[] {"STU001", "UPDATE", "test"});
        WriteFunction.logChange("Managers", new String[] {"MAN001", "UPDATE", "test"});
        System.out.println("Check: no new lines or new files in data/");

        // Test 3: wrong spelling -> nothing should be written
        System.out.println("\n=== Test 3: wrong spelling ===");
        WriteFunction.logChange("instructors", new String[] {"INS002", "UPDATE", "test"});
        System.out.println("Check: INS002 should NOT appear anywhere");

        // Test 4: writable file lists for each role
        System.out.println("\n=== Test 4: writable files ===");
        String[] roles = {"Managers", "Administrators", "Instructors",
                          "Facilities Officers", "Attendance Officers", "Students"};
        for (String role : roles) {
            System.out.println("\n-- " + role + " --");
            WriteFunction.getWriteableFiles(role);
        }
    }
}
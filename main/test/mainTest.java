package test;
import java.io.File;

import common_code.*;

public class mainTest {
    // public static void main(String[] args) {

    //     // Test 1: role with a log -> should add a line to Instructors_record.txt
    //     // System.out.println("=== Test 1: Instructors ===");
    //     // WriteFunction.logChange("Instructors", new String[] {"INS001", "UPDATE", "test"});
    //     // System.out.println("Check Instructors_record.txt for: INS001 | UPDATE | test");

    //     // Test 2: roles without a log -> nothing should be written
    //     System.out.println("\n=== Test 2: Students and Managers ===");
    //     WriteFunction.logChange("Students", new String[] {"STU001", "UPDATE", "test"});
    //     WriteFunction.logChange("Managers", new String[] {"MAN001", "UPDATE", "test"});
    //     System.out.println("Check: no new lines or new files in data/");

    //     // Test 3: wrong spelling -> nothing should be written
    //     System.out.println("\n=== Test 3: wrong spelling ===");
    //     WriteFunction.logChange("instructors", new String[] {"INS002", "UPDATE", "test"});
    //     System.out.println("Check: INS002 should NOT appear anywhere");

    //     // Test 4: writable file lists for each role
    //     System.out.println("\n=== Test 4: writable files ===");
    //     String[] roles = {"Managers", "Administrators", "Instructors",
    //                       "Facilities Officers", "Attendance Officers", "Students"};
    //     for (String role : roles) {
    //         System.out.println("\n-- " + role + " --");
    //         WriteFunction.getWriteableFiles(role);
    //     }
    // }
    public static void main(String[] args) {
        // File issueFile = new File("data", FileMenu.ISSUE);

        // // Change this row so it has the SAME number of values as issue.txt's header
        // String[] testRow = {"TEST001", "test issue", "open"};

        // // Test 1: allowed role -> expect true, row added to issue.txt
        // boolean allowed = WriteFunction.addRecord("Instructors", issueFile, testRow);
        // System.out.println("Instructors (expect true): " + allowed);

        // // Test 2: not allowed role -> expect false, nothing added
        // boolean denied = WriteFunction.addRecord("Students", issueFile, testRow);
        // System.out.println("Students (expect false): " + denied);
        File issueFile = new File("data", FileMenu.ISSUE);

// Same number of values as issue.txt's header
        String[] testRow = {"TEST001", "test issue", "open"};

// 1. Add a test row so there's something to delete
        boolean added = WriteFunction.addRecord("Instructors", issueFile, testRow);
        System.out.println("Added (expect true): " + added);

// 2. Not-allowed role tries to delete -> expect null, row stays
       String[] denied = WriteFunction.deleteRecord("TEST001", "Students", issueFile);
       System.out.println("Students delete (expect null): " + (denied == null ? "null" : String.join(" | ", denied)));

// 3. Allowed role deletes it -> expect the removed row
       String[] removed = WriteFunction.deleteRecord("TEST001", "Instructors", issueFile);
       System.out.println("Instructors delete (expect TEST001 row): " + (removed == null ? "null" : String.join(" | ", removed)));

// 4. Delete again -> already gone, expect null
       String[] again = WriteFunction.deleteRecord("TEST001", "Instructors", issueFile);
       System.out.println("Delete again (expect null): " + (again == null ? "null" : String.join(" | ", again)));
    }
}
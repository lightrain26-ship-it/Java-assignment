package instructor;

import common_code.CRUD;
import common_code.FileMenu;
import facility_officer.issue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class testadd {

    // true  = the new rows stay in issue.txt so you can look at them
    // false = issue.txt is put back to how it was when the test ends
    static final boolean KEEP_ROWS = true;

    static final File ISSUE_FILE = new File("data", FileMenu.ISSUE);
    static final File BACKUP = new File("data", "issue.txt.manualbackup");

    // Prints the last few rows of issue.txt
    static void showLastRows(String title, int count) {
        List<String[]> rows = CRUD.readFile(ISSUE_FILE);
        System.out.println("--- " + title + " (rows in file: " + (rows.size() - 1) + ") ---");
        for (int i = Math.max(1, rows.size() - count); i < rows.size(); i++) {
            System.out.println(String.join(" | ", rows.get(i)));
        }
    }

    public static void main(String[] args) throws Exception {

        // Always make a backup first, so you can undo this test
        Files.copy(ISSUE_FILE.toPath(), BACKUP.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try {
            showLastRows("BEFORE", 3);

            // 1. An equipment problem
            issue a = facilityProblem.addComplaint("R001", "EQ019", 2, "INS001", "Floor mat edge lifting");
            System.out.println("Added " + a.getIssueId() + " (equipment problem)");

            // 2. A room problem (equipment is "-", affected is ignored and saved as 0)
            issue b = facilityProblem.addComplaint("R002", "-", 5, "INS003", "Window latch broken");
            System.out.println("Added " + b.getIssueId() + " (room problem)");

            // 3. A bad report: it must be refused, and nothing may be saved
            try {
                facilityProblem.addComplaint("R001", "EQ019", 999, "INS001", "Too many");
                System.out.println("FAIL: the bad report was accepted");
            } catch (IllegalArgumentException e) {
                System.out.println("Refused as expected: " + e.getMessage());
            }

            showLastRows("AFTER", 4);

        } finally {
            if (KEEP_ROWS) {
                System.out.println("The new rows are kept. To undo: copy data/issue.txt.manualbackup over data/issue.txt.");
            } else {
                Files.copy(BACKUP.toPath(), ISSUE_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(BACKUP.toPath());
                System.out.println("issue.txt restored.");
            }
        }
    }
}
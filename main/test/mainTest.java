package test;
import java.io.File;

import common_code.*;

import java.util.Arrays;
import java.util.List;

public class mainTest {
    public static void main(String[] args) {
        File issueFile = new File("data", FileMenu.ISSUE);

        // Build test rows with the same number of columns as the header
        int columns = ReadFunction.readFile(issueFile).get(0).length;
        String[] testRow = makeRow("TEST001", "test", columns);
        String[] updatedRow = makeRow("TEST001", "updated", columns);

        // 1. Add -> expect success
        try {
            WriteFunction.addRecord("Instructors", issueFile, testRow);
            System.out.println("1. Add: OK");
        } catch (RuntimeException e) {
            System.out.println("1. Add FAILED: " + e.getMessage());
        }

        // 2. Add with a not-allowed role -> expect permission error
        try {
            WriteFunction.addRecord("Students", issueFile, testRow);
            System.out.println("2. Add as Students: should NOT get here");
        } catch (RuntimeException e) {
            System.out.println("2. Add as Students (expect permission error): " + e.getMessage());
        }

        // 3. Update -> expect the OLD row back
        try {
            String[] old = WriteFunction.updateRecord("Instructors", issueFile, updatedRow);
            System.out.println("3. Update OK, old row: " + String.join(" | ", old));
        } catch (RuntimeException e) {
            System.out.println("3. Update FAILED: " + e.getMessage());
        }

        // 4. Update a missing ID -> expect not-found error
        try {
            WriteFunction.updateRecord("Instructors", issueFile, makeRow("TEST999", "x", columns));
            System.out.println("4. Update missing ID: should NOT get here");
        } catch (RuntimeException e) {
            System.out.println("4. Update missing ID (expect not found): " + e.getMessage());
        }

        // 5. Update with wrong number of values -> expect column-count error
        try {
            String[] tooLong = Arrays.copyOf(updatedRow, columns + 1);
            tooLong[columns] = "extra";
            WriteFunction.updateRecord("Instructors", issueFile, tooLong);
            System.out.println("5. Wrong length: should NOT get here");
        } catch (RuntimeException e) {
            System.out.println("5. Wrong length (expect column error): " + e.getMessage());
        }

        // 6. Update with "|" inside a value -> expect invalid-value error
        try {
            String[] withPipe = updatedRow.clone();
            withPipe[columns - 1] = "bad|value";
            WriteFunction.updateRecord("Instructors", issueFile, withPipe);
            System.out.println("6. Pipe value: should NOT get here");
        } catch (RuntimeException e) {
            System.out.println("6. Pipe value (expect invalid value error): " + e.getMessage());
        }

        // 7. Delete -> expect the UPDATED row back (cleans up the test row)
        try {
            String[] removed = WriteFunction.deleteRecord("TEST001", "Instructors", issueFile);
            System.out.println("7. Delete OK, removed: " + String.join(" | ", removed));
        } catch (RuntimeException e) {
            System.out.println("7. Delete FAILED: " + e.getMessage());
        }

        // 8. Delete again -> expect not-found error
        try {
            WriteFunction.deleteRecord("TEST001", "Instructors", issueFile);
            System.out.println("8. Delete again: should NOT get here");
        } catch (RuntimeException e) {
            System.out.println("8. Delete again (expect not found): " + e.getMessage());
        }

        // 9. Read the file -> header first, no TEST001 left
        try {
            List<String[]> rows = ReadFunction.readFile(issueFile);
            System.out.println("9. File now:");
            for (String[] row : rows) {
                System.out.println("   " + String.join(" | ", row));
            }
        } catch (RuntimeException e) {
            System.out.println("9. Read FAILED: " + e.getMessage());
        }
    }

    // Makes a row: the ID first, then "label1", "label2"... to fill every column
    private static String[] makeRow(String id, String label, int columns) {
        String[] row = new String[columns];
        row[0] = id;
        for (int i = 1; i < columns; i++) {
            row[i] = label + i;
        }
        return row;
    }
}
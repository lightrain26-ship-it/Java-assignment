package student;

import common_code.CRUD;
import common_code.FileMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentData {

    // GET FILE PATH
    public static File getFile(String filename) {
        return new File("data", filename);
    }

    // READ FILE
    public static List<String[]> read(String filename) {
        return CRUD.readFile(getFile(filename));
    }

    // FIND RECORD USING ID (FIRST COLUMN)
    public static String[] findById(
            String filename, String id) {

        List<String[]> rows = read(filename);

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);

            if (row.length > 0 && row[0].equals(id)) {
                return row;
            }
        }

        return null;
    }

    // VALIDATE TEXT INPUT
    public static String requireText(
            String value, String fieldName) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty.");
        }

        value = value.trim();

        if (value.contains("|")) {
            throw new IllegalArgumentException(
                    fieldName + " cannot contain |.");
        }

        return value;
    }

    // VALIDATE ID FORMAT
    public static void validateId(String id, String prefix) {

        if (id == null ||
                !id.matches(prefix + "\\d{3}")) {

            throw new IllegalArgumentException(
                    "Invalid " + prefix + " ID.");
        }
    }

    // FIND CLASS
    public static String[] findClass(String classID) {
        return findById(FileMenu.CLASS, classID);
    }

    // GET STUDENT RECORDS
    // DEFAULT: STUDENT ID IS IN COLUMN 1
    public static List<String[]> getStudentRows(
            String filename, String studentID) {

        return getStudentRows(filename, studentID, 1);
    }

    // GET STUDENT RECORDS USING SPECIFIC COLUMN
    public static List<String[]> getStudentRows(
            String filename,
            String studentID,
            int studentColumnIndex) {

        List<String[]> result = new ArrayList<>();

        List<String[]> rows = read(filename);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length > studentColumnIndex &&
                    row[studentColumnIndex].equals(studentID)) {

                result.add(row);
            }
        }

        return result;
    }

    // CHECK WHETHER RECORD EXISTS
    public static boolean exists(
            String filename, String id) {

        return findById(filename, id) != null;
    }
}

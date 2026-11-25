
package student;

import common_code.CRUD;
import common_code.FileMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentData {

    public static File getFile(String filename) {
        return new File("data", filename);
    }

    public static List<String[]> read(String filename) {
        return CRUD.readFile(getFile(filename));
    }

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

    public static String requireText(
            String value, String fieldName) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                fieldName + " cannot be empty."
            );
        }

        value = value.trim();

        if (value.contains("|")) {
            throw new IllegalArgumentException(
                fieldName + " cannot contain |."
            );
        }

        return value;
    }

    public static void validateId(String id, String prefix) {

        if (id == null || !id.matches(
                prefix + "\\d{3}")) {

            throw new IllegalArgumentException(
                "Invalid " + prefix + " ID."
            );
        }
    }

    public static String[] findClass(String classID) {

        return findById(FileMenu.CLASS, classID);
    }

    public static List<String[]> getStudentRows(
            String filename, String studentID) {

        List<String[]> result = new ArrayList<>();

        List<String[]> rows = read(filename);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length > 1 &&
                    row[1].equals(studentID)) {

                result.add(row);
            }
        }

        return result;
    }

    public static boolean exists(
            String filename, String id) {

        return findById(filename, id) != null;
    }
}

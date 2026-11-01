package common_code;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.UncheckedIOException;
//import javax.swing.JOptionPane;

public class WriteFile {

    public static void appendFile(File file, String[] row) {
// row.length == 0 is for an array, and .isEmpty() is for list
    if (file == null || row == null || row.length == 0) {
    throw new IllegalArgumentException("Please check the file! File or row is missing or empty.");
    }
    String line = String.join(" | ", row);                         // join row with "|"

    try (FileWriter writer = new FileWriter(file, true)) {   // append mode
        writer.write(line + "\n");                     // the line + a newline
    } catch (IOException e) {
        throw new UncheckedIOException("Unable to write: " + file.getName(), e);
        //TODO: show this in GUI POPUP LATER
        //JOptionPane.showMessageDialog(null,"Unable to write: " + file.getName());
    }
  }
      //clear file first then overwrite the file
    public static void overWriteFile(File file, List<String[]> rows){
        if (file == null || rows == null || rows.isEmpty()){
            throw new IllegalArgumentException("Please check the file or the rows are missing or empty.");
        }
        try(FileWriter writer = new FileWriter(file, false)) {
            for (String[] row: rows){
                String line = String.join(" | ", row);
                writer.write(line +"\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write: " + file.getName(), e);
            // TODO: handle exception, later need to be changed into GUI version
        }
    }
}
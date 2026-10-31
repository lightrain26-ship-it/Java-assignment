package common_code;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

//import javax.swing.JOptionPane;

public class WriteFile {

    public static boolean appendFile(File file, String[] row) {
    String line = String.join(" | ", row);                         // join row with "|"

    try (FileWriter writer = new FileWriter(file, true)) {   // append mode
        writer.write(line + "\n");                     // the line + a newline
    } catch (IOException e) {
        System.out.println("Unable to write: " + file.getName());
        //TODO: show this in GUI POPUP LATER
        //JOptionPane.showMessageDialog(null,"Unable to write: " + file.getName());
    }
    return true;
}

    //clear fle first then overwrite the file
    public static boolean overWriteFile(File file, List<String[]> rows){
        if (file == null || rows == null || rows.isEmpty()){
            return false;
        }
        try(FileWriter writer = new FileWriter(file, false)) {
            for (String[] row: rows){
                String line = String.join(" | ", row);
                writer.write(line +"\n");
            }
            return true;
        } catch (IOException e) {
            // TODO: handle exception, later need to be changed into GUI version
            System.out.println("Unable to write: " + file.getName());
        }
        return false;
    }
}

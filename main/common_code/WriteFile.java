package common_code;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//import javax.swing.JOptionPane;

public class WriteFile {

    public static void appendFile(File file, String[] row) {
    String line = String.join(" | ", row);                         // join row with "|"

    try (FileWriter writer = new FileWriter(file, true)) {   // append mode?
        writer.write(line + "\n");                     // the line + a newline
    } catch (IOException e) {
        System.out.println("Unable to write: " + file.getName());
        //TODO: show this in GUI POPUP LATER
        //JOptionPane.showMessageDialog(null,"Unable to write: " + file.getName());
    }
}
}

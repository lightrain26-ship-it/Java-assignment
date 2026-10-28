package common_code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadFunction {
    public static List<String[]> readFile(File file) {
        //used to create a empty list
        List<String[]> lines = new ArrayList<String[]> ();
        String splitLines = "[\\|]";

        try (Scanner myReader = new Scanner(file)){
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
                String[] skipBreak = data.split(splitLines);
                lines.add(skipBreak);
            }
        }catch (FileNotFoundException e) {
            System.out.println("An error occurred!");
            // getname is ude to gain the file name
            System.out.println("Unable to read: " + file.getName());
            e.printStackTrace();
        }
        return lines;
    } 
    
}
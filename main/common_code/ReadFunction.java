package common_code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadFunction {
    public static List<String[]> readFile(File file) {
        if (file == null){
            throw new IllegalArgumentException("Please check the file! File is missing or empty.");
        }
        //used to create a empty list
        List<String[]> lines = new ArrayList<String[]> ();
        //skip the space to find "|"
        String splitLines = "\\s*\\|\\s*";

        try (Scanner myReader = new Scanner(file)){
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
                String[] skipBreak = data.trim().split(splitLines);
                lines.add(skipBreak);
            }
        }catch (FileNotFoundException e) {
            // getname is ude to gain the file name
            throw new UncheckedIOException("Unable to read: " + file.getName(), e);
        }
        return lines;
    } 
    
}
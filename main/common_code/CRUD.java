package common_code;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Scanner;

public class CRUD {
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

        //make  add record for staff and managers
    public static void addRecord(String role,File file,String[] row){
        if (WriteFunction.CanWrite(role, file.getName()) == false){
            throw new SecurityException(role + " does not have permission to write to " + file.getName());
        }
        WriteFile.appendFile(file, row);

    }
                //make  update record for staff and managers
    public static String[] updateRecord(String role,File file,String[] newRow){
        if (WriteFunction.CanWrite(role, file.getName()) == false){
            throw new SecurityException(role + " does not have permission to write to " + file.getName());
        }
        if (newRow == null || newRow.length == 0){
            throw new IllegalArgumentException("Please check the new row! It is missing or empty.");
        }
        for (String value: newRow){
            if (value == null || value.contains("|")){
                throw new IllegalArgumentException("Please check the new row! It contains invalid value '|'.");
            }
        }
        List<String[]> rows = readFile(file);
        if (rows.isEmpty()){
            throw new IllegalStateException("The file is empty. Cannot update record.");
        }
        if (newRow.length != rows.get(0).length){
            throw new IllegalArgumentException("The new row length does not match the existing row length.");
        }

        for(int i = 1; i < rows.size(); i ++){
            String[] rowData = rows.get(i);
            
            if (rowData[0].equals(newRow[0])){
                rows.set(i, newRow);
                WriteFile.overWriteFile(file, rows);
                return rowData;
            }
        }
        throw new NoSuchElementException("No record found with ID: " + newRow[0]);

    }
// DELETE WHOLE COLUMN
    public static String[] deleteRecord(String ID, String role,File file){
        if (WriteFunction.CanWrite(role, file.getName()) == false){
            throw new SecurityException(role + " does not have permission to write to " + file.getName());
        }
        List<String[]> rows = readFile(file);
        
        for(int i = 1; i < rows.size(); i ++){
            String[] rowData = rows.get(i);
            
            if (rowData[0].equals(ID)){
                rows.remove(i);
                WriteFile.overWriteFile(file, rows);
                return rowData;
            }
            
        }
        throw new NoSuchElementException("No record found with ID: " + ID);
    }
// FOR DELETE THE DATA EXCEPT ID ROW
            public static String[] deactivateRecord(String ID, String role,File file){
        if (WriteFunction.CanWrite(role, file.getName()) == false){
            throw new SecurityException(role + " does not have permission to write to " + file.getName());
        }
        List<String[]> rows = readFile(file);
        
        for(int i = 1; i < rows.size(); i ++){
            String[] rowData = rows.get(i);
            
            if (rowData[0].equals(ID)){
                String[] newRow = new String[rowData.length];
                newRow[0] = ID;
                for (int j = 1; j < newRow.length;j++){
                    newRow[j] = FileMenu.EMPTY_DOMAIN;
                }
                rows.set(i, newRow);
                WriteFile.overWriteFile(file, rows);
                return rowData;
            }
            
        }
        throw new NoSuchElementException(ID+ " is already deleted or not inside the file, please check again.");
    }
    
}

package common_code;

import java.io.File;

import java.util.List;
import java.util.NoSuchElementException;

public class WriteFunction {

        static boolean CanWrite(String role, String filename){
        switch ((role)) {
            case "Managers": //report part need to fill all in later confirm
                return filename.equals(FileMenu.ADMIN);
            case "Administrators":
                return filename.equals(FileMenu.FACILITYOFFICE) || filename.equals(FileMenu.ATTENDANCEOFFICE) || filename.equals(FileMenu.INSTRUCTOR) || filename.equals(FileMenu.STUDENT);
            case "Instructors":
                return filename.equals(FileMenu.SCHEDULE) || filename.equals(FileMenu.ISSUE) ;
            case "Facilities Officers":
                return filename.equals(FileMenu.EQUIPMENT) || filename.equals(FileMenu.ISSUE) || filename.equals(FileMenu.ROOMEQUIPMENT);
            case "Attendance Officers":
                return filename.equals(FileMenu.ATTENDANCE);
            case "Students":
                return filename.equals(FileMenu.PAYMENT) || filename.equals(FileMenu.BOOKING);
            default:
                return false;
        }
        
    }
    static String getLogFile(String role){
        switch ((role)) {
            case "Managers":
                return FileMenu.MANAGERRECORD;
            case "Administrators":
                return FileMenu.ADMINRECORD;

            case "Instructors":
                return FileMenu.INSTRUCTORSRECORD;

            case "Facilities Officers":
                return FileMenu.FACILITIESRECORD;

            case "Attendance Officers":
                return FileMenu.ATTENDANCESRECORD;
            default:
                return null;
        }
    }
// writes a row to the role’s log
    public static void logChange(String role, String[] row){
        String logName = getLogFile(role);
        //students: no log
        if (logName == null){
            return;
        }
        WriteFile.appendFile(new File("data", logName), row);
    }

    public static File[] getWriteableFiles(String role){
        File folder = new File("data");
        File[] txtFiles = folder.listFiles((dir, name)-> name.endsWith(".txt") && CanWrite(role, name));
        if (txtFiles == null) {
            //JOptionPane.showMessageDialog(null,"No txt files allowed to be written by this role.");
            throw new IllegalStateException("Data folder not found: " + folder.getAbsolutePath());
        }
        if(txtFiles.length == 0) {
            return new File[0];
        }
//TODO: remove printing once GUI design is decided
        System.out.println("Available TXT files:");
        for (int i = 0; i < txtFiles.length; i++) {
            //JOptionPane.showMessageDialog(null,(i + 1) + ". " + txtFiles[i].getName());
            System.out.println((i + 1) + ". " + txtFiles[i].getName());
        }
        return txtFiles;
    }

    //make  add record for staff and managers
    public static void addRecord(String role,File file,String[] row){
        if (CanWrite(role, file.getName()) == false){
            throw new SecurityException(role + " does not have permission to write to " + file.getName());
        }
        WriteFile.appendFile(file, row);

    }
                //make  update record for staff and managers
    public static String[] updateRecord(String role,File file,String[] newRow){
        if (CanWrite(role, file.getName()) == false){
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
        List<String[]> rows = ReadFunction.readFile(file);
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

    public static String[] deleteRecord(String ID, String role,File file){
        if (CanWrite(role, file.getName()) == false){
            throw new SecurityException(role + " does not have permission to write to " + file.getName());
        }
        List<String[]> rows = ReadFunction.readFile(file);
        
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

}




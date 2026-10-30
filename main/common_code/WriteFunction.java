package common_code;

import java.io.File;



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
        //students, Managers: no log
        if (logName == null){
            return;
        }
        WriteFile.appendFile(new File("data", logName), row);
    }

    public static File[] getWriteableFiles(String role){
        File folder = new File("data");
        File[] txtFiles = folder.listFiles((dir, name)-> name.endsWith(".txt") && CanWrite(role, name));
        if (txtFiles == null || txtFiles.length == 0) {
            //JOptionPane.showMessageDialog(null,"No txt files allowed to be written by this role.");
            System.out.println("No txt files allowed to be written by this role.");
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
}



package common_code;

import java.io.File;



public class WriteFunction {
        static final String ADMIN = "administrator.txt";
        static final String FACILITYOFFICE = "facilitiesOfficer.txt";
        static final String ATTENDANCEOFFICE = "attendanceOfficer.txt";
        static final String INSTRUCTOR  = "instructor.txt";
        static final String STUDENT = "student.txt";
        static final String SCHEDULE = "schedule.txt";
        static final String STUDENTRATE = "studentRating.txt";
        static final String ISSUE = "issue.txt";
        static final String EQUIPMENT = "equipment.txt";
        static final String ROOMEQUIPMENT = "room_equipment.txt";
        static final String PAYMENT = "payment.txt";
        static final String ATTENDANCE = "attendance.txt";
        static final String BOOKING = "booking.txt";
        static final String ADMINRECORD = "Admin_record.txt";
        static final String FACILITIESRECORD = "Facilities_record.txt";
        static final String INSTRUCTORSRECORD = "Instructors_record.txt";
        static final String ATTENDANCESRECORD = "Attendance_record.txt";

        static boolean CanWrite(String role, String filename){
        switch ((role)) {
            case "Managers": //report part need to fill all in later confirm
                return filename.equals(ADMIN);
            case "Administrators":
                return filename.equals(FACILITYOFFICE) || filename.equals(ATTENDANCEOFFICE) || filename.equals(INSTRUCTOR) || filename.equals(STUDENT);
            case "Instructors":
                return filename.equals(SCHEDULE) || filename.equals(ISSUE) ;
            case "Facilities Officers":
                return filename.equals(EQUIPMENT) || filename.equals(ISSUE) || filename.equals(ROOMEQUIPMENT);
            case "Attendance Officers":
                return filename.equals(ATTENDANCE);
            case "Students":
                return filename.equals(PAYMENT) || filename.equals(BOOKING);
            default:
                return false;
        }
        
    }
    static String getLogFile(String role){
        switch ((role)) {
            case "Administrators":
                return ADMINRECORD;

            case "Instructors":
                return INSTRUCTORSRECORD;

            case "Facilities Officers":
                return FACILITIESRECORD;

            case "Attendance Officers":
                return ATTENDANCESRECORD;
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



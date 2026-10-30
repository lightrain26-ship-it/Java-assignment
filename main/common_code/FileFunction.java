package common_code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FileFunction { 
    
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

    
    
    
    
    // fliefunction
    public static File getRoleToRun(String role) {
        File[] txtFiles = getAllowedFiles(role);
        if (txtFiles.length == 0){
            return null;
        }
        Scanner input = new Scanner(System.in);
// later need to make this option display in the GUI with better version and make sure it can get input by click( hope so it can works)
        System.out.print("Choose a file by number: ");
        int choice;
        try{
            choice = input.nextInt();
        } catch (InputMismatchException e){
            System.out.println("Invalid input. Please type it again!");
            input.close();
            return null;
        }
        //range check: stops crashes on 0 or a number that's too big
        if(choice < 1 || choice > txtFiles.length){
            System.out.println("Invalid input. Pick the number between 1 to " + txtFiles.length + " .");
            input.close();
            return null;
        }
        input.close();
        return txtFiles[choice - 1];
    }
    // constant var
    static boolean CanRead(String role, String filename){
        switch ((role)) {
            case "Managers": //report part need to fill all in later confirm
                return filename.equals(ADMIN) || filename.equals(ADMINRECORD)|| filename.equals(INSTRUCTORSRECORD)|| filename.equals(FACILITIESRECORD)|| filename.equals(ATTENDANCESRECORD);
            case "Administrators":
                return filename.equals(FACILITYOFFICE) || filename.equals(ATTENDANCEOFFICE) || filename.equals(INSTRUCTOR) || filename.equals(STUDENT) || filename.equals(ADMINRECORD);
            case "Instructors":
                return filename.equals(SCHEDULE) || filename.equals(STUDENTRATE) || filename.equals(ISSUE);
            case "Facilities Officers":
                return filename.equals(EQUIPMENT) || filename.equals(ISSUE) || filename.equals(ROOMEQUIPMENT);
            case "Attendance Officers":
                return filename.equals(ATTENDANCE);
            case "Students":
                return filename.equals(PAYMENT) || filename.equals(SCHEDULE) || filename.equals(BOOKING);
            default:
                return false;
        }
        
    }
    public static File[] getAllowedFiles(String role){
        File folder = new File("data");
        File[] txtFiles = folder.listFiles((dir, name)-> name.endsWith(".txt") && CanRead(role, name));
        if (txtFiles == null || txtFiles.length == 0) {
            System.out.println("No txt files found in this folder.");
            return new File[0];
        }

        System.out.println("Available TXT files:");
        for (int i = 0; i < txtFiles.length; i++) {
            System.out.println((i + 1) + ". " + txtFiles[i].getName());
        }
        return txtFiles;
    } 
    static void readFile (File file) {
        try( Scanner Reader = new Scanner(file)){
         while(Reader.hasNextLine()){
            System.out.println(Reader.nextLine());
         }
        }catch(FileNotFoundException e){
          System.out.println("Unable reading the file: " + file.getName());
        }
}

}

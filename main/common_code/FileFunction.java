package common_code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FileFunction { // fliefunction
    static void getRoleToRun(String role) {
        File[] txtFiles = getAllowedFiles(role);
        if (txtFiles.length == 0){
            return;
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
            return;
        }
        //range check: stops crashes on 0 or a number that's too big
        if(choice < 1 || choice > txtFiles.length){
            System.out.println("Invalid input. Pick the number between 1 to " + txtFiles.length + " .");
            input.close();
            return;
        }
        input.close();
    }

    // constant var
    static boolean CanRead(String role, String filename){
        switch ((role)) {
            case "Managers": //report part need to fill all in later confirm
                return filename.equals("administrator.txt") || filename.equals("record.txt");
            case "Adminstrators":
                return filename.equals("facilitiesOfficer.txt") || filename.equals("attendanceOfficer.txt") || filename.equals("instructor.txt") || filename.equals("student.txt") || filename.equals("record.txt");
            case "Instructors":
                return filename.equals("schedule.txt") || filename.equals("studentRating.txt") || filename.equals("issue.txt");
            case "Facilities Officers":
                return filename.equals("equipment.txt") || filename.equals("issue.txt") || filename.equals("room_equipment,txt");
            case "Attendance Officers":
                return filename.equals("attendance.txt");
            case "Students":
                return filename.equals("payment.txt") || filename.equals("schedule.txt") || filename.equals("booking.txt");
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


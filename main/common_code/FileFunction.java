package common_code;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

//import javax.swing.JOptionPane;

public class FileFunction { 
    
    
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
            //JOptionPane.showMessageDialog(null,"Invalid input. Please type it again!");
            System.out.println("Invalid input. Please type it again!");
            input.close();
            return null;
        }
        //range check: stops crashes on 0 or a number that's too big
        if(choice < 1 || choice > txtFiles.length){
            //JOptionPane.showMessageDialog(null,"Invalid input. Pick the number between 1 to " + txtFiles.length + " .");
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
            case "Managers": 
                return filename.equals(FileMenu.ADMIN) || filename.equals(FileMenu.ADMINRECORD)|| filename.equals(FileMenu.INSTRUCTORSRECORD)|| filename.equals(FileMenu.FACILITIESRECORD)|| filename.equals(FileMenu.ATTENDANCESRECORD)|| filename.equals(FileMenu.MANAGERRECORD);
            case "Administrators":
                return filename.equals(FileMenu.FACILITYOFFICE) || filename.equals(FileMenu.ATTENDANCEOFFICE) || filename.equals(FileMenu.INSTRUCTOR) || filename.equals(FileMenu.STUDENT) || filename.equals(FileMenu.ADMINRECORD);
            case "Instructors":
                return filename.equals(FileMenu.SCHEDULE) || filename.equals(FileMenu.STUDENTRATE) || filename.equals(FileMenu.ISSUE);
            case "Facilities Officers":
                return filename.equals(FileMenu.EQUIPMENT) || filename.equals(FileMenu.ISSUE) || filename.equals(FileMenu.ROOMEQUIPMENT);
            case "Attendance Officers":
                return filename.equals(FileMenu.ATTENDANCE);
            case "Students":
                return filename.equals(FileMenu.PAYMENT) || filename.equals(FileMenu.SCHEDULE) || filename.equals(FileMenu.BOOKING);
            default:
                return false;
        }
        
    }
    public static File[] getAllowedFiles(String role){
        File folder = new File("data");
        File[] txtFiles = folder.listFiles((dir, name)-> name.endsWith(".txt") && CanRead(role, name));
        if (txtFiles == null || txtFiles.length == 0) {
            //JOptionPane.showMessageDialog(null,"No txt files found in this folder.");
            System.out.println("No txt files found in this folder.");
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

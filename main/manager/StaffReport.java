package manager;

import common_code.*;

import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.time.LocalDate;

public class StaffReport implements ManagerReport{
    @Override 
     public void generate(Scanner input) {

    LogRecordService logService = new LogRecordService("MAN001");   // TODO: from Login
    StaffRecordService staffService = new StaffRecordService();

        int option;
        do{
           System.out.println("===== Staff Report =====");
           System.out.println("1. Manager own's record");
           System.out.println("2. Administrator records");
           System.out.println("3. Instructors' Rating report");
           System.out.println("4. Facilities officer report");
           System.out.println("5. attendance officer report");
           System.out.println("6. Back to Manager Main Menu");
           System.out.print("Enter the option: ");
           
           String line = input.nextLine();
           try{
                option = Integer.parseInt(line.trim());

           } catch (NumberFormatException e){
                option = -1;
           }
           try{
            switch(option){
                case 1:
                    SearchorViewOwnRecord(input, logService);
                    break;
                case 2:
                    SearchorViewAdminRecord(input, logService);  
                    break;
                case 3:
                    showTableMenu(input, staffService, new File("data", FileMenu.STUDENTRATE), "Instructor Ratings", "instructorID", "ratingDate");
                    break;
                case 4:
                    showTableMenu(input, staffService, new File("data", FileMenu.FACILITIESRECORD), "Facilities Records", "performedBy" ,"actionDate" );
                    break;
                case 5: //TODO: later need to prefix
                    showTableMenu(input, staffService, new File("data", FileMenu.ATTENDANCE), "Attendance Records", "ScheID","AttendanceDate");
                    break;
                case 6:
                    System.out.println("Returning to Manager Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 6.");
                    break;
            }
           } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
        
    }while(option != 6);
    }

        // Prints a list of log records; DELETE entries show a short summary
    private static void printRecords(List<LogRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        for (LogRecord record : records) {
            System.out.println(record);
        }
    }

    public static void  SearchorViewOwnRecord(Scanner input, LogRecordService logService){
    int choice;
    do {
        System.out.println("1.Search own record");
        System.out.println("2.View all of own records");
        System.out.println("3. Exit");
        System.out.print("Enter the choice: ");
        String line = input.nextLine();
        try{
                choice = Integer.parseInt(line.trim());

           } catch (NumberFormatException e){
                choice = -1;
           }
           try{
            switch(choice){
                case 1:
                    // search function, later need to check design
                    System.out.print("Action (ADD/UPDATE/DELETE): ");
                    String action = input.nextLine().trim().toUpperCase();
                    printRecords(logService.searchMyRecords(action));
                    break;
                case 2:
                    // display all 
                    printRecords(logService.getMyRecords());
                    break;
                case 3:
                    System.out.println("Returning to Staff Report Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 3.");
                    break;
            }
           } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }

    } while (choice != 3);
   }

    public static void  SearchorViewAdminRecord(Scanner input, LogRecordService logService){
    int choice;
    do {
        System.out.println("1.Search administrators' record by admin ID");
        System.out.println("2.Search administrators' record by action tyoe");
        System.out.println("3.View all of administrators' records");
        System.out.println("4. Exit");
        System.out.print("Enter the choice: ");
        String line = input.nextLine();
        try{
                choice = Integer.parseInt(line.trim());

           } catch (NumberFormatException e){
                choice = -1;
           }
           try{
            switch(choice){
                case 1:
                    // search function, later need to check design
                    System.out.print("Enter admin ID (e.g., ADM002): ");
                    String adminID = input.nextLine().trim().toUpperCase();
                    printRecords(logService.searchAdminRecords(adminID, null));
                    break;
                case 2:
                    // search function, later need to check design
                    System.out.print("Action (ADD/UPDATE/DELETE): ");
                    String action = input.nextLine().trim().toUpperCase();
                    printRecords(logService.searchAdminRecords(null, action));
                    break;
                case 3:
                    // display all 
                    printRecords(logService.getAllAdminRecords());
                    break;
                case 4:
                    System.out.println("Returning to Staff Report Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 4.");
                    break;
            }
           } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }

    } while (choice != 4);
   }

   private static void showTableMenu(Scanner input, StaffRecordService service, File file, 
                                     String title,String idColumn, String dateColumn){
        int choice;
    do {
        System.out.println("===== "+ title + " =====");
        System.out.println("1.Search by ID");
        System.out.println("2.Search by date range");
        System.out.println("3.View all");
        System.out.println("4. Back");
        System.out.print("Enter the choice: ");
        String line = input.nextLine();
        try{
                choice = Integer.parseInt(line.trim());

           } catch (NumberFormatException e){
                choice = -1;
           }
           try{
            switch(choice){
                case 1:
                    // search function, later need to check design
                    System.out.print("Enter " + idColumn + ": ");
                    String value = input.nextLine().trim().toUpperCase();
                    printRows(service.getHeader(file), service.searchRows(file, idColumn, value));
                    break;
                case 2:
                    // search by date range
                    LocalDate from = InputHelper.inputDate(input, "From (yyyy-MM-dd): ");
                    LocalDate to = InputHelper.inputDate(input, "From (yyyy-MM-dd): ");
                    printRows(service.getHeader(file), service.searchByDateRange(file,dateColumn, from, to));
                    break;
                case 3:
                    // display all 
                    printRows(service.getHeader(file), service.getRows(file));
                    break;
                case 4:
                    System.out.println("Returning to Staff Report Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 4.");
                    break;
            }
           } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }

    } while (choice != 4);
   }

    private static void printRows(String[] header, List<String[]> rows) {
        if (rows.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println(String.join(" | ", header));    // column names first
        for (String[] row : rows) {
            System.out.println(String.join(" | ", row));   // then each row
        }
    }

}


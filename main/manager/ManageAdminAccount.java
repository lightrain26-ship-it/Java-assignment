package manager;

import common_code.*;


import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import java.time.LocalDate;

public class ManageAdminAccount {
    public static void ManageAdminAccount() {
        //System.out.println("Manage Admin Account");
        // Implement the logic for managing admin accounts here
        // manage Admin accounts
        // This could include adding, updating, or deleting admin accounts
        //Structure: ID, password, role, name, email, phone, date of birth, gender, date joined
        // for read part, need to make sure cannot read the password, but can read the other information
        // for write part, need to make sure can write the password(or make it auto fill in, cuz it just the role short form + the ID number)
        
        Scanner num = new Scanner(System.in);
        int option;
        do{
           System.out.println("Manage Administrator Accounts: ");
           System.out.println("1. Add new Administrator account");
           System.out.println("2. Update existing Administrator account");
           System.out.println("3. Delete Administrator account");
           System.out.println("4. Search/ view Administrator account");
           System.out.println("5. Back to Manager Main Menu");
           System.out.print("Enter the option: ");
           
           String line = num.nextLine();
           try{
                option = Integer.parseInt(line.trim());

           } catch (NumberFormatException e){
                option = -1;
           }
           try{
            switch(option){
                case 1:
                    AddAdminAccount(num);
                    break;
                case 2:
                    // update existing admin account
                    UpdateAdminAccount(num);
                    break;
                case 3:
                    DeleteAdminAccount(num);
                    break;
                case 4:
                    SearchorViewAdminAccount(num);
                    break;
                case 5:
                    System.out.println("Returning to Manager Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 5.");
                    break;
            }
           } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
        
    }while(option != 5);
}

public static void  SearchorViewAdminAccount(Scanner input){
    int choice;
    do {
        System.out.println("1.Search administrator by ID or administrators' name");
        System.out.println("2.View all of administrators");
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
                    // skip first, search function
                    break;
                case 2:
                    // display all of the admin with value
                    ViewAdminAccount();
                    break;
                case 3:
                    System.out.println("Returning to Manager account management Main Menu...");
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
//function part
public static void AddAdminAccount(Scanner input){
    File adminFile = new File("data",FileMenu.ADMIN);
    File logFile = new File("data", FileMenu.MANAGERRECORD);
    //automatic fields
    // value is for temparary, later will add the correct one
    String newAdminID = IDGenerator.generateNextID(adminFile,"ADM" );
    String newAdminEmail = IDGenerator.generateEmail(newAdminID);
    String password = "admin" + newAdminID.substring(3);
    LocalDate dateJoined = LocalDate.now();

    String newRecordID = IDGenerator.generateNextID(logFile, "REC");

// typed fields (each one keeps asking until valid)
    String name = InputHelper.inputName(input);
    String phone = InputHelper.inputPhone(input);
    LocalDate dob = InputHelper.inputDob(input, 18);
    String gender = InputHelper.inputGender(input);
    // 3. build the object (constructor order: id, password, name, email, phone, dob, gender, dateJoined, role)
    AdminAccount admin = new AdminAccount(newAdminID, password, name, newAdminEmail, phone, dob, gender, dateJoined, "Administrator");

    CRUD.addRecord("Managers", adminFile, admin.toFileRow());
    String[] writeRow ={newRecordID, "ADD",newAdminID,name,  "Administrator", "MAN001",LocalDate.now().toString(), "N/A" ,"N/A","N/A"};
    WriteFunction.logChange("Managers", writeRow);

    System.out.println("New administrator added: " + newAdminID + ","+name +"," + newAdminEmail + "," + dateJoined);

}
// keep admin ID and delete the rest of data
public static void DeleteAdminAccount(Scanner input){
    File adminFile = new File("data", FileMenu.ADMIN);
    File logFile = new File("data", FileMenu.MANAGERRECORD);
    String newRecordID = IDGenerator.generateNextID(logFile, "REC");
    //get id check
    String id = InputHelper.modifyUser(input, "ADM", "Delete");
    InputHelper.inputConfirm(input, "Delete this account(y/n): ");
    
    String[] oldRow = CRUD.deactivateRecord(id, "Managers", adminFile);
    //TODO: replace tghe manager id into read by login
    String[] logRow = {newRecordID, "DELETE", id, oldRow[3], "Administrator", "MAN001",LocalDate.now().toString(), "N/A" ,"N/A","N/A"};
    WriteFunction.logChange("Managers", logRow);
    System.out.println("Administrator id has been deleted: "+ id);
}

public static void ViewAdminAccount(){

    File adminFile = new File("data", FileMenu.ADMIN);
    List<String[]> rows = CRUD.readFile(adminFile);
    String[] header = rows.get(0);

    for (int x = 0; x < header.length;x ++){
        if( x == 1){
            continue;
        }else{
          System.out.print(header[x].toUpperCase() + ",");  
        }
    }
    // skip the array data
    System.out.println();
    
    for (int r = 1; r < rows.size(); r++) {         // outer loop: one round per admin
        String[] row = rows.get(r);

        if (row[1].equals(FileMenu.EMPTY_DOMAIN)) { // deleted admin -> skip this round
            continue;
        }

        for (int x = 0; x < row.length; x++) {      // inner loop: one round per column
            if (x == 1) {
                continue;                           // skip the password column
            }
            System.out.print(row[x] + ",");
        }
        System.out.println();                       // ends this admin's line
    }
}

public static void UpdateAdminAccount(Scanner input){
    File adminFile = new File("data", FileMenu.ADMIN);
    File logFile = new File("data", FileMenu.MANAGERRECORD);

    String userID = InputHelper.modifyUser(input, "ADM", "update");
    List<String[]> rows = CRUD.readFile(adminFile);
    String[] oldRow = null;
    for(int r = 1; r < rows.size(); r ++){
        String[] row = rows.get(r);
    if(row[0].equals(userID) && !row[1].equals(FileMenu.EMPTY_DOMAIN)){
        oldRow = row;
        break;
    }
    }
    if(oldRow == null){ // check is there data or not
        throw new NoSuchElementException("No active administrator with ID: " + userID);
    }

    String[] header = rows.get(0);
    for (int x =0; x < header.length; x ++){
        if(x == 1){
            continue;
        }else{
          System.out.println(header[x].toUpperCase() + ": " + oldRow[x]);  
        }
    }
    int choice;
    String[] newRow = oldRow.clone();
    // loop for asking update admin's data
    do {
        System.out.println("1.Name");
        System.out.println("2.Phone number");
        System.out.println("3.Date of birth");
        System.out.println("4.Gender");
        System.out.println("5. Exit");
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
                    //NAME
                    newRow[3] = InputHelper.inputName(input);
                    break;
                case 2:
                    // PHONE NUMBER
                    newRow[5] = InputHelper.inputPhone(input);
                    break;
                case 3:
                    //DOB
                    newRow[6] = InputHelper.inputDob(input, 18).toString();
                    break;
                case 4:
                    //gender
                    newRow[7] = InputHelper.inputGender(input);
                    break;
                case 5:
                    System.out.println("Finished updating adminstrator's account...");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 5.");
                    break;
            }
           } catch(RuntimeException e){
                System.out.println(e.getMessage());
            }
       

    } while (choice != 5);


    
if(Arrays.equals(oldRow, newRow)){
        System.out.println("Nothing has been changed.");
        return;  
    }

InputHelper.inputConfirm(input, "Save changes to " + userID + "? (y/n): ");

CRUD.updateRecord("Managers", adminFile, newRow);
for (int x = 0; x < newRow.length; x++){
    if (!oldRow[x].equals(newRow[x])){
String newRecordID = IDGenerator.generateNextID(logFile, "REC");
String[] logRow = {newRecordID, "UPDATE", userID, newRow[3], "Administrator", "MAN001",LocalDate.now().toString(), header[x] ,oldRow[x],newRow[x]};
WriteFunction.logChange("Managers", logRow); 
 }
}
System.out.println("Administrator " + userID + " updated.");

   
}

}

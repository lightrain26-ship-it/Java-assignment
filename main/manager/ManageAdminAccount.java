package manager;

import java.util.Scanner;
import java.io.File;
import java.time.LocalDate;
import common_code.*;

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
                    break;
                case 3:
                    // delete admin account
                    break;
                case 4:
                    // search/view admin account
                    break;
                case 5:
                    System.out.println("Returning to Manager Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 5.");
                    break;
            }
           } catch(RuntimeException e){
                System.out.println("An error occurred while processing your request.");
            }
        
    }while(option != 5);
}

public static void AddAdminAccount(Scanner input){
    File adminFile = new File("data",FileMenu.ADMIN);
    //automatic fields
    // value is for temparary, later will add the correct one
    String newAdminID = IDGenerator.generateNextID(adminFile,"ADM" );
    String newAdminEmail = IDGenerator.generateEmail(newAdminID);
    String password = "admin" + newAdminID.substring(3);
    LocalDate dateJoined = LocalDate.now();

// typed fields (each one keeps asking until valid)
    String name = InputHelper.inputName(input);
    String phone = InputHelper.inputPhone(input);
    LocalDate dob = InputHelper.inputDob(input, 18);
    String gender = InputHelper.inputGender(input);
    // 3. build the object (constructor order: id, password, name, email, phone, dob, gender, dateJoined, role)
    AdminAccount admin = new AdminAccount(newAdminID, password, name, newAdminEmail, phone, dob, gender, dateJoined, "Administrator");

    CRUD.addRecord("Managers", adminFile, admin.toFileRow());
    System.out.println("New administrator added: " + newAdminID + ","+name +"," + newAdminEmail + "," + dateJoined);

}
}

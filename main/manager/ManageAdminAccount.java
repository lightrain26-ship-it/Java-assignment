package manager;

import common_code.*;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.YearMonth;

public class ManageAdminAccount {
    public static void showMenu() {
        //System.out.println("Manage Admin Account");
        // Implement the logic for managing admin accounts here
        // manage Admin accounts
        // This could include adding, updating, or deleting admin accounts
        //Structure: ID, password, role, name, email, phone, date of birth, gender, date joined
        // for read part, need to make sure cannot read the password, but can read the other information
        // for write part, need to make sure can write the password(or make it auto fill in, cuz it just the role short form + the ID number)
        AdminService service = new AdminService("MAN001"); //TODO: LATER NEED TO CHANGE INTO GET mANAGERID FROM LOGIN
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
                    AddAdminAccount(num, service);
                    break;
                case 2:
                    // update existing admin account
                    UpdateAdminAccount(num, service);
                    break;
                case 3:
                    DeleteAdminAccount(num, service);
                    break;
                case 4:
                    SearchorViewAdminAccount(num, service);
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

public static void  SearchorViewAdminAccount(Scanner input, AdminService service){
    int choice;
    do {
        System.out.println("1.Search administrator by ID or administrators' name or joned year with month");
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
                    // search function, later need to check design
                    SearchAdminAccount(input, service);
                    break;
                case 2:
                    // display all of the admin with value
                    ViewAdminAccount(service);
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
public static void AddAdminAccount(Scanner input, AdminService service){
// typed fields (each one keeps asking until valid)
    String name = InputHelper.inputName(input);
    String phone = InputHelper.inputPhone(input);
    LocalDate dob = InputHelper.inputDob(input, 18);
    String gender = InputHelper.inputGender(input);
    // 3. build the object (constructor order: id, password, name, email, phone, dob, gender, dateJoined, role)
    AdminAccount admin = service.addAdmin(name, phone, dob, gender);

    System.out.println("New administrator added: " + admin);

}
// keep admin ID and delete the rest of data
public static void DeleteAdminAccount(Scanner input, AdminService service){
    //get id check
    String id = InputHelper.modifyUser(input, "ADM", "Delete");
    AdminAccount admin = service.findActiveAdmin(id);
    System.out.println(admin);

    InputHelper.inputConfirm(input, "Delete this account(y/n): ");
    service.deactivateAdmin(id);
 
    System.out.println("Administrator "+ id+ " has been deleted.");
}

public static void ViewAdminAccount(AdminService service){

   List<AdminAccount> admins = service.getActiveAdmins();

    if (admins.isEmpty()) {
        System.out.println("No administrators found.");
        return;
    }

    for (AdminAccount admin : admins) {
        System.out.println(admin);      // uses toString(): no password
    }
}

public static void UpdateAdminAccount(Scanner input, AdminService service){
    String id = InputHelper.modifyUser(input, "ADM", "update");
    AdminAccount admin = service.findActiveAdmin(id);       // error here if it doesn't exist
    System.out.println(admin);

    // start with the current values; the menu changes them
    String name = admin.getName();
    String phone = admin.getPhone();
    LocalDate dob = admin.getDob();
    String gender = admin.getGender();

    int choice;
    do {
        System.out.println("1. Name");
        System.out.println("2. Phone number");
        System.out.println("3. Date of birth");
        System.out.println("4. Gender");
        System.out.println("5. Finish");
        System.out.print("Enter the choice: ");
        try {
            choice = Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = -1;
        }
        switch (choice) {
            case 1: name = InputHelper.inputName(input); break;
            case 2: phone = InputHelper.inputPhone(input); break;
            case 3: dob = InputHelper.inputDob(input, 18); break;
            case 4: gender = InputHelper.inputGender(input); break;
            case 5: break;
            default: System.out.println("Invalid option. Please enter a number between 1 and 5.");
        }
    } while (choice != 5);

    if (name.equals(admin.getName()) && phone.equals(admin.getPhone())
            && dob.equals(admin.getDob()) && gender.equals(admin.getGender())) {
        System.out.println("Nothing has been changed.");
        return;
    }

    InputHelper.inputConfirm(input, "Save changes to " + id + "? (y/n): ");
    AdminAccount updated = service.updateAdmin(id, name, phone, dob, gender);
    System.out.println("Updated: " + updated);
}

public static void SearchAdminAccount(Scanner input, AdminService service){
    System.out.print("Enter an ID (e.g. ADM001), a join month (e.g. 2024-08), or part of a name: ");
    String text = input.nextLine().trim();
    if (text.isEmpty()) {
        System.out.println("Search text cannot be empty.");
        return;
    }

    if (text.toUpperCase().matches("ADM\\d{3}")) {          // looks like an ID
        System.out.println(service.findActiveAdmin(text.toUpperCase()));
        return;
    }
    
    if (text.matches("\\d{4}-\\d{2}")){
        YearMonth month = InputHelper.checkJoinMonth(text);
        List<AdminAccount> results = service.searchByJoinedMonth(month);
        if(results.isEmpty()){
            System.out.println("No administrators joined in " + month +" .");
        } else {
            // print every admin found (one line each, no password), same as for (int a = 0; a < results.size(); a ++) 
            for (AdminAccount a : results){
                System.out.println(a);
            }
        }
        return;
    }

    List<AdminAccount> results = service.searchByAdminName(text); // otherwise search names
    if (results.isEmpty()) {
        System.out.println("No administrators found.");
        return;
    }
    for (AdminAccount a : results) {
        System.out.println(a);
    }
   
}

}

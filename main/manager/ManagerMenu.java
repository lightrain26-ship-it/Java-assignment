package manager;

import java.util.Scanner;

public class ManagerMenu {
    public static void managerMainMenu() {
        Scanner input = new Scanner(System.in);   // 1. created ONCE, before the loop
        int choice;

        do {
            // 2. show the menu every time
            System.out.print("Manager Main Menu\n" +
                    "1. Manage Administrators\n" +
                    "2. Staff reports\n" +
                    "3. Revenue reports\n" +
                    "4. Exit\n" +
                    "Enter the option number(1-4): ");

            // 3. read the choice; a non-number becomes -1 instead of crashing
            String line = input.nextLine();
            try {
                choice = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            // 4. run the option; errors inside print a message instead of crashing
            try {
                switch (choice) {
                    case 1:
                        ManageAdminAccount.showMenu();
                        break;
                    case 2:
                        StaffReport.generateStaffReport();
                        break;
                    case 3:
                        ManagerReport report = new RevenueReport(); 
                        report.generate(input);
                        break;
                    case 4:
                        System.out.println("Exiting the program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 4.");
                        break;
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (choice != 4);
    input.close();
    }
    
}

package manager;

import common_code.*;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Map;
import java.util.Scanner;

public class RevenueReport implements ManagerReport {
    @Override 
    public void generate(Scanner input){
        RevenueService service = new RevenueService();
        LocalDate from = LocalDate.MIN;
        LocalDate to = LocalDate.MAX;
        int choice;

        do {
            if (from.equals(LocalDate.MIN) && to.equals(LocalDate.MAX)) {
                System.out.println("Period: All time");
            } else {
                System.out.println("Period: " + from + " to " + to);
            }
            // 2. show the menu every time
            System.out.print("===== Revenue Report Menu =====\n" +
                    "1. Set period\n" +
                    "2. Reset to all time\n" +
                    "3. Net revenue\n" +
                    "4. Pending payments\n" +
                    "5. Revenue by year\n" +
                    "6. Revenue by month\n" +
                    "7. Revenue by class\n" +
                    "8. Revenue by payment method\n" +
                    "9. Back\n" + 
                    "Enter the option: ");

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
                        from = InputHelper.inputDate(input, "From (yyyy-MM-dd): ");
                        to = InputHelper.inputDate(input, "To (yyyy-MM-dd): ");
                        if (from.isAfter(to)) {
                            System.out.println("Start date is after end date. Period reset to all time.");
                            from = LocalDate.MIN;
                            to = LocalDate.MAX;
                        } else {
                            System.out.println("Period set.");
                        }
                        break;
                    case 2:
                        System.out.println("Period reset to all time.");
                        from = LocalDate.MIN;
                        to = LocalDate.MAX;
                        break;
                    case 3:
                        System.out.println(String.format("Net revenue: RM%,.2f", service.getTotalRevenue(from, to)));
                        break;
                    case 4:
                        System.out.println(String.format("Total Pending: RM%,.2f", service.getTotalPending(from, to)));
                        break;
                    case 5:
                        {
                        Map<Year, Double> byYear = service.getRevenueByYear(from, to);
                        if (byYear.isEmpty()){
                            System.out.println("No paid revenue found for this period.");
                        } else {
                            for (Map.Entry<Year, Double> e : byYear.entrySet()){
                                System.out.println(e.getKey() + " : " + String.format("RM %,.2f", e.getValue()));
                            }
                        }
                        break;
                        }
                    case 6:
                        {
                        Map<YearMonth, Double> byYearMonth = service.getRevenueByMonth(from, to);
                        if (byYearMonth.isEmpty()){
                            System.out.println("No paid revenue found for this period.");
                        } else {
                            for (Map.Entry<YearMonth, Double> e : byYearMonth.entrySet()){
                                System.out.println(e.getKey() + " : " + String.format("RM %,.2f", e.getValue()));
                            }
                        }
                        break;
                        }
                    case 7:
                        {
                        Map<String, Double> byClass = service.getRevenueByClass(from, to);
                        if (byClass.isEmpty()){
                            System.out.println("No paid revenue found for this period.");
                        } else {
                            for (Map.Entry<String, Double> e : byClass.entrySet()){
                                System.out.println(e.getKey() + " : " + String.format("RM %,.2f", e.getValue()));
                            }
                        }
                        break;
                        }
                    case 8:
                        {
                        Map<String, Double> byMethod = service.getRevenueByMethod(from, to);
                        if (byMethod.isEmpty()){
                            System.out.println("No paid revenue found for this period.");
                        } else {
                            for (Map.Entry<String, Double> e : byMethod.entrySet()){
                                System.out.println(e.getKey() + " : " + String.format("RM %,.2f", e.getValue()));
                            }
                        }
                        break;
                        }
                    case 9:
                        System.out.println("Exiting revenue report system. Back to Manager Main Menu.");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number between 1 and 9.");
                        break;
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (choice != 9);
    }
}


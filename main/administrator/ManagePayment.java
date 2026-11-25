package administrator;

import common_code.CRUD;
import common_code.FileMenu;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

//View and search payment records (Administrator can only view, cannot add / update / delete)
public class ManagePayment {
    private String userID;
    private String userRole;
    private File paymentFile = new File("data", FileMenu.PAYMENT);
    private int brokenLines = 0;        //How many broken lines were ignored in the last getAllPayments()

    //CONSTRUCTOR
    //Only Administrators can use this service. Save the user that is logged in
    public ManagePayment(String userID, String userRole) {
        if (!userRole.equals("Administrators")) {
            throw new SecurityException("Access denied. Only administrators can view payment records");
        }
        this.userID = userID;
        this.userRole = userRole;
    }

    public int getBrokenLines() {
        return brokenLines;
    }

    //View all payments. A broken line is ignored (and counted), the other lines are still read
    public List<PaymentRecord> getAllPayments() {
        List<PaymentRecord> payments = new ArrayList<>();
        brokenLines = 0;

        List<String[]> rows = CRUD.readFile(paymentFile);
        if (rows.isEmpty()) {
            return payments;
        }
        int columns = rows.get(0).length;
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length != columns) {
                brokenLines++;
                continue;
            }
            try {
                payments.add(PaymentRecord.fromRow(row));
            } catch (RuntimeException e) {
                brokenLines++;          //for example the amount is not a number or the date is not a real date
            }
        }
        return payments;
    }

    //HELPER METHODS
    //Values for the Method dropdown (every different method that is really inside payment.txt)
    public List<String> getMethods() {
        List<String> methods = new ArrayList<>();
        for (PaymentRecord payment : getAllPayments()) {
            if (!methods.contains(payment.getMethod())) {
                methods.add(payment.getMethod());
            }
        }
        return methods;
    }

    //HELPER METHODS
    //Values for the Status dropdown (every different status that is really inside payment.txt)
    public List<String> getStatuses() {
        List<String> statuses = new ArrayList<>();
        for (PaymentRecord payment : getAllPayments()) {
            if (!statuses.contains(payment.getStatus())) {
                statuses.add(payment.getStatus());
            }
        }
        return statuses;
    }

    //HELPER METHODS
    //Total amount of the payments with status Paid
    public double getTotalPaid(List<PaymentRecord> payments) {
        double total = 0;
        for (PaymentRecord payment : payments) {
            if (payment.getStatus().equalsIgnoreCase("Paid")) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    //HELPER METHODS
    //Check a date typed by admin (yyyy-MM-dd). Empty means "no date limit" and returns null
    private LocalDate checkFilterDate(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        text = text.trim();
        if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException(fieldName + " date must be in yyyy-MM-dd with numbers only (e.g. 2026-09-01)");
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " date is not a real date (check the month and day)");
        }
    }

    //Search with the filters of the Payment Records screen. Every filter can be empty / "All":
    //keyword = payment, booking, student or class ID | status and method = dropdown | from and to = date range (included)
    public List<PaymentRecord> searchPayments(String keyword, String status, String method, String fromText, String toText) {
        LocalDate from = checkFilterDate(fromText, "From");
        LocalDate to = checkFilterDate(toText, "To");
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after To date");
        }
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean allStatus = status == null || status.equals("All");
        boolean allMethod = method == null || method.equals("All");

        List<PaymentRecord> results = new ArrayList<>();
        for (PaymentRecord payment : getAllPayments()) {
            if (hasKeyword && !payment.matches(keyword.trim())) {
                continue;
            }
            if (!allStatus && !payment.getStatus().equals(status)) {
                continue;
            }
            if (!allMethod && !payment.getMethod().equals(method)) {
                continue;
            }
            if (from != null && payment.getPaymentDate().isBefore(from)) {
                continue;
            }
            if (to != null && payment.getPaymentDate().isAfter(to)) {
                continue;
            }
            results.add(payment);
        }
        return results;
    }
}

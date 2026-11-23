package student;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentPayment {

    private final String studentID;

    private final File paymentFile =
            StudentData.getFile(FileMenu.PAYMENT);

    public StudentPayment(String studentID) {
        StudentData.validateId(studentID, "STU");
        this.studentID = studentID;
    }

    // ADD PAYMENT

    public String[] addPayment(
            String bookingID, String paymentMethod) {

        bookingID = StudentData.requireText(
                bookingID, "Booking ID");

        paymentMethod = StudentData.requireText(
                paymentMethod, "Payment method");

        String[] booking =
                StudentData.findById(
                        FileMenu.BOOKING, bookingID);

        if (booking == null || booking.length != 7) {
            throw new IllegalArgumentException(
                    "Booking not found.");
        }

        if (!booking[1].equals(studentID)) {
            throw new SecurityException(
                    "This booking does not belong to you.");
        }

        if (!booking[6].equalsIgnoreCase("Confirmed")) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be paid.");
        }

        // PREVENT DUPLICATE PAYMENT

        List<String[]> payments = viewPayments();

        for (String[] payment : payments) {

            if (payment.length == 8
                    && payment[1].equals(bookingID)) {

                throw new IllegalStateException(
                        "Payment already exists for this booking.");
            }
        }

        // GET CLASS PRICE

        String[] cls =
                StudentData.findClass(booking[2]);

        if (cls == null || cls.length != 13) {
            throw new IllegalStateException(
                    "Class information not found.");
        }

        double amount =
                Double.parseDouble(cls[12]);

        if (amount <= 0) {
            throw new IllegalStateException(
                    "Invalid payment amount.");
        }

        String paymentID =
                IDGenerator.generateNextID(
                        paymentFile, "PAY");

        String[] payment = {
            paymentID,
            bookingID,
            studentID,
            booking[2],
            String.format(
                java.util.Locale.US, "%.2f", amount),
            LocalDate.now().toString(),
            paymentMethod,
            "Paid"
        };

        CRUD.addRecord(
                "Students", paymentFile, payment);

        return payment;
    }

    // VIEW OWN PAYMENTS

    public List<String[]> viewPayments() {

        return StudentData.getStudentRows(
                FileMenu.PAYMENT, studentID);
    }

    // SEARCH PAYMENTS

    public List<String[]> searchPayments(String keyword) {

        keyword = StudentData.requireText(
                keyword, "Search keyword").toLowerCase();

        List<String[]> result = new ArrayList<>();

        for (String[] row : viewPayments()) {

            for (String value : row) {

                if (value.toLowerCase().contains(keyword)) {
                    result.add(row);
                    break;
                }
            }
        }

        return result;
    }
}
package student;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Locale;

public class StudentPayment {

    private final String studentID;

    private final File paymentFile =
            StudentData.getFile(FileMenu.PAYMENT);

    private final File bookingFile =
            StudentData.getFile(FileMenu.BOOKING);

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

        // FIND BOOKING
        String[] booking =
                StudentData.findById(
                        FileMenu.BOOKING, bookingID);

        if (booking == null || booking.length != 7) {
            throw new IllegalArgumentException(
                    "Booking not found.");
        }

        // VERIFY BOOKING OWNER
        if (!booking[1].equals(studentID)) {
            throw new SecurityException(
                    "This booking does not belong to you.");
        }

        // ONLY PENDING BOOKINGS CAN BE PAID
        if (!booking[6].equalsIgnoreCase("Pending Payment")) {
            throw new IllegalStateException(
                    "Only bookings awaiting payment can be paid.");
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

        double amount;

        try {
            amount = Double.parseDouble(cls[12]);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Invalid class price.");
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalStateException(
                    "Invalid payment amount.");
        }

        // GENERATE PAYMENT ID
        String paymentID =
                IDGenerator.generateNextID(
                        paymentFile, "PAY");

        // CREATE PAYMENT RECORD
        String[] payment = {

            paymentID,
            bookingID,
            studentID,
            booking[2],
            String.format(Locale.US, "%.2f", amount),
            booking[4],
            paymentMethod,
            "Paid"
        };

        // SAVE PAYMENT
        CRUD.addRecord(
                "Students", paymentFile, payment);

        // UPDATE BOOKING STATUS TO CONFIRMED
        String[] updatedBooking = booking.clone();

        updatedBooking[6] = "Confirmed";

        CRUD.updateRecord(
                "Students", bookingFile, updatedBooking);

        return payment;
    }

    // VIEW OWN PAYMENTS
    public List<String[]> viewPayments() {

        return StudentData.getStudentRows(
                FileMenu.PAYMENT, studentID, 2);
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
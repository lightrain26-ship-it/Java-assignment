package administrator;

import java.time.LocalDate;

//One line of payment.txt: paymentID | bookingID | studentID | classID | amount | paymentDate | paymentMethod | status
public class PaymentRecord {
    //OBJECT ORIENTED
    //Encapsulation: the data is private, other classes can only read it with the getter methods
    private String paymentID;
    private String bookingID;
    private String studentID;
    private String classID;
    private double amount;
    private LocalDate paymentDate;
    private String method;
    private String status;

    //CONSTRUCTOR
    //Create one payment record from the 8 values of a txt line
    public PaymentRecord(String paymentID, String bookingID, String studentID, String classID,
                         double amount, LocalDate paymentDate, String method, String status) {
        this.paymentID = paymentID;
        this.bookingID = bookingID;
        this.studentID = studentID;
        this.classID = classID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.status = status;
    }

    public String getPaymentID() { return paymentID; }
    public String getBookingID() { return bookingID; }
    public String getStudentID() { return studentID; }
    public String getClassID() { return classID; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }

    //HELPER METHODS
    //Change one txt row into a PaymentRecord. Throws an error if the row is broken (the caller will ignore that row)
    public static PaymentRecord fromRow(String[] row) {
        return new PaymentRecord(row[0], row[1], row[2], row[3], Double.parseDouble(row[4]),
                LocalDate.parse(row[5]), row[6], row[7]);
    }

    //HELPER METHODS
    //Search by payment ID, booking ID, student ID or class ID (part of the ID is enough, not case sensitive)
    public boolean matches(String keyword) {
        keyword = keyword.toLowerCase();
        return paymentID.toLowerCase().contains(keyword)
                || bookingID.toLowerCase().contains(keyword)
                || studentID.toLowerCase().contains(keyword)
                || classID.toLowerCase().contains(keyword);
    }

    //One row for the table
    public String[] toTableRow() {
        return new String[]{paymentID, bookingID, studentID, classID, String.format("%.2f", amount),
                paymentDate.toString(), method, status};
    }
}

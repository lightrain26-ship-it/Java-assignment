package manager;

import java.time.LocalDate;

public class PaymentLog {
    private String paymentID;
    private String bookingID;
    private String studentID;
    private String classID;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String status;

    public PaymentLog(String paymentID,
                      String bookingID,
                      String studentID,
                      String classID,
                      double amount,
                      LocalDate paymentDate,
                      String paymentMethod,
                      String status){
        this.paymentID = paymentID;
        this.bookingID = bookingID;
        this.studentID = studentID;
        this.classID = classID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
                     }
    
    public static PaymentLog fromRow(String[] row){
        return new PaymentLog(
            row[0],                    // PAYMENTID
            row[1],                    // BOOKINGID
            row[2],                    // STUDENTID
            row[3],                    // CLASSID
           Double.parseDouble( row[4]),   //AMOUNT
            LocalDate.parse( row[5]), // PAYMENTDATE
            row[6],  // PAYMENTMETHOD
            row[7]                  // STATUS
        );
    }

    public double getAmount(){
        return amount;
    }

    public LocalDate getPaymentDate(){
        return paymentDate;
    }

    public String getStatus(){
        return status;
    }

    public String getPaymentID(){
        return paymentID;
    }

    public String getClassID(){
        return classID;
    }

    public String getStudentID(){
        return studentID;
    }

    public String getPaymentMethod(){
        return paymentMethod;
    }

    public String getBookingID(){
        return bookingID;
    }

    @Override
    public String toString(){
        return paymentID + " | " + bookingID + " | " + studentID + " | " + classID + " | "
             + amount + " | " + paymentDate + " | " + paymentMethod + " | "
             + status;
    }   
}


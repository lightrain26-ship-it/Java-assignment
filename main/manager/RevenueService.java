package manager;

import common_code.*;
import java.io.File;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class RevenueService {
    private int skippedRows = 0;

    private List<PaymentLog> getPaymentsBetween(LocalDate from, LocalDate to){
        if (from.isAfter(to)){
            throw new IllegalArgumentException("Start must before end date.");
        }
        List<PaymentLog> result = new ArrayList<>();

        for (PaymentLog payment: getPayments()){
            LocalDate date = payment.getPaymentDate();
            if (!date.isBefore(from) && !date.isAfter(to)){
                result.add(payment);
            }
        }
        return result;
    }

    public List<PaymentLog> getPayments(){
        File paymentFile = new File("data" , FileMenu.PAYMENT);
        List<String[]> rows = CRUD.readFile(paymentFile);
        skippedRows = 0;

        List<PaymentLog> payments = new ArrayList<>();
        for (int r = 1; r < rows.size(); r++) {                // skip header (row 0)
            String[] row = rows.get(r);
            try {
                payments.add(PaymentLog.fromRow(row));          // may fail on a bad row
            } catch (RuntimeException e) {
                skippedRows++;                                  // count it, then move on
            }
        }   
        return payments;
    }
    

    public int getSkippedRows(){
        return skippedRows;
         }

    public double getTotalRevenue(){
        return getTotalRevenue(LocalDate.MIN, LocalDate.MAX);
    }


    public double getTotalRevenue(LocalDate from, LocalDate to){
        double total = 0;
        for (PaymentLog payment : getPaymentsBetween(from, to)) {
            if (payment.getStatus().equalsIgnoreCase("Paid")) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    public double getTotalPending(){
        return getTotalPending(LocalDate.MIN, LocalDate.MAX);
    }

    public double getTotalPending(LocalDate from, LocalDate to){
        double total = 0;
        for (PaymentLog payment: getPaymentsBetween(from, to)){
            // check what payment has changed
         if(payment.getStatus().equalsIgnoreCase("Pending")){
            total += payment.getAmount();
         }

        }
        return total;
    }

    public Map<Year, Double> getRevenueByYear(){
        return getRevenueByYear(LocalDate.MIN, LocalDate.MAX);
     }

    public Map<Year, Double> getRevenueByYear(LocalDate from, LocalDate to){
        Map<Year, Double> revenue = new TreeMap<>();
        for (PaymentLog payment: getPaymentsBetween(from, to)){
            if(payment.getStatus().equalsIgnoreCase("Paid")){
                //add this payment to its year
                Year year = Year.from(payment.getPaymentDate());
                revenue.put(year, revenue.getOrDefault(year, 0.0) +  payment.getAmount());
            }
        }
        return revenue;
    }

    public Map<YearMonth, Double> getRevenueByMonth(){
        return getRevenueByMonth(LocalDate.MIN, LocalDate.MAX);
    }

    public Map<YearMonth, Double> getRevenueByMonth(LocalDate from, LocalDate to){
        Map<YearMonth, Double> revenue = new TreeMap<>();
        for (PaymentLog payment: getPaymentsBetween(from, to)){
            if(payment.getStatus().equalsIgnoreCase("Paid")){
                //add this payment to its month
                YearMonth month = YearMonth.from(payment.getPaymentDate());
                revenue.put(month, revenue.getOrDefault(month, 0.0) +  payment.getAmount());
            }
        }
        return revenue;
    }

    public Map<String, Double> getRevenueByClass(){
        return getRevenueByClass(LocalDate.MIN, LocalDate.MAX);
    }
    
    public Map<String, Double> getRevenueByClass(LocalDate from, LocalDate to){
        Map<String, Double> revenue = new TreeMap<>();
        for (PaymentLog payment: getPaymentsBetween(from, to)){
            if(payment.getStatus().equalsIgnoreCase("Paid")){
                //add this payment to its classID
                String classID = payment.getClassID();
                revenue.put(classID, revenue.getOrDefault(classID, 0.0) + payment.getAmount());
            }
        }
        return revenue;
    } 

    public Map<String, Double> getRevenueByMethod(){
        return getRevenueByMethod(LocalDate.MIN, LocalDate.MAX);
    }
    

    public Map<String, Double> getRevenueByMethod(LocalDate from, LocalDate to){
        Map<String, Double> revenue = new TreeMap<>();
        for (PaymentLog payment: getPaymentsBetween(from, to)){
            if(payment.getStatus().equalsIgnoreCase("Paid")){
                //add this payment to its method
                String method = payment.getPaymentMethod();
                revenue.put(method, revenue.getOrDefault(method, 0.0) + payment.getAmount());
            }
        }
        return revenue;
    }  

}
 


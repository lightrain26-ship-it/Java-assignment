package common_code;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHelper {
    public static String inputName(Scanner input){
            while(true){
    System.out.print("New admin's name: ");
    String userName = input.nextLine().trim();
    try{
        checkName(userName);
        return userName;
    }catch(IllegalArgumentException e){ 
        System.out.println(e.getMessage()); 
    }
     }
    }

    public static String inputPhone(Scanner input){
        while(true){
            System.out.print("New admin phone's number: ");
            String userPhone = input.nextLine().trim();
            try {
                checkphonenumber(userPhone);
                return userPhone;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public static LocalDate inputDob(Scanner input, int minAge){
        while(true){
            System.out.print("New admin's date of birth(yyyy-MM-dd):");
            String userDob = input.nextLine().trim();
            try {
                return checkDob(userDob, minAge);
            } catch (IllegalArgumentException e) {
                // TODO: handle exception
                 System.out.println(e.getMessage());
            }
        }
    }

    public static String inputGender(Scanner input){
        while(true){
            System.out.print("New admin's gender(M/F):");
            String userGender = input.nextLine().trim();
            try {
               return checkGender(userGender); 
            } catch (IllegalArgumentException e) {
                // TODO: handle exception
                System.out.println(e.getMessage());
            }
        }
    }

    public static void checkName(String userName){
        if (userName == null ||userName.isEmpty()|| !userName.matches("[A-Z][a-z]*( [A-Z][a-z]*)*")){
           throw new IllegalArgumentException("Name must not be empty, and each word must start with a capital letter followed by lowercase letters (e.g. Ali Ahmad).");
        }
    }

        public static void checkphonenumber(String userPhone){
        if (userPhone == null ||userPhone.isEmpty()|| !userPhone.matches("0\\d{9,10}")){
           throw new IllegalArgumentException("Phone must not be empty, and each phone number should start with 0 and be 10-11 digits(e.g.0171234980).");
        }
    }

    public static LocalDate checkDob(String userDob, int minAge) {
       if (userDob == null || userDob.isEmpty()) {
        throw new IllegalArgumentException("Date of birth must not be empty.");
       }

       LocalDate dob;
       try {
         dob = LocalDate.parse(userDob);                 // text -> date, or throws
       } catch (DateTimeParseException e) {
         throw new IllegalArgumentException("Date of birth must be yyyy-MM-dd (e.g. 2010-01-01).");
       }

       if (dob.isAfter(LocalDate.now())) {                 // isAfter on the DATE, not the text
         throw new IllegalArgumentException("Date of birth cannot be in the future.");
       }

        int age = Period.between(dob, LocalDate.now()).getYears();
       if (age < 18) {
         throw new IllegalArgumentException("Staff must be at least 18 years old.");
       }

       return dob;
}
    public static String checkGender(String userGender){
        if(userGender == null || userGender.isEmpty()){
            throw new IllegalArgumentException("Gender must not be empty.");
        }
        if(userGender.toUpperCase().equals("M") || userGender.toUpperCase().equals( "MALE")){
            return "Male";
        }
        if(userGender.toUpperCase().equals("F") || userGender.toUpperCase().equals( "FEMALE")){
            return "Female";
        }
          throw new IllegalArgumentException("Gender must be male (M) or female (F).");
        }
}
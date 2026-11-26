package student;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentBooking {

    private final String studentID;

    private final File bookingFile =
            StudentData.getFile(FileMenu.BOOKING);

    public StudentBooking(String studentID) {
        StudentData.validateId(studentID, "STU");
        this.studentID = studentID;
    }

    // CHECK TWO-DAY BOOKING REQUIREMENT
    private void validateBookingDate(String date) {

        LocalDate scheduledDate = LocalDate.parse(date);

        if (scheduledDate.isBefore(
                LocalDate.now().plusDays(2))) {

            throw new IllegalArgumentException(
                    "Bookings must be made at least 2 days before the class.");
        }
    }

    // VIEW AVAILABLE SCHEDULES
    public List<String[]> viewAvailableClasses() {

        List<String[]> available = new ArrayList<>();

        List<String[]> schedules =
                StudentData.read(FileMenu.SCHEDULE);

        for (int i = 1; i < schedules.size(); i++) {

            String[] schedule = schedules.get(i);

            if (schedule.length != 7) {
                continue;
            }

            String[] cls =
                    StudentData.findClass(schedule[1]);

            if (cls == null || cls.length != 13) {
                continue;
            }

            if (!schedule[6].equalsIgnoreCase("ACTIVE")
                    || !cls[11].equalsIgnoreCase("ACTIVE")) {
                continue;
            }

            try {
                validateBookingDate(schedule[2]);
            } catch (IllegalArgumentException e) {
                continue;
            }

            available.add(schedule);
        }

        return available;
    }

    // COUNT ACTIVE BOOKINGS FOR A SCHEDULE
    private int countBookings(String scheduleID) {

        int count = 0;

        List<String[]> rows =
                CRUD.readFile(bookingFile);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length != 7) {
                continue;
            }

            boolean activeStatus =
                    row[6].equalsIgnoreCase("Confirmed")
                    || row[6].equalsIgnoreCase("Pending Payment");

            if (row[3].equals(scheduleID) && activeStatus) {
                count++;
            }
        }

        return count;
    }

    // CHECK DUPLICATE BOOKING
    private boolean alreadyBooked(
            String scheduleID,
            String excludeBookingID) {

        for (String[] row : viewBookings()) {

            if (row.length != 7) {
                continue;
            }

            boolean activeStatus =
                    row[6].equalsIgnoreCase("Confirmed")
                    || row[6].equalsIgnoreCase("Pending Payment");

            if (row[3].equals(scheduleID)
                    && activeStatus
                    && !row[0].equals(excludeBookingID)) {

                return true;
            }
        }

        return false;
    }

    // CHECK CLASS CAPACITY
    private void checkCapacity(
            String[] cls,
            String scheduleID) {

        int maximum = Integer.parseInt(cls[5]);

        int current = countBookings(scheduleID);

        if (current >= maximum) {
            throw new IllegalStateException(
                    "This class has reached maximum capacity.");
        }
    }

    // BOOK CLASS
    public String[] bookClass(String scheduleID) {

        scheduleID = StudentData.requireText(
                scheduleID, "Schedule ID");

        String[] schedule =
                StudentData.findById(
                        FileMenu.SCHEDULE, scheduleID);

        if (schedule == null || schedule.length != 7) {
            throw new IllegalArgumentException(
                    "Schedule not found.");
        }

        if (!schedule[6].equalsIgnoreCase("ACTIVE")) {
            throw new IllegalStateException(
                    "Schedule is not available.");
        }

        // VALIDATE SCHEDULE DATE
        validateBookingDate(schedule[2]);

        String classID = schedule[1];

        String[] cls =
                StudentData.findClass(classID);

        if (cls == null || cls.length != 13) {
            throw new IllegalStateException(
                    "Class information not found.");
        }

        if (!cls[11].equalsIgnoreCase("ACTIVE")) {
            throw new IllegalStateException(
                    "Class is not active.");
        }

        // CHECK DUPLICATE BOOKING
        if (alreadyBooked(scheduleID, null)) {
            throw new IllegalStateException(
                    "You have already booked this schedule.");
        }

        // CHECK CAPACITY
        checkCapacity(cls, scheduleID);

        // GENERATE BOOKING ID
        String bookingID =
                IDGenerator.generateNextID(
                        bookingFile, "BKG");

        // CREATE BOOKING
        String[] booking = {

            bookingID,
            studentID,
            classID,
            scheduleID,
            LocalDate.now().toString(),
            schedule[2],
            "Pending Payment"
        };

        CRUD.addRecord(
                "Students", bookingFile, booking);

        return booking;
    }

    // VIEW OWN BOOKINGS
    public List<String[]> viewBookings() {

        return StudentData.getStudentRows(
                FileMenu.BOOKING, studentID);
    }

    // SEARCH OWN BOOKINGS
    public List<String[]> searchBookings(String keyword) {

        keyword = StudentData.requireText(
                keyword, "Search keyword").toLowerCase();

        List<String[]> result = new ArrayList<>();

        for (String[] row : viewBookings()) {

            for (String value : row) {

                if (value.toLowerCase().contains(keyword)) {
                    result.add(row);
                    break;
                }
            }
        }

        return result;
    }

    // RESCHEDULE BOOKING
    public String[] rescheduleBooking(
            String bookingID, String newScheduleID) {

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

        if (!booking[6].equalsIgnoreCase("Confirmed")
                && !booking[6].equalsIgnoreCase("Pending Payment")) {

            throw new IllegalStateException(
                    "Only active bookings can be rescheduled.");
        }

        String[] schedule =
                StudentData.findById(
                        FileMenu.SCHEDULE, newScheduleID);

        if (schedule == null || schedule.length != 7) {
            throw new IllegalArgumentException(
                    "New schedule not found.");
        }

        // MUST BE THE SAME CLASS
        if (!schedule[1].equals(booking[2])) {
            throw new IllegalArgumentException(
                    "You can only select another schedule of the same class.");
        }

        if (!schedule[6].equalsIgnoreCase("ACTIVE")) {
            throw new IllegalStateException(
                    "New schedule is not available.");
        }

        // CHECK TWO-DAY REQUIREMENT
        validateBookingDate(schedule[2]);

        // PREVENT SELECTING THE SAME SCHEDULE
        if (schedule[0].equals(booking[3])) {
            throw new IllegalArgumentException(
                    "You are already booked for this schedule.");
        }

        // CHECK DUPLICATE BOOKING
        if (alreadyBooked(newScheduleID, bookingID)) {
            throw new IllegalStateException(
                    "You already have a booking for this schedule.");
        }

        // CHECK CAPACITY
        String[] cls =
                StudentData.findClass(booking[2]);

        checkCapacity(cls, newScheduleID);

        // UPDATE BOOKING
        String[] updated = booking.clone();

        updated[3] = newScheduleID;
        updated[5] = schedule[2];

        CRUD.updateRecord(
                "Students", bookingFile, updated);

        return updated;
    }

    // CANCEL BOOKING
    public void cancelBooking(String bookingID) {

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

        if (!booking[6].equalsIgnoreCase("Confirmed")
                && !booking[6].equalsIgnoreCase("Pending Payment")) {

            throw new IllegalStateException(
                    "This booking cannot be cancelled.");
        }

        String[] updated = booking.clone();

        updated[6] = "Cancelled";

        CRUD.updateRecord(
                "Students", bookingFile, updated);
    }
}
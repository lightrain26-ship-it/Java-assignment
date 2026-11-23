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

    // VIEW AVAILABLE SCHEDULES

    public List<String[]> viewAvailableClasses() {

        List<String[]> available = new ArrayList<>();

        List<String[]> schedules =
                CRUD.readFile(StudentData.getFile(FileMenu.SCHEDULE));

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

            if (LocalDate.parse(schedule[2])
                    .isBefore(LocalDate.now())) {
                continue;
            }

            available.add(schedule);
        }

        return available;
    }

    // CHECK EXISTING BOOKINGS FOR CAPACITY

    private int countBookings(
            String classID, String scheduledDate) {

        int count = 0;

        List<String[]> rows =
                CRUD.readFile(bookingFile);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length != 7) {
                continue;
            }

            if (row[2].equals(classID)
                    && row[5].equals(scheduledDate)
                    && row[6].equalsIgnoreCase("Confirmed")) {
                count++;
            }
        }

        return count;
    }

    // CHECK DUPLICATE BOOKING

    private boolean alreadyBooked(
            String classID,
            String scheduledDate,
            String excludeBookingID) {

        for (String[] row : viewBookings()) {

            if (row.length != 7) {
                continue;
            }

            if (row[2].equals(classID)
                    && row[5].equals(scheduledDate)
                    && row[6].equalsIgnoreCase("Confirmed")
                    && !row[0].equals(excludeBookingID)) {
                return true;
            }
        }

        return false;
    }

    // CHECK CLASS CAPACITY

    private void checkCapacity(
            String[] cls,
            String classID,
            String scheduledDate) {

        int maximum = Integer.parseInt(cls[5]);

        int current =
                countBookings(classID, scheduledDate);

        if (current >= maximum) {
            throw new IllegalStateException(
                    "This class has reached maximum capacity.");
        }
    }

    // BOOK CLASS

    public String[] bookClass(String scheduleID) {

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

        LocalDate scheduledDate =
                LocalDate.parse(schedule[2]);

        if (scheduledDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Cannot book a past schedule.");
        }

        String classID = schedule[1];

        String[] cls = StudentData.findClass(classID);

        if (cls == null || !cls[11].equalsIgnoreCase("ACTIVE")) {
            throw new IllegalStateException(
                    "Class is not active.");
        }

        if (alreadyBooked(classID, schedule[2], null)) {
            throw new IllegalStateException(
                    "You have already booked this class.");
        }

        checkCapacity(cls, classID, schedule[2]);

        String bookingID =
                IDGenerator.generateNextID(
                        bookingFile, "BKG");

        // NEW BOOKING STRUCTURE:
        // bookingID | studentID | classID | scheID
        // | bookingDate | scheduledDate | status

        String[] booking = {
            bookingID,
            studentID,
            classID,
            scheduleID,
            LocalDate.now().toString(),
            schedule[2],
            "Confirmed"
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

        if (!booking[6].equalsIgnoreCase("Confirmed")) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be rescheduled.");
        }

        String[] schedule =
                StudentData.findById(
                        FileMenu.SCHEDULE, newScheduleID);

        if (schedule == null || schedule.length != 7) {
            throw new IllegalArgumentException(
                    "New schedule not found.");
        }

        if (!schedule[1].equals(booking[2])) {
            throw new IllegalArgumentException(
                    "You can only select another schedule of the same class.");
        }

        if (!schedule[6].equalsIgnoreCase("ACTIVE")) {
            throw new IllegalStateException(
                    "New schedule is not available.");
        }

        LocalDate newDate =
                LocalDate.parse(schedule[2]);

        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Cannot select a past schedule.");
        }

        if (alreadyBooked(
                booking[2], schedule[2], bookingID)) {
            throw new IllegalStateException(
                    "You already have a booking for this class on that date.");
        }

        String[] cls = StudentData.findClass(booking[2]);

        checkCapacity(cls, booking[2], schedule[2]);

        String[] updated = booking.clone();

        // Update both new schedule ID and date
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

        if (!booking[6].equalsIgnoreCase("Confirmed")) {
            throw new IllegalStateException(
                    "This booking is not active.");
        }

        String[] updated = booking.clone();

        updated[6] = "Cancelled";

        CRUD.updateRecord(
                "Students", bookingFile, updated);
    }
}
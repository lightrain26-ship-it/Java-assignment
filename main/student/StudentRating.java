package student;

import common_code.CRUD;
import common_code.FileMenu;
import common_code.IDGenerator;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentRating {

    private final String studentID;

    private final File ratingFile =
            StudentData.getFile(FileMenu.STUDENTRATE);

    private final File bookingFile =
            StudentData.getFile(FileMenu.BOOKING);

    public StudentRating(String studentID) {
        StudentData.validateId(studentID, "STU");
        this.studentID = studentID;
    }

    // CHECK ATTENDANCE USING SCHEDULE ID
    private boolean hasAttended(String scheID) {

        List<String[]> attendance =
                CRUD.readFile(
                        StudentData.getFile(FileMenu.ATTENDANCE));

        for (int i = 1; i < attendance.size(); i++) {

            String[] record = attendance.get(i);

            if (record.length != 5) {
                continue;
            }

            if (record[0].equals(scheID)
                    && record[1].equals(studentID)) {

                String status = record[2];

                if (status.equalsIgnoreCase("PRESENT")
                        || status.equalsIgnoreCase("LATE")) {

                    return true;
                }
            }
        }

        return false;
    }

    // ADD INSTRUCTOR RATING
    public String[] addRating(
            String bookingID,
            int ratingScore,
            String feedback) {

        bookingID = StudentData.requireText(
                bookingID, "Booking ID");

        if (ratingScore < 1 || ratingScore > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5.");
        }

        feedback = StudentData.requireText(
                feedback, "Feedback");

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

        // ONLY CONFIRMED BOOKINGS CAN BE RATED
        if (!booking[6].equalsIgnoreCase("Confirmed")) {
            throw new IllegalStateException(
                    "This booking cannot be rated.");
        }

        String classID = booking[2];
        String scheID = booking[3];

        // VERIFY SCHEDULE
        String[] schedule =
                StudentData.findById(
                        FileMenu.SCHEDULE, scheID);

        if (schedule == null || schedule.length != 7) {
            throw new IllegalStateException(
                    "Schedule information not found.");
        }

        // VERIFY BOOKING AND SCHEDULE DATE
        if (!booking[5].equals(schedule[2])) {
            throw new IllegalStateException(
                    "Booking date does not match schedule date.");
        }

        // PREVENT RATING BEFORE CLASS DATE
        LocalDate scheduledDate =
                LocalDate.parse(booking[5]);

        if (scheduledDate.isAfter(LocalDate.now())) {
            throw new IllegalStateException(
                    "You cannot rate a class before its scheduled date.");
        }

        // VERIFY ATTENDANCE
        if (!hasAttended(scheID)) {
            throw new IllegalStateException(
                    "You must attend this class before submitting a rating.");
        }

        // FIND INSTRUCTOR
        String[] cls =
                StudentData.findClass(classID);

        if (cls == null || cls.length != 13) {
            throw new IllegalStateException(
                    "Class information not found.");
        }

        String instructorID = cls[1];

        // PREVENT DUPLICATE RATING
        List<String[]> existingRatings = viewRatings();

        for (String[] existing : existingRatings) {

            if (existing.length == 8
                    && existing[1].equals(bookingID)) {

                throw new IllegalStateException(
                        "You have already rated this booking.");
            }
        }

        // GENERATE RATING ID
        String ratingID =
                IDGenerator.generateNextID(
                        ratingFile, "RAT");

        // CREATE RATING RECORD
        // ratingID | bookingID | studentID | classID
        // | instructorID | ratingScore | feedback | ratingDate

        String[] rating = {

            ratingID,
            bookingID,
            studentID,
            classID,
            instructorID,
            String.valueOf(ratingScore),
            feedback,
            booking[5]
        };

        // SAVE RATING
        CRUD.addRecord(
                "Students", ratingFile, rating);

        // UPDATE BOOKING STATUS TO COMPLETED
        String[] updatedBooking = booking.clone();

        updatedBooking[6] = "Completed";

        CRUD.updateRecord(
                "Students", bookingFile, updatedBooking);

        return rating;
    }

    // VIEW OWN RATINGS
    public List<String[]> viewRatings() {

        return StudentData.getStudentRows(
                FileMenu.STUDENTRATE, studentID, 2);
    }

    // SEARCH OWN RATINGS
    public List<String[]> searchRatings(String keyword) {

        keyword = StudentData.requireText(
                keyword, "Search keyword").toLowerCase();

        List<String[]> result = new ArrayList<>();

        for (String[] row : viewRatings()) {

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
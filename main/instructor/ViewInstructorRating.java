package instructor;

import common_code.CRUD;
import common_code.FileMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewInstructorRating {

    private final String instructorID;

    private final File ratingFile =
            new File("data", FileMenu.STUDENTRATE);

    public ViewInstructorRating(String instructorID) {

        if (instructorID == null
                || !instructorID.matches("INS\\d{3}")) {

            throw new IllegalArgumentException(
                    "Invalid instructor ID.");
        }

        this.instructorID = instructorID;
    }

    // VIEW RATINGS FOR LOGGED-IN INSTRUCTOR

    public List<String[]> viewRatings() {

        List<String[]> result = new ArrayList<>();

        List<String[]> rows =
                CRUD.readFile(ratingFile);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length != 8) {
                continue;
            }

            if (row[4].equals(instructorID)) {
                result.add(row);
            }
        }

        return result;
    }

    // SEARCH RATINGS

    public List<String[]> searchRatings(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty.");
        }

        keyword = keyword.trim().toLowerCase();

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

    // TOTAL NUMBER OF RATINGS

    public int getTotalRatings() {
        return viewRatings().size();
    }

    // CALCULATE AVERAGE RATING

    public double getAverageRating() {

        List<String[]> ratings = viewRatings();

        if (ratings.isEmpty()) {
            return 0.0;
        }

        int totalScore = 0;

        for (String[] rating : ratings) {
            totalScore += Integer.parseInt(rating[5]);
        }

        return (double) totalScore / ratings.size();
    }

    // FILTER BY SCORE

    public List<String[]> getRatingsByScore(int score) {

        if (score < 1 || score > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5.");
        }

        List<String[]> result = new ArrayList<>();

        for (String[] row : viewRatings()) {

            if (Integer.parseInt(row[5]) == score) {
                result.add(row);
            }
        }

        return result;
    }
}
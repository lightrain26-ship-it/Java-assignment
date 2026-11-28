package facility_officer;

import Util.CRUD;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FacilityOfficer extends User {

    private String role;

    private final static String FACILITYOFFICER =
            "data/facilitiesOfficer.txt";

    public FacilityOfficer(String userID, String password, String name, String email, String phone, LocalDate dob, String gender, LocalDate dateJoined, String role) {
        super(userID, password, name, email, phone, dob, gender, dateJoined);
        this.role = role;
    }

    public FacilityOfficer() {};

    public String getRole() {
        return role;
    }

    // any setter method?


    public static List<FacilityOfficer> loadFO()
            throws IllegalArgumentException {

        File file = new File(FACILITYOFFICER);
        List<String[]> read = CRUD.readFile(file);
        read.remove(0);

        List<FacilityOfficer> vals = new ArrayList<>();

        try {
            for (String[] s : read) {
                vals.add(new FacilityOfficer(
                        s[0], s[1], s[3], s[4], s[5], LocalDate.parse(s[6]), s[7], LocalDate.parse(s[8]), s[2]
                ));
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array Index Out Of Bound occurs, Facility Officer");

        } catch (DateTimeParseException e) {
            System.out.println("Invalid parsing the time format, Facility Officer");

        } catch (NullPointerException e) {
            System.out.println("There is Null value, Facility Officer");
        }

        return vals;
    }


    public static FacilityOfficer findFO(String id) {

        for (FacilityOfficer l : loadFO()) {
            if (l.getId().equals(id)) {
                return l; // returns a complete Facility Officer object
            }
        }

        return new FacilityOfficer();
    }
}

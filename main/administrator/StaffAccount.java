package administrator;

import common_code.User;
import java.time.LocalDate;

public class StaffAccount extends User {
    private String role;

    public StaffAccount(String role, String id, String password, String name, String email,
                        String phone, LocalDate dob, String gender, LocalDate dateJoined) {
        super(id, password, name, email, phone, dob, gender, dateJoined);
        this.role = role;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public String[] toFileRow() {
        return new String[]{getId(), getPassword(), getRole(), getName(), getEmail(),
                getPhone(), getDob().toString(), getGender(), getDateJoined().toString()};
    }

    public static StaffAccount fromRow(String[] row) {
        return new StaffAccount(row[2], row[0], row[1], row[3], row[4], row[5],
                LocalDate.parse(row[6]), row[7], LocalDate.parse(row[8]));
    }

    public boolean matches(String keyword) {
        keyword = keyword.toLowerCase();
        return getId().toLowerCase().contains(keyword)
                || getName().toLowerCase().contains(keyword)
                || getRole().toLowerCase().contains(keyword)
                || getEmail().toLowerCase().contains(keyword)
                || getPhone().contains(keyword);
    }

    @Override public String toString() {
        return getId() + " | " + getRole() + " | " + getName() + " | " + getEmail() + " | "
                + getPhone() + " | " + getDob() + " | " + getGender() + " | " + getDateJoined();
    }
}
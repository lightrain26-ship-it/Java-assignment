package administrator;

import java.time.LocalDate;

public class InstructorAccount extends StaffAccount {
    private String specialty;
    private String grade;

    public InstructorAccount(String id, String password, String name, String email, String phone,
                      LocalDate dob, String gender, String specialty, String grade, LocalDate dateJoined) {
        super("Instructor", id, password, name, email, phone, dob, gender, dateJoined);
        this.specialty = specialty;
        this.grade = grade;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String[] toFileRow() {
        return new String[]{getId(), getPassword(), getRole(), getName(), getEmail(), getPhone(),
                getDob().toString(), getGender(), specialty, grade, getDateJoined().toString()};
    }

    public static InstructorAccount fromInstructorRow(String[] row) {
        return new InstructorAccount(row[0], row[1], row[3], row[4], row[5], LocalDate.parse(row[6]),
                row[7], row[8], row[9], LocalDate.parse(row[10]));
    }

    @Override
    public boolean matches(String keyword) {
        keyword = keyword.toLowerCase();
        return super.matches(keyword)
                || specialty.toLowerCase().contains(keyword)
                || grade.toLowerCase().contains(keyword);
    }
}
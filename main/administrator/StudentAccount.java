package administrator;

public class StudentAccount {
    private String specialty;
    private String grade;
    
    public StudentAccount(String id, String password, String name, String email, String phone, LocalDate dob, String gender, String specialty, String grade, LocalDate dateJoined) {
        super(id, password, name, email, phone, dob, gender, dateJoined);
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
    public String getRole() {
        return "Student";
    }

    //Student.txt has 10 columns and no role column
    @Override public String[] toFileRow() {
        return new String[] {getID(), 
                            getPassword(),
                            getName(), 
                            getEmail(), 
                            getPhone(), 
                            getDob().toString(), 
                            getGender(), 
                            specialty, 
                            grade, 
                            getDateJoined().toString()};
    }

    public static StudentAccount fromRow(String[] row) {
        return new StudentAccount(row[0], row[1], row[2], row[3], row[4],
                                row[5], row[6], row[7], row[8], LocalDate.parse(row[9]));
    }

    //Search every field except password
    public boolean matches(String keyword) {
        keyword = keyword.toLowerCase();
        return getId().toLowerCase().contains(keyword) 
            || getName().toLowerCase().contains(keyword)
            || getEmail().toLowerCase().contains(keyword)
            || getPhone().contains(keyword)
            || getDob().toString().contains(keyword)
            || getGender().equalsIgnoreCase(keyword)
            || specialty.toLowerCase().contains(keyword)
            || grade.toLowerCase().contains(keyword)
            || getDateJoined().toString().contains(keyword);
    }

    @Override
    public Stirng toStirng() {
        return getID() + " | " + getName() + " | " + getEmail() + " | " + getPhone() + " | " + 
            getDob() + " | " + getGender + " | " + specialty + " | " + grade + " | " + getDateJoined();
    }
}

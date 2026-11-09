package manager;

import common_code.*;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class AdminService {
    private String managerID;                          // who is using the service (for logs)
    
    private final File adminFile = new File("data", FileMenu.ADMIN);
    private final File logFile = new File("data", FileMenu.MANAGERRECORD);
    
    public AdminService(String managerID) {
        this.managerID = managerID;
    }
 // One place that builds every log row
    private void logAction(String type, String accountID, String accountName,String field, String oldValue, String newValue){
        String recordID = IDGenerator.generateNextID(logFile, "REC");
        String[] row = {recordID, type, accountID, accountName, "Administrator", managerID,
                LocalDate.now().toString(), field, oldValue, newValue};
        WriteFunction.logChange("Managers", row);
    }

    // Returns all admins that are not deleted
    public List<AdminAccount> getActiveAdmins() {
        File adminFile = new File("data", FileMenu.ADMIN);
        List<String[]> rows = CRUD.readFile(adminFile);       // 1. read the file

        List<AdminAccount> admins = new ArrayList<>();        // 2. empty list to collect

        for (int r = 1; r < rows.size(); r++) {               // 3. skip header (row 0)
            String[] row = rows.get(r);
            if (row[1].equals(FileMenu.EMPTY_DOMAIN)) {       //    deleted -> skip
                continue;
            }
            admins.add(AdminAccount.fromRow(row));            //    row -> object, add it
        }

        return admins;                                        // 4. give the list back
    }

    // Returns the one active admin with this ID, or throws if none
    public AdminAccount findActiveAdmin(String adminID) {
        File adminFile = new File("data", FileMenu.ADMIN);
        List<String[]> rows = CRUD.readFile(adminFile);

        for (int r = 1; r < rows.size(); r++) {                // skip header
            String[] row = rows.get(r);
            if (row[0].equals(adminID) && !row[1].equals(FileMenu.EMPTY_DOMAIN)) {
                return AdminAccount.fromRow(row);              // found: return it now
            }
        }

        throw new NoSuchElementException("No active administrator with ID: " + adminID);
    }
 // Creates ID, email, password, date joined; saves; logs
    public AdminAccount addAdmin(String name, String phone, LocalDate dob, String gender){
        String adminID = IDGenerator.generateNextID(adminFile, "ADM");
        String email = IDGenerator.generateEmail(adminID);
        String password = "admin" + adminID.substring(3);

        AdminAccount admin = new AdminAccount(adminID, password, name, email, phone, dob, gender, LocalDate.now());

        CRUD.addRecord("Managers", adminFile, admin.toFileRow());
        logAction("ADD", adminID, admin.getName(), FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN);
        return admin;
    }
// Keeps the ID, sets the rest to N/A; logs; returns the admin as it was
    public AdminAccount deactivateAdmin(String adminID){
        AdminAccount admin = findActiveAdmin(adminID); // fails first if not found
        CRUD.deactivateRecord(adminID, "Managers", adminFile);
        logAction("DELETE", adminID,admin.getName(), FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN, FileMenu.EMPTY_DOMAIN);
        return admin;
    }

    public AdminAccount updateAdmin(String adminID, String name, String phone, LocalDate dob, String gender){
        AdminAccount oldAdmin = findActiveAdmin(adminID);
        AdminAccount newAdmin = new AdminAccount(oldAdmin.getId(), oldAdmin.getPassword(), name,
                oldAdmin.getEmail(), phone, dob, gender, oldAdmin.getDateJoined());
        String[] oldRow = oldAdmin.toFileRow();
        String[] newRow = newAdmin.toFileRow();
        if(Arrays.equals(oldRow, newRow)){
            throw new IllegalStateException("Nothing has been changed.");
        }
        CRUD.updateRecord("Managers", adminFile, newRow);
        String[] header = CRUD.readFile(adminFile).get(0);
        for (int x = 0; x < newRow.length; x++) {
            if (!oldRow[x].equals(newRow[x])) {
                logAction("UPDATE", adminID, newAdmin.getName(), header[x], oldRow[x], newRow[x]);
            }
        }
        return newAdmin;
    }

    public List<AdminAccount>  searchByAdminName(String input){
        String keyword = input.trim().toLowerCase();
        List<AdminAccount> results = new ArrayList<>();
        for (AdminAccount admin : getActiveAdmins()) {
            if (admin.getName().toLowerCase().contains(keyword)) {
                results.add(admin);
            }
        }
        return results;
    }

    public List<AdminAccount> searchByJoinedMonth(YearMonth month){
        List<AdminAccount> results = new ArrayList<>();
        for (AdminAccount admin : getActiveAdmins()) {
            if (YearMonth.from(admin.getDateJoined()).equals(month)) {
                results.add(admin);
            }
        }
        return results;
    }
}

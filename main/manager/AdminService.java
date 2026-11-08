package manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import common_code.*;

public class AdminService {
    private String managerID;                          // who is using the service (for logs)

    public AdminService(String managerID) {
        this.managerID = managerID;
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
}

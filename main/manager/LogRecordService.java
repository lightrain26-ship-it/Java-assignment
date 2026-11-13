package manager;

import common_code.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogRecordService {
    private String managerID;                          // who is using the service (for own record)
    
    
    private final File adminRecordFile  = new File("data", FileMenu.ADMINRECORD);
    private final File managerRecordFile = new File("data", FileMenu.ADMINRECORD);
    
    
    private boolean isVisible(LogRecord record){
        String performer = record.getperformedBy();
        if(performer.toUpperCase().startsWith("MAN")){
            return performer.equalsIgnoreCase(managerID);
        }
        return true;
    }

    private boolean isAdminEntry(LogRecord record){
        return record.getperformedBy().toUpperCase().startsWith("ADM");
    }
    
    public LogRecordService(String managerID) {
        this.managerID = managerID;
    }

    public List<LogRecord> getRecords(File file){
        // 1. read the file
        List<String[]> rows = CRUD.readFile(file);       

        List<LogRecord> managers = new ArrayList<>();        // 2. empty list to collect

        for (int r = 1; r < rows.size(); r++) {               // 3. skip header (row 0)
            String[] row = rows.get(r);
            managers.add(LogRecord.fromRow(row));            //    row -> object, add it
        }
        return managers;

    }

    public List<LogRecord> filterRecords(File file, String performedBy, String actionType){
        List<LogRecord> admins = new ArrayList<>();

        for (LogRecord record : getRecords(file)) {
            // check 1: does the admin/manager ID match? (empty = any)
            boolean idMatches = performedBy == null || performedBy.isEmpty()
                    || record.getperformedBy().equalsIgnoreCase(performedBy);

            // check 2: does the action type match? (empty = any)
            boolean actionMatches = actionType == null || actionType.isEmpty()
                    || record.getActionType().equalsIgnoreCase(actionType);

            // keep the record only if all three are true
            if (idMatches && actionMatches && isVisible(record)) {
                admins.add(record);
            }
        }
        return admins;
    }

    // Keeps only the entries performed by an administrator
    private List<LogRecord> keepAdminEntries(List<LogRecord> records) {
        List<LogRecord> result = new ArrayList<>();
        for (LogRecord record : records) {
            if (isAdminEntry(record)) {
                result.add(record);
            }
        }
        return result;
    }

    public List<LogRecord> getAllAdminRecords(){
        return keepAdminEntries(filterRecords(adminRecordFile, null, null));
    }
    public List<LogRecord> getMyRecords(){
        return filterRecords(managerRecordFile, managerID, null);
    }

    public List<LogRecord> searchMyRecords(String actionType){
        return filterRecords(managerRecordFile, managerID, actionType);
    }

    public List<LogRecord> searchAdminRecords(String adminID, String actionType){
        return keepAdminEntries(filterRecords(adminRecordFile, adminID, actionType));
    }
    
}


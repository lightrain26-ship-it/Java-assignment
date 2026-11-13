package manager;

import java.time.LocalDate;

public class LogRecord {
    private String recordID;
    private String actionType;
    private String accountID;
    private String accountName;
    private String accountRole;
    private String performedBy;
    private LocalDate actionDate;
    private String fieldChanged;
    private String oldValue;
    private String newValue;

    public LogRecord(String recordID,
                     String actionType,
                     String accountID,
                     String accountName,
                     String accountRole,
                     String performedBy,
                     LocalDate actionDate,
                     String fieldChanged,
                     String oldValue,
                     String newValue){
        this.recordID = recordID;
        this.actionType = actionType;
        this.accountID = accountID;
        this.accountName = accountName;
        this.accountRole = accountRole;
        this.performedBy = performedBy;
        this.actionDate = actionDate;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
                     }

        public static LogRecord fromRow(String[] row){
        return new LogRecord(
            row[0],                    // recID
            row[1],                    // action TYPE
            row[2],                    // ACCOUNT ID
            row[3],                    // ACCOUNT NAME
            row[4],                    // ACCOUNT ROLE
            row[5],                   // PERFORM BY - ADMIN OR MANAGER
            LocalDate.parse( row[6]),  // ACTION DATE
            row[7],                   // FILECHANGED
            row[8],                   // OLD VALUE
            row[9]                  // NEW VALUE
        ); 
    }

    public String getActionType(){
        return actionType;
    }

    public String getperformedBy(){
        return performedBy;
    }

    @Override
    public String toString(){
        return recordID + " | " + actionType + " | " + accountID + " | " + accountName + " | "
             + accountRole + " | " + performedBy + " | " + actionDate + " | "
             + fieldChanged + " | " + oldValue + " | " + newValue;
    }
}

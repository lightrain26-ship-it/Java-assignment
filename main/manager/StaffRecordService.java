package manager;

import common_code.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffRecordService {

    public String[] getHeader(File file){
        List<String[]> row = CRUD.readFile(file);
        if(row.isEmpty()){
            throw new IllegalStateException("File has no header: " + file.getName());
        } else{
            return row.get(0);
        }
    }

        public List<String[]> getRows(File file){
        // 1. read the file
        List<String[]> rows = CRUD.readFile(file);       

        List<String[]> staffRows = new ArrayList<>();        // 2. empty list to collect

        for (int r = 1; r < rows.size(); r++) {               // 3. skip header (row 0)
            String[] row = rows.get(r);
            staffRows.add(row);            //    row -> object, add it
        }
        return staffRows;

    }

    public List<String[]> searchRows(File file, String columnName, String value){
        String[] header = getHeader(file);
        int index = -1;                                   // -1 means "not found yet"

        for (int i = 0; i < header.length; i++) {         // check each column name
            if (header[i].equalsIgnoreCase(columnName)) {
                index = i;                                // found: remember its position
                break;                                    // stop looking
            }
        }

        if (index == -1) {                                // after the loop: nothing matched
            throw new IllegalArgumentException("No column named: " + columnName);
        }

        List<String[]> result = new ArrayList<>();

        for (String[] row : getRows(file)) {              // read once, go through each row
            if (index < row.length && row[index].equalsIgnoreCase(value)) {
                result.add(row);                          // matches: keep it
            }
        }
            return result;
    }

    public List<String[]> searchByDateRange(File file, String dateColumn, LocalDate from, LocalDate to){
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must not be after the end date.");
        }

        // find the date column's position
        String[] header = getHeader(file);
        int index = -1;
        for (int i = 0; i < header.length; i++) {
            if (header[i].equalsIgnoreCase(dateColumn)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("No column named: " + dateColumn);
        }

        // keep the rows whose date falls inside the range
        List<String[]> result = new ArrayList<>();
        for (String[] row : getRows(file)) {
            if (index >= row.length) {
                continue;                              // broken row: skip
            }
            try {
                LocalDate date = LocalDate.parse(row[index].substring(0, 10));   // yyyy-MM-dd part
                if (!date.isBefore(from) && !date.isAfter(to)) {
                    result.add(row);
                }
            } catch (RuntimeException e) {
                // bad or missing date in this row: skip it
            }
        }
        return result;
    }
}


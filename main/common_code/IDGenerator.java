package common_code;

import java.io.File;
import java.util.List;
public class IDGenerator {
    public static String fillNum(int num, String prefix, int paddingLength ){

        String numFormat = "%0" + paddingLength + "d";
        
        return prefix + String.format(numFormat, num);
    }

    public static String generateNextID(File file, String prefix){
        List<String[]> rows = CRUD.readFile(file);

        if (rows.isEmpty()){
            throw new IllegalStateException("The file has no header:" + file.getName());        
        }
        int maxNum = 0;
        for (int i = 1; i < rows.size(); i++){
            String[] row = rows.get(i);
            String id = row[0];
            if (id.startsWith(prefix)){
                String numPart = id.substring(prefix.length());
                try{
                    int num = Integer.parseInt(numPart);
                    if (num > maxNum){
                        maxNum = num;
                    }
                } catch (NumberFormatException e){
                    // Ignore invalid ID format
                }
            }
        }
        return fillNum(maxNum + 1, prefix, 3);
    }

    public static String generateEmail(String id){
        return id + FileMenu.EMAIL_DOMAIN;

    }

    // gets the number from an ID like "EQ024" -> 24, or 0 if the text is not an ID with this prefix
    private static int numberFromId(String text, String prefix) {
        if (text == null) {
            return 0;
        }

        text = text.trim();

        if (!text.startsWith(prefix)) {
            return 0;
        }

        // Cut the prefix and turn the rest into numbers (Ex: "EQ024" -> "024" -> 24)
        try {
            return Integer.parseInt(text.substring(prefix.length()));
        } 
        catch (NumberFormatException e) {
            return 0;   // if its not a real ID, ignore it
        }
    }

    // Make the next ID and also check the log records. If an ID is deleted never use it again.
    public static String generateNextID(File file, File logFile, String prefix) {
        List<String[]> rows = CRUD.readFile(file);

        if (rows.isEmpty()) {
            throw new IllegalStateException("The file has no header:" + file.getName());
        }

        int maxNum = 0;

        // main file: ID is in column 0, row 0 is the header
        for (int i = 1; i < rows.size(); i++) {
            int num = numberFromId(rows.get(i)[0], prefix);
            if (num > maxNum) {
                maxNum = num;
            }
        }

        // log file: check every cell, because the ID can be in any column
        if (logFile != null && logFile.exists()) {
            for (String[] row : CRUD.readFile(logFile)) {
                if (row == null) {
                    continue;
                }
                
                for (String cell : row) {
                    int num = numberFromId(cell, prefix);
                    if (num > maxNum) {
                        maxNum = num;
                    }
                }
            }
        }

        return fillNum(maxNum + 1, prefix, 3);
    }    
}
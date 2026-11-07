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

}
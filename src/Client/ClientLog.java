package Client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;


public class ClientLog {
    private static String dir = System.getProperty("user.dir")+"\\src\\Client\\logs\\";

    public static void createLog(String id) {

        try {
            File file = new File(dir);
            file.mkdirs();

            String fileName = dir + id + ".txt";
            file = new File(fileName);
            file.createNewFile();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void log(String id, String logMsg){
        try {

            File file = new File(dir + id + ".txt");

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logMsg = timestamp.toString() + " " + logMsg;

            bw.write(logMsg);
            bw.newLine();

            bw.close();
            fw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}

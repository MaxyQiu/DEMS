package RM4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;


public class ServerLog {
    private static String dir = System.getProperty("user.dir")+"\\src\\RM4\\logs\\";

    public static void createLog(String serverName) throws Exception{

        File file = new File(dir);
        file.mkdirs();

        String fileName = dir+serverName.toLowerCase()+".txt";
        file = new File(fileName);
        file.createNewFile();

    }

    public static void log(String servername, String logMsg){
        try {
            servername = servername.toLowerCase();
            File file = new File(dir + servername + ".txt");

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

        }
    }


}

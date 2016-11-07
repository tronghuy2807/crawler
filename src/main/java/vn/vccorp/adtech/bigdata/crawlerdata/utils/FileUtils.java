package vn.vccorp.adtech.bigdata.crawlerdata.utils;

import org.apache.commons.configuration.Configuration;
import vn.vccorp.adtech.bigdata.crawlerdata.global.SystemInfo;

import java.io.*;

/**
 * Created by thuyenhx on 23/10/2015.
 */
public class FileUtils {

    private static FileWriter fileWritter;
    private static PrintWriter printWriter;

    static {
        init();
    }

    public static synchronized void init() {
        Configuration conf = SystemInfo.getConfiguration();
        String fileName = conf.getString("file.name");
        File file = new File(fileName);

        try {
            //if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            fileWritter = new FileWriter(file.getName(),true);
            printWriter = new PrintWriter(fileWritter);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized static PrintWriter getInstanceWriter() {
        return printWriter;
    }

    public static void closeWriter() {
        printWriter.close();
    }
}

package inesc_id.pt.motivandroid.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class will provide utility to read logs.
 *
 * @author Chintan Rathod (http://chintanrathod.com)
 */
public class LogsUtil{

    public static String INIT_LOG_TAG = "_$_";

    public static StringBuilder readLogs() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.contains("_$_")){
                    logBuilder.append(line + "\n");
                }
            }
        } catch (IOException e) {
        }
        return logBuilder;
    }
}

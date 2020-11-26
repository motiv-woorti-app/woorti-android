package inesc_id.pt.motivandroid.deprecated;

import android.content.Context;
import android.os.Environment;

import com.crashlytics.android.Crashlytics;

import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import inesc_id.pt.motivandroid.utils.DateHelper;

import static org.joda.time.DateTimeZone.UTC;

@Deprecated
public class CustomLogger {

    public static String LOG_FILE_NAME = "MOTIV_LOG_FILE";

    private static CustomLogger instance;
    private Context context;
    Long currentlyLoggedTimestamp;

    public static CustomLogger getInstance(Context context) {

        if(instance == null){
            instance = new CustomLogger(context);
        }
        return instance;
    }

    private CustomLogger(Context context) {

        this.context = context;


//        currentlyLoggedTimestamp = SharedPreferencesUtil.readCurrentLoggedTimestamp(context, -1);
//
//        if(currentlyLoggedTimestamp == 0){
//            SharedPreferencesUtil.writeCurrentLoggedTimestamp(context, new DateTime(UTC).getMillis());
//        }

    }

    public void log(int priority, String LOG_TAG, String message){

        Crashlytics.log(priority, LOG_TAG, message);



    }

    public static void log(int priority, String LOG_TAG, String message, boolean writeToLogFile){

        Crashlytics.log(priority, LOG_TAG, message);

        if (writeToLogFile){

            insertNewLogEntry(new DateTime(UTC).getMillis(), LOG_TAG, message);

        }


    }

    synchronized
    private static void insertNewLogEntry(long logTimeStamp, String tag, String message){

        if (!isLoggedDateFromToday(logTimeStamp)){
//            SharedPreferencesUtil.writeCurrentLoggedTimestamp(context, logTimeStamp);
//            deleteLog(context);
//            appendLog(context, tag + " " + message, logTimeStamp);
        }


    }

    private static boolean isLoggedDateFromToday(long logTimestamp) {

//        if(DateHelper.isSameDay(logTimestamp, currentlyLoggedTimestamp)){
//            return true;
//        }else{
//            return false;
//        }
return false;
    }

    public static boolean deleteLog(Context context) {
        boolean result = true;
        File root = Environment.getExternalStorageDirectory(); //con.getExternalFilesDir(null);
        File logFile = new File(root, LOG_FILE_NAME);
        if (logFile.isFile()) {
            result = logFile.delete();
        }
        return result;
    }

    public static void appendLog(Context context, String text, long timestamp) {
        File root = Environment.getExternalStorageDirectory(); //con.getExternalFilesDir(null);
        File logFile = new File(root, LOG_FILE_NAME);
        String timeStamp = DateHelper.getDateFromTSString(timestamp);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                    true));
            buf.append(timeStamp+" ");
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package inesc_id.pt.motivandroid.deprecated;

import android.content.Context;

import org.joda.time.DateTime;

import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.utils.DateHelper;

import static org.joda.time.DateTimeZone.UTC;

@Deprecated
public class DailyLogManager {

    private static DailyLogManager instance;
    private Context context;
    PersistentTripStorage persistentTripStorage;
    Long currentlyLoggedTimestamp;

    public static DailyLogManager getDailyLogManger(Context context) {

        if(instance == null){
            instance = new DailyLogManager(context);
        }
        return instance;
    }

    private DailyLogManager(Context context) {

        this.context = context;
        persistentTripStorage = new PersistentTripStorage(context);
        currentlyLoggedTimestamp = SharedPreferencesUtil.readCurrentLoggedTimestamp(context, -1);

        if(currentlyLoggedTimestamp == 0){
            SharedPreferencesUtil.writeCurrentLoggedTimestamp(context, new DateTime(UTC).getMillis());
        }

    }

    synchronized
    public void insertNewLogEntry(long logTimeStamp, String tag, String message, Boolean persistentLog){

        if (!isLoggedDateFromToday(logTimeStamp)){
//            persistentTripStorage.dropAllSavedLogs();
            SharedPreferencesUtil.writeCurrentLoggedTimestamp(context, logTimeStamp);
        }

//        persistentTripStorage.writeLogEntry();

    }

    private boolean isLoggedDateFromToday(long logTimestamp) {

        if(DateHelper.isSameDay(logTimestamp, currentlyLoggedTimestamp)){
            return true;
        }else{
            return false;
        }

    }


}

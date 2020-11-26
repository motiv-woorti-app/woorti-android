package inesc_id.pt.motivandroid.deprecated;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.joda.time.DateTime;

import inesc_id.pt.motivandroid.data.ActivityDataContainer;

import static org.joda.time.DateTimeZone.UTC;


@Deprecated
public class ActivityRecogService extends IntentService{

    public static boolean shouldStop = false;

    public ActivityRecogService() {
        super("ActivityRecogService");

        Log.e( "ActivityRecogService", "construtor: "+ shouldStop);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            if(isShouldStop()){
                stopSelf();
            }else {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                Log.e("ActivityRecogition", "intent received");

                //handleDetectedActivities(result.getProbableActivities());

                Intent localIntent = new Intent("ActivityDetected");

                ActivityDataContainer resultContainer = new ActivityDataContainer(new DateTime(UTC).getMillis(), result);

                for(DetectedActivity da : result.getProbableActivities()){

                    Log.e("--recog", da.getType() + " " + da.getConfidence());

                }

                // Broadcast the result.
                localIntent.putExtra("result", resultContainer);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

                //ActivityDataContainer toBeSent = new ActivityDataContainer(new DateTime(UTC).getMillis(), result);
                //dbDriver.sendActivityData(toBeSent);
            }
        }
    }

    public static synchronized boolean isShouldStop() {
        return shouldStop;
    }

    public static synchronized void setShouldStop(boolean shouldStop) {
        ActivityRecogService.shouldStop = shouldStop;
    }
}

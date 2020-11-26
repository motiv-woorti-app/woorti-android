package inesc_id.pt.motivandroid;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.LinkedList;

import android.os.Handler;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.ActivityDataContainer;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.persistence.TSMSnapshotHelper;
import inesc_id.pt.motivandroid.surveyNotification.CheckSurveysNotificationsReceiver;
import inesc_id.pt.motivandroid.tripStateMachine.TripStateMachine;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.NotificationHelper;

import static org.joda.time.DateTimeZone.UTC;

/**
 * ActivityRecognitionService
 *
 *  One of the main components of the app. This service is always running, in the foreground. Is
 *  responsible for dealing with retrieving data from the sensors (location and acceleration values)
 *  and for passing it to the TripStateMachine. Also when not in trip, it decides when to stop or
 *  start requesting location data.
 *
 *
 * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
 * developed in the context of the European research project MoTiV (motivproject.eu). The
 * code was developed by partner INESC-ID with contributions in graphics design by partner
 * TIS. The Woorti app development was one of the outcomes of a Work Package of the MoTiV
 * project.
 * The Woorti app was originally intended as a tool to support data collection regarding
 * mobility patterns from city and country-wide campaigns and provide the data and user
 * management to campaign managers.
 *
 * The Woorti app development followed an agile approach taking into account ongoing
 * feedback of partners and testing users while continuing under development. This has
 * been carried out as an iterative process deploying new app versions. Along the
 * timeline, various previously unforeseen requirements were identified, some requirements
 * Were revised, there were requests for modifications, extensions, or new aspects in
 * functionality or interaction as found useful or interesting to campaign managers and
 * other project partners. Most stemmed naturally from the very usage and ongoing testing
 * of the Woorti app. Hence, code and data structures were successively revised in a
 * way not only to accommodate this but, also importantly, to maintain compatibility with
 * the functionality, data and data structures of previous versions of the app, as new
 * version roll-out was never done from scratch.
 * The code developed for the Woorti app is made available as open source, namely to
 * contribute to further research in the area of the MoTiV project, and the app also makes
 * use of open source components as detailed in the Woorti app license.
 * This project has received funding from the European Union’s Horizon 2020 research and
 * innovation programme under grant agreement No. 770145.
 * This file is part of the Woorti app referred to as SOFTWARE.
 */

public class ActivityRecognitionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityRecognitionService.class.getSimpleName());

    public static final String PRIMARY_NOTIF_CHANNEL = "default";
    public static final int PRIMARY_FOREGROUND_NOTIF_SERVICE_ID = 1001;

//    private final String LOG_TAG = LogsUtil.INIT_LOG_TAG + "ARService";

    private final boolean fullDetectionMode = true;

    ArrayList<ActivityDataContainer> ActivitiesDetected;

    TripStateMachine tripStateMachine;

    Context context;

    int testActivity=7;

    FusedLocationProviderClient fusedLocationProviderClient;

    public static boolean serviceRunning=false;

//    boolean testing=false;
//    boolean tracking = false;

    //////// Acceleration detection

    SensorManager mSensorManager;
    Sensor mSensor;

    int startId = 0;

    public ActivityRecognitionService() {
        //super("ActivityRecognitionService");
        LOG.debug("Construtor");

        ActivitiesDetected = new ArrayList<ActivityDataContainer>();
        accelerationDataLinkedList = new LinkedList<>();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Binding
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final IBinder mBinder = new ActivityRecognitionBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
        //return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Accelerometer callbacks
    ////////////////////////////////////////////////////////////////////////////////////////////////

    double gravity[] = {0,0,0};
    double linear_acceleration[] = {0,0,0};
    int counter = 0;
    final double alpha = 0.8;

    boolean isCalibrated = false;
    long lastAccelerationSampleTS=0;

    LinkedList<AccelerationData> accelerationDataLinkedList;
    int i = 0;

    long lastActivatedGPSTS = 0l;

    long timeToTestGps = 5 * 1000 * 60; //5 minutes in milliseconds

    boolean currentlyRequestingLocations = false;

    long referenceTimestamp = 0l;
    long referenceEventTimestamp = 0l;

    @SuppressWarnings("MissingPermission")
    @Override
    public void onSensorChanged(SensorEvent event){
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        if(referenceTimestamp == 0l){
            //set reference timestamp
            referenceTimestamp = new DateTime(UTC).getMillis();
            referenceEventTimestamp = event.timestamp;
        }

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        if(isCalibrated) {

            //timestamp is in nanoseconds = 1s
            if(event.timestamp >= (lastAccelerationSampleTS+1000000000)) {

                if(tripStateMachine.currentState == TripStateMachine.state.still){

                    //gps was activated, but 6 minutes have passed and the state is still "still" -> remove location updates
                    if((new DateTime(UTC).getMillis() - lastActivatedGPSTS > timeToTestGps) && (lastActivatedGPSTS != 0l)){
                        LOG.debug("GPS was activated, but X minutes have passed and the state is still still -> remove location updates and reinitialize state machine");
                        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                        currentlyRequestingLocations = false;
//                        tripStateMachine.initializeStateMachine(false);
                        lastActivatedGPSTS = 0l;

                    }else{

                        accelerationDataLinkedList.addLast(new AccelerationData(linear_acceleration[0],linear_acceleration[1],linear_acceleration[2],new DateTime(UTC).getMillis()));

                        i++;

                        if(i >= 10){

                            double meanAccel = computeAccelMean(accelerationDataLinkedList);

                            if(meanAccel > keys.MIN_ACCEL){

                                LocationRequest a = buildHighAccuracyAfterMovementRequest();

                                lastActivatedGPSTS = new DateTime(UTC).getMillis();

                                if(!currentlyRequestingLocations){
//                                    tripStateMachine.initializeStateMachine(false);
                                    LOG.debug("Mean acceleration in the last 10 seconds > 3.0 -> ACTIVATING GPS");
                                }else{
                                    LOG.debug("Extending GPS test period");
                                }

                                //if already in gps test period -> just increase the timestamp
                                if(!currentlyRequestingLocations) {
                                    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                    fusedLocationProviderClient.requestLocationUpdates(a,mLocationCallback,null);
                                    currentlyRequestingLocations = true;
                                }

                            }else{

                                LOG.debug("Mean acceleration in the last 10 seconds < 3.0 -> doing nothing");

                            }

                            accelerationDataLinkedList.clear();
                            i=0;


                        }

                    }

                }


                // divide by 1000000 to get the delta in ms
                // add to the reference timestamp to get current timestamp
                long computedTimestamp = (event.timestamp - referenceEventTimestamp)/1000000 + referenceTimestamp;

                tripStateMachine.insertAccelerometerUpdate(new AccelerationData(linear_acceleration[0],linear_acceleration[1],linear_acceleration[2],computedTimestamp));

                lastAccelerationSampleTS = event.timestamp;




                LOG.debug(""+Math.sqrt(linear_acceleration[0]*linear_acceleration[0]+linear_acceleration[1]*linear_acceleration[1]+linear_acceleration[2]*linear_acceleration[2]) + " at " + DateHelper.getDateFromTSString(computedTimestamp));


            }

        }else {
            LOG.debug("Still calibrating accelerometer");
            counter++;
            //wait until the low pass filter

            if (counter > 10) {
                isCalibrated = true;
                lastAccelerationSampleTS = event.timestamp;

            }
        }

    }

    private double computeAccelMean(LinkedList<AccelerationData> accelerationDataLinkedList) {

        double result = 0d;

        for(AccelerationData accelerationData : accelerationDataLinkedList){

            result += Math.sqrt(accelerationData.getxValue()*accelerationData.getxValue()+accelerationData.getyValue()*accelerationData.getyValue()+accelerationData.getzValue()*accelerationData.getzValue());

        }

        return result/accelerationDataLinkedList.size();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class ActivityRecognitionBinder extends Binder {
        public ActivityRecognitionService getService() {
            return ActivityRecognitionService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onTaskRemoved(Intent rootIntent)
    {

//        stopRecognition();
    }


    public void stopRecognition() {

        LOG.debug("Stopself stop recog");

        //unregister listeners
        //do any other cleanup if required
        activitySimulationHandler.removeCallbacks(runnableCode);

            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            currentlyRequestingLocations = false;

            LocalBroadcastManager.getInstance(context).unregisterReceiver(mFullTripReceiver);

            LocalBroadcastManager.getInstance(context).unregisterReceiver(tripStartedReceiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(tripStartedRecoveredReceiver);

            stopAccelerometer();


        serviceRunning = false;


        LocalBroadcastManager.getInstance(context).unregisterReceiver(
                mFullTripReceiver);

        TSMSnapshotHelper tsmSnapshotHelper = new TSMSnapshotHelper(context);
        tsmSnapshotHelper.deleteAllSnapshotRecords();

        //stop service
        stopForeground(true);
        stopSelf(startId);

    }

    public void forceFinishTrip(){

        tripStateMachine.forceFinishTrip(false);

    }

    public void forceStopAccel(){
        stopAccelerometer();
    }

    public void forceStopLoc(){
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        currentlyRequestingLocations = false;
    }

    //testing alarm
    AlarmManager alarmMgr;




    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCreate() {
        super.onCreate();

        LOG.debug("onCreate");

        context = getApplicationContext();

        buildGoogleApiClient();

        //register the broadcast receiver to handle receiving ActivityRecognitionResult
        //from the ActivityRecognitionService
//        LocalBroadcastManager.getInstance(context).registerReceiver(
//                mMessageReceiver, new IntentFilter("ActivityDetected"));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mFullTripReceiver, new IntentFilter("FullTripFinished"));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                tripStartedReceiver, new IntentFilter("FullTripStarted"));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                tripStartedRecoveredReceiver, new IntentFilter("FullTripStartedRecovered"));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                startedSuspecting, new IntentFilter("StartedSuspecting"));

        mGoogleApiClient.connect();

        activitySimulationHandler = new Handler();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //alarm
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        startCheckNotificationsReceiver(alarmMgr);


    }

    // start an alarm to check surveys hourly.
    private void startCheckNotificationsReceiver(AlarmManager alarmMgr) {

        Intent intent = new Intent(getApplicationContext(), CheckSurveysNotificationsReceiver.class);
        PendingIntent checkSurveysAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,5000,
        60*60 * 1000, checkSurveysAlarmIntent); //hourly


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LOG.debug("Service onStartCommand " + startId);
        this.startId = startId;

        serviceRunning = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationHelper.createChannel(this);

        }

        PendingIntent contentIntent = NotificationHelper.buildPendingIntentTripEnded(this, "");

        startForeground(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID, NotificationHelper.buildForegroundNotification(this,
                null,
                getString(R.string.Woorti_Is_Running), contentIntent));

        //per https://medium.com/androiddevelopers/migrating-mediastyle-notifications-to-support-android-o-29c7edeca9b7

        StartStateMachine startStateMachine = new StartStateMachine();
        startStateMachine.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return START_STICKY;
    }


    protected GoogleApiClient mGoogleApiClient;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Google API methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionSuspended(int i) {
        LOG.info( "GoogleApiClient connection suspended, restarting it...");
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mInsideShopNotification);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LOG.error( "GoogleApiClient connection failed!");
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mInsideShopNotification);
        mGoogleApiClient.connect();

    }

    // location callback. passes location updates to the trip state machine
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                LocationDataContainer received = new LocationDataContainer(
                        new DateTime(UTC).getMillis(),
                        location.getAccuracy(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getSpeed(),
                        location.getTime());

//                kalmanFilterTest.filterAndAddLocation(location);

                LOG.debug("Time of fix" + DateHelper.getDateFromTSString(location.getTime()));

                LOG.debug("lat:"+location.getLatitude()+" lng:"+location.getLongitude()
                        + " ac:"+location.getAccuracy() + " " +location.getProvider() + " speed:" + location.getSpeed() + " loc time : " + location.getTime());

                LOG.debug("CSV: " + location.getLatitude() + "," + location.getLongitude() + "," + "ACC " + location.getAccuracy() + " " +
                        DateHelper.getDateFromTSString(new DateTime(UTC).getMillis()));

                tripStateMachine = TripStateMachine.getInstance(context, false, fullDetectionMode);
                tripStateMachine.insertLocationUpdate(received, false);

            }
        };
    };

    @Override
    public void onConnected(Bundle bundle) {
        LOG.debug("GoogleApiClient connected successfully");

        //todo commented to test

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            Notification.Builder builder = new Notification.Builder(this, "pt.inesc.motiv")
//                    .setContentTitle(getString(R.string.app_name))
//                    //.setContentText("Motiv is doing work on the background")
//                    .setSubText("Motiv is doing work on the background")
//                    .setAutoCancel(true);
//
//            Notification notification = builder.build();
//            startForeground(1, notification);
//
//        } else {
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"pt.inesc.motiv")
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setWhen(new DateTime(UTC).getMillis())
//                    .setContentTitle("MotivAndroid")
//                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                    .setContentText("Motiv is running in the background")
//                    .setContentInfo("Info");
//
//            startForeground(1, builder.build());
//        }
//
//
//        StartStateMachine startStateMachine = new StartStateMachine();
//        startStateMachine.execute();









        //Request Activity Recognition data to be gathered
//        activityRecognitionClient.requestActivityUpdates(0,pendingIntent);

        //location part
//        LocationRequest mLocationRequest = buildPowerBalancedLocRequest(); TODO this changed

//        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null); TODO THIS CHANGED


        //tripStateMachine.forceStartTrip();

    }

    private class StartStateMachine extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {

            LOG.debug("Instantiating TripStateMachine");
            tripStateMachine = TripStateMachine.getInstance(context, false, fullDetectionMode);
            startAccelerometer();

            return null;
        }
//
//        protected void onProgressUpdate(Void... progress) {
//
//        }

        protected void onPostExecute(Void... urls) {


        }
    }

    private void showNotificationState(String state){

        PendingIntent contentIntent = NotificationHelper.buildPendingIntentTripEnded(this, "");


        Notification tripEndedNotification = NotificationHelper.buildForegroundNotification(
                this,
                null,
                state,
                contentIntent);

        NotificationHelper.issueNotification(this, tripEndedNotification);
    }

    private void showNotificationStateFullTripHasEnded(String fullTripDateId){

        PendingIntent contentIntent = NotificationHelper.buildPendingIntentTripEnded(this, fullTripDateId);

        String titleToBeShown = getString(R.string.Trip_Has_Ended);
        String notificationToBeShown = getString(R.string.You_Can_Now_Report_About_It_In_Woorti);

        Notification tripEndedNotification = NotificationHelper.buildForegroundNotification(
                this,
                titleToBeShown,
                notificationToBeShown,
                contentIntent);

        NotificationHelper.issueNotification(this, tripEndedNotification);
    }

    Handler activitySimulationHandler;

    private LocationRequest buildHighAccuracyLocRequest(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LOG.debug("Building high accuracy location request");

        return mLocationRequest;

    }

    private LocationRequest buildHighAccuracyAfterMovementRequest(){

        LocationRequest mLocationRequest = new LocationRequest();

        //testing
//        mLocationRequest.setInterval(20000);
        mLocationRequest.setInterval(10000);

        mLocationRequest.setFastestInterval(10000);

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LOG.debug("Building high accuracy location request");

        return mLocationRequest;

    }

    private LocationRequest buildPowerBalancedLocRequest(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(0);

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LOG.debug("Building power balanced location request");

        return mLocationRequest;

    }

    @Deprecated
    public void testChangeModality(int newModality){

        LOG.debug( "Changed simulated modality to: " + ActivityDetected.keys.modalities[newModality]);
        testActivity = newModality;

    }

    @Deprecated
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            LOG.debug("Simulating an event with modality: "+ActivityDetected.keys.modalities[testActivity]);
            ArrayList<ActivityDetected> test = new ArrayList<>();
            test.add(new ActivityDetected(testActivity,100));
            ActivityDataContainer testContainer = new ActivityDataContainer(new DateTime(UTC).getMillis(),test);
            tripStateMachine.insertActivityUpdate(testContainer);

            activitySimulationHandler.postDelayed(runnableCode, 100);
        }
    };


    // callback called when the trip state machine broadcasts that a trip has ended and the service
    // needs to start requesting location data.
    @SuppressWarnings("MissingPermission")
    private BroadcastReceiver mFullTripReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

                fusedLocationProviderClient.removeLocationUpdates(mLocationCallback); // todo be careful this has been changed
                lastActivatedGPSTS = 0l;
                currentlyRequestingLocations = false;

                LOG.debug("Fulltrip finished --- remove location updates / lastActivatedGPSTS = 0l");

            Boolean realTrip = intent.getBooleanExtra("isTrip", false);

            if(realTrip){
                String dateId = intent.getStringExtra("date");
                showNotificationStateFullTripHasEnded(dateId);
            }else{
                showNotificationState(getString(R.string.Woorti_Is_Running));
            }

        }
    };

    // callback called when the trip state machine broadcasts that has started a trip and the service
    // needs to start requesting location data
    @SuppressWarnings("MissingPermission")
    private BroadcastReceiver tripStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //startAccelerometer(); todo be careful this has been changed

            LOG.debug( "Full trip has started - broacast received");

            //Request Location data to be gathered
            LocationRequest mLocationRequest = buildHighAccuracyLocRequest();

                fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
                currentlyRequestingLocations = true;

            if (!intent.getBooleanExtra("ManualTripStart", false)){

                String notificationToBeShown = getApplicationContext().getString(R.string.Woorti_Just_Detected_Start_Trip);
                showNotificationState(notificationToBeShown);

            }

        }
    };

    // callback called when the trip state machine broadcasts that a trip might be starting and the
    //service needs to start requesting location data
    @SuppressWarnings("MissingPermission")
    private BroadcastReceiver startedSuspecting = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //startAccelerometer(); todo be careful this has been changed

            LOG.debug("Started suspecting - received broadcast from TSM");

            //Request Location data to be gathered
            LocationRequest mLocationRequest = buildHighAccuracyLocRequest();

            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
            lastActivatedGPSTS = DateTime.now().getMillis();
            currentlyRequestingLocations = true;


        }
    };

    // callback called when the trip state machine broadcasts that a trip has been recovered and the
    //service needs to start requesting location data
    @SuppressWarnings("MissingPermission")
    private BroadcastReceiver tripStartedRecoveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //startAccelerometer(); todo be careful this has been changed

            LOG.debug("Full trip has been recovered - broadcast message received");

            //Request Location data to be gathered
            LocationRequest mLocationRequest = buildHighAccuracyLocRequest();

                fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
                currentlyRequestingLocations = true;


            showNotificationState("Trip state has been recovered. Started at " + DateHelper.getDateFromTSString(new DateTime(UTC).getMillis()));

        }
    };

    private void stopAccelerometer(){
        LOG.debug("stopping accelerometer startid" + startId);
        mSensorManager.unregisterListener(this);


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LOG.debug("onDestroy()");

        LOG.error("Cancel alarm");

        Intent intent = new Intent(getApplicationContext(), KeepAwakeReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        alarmMgr.cancel(alarmIntent);

    }



    private void startAccelerometer(){
//        mSensorManager.registerListener(this,mSensor,1000000);
        mSensorManager.registerListener(this,mSensor,1000000, 0);
        LOG.debug(mSensor.toString());
        LOG.debug("fifo max event count" + mSensor.getFifoMaxEventCount());
        LOG.debug("reserved fifo event count" + mSensor.getFifoReservedEventCount());
        LOG.debug( "starting accelerometer startid" + startId);


//        //testing alarm
//        AlarmManager alarmMgr;
//        PendingIntent alarmIntent;
//
//        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, KeepAwakeReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//

        Intent intent = new Intent(getApplicationContext(), KeepAwakeReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,5000,
//                        2* 60 * 1000, alarmIntent);
                        3 * 60 * 1000, alarmIntent);

//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,new DateTime(UTC).getMillis(), 60*1000, alarmIntent);
    }

    public interface keys{

        String highAccuracyMode = "highAccuracy";
        String powerBalanceMode = "powerBalance";

        double MIN_ACCEL = 2.5;


    }

}
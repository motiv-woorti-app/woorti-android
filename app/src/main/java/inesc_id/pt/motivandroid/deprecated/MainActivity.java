package inesc_id.pt.motivandroid.deprecated;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;

import org.dmg.pmml.FieldName;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import inesc_id.pt.motivandroid.ActivityRecognitionService;
import inesc_id.pt.motivandroid.BuildConfig;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.onboarding.activities.LoginActivity;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.showOngoingTrip.ShowOngoingTrip;
import inesc_id.pt.motivandroid.surveyNotification.SurveyManager;
import inesc_id.pt.motivandroid.tripStateMachine.Classifier;
import inesc_id.pt.motivandroid.tripStateMachine.RawDataDetection;
import inesc_id.pt.motivandroid.tripStateMachine.RawDataPreProcessing;
import inesc_id.pt.motivandroid.tripStateMachine.TripStateMachine;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.MLAlgorithmInput;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.MLInputMetadata;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.LogsUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static org.joda.time.DateTimeZone.UTC;

@Deprecated
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private final String LOG_TAG = LogsUtil.INIT_LOG_TAG + "MainActivity";

    private final int FINE_LOCATION_AND_READ_EXTERNAL_STORAGE = 100;

    final int REQUEST_SOURCE_LOCATION_REQUEST_CODE = 1234;

    public GoogleApiClient mApiClient;

    ListView tripListView;

    ActivityRecognitionService activityRecognitionService;

    Intent myService;

    boolean mBound = false;

    boolean serviceState;
    Button startStopDetectionButton;

    //testing

    Button viewTripsButton;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    Button signInOutButton;
    Boolean signInStatus = false;

    Button finishAndLogOutButton;

    Button dumpTripsToServerButton;

    Button deleteStateButton;

    Button sendLogButton;

    Button requestWeatherButton;

    Button deleteSentTripsButton;
    Button viewTriggeredSurveys;

    Button newInterfaceButton;

    Button showOngoingTripButton;

    FirebaseTokenManager firebaseTokenManager;

    PersistentTripStorage persistentTripStorage;

    TextView buildVersionTextView;

    SurveyManager surveyManager;

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 100;

    //ml

    Classifier classifier;

    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class.getSimpleName());

//    private static void updateResources(Context context, String language) {
//        Configuration config = new Configuration();
//        context.getResources().getConfiguration().locale = locale;
//
//        res.updateConfiguration(config, res.getDisplayMetrics());
//    }

    private class CheckStateMachineAndUpdateButton extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {


            TripStateMachine.getInstance(getApplicationContext(), false, true);

            publishProgress();

            return null;
        }
//
        protected void onProgressUpdate(Void... progress) {

            startStopDetectionButton.setVisibility(View.VISIBLE);

            if(TripStateMachine.getInstance(getApplicationContext(), false, true).currentState == TripStateMachine.state.still){

                startStopDetectionButton.setText("Start trip");
                startStopDetectionButton.setTextColor(Color.GREEN);

            }else{

                startStopDetectionButton.setText("Stop trip");
                startStopDetectionButton.setTextColor(Color.RED);

            }
        }


//        protected Void onPostExecute(Void... urls) {
//
//
//
//            return null;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //testing languages
//        LOG.error(Arrays.toString(BuildConfig.TRANSLATION_ARRAY));

        LOG.debug( "Current time init on resume" + new DateTime(UTC).getMillis());

        LOG.debug("onResume");

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mFullTripReceiver, new IntentFilter("FullTripFinished"));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mFullTripStartedReceiver, new IntentFilter("FullTripStarted"));

        LOG.debug( "Current time init on resume" + new DateTime(UTC).getMillis());


//        myService = new Intent(this, ActivityRecognitionService.class);

        if (!mBound && ActivityRecognitionService.serviceRunning){

            LOG.debug(  "myservice ="+ myService.toString());
            LOG.debug(  "mconn="+ mConnection.toString());


            getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }

        LOG.debug(  "onResume - serviceRunning="+ActivityRecognitionService.serviceRunning);

        firebaseTokenManager = FirebaseTokenManager.getInstance(getApplicationContext());
        firebaseTokenManager.getAndSetFirebaseToken();

//        if(ActivityRecognitionService.serviceRunning){
//            serviceState = true;
//            startStopDetectionButton.setText(keys.stopDetection);
//            startStopDetectionButton.setTextColor(Color.RED);
//            startStopDetectionButton.setClickable(false);
//        }else{
//            serviceState = false;
//            startStopDetectionButton.setText(keys.startDetection);
//            startStopDetectionButton.setTextColor(Color.GREEN);
//        }

        String token = FirebaseInstanceId.getInstance().getToken();
        LOG.debug( "FCM token" + token);

        FirebaseTokenManager.getInstance(getApplicationContext()).checkAndSendFCMToken();

        surveyManager = SurveyManager.getInstance(getApplicationContext());

//        try {
//            FirebaseInstanceId.getInstance().deleteInstanceId();
//
//            FirebaseInstanceId.getInstance().getToken();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        LOG.debug( "Current time" + new DateTime(UTC).getMillis());

        CheckStateMachineAndUpdateButton checkStateMachineAndUpdateButton = new CheckStateMachineAndUpdateButton();
        checkStateMachineAndUpdateButton.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    OnCompleteListener<GetTokenResult> onCompleteGetIdToken = new OnCompleteListener<GetTokenResult>() {
        public void onComplete(@NonNull Task<GetTokenResult> task) {
            if (task.isSuccessful()) {
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseToken(task.getResult().getToken());
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseTokenTimestamp(task.getResult().getIssuedAtTimestamp());
                Log.d("LoginActivity", "OnComplete Token " + task.getResult().getToken() + " from "+ DateHelper.getDateFromTSString(task.getResult().getIssuedAtTimestamp()));

                FirebaseTokenManager.getInstance(getApplicationContext()).checkAndSendFCMToken();



                resendOnBoardingToServer();

            } else {
                Log.e("LoginActivity", task.getException().toString());
                Toast.makeText(getApplicationContext(), "You wont be able to use any features that require internet connection." +
                        "To do so please check your internet connection and restart the app", Toast.LENGTH_LONG).show();
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseToken(null);

            }
        }
    };

    public void resendOnBoardingToServer(){

        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getApplicationContext(), "notExistent");

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {
            Log.d("LoginActivity", "Existing onboarding settings are from the current user trying to log in");

            //check if onboarding settings have successfully been sent to the server, if not resend them
            if (!userSettingStateWrapper.isSentToServer()) {
                Log.d("LoginActivity", "Onboarding data exists but not yet sent to server...resending!");
//                MotivAPIClientManager.getInstance(getApplicationContext()).makeUpdateOnboardingRequest(userSettingStateWrapper);
            }
        }

    }


//    public void configureLogging(){
//
//            LoggerConfiguration.configuration()
//                    .removeRootLogcatHandler()
//                    .addHandlerToRootLogger(new CrashlyticsLoggerHandler());
//
//
////        LoggerConfiguration.configuration().setLogLevel(this, LogLevel.DEBUG);
//
//            FileLogHandlerConfiguration fileHandler = LoggerConfiguration.fileLogHandler(this);
//
//            //4mb max limit log file size
//            fileHandler.setLogFileSizeLimitInBytes(1024 * 4096);
//            //2 rotating files
//            fileHandler.setRotateFilesCountLimit(3);
//
//            File root = Environment.getExternalStorageDirectory();
//
//            File myDir = new File(root + "/motivAndroidLogs");
//            if (!myDir.exists()) {
//                myDir.mkdirs();
//            }
//
//            fileHandler.setFullFilePathPattern(myDir.getAbsolutePath() + "/motivlog%g.log");
//            LoggerConfiguration.configuration().addHandlerToRootLogger(fileHandler);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        configureLogging();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        try{

            LOG.debug("Setting crashlytics user email as: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
            Crashlytics.setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        }catch (NullPointerException e){

            LOG.debug("FirebaseAuth.getInstance().getCurrentUser().getEmail() equals null. Not setting user email for crashlytics");

        }




        //mApiClient.connect();

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());

        viewTripsButton = findViewById(R.id.viewTripsButton);
        viewTripsButton.setOnClickListener(this);

        showOngoingTripButton = findViewById(R.id.showOngoingTripButton);
        showOngoingTripButton.setOnClickListener(this);

        LOG.debug( "onCreate");

        //LocalBroadcastManager.getInstance(this).registerReceiver(
        //        mFullTripReceiver, new IntentFilter("FullTripFinished"));

        startStopDetectionButton = findViewById(R.id.startStopDetectionButton);
        startStopDetectionButton.setOnClickListener(this);
        startStopDetectionButton.setVisibility(View.INVISIBLE);


        Button sendReq = (Button) findViewById(R.id.invokeRouteRankButton);
        sendReq.setOnClickListener(this);

        finishAndLogOutButton = findViewById(R.id.finishAndLogOutButton);
        finishAndLogOutButton.setOnClickListener(this);

        dumpTripsToServerButton = findViewById(R.id.dumpTripsToServerButton);
        dumpTripsToServerButton.setOnClickListener(this);

        persistentTripStorage = new PersistentTripStorage(getApplicationContext());

        buildVersionTextView = findViewById(R.id.buildVersionAppTextView);

        buildVersionTextView.setText("Build Version: "+ BuildConfig.VERSION_NAME);

        requestWeatherButton = findViewById(R.id.requestWeatherButton);
        requestWeatherButton.setOnClickListener(this);

        deleteSentTripsButton = findViewById(R.id.deleteSentTripsButton);
        deleteSentTripsButton.setOnClickListener(this);

        viewTriggeredSurveys = findViewById(R.id.viewTriggeredSurveys);
        viewTriggeredSurveys.setOnClickListener(this);

        newInterfaceButton = findViewById(R.id.newInterfaceButton);
        newInterfaceButton.setOnClickListener(this);

//        if (auth.getCurrentUser() != null) {
//            signInOutButton.setText("Sign out");
//        } else {
//            signInOutButton.setText("Sign In");
//        }

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(onCompleteGetIdToken);

        myService = new Intent(getApplicationContext(), ActivityRecognitionService.class);


        LOG.debug( "Service running? " + ActivityRecognitionService.serviceRunning);

        if(!ActivityRecognitionService.serviceRunning){


            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


            if (!wifi.isWifiEnabled()){
                LOG.debug( "WiFi disabled. Enabling it!");
                wifi.setWifiEnabled(true);
            }

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            //AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);

//            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //todo uncomment
//                showEnableLocationAlertDialog();
//            }                                                                        //todo uncomment

            startServiceWithPermissions();

            serviceState = true;
//            startStopDetectionButton.setText(keys.stopDetection);
//            startStopDetectionButton.setTextColor(Color.RED);

//            startStopDetectionButton.setEnabled(false);
        }

        //startServiceWithPermissions();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(myService);
            getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
        } else {
            getApplicationContext().startService(myService);
            getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
        }*/

        //createAndLogFullTripJSON();

        //createAndLogFullTripJSON();

//        try {
//
//            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
//            List <Address> addresses = geo.getFromLocation(38.7167, -9.1333, 1);
//            if (addresses.isEmpty()) {
//                Log.e("geocoder","waiting for");
//            } else {
//                if (addresses.size() > 0) {
//                    Log.e("geocoder",addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // getFromLocation() may sometimes fail
//        }

//        Button loadRandomForestButton = findViewById(R.id.loadRandomForest);
//        Button evaluationButton = findViewById(R.id.evaluationButton);


//        View.OnClickListener loadRandomForestButtonListener = new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Classifier.initClassifier(getApplicationContext(), "randomForest.pmml.ser");
//                classifier = Classifier.getInstance();
//
//                showEvaluatorDialog(classifier.getActiveFields(), classifier.getTargetFields(), classifier.getOutputFields());
//            }
//        };

//        // init TEST manual
        View.OnClickListener evaluateRandomForestListener = new View.OnClickListener(){
            @Override
            public void onClick(View view){
//                evaluate(); //todo uncomment

                Classifier.initClassifier(getApplicationContext(), "randomForest.pmml.ser");
                classifier = Classifier.getInstance();

                showEvaluatorDialog(classifier.getActiveFields(), classifier.getTargetFields(), classifier.getOutputFields());
                testData();
            }
        };
//        requestWeatherButton.setOnClickListener(evaluateRandomForestListener);     // uses old view trips button!!!
//        // end TEST manual

//
//        loadRandomForestButton.setOnClickListener(loadRandomForestButtonListener);
//        evaluationButton.setOnClickListener(evaluateRandomForestListener);

    }

    private void testData() {
        RawDataDetection rawDataDetection;
        Resources resources = getResources();
        InputStream iS;
        String m;

        try {

            RawDataPreProcessing rawDataPreProcessing = RawDataPreProcessing.getInstance(getApplicationContext());

            rawDataDetection = RawDataDetection.getInstance(getApplicationContext());
            rawDataDetection.TESTcleanUp();

            iS = resources.getAssets().open("rawData_32_150.txt");      // "rawData_33_128.txt"  "rawData_32_122.txt"  "rawData_30_53.txt"  "rawData_32_86.txt"
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));

            String line = reader.readLine();
            int osVer = Integer.parseInt(line);
            MLAlgorithmInput.setCurOs(osVer);

            while ((line = reader.readLine()) != null) {
                String[] args = line.split(",");
                if (Double.valueOf(args[0]) == 0.0) {
                    m = "ts= " + args[1] + "  lat= " + args[5] + "  lon= " + args[6] + "  acc=" + args[7];
                    //Log.d(">--GPS-->", m);

                    LocationDataContainer locationDataContainer = new LocationDataContainer(Long.valueOf(args[1]), Float.valueOf(args[7]), Double.valueOf(args[5]), Double.valueOf(args[6]), 0, 0);
                    rawDataPreProcessing.insertLocation(locationDataContainer);
                } else {
                    m = "ts= " + args[1] + "  x= " + args[2] + "  y= " + args[3] + "  z= " + args[4];
                    AccelerationData accelerationData = new AccelerationData(Double.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Long.valueOf(args[1]));
//                    Log.d(">--ACL-->", m);
                    rawDataPreProcessing.insertAcceleration(accelerationData);
                }

            }

//            RawDataDetection rawDataDetection = RawDataDetection.getInstance(getApplicationContext());

            ArrayList<MLInputMetadata> output = rawDataDetection.getOutputsMetadata();
            Log.d("_>_", "Pre data:");
            int test = 0;
            for (MLInputMetadata o : output){
//                if (test >=20){
//                    break;
//                }
//                test += 1;
                MLAlgorithmInput mlAlgorithmInput = o.getMlAlgorithmInput();

//                mlAlgorithmInput.setOSVersion(osVer);

                Log.d("_>_", "" + mlAlgorithmInput.getProcessedPoints().getEstimatedSpeed() +
                        " ; " + mlAlgorithmInput.getAccelsBelowFilter() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_03_06() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_06_1() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_1_3() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_3_6() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getAbove_6() +
                        " ; " + mlAlgorithmInput.getAvgFilteredAccels() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getAvgAccel() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getMaxAccel() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getMinAccel() +
                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getStdDevAccel() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getAvgSpeed() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getMaxSpeed() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getMinSpeed() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getStdDevSpeed() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getAvgAcc() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getMaxAcc() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getMinAcc() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getStdDevAcc() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getGpsTimeMean() +
                        " ; " + mlAlgorithmInput.getProcessedPoints().getDistance() + ";");
            }

//            Log.d("_>_", "Probas (input test):");
//
//            int outputNum = 0;
//            for (MLInputMetadata o : output){
//                List<KeyValueWrapper> probasOrdered = o.getProbasOrdered();
//
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(outputNum);
//                stringBuilder.append(" [");
//                for (KeyValueWrapper currProba : probasOrdered){
//                    stringBuilder.append("(").append(currProba.getKey()).append(", ").append(currProba.getValue()).append("), ");
//                }
//                stringBuilder.append("]");
//                Log.d("_>_", stringBuilder.toString());
//
//                outputNum ++;
//            }

            rawDataDetection.tripEvaluation();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int ts = 400000000;

//    private void createAndLogFullTripJSON(){
//
//        ts += 5000;
//
//        ArrayList<LocationDataContainer> lcdList = new ArrayList<>();
//
//        double lat = 0;
//        double lon = 0;
//
//        for(int i = 0; i < 1000; i++){
//
//            lat += 0.00001;
//            lon -= 0.00001;
//            lcdList.add(new LocationDataContainer(i*10, 0,lat,lon,0,0));
//
//
//        }
//
//        ArrayList<AccelerationData> accelerationDataArrayList = new ArrayList<>();
//
//        for (int j = 0; j< 10000 ; j++){
//            accelerationDataArrayList.add(new AccelerationData(0,0,0,j));
//        }
//
//        ArrayList<ActivityDataContainer> acdList = new ArrayList<>();
//
////        DetectedActivity detectedActivity = new DetectedActivity(0,90);
////        DetectedActivity detectedActivity2 = new DetectedActivity(1,10);
////
////        List<DetectedActivity> detectedActivityList = new ArrayList<>();
////        detectedActivityList.add(detectedActivity);
////        detectedActivityList.add(detectedActivity2);
////
////        ActivityRecognitionResult activityRecognitionResult = new ActivityRecognitionResult(detectedActivityList,2,2);
////
////        ActivityDataContainer activityDataContainer = new ActivityDataContainer(6,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer1 = new ActivityDataContainer(12,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer2 = new ActivityDataContainer(22,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer3 = new ActivityDataContainer(31,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer4 = new ActivityDataContainer(46,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer5 = new ActivityDataContainer(54,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer6 = new ActivityDataContainer(62,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer7 = new ActivityDataContainer(71,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer8 = new ActivityDataContainer(83,activityRecognitionResult);
////        ActivityDataContainer activityDataContainer9 = new ActivityDataContainer(94,activityRecognitionResult);
////
////
////        acdList.add(activityDataContainer);
////        acdList.add(activityDataContainer1);
////        acdList.add(activityDataContainer2);
////        acdList.add(activityDataContainer3);
////        acdList.add(activityDataContainer4);
////        acdList.add(activityDataContainer5);
////        acdList.add(activityDataContainer6);
////        acdList.add(activityDataContainer7);
////        acdList.add(activityDataContainer8);
////        acdList.add(activityDataContainer9);
//
//
//
//        Trip trip = new Trip(lcdList,acdList,0,ts,1000000000,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
//        Trip trip2 = new Trip(lcdList,acdList,1000000000,ts,1020000000,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
//
//
//        //        WaitingEvent we = new WaitingEvent(lcdList,71,89,0,0);
////        Trip trip2 = new Trip(lcdList,acdList,1,11,20,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip3 = new Trip(lcdList,acdList,1,21,30,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip4 = new Trip(lcdList,acdList,1,31,40,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip5 = new Trip(lcdList,acdList,1,41,50,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip6 = new Trip(lcdList,acdList,1,51,60,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip7 = new Trip(lcdList,acdList,1,61,70,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip8 = new Trip(lcdList,acdList,1,71,80,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip9 = new Trip(lcdList,acdList,1,81,90,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
////        Trip trip10 = new Trip(lcdList,acdList,1,91,100,0,0,0,0,0, accelerationDataArrayList,0,0,0,0,0, 5);
//
//
//        ArrayList<FullTripPart> fullTripPartArrayList = new ArrayList<>();
//
//        fullTripPartArrayList.add(trip);
//        fullTripPartArrayList.add(trip2);
//
//
//        FullTrip fullTrip = new FullTrip(fullTripPartArrayList,ts,1000000000,0,0,
//                0,0, MiscUtils.getDeviceName(),"Android", MiscUtils.getOSVersion(),FirebaseAuth.getInstance().getCurrentUser().getUid() ,false);
//
////        JSONUtils jsonUtils = JSONUtils.getInstance();
////        Gson gson = jsonUtils.getGson();
//        //Log.i("jsonFullTrip",gson.toJson(fullTrip));
//
//        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(this);
//        persistentTripStorage.insertFullTripObject(fullTrip);
//
//    }

    private void showEvaluatorDialog(List<FieldName> activeFields, List<FieldName> targetFields, List<FieldName> outputFields){
        StringBuilder sb = new StringBuilder();

        sb.append("Active fields: ").append(activeFields);
        sb.append("\n");
        sb.append("Target fields: ").append(targetFields);
        sb.append("\n");
        sb.append("Output fields: ").append(outputFields);

        TextView textView = new TextView(this);
        textView.setText(sb.toString());
        Log.d("---Model: ", sb.toString());

        Dialog dialog = new Dialog(this);
        dialog.setContentView(textView);
        dialog.show();
    }

    private void showToast(String errorMessageRes) {

        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        LOG.debug(  "onConnected()");

        //startActionButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "ActivityRecognitionResult" is broadcasted.
    private BroadcastReceiver mFullTripReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            CheckStateMachineAndUpdateButton checkStateMachineAndUpdateButton = new CheckStateMachineAndUpdateButton();
            checkStateMachineAndUpdateButton.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    };

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "ActivityRecognitionResult" is broadcasted.
    private BroadcastReceiver mFullTripStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            CheckStateMachineAndUpdateButton checkStateMachineAndUpdateButton = new CheckStateMachineAndUpdateButton();
            checkStateMachineAndUpdateButton.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ActivityRecognitionService.ActivityRecognitionBinder binder = (ActivityRecognitionService.ActivityRecognitionBinder) service;
            activityRecognitionService = binder.getService();
            mBound = true;

            serviceState = true;
//            startStopDetectionButton.setText(keys.stopDetection);
//            startStopDetectionButton.setTextColor(Color.RED);

            LOG.debug(  "onserviceConnected()");

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        LOG.debug( "onStop");
        // Unbind from the service

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFullTripReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFullTripStartedReceiver);

        if (mBound) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*if (mBound && ActivityRecognitionService.serviceRunning) {
            getApplicationContext().unbindService(mConnection);
            getApplicationContext().stopService(new Intent(this, ActivityRecognitionService.class));
            mBound = false;


            if(android.os.Build.VERSION.SDK_INT >= 21)
            {
                finishAndRemoveTask();
            }
            else
            {
                finish();
            }
        }*/

        LOG.debug( "onDestroy");
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        Log.e("activity","onStart");

        // Bind to LocalService
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }*/

    boolean pt = false;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.invokeRouteRankButton:
                //sendSessionRequest();

//                Intent startRRActivityIntent = new Intent(getApplicationContext(), SearchRouteRankActivity.class);
//                startActivity(startRRActivityIntent);

                break;

//            case R.id.viewTripsButton:
//                Intent startViewTripsActivityIntent = new Intent(getApplicationContext(), ViewTripsActivity.class);
//                startActivity(startViewTripsActivityIntent);
//
//                //Crashlytics.getInstance().crash(); // Force a crash
//
//                break;
            case R.id.startStopDetectionButton:

//                activityRecognitionService.forceFinishTrip();

//                if (!ActivityRecognitionService.serviceRunning){
//                    startServiceWithPermissions();
//                }

                if(TripStateMachine.getInstance(getApplicationContext(), false, true).currentState == TripStateMachine.state.still){

                    TripStateMachine.getInstance(getApplicationContext(), false, true).forceStartTrip();

                    startStopDetectionButton.setText("Stop trip");
                    startStopDetectionButton.setTextColor(Color.RED);

                }else{

                    TripStateMachine.getInstance(getApplicationContext(), false, true).forceFinishTrip(false);
                    startStopDetectionButton.setText("Start trip");
                    startStopDetectionButton.setTextColor(Color.GREEN);

                }



//                LOG.debug( "init start" + new DateTime(UTC).getMillis());
//
//                if(!ActivityRecognitionService.serviceRunning){
//
//                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//                    if (!wifi.isWifiEnabled()){
//                        Crashlytics.log(Log.DEBUG, LOG_TAG,"WiFi disabled. Enabling it!");
//                        wifi.setWifiEnabled(true);
//                    }
//
//                    LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//                    //AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
//                    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//
//                        showEnableLocationAlertDialog();
//
//                    }else{
//
////                        createAndLogFullTripJSON();
//
//
//                        startServiceWithPermissions();
//                        serviceState = true;
//                        startStopDetectionButton.setText(keys.stopDetection);
//                        startStopDetectionButton.setTextColor(Color.RED);
//                    }
//
//                    LOG.debug( "init start" + new DateTime(UTC).getMillis());
//
//                }else{
//
//                    //TripStateMachine.getInstance(getApplicationContext(),false).forceFinishTrip(false);
//                    //getApplicationContext().stopService(new Intent(this, ActivityRecognitionService.class));
//
//                    /*if(mBound && ActivityRecognitionService.serviceRunning){
//                        getApplicationContext().unbindService(mConnection);
//                        mBound = false;
//                        //getApplicationContext().stopService(new Intent(this, ActivityRecognitionService.class));
//                        activityRecognitionService.stopRecognition();
//                        serviceState=false;
//                        startStopDetectionButton.setText(keys.startDetection);
//                    }*/
//
//                    stopServiceAndForceFinishTrip();
//
//
//
//
//                    //Log.e("logactivity", LogsUtil.readLogs().toString());
//
//                    /*if (ActivityRecognitionService.serviceRunning) {
//
//                        if(android.os.Build.VERSION.SDK_INT >= 21)
//                        {
//                            finishAndRemoveTask();
//                        }
//                        else
//                        {
//                            finish();
//                        }
//                    }*/
//
//                }
                break;

            case R.id.finishAndLogOutButton:

                if(serviceState) {
                    stopServiceAndForceFinishTrip();
                }

                FirebaseAuth.getInstance().signOut();
//                LoginManager.getInstance().logOut();


                startActivity(new Intent(getApplicationContext(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();

                break;
            case R.id.dumpTripsToServerButton:

//                dumpSentTripsToServer();

                break;
            case R.id.deleteSentTripsButton:

                deleteSentTrips();

                break;

            case R.id.showOngoingTripButton:

                if(TripStateMachine.getInstance(getApplicationContext(), false, true).getCurrentOngoingTrip() == null){

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.No_Ongoing_Trip_To_Show), Toast.LENGTH_SHORT).show();

                }else{

                    startActivity(new Intent(getApplicationContext(), ShowOngoingTrip.class));

                }

                break;
            case R.id.requestWeatherButton:
                startActivity(new Intent(getApplicationContext(), WeatherActivity.class));

//                startActivity(new Intent(getApplicationContext(), MobilityCoachActivity.class));


//                Log.e("testing locale", getResources().getConfiguration().locale.getLanguage());
//
//                if (getResources().getConfiguration().locale.getLanguage().equals(new Locale("en").getLanguage())){
//                    Log.e("testing locale", "locale equals en - changing to pt");
//
//                    Locale locale = new Locale("pt");
//                    Locale.setDefault(locale);
//                    Resources res = getApplicationContext().getResources();
//                    Configuration config = new Configuration(res.getConfiguration());
//                    config.locale = locale;
//                    res.updateConfiguration(config, res.getDisplayMetrics());
//
//                    recreate();
//
//
//                }else if(getResources().getConfiguration().locale.getLanguage().equals(new Locale("pt").getLanguage())){
//                    Log.e("testing locale", "locale equals pt");
//
//                    Locale locale = new Locale("en");
//                    Locale.setDefault(locale);
//                    Resources res = getApplicationContext().getResources();
//                    Configuration config = new Configuration(res.getConfiguration());
//                    config.locale = locale;
//                    res.updateConfiguration(config, res.getDisplayMetrics());
//                    pt = false;
//
//                    recreate();
//
//
//                }else{
//                    Log.e("testing locale", "locale no equals");
//                }



                break;
            case R.id.newInterfaceButton:
                startActivity(new Intent(getApplicationContext(), HomeDrawerActivity.class));
                break;
            case R.id.viewTriggeredSurveys:
                startActivity(new Intent(getApplicationContext(), ViewTriggeredSurveysList.class));
                break;

    }
    }

    private void deleteSentTrips(){

        ArrayList<FullTrip> allFullTrips = persistentTripStorage.getAllFullTripsObject();

        for(FullTrip ft : allFullTrips){
            if(ft.isSentToServer()) {
                persistentTripStorage.deleteFullTripByDate(ft.getDateId());
                LOG.debug( "To be deleted " + ft.getInitTimestamp() + " " + DateHelper.getDateFromTSString(ft.getInitTimestamp()));
            }
            }
        }


    public void sendLogEmail(){
        //startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:to@gmail.com")));


        String[] logFiles = {"motivlog0.log", "motivlog1.log", "motivlog2.log"};

        final Intent emailIntent = new Intent( Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { "dummy@dummy.com" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Log from " + FirebaseAuth.getInstance().getCurrentUser().getEmail());

        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "The Bug i want to report is: (Fill in what was supposed to happen)"+ "\n"+"\n"+
                        "If the bug is related to trip detected please fill in the following"+ "\n"+
                        "Date and time of when the trip was supposed to start:"+ "\n" +
                        "Date and time of when the trip was supposed to end:"+ "\n"+ "\n"+
                        "Please also fill in any aditional information that you think is important:"
        );

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        int existingFiles = 0;

        ArrayList<Uri> URIArrayList=new ArrayList<Uri>();

        for(String logFileName : logFiles){

            File file = new File(Environment.getExternalStorageDirectory().toString() +"/motivAndroidLogs" + "/"+logFileName);
            if(file.exists()){
                existingFiles ++;
                System.out.println("file is already there");
//                Uri uri = Uri.fromFile(file);

                //per https://robusttechhouse.com/how-to-share-files-securely-with-fileprovider/
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "inesc_id.pt.motivandroid.logfileprovider", file);

                URIArrayList.add(uri);
            }else{
                System.out.println("Not find file ");
                break;
            }
        }

        if (existingFiles == 0) {
            Toast.makeText(getApplicationContext(), "Attachment Error", Toast.LENGTH_SHORT).show();
            return;
        }else{
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, URIArrayList);

            Log.d("Home Drawer Activity", "Attachment list size: " + URIArrayList.size());

            startActivityForResult(Intent.createChooser(
                    emailIntent, "Send mail..."), 1);
        }


    }

    private void showEnableLocationAlertDialog(){

        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Location services not enabled!");  // GPS not found
        builder.setMessage("In order for the app to track your trips, you'll need to enable the location services"); // Want to enable?
        builder.setPositiveButton("Yes, take me there!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();

    }

    private void stopServiceAndForceFinishTrip(){

        TripStateMachine.getInstance(getApplicationContext(),false, false).forceFinishTrip(false);
        //getApplicationContext().stopService(new Intent(this, ActivityRecognitionService.class));

        LOG.debug(  "mbound " + mBound);
        LOG.debug(  "servicerunning " + ActivityRecognitionService.serviceRunning);

        if(mBound && ActivityRecognitionService.serviceRunning) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
            //getApplicationContext().stopService(new Intent(this, ActivityRecognitionService.class));
            activityRecognitionService.stopRecognition();
            serviceState = false;
//            startStopDetectionButton.setText(keys.startDetection);
//            startStopDetectionButton.setTextColor(Color.GREEN);
//            startStopDetectionButton.setClickable(true);

        }




    }

    public int getLocationMode(Context context)
    {
        try {

            Log.e("LocationMode", "location mode " + Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE));

            return Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
//            0 = LOCATION_MODE_OFF
//            1 = LOCATION_MODE_SENSORS_ONLY
//            2 = LOCATION_MODE_BATTERY_SAVING
//            3 = LOCATION_MODE_HIGH_ACCURACY
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }

    }

    @AfterPermissionGranted(FINE_LOCATION_AND_READ_EXTERNAL_STORAGE)
    private void startServiceWithPermissions(){
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {

            LOG.debug("Location mode " + getLocationMode(this));

            if(getLocationMode(this) == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY || (BuildConfig.VERSION_CODE >= 28)) {

                ContextCompat.startForegroundService(getApplicationContext(), myService);
                getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    getApplicationContext().startForegroundService(myService);
//                    getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
//                } else {
//                    getApplicationContext().startService(myService);
//                    getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
//                }
            }else{

                //METER UM DIALOG AQUI PARA QUE NAO VÁ DIRECTO PARA AS SETTINGS

                Toast.makeText(getApplicationContext(), "You must enable location - high accuracy", Toast.LENGTH_LONG).show();

            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                startActivityForResult(new Intent(action), REQUEST_SOURCE_LOCATION_REQUEST_CODE);

            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "In order for the app to work properly you must allow it to access fine location and read external storage",
                    FINE_LOCATION_AND_READ_EXTERNAL_STORAGE, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LOG.debug("rc " + requestCode + "result " + resultCode);

            switch (requestCode) {
                case REQUEST_SOURCE_LOCATION_REQUEST_CODE:
                        LOG.debug("case rslrc");

                        if(getLocationMode(this) == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {

                            ContextCompat.startForegroundService(getApplicationContext(), myService);
                            getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);

//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                getApplicationContext().startForegroundService(myService);
//                                getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
//                            } else {
//                                getApplicationContext().startService(myService);
//                                getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
//                            }

                        }else{
                            Toast.makeText(getApplicationContext(), "You must enable location - high accuracy in order for the app to track your trips. Please press start detection and enable!", Toast.LENGTH_LONG).show();
                        }

                    break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public interface keys{

        String startDetection = "Start detection";
        String stopDetection = "Stop detection";


    }

}
package inesc_id.pt.motivandroid.home.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ListIterator;
import java.util.Locale;

import inesc_id.pt.motivandroid.ActivityRecognitionService;
import inesc_id.pt.motivandroid.BuildConfig;
import inesc_id.pt.motivandroid.MyContextWrapper;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.deprecated.ViewTriggeredSurveysList;
import inesc_id.pt.motivandroid.deprecated.WeatherActivity;
import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.userSettingsData.Language;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.surveys.Survey;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.surveys.answers.Answer;
import inesc_id.pt.motivandroid.home.fragments.DashboardFragment;
import inesc_id.pt.motivandroid.home.fragments.HomeFragment;
import inesc_id.pt.motivandroid.home.fragments.MobilityCoachFragment;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.home.fragments.ProfileAndSettingsFragment;
import inesc_id.pt.motivandroid.deprecated.TestDashboardFragment;
import inesc_id.pt.motivandroid.home.surveys.SurveyTestActivity;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.onboarding.activities.LoginActivity;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
//import inesc_id.pt.motivandroid.planTrip.fragments.RoutesLegsFragment;
//import inesc_id.pt.motivandroid.planTrip.fragments.ViewMapFragment;
import inesc_id.pt.motivandroid.surveyNotification.SurveyManager;
import inesc_id.pt.motivandroid.tripStateMachine.Classifier;
import inesc_id.pt.motivandroid.tripStateMachine.RawDataDetection;
import inesc_id.pt.motivandroid.tripStateMachine.RawDataPreProcessing;
import inesc_id.pt.motivandroid.tripStateMachine.TripStateMachine;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.MLAlgorithmInput;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.MLInputMetadata;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.NotificationHelper;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static org.joda.time.DateTimeZone.UTC;

/**
 *
 * HomeDrawerActivity
 *
 *  This is the "app main activity". After completing the onboarding and being logged in, the user
 *  is taken to this activity. Basically, represents the app main menu. This activity consists of:
 *      - Main fragment - presents MyTripsFragment by default, is replaced by the fragment chosen by the
 *      user by pressing one of the options presented in the navigation drawer. These fragment are:
 *          - HomeFragment
 *          - MyTripsFragment
 *          - DashboardFragment
 *          - MobilityCoachFragment
 *          - ProfileAndSettingsFragment
 *      - Navigation drawer - allows the user to access/switch the main fragment to the one he chooses
 *
 *  This activity may also be launched by user activity (e.g. pressing a notification). The notification
 *  might specify a different to replace the main fragment. This info is passed through a parameter in
 *  the intent NotificationHelper.keys.NOTIFICATION_CODE_PARAMETER.
 *
 *  The activity also launches/starts, if not yet started, the activity recognition service. The
 *  service is responsible for retrieving location and accelerometer data and pass it to the
 *  TripStateMachine (which decides if a trip has started or ended)
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

public class HomeDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                View.OnClickListener,
                        MyTripsFragment.OnFragmentInteractionListener,
                            HomeFragment.OnFragmentInteractionListener,
                                ProfileAndSettingsFragment.OnFragmentInteractionListener,
                                    MobilityCoachFragment.OnFragmentInteractionListener,
//                                        RoutesLegsFragment.OnFragmentInteractionListener,
//                                            ViewMapFragment.OnFragmentInteractionListener,
                                                DashboardFragment.OnFragmentInteractionListener,
                                                    TestDashboardFragment.OnFragmentInteractionListener, EasyPermissions.PermissionCallbacks{

    DrawerLayout drawer;

    private final int FINE_LOCATION_AND_READ_EXTERNAL_STORAGE = 100;

    final int REQUEST_SOURCE_LOCATION_REQUEST_CODE = 1234;

    public GoogleApiClient mApiClient;

    ActivityRecognitionService activityRecognitionService;

    Intent myService;

    boolean mBound = false;

    boolean serviceState;

    FirebaseTokenManager firebaseTokenManager;

    PersistentTripStorage persistentTripStorage;

    SurveyManager surveyManager;

    Classifier classifier;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase));
    }

    int REQUEST_CHECK_SETTINGS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        overlayProgressLayout = findViewById(R.id.progress_view);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        onNewIntent(getIntent());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setItemIconTintList(null);

        View header = navigationView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.buildVersionAppTextView);
        text.setText("Build Version: "+ BuildConfig.VERSION_NAME);

        ImageView logoImageView = header.findViewById(R.id.imageView14);

        Glide.with(this).load(R.drawable.logo_nav_drawer)
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                .fitCenter()
                .into(logoImageView);

        ImageView footerView = ((NavigationView) findViewById(R.id.footer_nav_view)).findViewById(R.id.footer_powered_motiv);
        Glide.with(this).load(R.drawable.powered_motiv)
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                .into(footerView);


        navigationView.setNavigationItemSelectedListener(this);

        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

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

        LOG.debug( "onCreate");

        int currentTimestampOnDevice = SurveyManager.getInstance(getApplicationContext()).getCurrentGlobalTimestampOnDevice();
        MotivAPIClientManager.getInstance(getApplicationContext()).makeGetSurveysRequest(currentTimestampOnDevice);

        persistentTripStorage = new PersistentTripStorage(getApplicationContext());

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(onCompleteGetIdToken);
        }

        myService = new Intent(getApplicationContext(), ActivityRecognitionService.class);

        LOG.debug( "Service running? " + ActivityRecognitionService.serviceRunning);

        if(!ActivityRecognitionService.serviceRunning){


            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            //enable wifi in order to increase location accuracy
            if (!wifi.isWifiEnabled()){
                LOG.debug( "WiFi disabled. Enabling it!");
                wifi.setWifiEnabled(true);
            }

            LocationRequest testLocationRequest = buildSingleHighAccuracyLocRequest();

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(testLocationRequest);

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(HomeDrawerActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });

            //start activity recognition service
            startServiceWithPermissions();

            serviceState = true;

        }

//        View.OnClickListener evaluateRandomForestListener = new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
////                evaluate(); //todo uncomment
//
//                Classifier.initClassifier(getApplicationContext(), "randomForest.pmml.ser");
//                classifier = Classifier.getInstance();
//
//                showEvaluatorDialog(classifier.getActiveFields(), classifier.getTargetFields(), classifier.getOutputFields());
//                testData();
//            }
//        };

    }

    private LocationRequest buildSingleHighAccuracyLocRequest(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LOG.debug("Building high accuracy location request to check if location is enabled");

        return mLocationRequest;

    }


    /**
     * check if the activity was launched from a notification and decide which menu should replace
     * the main fragment
     *
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(extras != null){
            if(extras.containsKey(NotificationHelper.keys.NOTIFICATION_CODE_PARAMETER))
            {
                int msg = extras.getInt(NotificationHelper.keys.NOTIFICATION_CODE_PARAMETER);

                switch (msg){
                    case keys.MY_TRIPS:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment, MyTripsFragment.newInstance("","")).commitAllowingStateLoss();
                        break;
                    case keys.MOBILITY_COACH:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment, MobilityCoachFragment.newInstance("","")).commitAllowingStateLoss();
                        break;
                    case keys.DASHBOARD:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment, DashboardFragment.newInstance("","")).commitAllowingStateLoss();
                        break;
                    case keys.HOME:
                    default:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment.newInstance("","")).commitAllowingStateLoss();
                        break;
                }

            }else{
                fragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment.newInstance("","")).commitAllowingStateLoss();
            }
        }else{
            fragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment.newInstance("","")).commitAllowingStateLoss();
        }


    }

    private static final Logger LOG = LoggerFactory.getLogger(HomeDrawerActivity.class.getSimpleName());

    @Override
    protected void onResume() {
        super.onResume();

        LOG.debug( "Current time init on resume" + new DateTime(UTC).getMillis());

        LOG.debug("onResume");

        LOG.debug( "Current time init on resume" + new DateTime(UTC).getMillis());

        if (!mBound && ActivityRecognitionService.serviceRunning){

            LOG.debug(  "myservice ="+ myService.toString());
            LOG.debug(  "mconn="+ mConnection.toString());

            getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }

        LOG.debug(  "onResume - serviceRunning="+ActivityRecognitionService.serviceRunning);

        firebaseTokenManager = FirebaseTokenManager.getInstance(getApplicationContext());
        firebaseTokenManager.getAndSetFirebaseToken();

        String token = FirebaseInstanceId.getInstance().getToken();
        LOG.debug( "FCM token" + token);

        FirebaseTokenManager.getInstance(getApplicationContext()).checkAndSendFCMToken();

        surveyManager = SurveyManager.getInstance(getApplicationContext());

        LOG.debug( "Current time" + new DateTime(UTC).getMillis());

        mailClientOpened = false;

    }

    OnCompleteListener<GetTokenResult> onCompleteGetIdToken = new OnCompleteListener<GetTokenResult>() {
        public void onComplete(@NonNull Task<GetTokenResult> task) {
            if (task.isSuccessful()) {
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseToken(task.getResult().getToken());
                FirebaseTokenManager.getInstance(getApplicationContext()).setLastFirebaseTokenTimestamp(task.getResult().getIssuedAtTimestamp());
                Log.d("LoginActivity", "OnComplete Token " + task.getResult().getToken() + " from "+ DateHelper.getDateFromTSString(task.getResult().getIssuedAtTimestamp()));

                FirebaseTokenManager.getInstance(getApplicationContext()).checkAndSendFCMToken();
                resendOnBoardingToServer();

                MotivAPIClientManager.getInstance(getApplicationContext()).getMyRewardData();
                MotivAPIClientManager.getInstance(getApplicationContext()).putAndGetMyRewardStatus();

                MotivAPIClientManager.getInstance(getApplicationContext()).getStatsFromServerAndSet();

                MotivAPIClientManager motivAPIClientManager = MotivAPIClientManager.getInstance(getApplicationContext());
                motivAPIClientManager.sendConfirmedTripsToServer();
                motivAPIClientManager.sendTripSummaries();


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

            ArrayList<Campaign> userCampaigns = userSettingStateWrapper.getUserSettings().getCampaigns();

            if(userCampaigns != null && userCampaigns.size()>0) {
                // todo fast fix

                Log.e("HomeDrawerActivity", "More than 0 campaigns on userSettings " + userCampaigns.size());

                boolean fixed = false;

                ListIterator<Campaign> iter = userCampaigns.listIterator();
                while(iter.hasNext()){
                    if(iter.next().getCampaignID() == null){

                        Log.e("HomeDrawerActivity", "Campaign id  null ");

                        fixed = true;
                        iter.remove();
                    }
                }

                if(fixed) {
                    SharedPreferencesUtil.writeOnboardingUserData(getApplicationContext(), userSettingStateWrapper, true);
                }

            }

            //check if onboarding settings have successfully been sent to the server, if not resend them
            if (!userSettingStateWrapper.isSentToServer()) {
                Log.d("LoginActivity", "Onboarding data exists but not yet sent to server...resending!");
                MotivAPIClientManager.getInstance(getApplicationContext()).makeUpdateOnboardingRequest();
            }
        }

    }

//    private void testData() {
//        RawDataDetection rawDataDetection;
//        Resources resources = getResources();
//        InputStream iS;
//        String m;
//
//        try {
//            RawDataPreProcessing rawDataPreProcessing = RawDataPreProcessing.getInstance(getApplicationContext());
//
//            rawDataDetection = RawDataDetection.getInstance(getApplicationContext());
//            rawDataDetection.TESTcleanUp();
//
//            iS = resources.getAssets().open("rawData_32_150.txt");      // "rawData_33_128.txt"  "rawData_32_122.txt"  "rawData_30_53.txt"  "rawData_32_86.txt"
//            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));
//
//            String line = reader.readLine();
//            int osVer = Integer.parseInt(line);
//            MLAlgorithmInput.setCurOs(osVer);
//
//            while ((line = reader.readLine()) != null) {
//                String[] args = line.split(",");
//                if (Double.valueOf(args[0]) == 0.0) {
//                    m = "ts= " + args[1] + "  lat= " + args[5] + "  lon= " + args[6] + "  acc=" + args[7];
//                    //Log.d(">--GPS-->", m);
//
//                    LocationDataContainer locationDataContainer = new LocationDataContainer(Long.valueOf(args[1]), Float.valueOf(args[7]), Double.valueOf(args[5]), Double.valueOf(args[6]), 0, 0);
//                    rawDataPreProcessing.insertLocation(locationDataContainer);
//                } else {
//                    m = "ts= " + args[1] + "  x= " + args[2] + "  y= " + args[3] + "  z= " + args[4];
//                    AccelerationData accelerationData = new AccelerationData(Double.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]), Long.valueOf(args[1]));
////                    Log.d(">--ACL-->", m);
//                    rawDataPreProcessing.insertAcceleration(accelerationData);
//                }
//
//            }
//
////            RawDataDetection rawDataDetection = RawDataDetection.getInstance(getApplicationContext());
//
//            ArrayList<MLInputMetadata> output = rawDataDetection.getOutputsMetadata();
//            Log.d("_>_", "Pre data:");
//            int test = 0;
//            for (MLInputMetadata o : output){
////                if (test >=20){
////                    break;
////                }
////                test += 1;
//                MLAlgorithmInput mlAlgorithmInput = o.getMlAlgorithmInput();
//
////                mlAlgorithmInput.setOSVersion(osVer);
//
//                Log.d("_>_", "" + mlAlgorithmInput.getProcessedPoints().getEstimatedSpeed() +
//                        " ; " + mlAlgorithmInput.getAccelsBelowFilter() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_03_06() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_06_1() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_1_3() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getBetween_3_6() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getAbove_6() +
//                        " ; " + mlAlgorithmInput.getAvgFilteredAccels() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getAvgAccel() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getMaxAccel() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getMinAccel() +
//                        " ; " + mlAlgorithmInput.getProcessedAccelerations().getStdDevAccel() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getAvgSpeed() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getMaxSpeed() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getMinSpeed() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getStdDevSpeed() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getAvgAcc() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getMaxAcc() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getMinAcc() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getStdDevAcc() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getGpsTimeMean() +
//                        " ; " + mlAlgorithmInput.getProcessedPoints().getDistance() + ";");
//            }
//
////            Log.d("_>_", "Probas (input test):");
////
////            int outputNum = 0;
////            for (MLInputMetadata o : output){
////                List<KeyValueWrapper> probasOrdered = o.getProbasOrdered();
////
////                StringBuilder stringBuilder = new StringBuilder();
////                stringBuilder.append(outputNum);
////                stringBuilder.append(" [");
////                for (KeyValueWrapper currProba : probasOrdered){
////                    stringBuilder.append("(").append(currProba.getKey()).append(", ").append(currProba.getValue()).append("), ");
////                }
////                stringBuilder.append("]");
////                Log.d("_>_", stringBuilder.toString());
////
////                outputNum ++;
////            }
//
//            rawDataDetection.tripEvaluation();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    int ts = 400000000;

//    private void showEvaluatorDialog(List<FieldName> activeFields, List<FieldName> targetFields, List<FieldName> outputFields){
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("Active fields: ").append(activeFields);
//        sb.append("\n");
//        sb.append("Target fields: ").append(targetFields);
//        sb.append("\n");
//        sb.append("Output fields: ").append(outputFields);
//
//        TextView textView = new TextView(this);
//        textView.setText(sb.toString());
//        Log.d("---Model: ", sb.toString());
//
//        Dialog dialog = new Dialog(this);
//        dialog.setContentView(textView);
//        dialog.show();
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LOG.debug(  "onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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



        if (mBound) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }

        mailClientOpened = true;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reportingSurveyReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LOG.debug( "onDestroy");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.deleteSentTripsButton:
                deleteSentTrips();
                break;

            case R.id.requestWeatherButton:
                startActivity(new Intent(getApplicationContext(), WeatherActivity.class));
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


//    private void showEnableLocationAlertDialog(){
//
//        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
//        builder.setTitle("Location services not enabled!");  // GPS not found
//        builder.setMessage("In order for the app to track your trips, you'll need to enable the location services"); // Want to enable?
//        builder.setPositiveButton("Yes, take me there!", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            }
//        });
//        builder.setNegativeButton("No", null);
//        builder.create().show();
//
//    }

    private void stopServiceAndForceFinishTrip(){

        TripStateMachine.getInstance(getApplicationContext(),false, true).forceFinishTrip(false);
        //getApplicationContext().stopService(new Intent(this, ActivityRecognitionService.class));

        LOG.debug(  "mbound " + mBound);
        LOG.debug(  "servicerunning " + ActivityRecognitionService.serviceRunning);

        if(mBound && ActivityRecognitionService.serviceRunning) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
            //getApplicationContext().stopService(new Intent(this, ActivityRecognitionService.class));
            activityRecognitionService.stopRecognition();
            serviceState = false;

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

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @AfterPermissionGranted(FINE_LOCATION_AND_READ_EXTERNAL_STORAGE)
    private void startServiceWithPermissions(){
        if (EasyPermissions.hasPermissions(this, perms)) {

            LOG.debug("Location mode " + getLocationMode(this));

            if(getLocationMode(this) == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY || (BuildConfig.VERSION_CODE >= 28)) {

                ContextCompat.startForegroundService(getApplicationContext(), myService);
                getApplicationContext().bindService(myService, mConnection, Context.BIND_AUTO_CREATE);


            }else{

                Toast.makeText(getApplicationContext(), "You must enable location - high accuracy", Toast.LENGTH_LONG).show();

                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                startActivityForResult(new Intent(action), REQUEST_SOURCE_LOCATION_REQUEST_CODE);

            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getApplicationContext().getString(R.string.Onboarding_Dialog_Box_Ask_Location_Storage_Permissions),
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


                }else{
                    Toast.makeText(getApplicationContext(), "You must enable location - high accuracy in order for the app to track your trips. Please press start detection and enable!", Toast.LENGTH_LONG).show();
                }
                break;

            case (REPORTING_RESULT):

                if(resultCode == RELOAD_REPORTING) {

                    String language = "eng";

                    //todo fix this. Need to check survey language of creation...assign to lang if the
                    // user uses a locale different than the ones available on the survey

                    for (Language lang : Language.getAllLanguages()) {
                        if (getResources().getConfiguration().locale.getLanguage().equals(new Locale(lang.getSmartphoneID()).getLanguage())) {
                            language = lang.getWoortiID();
                            Log.e("Survey Test Activity", "Language matched. Will try to show survey in " + lang.getName());
                            break;
                        }
                    }

                    SurveyStateful surveyStateful = new SurveyStateful(reportingSurvey, false, new ArrayList<Answer>(), 0l, logTS, FirebaseAuth.getInstance().getUid(), false, language);

                    Intent intentShowSurvey = new Intent(getApplicationContext(), SurveyTestActivity.class);
                    intentShowSurvey.putExtra("SurveyStateful", surveyStateful);
                    intentShowSurvey.putExtra("isReporting", true);


                    startActivityForResult(intentShowSurvey, REPORTING_RESULT);
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> permsFailed) {

        Toast.makeText(this, getApplicationContext().getString(R.string.Onboarding_Warning_Permissions_Denied_Retry), Toast.LENGTH_LONG).show();
        askForPermissions();

    }

    public void askForPermissions(){

        EasyPermissions.requestPermissions(this,
                getApplicationContext().getString(R.string.Onboarding_Dialog_Box_Ask_Location_Storage_Permissions),
                FINE_LOCATION_AND_READ_EXTERNAL_STORAGE,
                perms);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.home) {
            fragmentClass = HomeFragment.class;
        }

        else
        if (id == R.id.my_trips) {
            fragmentClass = MyTripsFragment.class;
        }
        else if (id == R.id.dashboard) {
            fragmentClass = DashboardFragment.class;
        } else if (id == R.id.mobility_coach) {
            fragmentClass = MobilityCoachFragment.class;
        }
          else if (id == R.id.settings) {
            fragmentClass = ProfileAndSettingsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment).commitAllowingStateLoss();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void finishAndLogout() {

        if(serviceState) {
            stopServiceAndForceFinishTrip();
        }

        FirebaseAuth.getInstance().signOut();

        GoogleSignIn.getClient(
                getApplicationContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        startActivity(new Intent(getApplicationContext(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openDrawer(){
        drawer.openDrawer(GravityCompat.START);
    }

    private boolean mailClientOpened = false;


    long logTS = 0l;
    String uid;

    public void reportIssue(){

        MotivAPIClientManager.getInstance(getApplicationContext()).getReportingSurvey();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                reportingSurveyReceiver, new IntentFilter(MotivAPIClientManager.keys.reportingSurveyBroadcastKey));

    }

    Survey reportingSurvey;

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "ActivityRecognitionResult" is broadcasted.
    private BroadcastReceiver reportingSurveyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            LOG.debug(  "Full trip finished message received in main activity - sent from state machine");

           if(intent.getStringExtra(MotivAPIClientManager.keys.result).equals(MotivAPIClientManager.keys.success)){

               reportingSurvey = (Survey) intent.getSerializableExtra(MotivAPIClientManager.keys.reportingSurveyKey);

               String language = "eng";

               // user uses a locale different than the ones available on the survey

               for(Language lang : Language.getAllLanguages()){
                   if(getResources().getConfiguration().locale.getLanguage().equals(new Locale(lang.getSmartphoneID()).getLanguage())){
                       language = lang.getWoortiID();
                       Log.e("Survey Test Activity", "Language matched. Will try to show survey in " + lang.getName());
                       break;
                   }
               }

               LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this);

               SurveyStateful surveyStateful = new SurveyStateful(reportingSurvey,false, new ArrayList<Answer>(), 0l, logTS, FirebaseAuth.getInstance().getUid(), false, language);

               Intent intentShowSurvey = new Intent(getApplicationContext(), SurveyTestActivity.class);
               intentShowSurvey.putExtra("SurveyStateful", surveyStateful);
               intentShowSurvey.putExtra("isReporting", true);


               startActivityForResult(intentShowSurvey, REPORTING_RESULT);

           }else{

               Toast.makeText(getApplicationContext(), "Failed to retrieve reporting survey. Please check your internet connection", Toast.LENGTH_LONG).show();
           }

        }
    };

    public static final int RELOAD_REPORTING = 1002;
    public static final int REPORTING_RESULT = 1001;

    public interface keys{

        int MY_TRIPS = 0;
        int MOBILITY_COACH = 1;
        int DASHBOARD = 2;
        int HOME = 3;

    }

}

package inesc_id.pt.motivandroid.home.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.myTrips.activities.DeleteSplitMergeTripsActivity;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.myTrips.adapters.ListMyTripsAdapter;
import inesc_id.pt.motivandroid.myTrips.sort.SortFullTripDigestsByRecentDate;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.showOngoingTrip.ShowOngoingTrip;
import inesc_id.pt.motivandroid.tripStateMachine.TripStateMachine;
import inesc_id.pt.motivandroid.utils.MiscUtils;

import static android.app.Activity.RESULT_CANCELED;

/**
 *
 * MyTripsFragment
 *
 * Main My trips menu fragment. Presents the user with:
 *  - user list of trips (vertically from most to less recent)
 *  - if there a currently ongoing trip (pressing this TextView also allows the user to check a preview
 *  of the current trip
 *  - button to force start a trip or force stop a trip
 *  - button to allow the user to delete, join or split trips
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
public class MyTripsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int chosenTrip = -1;

    private OnFragmentInteractionListener mListener;

    public PersistentTripStorage persistentTripStorage;

//    public ArrayList<FullTrip> myTripsList;
    public ArrayList<FullTripDigest> myTripsDigestsList;

    public ListView myTripsListView;

    private ListMyTripsAdapter adapter;

    private ImageButton openDrawerButton;

    Button startStopDetectionButton;
//    Button showOngoingTripButton;

    Context context;

    Button editButton;
    TextView noTripsAvailableTextView;

    public MyTripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyTripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTripsFragment newInstance(String param1, String param2) {
        MyTripsFragment fragment = new MyTripsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        persistentTripStorage = new PersistentTripStorage(context);

        //get user trip digests from database
        myTripsDigestsList = persistentTripStorage.getAllFullTripDigestsByUserIDObjects(FirebaseAuth.getInstance().getUid());

    }

    TextView tripBeingRecordedTextView;

    TextView welcomeUserTextView;

    FrameLayout overlayLayout;

    ImageButton batteryPopupButton;

    LinearLayout popupMessageLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_trips_coordinated, container, false);

        overlayLayout = view.findViewById(R.id.progress_view);

        batteryPopupButton = view.findViewById(R.id.batteryPopupButton);
        batteryPopupButton.setOnClickListener(this);

        startStopDetectionButton = view.findViewById(R.id.startStopDetectionButton);
        startStopDetectionButton.setOnClickListener(this);
        startStopDetectionButton.setVisibility(View.INVISIBLE);

//        showOngoingTripButton = view.findViewById(R.id.showOngoingTripButton);
//        showOngoingTripButton.setOnClickListener(this);

        editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(this);

        noTripsAvailableTextView = view.findViewById(R.id.noTripsRecordedTextView);
        noTripsAvailableTextView.setVisibility(View.INVISIBLE);

        welcomeUserTextView = view.findViewById(R.id.welcomeUserTextView);
        welcomeUserTextView.setVisibility(View.INVISIBLE);

        tripBeingRecordedTextView = view.findViewById(R.id.tripBeingRecordedTextView);
        tripBeingRecordedTextView.setOnClickListener(this);

        popupMessageLinearLayout = view.findViewById(R.id.mytripsPopupLinearLayout);

//        address_looking_up = view.findViewById(R.id.address_looking_up);

        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), null);

        if(userSettingStateWrapper != null){

            if (userSettingStateWrapper.getUserSettings() != null){

                //show battery optimization stuff popup
                if(!userSettingStateWrapper.getUserSettings().getSeenBateryPopup()){
                    showBatteryAlterPopup();
                    userSettingStateWrapper.getUserSettings().setSeenBateryPopup(true);
                    SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);
                }else{

                    // check to see if we need to show block activity popup
                    if ((FirebaseAuth.getInstance().getCurrentUser() != null) &&
                            (FirebaseAuth.getInstance().getCurrentUser().getMetadata() != null) &&
                            (FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp() != FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp()) &&
                            (!userSettingStateWrapper.getUserSettings().isDontShowBlockPopup())
                            && persistentTripStorage.getAllFullTripDigestsForTimeIntervalObjects(DateTime.now().minusDays(3).getMillis()).size() == 0
                            ){
                            showTripDetectionBlockingPopup(userSettingStateWrapper);
                    }

                }

        }
        }

        return view;
    }

    ContentLoadingProgressBar address_looking_up;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        ImageView imageView = (ImageView) getView().findViewById(R.id.foo);


        myTripsListView = getView().findViewById(R.id.myTripsListView);

        myTripsListView.setOnItemClickListener(this);

        openDrawerButton = getView().findViewById(R.id.openDrawerButton);
        openDrawerButton.setOnClickListener(this);



        //todo warning...code just to recreate trips from disk (erased db...)
//        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getContext());
//        persistentTripStorage.readFullTripsErasedAndRecreateDigest();
//          persistentTripStorage.deleteAllRewardsObject();


    }

    @Override
    public void onResume() {
        super.onResume();

        sortAndUpdateUI();

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mFullTripReceiver, new IntentFilter("FullTripFinished"));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mFullTripStartedReceiver, new IntentFilter("FullTripStarted"));


    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(context).unregisterReceiver(mFullTripReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mFullTripStartedReceiver);
    }

    public void sortAndUpdateUI(){

        //get user trip digests from disk
        myTripsDigestsList = persistentTripStorage.getAllFullTripDigestsByUserIDObjects(FirebaseAuth.getInstance().getUid());

        for(FullTripDigest fullTripDigest : myTripsDigestsList){

            //check if any of the user trips is lacking start/end address textual representation,
            //and if so try to retrieve the missing info
            if ((fullTripDigest.getStartAddress() == null) || (fullTripDigest.getFinalAddress() == null)){

                String fromPlace = null;
                String toPlace = null;

                if (fullTripDigest.getStartAddress() == null) {

                    fromPlace = getPlaceFromCoordinatesGeocoder(fullTripDigest.getStartLocation().getLatLng());
                    Log.e("placefrom", "from" + fromPlace);
                }

                if (fullTripDigest.getFinalAddress() == null) {

                    toPlace = getPlaceFromCoordinatesGeocoder(fullTripDigest.getFinalLocation().getLatLng());
                    Log.e("placeto", "to" + toPlace);
                }

                if ((fromPlace != null) && (toPlace != null)) {

                    fullTripDigest.setStartAddress(fromPlace);
                    fullTripDigest.setFinalAddress(toPlace);

                    persistentTripStorage.updateFullTripDigestDataObject(fullTripDigest,fullTripDigest.getTripID());


                }else{
                    Log.e("MyTripsFragment", "Unable to retrieve places");
                }

            }

        }

        //user has any trip digests
        if(myTripsDigestsList.size() != 0){
            //sort trip digests from most recent to less recent
            SortFullTripDigestsByRecentDate sortFullTripByRecentDate = new SortFullTripDigestsByRecentDate();
            Collections.sort(myTripsDigestsList, sortFullTripByRecentDate);

            adapter = new ListMyTripsAdapter(myTripsDigestsList,context);
            myTripsListView.setAdapter(adapter);
            myTripsListView.setVisibility(View.VISIBLE);
            noTripsAvailableTextView.setVisibility(View.INVISIBLE);
            welcomeUserTextView.setVisibility(View.INVISIBLE);

            popupMessageLinearLayout.removeAllViews();
            LayoutInflater li = LayoutInflater.from(context);
            ConstraintLayout myTripsMessageLayout = (ConstraintLayout) li.inflate(R.layout.mytrips_popup_message_layout, null, false);
            TextView popupMessage = myTripsMessageLayout.findViewById(R.id.popupMessage);

            if(checkIfHas0TripsReported(myTripsDigestsList)){

                popupMessage.setText(context.getString(R.string.These_Are_The_Trips_You_Have_Made));
                popupMessageLinearLayout.addView(myTripsMessageLayout);

                Log.e("MyTripsFragment", "0 reported trips");

            }else{

                int tripsToReportInTheLast3Days = checkAmountOfPendingTripsInLast3Days(myTripsDigestsList);

                String[] array = {tripsToReportInTheLast3Days+""};

                String message = context.getString(R.string.You_Have_X_Trips_Left_3_Days, tripsToReportInTheLast3Days);

                popupMessage.setText(MiscUtils.styleKeywords(message, array, new ForegroundColorSpan(getResources().getColor(R.color.colorOrangeTripPolyline))));

//                popupMessage.setText(context.getString(R.string.You_Have_X_Trips_Left_3_Days, tripsToReportInTheLast3Days));
                popupMessageLinearLayout.addView(myTripsMessageLayout);

                Log.e("MyTripsFragment", "Trips to report in the last 3 days");


            }

        }else{ //user has no trip digests yet
            editButton.setVisibility(View.INVISIBLE);
            noTripsAvailableTextView.setVisibility(View.VISIBLE);
            welcomeUserTextView.setVisibility(View.VISIBLE);

            UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, "");

            popupMessageLinearLayout.removeAllViews();

            if (userSettingStateWrapper != null && userSettingStateWrapper.getUserSettings() != null && userSettingStateWrapper.getUserSettings().getName() != null){
                welcomeUserTextView.setText(context.getString(R.string.Welcome_To_Your_Trips_Board_Username,userSettingStateWrapper.getUserSettings().getName()));

                Log.e("MyTripsFragment", "name " + userSettingStateWrapper.getUserSettings().getName());

            }


            myTripsListView.setVisibility(View.INVISIBLE);
        }



        if ((chosenTrip != -1) && (chosenTrip < myTripsDigestsList.size())){

            timerDelayRunForScroll(0);
        }


//        todo: test post processing filtering for all my trips
//        for (FullTripDigest fullTripDigest : myTripsDigestsList){
//
//            TripAnalysis tripAnalysis = new TripAnalysis(false);
//
//            FullTrip fullTrip = new PersistentTripStorage(getActivity()).getFullTripByDate(fullTripDigest.getTripID());
//
//            try {
//                tripAnalysis.analyseListOfTrips(fullTrip.getTripList(), false, false);
//            }catch (Exception e){
//                Log.e("MyTripsFragment",e.getMessage());
//            }
//        }

        CheckStateMachineAndUpdateButton checkStateMachineAndUpdateButton = new CheckStateMachineAndUpdateButton();
        checkStateMachineAndUpdateButton.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * @param digestArray array of user trip digests
     * @return true if at least one the trips hasn't been validated yet, false otherwise
     */
    private boolean checkIfPendingTrips(ArrayList<FullTripDigest> digestArray) {

        for(FullTripDigest digest : digestArray){
            if (!digest.isValidated()) return true;
        }

        return false;
    }

    /**
     * @param digestArrayList array of user trip digests
     * @return amount of unvalidated trips in the last three days
     */
    private int checkAmountOfPendingTripsInLast3Days(ArrayList<FullTripDigest> digestArrayList){

        int i = 0;

        long threeDaysBeforeTS = DateTime.now().minusDays(3).getMillis();

        for(FullTripDigest digest : digestArrayList){
            if (!digest.isValidated() && (digest.getInitTimestamp() > threeDaysBeforeTS)) i++;
        }

        return i;
    }

    /**
     * @param digestArrayList array of user trip digests
     * @return true if the user has 0 reported trips, false otherwise
     */
    private boolean checkIfHas0TripsReported(ArrayList<FullTripDigest> digestArrayList){


        for(FullTripDigest digest : digestArrayList){
            if (digest.isValidated()) return false;
        }

        return true;
    }

    /**
     * check trip state machine state and update Start/End trip button
     */
    private class CheckStateMachineAndUpdateButton extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute()
        {
            Log.e("onPreExecutive","showing progress bar");

            overlayLayout.setVisibility(View.VISIBLE);



        }

        protected Void doInBackground(Void... urls) {

            TripStateMachine.getInstance(context, false, true);

            publishProgress();

            return null;
        }

        protected void onProgressUpdate(Void... progress) {

            Log.e("onProgressUpdate","Hide progressbar");
            overlayLayout.setVisibility(View.GONE);

            startStopDetectionButton.setVisibility(View.VISIBLE);


            if(TripStateMachine.getInstance(context, false, true).currentState
                    == TripStateMachine.state.still){ //no ongoing trip

                startStopDetectionButton.setText(R.string.Start_Trip);
                startStopDetectionButton.setBackgroundResource(R.drawable.home_surveys_answer_done_button);
                tripBeingRecordedTextView.setVisibility(View.INVISIBLE);


            }else{

                startStopDetectionButton.setText(R.string.End_Trip);
                startStopDetectionButton.setBackgroundResource(R.drawable.home_surveys_answer_no_button);
                tripBeingRecordedTextView.setVisibility(View.VISIBLE);

            }
        }
    }

    // Our handler for received Intents. This will be called a trip has been ackowledged has ended
    private BroadcastReceiver mFullTripReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            sortAndUpdateUI();

        }
    };

    // Our handler for received Intents. This will be called whenever a trip has been started
    private BroadcastReceiver mFullTripStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            CheckStateMachineAndUpdateButton checkStateMachineAndUpdateButton = new CheckStateMachineAndUpdateButton();
            checkStateMachineAndUpdateButton.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    };

    public void timerDelayRunForScroll(long time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    myTripsListView.setSelection(chosenTrip);
                } catch (Exception e) {}
            }
        }, time);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * @param latLng location
     * @return get textual representation corresponding to the location provided (address in text)
     */
    public String getPlaceFromCoordinatesGeocoder(LatLng latLng){

        try {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.isEmpty()) {
                return null;
            } else {
                if (addresses.size() > 0) {

                    if(addresses.get(0).getMaxAddressLineIndex()>=0){
                        return addresses.get(0).getAddressLine(0);
                    }else{
                        return addresses.get(0).getThoroughfare() + ", "+ addresses.get(0).getSubThoroughfare();
                    }
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
            return null;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * handle user click on a trip digest. Starts the MyTripsActivity for the user to start the
     * trip validation process
     *
     * @param parent
     * @param view
     * @param position index chosen on the list
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            FullTripDigest selectedTrip = myTripsDigestsList.get(position);

            chosenTrip = position;

            Intent intent = new Intent(context, MyTripsActivity.class);
            intent.putExtra(keys.FULLTRIP_DATE_TO_BE_VALIDATED, selectedTrip.getTripID()); //todo
            startActivityForResult(intent, keys.MY_TRIPS_VALIDATION_REQUEST_CODE);

    }

    // Call Back method  to get the Message form other Activity
    //when the user returns from validating a trip
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("MyTripsFragment", "onActivityResult reqCode" + requestCode + " resCode " + resultCode);

        // check if the request code is same as what is passed  here it is 2
        if(requestCode== keys.MY_TRIPS_VALIDATION_REQUEST_CODE)
        {
            if(resultCode != RESULT_CANCELED)

            if (data != null) {
                boolean validatedTripSuccess = data.getBooleanExtra(keys.MY_TRIPS_VALIDATION_RESULT, false);

                Log.e("MyTripsFragment", "validatedTripSuccess " + validatedTripSuccess);

                if (validatedTripSuccess) {

                    UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

                    if(userSettingStateWrapper != null && userSettingStateWrapper.getUserSettings() != null){

                        if (!userSettingStateWrapper.getUserSettings().isHasReportedTrip()) {
                            userSettingStateWrapper.getUserSettings().setHasReportedTrip(true);
                            SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

                            showYouHaveAReportedTripPopup();

                        }else{
                            // check if we have to show the fill in the profile popup

                            if (!userSettingStateWrapper.getUserSettings().isDontShowTellUsMorePopup()){

                                if (myTripsDigestsList != null && userSettingStateWrapper.getUserSettings().hasUnfilledHousehold()){

                                    if (FullTripDigest.checkNumberOfValidatedTrips(myTripsDigestsList) >= 5) {

                                        if( new Random().nextDouble() <= 0.33 ) {
                                            // if user has more than 5 validated trips and yet not
                                            //finished filling its profile info, show a popup for the
                                            //to go fill more info (only shown with a 33 percent chance)

                                            showTellUsMoreAboutYourselfPopup(userSettingStateWrapper);

                                        }

                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * show a popup informing the user that he/she has completed reporting a trip and may now check
     * the dashboard for user trip stats
     */
    private void showYouHaveAReportedTripPopup() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.you_have_a_reported_trip_popup_layout, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.imageView12);

        Glide.with(this.getContext()).load(R.drawable.onboarding_illustrations_help_research).into(imageView);

        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button goToDashboardButton = mView.findViewById(R.id.goToDashboardButton);

        goToDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, new DashboardFragment()).commit();
            }
        });

    }

    /**
     * show a popup to alert the user that its smartphone preferences might preventing the woorti
     * app from gathering trips successfully
     *
     * @param userSettingStateWrapper
     */
    private void showTripDetectionBlockingPopup(final UserSettingStateWrapper userSettingStateWrapper) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.is_your_phone_blocking_trip_detection_popup, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.popupImageView);

        Glide.with(this.getContext()).load(R.drawable.popup_blocking_illustration).into(imageView);

        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button continueButton = mView.findViewById(R.id.continueButton);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.main_fragment, new DashboardFragment()).commit();
            }
        });

        Button goToEnableAppActivity = mView.findViewById(R.id.enableAppActButton);

        goToEnableAppActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dontkillmyapp.com/"));
                startActivity(browserIntent);

            }
        });

        TextView dontShowAgain = mView.findViewById(R.id.doNotShowAgainTextView);
        dontShowAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                userSettingStateWrapper.getUserSettings().setDontShowBlockPopup(true);
                SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

            }
        });

    }

    /**
     * show a popup to warn the user that there is still unfilled profile info
     *
     * @param userSettingStateWrapper
     */
    private void showTellUsMoreAboutYourselfPopup(final UserSettingStateWrapper userSettingStateWrapper) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.tell_us_more_about_yourself_popup, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.popupImageView);

        Glide.with(this.getContext()).load(R.drawable.mytrips_navigation_enjoyment_icon).into(imageView);

        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView laterTextView = mView.findViewById(R.id.laterTextView);

        laterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        Button fillInProfileButton = mView.findViewById(R.id.fillInProfileButton);

        fillInProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();

                dialog.dismiss();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, ProfileAndSettingsFragment.newInstance(ProfileAndSettingsFragment.keys.goToDemographicInfo)).commit();

            }
        });

        TextView dontShowAgainTextView = mView.findViewById(R.id.dontShowAgainTextView);
        dontShowAgainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                userSettingStateWrapper.getUserSettings().setDontShowTellUsMorePopup(true);
                SharedPreferencesUtil.writeOnboardingUserData(context, userSettingStateWrapper, true);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.openDrawerButton:
                ((HomeDrawerActivity) getActivity()).openDrawer();
                break;
            case R.id.startStopDetectionButton:

                if(TripStateMachine.getInstance(context, false, true).currentState == TripStateMachine.state.still){

                    TripStateMachine.getInstance(context, false, true).forceStartTrip();

                    startStopDetectionButton.setText(R.string.End_Trip);

                }else{

                    TripStateMachine.getInstance(context, false, true).forceFinishTrip(false);
                    startStopDetectionButton.setText(R.string.Start_Trip);

                }

                break;
            case R.id.tripBeingRecordedTextView:

                if(TripStateMachine.getInstance(context, false, true).getCurrentOngoingTrip() == null){

                    Toast.makeText(context, R.string.No_Ongoing_Trip_To_Show, Toast.LENGTH_SHORT).show();

                    tripBeingRecordedTextView.setVisibility(View.INVISIBLE);

                }else{

                    startActivity(new Intent(context, ShowOngoingTrip.class));

                }

                break;

            case R.id.editButton:

                showEditTripsDialog();

                break;

            case R.id.batteryPopupButton:

                showBatteryAlterPopup();

                break;

        }
    }


    /**
     * show popup to warn the user about possible smartphone battery optimization preferences that
     * might be preventing the woorti app from successfully gathering trips.
     */
    private void showBatteryAlterPopup() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.battery_alert_popup_layout, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button closeButton = mView.findViewById(R.id.closePopUpButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //call method on fragment to show add activities dialog
            }
        });

    }

    /**
     * shows a popup that allows the user to go to split, merge, delete trips menu
     */
    private void showEditTripsDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.my_trips_split_merge_delete_dialog, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        ConstraintLayout splitButton = mView.findViewById(R.id.splitButton);
        ConstraintLayout mergeButton = mView.findViewById(R.id.mergeButton);
        ConstraintLayout deleteButton = mView.findViewById(R.id.deleteButton);

        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(createSplitsTripsIntent());
                //call method on fragment to show add activities dialog
            }
        });

        mergeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(createMergeTripsIntent());
                //call method on fragment to show add activities dialog
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(createDeleteTripsIntent());
                //call method on fragment to show add activities dialog
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public Intent createMergeTripsIntent(){
        Intent mergeIntent = new Intent(getContext(), DeleteSplitMergeTripsActivity.class);
        mergeIntent.putExtra(DeleteSplitMergeTripsActivity.keys.mode, DeleteSplitMergeTripsActivity.keys.MERGE_MODE);

        return mergeIntent;
    }

    public Intent createSplitsTripsIntent(){
        Intent splitIntent = new Intent(getContext(), DeleteSplitMergeTripsActivity.class);
        splitIntent.putExtra(DeleteSplitMergeTripsActivity.keys.mode, DeleteSplitMergeTripsActivity.keys.SPLIT_MODE);

        return splitIntent;
    }

    public Intent createDeleteTripsIntent(){

        Intent deleteIntent = new Intent(getContext(), DeleteSplitMergeTripsActivity.class);
        deleteIntent.putExtra(DeleteSplitMergeTripsActivity.keys.mode, DeleteSplitMergeTripsActivity.keys.DELETE_MODE);

        return deleteIntent;

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface keys {

        String FULLTRIP_DATE_TO_BE_VALIDATED = "fullTripToBeValidated";
        String FULL_TRIP_PART_ACTIVITIES = "fullTripToAddActivities";
        String FULL_TRIP_VALIDATION_WRAPPER = "fullTripValidationWrapper";
        String FULL_TRIP_VALIDATION_INDEX = "fullTripValidationIndex";

        int MY_TRIPS_VALIDATION_REQUEST_CODE = 101;
        String MY_TRIPS_VALIDATION_RESULT = "MY_TRIPS_VALIDATION_RESULT";

    }

}

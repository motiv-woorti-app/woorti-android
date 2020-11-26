package inesc_id.pt.motivandroid.home.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.UserStatsManager;
import inesc_id.pt.motivandroid.dashboard.activities.CommunityStatsActivity;
import inesc_id.pt.motivandroid.dashboard.activities.UserStatsActivity;
import inesc_id.pt.motivandroid.dashboard.activities.WorthwhilenessStatsActivity;
import inesc_id.pt.motivandroid.dashboard.holders.CommunityStatsHolder;
import inesc_id.pt.motivandroid.dashboard.holders.UserStatsHolder;
import inesc_id.pt.motivandroid.dashboard.holders.WorthwhilenssStatsHolder;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.DistanceTimeCaloriesActivities;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.WorthwhilenessCO2Score;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.statsFromServer.CorrectedModeServer;
import inesc_id.pt.motivandroid.data.statsFromServer.StatsFromTimeIntervalStruct;
import inesc_id.pt.motivandroid.data.statsFromServer.StatsFromTimeIntervalStructForUser;
import inesc_id.pt.motivandroid.data.statsFromServer.ValueFromTripTotalServer;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.CO2ModeCounter;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.CaloriesModeCounter;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.ModalityTimeDistanceCounter;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.GenericActivityLeg;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.MiscUtils;
import inesc_id.pt.motivandroid.utils.NumbersUtil;
import inesc_id.pt.motivandroid.utils.PopupUtil;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 *
 * DashboardFragment
 *
 * Main dashboard fragment. Presents usage statistics to the user. Allows the user to compare its
 * statistics and worthwhileness scores with the numbers from the motiv community.
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
public class DashboardFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    UserSettingStateWrapper userSettingStateWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");
        UserStatsManager.getInstance().checkIfStatsAreFreshAndRetrieve(getContext());

    }

    PersistentTripStorage persistentTripStorage;

    TextView youHaveSpentTextView;
    TextView inTotalSpentCaloriesTextView;
    TextView footprintTextView;

    TextView yourTripsTextOverallScore;
    TextView productiveScoreTextView;
    TextView enjoymentScoreTextView;
    TextView fitnessScoreTextView;
    TextView worthwhileScoreTextView;
    TextView whileTravelingTextView;

    String city;

    /**
     * dropdown button to choose the desired time interval
     */
    Button buttonDays;

    LinearLayout activitiesLinearLayout;

    PopupWindow popupWindow;

    int modeUserOrCommunity;
    String campaignID;


    /**
     * dropdown button to switch between user mode ("You" - only user stats considered) and other
     * community modes (country, city, campaigns etc)
     */
    Button buttonUserCommunity;

    ConstraintLayout worthwhilenessOverallLayout;

    ConstraintLayout worthwhilenessScoreConstraintLayout;
    ConstraintLayout fitnessScoreConstraintLayout;
    ConstraintLayout enjoymentScoreConstraintLayout;
    TextView productivityLabelTextView;
    TextView enjoymentLabelTextView;
    TextView fitnessLabelTextView;
    TextView worthwhilenessLabelTextView;

    ConstraintLayout spentActivitiesLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.dashboard_time_distance_by_day, container, false);

        spentActivitiesLayout = view.findViewById(R.id.spentActivitiesLayout);

        worthwhilenessScoreConstraintLayout = view.findViewById(R.id.worthwhilenessScoreConstraintLayout);
        fitnessScoreConstraintLayout = view.findViewById(R.id.fitnessScoreConstraintLayout);
        enjoymentScoreConstraintLayout = view.findViewById(R.id.enjoymentScoreConstraintLayout);

        productivityLabelTextView = view.findViewById(R.id.productivityLabelTextView);
        enjoymentLabelTextView = view.findViewById(R.id.enjoymentStringTextView);
        fitnessLabelTextView = view.findViewById(R.id.fitnessStringTextView);
        worthwhilenessLabelTextView = view.findViewById(R.id.textView34);

        ImageButton worthWhilenessInfoButton = view.findViewById(R.id.infoWorthwhilenessButton);
        worthWhilenessInfoButton.setOnClickListener(this);

        ImageButton infoCarbonFootprintButton = view.findViewById(R.id.infoCarbonFootprintButton);
        infoCarbonFootprintButton.setOnClickListener(this);

        worthwhilenessOverallLayout = view.findViewById(R.id.worthwhilenessOverallLayout);

        youHaveSpentTextView = view.findViewById(R.id.tvYouHaveSpent);

        inTotalSpentCaloriesTextView = view.findViewById(R.id.tvCalories);

        footprintTextView = view.findViewById(R.id.tvFootprint);

        ImageView graphImageView = view.findViewById(R.id.imageView20);

        Glide.with(this).load(R.drawable.dashboard_illustraion_factors_diagram)
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                .into(graphImageView);

        yourTripsTextOverallScore = view.findViewById(R.id.yourTripsWorthwhileTextView);
        productiveScoreTextView = view.findViewById(R.id.productiveValueTextView);
        enjoymentScoreTextView = view.findViewById(R.id.enjoymentValueTextView);
        fitnessScoreTextView = view.findViewById(R.id.fitnessValueTextView);
        worthwhileScoreTextView = view.findViewById(R.id.worthwhileValueTextView);
        whileTravelingTextView = view.findViewById(R.id.whileTravelingTextView);

        activitiesLinearLayout = (LinearLayout) view.findViewById(R.id.activitiesBarLayout);

        buttonDays = view.findViewById(R.id.buttonDays);
        buttonDays.setOnClickListener(this);

        ImageButton openDrawerButton = view.findViewById(R.id.openDrawerButton);
        openDrawerButton.setOnClickListener(buttonListener);

        persistentTripStorage = new PersistentTripStorage(getContext());

        ArrayList<FullTripDigest> arrayListDigests3days = persistentTripStorage.getAllFullTripDigestsForTimeIntervalObjects(DateTime.now().minusDays(3).getMillis());

        for(FullTripDigest fullTripDigest : arrayListDigests3days){
            Log.e("DashboardFragment","digest " + fullTripDigest.getInitTimestamp());
        }

        UserSettingStateWrapper user = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

        if((user != null) && (user.getUserSettings() != null) && (!user.getUserSettings().isHasGoneToDashboard())){
            showWhatIsDashboardPopup();
            user.getUserSettings().setHasGoneToDashboard(true);
            SharedPreferencesUtil.writeOnboardingUserData(getContext(), user, true);
        }

        city = user.getUserSettings().getCity();

        currentIntervalKey = keys.day3;
        getDigestsAndRedraw(DateTime.now().minusDays(3).getMillis(), inflater);

        ImageButton distanceStatsButton = view.findViewById(R.id.distanceStatsButton);
        distanceStatsButton.setOnClickListener(this);

        ImageButton timeStatsButton = view.findViewById(R.id.timeStatsButton);
        timeStatsButton.setOnClickListener(this);

        Button caloriesStatsButton = view.findViewById(R.id.caloriesStatsButton);
        caloriesStatsButton.setOnClickListener(this);

        ImageButton co2StatsButton = view.findViewById(R.id.co2StatsButton);
        co2StatsButton.setOnClickListener(this);

        buttonUserCommunity = view.findViewById(R.id.buttonYou);
        buttonUserCommunity.setOnClickListener(this);

//        activitiesBarLayout = view.findViewById(R.id.activitiesBarLayout);

        return view;
    }

    private void showWhatIsCarbonBudgetPopup() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.info_carbon_footprint_popup_layout, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.imageView21);

        Glide.with(this.getContext()).load(R.drawable.carbon_popup_illustration).into(imageView);

        TextView linkTextView = mView.findViewById(R.id.linkTextView);
        linkTextView.setPaintFlags(linkTextView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.CO2_Budget_Link_URL)));
                startActivity(browserIntent);

            }
        });

        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }


    private void showWhatIsDashboardPopup() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.this_is_your_dashboard_popup_layout, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.imageView12);

        Glide.with(this.getContext()).load(R.drawable.onboarding_illustrations_help_research).into(imageView);

        final AlertDialog dialog = mBuilder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null){
                    Fragment myFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.main_fragment);
                    if (myFragment instanceof DashboardFragment) {
                        dialog.show();
                    }
                }
            }
        }, 4000);

        Button seeDashboardButton = mView.findViewById(R.id.seeDashboardButton);

        seeDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //call method on fragment to show add activities dialog
            }
        });

        Button reportTripsButton = mView.findViewById(R.id.reportTripsButtons);

        reportTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, new MyTripsFragment()).commit();
            }
        });

    }


    void setTextWithSpan(TextView textView, String text, ArrayList<String> spanTextArray, StyleSpan style) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);

        for(String spanText: spanTextArray) {
            int start = text.indexOf(spanText);
            int end = start + spanText.length();
            sb.setSpan(style, start, end, SPAN_INCLUSIVE_INCLUSIVE);
        }
        textView.setText(sb);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.openDrawerButton:
                    try {
                        ((HomeDrawerActivity) getActivity()).openDrawer();
                    } catch (Exception e){ Log.e("DashboardFragment", "Null pointer on getActivity");}
                    break;
            }
        }
    };


    /**
     * array of trip digests for the user specified time interval
     */
    ArrayList<FullTripDigest> timeIntervalDigests;

    /**
     * computes and draws user activity stats ("You" user stats only mode)
     *
     * @param activityTimeCounters activities done by the user and the time spent doing these
     * @param inflater
     */
    private void drawUserTripActivityData(HashMap<String, Double> activityTimeCounters, LayoutInflater inflater){

        if(activityTimeCounters.entrySet().size() > 0){

            spentActivitiesLayout.setVisibility(View.VISIBLE);

            int i = 0;

            int percentageOther = 100;

            if(activityTimeCounters.containsKey(GenericActivityLeg.keys.ActivitiesCodeText[0]) && activityTimeCounters.entrySet().size() > 0 ){
                activityTimeCounters.remove(GenericActivityLeg.keys.ActivitiesCodeText[0]);
            }

            //compute total time of all activities

            double totalTimeOfAllActivitiesInMinutes = 0; //trip stats time counter already in minutes

            for (Map.Entry<String, Double> entry : activityTimeCounters.entrySet()) {  // Iterate through hashmap
                totalTimeOfAllActivitiesInMinutes += entry.getValue();
            }


            while(i<=2 && activityTimeCounters.entrySet().size() > 0){

                double maxActivityTime = 0;
                String activityText = "default";

                Double maxValueInMap=(Collections.max(activityTimeCounters.values()));  // This will return max value in the Hashmap
                String key = "";

                for (Map.Entry<String, Double> entry : activityTimeCounters.entrySet()) {  // Iterate through hashmap

                    if (entry.getValue() == maxValueInMap) {
                        maxActivityTime = maxValueInMap;
                        activityText = GenericActivityLeg.getActivityTextFromTextCode(entry.getKey(), getContext());
                        key = entry.getKey();
                    }
                }

                int percentage = (int) ((maxActivityTime / totalTimeOfAllActivitiesInMinutes) * 100.0);

                percentageOther -= percentage;

                addActivitiesBar(activityText, percentage,  activitiesLinearLayout, inflater);

                //remove max value and repeat to obtain 2nd and 3rd "most done" activities
                activityTimeCounters.remove(key);

                if (i == 0){
                    whileTravelingTextView.setText(getString(R.string.While_Traveling_Activities, percentage+"%", activityText));
                }

                // no more activities, lets draw
                if(activityTimeCounters.entrySet().size() == 0) break;

                i++;
            }

            //adding other bar in the end
            addActivitiesBar(getString(R.string.Other), percentageOther, activitiesLinearLayout, inflater);
            Log.e("DashboardFragment","total time traveled mins = " + totalTimeOfAllActivitiesInMinutes);

        }else{
            //no activities in the specified interval, just add other
            Log.e("DashFragment", "No activities");
//            whileTravelingTextView.setText(getString(R.string.While_Traveling_Activities, 100+"%", getString(R.string.Other)));
//            addActivitiesBar(getString(R.string.Other), 0, activitiesLinearLayout, inflater);

            Log.e("DashFragment", "GONE");

            spentActivitiesLayout.setVisibility(View.GONE);

        }

    }


    /**
     * compute and draw community stats
     *
     * @param initTS initial timestamp of the desired time interval
     * @param inflater
     * @param modeUserOrCommunity which is the mode (country, campaign or per city region)
     * @param campaignID id of the selected campaign, if one was selected
     */
    private void getDigestsAndRedrawCommunityStats(long initTS, LayoutInflater inflater,int modeUserOrCommunity, String campaignID){

        timeIntervalDigests = persistentTripStorage.getAllFullTripDigestsForTimeIntervalObjects(initTS);

        //todo apply to carbon footprint percentage too...

        StatsFromTimeIntervalStruct commStats;

        String communityName;

        switch (modeUserOrCommunity){

            case keys.COUNTRY_MODE:
                commStats = UserStatsManager.getInstance().getCountryStats(currentIntervalKey);
                break;

            case keys.CAMPAIGN_MODE:
                commStats = UserStatsManager.getInstance().getCampaignStats(currentIntervalKey, campaignID);
                break;
            default:
            case keys.CITY_REGION_MODE:
                commStats = UserStatsManager.getInstance().getCityStats(currentIntervalKey);
                break;

        }

        if (commStats == null){

            Toast.makeText(getContext(), getString(R.string.Unable_Show_Stats), Toast.LENGTH_LONG).show();
            return;

        }

        switch (modeUserOrCommunity){

            case keys.CAMPAIGN_MODE:
                communityName =  CampaignManager.getInstance(getContext()).getCampaignName(commStats.getName());
                if (communityName == null){
                    communityName = commStats.getName();
                }
                break;
            default:
                communityName = commStats.getName();
                break;

        }

        Log.e("DashboardFragment", "#Digests from this time interval: " + timeIntervalDigests.size());

        DistanceTimeCaloriesActivities values = getDistanceTimeCaloriesActivitiesData(timeIntervalDigests);

        long totalTimeTraveled = values.getTotalTimeTraveled();
        long totalDistanceTraveled = values.getTotalDistanceTraveled();
        long caloriesUser = values.getCalories();
        HashMap<String, Double> activityTimeCounters = values.getActivityTimeCounters();

        activitiesLinearLayout.removeAllViews();

        drawUserTripActivityData(activityTimeCounters, inflater);

        long communityDistanceKm;

        try {
            communityDistanceKm = NumbersUtil.roundNoDecimalPlace(commStats.getTotalDistance()/1000.0/commStats.getTotalUsers());
        }catch (Exception e){
            communityDistanceKm = 0;
        }

        long communityDurationMinutes;

        try{
            communityDurationMinutes = TimeUnit.MILLISECONDS.toMinutes(commStats.getTotalDuration()/commStats.getTotalUsers());
        }catch(Exception e){
            communityDurationMinutes = 0;
        }

        long userDistanceKm = NumbersUtil.roundNoDecimalPlace(totalDistanceTraveled/1000.0/getNumberOfDaysOfKey(currentIntervalKey));
        long userDurationMinutes = TimeUnit.MILLISECONDS.toMinutes(totalTimeTraveled/getNumberOfDaysOfKey(currentIntervalKey));

        String distance;

        if (communityDistanceKm >= userDistanceKm){
            distance = getString(R.string.Number_Km_More, (communityDistanceKm-userDistanceKm));
        }else{
            distance = getString(R.string.Number_Km_Less, (userDistanceKm-communityDistanceKm));
        }

        String minutes;

        if (communityDurationMinutes >= userDurationMinutes){
            minutes = getString(R.string.Number_Min_More, (communityDurationMinutes-userDurationMinutes));
        }else{
            minutes = getString(R.string.Number_Min_Less, (userDurationMinutes-communityDurationMinutes));
        }

        String[] toBeBolded = {minutes, distance, communityName};

        youHaveSpentTextView.setText(MiscUtils.emboldenKeywords(getString(R.string.People_From_Place_Have_Spent_On_Average, communityName, minutes, distance), toBeBolded));

        //dummy



        ////////////////////////////////////////////////////////////////////////////////////////////
        // worthwhileness average computation

//        double worthwhilenessScore = 0.0;
//        double productivityScore = 0.0;
//        double enjoymentScore = 0.0;
//        double fitnessScore = 0.0;
//        double totalWorthwhilenessTime = 0.0;
//
//        double co2 = 0.0;
//
//        for (FullTripDigest fullTripDigest : timeIntervalDigests){
//
//            if((fullTripDigest.getTripStats()) != null && (fullTripDigest.getTripStats().getWorthwhilenessScore() != null) && (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessScore() != 0.0)){
//
//                double tripTimeInMinutes = getTimeIntervalInMinutes(fullTripDigest.getEndTimestamp(), fullTripDigest.getInitTimestamp());
//
//                totalWorthwhilenessTime+= tripTimeInMinutes;
//
//                worthwhilenessScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessScore() * tripTimeInMinutes);
//                productivityScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessProductivity() * tripTimeInMinutes);
//                enjoymentScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessEnjoyment() * tripTimeInMinutes);
//                fitnessScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessFitness() * tripTimeInMinutes);
//                co2 += fullTripDigest.getTripStats().getC02FootprintValue();
//
//            }
//
//        }


        underlineWorthwhilenessAndSetClickable();

        WorthwhilenessCO2Score worthwhilenessCO2Score = WorthwhilenessCO2Score.computeWorthwhilenessScore(timeIntervalDigests);

        double finalWorthwhilenessScore =  worthwhilenessCO2Score.getWorthwhilenessScore();
        double finalProductivityScore =  worthwhilenessCO2Score.getProductivityScore();
        double finalEnjoymentScore =  worthwhilenessCO2Score.getEnjoymentScore();
        double finalFitnessScore =  worthwhilenessCO2Score.getFitnessScore();

        double co2 = worthwhilenessCO2Score.getCo2();

        yourTripsTextOverallScore.setText(getString(R.string.Your_Trips_Overall_Percentage_Worthwhile, ((int) finalWorthwhilenessScore) + "%"));
        worthwhileScoreTextView.setText((int) finalWorthwhilenessScore + "%");
        productiveScoreTextView.setText((int) finalProductivityScore + "%");
        enjoymentScoreTextView.setText((int) finalEnjoymentScore + "%");
        fitnessScoreTextView.setText((int) finalFitnessScore + "%");

        Log.e("Dashboard", "finalWorthwhilenessScore " + (int) finalWorthwhilenessScore);
        Log.e("Dashboard", "finalProductivityScore " + (int) finalProductivityScore);
        Log.e("Dashboard", "finalEnjoymentScore " + (int) finalEnjoymentScore);
        Log.e("Dashboard", "finalFitnessScore " + (int) finalFitnessScore);


        ////////////////////////////////////////////////////////////////////////////////////////////
        // carbon footprint

//        TODO TEST SHOULD COMMUNITY BE SPLIT BY ONE DAY????
//        long timeIntervalUser = DateTime.now().getMillis() - initTS;

        long dayInMillis = 86400000;

//        long timeIntervalUser = userDaysWithTrips * dayInMillis;
        long timeIntervalUser = DateTime.now().getMillis() - initTS;

        double numIntervalDays = getNumberOfDaysOfKey(currentIntervalKey);

        Log.e("DashboardFragment", "total users" + commStats.getTotalUsers());

        long timeInterval = 86400000;

        Log.e("DashboardFragment", "timeInterval hours" + DateHelper.getHoursFromMillis(timeInterval));

        double carbonFootprintValueUser = getCarbonFootprint(timeIntervalUser, co2);

        String percentageFootprintUser = Math.round(carbonFootprintValueUser*100) + "%";

        double co2Community = 0.0;

        long caloryValueCommunity = 0;

        for (CorrectedModeServer correctedModeServer : commStats.getCorrectedModes()){
            co2Community += ActivityDetected.getCO2ValueForDistanceAndMode(correctedModeServer.getDistance(), correctedModeServer.getMode());
            caloryValueCommunity += ActivityDetected.getCaloriesFromModeAndDistance(correctedModeServer.getMode(), correctedModeServer.getDistance());
        }

        Log.e("DashboardFragment", "sum co2 community " + co2Community);

//        //todo testing gama
//
        double X = 30000;

        double gama;

        try{
            gama = (X* commStats.getTotalUsers())/commStats.getTotalDistance();
        }catch (Exception e){
            gama = 1;
        }

        if (gama < 1){
            gama = 1;
        }


        try{
            //todo kinda reasonable 05/09
            co2Community = co2Community/commStats.getTotalUsers();
            //todo new try 05/09
//            co2Community = co2Community/numIntervalDays;
//            co2Community = co2Community;
            caloryValueCommunity = caloryValueCommunity/commStats.getTotalUsers();
        }catch (Exception e){

            Log.e("DashboardFragment", "exception comm stats");
            co2Community = 0;
            caloryValueCommunity = 0;
        }

        double carbonFootprintValueCommunity = getCarbonFootprint(timeInterval, co2Community);
        String percentageFootprintCommunity = Math.round(carbonFootprintValueCommunity*100*gama) + "%";

        StringBuilder sbCarbonPhrase = new StringBuilder();

        sbCarbonPhrase.append(getString(R.string.And_This_Counted_Carbon_Footprint, percentageFootprintUser));
        sbCarbonPhrase.append(" ");
        sbCarbonPhrase.append(getString(R.string.And_This_Counted_Carbon_Footprint_Users, percentageFootprintCommunity));

        String[] percentageFootprintBolded = {percentageFootprintUser, percentageFootprintCommunity};

        footprintTextView.setText(MiscUtils.emboldenKeywords(sbCarbonPhrase.toString(), percentageFootprintBolded));

        /////////////////////////////////////////////
        // FILL calories values

        String caloryValueUserString = caloriesUser + " cal";

        String caloryValueCommunityString = caloryValueCommunity + " cal";

        String[] caloriesStrings = {caloryValueUserString, caloryValueCommunityString};

        StringBuilder sbCaloriesPhrase = new StringBuilder();

        sbCaloriesPhrase.append(getString(R.string.In_Total_You_Spent_Calories, caloryValueUserString));
        sbCaloriesPhrase.append(" ");
        sbCaloriesPhrase.append(getString(R.string.In_Total_Users_Spent_Calories, caloryValueCommunityString));

        inTotalSpentCaloriesTextView.setText(MiscUtils.emboldenKeywords(sbCaloriesPhrase.toString(), caloriesStrings));



    }


    /**
     * computes distance, time, calories and activities done numbers for the provided
     * timeIntervalDigests
     *
     * @param timeIntervalDigests
     * @return
     */
    private DistanceTimeCaloriesActivities getDistanceTimeCaloriesActivitiesData(ArrayList<FullTripDigest> timeIntervalDigests){

        HashMap<String, Double> activityTimeCounters = new HashMap<>();

        long totalTimeTraveled = 0;
        long totalDistanceTraveled = 0;
        long calories = 0;

        for (FullTripDigest fullTripDigest : timeIntervalDigests){

            if(fullTripDigest.getTripStats() != null){
                //time, distance, calories
                totalTimeTraveled += (fullTripDigest.getEndTimestamp() - fullTripDigest.getInitTimestamp());
                totalDistanceTraveled += (fullTripDigest.getDistanceTraveled());
                calories+=fullTripDigest.getTripStats().getCaloriesSpentValue();

                //activity time counter
                for(ActivityLeg activityLeg : fullTripDigest.getTripStats().getActivityTimeCounter().getLegActivities()){

                    String activityKey = activityLeg.getTextCode();
                    double timeOfActivity = getTimeMinutes((fullTripDigest.getEndTimestamp() - fullTripDigest.getInitTimestamp()) / fullTripDigest.getTripStats().getActivityTimeCounter().getLegActivities().size());

                    if(activityTimeCounters.containsKey(activityKey)){
                        Double prevTime = activityTimeCounters.get(activityKey);
                        activityTimeCounters.put(activityKey, prevTime + timeOfActivity);

                    }else{
                        activityTimeCounters.put(activityKey, timeOfActivity);
                    }
                }

            }
        }

        return new DistanceTimeCaloriesActivities(totalTimeTraveled, totalDistanceTraveled, calories, activityTimeCounters);

    }

    /**
     * retrieve trip digests for the initial timestamp specified in initTS and redraw stats
     *
     * @param initTS
     * @param inflater
     */
    private void getDigestsAndRedraw(long initTS, LayoutInflater inflater){

        timeIntervalDigests = persistentTripStorage.getAllFullTripDigestsForTimeIntervalObjects(initTS);

        Log.e("DashboardFragment", "#Digests from this time interval: " + timeIntervalDigests.size());

        DistanceTimeCaloriesActivities values = getDistanceTimeCaloriesActivitiesData(timeIntervalDigests);

        HashMap<String, Double> activityTimeCounters = values.getActivityTimeCounters();
        long totalTimeTraveled = values.getTotalTimeTraveled();
        long totalDistanceTraveled = values.getTotalDistanceTraveled();
        long calories = values.getCalories();

        activitiesLinearLayout.removeAllViews();

        drawUserTripActivityData(activityTimeCounters, inflater);

        String minutes = TimeUnit.MILLISECONDS.toMinutes(totalTimeTraveled) + " min";
        String distance = (totalDistanceTraveled/1000) + " km";

        String[] toBeBolded = {minutes, distance, city};

        youHaveSpentTextView.setText(MiscUtils.emboldenKeywords(getString(R.string.You_Have_Spent_Complete, minutes, distance, city), toBeBolded));

        String caloryValue = calories + " cal";
        String[] caloriesString = {caloryValue};

        inTotalSpentCaloriesTextView.setText(MiscUtils.emboldenKeywords(getString(R.string.In_Total_You_Spent_Calories, caloryValue), caloriesString));

        ////////////////////////////////////////////////////////////////////////////////////////////
        // worthwhileness average computation

//        double worthwhilenessScore = 0.0;
//        double productivityScore = 0.0;
//        double enjoymentScore = 0.0;
//        double fitnessScore = 0.0;
//        double totalWorthwhilenessTime = 0.0;
//
//        double co2 = 0.0;
//
//        for (FullTripDigest fullTripDigest : timeIntervalDigests){
//
//            if((fullTripDigest.getTripStats()) != null && (fullTripDigest.getTripStats().getWorthwhilenessScore() != null) && (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessScore() != 0.0)){
//
//                double tripTimeInMinutes = getTimeIntervalInMinutes(fullTripDigest.getEndTimestamp(), fullTripDigest.getInitTimestamp());
//
//                totalWorthwhilenessTime+= tripTimeInMinutes;
//
//                worthwhilenessScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessScore() * tripTimeInMinutes);
//                productivityScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessProductivity() * tripTimeInMinutes);
//                enjoymentScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessEnjoyment() * tripTimeInMinutes);
//                fitnessScore+= (fullTripDigest.getTripStats().getWorthwhilenessScore().getWorthwhilenessFitness() * tripTimeInMinutes);
//                co2 += fullTripDigest.getTripStats().getC02FootprintValue();
//
//            }
//
//        }



//        removeUnderlineWorthwhileness();
//        underlineWorthwhilenessAndSetClickable();

        removeUnderlineAndSetUnclickable();

        WorthwhilenessCO2Score worthwhilenessCO2Score = WorthwhilenessCO2Score.computeWorthwhilenessScore(timeIntervalDigests);

        double finalWorthwhilenessScore =  worthwhilenessCO2Score.getWorthwhilenessScore();
        double finalProductivityScore =  worthwhilenessCO2Score.getProductivityScore();
        double finalEnjoymentScore =  worthwhilenessCO2Score.getEnjoymentScore();
        double finalFitnessScore =  worthwhilenessCO2Score.getFitnessScore();

        double co2 = worthwhilenessCO2Score.getCo2();

        yourTripsTextOverallScore.setText(getString(R.string.Your_Trips_Overall_Percentage_Worthwhile, ((int) finalWorthwhilenessScore) + "%"));
        worthwhileScoreTextView.setText((int) finalWorthwhilenessScore + "%");
        productiveScoreTextView.setText((int) finalProductivityScore + "%");
        enjoymentScoreTextView.setText((int) finalEnjoymentScore + "%");
        fitnessScoreTextView.setText((int) finalFitnessScore + "%");

        Log.e("Dashboard", "finalWorthwhilenessScore " + (int) finalWorthwhilenessScore);
        Log.e("Dashboard", "finalProductivityScore " + (int) finalProductivityScore);
        Log.e("Dashboard", "finalEnjoymentScore " + (int) finalEnjoymentScore);
        Log.e("Dashboard", "finalFitnessScore " + (int) finalFitnessScore);


        ////////////////////////////////////////////////////////////////////////////////////////////
        // carbon footprint

        long timeInterval = DateTime.now().getMillis() - initTS;

//        getFootprintPercentage(timeInterval);

        double carbonFootprintValue = getCarbonFootprint(timeInterval, co2);

        String percentageFootprint = Math.round(carbonFootprintValue*100) + "%";
        String[] percentageFootprintBolded = {percentageFootprint};

        footprintTextView.setText(MiscUtils.emboldenKeywords(getString(R.string.And_This_Counted_Carbon_Footprint, percentageFootprint), percentageFootprintBolded));


    }

    public void underlineWorthwhilenessAndSetClickable(){

        worthwhilenessScoreConstraintLayout.setOnClickListener(this);
        worthwhilenessScoreConstraintLayout.setClickable(true);

        fitnessScoreConstraintLayout.setOnClickListener(this);
        fitnessScoreConstraintLayout.setClickable(true);

        enjoymentScoreConstraintLayout.setOnClickListener(this);
        enjoymentScoreConstraintLayout.setClickable(true);

        productivityLabelTextView.setOnClickListener(this);
        productivityLabelTextView.setPaintFlags(productivityLabelTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        enjoymentLabelTextView.setPaintFlags(productivityLabelTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        fitnessLabelTextView.setPaintFlags(productivityLabelTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        worthwhilenessLabelTextView.setPaintFlags(productivityLabelTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        productivityLabelTextView.setClickable(true);

    }

    public void removeUnderlineAndSetUnclickable(){

        worthwhilenessScoreConstraintLayout.setOnClickListener(null);
        worthwhilenessScoreConstraintLayout.setClickable(false);

        fitnessScoreConstraintLayout.setOnClickListener(null);
        fitnessScoreConstraintLayout.setClickable(false);

        enjoymentScoreConstraintLayout.setOnClickListener(null);
        enjoymentScoreConstraintLayout.setClickable(false);

        productivityLabelTextView.setOnClickListener(null);
        productivityLabelTextView.setClickable(false);

        productivityLabelTextView.setPaintFlags( productivityLabelTextView.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
        enjoymentLabelTextView.setPaintFlags( productivityLabelTextView.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
        fitnessLabelTextView.setPaintFlags( productivityLabelTextView.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
        worthwhilenessLabelTextView.setPaintFlags( productivityLabelTextView.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));

    }

    private void addActivitiesBar(String activityText, int percentage, LinearLayout activitiesLinearLayout, LayoutInflater inflater) {

        ConstraintLayout activityIndividualLayout = (ConstraintLayout) inflater.inflate(R.layout.dashboard_activity_bar_layout, null, false);

        TextView mostDoneActivity = activityIndividualLayout.findViewById(R.id.mostDoneActivityTextView);
        TextView mostDoneActivityPercentageTextView = activityIndividualLayout.findViewById(R.id.mostDoneActivityPercentageTextView);
        SeekBar currentScoreSeekBar2 = activityIndividualLayout.findViewById(R.id.currentScoreSeekBar);

        mostDoneActivity.setText(activityText);
        mostDoneActivityPercentageTextView.setText(percentage + "%");
        currentScoreSeekBar2.setProgress(percentage);

        activitiesLinearLayout.addView(activityIndividualLayout);

    }

    private double getCarbonFootprint(long timeInterval, double value){

        //value in grams per day per person
        double perDayCo2Value = 1370;

//        Log.e("Carbon", "time interval in days" + (timeInterval/(24*60*60*1000.0)));


        double perIntervalTotalValue = (timeInterval/(24*60*60*1000.0))*perDayCo2Value;

        return value/perIntervalTotalValue;
    }

    public static double getTimeIntervalInMinutes(long endTimestamp, long initTimestamp){

        return (endTimestamp - initTimestamp)/60.0/1000.0;

    }

    private double getTimeMinutes(long millis){

        return millis/60.0/1000.0;

    }

    int currentIntervalKey = 1;

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.buttonDays:
                showTimePopup(getContext(), buttonDays);
                /*
                if(timeIntervalOptionsLayout.getVisibility() == View.GONE) {
                    timeIntervalOptionsLayout.setVisibility(View.VISIBLE);
                }else{
                    timeIntervalOptionsLayout.setVisibility(View.GONE);
                }
                */
                break;

            case R.id.tvDay:

                currentIntervalKey = keys.day1;
                buttonDays.setText(getString(R.string.Day));

                if(modeUserOrCommunity == keys.USER_MODE){
                    getDigestsAndRedraw(DateTime.now().minusDays(1).getMillis(), getLayoutInflater());
                }else{
                    getDigestsAndRedrawCommunityStats(DateTime.now().minusDays(1).getMillis(), getLayoutInflater(), modeUserOrCommunity, campaignID);
                }


                popupWindow.dismiss();

                break;
            case R.id.tv3Days:

                currentIntervalKey = keys.day3;

                if(modeUserOrCommunity == keys.USER_MODE){
                    getDigestsAndRedraw(DateTime.now().minusDays(3).getMillis(), getLayoutInflater());
                }else{
                    getDigestsAndRedrawCommunityStats(DateTime.now().minusDays(3).getMillis(), getLayoutInflater(), modeUserOrCommunity, campaignID);
                }

                buttonDays.setText(getString(R.string.Days_3));
                popupWindow.dismiss();

                break;
            case R.id.tvWeek:

                currentIntervalKey = keys.day7;

                if(modeUserOrCommunity == keys.USER_MODE){
                    getDigestsAndRedraw(DateTime.now().minusWeeks(1).getMillis(), getLayoutInflater());
                }else{
                    getDigestsAndRedrawCommunityStats(DateTime.now().minusWeeks(1).getMillis(), getLayoutInflater(), modeUserOrCommunity, campaignID);
                }

                buttonDays.setText(getString(R.string.Week));
                popupWindow.dismiss();

                break;
            case R.id.tvMonth:

                currentIntervalKey = keys.day30;

                if(modeUserOrCommunity == keys.USER_MODE){
                    getDigestsAndRedraw(DateTime.now().minusMonths(1).getMillis(), getLayoutInflater());
                }else{
                    getDigestsAndRedrawCommunityStats(DateTime.now().minusMonths(1).getMillis(), getLayoutInflater(), modeUserOrCommunity, campaignID);
                }

                buttonDays.setText(getString(R.string.Month));
                popupWindow.dismiss();


                break;
            case R.id.tvYear:

                currentIntervalKey = keys.day365;

                if(modeUserOrCommunity == keys.USER_MODE){
                    getDigestsAndRedraw(DateTime.now().minusYears(1).getMillis(), getLayoutInflater());
                }else{
                    getDigestsAndRedrawCommunityStats(DateTime.now().minusYears(1).getMillis(), getLayoutInflater(), modeUserOrCommunity, campaignID);
                }

                buttonDays.setText(getString(R.string.Year));
                popupWindow.dismiss();

                break;
//            case R.id.tvEra:
//
//                currentIntervalKey = keys.ever;
//
//                buttonDays.setText(getString(R.string.Era));
//                popupWindow.dismiss();
//
//                break;

            case R.id.distanceStatsButton:

                goToShowStats(keys.DISTANCE);

                break;

            case R.id.timeStatsButton:

                goToShowStats(keys.TIME);

                break;

            case R.id.co2StatsButton:

                goToShowStats(keys.C02);

                break;

            case R.id.caloriesStatsButton:

                goToShowStats(keys.CALORIES);

                break;

            case R.id.buttonYou:
                showUserCommunityPopup(v);
                break;

            case R.id.infoWorthwhilenessButton:

                PopupUtil.showBubblePopup(getContext(),  worthwhilenessOverallLayout, getString(R.string.Dashboard_Worthwhileness_Popup_Text));

                break;

            case R.id.infoCarbonFootprintButton:
                showWhatIsCarbonBudgetPopup();
                break;

            case R.id.worthwhilenessScoreConstraintLayout:
                goToShowWorthwhilenessCommunityStats(WorthwhilenessStatsActivity.keys.WORTHWHILENESS);
                break;

            case R.id.fitnessScoreConstraintLayout:
                goToShowWorthwhilenessCommunityStats(WorthwhilenessStatsActivity.keys.FITNESS);
                break;

            case R.id.enjoymentScoreConstraintLayout:
                goToShowWorthwhilenessCommunityStats(WorthwhilenessStatsActivity.keys.ENJOYMENT);
                break;

            case R.id.productivityLabelTextView:
                goToShowWorthwhilenessCommunityStats(WorthwhilenessStatsActivity.keys.PRODUCTIVITY);
                break;
        }

    }

    private void goToShowStats(int MODE) {

        switch (modeUserOrCommunity){

            case keys.USER_MODE:

                switch(MODE){
                    case keys.DISTANCE:
                        goToShowUserStats(timeIntervalDigests, UserStatsActivity.keys.DISTANCE_TRAVELED_USER_MODE);
                        break;
                    case keys.TIME:
                        goToShowUserStats(timeIntervalDigests, UserStatsActivity.keys.TIME_TRAVELED_USER_MODE);
                        break;
                    case keys.C02:
                        goToShowUserStats(timeIntervalDigests, UserStatsActivity.keys.CO2_USER_MODE);
                        break;
                    case keys.CALORIES:
                        goToShowUserStats(timeIntervalDigests, UserStatsActivity.keys.CALORIES_USER_MODE);
                        break;
                }

                break;
            //default means community
            default:

                switch (MODE){

                        case keys.DISTANCE:
                            goToShowCommunityStats(timeIntervalDigests, UserStatsActivity.keys.DISTANCE_TRAVELED_COMMUNITY_MODE);
                            break;
                        case keys.TIME:
                            goToShowCommunityStats(timeIntervalDigests, UserStatsActivity.keys.TIME_TRAVELED_COMMUNITY_MODE);
                            break;
                        case keys.C02:
                            goToShowCommunityStats(timeIntervalDigests, UserStatsActivity.keys.CO2_COMMUNITY_MODE);
                            break;
                        case keys.CALORIES:
                            goToShowCommunityStats(timeIntervalDigests, UserStatsActivity.keys.CALORIES_COMMUNITY_MODE);
                            break;


                }

        }

    }

    private void goToShowWorthwhilenessCommunityStats(int keyProdFitEnjoyWorthwhileness){

        StatsFromTimeIntervalStruct commStats;

        switch (modeUserOrCommunity){

            case keys.COUNTRY_MODE:
                commStats = UserStatsManager.getInstance().getCountryStats(currentIntervalKey);
                break;

            case keys.CAMPAIGN_MODE:
                commStats = UserStatsManager.getInstance().getCampaignStats(currentIntervalKey, campaignID);
                break;
            default:
            case keys.CITY_REGION_MODE:
                commStats = UserStatsManager.getInstance().getCityStats(currentIntervalKey);
                break;

        }

        if (commStats == null){

            Toast.makeText(getContext(), getString(R.string.Unable_Show_Stats), Toast.LENGTH_LONG).show();
            return;

        }

        try{

            Intent intent = new Intent(getContext(), WorthwhilenessStatsActivity.class);
            intent.putExtra(WorthwhilenessStatsActivity.keys.modeToDrawIntentKey, keyProdFitEnjoyWorthwhileness);

            StatsFromTimeIntervalStructForUser userStats = UserStatsManager.getInstance().getUserStats(currentIntervalKey);

            switch (keyProdFitEnjoyWorthwhileness){
                case WorthwhilenessStatsActivity.keys.WORTHWHILENESS:
                    intent.putExtra(WorthwhilenessStatsActivity.keys.arrayStatsIntentKey, getCommunityWorthwhilenessStats(commStats, userStats));
                    break;

                case WorthwhilenessStatsActivity.keys.PRODUCTIVITY:
                    intent.putExtra(WorthwhilenessStatsActivity.keys.arrayStatsIntentKey, getCommunityWorthwhilenessProdEnjoyFitStats(commStats,ValueFromTripTotalServer.keys.PRODUCTIVITY, userStats));
                    break;

                case WorthwhilenessStatsActivity.keys.FITNESS:
                    intent.putExtra(WorthwhilenessStatsActivity.keys.arrayStatsIntentKey, getCommunityWorthwhilenessProdEnjoyFitStats(commStats,ValueFromTripTotalServer.keys.FITNESS, userStats));
                    break;

                case WorthwhilenessStatsActivity.keys.ENJOYMENT:
                    intent.putExtra(WorthwhilenessStatsActivity.keys.arrayStatsIntentKey, getCommunityWorthwhilenessProdEnjoyFitStats(commStats,ValueFromTripTotalServer.keys.ENJOYMENT, userStats));
                    break;
            }

            startActivity(intent);

        }catch (Exception e){

            Toast.makeText(getContext(), "Failed to get community stats", Toast.LENGTH_LONG).show();

        }

    }

    private void goToShowCommunityStats(ArrayList<FullTripDigest> timeIntervalDigests, int modeUserOrCommDistTimeCalCO2) {

        StatsFromTimeIntervalStruct commStats;

        switch (modeUserOrCommunity){

            case keys.COUNTRY_MODE:
                commStats = UserStatsManager.getInstance().getCountryStats(currentIntervalKey);
                break;

            case keys.CAMPAIGN_MODE:
                commStats = UserStatsManager.getInstance().getCampaignStats(currentIntervalKey, campaignID);
                break;
            default:
            case keys.CITY_REGION_MODE:
                commStats = UserStatsManager.getInstance().getCityStats(currentIntervalKey);
                break;

        }

        if (commStats == null){

            Toast.makeText(getContext(), "Failed to get community stats", Toast.LENGTH_LONG).show();
            return;

        }



        Log.d("--Stats", "name " + commStats.getName());
        Log.d("--stats", "size" + commStats.getCorrectedModes().size());

        try {

            Intent intent = new Intent(getContext(), CommunityStatsActivity.class);
            intent.putExtra(UserStatsActivity.keys.modeToDrawIntentKey, modeUserOrCommDistTimeCalCO2);

            switch (modeUserOrCommDistTimeCalCO2){
                case UserStatsActivity.keys.TIME_TRAVELED_COMMUNITY_MODE:
                    intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getCommunityStatsHolderTimeTravelled(commStats,timeIntervalDigests, getNumberOfDaysOfKey(currentIntervalKey)));
                    break;

                case UserStatsActivity.keys.DISTANCE_TRAVELED_COMMUNITY_MODE:
                    intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getCommunityStatsHolderDistanceTravelled(commStats,timeIntervalDigests, getNumberOfDaysOfKey(currentIntervalKey)));
                    break;

                case UserStatsActivity.keys.CO2_COMMUNITY_MODE:
                    intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getCommunityStatsHolderCO2(commStats,timeIntervalDigests, getNumberOfDaysOfKey(currentIntervalKey)));
                    break;

                case UserStatsActivity.keys.CALORIES_COMMUNITY_MODE:
                    intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getCommunityStatsHolderCalories(commStats,timeIntervalDigests, getNumberOfDaysOfKey(currentIntervalKey)));
                    break;
            }

            startActivity(intent);


        }catch (Exception e){

            Toast.makeText(getContext(), getString(R.string.Unable_Show_Stats), Toast.LENGTH_LONG).show();

        }


    }

    private void goToShowUserStats(ArrayList<FullTripDigest> timeIntervalDigests, int mode) {

        Intent intent = new Intent(getContext(), UserStatsActivity.class);
        intent.putExtra(UserStatsActivity.keys.modeToDrawIntentKey, mode);

        switch (mode){
            case UserStatsActivity.keys.TIME_TRAVELED_USER_MODE:
                intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getUserStatsHolderTimeTravelled(timeIntervalDigests));
                break;

            case UserStatsActivity.keys.DISTANCE_TRAVELED_USER_MODE:
                intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getUserStatsHolderDistanceTravelled(timeIntervalDigests));
                break;

            case UserStatsActivity.keys.CO2_USER_MODE:
                intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getUserStatsHolderCO2(timeIntervalDigests));
                break;

            case UserStatsActivity.keys.CALORIES_USER_MODE:
                intent.putExtra(UserStatsActivity.keys.arrayStatsIntentKey, getUserStatsHolderCalories(timeIntervalDigests));
                break;
        }

        startActivity(intent);

    }


    public void showTimePopup(Context context, View viewToDrawPopupAbove){

        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.dashboard_options_popup, null);

        TextView tvday = layout.findViewById(R.id.tvDay);
        tvday.setOnClickListener(this);
        TextView tv3Days = layout.findViewById(R.id.tv3Days);
        tv3Days.setOnClickListener(this);
        TextView tvWeek = layout.findViewById(R.id.tvWeek);
        tvWeek.setOnClickListener(this);
        TextView tvMonth = layout.findViewById(R.id.tvMonth);
        tvMonth.setOnClickListener(this);
        TextView tvYear = layout.findViewById(R.id.tvYear);
        tvYear.setOnClickListener(this);
//        TextView tvEra = layout.findViewById(R.id.tvEra);
//        tvEra.setOnClickListener(this);

        viewToDrawPopupAbove.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        popupWindow = new PopupWindow(layout, viewToDrawPopupAbove.getMeasuredWidth(), height);

        int[] location = new int[2];
        viewToDrawPopupAbove.getLocationInWindow(location);

        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(viewToDrawPopupAbove, Gravity.NO_GRAVITY, location[0], location[1] + viewToDrawPopupAbove.getMeasuredHeight());
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

    public ArrayList<WorthwhilenssStatsHolder> getCommunityWorthwhilenessStats(StatsFromTimeIntervalStruct serverStats, StatsFromTimeIntervalStructForUser userStats){

        ArrayList<WorthwhilenssStatsHolder> result = new ArrayList<>();

        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            if (correctedModeServer.getMode() != -1){
                int percentageCommunity = NumbersUtil.roundNoDecimalPlace((correctedModeServer.getWastedTimeWSum() * 100.0) / (correctedModeServer.getDuration() * 5));
                result.add(new WorthwhilenssStatsHolder(0,percentageCommunity, correctedModeServer.getMode()));
            }

        }

        if(userStats != null){

            for (CorrectedModeServer userCorrectedModeServer : userStats.getCorrectedModes()){

                if (userCorrectedModeServer.getMode() != -1){

                    WorthwhilenssStatsHolder ifExists = WorthwhilenssStatsHolder.getStatsForMode(result, userCorrectedModeServer.getMode());

                    int percentageUser = NumbersUtil.roundNoDecimalPlace((userCorrectedModeServer.getWastedTimeWSum() * 100.0) / (userCorrectedModeServer.getDuration() * 5));

                    if (ifExists == null){
                        result.add(new WorthwhilenssStatsHolder(percentageUser,0, userCorrectedModeServer.getMode()));
                    }else{
                        ifExists.setUserPercentage(percentageUser);
                    }

                }

            }

        }

        return result;

    }

    public ArrayList<WorthwhilenssStatsHolder> getCommunityWorthwhilenessProdEnjoyFitStats(StatsFromTimeIntervalStruct serverStats, int keyProdEnjoyFit, StatsFromTimeIntervalStructForUser userStats){

        ArrayList<WorthwhilenssStatsHolder> result = new ArrayList<>();

        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            if (correctedModeServer.getMode() != -1){

                long worthElementValue = 0;

                for(ValueFromTripTotalServer valueFromTripModeSum : correctedModeServer.getValueFromTripModeWSum()){
                    if(valueFromTripModeSum.getCode() == keyProdEnjoyFit){
                        worthElementValue = valueFromTripModeSum.getValue();
                    }
                }

                int percentageCommunity = NumbersUtil.roundNoDecimalPlace((worthElementValue * 100.0) / (correctedModeServer.getDuration() * 2));
                result.add(new WorthwhilenssStatsHolder(0,percentageCommunity, correctedModeServer.getMode()));
            }

        }

        if(userStats != null){

            for (CorrectedModeServer userModeServer : userStats.getCorrectedModes()){

                if (userModeServer.getMode() != -1){

                    long worthElementValue = 0;

                    for(ValueFromTripTotalServer valueFromTripModeSum : userModeServer.getValueFromTripModeWSum()){
                        if(valueFromTripModeSum.getCode() == keyProdEnjoyFit){
                            worthElementValue = valueFromTripModeSum.getValue();
                        }
                    }

                    int percentageUser = NumbersUtil.roundNoDecimalPlace((worthElementValue * 100.0) / (userModeServer.getDuration() * 2));

                    WorthwhilenssStatsHolder ifExists = WorthwhilenssStatsHolder.getStatsForMode(result, userModeServer.getMode());

                    if (ifExists == null){
                        result.add(new WorthwhilenssStatsHolder(percentageUser,0, userModeServer.getMode()));
                    }else{
                        ifExists.setUserPercentage(percentageUser);
                    }

                }

            }

        }

        return result;

    }

    public ArrayList<UserStatsHolder> getUserStatsHolderDistanceTravelled(ArrayList<FullTripDigest> digests){

        long totalDistance = 0;

        ArrayList<ModalityTimeDistanceCounter> modalityTimeDistanceCounters = new ArrayList<>();

        for (FullTripDigest digest : digests){
//            Log.d("DASHBOARD_BUG", "digest count = " + digests.size());
            if (digest.getTripStats() != null && digest.getTripStats().getModalityTimeDistanceCounters() != null) {

                for (ModalityTimeDistanceCounter modalityTimeDistanceCounter : digest.getTripStats().getModalityTimeDistanceCounters()) {

                    if(modalityTimeDistanceCounter.getModality() != -1) {
//                        Log.d("DASHBOARD_BUG", "distance to add = " + modalityTimeDistanceCounter.getDistanceCounter());
                        ModalityTimeDistanceCounter.addModalityTimeDistance(modalityTimeDistanceCounters, modalityTimeDistanceCounter);
                        totalDistance += modalityTimeDistanceCounter.getDistanceCounter();
                    }
                }
            }
        }

        ArrayList<UserStatsHolder> statsHolders = new ArrayList<>();

        for (ModalityTimeDistanceCounter modalityTimeDistanceCounter : modalityTimeDistanceCounters){
            double percentage = NumbersUtil.roundNoDecimalPlace(modalityTimeDistanceCounter.getDistanceCounter()/totalDistance*100.0);
            double distance = NumbersUtil.roundToOneDecimalPlace(modalityTimeDistanceCounter.getDistanceCounter()/1000.0);

            statsHolders.add(new UserStatsHolder(distance, (int) percentage, modalityTimeDistanceCounter.getModality()));
        }

        return statsHolders;

    }

    public ArrayList<UserStatsHolder> getUserStatsHolderTimeTravelled(ArrayList<FullTripDigest> digests){

        double totalTime = 0.0;

        ArrayList<ModalityTimeDistanceCounter> modalityTimeDistanceCounters = new ArrayList<>();

//        Log.e("UserStatsActivity", "digests size " + digests.size());

        for (FullTripDigest digest : digests){
            if (digest.getTripStats() != null && digest.getTripStats().getModalityTimeDistanceCounters() != null) {
                for (ModalityTimeDistanceCounter modalityTimeDistanceCounter : digest.getTripStats().getModalityTimeDistanceCounters()) {

                    if(modalityTimeDistanceCounter.getModality() != -1) {
                        ModalityTimeDistanceCounter.addModalityTimeDistance(modalityTimeDistanceCounters, modalityTimeDistanceCounter);
                        totalTime += modalityTimeDistanceCounter.getTimeCounter();
//                        Log.e("UserStatsActivity", "counter " + modalityTimeDistanceCounter.getTimeCounter());
                    }

                }
            }
        }

        Log.e("UserStatsActivity", "time " + totalTime);

        ArrayList<UserStatsHolder> statsHolders = new ArrayList<>();

        for (ModalityTimeDistanceCounter modalityTimeDistanceCounter : modalityTimeDistanceCounters){

            Log.e("UserStatsActivity", "time " + modalityTimeDistanceCounter.getTimeCounter());
            Log.e("UserStatsActivity", "modality " + modalityTimeDistanceCounter.getModality());

            int percentage = NumbersUtil.roundNoDecimalPlace((modalityTimeDistanceCounter.getTimeCounter()/totalTime)*100.0);

            double time = NumbersUtil.roundToOneDecimalPlace(modalityTimeDistanceCounter.getTimeCounter()/60.0);

            statsHolders.add(new UserStatsHolder(time,percentage, modalityTimeDistanceCounter.getModality()));
        }

        return statsHolders;

    }

    public ArrayList<UserStatsHolder> getUserStatsHolderCalories(ArrayList<FullTripDigest> digests){

        double totalCalories = 0;

        ArrayList<CaloriesModeCounter> caloriesModeCounters = new ArrayList<>();

        Log.e("UserStatsActivity", "digests size " + digests.size());

        for (FullTripDigest digest : digests){
            if (digest.getTripStats() != null && digest.getTripStats().getModalityTimeDistanceCounters() != null) {
                for (ModalityTimeDistanceCounter modalityTimeDistanceCounter : digest.getTripStats().getModalityTimeDistanceCounters()) {

                    if(modalityTimeDistanceCounter.getModality() != -1) {
                        double tripCaloriesMode = ActivityDetected.getCaloriesFromModeAndDistance(modalityTimeDistanceCounter.getModality(), modalityTimeDistanceCounter.getDistanceCounter());
                        CaloriesModeCounter.addModalityCalories(caloriesModeCounters, new CaloriesModeCounter(tripCaloriesMode, modalityTimeDistanceCounter.getModality()));
                        totalCalories += tripCaloriesMode;
                    }

                }
            }
        }


        ArrayList<UserStatsHolder> statsHolders = new ArrayList<>();

        for (CaloriesModeCounter caloriesModeCounter : caloriesModeCounters){

            if(caloriesModeCounter.getCaloriesCounter() > 0) {
                int percentage = NumbersUtil.roundNoDecimalPlace((caloriesModeCounter.getCaloriesCounter() / totalCalories) * 100.0);
                double caloriesForMode = NumbersUtil.roundToOneDecimalPlace(caloriesModeCounter.getCaloriesCounter());

                statsHolders.add(new UserStatsHolder(caloriesForMode, percentage, caloriesModeCounter.getModality()));
            }
        }

        return statsHolders;

    }

    public ArrayList<UserStatsHolder> getUserStatsHolderCO2(ArrayList<FullTripDigest> digests){

        double totalCO2 = 0.0;

        ArrayList<CO2ModeCounter> CO2ModeCounters = new ArrayList<>();

        Log.e("UserStatsActivity", "digests size " + digests.size());

        for (FullTripDigest digest : digests){
            if (digest.getTripStats() != null && digest.getTripStats().getModalityTimeDistanceCounters() != null) {
                for (ModalityTimeDistanceCounter modalityTimeDistanceCounter : digest.getTripStats().getModalityTimeDistanceCounters()) {

                    if(modalityTimeDistanceCounter.getModality() != -1) {
                        double tripCO2Mode = ActivityDetected.getCO2ValueForDistanceAndMode(modalityTimeDistanceCounter.getDistanceCounter(), modalityTimeDistanceCounter.getModality());
                        CO2ModeCounter.addModalityCO2(CO2ModeCounters, new CO2ModeCounter(tripCO2Mode, modalityTimeDistanceCounter.getModality()));
                        totalCO2 += tripCO2Mode;
                    }

                }
            }
        }


        ArrayList<UserStatsHolder> statsHolders = new ArrayList<>();

        for (CO2ModeCounter co2ModeCounter : CO2ModeCounters){
            int percentage = NumbersUtil.roundNoDecimalPlace((co2ModeCounter.getCo2Counter() / totalCO2) * 100.0);
            double caloriesForMode = NumbersUtil.roundToOneDecimalPlace(co2ModeCounter.getCo2Counter()/1000.0);

            statsHolders.add(new UserStatsHolder(caloriesForMode, percentage, co2ModeCounter.getModality()));
        }


        return statsHolders;

    }

    //COMMUNITY

    public ArrayList<CommunityStatsHolder> getCommunityStatsHolderTimeTravelled(StatsFromTimeIntervalStruct serverStats, ArrayList<FullTripDigest> digests, int numberOfDays){

        ArrayList<CommunityStatsHolder> result = new ArrayList<>();

        ArrayList<UserStatsHolder> userStatsHolders = getUserStatsHolderTimeTravelled(digests);

        long totalCommunityDuration = serverStats.getTotalDuration();


        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            Log.e("DashboardFragment", "MODE " + correctedModeServer.getMode() + " TIME " + correctedModeServer.getDuration() + " DISTANCE " + correctedModeServer.getDistance());

        }

        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            UserStatsHolder userStatsForMode = UserStatsHolder.getStatsForMode(userStatsHolders, correctedModeServer.getMode());

            if(userStatsForMode == null){

                result.add(new CommunityStatsHolder(0,
                        NumbersUtil.roundNoDecimalPlace(correctedModeServer.getDuration()*100.0/totalCommunityDuration),
                        0,
//                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDuration()/numberOfDays)/1000.0/60.0),
                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDuration()/serverStats.getTotalUsers())/1000.0/60.0),
                        correctedModeServer.getMode()));

            }else{

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        NumbersUtil.roundNoDecimalPlace(correctedModeServer.getDuration()*100.0/totalCommunityDuration),
                        NumbersUtil.roundToOneDecimalPlace(userStatsForMode.getParam1()/numberOfDays),
//                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDuration()/numberOfDays)/1000.0/60.0),
                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDuration()/serverStats.getTotalUsers())/1000.0/60.0),
                        correctedModeServer.getMode()));



            }

        }

        for(UserStatsHolder userStatsForMode : userStatsHolders){

            if((!CommunityStatsHolder.isModeOnArray(userStatsForMode.getMode(), result)) && (userStatsForMode.getMode() != -1)){

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        0,
                        NumbersUtil.roundToOneDecimalPlace(userStatsForMode.getParam1()/numberOfDays),
                        0,
                        userStatsForMode.getMode()));

            }

        }

        return result;

    }

    public ArrayList<CommunityStatsHolder> getCommunityStatsHolderDistanceTravelled(StatsFromTimeIntervalStruct serverStats, ArrayList<FullTripDigest> digests, int numberOfDays){

        ArrayList<CommunityStatsHolder> result = new ArrayList<>();

        ArrayList<UserStatsHolder> userStatsHolders = getUserStatsHolderDistanceTravelled(digests);

        double totalCommunityDistance = serverStats.getTotalDistance();

        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            Log.e("DashboardFragment", "MODE " + correctedModeServer.getMode() + " TIME " + correctedModeServer.getDuration() + " DISTANCE " + correctedModeServer.getDistance());

        }

        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            UserStatsHolder userStatsForMode = UserStatsHolder.getStatsForMode(userStatsHolders, correctedModeServer.getMode());

            if(userStatsForMode == null){

                result.add(new CommunityStatsHolder(0,
                        NumbersUtil.roundNoDecimalPlace((correctedModeServer.getDistance()/totalCommunityDistance)*100),
                        0,
//                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDistance()/numberOfDays)/1000.0),
                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDistance()/serverStats.getTotalUsers())/1000.0),
                        correctedModeServer.getMode()));

            }else{

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        NumbersUtil.roundNoDecimalPlace((correctedModeServer.getDistance()/totalCommunityDistance)*100),
                        NumbersUtil.roundToOneDecimalPlace(userStatsForMode.getParam1()/numberOfDays),
//                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDistance()/numberOfDays)/1000.0),
                        NumbersUtil.roundToOneDecimalPlace((correctedModeServer.getDistance()/serverStats.getTotalUsers())/1000.0),
                        correctedModeServer.getMode()));

            }

        }

        for(UserStatsHolder userStatsForMode : userStatsHolders){

            if((!CommunityStatsHolder.isModeOnArray(userStatsForMode.getMode(), result)) && (userStatsForMode.getMode() != -1)){

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        0,
                        NumbersUtil.roundToOneDecimalPlace(userStatsForMode.getParam1()/numberOfDays),
                        0,
                        userStatsForMode.getMode()));

            }

        }

        return result;

    }

    public ArrayList<CommunityStatsHolder> getCommunityStatsHolderCalories(StatsFromTimeIntervalStruct serverStats, ArrayList<FullTripDigest> digests, int numberOfDays){

        ArrayList<CommunityStatsHolder> result = new ArrayList<>();

        ArrayList<UserStatsHolder> userStatsHolders = getUserStatsHolderCalories(digests);

        double totalCommunityCalories = 0;

        ArrayList<CaloriesModeCounter> caloriesModeCountersCommunity = new ArrayList<>();

        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            if(correctedModeServer.getMode() != -1) {

                double tripCaloriesMode = ActivityDetected.getCaloriesFromModeAndDistance(correctedModeServer.getMode(), correctedModeServer.getDistance());
                totalCommunityCalories += tripCaloriesMode;
                CaloriesModeCounter.addModalityCalories(caloriesModeCountersCommunity, new CaloriesModeCounter(tripCaloriesMode, correctedModeServer.getMode()));

            }

        }

        for (CaloriesModeCounter caloriesModeCommunity : caloriesModeCountersCommunity){

            UserStatsHolder userStatsForMode = UserStatsHolder.getStatsForMode(userStatsHolders, caloriesModeCommunity.getModality());

            if(userStatsForMode == null){

                result.add(new CommunityStatsHolder(0,
                        NumbersUtil.roundNoDecimalPlace((caloriesModeCommunity.getCaloriesCounter()/totalCommunityCalories)*100),
                        0,
//                        NumbersUtil.roundToOneDecimalPlace( caloriesModeCommunity.getCaloriesCounter()/numberOfDays),
                        NumbersUtil.roundToOneDecimalPlace( caloriesModeCommunity.getCaloriesCounter()/serverStats.getTotalUsers()),
                        caloriesModeCommunity.getModality()));

            }else{

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        NumbersUtil.roundNoDecimalPlace((caloriesModeCommunity.getCaloriesCounter()/totalCommunityCalories)*100),
                        NumbersUtil.roundToOneDecimalPlace( userStatsForMode.getParam1()/numberOfDays),
//                        NumbersUtil.roundToOneDecimalPlace( caloriesModeCommunity.getCaloriesCounter()/numberOfDays),
                        NumbersUtil.roundToOneDecimalPlace( caloriesModeCommunity.getCaloriesCounter()/serverStats.getTotalUsers()),
                        caloriesModeCommunity.getModality()));

            }

        }

        for(UserStatsHolder userStatsForMode : userStatsHolders){

            if((!CommunityStatsHolder.isModeOnArray(userStatsForMode.getMode(), result)) && (userStatsForMode.getMode() != -1)){

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        0,
                        NumbersUtil.roundToOneDecimalPlace( userStatsForMode.getParam1()/numberOfDays),
                        0,
                        userStatsForMode.getMode()));

            }

        }

        return result;

    }

    public ArrayList<CommunityStatsHolder> getCommunityStatsHolderCO2(StatsFromTimeIntervalStruct serverStats, ArrayList<FullTripDigest> digests, int numberOfDays){

        ArrayList<CommunityStatsHolder> result = new ArrayList<>();

        ArrayList<UserStatsHolder> userStatsHolders = getUserStatsHolderCO2(digests);

        double totalCommunityCO2 = 0;

        ArrayList<CO2ModeCounter> co2ModeCountersCommunity = new ArrayList<>();

        for (CorrectedModeServer correctedModeServer : serverStats.getCorrectedModes()){

            if(correctedModeServer.getMode() != -1) {


                    double tripCO2Mode = ActivityDetected.getCO2ValueForDistanceAndMode(correctedModeServer.getDistance()/1000.0, correctedModeServer.getMode());
                    CO2ModeCounter.addModalityCO2(co2ModeCountersCommunity, new CO2ModeCounter(tripCO2Mode, correctedModeServer.getMode()));
                    totalCommunityCO2 += tripCO2Mode;

            }

        }

        for (CO2ModeCounter co2ModeCommunity : co2ModeCountersCommunity){

            UserStatsHolder userStatsForMode = UserStatsHolder.getStatsForMode(userStatsHolders, co2ModeCommunity.getModality());

            if(userStatsForMode == null){

                result.add(new CommunityStatsHolder(0,
                        NumbersUtil.roundNoDecimalPlace((co2ModeCommunity.getCo2Counter()/totalCommunityCO2)*100),
                        0,
//                        NumbersUtil.roundToOneDecimalPlace(co2ModeCommunity.getCo2Counter()/numberOfDays),
                        NumbersUtil.roundToOneDecimalPlace(co2ModeCommunity.getCo2Counter()/serverStats.getTotalUsers()),
                        co2ModeCommunity.getModality()));

            }else{

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        NumbersUtil.roundNoDecimalPlace((co2ModeCommunity.getCo2Counter()/totalCommunityCO2)*100),
                        NumbersUtil.roundToOneDecimalPlace(userStatsForMode.getParam1()/numberOfDays),
//                        NumbersUtil.roundToOneDecimalPlace(co2ModeCommunity.getCo2Counter()/numberOfDays),
                        NumbersUtil.roundToOneDecimalPlace(co2ModeCommunity.getCo2Counter()/serverStats.getTotalUsers()),
                        co2ModeCommunity.getModality()));

            }

        }

        for(UserStatsHolder userStatsForMode : userStatsHolders){

            if((!CommunityStatsHolder.isModeOnArray(userStatsForMode.getMode(), result)) && (userStatsForMode.getMode() != -1)){

                result.add(new CommunityStatsHolder(userStatsForMode.getParam2(),
                        0,
                        NumbersUtil.roundToOneDecimalPlace(userStatsForMode.getParam1()/numberOfDays),
                        0,
                        userStatsForMode.getMode()));

            }

        }

        return result;

    }

    // user/community selection
    public void showUserCommunityPopup(View viewToDrawPopupAbove){

        LinearLayout outterOptionsLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dashboard_outter_options_linear_layout, null, false);

        String country = userSettingStateWrapper.getUserSettings().getCountry();

        String city = userSettingStateWrapper.getUserSettings().getCity();

        ArrayList<Campaign> campaigns = CampaignManager.getInstance(getContext()).getCampaigns();

        drawYouTopOptionSection(outterOptionsLinearLayout);
        drawCityRegionMidOptionSection(outterOptionsLinearLayout, city);
        drawCountryMidOptionSection(outterOptionsLinearLayout, country);
        drawCampaignsMidOptionSection(outterOptionsLinearLayout, campaigns);
//        drawGlobalBottomOptionSection(outterOptionsLinearLayout);

        viewToDrawPopupAbove.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        popupWindow = new PopupWindow(outterOptionsLinearLayout, viewToDrawPopupAbove.getMeasuredWidth(), height);

        int[] location = new int[2];
        viewToDrawPopupAbove.getLocationInWindow(location);

        outterOptionsLinearLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(viewToDrawPopupAbove, Gravity.NO_GRAVITY, location[0], location[1] + viewToDrawPopupAbove.getMeasuredHeight());
    }

    public void drawYouTopOptionSection(LinearLayout outterOptionsLinearLayout){

        ConstraintLayout topModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.top_dashboard_you_community_option_layout, null, false);
        outterOptionsLinearLayout.addView(topModuleLayout);

        topModuleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonUserCommunity.setText(getString(R.string.You));

                getDigestsAndRedraw(DateTime.now().minusDays(getNumberOfDaysOfKey(currentIntervalKey)).getMillis(),  getLayoutInflater());

                modeUserOrCommunity = keys.USER_MODE;
                popupWindow.dismiss();



            }
        });

        //lacks onclick
    }

    public void drawCityRegionMidOptionSection(LinearLayout outterOptionsLinearLayout, final String city){

        //city region

        ConstraintLayout cityRegionModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.mid_dashboard_you_community_option_layout, null, false);
        TextView cityRegionSectionTitleTextView = cityRegionModuleLayout.findViewById(R.id.sectionTitleTextView);
        cityRegionSectionTitleTextView.setText(getString(R.string.City_Region));

        LinearLayout cityRegionLL = cityRegionModuleLayout.findViewById(R.id.innerOptionsLinearLayout);

        ConstraintLayout cityRegionTextOptionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dashboard_inner_option_text_layout, null, false);
        TextView innerOptionTextView = cityRegionTextOptionLayout.findViewById(R.id.optionTextView);
        innerOptionTextView.setText(city);
        cityRegionLL.addView(cityRegionTextOptionLayout);

        outterOptionsLinearLayout.addView(cityRegionModuleLayout);


        cityRegionModuleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonUserCommunity.setText(city);

                modeUserOrCommunity = keys.CITY_REGION_MODE;

                getDigestsAndRedrawCommunityStats(DateTime.now().minusDays(getNumberOfDaysOfKey(currentIntervalKey)).getMillis(),  getLayoutInflater(), modeUserOrCommunity,campaignID);

                popupWindow.dismiss();

            }
        });



        //end city region

        //lacks onclick
    }

    public void drawCountryMidOptionSection(LinearLayout outterOptionsLinearLayout, final String country){

        //init country

        ConstraintLayout countryModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.mid_dashboard_you_community_option_layout, null, false);
        TextView countrySectionTitleTextView = countryModuleLayout.findViewById(R.id.sectionTitleTextView);
        countrySectionTitleTextView.setText(R.string.Country);

        LinearLayout countryLL = countryModuleLayout.findViewById(R.id.innerOptionsLinearLayout);

        ConstraintLayout countryTextOptionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dashboard_inner_option_text_layout, null, false);
        TextView countryInnerOptionTextView = countryTextOptionLayout.findViewById(R.id.optionTextView);
        countryInnerOptionTextView.setText(country);

        countryLL.addView(countryTextOptionLayout);
        outterOptionsLinearLayout.addView(countryModuleLayout);

        countryModuleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonUserCommunity.setText(country);

                modeUserOrCommunity = keys.COUNTRY_MODE;

                getDigestsAndRedrawCommunityStats(DateTime.now().minusDays(getNumberOfDaysOfKey(currentIntervalKey)).getMillis(),  getLayoutInflater(), modeUserOrCommunity,campaignID);

                popupWindow.dismiss();

            }
        });

        //end country

        //lacks onclick
    }

    public void drawCampaignsMidOptionSection(LinearLayout outterOptionsLinearLayout, ArrayList<Campaign> campaignArrayList){

        //init campaigns

        //todo warning now is bottom
//        ConstraintLayout campaignsModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.mid_dashboard_you_community_option_layout, null, false);
        ConstraintLayout campaignsModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.bottom_dashboard_you_community_option_layout, null, false);
        TextView campaignSectionTitleTextView = campaignsModuleLayout.findViewById(R.id.sectionTitleTextView);
        campaignSectionTitleTextView.setText(getString(R.string.Campaigns));

        LinearLayout campaignsLL = campaignsModuleLayout.findViewById(R.id.innerOptionsLinearLayout);

        int i = 0;

        for(final Campaign campaign : campaignArrayList){

            ConstraintLayout campaignTextOptionLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dashboard_inner_option_text_layout, null, false);
            TextView ptCampaignInnerOptionTextView = campaignTextOptionLayout.findViewById(R.id.optionTextView);
            ptCampaignInnerOptionTextView.setText(campaign.getName());

            campaignsLL.addView(campaignTextOptionLayout);

            i++;



            campaignTextOptionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    buttonUserCommunity.setText(campaign.getName());

                    modeUserOrCommunity = keys.CAMPAIGN_MODE;
                    campaignID = campaign.getCampaignID();

                    getDigestsAndRedrawCommunityStats(DateTime.now().minusDays(getNumberOfDaysOfKey(currentIntervalKey)).getMillis(),  getLayoutInflater(), modeUserOrCommunity,campaignID);


                    popupWindow.dismiss();

                }
            });

            if(i>4){
                break;
            }

        }

        outterOptionsLinearLayout.addView(campaignsModuleLayout);

        //end campaigns

        //lacks onclick
    }

    public int getNumberOfDaysOfKey(int key){

        switch (key){

            case keys.day1:
                return 1;
            case keys.day3:
                return 3;
            case keys.day7:
                return 7;
            case keys.day30:
                return 30;
            case keys.day365:
                return 365;
            case keys.ever:
            default:
                return 365;

        }

    }

    public interface keys {

        int USER_MODE = 0;
        int CITY_REGION_MODE = 1;
        int COUNTRY_MODE = 2;
        int CAMPAIGN_MODE = 3;
        int GLOBAL_MODE = 4;

        int TIME = 0;
        int DISTANCE = 1;
        int C02 = 2;
        int CALORIES = 3;

        int day1 = 0;
        int day3 = 1;
        int day7 = 2;
        int day30 = 3;
        int day365 = 4;
        int ever = 5;

    }

}

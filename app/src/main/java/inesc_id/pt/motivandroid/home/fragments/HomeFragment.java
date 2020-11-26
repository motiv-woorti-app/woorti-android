package inesc_id.pt.motivandroid.home.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.WorthwhilenessCO2Score;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.data.rewards.RewardStatus;
import inesc_id.pt.motivandroid.data.stories.Story;
import inesc_id.pt.motivandroid.data.stories.StoryStateful;
import inesc_id.pt.motivandroid.data.surveys.SurveyStateful;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.home.activities.HomeDrawerActivity;
import inesc_id.pt.motivandroid.home.surveys.SurveyTestActivity;
import inesc_id.pt.motivandroid.mobilityCoach.activities.StoryActivity;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.surveyNotification.SurveyManager;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.NumbersUtil;

import static org.joda.time.DateTimeZone.UTC;

/**
 *
 * HomeFragment
 *
 * Presents the user with:
 *  - user trip data (total trips completed, total unique days with trips and points).
 *  - list of still unanswered surveys
 *  - warning that there trips to be validated
 *  - list of new stories
 *  - last 7 days travel report
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
 *
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {


    }


    ArrayList<SurveyStateful> triggeredSurveys;
    ArrayList<Story> storiesData;

    private boolean checkIfPendingTrips(Context context) {

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        for(FullTripDigest digest : persistentTripStorage.getAllFullTripDigestsByUserIDObjects(FirebaseAuth.getInstance().getUid())){
            if (!digest.isValidated()) return true;
        }

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getContext());
        triggeredSurveys = persistentTripStorage.getAllTriggeredSurveysObject();

        storiesData = Story.getDummmyStoryList(getContext());

        int currentTimestampOnDevice = SurveyManager.getInstance(getContext()).getCurrentGlobalTimestampOnDevice();
        MotivAPIClientManager.getInstance(getContext()).makeGetSurveysRequest(currentTimestampOnDevice);
    }

    View globalView;

    TextView totalPointsTextView;

    TextView daysWithTripsTextView;
    TextView numberOfTripsTextView;

    ConstraintLayout rewardsWrapperConstraintLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_layout_scrollview_only_surveys, container, false);

        globalView = view;

        ImageButton openDrawerButton = view.findViewById(R.id.openDrawerButton);
        openDrawerButton.setOnClickListener(buttonListener);

        ImageView woortiLogoImageView = view.findViewById(R.id.woortiLogoFooterImageView);

        Glide.with(this).load(R.drawable.woorti_white_logo_footer)
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                .into(woortiLogoImageView);


        // draw trip validation

        ConstraintLayout newTripsToValidateLayout = view.findViewById(R.id.newTripsToValidateLayout);

        daysWithTripsTextView = view.findViewById(R.id.daysWithTripsTextView);
        numberOfTripsTextView = view.findViewById(R.id.numberOfTripsTextView);

        //draw "You have trips to validated" button if its the case

        if(checkIfPendingTrips(getContext())){
            newTripsToValidateLayout.setVisibility(View.VISIBLE);
            newTripsToValidateLayout.setOnClickListener(this);

            ImageView tripToValidateImageView = newTripsToValidateLayout.findViewById(R.id.newStoryImageView);

            Glide.with(this).load(R.drawable.new_survey)
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                    .centerCrop()
                    .into(tripToValidateImageView);

        }else {
            newTripsToValidateLayout.setVisibility(View.GONE);
        }

        ConstraintLayout todaysTravelReportLayout = view.findViewById(R.id.todaysTravelReportLayout);
        todaysTravelReportLayout.setOnClickListener(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // draw rewards

        rewardsWrapperConstraintLayout = view.findViewById(R.id.rewardsWrapperConstraintLayout);

        updateAndDrawRewards();

        totalPointsTextView = view.findViewById(R.id.totalPointsTextView);

        return view;
    }

    UserSettingStateWrapper userSettingStateWrapper;
    ArrayList<StoryStateful> storyStatefuls;

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                updatedRewardsReceived);
    }

    @Override
    public void onResume() {
        super.onResume();

        //survey part

        ArrayList<SurveyStateful> allTriggeredSurveys = new ArrayList<>(new PersistentTripStorage(getContext()).getAllTriggeredSurveysObject());

        Collections.reverse(allTriggeredSurveys);

        triggeredSurveys = new ArrayList<>();

        for(SurveyStateful surveyStateful : allTriggeredSurveys){

            if(!surveyStateful.isHasBeenAnswered()){
                triggeredSurveys.add(surveyStateful);
                Log.e("HomeFragment", "Showing survey " + surveyStateful.getSurvey().getSurveyID());
            }

            if(triggeredSurveys.size() >= 2){
                break;
            }

        }

        drawNewSurveys(globalView, getLayoutInflater());

        // stories

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                updatedRewardsReceived, new IntentFilter(RewardManager.keys.rewardsStatusRefreshedBroadcastKey));

        storyStatefuls = userSettingStateWrapper.getUserSettings().getStories();

        if (!(storyStatefuls == null || storyStatefuls.size() == 0)){

            int zeroTimestampCounters = 0;
            for (StoryStateful storyStateful : storyStatefuls){

                if (storyStateful.getReadTimestamp() == 0l){
                    zeroTimestampCounters++;
                }

            }

            if(zeroTimestampCounters > 1){
                Log.e("MobilityCoachFragment", "Corrupted stories, reinitializing");
                storyStatefuls = null;
            }

        }


        checkAndAddNewStoryStateful(storiesData);



        drawNewStories(globalView, getLayoutInflater());


        try {
//            showTimeDistanceStats(globalView);
            showNewTimeDistanceStats(globalView);
        }catch (Exception e){
            Log.e("HomeFragment", "EXCEPTION DURING STAT COMPUTING");
        }

        // update points

        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");

        if(userSettingStateWrapper.getUserSettings() != null){

            boolean setPoints = false;

            if(userSettingStateWrapper.getUserSettings().getChosenDefaultCampaignID() != null){

                for(CampaignScore campaign: userSettingStateWrapper.getUserSettings().getPointsPerCampaign()){

                    if(campaign.getCampaignID().equals(userSettingStateWrapper.getUserSettings().getChosenDefaultCampaignID())){

                        setPoints = true;
                        totalPointsTextView.setText(campaign.getCampaignScore() + "");
                        Log.e("HomeFragment", "Showing score for campaign with id (set by user) " + userSettingStateWrapper.getUserSettings().getChosenDefaultCampaignID());

                    }
                }
            }


//                if(userSettingStateWrapper.getUserSettings().getPointsPerCampaign() != null);

            if((!setPoints) && (userSettingStateWrapper.getUserSettings().getPointsPerCampaign() != null)){
            int size = userSettingStateWrapper.getUserSettings().getPointsPerCampaign().size();

            for (CampaignScore campaignScore : userSettingStateWrapper.getUserSettings().getPointsPerCampaign()){

                if(campaignScore.getCampaignID().equals("dummyCampaignID") && size > 1){
                    continue;
                }

                Log.e("HomeFragment", "Showing points for campaign " + campaignScore.getCampaignID());

                totalPointsTextView.setText(campaignScore.getCampaignScore() + "");
                break;
            }

        }
        }

//        compute days with validated trips and amount of trips

        ArrayList<FullTripDigest> fullTripDigests = new PersistentTripStorage(getContext()).getAllFullTripDigestsByUserIDObjects(FirebaseAuth.getInstance().getUid());

        int daysWithTrips = 0;
        int numTrips = 0;

        long lastTS = 0;

        for (FullTripDigest fullTripDigest: fullTripDigests){

            if((fullTripDigest.isValidated())){

                numTrips++;

                if(!DateHelper.isSameDay(lastTS, fullTripDigest.getInitTimestamp())){
                    daysWithTrips++;
                }

                lastTS = fullTripDigest.getInitTimestamp();

            }
        }

        daysWithTripsTextView.setText(daysWithTrips + "");
        numberOfTripsTextView.setText(numTrips + "");
        Log.e("HomeFragment", "Num validated trips: " + numTrips + " days with validated trips: " + daysWithTrips);

    }

    public void updateAndDrawRewards(){

//        ArrayList<RewardData> rewardDataArrayList = RewardManager.getInstance().getRewardDataArrayList();
        ArrayList<RewardData> rewardDataArrayList = RewardManager.getInstance().getValidRewardsDataArrayList();

//        ConstraintLayout rewardsWrapperConstraintLayout = view.findViewById(R.id.rewardsWrapperConstraintLayout);

        boolean alreadyShowingPopup = false;

        if (rewardDataArrayList.size() > 0){

            rewardsWrapperConstraintLayout.setVisibility(View.VISIBLE);

            LinearLayout rewardsLinearLayout = (LinearLayout) rewardsWrapperConstraintLayout.findViewById(R.id.rewardLinearLayout);
            rewardsLinearLayout.removeAllViews();

            for (RewardData rewardData : rewardDataArrayList){

                //skiprewards that have ended more than 1 month ago (2592000000l equals 1 month in milliseconds)
                if ((rewardData.getEndDate() + 2592000000l) < DateTime.now().getMillis()) continue;

                RewardStatus rewardStatus = RewardManager.getInstance().getRewardStatus(getContext(), rewardData.getRewardId());

                if (rewardStatus != null){

                    if(!alreadyShowingPopup && !rewardStatus.isHasShownPopup()){

                        if(checkIfWasCompletedAndShowPopup(rewardStatus, rewardData)){

                            Log.e("Reward homefrag", "hasShownPopup " + rewardStatus.isHasShownPopup() + "reward id " + rewardData.getRewardId() + " version " + rewardData.getRewardId());

                            RewardManager.getInstance().setRewardAsShown(rewardData.getRewardId(), getContext());

                            Log.e("Reward homefrag", "hasShownPopup after " + RewardManager.getInstance().getRewardStatus(getContext(), rewardData.getRewardId()).isHasShownPopup());

                            alreadyShowingPopup = true;
                        }

                    }

                }

                ConstraintLayout storiesModuleLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.home_reward_layout, null, false);
                rewardsLinearLayout.addView(storiesModuleLayout);

                switch (rewardData.getTargetType()){
                    case RewardData.keys.POINTS:
                    case RewardData.keys.DAYS:
                    case RewardData.keys.TRIPS:

                        if (rewardStatus != null){
                            updateRewardValues(storiesModuleLayout, rewardData, rewardStatus.getCurrentValue());

                        }else{
                            updateRewardValues(storiesModuleLayout, rewardData, 0);
                        }

                        break;

                    case RewardData.keys.POINTS_ALL_TIME:

                        updateRewardValues(storiesModuleLayout, rewardData, CampaignManager.getInstance(
                                getContext()).getCampaignScore(rewardData.getTargetCampaignId(),
                                getContext()));

                        break;
                    case RewardData.keys.DAYS_ALL_TIME:
                        updateRewardValues(storiesModuleLayout, rewardData, RewardManager.getInstance().getDaysWithTripsAllTime());
                        break;
                    case RewardData.keys.TRIPS_ALL_TIME:
                        updateRewardValues(storiesModuleLayout, rewardData, RewardManager.getInstance().getTripsAllTime());
                        break;
                }


            }


        }else{

            rewardsWrapperConstraintLayout.setVisibility(View.GONE);

        }

    }

    private boolean checkIfWasCompletedAndShowPopup(RewardStatus rewardStatus, RewardData rewardData) {

        if (rewardData.getTargetValue() < rewardStatus.getCurrentValue()){

            showCompletedRewardPopup(rewardData.getRewardName());
            return true;

        }else{

            return false;

        }

    }

    private void showCompletedRewardPopup(String completedRewardName) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.reward_completed_popup_layout, null);

        mBuilder.setView(mView);

        ImageView imageView = mView.findViewById(R.id.squirrelImageView);

        Glide.with(this.getContext()).load(R.drawable.onboarding_illustrations_enjoyment).into(imageView);

        TextView rewardNameCompletedTextView = mView.findViewById(R.id.popUpMessageTextView);

        rewardNameCompletedTextView.setText(getString(R.string.You_Have_Completed_Your_Target, completedRewardName));

        final AlertDialog dialog = mBuilder.create();

        Button continueButton = mView.findViewById(R.id.continueButton);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                //call method on fragment to show add activities dialog
            }
        });

        dialog.show();


    }


    private BroadcastReceiver updatedRewardsReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            updateAndDrawRewards();

        }
    };

    //refactor
    private long computeAvailableTime() {

        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 3);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        Log.d("MobilityCoachFragment" , DateHelper.getDateFromTSString(date.getTimeInMillis()));

        if (date.getTimeInMillis() < new DateTime(UTC).getMillis()){
            Log.d("MobilityCoachFragment" , "3am already passed!");
            date.add(Calendar.DAY_OF_MONTH, 1);
            Log.d("MobilityCoachFragment" , DateHelper.getDateFromTSString(date.getTimeInMillis()));
        }else{
            Log.d("MobilityCoachFragment" , "3a still to come!");
        }

        return date.getTimeInMillis();
    }

    //just copied from home fragment...refactor
    private void checkAndAddNewStoryStateful(ArrayList<Story> stories) {

        boolean needToWrite = false;

        //if no stories in settings/onboarding -> add first story of story list
        if((storyStatefuls == null) || (storyStatefuls.size() == 0)){

            Log.d("MobilityCoachFragment","No stories stateful in onboarding -> adding first story");

            storyStatefuls = new ArrayList<>();

            storyStatefuls.add(new StoryStateful(stories.get(0).getStoryID(), false, 0, new DateTime(UTC).getMillis()));
            userSettingStateWrapper.getUserSettings().setStories(storyStatefuls);
            needToWrite = true;

            //else if last story shown was read already -> check if there are more stories and add to settings/onboarding stories list
        }else{

            StoryStateful lastStoryStateful = storyStatefuls.get(storyStatefuls.size()-1);

            Story nextStoryIfExists = Story.getStoryById(lastStoryStateful.getStoryID() + 1, storiesData);

            if(lastStoryStateful.isRead() && nextStoryIfExists != null){
                storyStatefuls.add(new StoryStateful(nextStoryIfExists.getStoryID(), false, 0l, computeAvailableTime()));
                userSettingStateWrapper.getUserSettings().setStories(storyStatefuls);
                needToWrite = true;
            }

        }

        if (needToWrite){

            Log.e("MobilityCoachFragment", "needs to write = " + true);
            SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

        }

    }

    private void drawNewStories(View view, LayoutInflater inflater) {


        LinearLayout storiesLinearLayout = (LinearLayout) view.findViewById(R.id.newStoriesLinearLayout);
        storiesLinearLayout.removeAllViews();


        ArrayList<StoryStateful> stories = new ArrayList<>(storyStatefuls);
        Collections.reverse(stories);

        ArrayList<StoryStateful> storiesToBeShown = new ArrayList<>();

        for(StoryStateful storyStateful : stories){

            Log.e("HomeFragment", "adding " + storyStateful.getStoryID() + "should be drawn "+ DateHelper.getDateFromTSString(storyStateful.getAvailableTimestamp()));

            if((storyStateful.getAvailableTimestamp() < DateTime.now().getMillis()) && !storyStateful.isRead()){
                storiesToBeShown.add(storyStateful);
            }

            if(storiesToBeShown.size() >= 2){
                break;
            }

        }


        for(final StoryStateful storyStateful : storiesToBeShown){

            ConstraintLayout storyLayout = (ConstraintLayout) inflater.inflate(R.layout.new_story_wrapper_layout, null, false);

            TextView storyTitle = storyLayout.findViewById(R.id.storyTitleTextView);


            final Story beingShown = Story.getDummmyStoryList(getContext()).get(storyStateful.getStoryID());
            storyTitle.setText(beingShown.getContentTitle());

            ImageView imageView = storyLayout.findViewById(R.id.newStoryImageView);

            imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            Log.e("HomeFragment", "storyImageView height " + imageView.getMeasuredHeight() +  " storyImageView width " +  imageView.getMeasuredWidth());

            Glide.with(this).load(beingShown.getImageDrawable())
                    .centerCrop()
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                    .into(imageView);

//            imageView.setImageDrawable(getResources().getDrawable(story.getImageDrawable()));

            final int storyState;

            if (storyStateful.isRead()) {
                Log.d("MobilityCoachFragment","Story read");
                storyState = MobilityCoachFragment.keys.READ_STORY;
            } else if (storyStateful.getAvailableTimestamp() < new DateTime(UTC).getMillis()) {
                Log.d("MobilityCoachFragment","StoryAvailable but not read");
                storyState = MobilityCoachFragment.keys.AVAILABLE_STORY_NOT_READ;
            } else {
                Log.d("MobilityCoachFragment","Coming tomorrow story");
                storyState = MobilityCoachFragment.keys.COMING_TOMORROW_STORY;
            }

            storyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //if story
                    if (storyState == MobilityCoachFragment.keys.AVAILABLE_STORY_NOT_READ) {

                        storyStateful.setRead(true);
                        storyStateful.setReadTimestamp(new DateTime(UTC).getMillis());

                        Story nextStoryIfExists = Story.getStoryById(storyStateful.getStoryID() + 1, storiesData);

                        if (nextStoryIfExists != null) {
                            //dummy
                            storyStatefuls.add(new StoryStateful(nextStoryIfExists.getStoryID(), false, 0, computeAvailableTime()));
                        }

                        userSettingStateWrapper.getUserSettings().setStories(storyStatefuls);
                        SharedPreferencesUtil.writeOnboardingUserData(getContext(), userSettingStateWrapper, true);

                    }


                    Intent intentStory = new Intent(getContext(), StoryActivity.class);
                    intentStory.putExtra(StoryActivity.keys.INTENT_STORY, beingShown);

                    startActivity(intentStory);

                }
            });

            storiesLinearLayout.addView(storyLayout);

        }

    }

    private void drawNewSurveys(View view, LayoutInflater inflater) {

        LinearLayout surveysLinearLayout = (LinearLayout) view.findViewById(R.id.newSurveysLinearLayout);
        surveysLinearLayout.removeAllViews();

        for(final SurveyStateful triggeredSurvey : triggeredSurveys){


            if(!triggeredSurvey.isHasBeenAnswered()) {

                ConstraintLayout surveyLayout = (ConstraintLayout) inflater.inflate(R.layout.new_survey_wrapper_layout, null, false);
                surveysLinearLayout.addView(surveyLayout);

                ImageView imageView = surveyLayout.findViewById(R.id.newSurveyImageView);

                Glide.with(this).load(R.drawable.illustration_home_survey)
                        .centerCrop()
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                        .into(imageView);

                TextView surveyPointsTextView = surveyLayout.findViewById(R.id.surveyPointsTextView);
                surveyPointsTextView.setText(triggeredSurvey.getSurvey().getSurveyPoints() + " pts");

                TextView estimatedSurveyDurationTextView = surveyLayout.findViewById(R.id.textView6);

                estimatedSurveyDurationTextView.setText(triggeredSurvey.getSurvey().getEstimatedDuration() + " min");

                Log.e("HomeFragment", "Estimated duration" + triggeredSurvey.getSurvey().getEstimatedDuration());

                surveyLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!triggeredSurvey.isHasBeenAnswered()) {

                            Intent intentShowSurvey = new Intent(getContext(), SurveyTestActivity.class);
                            intentShowSurvey.putExtra("SurveyStateful", triggeredSurvey);


                            Log.d("size surveys", "size " + triggeredSurvey.getSurvey().getQuestions().size());
                            Log.d("size surveys", "pref lang " + triggeredSurvey.getSurvey().getDefaultLanguage());


                            startActivity(intentShowSurvey);
                        } else {

                            Toast.makeText(getContext(), "Already answered survey", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }


        }

    }

//    public void showTimeDistanceStats(View view){
//
//        TextView timeTextView = view.findViewById(R.id.timeTextView);
//        TextView distanceTextView = view.findViewById(R.id.distanceTextView);
//
//        TextView moreLessTimeTextView = view.findViewById(R.id.minutesMoreLessTextView);
//        TextView moreLessDistanceTextView = view.findViewById(R.id.kmMoreLessTextView);
//
//
//        long lastDayTS = DateTime.now().minusDays(1).getMillis();
//        long lastTwoDayTS = DateTime.now().minusDays(2).getMillis();
//
//        ArrayList<FullTripDigest> timeIntervalDigests = new PersistentTripStorage(getContext()).getAllFullTripDigestsForTimeIntervalObjects(lastTwoDayTS);
//
//        Log.e("DashboardFragment", "#Digests from this time interval: " + timeIntervalDigests.size());
//
//        long totalTimeTraveledLastDay = 0;
//        long totalDistanceTraveledLastDay = 0;
//
//        long totalTimeTraveledDayBefore = 0;
//        long totalDistanceTraveledDayBefore = 0;
//
//
//        for (FullTripDigest fullTripDigest : timeIntervalDigests){
//
//            if((fullTripDigest.getTripStats() != null) && (fullTripDigest.getInitTimestamp() > lastTwoDayTS) && (fullTripDigest.getInitTimestamp() < lastDayTS)){
//
//                totalTimeTraveledDayBefore += (fullTripDigest.getEndTimestamp() - fullTripDigest.getInitTimestamp());
//                totalDistanceTraveledDayBefore += (fullTripDigest.getDistanceTraveled());
//
//            }else if((fullTripDigest.getTripStats() != null) && (fullTripDigest.getInitTimestamp() > lastDayTS) && (fullTripDigest.getInitTimestamp() < DateTime.now().getMillis())){
//
//                totalTimeTraveledLastDay += (fullTripDigest.getEndTimestamp() - fullTripDigest.getInitTimestamp());
//                totalDistanceTraveledLastDay += (fullTripDigest.getDistanceTraveled());
//            }
//
//        }
//
//        if(totalTimeTraveledLastDay >= totalTimeTraveledDayBefore){
//
//            long minutesMore = TimeUnit.MILLISECONDS.toMinutes(totalTimeTraveledLastDay- totalTimeTraveledDayBefore);
//            timeTextView.setText(minutesMore+"");
//            moreLessTimeTextView.setText(getString(R.string.Minutes_More));
//
//
//        }else{
//
//            long minutesLess = TimeUnit.MILLISECONDS.toMinutes(totalTimeTraveledDayBefore-totalTimeTraveledLastDay);
//            timeTextView.setText(minutesLess+"");
//            moreLessTimeTextView.setText(getString(R.string.Minutes_Less));
//
//
//        }
//
//        if(totalDistanceTraveledLastDay >= totalDistanceTraveledDayBefore){
//
//            long distance = ((totalDistanceTraveledLastDay-totalDistanceTraveledDayBefore)/1000);
//
//            distanceTextView.setText(distance+"");
//            moreLessDistanceTextView.setText(getString(R.string.Km_More));
//
//        }else{
//
//            long distance = ((totalDistanceTraveledDayBefore-totalDistanceTraveledLastDay)/1000);
//
//            distanceTextView.setText(distance+"");
//            moreLessDistanceTextView.setText(getString(R.string.Km_Less));
//        }
//
//
//    }

    public void showNewTimeDistanceStats(View view){

        TextView timeTextView = view.findViewById(R.id.timeTextView);
        TextView distanceTextView = view.findViewById(R.id.distanceTextView);

        TextView moreLessTimeTextView = view.findViewById(R.id.minutesMoreLessTextView);
        TextView moreLessDistanceTextView = view.findViewById(R.id.kmMoreLessTextView);

        long lastWeekStart = DateTime.now().minusWeeks(1).getMillis();

        ArrayList<FullTripDigest> timeIntervalDigests = new PersistentTripStorage(getContext()).getAllFullTripDigestsForTimeIntervalObjects(lastWeekStart);

        Log.e("DashboardFragment", "#Digests from this time interval: " + timeIntervalDigests.size());

        long totalTimeTraveledLastWeek = 0;

        for (FullTripDigest fullTripDigest : timeIntervalDigests){

            if((fullTripDigest.getTripStats() != null) && (fullTripDigest.getInitTimestamp() > lastWeekStart)){
                totalTimeTraveledLastWeek += (fullTripDigest.getEndTimestamp() - fullTripDigest.getInitTimestamp());
            }
        }

        double timeTraveledLastWeekHours = DateHelper.getHoursFromMillis(totalTimeTraveledLastWeek);

        timeTextView.setText(timeTraveledLastWeekHours +"");

        moreLessTimeTextView.setText(getString(R.string.Hours));

        WorthwhilenessCO2Score worthwhilenessCO2Score = WorthwhilenessCO2Score.computeWorthwhilenessScore(timeIntervalDigests);

        double finalWorthwhilenessScore =  worthwhilenessCO2Score.getWorthwhilenessScore();

        distanceTextView.setText(NumbersUtil.roundNoDecimalPlace(finalWorthwhilenessScore) + "%");

        moreLessDistanceTextView.setText(getString(R.string.Worthwhileness));


    }


    private void updateRewardValues(View view, RewardData rewardData, int currentScore) {

        TextView nameOfTheRewardTextView = view.findViewById(R.id.nameOfTheRewardTextView);
        TextView timeLeftOfReward = view.findViewById(R.id.timeLeftRewardTextView);
        TextView minRangeRewardTextView = view.findViewById(R.id.minRangeRewardTextView);
        TextView maxRangeRewardTextView = view.findViewById(R.id.maxRangeRewardTextView);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        nameOfTheRewardTextView.setText(rewardData.getRewardName());

//        int daysLeftForReward = (int) DateHelper.getDaysUntilDate(DateTime.now().getMillis(), rewardData.getEndDate());

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(rewardData.getEndDate()));


//        timeLeftOfReward.setText(getString(R.string.days_left_reward, daysLeftForReward+""));
        timeLeftOfReward.setText(getString(R.string.Complete_Until, date+""));

        int min = 0;
        int max = rewardData.getTargetValue();

        setRangesTextViews(minRangeRewardTextView, maxRangeRewardTextView, rewardData);

//        minRangeRewardTextView.setText(getString(R.string.min_range_reward, min + ""));
//
//        maxRangeRewardTextView.setText(getString(R.string.max_range_reward, (max) +""));
        progressBar.setMax(max-min);

        progressBar.setProgress(currentScore - min);

    }

    public void setRangesTextViews(TextView minRange, TextView maxRange, RewardData rewardData){

        StringBuilder minRangeString = new StringBuilder();
        minRangeString.append(0);
        minRangeString.append(" ");

        StringBuilder maxRangeString = new StringBuilder();
        maxRangeString.append(rewardData.getTargetValue());
        maxRangeString.append(" ");


        switch (rewardData.getTargetType()){
            case RewardData.keys.POINTS:
                minRangeString.append(getString(R.string.Points));
                maxRangeString.append(getString(R.string.Points));
                break;

            case RewardData.keys.DAYS:
                minRangeString.append(getString(R.string.Days));
                maxRangeString.append(getString(R.string.Days));
                break;

            case RewardData.keys.TRIPS:
                minRangeString.append(getString(R.string.Trips));
                maxRangeString.append(getString(R.string.Trips));
                break;
            case RewardData.keys.POINTS_ALL_TIME:
                minRangeString.append(getString(R.string.Points_All_Time));
                maxRangeString.append(getString(R.string.Points_All_Time));
                break;
            case RewardData.keys.DAYS_ALL_TIME:
                minRangeString.append(getString(R.string.Days_All_Time));
                maxRangeString.append(getString(R.string.Days_All_Time));
                break;
            case RewardData.keys.TRIPS_ALL_TIME:
                minRangeString.append(getString(R.string.Trips_All_Time));
                maxRangeString.append(getString(R.string.Trips_All_Time));
                break;
        }

        minRange.setText(minRangeString.toString());
        maxRange.setText(maxRangeString.toString());

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

    public void createAndShowSampleSurvey(){

        ArrayList<String> answers = new ArrayList<>();
        answers.add("option1");
        answers.add("option2");
        answers.add("option3");
        answers.add("option4");

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.newTripsToValidateLayout:

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, MyTripsFragment.newInstance("","")).commitAllowingStateLoss();

                break;

            case R.id.todaysTravelReportLayout:

                FragmentManager fragmentManagerT = getFragmentManager();
                fragmentManagerT.beginTransaction().replace(R.id.main_fragment, DashboardFragment.newInstance("","")).commitAllowingStateLoss();

                break;
        }


    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.openDrawerButton:
                    try {
                        ((HomeDrawerActivity) getActivity()).openDrawer();
                    } catch (Exception e){ Log.e("ProfileAndSettings", "Null pointer on getActivity");}
                    break;
            }
        }
    };

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

    //unused right now
    public void setUpGoalsCount(int goalsAchieved){

        int goalsToBeDrawn = goalsAchieved;

        if(goalsAchieved > 5){
            goalsToBeDrawn = 5;
        }

        for(int i = 0; i<5; i++){

            ImageView goal = getView().findViewById(keys.goalRes[i]);

            if(goalsToBeDrawn > i) {
                Drawable d = getResources().getDrawable(R.drawable.goal_achieved);
                goal.setImageDrawable(d);
            }else{
                goal.setImageResource(R.drawable.goal_not_achieved);
            }

        }

    }

    public interface keys{

        int[] goalRes = {
                R.id.goal1ImageView,
                R.id.goal2ImageView,
                R.id.goal3ImageView,
                R.id.goal4ImageView,
                R.id.goal5ImageView};

    }
}

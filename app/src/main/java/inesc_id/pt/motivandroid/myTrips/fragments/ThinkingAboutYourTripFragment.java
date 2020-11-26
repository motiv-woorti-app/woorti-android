package inesc_id.pt.motivandroid.myTrips.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.modesOfTransport.WorthwhilenessValues;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.ActivityTimeCounter;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.ModalityTimeDistanceCounter;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.TripStats;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.WorthwhilenessScore;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.motviAPIClient.MotivAPIClientManager;
import inesc_id.pt.motivandroid.myTrips.activities.MyTripsActivity;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * ThinkingAboutYourTripFragment
 *
 * Last fragment of the trip validation flow. The user answers the question "Would you have liked to
 * use your travel time more for...?". Also asks the user if he has any textual comments to add.
 * On "End" press, the trip is finished (score computed and trip saved persistently)
 *
 * Trip id is passed through ARG_PARAM1 parameter
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
public class ThinkingAboutYourTripFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String fullTripID;

    private FullTrip fullTripBeingValidated;

    private OnFragmentInteractionListener mListener;

    ConstraintLayout prodButtonLayout;
    ConstraintLayout enjoyButtonLayout;
    ConstraintLayout fitnessButtonLayout;

    EditText otherOption;

    Button endButton;

    TripKeeper tripKeeper;


    public ThinkingAboutYourTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ThinkingAboutYourTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThinkingAboutYourTripFragment newInstance(String param1) {
        ThinkingAboutYourTripFragment fragment = new ThinkingAboutYourTripFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    UserSettingStateWrapper userSettingStateWrapper;

    ArrayList<ModeOfTransportUsed> modesOfTransportUsed;
    WorthwhilenessValues generalValuesForWorthwhilenss;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fullTripID = getArguments().getString(ARG_PARAM1);
        }

        tripKeeper = TripKeeper.getInstance(getContext());
        fullTripBeingValidated = tripKeeper.getCurrentFullTrip(fullTripID);

        if(getActivity() instanceof MyTripsActivity){
            ((MyTripsActivity) getActivity()).computeAndUpdateTripScore();
        }

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getContext(), "");
        modesOfTransportUsed = userSettingStateWrapper.getUserSettings().getPreferedModesOfTransport();

        //get user worthwhileness scores
        int fitnessValue = userSettingStateWrapper.getUserSettings().getActivityValue();
        int enjoymentValue = userSettingStateWrapper.getUserSettings().getRelaxingValue();
        int productivityValue = userSettingStateWrapper.getUserSettings().getProductivityValue();

        generalValuesForWorthwhilenss = new WorthwhilenessValues(productivityValue, enjoymentValue, fitnessValue);

    }

    int pressedButton = -1;

    public void selectOption(int mode){

        switch(mode){

            case keys.prod:

                setImageViewResourceGlide(R.drawable.mytrips_navigation_productivity_icon, productivityImageView);
                prodTextView.setTextColor(Color.parseColor("#FFB020"));
                break;
            case keys.enjoy:

                setImageViewResourceGlide(R.drawable.mytrips_navigation_enjoyment_icon, enjoymentImageView);
                enjoyTextView.setTextColor(Color.parseColor("#1CB0F6"));
                break;
            case keys.fitness:
                setImageViewResourceGlide(R.drawable.mytrips_navigation_fitness_icon, fitnessImageView);
                fitnessTextView.setTextColor(Color.parseColor("#82CF90"));
                break;

        }

    }

    public void setImageViewResourceGlide(int drawable, ImageView imageView){

        Glide.with(this).load(drawable)
//                    .override(view.getMeasuredWidth(), view.getMeasuredHeight())
                .into(imageView);

    }

    public void unselectOption(int mode){

        switch(mode){

            case keys.prod:

                setImageViewResourceGlide(R.drawable.mytrips_navigation_productivity_bw, productivityImageView);
                prodTextView.setTextColor(Color.parseColor("#70706D"));

                break;
            case keys.enjoy:

                setImageViewResourceGlide(R.drawable.mytrips_navigation_enjoyment_bw, enjoymentImageView);
                enjoyTextView.setTextColor(Color.parseColor("#70706D"));

                break;
            case keys.fitness:
                setImageViewResourceGlide(R.drawable.mytrips_navigation_fitness_bw, fitnessImageView);
                fitnessTextView.setTextColor(Color.parseColor("#70706D"));

                break;

        }

    }

    // productivity, enjoyment, fitness buttons (like a switch)
    ImageView productivityImageView;
    ImageView enjoymentImageView;
    ImageView fitnessImageView;

    TextView prodTextView;
    TextView enjoyTextView;
    TextView fitnessTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thinking_about_your_trip, container, false);

        prodButtonLayout = view.findViewById(R.id.productivityConstraintLayout);
        enjoyButtonLayout = view.findViewById(R.id.enjoymentConstraintLayout);
        fitnessButtonLayout = view.findViewById(R.id.fitnessConstraintLayout);

        prodButtonLayout.setOnClickListener(this);
        enjoyButtonLayout.setOnClickListener(this);
        fitnessButtonLayout.setOnClickListener(this);

        productivityImageView = view.findViewById(R.id.imageViewProductivity);
        enjoymentImageView = view.findViewById(R.id.imageViewEnjoyment);
        fitnessImageView = view.findViewById(R.id.imageViewFitness);

        prodTextView = view.findViewById(R.id.textViewProductivity);
        enjoyTextView = view.findViewById(R.id.textViewEnjoyment);
        fitnessTextView = view.findViewById(R.id.textViewFitness);

        unselectOption(keys.prod);
        unselectOption(keys.enjoy);
        unselectOption(keys.fitness);

        otherOption = view.findViewById(R.id.otherOptionEditText);

        if(fullTripBeingValidated.getUseTripMoreFor() != null){
            pressedButton = fullTripBeingValidated.getUseTripMoreFor();
            selectOption(pressedButton);
        }

        if(fullTripBeingValidated.getShareInformation() != null){
            otherOption.setText(fullTripBeingValidated.getShareInformation());
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        endButton = view.findViewById(R.id.btNext);
        endButton.setOnClickListener(this);
//        setEndButtonEndable();

        int possibleScore = 0;
        possibleScore = fullTripBeingValidated.getTripScored(getContext()).getPossibleAllInfoPoints();
        if(getActivity() instanceof  MyTripsActivity){
            ((MyTripsActivity) getActivity()).updatePossibleScore(possibleScore);
        }

        //check if there is Prod, enjoyment, fitness from full trip, if so selectOption(option from fulltrip)
        //also fill anything you would like to share if there is

        return view;
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

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.productivityConstraintLayout:

                if(pressedButton != keys.prod){
                    unselectOption(pressedButton);
                    pressedButton = keys.prod;
                    selectOption(keys.prod);
                }else{
                    unselectOption(pressedButton);
                    pressedButton = -1;
                }

                break;

            case R.id.enjoymentConstraintLayout:

                if(pressedButton != keys.enjoy){
                    unselectOption(pressedButton);
                    pressedButton = keys.enjoy;
                    selectOption(keys.enjoy);
                }else{
                    unselectOption(pressedButton);
                    pressedButton = -1;
                }

                break;

            case R.id.fitnessConstraintLayout:


                if(pressedButton != keys.fitness){
                    unselectOption(pressedButton);
                    pressedButton = keys.fitness;
                    selectOption(keys.fitness);
                }else{
                    unselectOption(pressedButton);
                    pressedButton = -1;
                }

                break;

            case R.id.btNext:
                closeTripAndFinish();
                break;

        }

    }

    private void closeTripAndFinish() {

        fullTripBeingValidated.setValidated(true);
        fullTripBeingValidated.setSentToServer(false);

        //save user selected option
        fullTripBeingValidated.setUseTripMoreFor(pressedButton);

        if (otherOption.getText().toString().length() > 0) {

            fullTripBeingValidated.setShareInformation(otherOption.getText().toString());

        }

        fullTripBeingValidated.getTripScored(getContext()).answerCompleteAllInfo();

        ArrayList<RewardData> completedRewards = new ArrayList<>();

        //compute trip score to be assigned to each campaign
        //update campaign scores (need previous score to avoid adding duplicate score)
        if(getActivity() instanceof MyTripsActivity){

            Log.e("TripScore", "prevScore " + fullTripBeingValidated.getPrevScore());
            Log.e("TripScore", "currScore " + fullTripBeingValidated.computeTripScore(getContext()));

            HashMap<String, Integer> newScore = fullTripBeingValidated.computeTripScoreForAllCampaigns(getContext());

            for(Map.Entry<String, Integer> entryNewScore : newScore.entrySet()){
                Log.e("TripScore", "new score : k: " + entryNewScore.getKey() + " v " + entryNewScore.getValue());
            }

            for(Map.Entry<String, Integer> entryNewScore : newScore.entrySet()){
                Log.e("TripScore", "olc score : k: " + entryNewScore.getKey() + " v " + entryNewScore.getValue());
            }

            HashMap<String, Integer> prevScore = fullTripBeingValidated.getPrevScore();

            newScore.keySet().removeAll(Collections.singleton(null));
            prevScore.keySet().removeAll(Collections.singleton(null));

            completedRewards.addAll(((MyTripsActivity) getActivity()).updateGlobalScoreForCampaignsAndSave(prevScore, newScore));

            fullTripBeingValidated.setPrevScore(newScore);

        }

        //send reward status data to server
        MotivAPIClientManager.getInstance(getContext()).putAndGetMyRewardStatus();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // compute trip stats

        TripStats tripStats = new TripStats();

        ArrayList<ModalityTimeDistanceCounter> modalityTimeDistanceCounterArrayList = new ArrayList<>();

        for(FullTripPart ftp : fullTripBeingValidated.getTripList()){

            if(ftp.isTrip()){

                long legDistance = ((Trip) ftp).getDistanceTraveled();
                long timeTraveled = (((Trip) ftp).getEndTimestamp() - ((Trip) ftp).getInitTimestamp())/1000;

                int modality = ((Trip) ftp).getCorrectedModeOfTransport();

                ModalityTimeDistanceCounter.addModalityTimeDistance(modalityTimeDistanceCounterArrayList, new ModalityTimeDistanceCounter(legDistance, modality,timeTraveled));
            }
        }



        tripStats.setModalityTimeDistanceCounters(modalityTimeDistanceCounterArrayList);

        for(FullTripPart ftp : fullTripBeingValidated.getTripList()){

            if(ftp.getGenericActivities() != null && ftp.getGenericActivities().size() > 0){
                Log.e("ThinkingAbout", "Adding activity to leg");
                ActivityTimeCounter activityTimeCounter = new ActivityTimeCounter((ftp.getEndTimestamp() - ftp.getInitTimestamp()), ftp.getGenericActivities());
                tripStats.setActivityTimeCounter(activityTimeCounter);
                break;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        /// compute worthwhileness scores, co2 and calories values

        float intermediaryFitnessScore = 0;
        float intermediaryProductivityScore = 0;
        float intermediaryEnjoymentScore = 0;
        float intermediateTotalWorth = 0;

        int caloriesSpent = 0;
        int co2Footprint = 0;

        for(FullTripPart ftp : fullTripBeingValidated.getTripList()){

            if(ftp.isTrip()){

                int legMode = ((Trip) ftp).getCorrectedModeOfTransport();

                double timeTraveled = (ftp.getEndTimestamp() - ftp.getInitTimestamp())/1000.0/60.0;

                intermediaryProductivityScore += ActivityDetected.getProdEnjoyFitnessWorthFromLeg(timeTraveled, legMode, ActivityDetected.keys.PRODUCTIVITY_TYPE, modesOfTransportUsed);
                intermediaryEnjoymentScore += ActivityDetected.getProdEnjoyFitnessWorthFromLeg(timeTraveled, legMode, ActivityDetected.keys.ENJOYMENT_TYPE, modesOfTransportUsed);
                intermediaryFitnessScore += ActivityDetected.getProdEnjoyFitnessWorthFromLeg(timeTraveled, legMode, ActivityDetected.keys.FITNESS_TYPE, modesOfTransportUsed);

                intermediateTotalWorth += ActivityDetected.getTotalWorthFromLeg(legMode, modesOfTransportUsed, generalValuesForWorthwhilenss, timeTraveled);

                caloriesSpent+= ActivityDetected.getCaloriesFromModeAndDistance(legMode, ((Trip) ftp).getDistanceTraveled());
                co2Footprint += ActivityDetected.getCO2ValueForDistanceAndMode(((Trip) ftp).getDistanceTraveled(), legMode);

            }

        }

        double timeTraveled = (fullTripBeingValidated.getEndTimestamp() - fullTripBeingValidated.getInitTimestamp())/60.0/1000.0;
        Log.e("TripStats", "--- co2 " + co2Footprint);
        Log.e("TripStats", "--- calories " + caloriesSpent);
        Log.e("TripStats", "duration " + timeTraveled);
        Log.e("TripStats", "--- prod score " + intermediaryProductivityScore);
        Log.e("TripStats", "--- enj score " + intermediaryEnjoymentScore);
        Log.e("TripStats", "--- fit score " + intermediaryFitnessScore);
        Log.e("TripStats", "--- total score " + intermediateTotalWorth);

        Log.e("TripStats", "--f-- prod score " + intermediaryProductivityScore/timeTraveled);
        Log.e("TripStats", "--f-- enj score " + intermediaryEnjoymentScore/timeTraveled);
        Log.e("TripStats", "--f-- fit score " + intermediaryFitnessScore/timeTraveled);
        Log.e("TripStats", "--f-- total score " + intermediateTotalWorth/timeTraveled);


        tripStats.setWorthwhilenessScore(new WorthwhilenessScore(intermediaryProductivityScore/timeTraveled, intermediaryEnjoymentScore/timeTraveled, intermediaryFitnessScore/timeTraveled, intermediateTotalWorth/timeTraveled));
        tripStats.setC02FootprintValue(co2Footprint);
        tripStats.setCaloriesSpentValue(caloriesSpent);

        //save trip stats on the trip object
        fullTripBeingValidated.setTripStats(tripStats);

        ////////////////////////////////////////////////////////////////////////////////////////////

        fullTripBeingValidated.setValidationDate(DateTime.now().getMillis());

        tripKeeper.saveFullTripPersistently(fullTripBeingValidated);

        Log.d("ThinkingAboutYourTrip", "CompletedRewardsSize " + completedRewards.size());

        boolean showingCompletedPopup = false;

        //check if due to validating this trip, a new reward was achieved and if so show the
        //"completed reward popup)
        for (RewardData completedReward : completedRewards){
            Log.d("ThinkingAboutYourTrip", "CompletedRewardName " + completedReward);
            showCompletedRewardPopup(completedReward.getRewardName());
            showingCompletedPopup = true;

            RewardManager.getInstance().setRewardAsShown(completedReward.getRewardId(), getContext());
            break;
        }


        MotivAPIClientManager motivAPIClientManager = MotivAPIClientManager.getInstance(getContext());

        //try to send updated user data to server
        motivAPIClientManager.makeUpdateOnboardingRequest();

        //try to send validated trips to server
        motivAPIClientManager.sendConfirmedTripsToServer();

        //tell my trips fragment that a trip has been validated, to check if we should show the popup
        Intent intent=new Intent();
        intent.putExtra(MyTripsFragment.keys.MY_TRIPS_VALIDATION_RESULT,true);
        getActivity().setResult(MyTripsFragment.keys.MY_TRIPS_VALIDATION_REQUEST_CODE,intent);

        if(!showingCompletedPopup) {
            getActivity().finish();
        }


    }


    /**
     * Shows a updated that informs the user that he has completed a reward as a result of valida-
     * ting this trip
     *
     * @param completedRewardName
     */
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
                if(getActivity() != null) {
                    getActivity().finish();
                }
                //call method on fragment to show add activities dialog
            }
        });

        dialog.show();


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

    public interface keys{

        int prod = 0;
        int enjoy = 1;
        int fitness = 2;

    }
}

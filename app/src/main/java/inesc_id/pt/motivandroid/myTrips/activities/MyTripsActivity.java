package inesc_id.pt.motivandroid.myTrips.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.home.fragments.MyTripsFragment;
import inesc_id.pt.motivandroid.myTrips.ShowCurrentTripActivity;
import inesc_id.pt.motivandroid.deprecated.LegProductivityRelaxingFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.LegValidationFragment;
import inesc_id.pt.motivandroid.deprecated.MyTripsAddActivitiesFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.MyTripsFactorsWastedTime;
import inesc_id.pt.motivandroid.myTrips.fragments.MyTripsValueObtained;
import inesc_id.pt.motivandroid.myTrips.fragments.ObjectiveOfTripFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.OverviewAfterRelaxingProductivityFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.RatingStarsFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.SelectPartOfTripForFurtherFeedbackFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.ThinkingAboutYourTripFragment;
import inesc_id.pt.motivandroid.myTrips.fragments.legEdit.splitMergeDeleteFragments.DeleteTripPartFragment;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.persistence.TripKeeper;

/**
 * MyTripsActivity
 *
 *  Main activity of the "My trips" module. This class is instantiated when the user chooses a trip
 * from the trip list on the "MyTripsFragment". The selected trip's id is passed through the intent
 * (key = MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED).
 *  The user will go through a sequence of fragments to validate a trip:
 *
 *  1-LegValidationFragment
 *  2-RatingStarsFragment (keys.FEEL_ABOUT_TRIP_MODE)
 *  3-ObjectiveOfTripFragment
 *  4-SelectPartOfTripForFurtherFeedbackFragment (if trip has more than one leg, else jump to 5)
 *  5-RatingStarsFragment(keys.TRAVEL_TIME_WASTED_MODE)
 *  6-MyTripsValueObtained
 *  7-MyTripsFactorsWastedTime
 *  8-ThinkingAboutYourTripFragment
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

public class MyTripsActivity extends AppCompatActivity implements LegValidationFragment.OnFragmentInteractionListener,
        ObjectiveOfTripFragment.OnFragmentInteractionListener,
        LegProductivityRelaxingFragment.OnFragmentInteractionListener,
        OverviewAfterRelaxingProductivityFragment.OnFragmentInteractionListener,
        MyTripsAddActivitiesFragment.OnFragmentInteractionListener, View.OnClickListener,
        DeleteTripPartFragment.OnFragmentInteractionListener,
        SelectPartOfTripForFurtherFeedbackFragment.OnFragmentInteractionListener,
        RatingStarsFragment.OnFragmentInteractionListener, MyTripsFactorsWastedTime.OnFragmentInteractionListener,
        MyTripsValueObtained.OnFragmentInteractionListener, ThinkingAboutYourTripFragment.OnFragmentInteractionListener {


    /**
     * button to open ShowCurrentTripActivity
     */
    Button mapButton;

    /**
     * button to open edit trip options dialog
     */
    Button editButton;

    /**
     * "edit trip" options dialog
     */
    Dialog dialog;

    /**
     * "edit trip" dialog buttons (merge, split, delete)
     */
    Button mergeButton;
    Button splitButton;
    Button deleteButton;

    /**
     * instance of the current trip being validated
     */
    FullTrip fullTripToBeValidated;

    /**
     * trip id of the current trip being validated
     */
    String fullTripID;

    ImageButton backButton;

    /**
     * current score for the trip textview
     */
    TextView pointsTextView;

    /**
     * max possible score for the trip textview
     */
    TextView possiblePointsTv;

    UserSettingStateWrapper userSettingStateWrapper;

    ArrayList<Campaign> campaignArrayList;

    private static final Logger LOG = LoggerFactory.getLogger(MyTripsActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips_coordinated);

        mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(this);

        editButton = findViewById(R.id.editButton);

        editButton.setOnClickListener(this);

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_edittrip_option, null);
        dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        dialog.setContentView(view);

        mergeButton = dialog.findViewById(R.id.mergeButton);
        splitButton = dialog.findViewById(R.id.splitButton);
        deleteButton = dialog.findViewById(R.id.deleteButton);

        mergeButton.setOnClickListener(this);
        splitButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        fullTripID = getIntent().getStringExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED);

        LOG.debug("FullTripID " + fullTripID);

        //get object of trip being validated
        fullTripToBeValidated = TripKeeper.getInstance(getApplicationContext()).getCurrentFullTrip(fullTripID);

        //  if trip id exists but the trip itself does not exist, delete any trace of the trip and
        //and finish activity
        if(fullTripToBeValidated == null){

            if (fullTripID != null){

                try{
                    PersistentTripStorage persistentTripStorage = new PersistentTripStorage(getApplicationContext());
                    persistentTripStorage.deleteFullTripByDate(fullTripID);

                }catch (Exception e){
                    LOG.error(e.getMessage());
                }

            }

            finish();
        }

        // if the hasnt been assined departure and arrival textual addresses, try to retrieve these
        //addresses
        if(fullTripToBeValidated.getStartAddress() == null || fullTripToBeValidated.getFinalAddress() == null){
            try{
                retrieveAndSetPlaces(fullTripToBeValidated.getDeparturePlace().getLatLng(),
                        fullTripToBeValidated.getArrivalPlace().getLatLng(), fullTripToBeValidated);
            }catch(Exception e){
                Log.e("MyTripsActivity", "Exception retrieving addresses");
            }

        }

        pointsTextView = findViewById(R.id.pointsTextView);
        possiblePointsTv = findViewById(R.id.possiblePointsTv);

        //get user settings
        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getApplicationContext(), "notExistent");

        //get user enrolled campaigns
        campaignArrayList = userSettingStateWrapper.getUserSettings().getCampaigns();

        if(campaignArrayList == null){
            userSettingStateWrapper.getUserSettings().setCampaigns(new ArrayList<Campaign>());
        }

        // instantiate first fragment of the trip validation sequence
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = LegValidationFragment.newInstance(fullTripID);
        ft.replace(R.id.my_trips_main_fragment, fragment);
        ft.commit();
    }

    public ArrayList<Campaign> getCampaignList(){
        return campaignArrayList;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * tries to retrieve arrival address and departure address and sets these on the trip
     * fullTripBeingValidated
     *
     * @param departure departure location
     * @param arrival arrival location
     * @param fullTripBeingValidated current trip being validated
     */
     private void retrieveAndSetPlaces(LatLng departure, LatLng arrival, FullTrip fullTripBeingValidated) {

         String fromPlace = null;
         String toPlace = null;


         fromPlace = getPlaceFromCoordinatesGeocoder(departure);
         toPlace = getPlaceFromCoordinatesGeocoder(arrival);


         if ((fromPlace != null) && (toPlace != null)) {

             fullTripBeingValidated.setStartAddress(fromPlace);
             fullTripBeingValidated.setFinalAddress(toPlace);

             Log.d("MyTripsActivity", "Retrieved places text successfully");

         }else{
             Log.d("MyTripsActivity", "Unable to retrieve places");
         }

     }


    /**
     * @param latLng
     * @return address corresponding to the location provided (latLng)
     */
     public String getPlaceFromCoordinatesGeocoder(LatLng latLng){

         try {
             Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
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

    /**
     * opens edit trip dialog (merge, split, delete option)
     */
    private void showEditTripDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
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
                startActivity(createSplitsLegsIntent());
                //call method on fragment to show add activities dialog
            }
        });

        mergeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(createMergeLegsIntent());
                //call method on fragment to show add activities dialog
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(createDeleteLegsIntent());
                //call method on fragment to show add activities dialog
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void updateScore(int score){

        pointsTextView.setText(score + " pts");

    }

    public void updatePossibleScore(int score){
        possiblePointsTv.setText("+" + score + " /");
    }

    /**
     * compute current trip score and update the score textview
     */
    public void computeAndUpdateTripScore(){

        updateScore(fullTripToBeValidated.computeTripScore(getApplicationContext()));

    }


    /**
     * updates campaigns scores and saves them persistently.
     *
     * @param prevScore score for each campaign for the trip (trip might have been already validated)
     * @param newScore score to be assigned to each campaign by the trip (after validating/revalidating)
     * @return if any reward was completed by this update, returns these rewards
     */
     public ArrayList<RewardData>
     updateGlobalScoreForCampaignsAndSave(HashMap<String, Integer> prevScore, HashMap<String, Integer> newScore){


        ArrayList<RewardData> completedRewards = new ArrayList<>();

         for (Map.Entry<String, Integer> newScoreOfTripForCampaign : newScore.entrySet()) {

             String campaignID = newScoreOfTripForCampaign.getKey();
             Integer newScoreValue = newScoreOfTripForCampaign.getValue();

             //check if a score for this had previously been added to the campaign score, if so subtract
             //previous score and add the new one
             if (prevScore.containsKey(newScoreOfTripForCampaign.getKey())){
                CampaignScore.updateCampaignScore(prevScore.get(campaignID), newScoreValue, campaignID, userSettingStateWrapper.getUserSettings().getPointsPerCampaign());
             }else{
                 CampaignScore.updateCampaignScore(0, newScoreValue, campaignID, userSettingStateWrapper.getUserSettings().getPointsPerCampaign());
                 completedRewards.addAll(RewardManager.getInstance().checkAndAssignPointsForTrip(campaignID, fullTripToBeValidated.getInitTimestamp(), getApplicationContext(), newScoreValue));
             }


         }

         SharedPreferencesUtil.writeOnboardingUserData(getApplicationContext(), userSettingStateWrapper, true);

         return completedRewards;

     }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.editButton:

                showEditTripDialog();

                break;
            case R.id.mapButton:

                Intent intent = new Intent(this, ShowCurrentTripActivity.class);
                intent.putExtra(keys.CURRENT_TRIP, fullTripID);
                startActivity(intent);

                break;
            case R.id.deleteButton:
                dialog.dismiss();
                startActivity(createDeleteLegsIntent());
                break;
            case R.id.mergeButton:
                dialog.dismiss();
                startActivity(createMergeLegsIntent());
                break;
            case R.id.splitButton:
                dialog.dismiss();
                startActivity(createSplitsLegsIntent());
                break;
            case R.id.backButton:
                onBackPressed();
                break;
        }

    }

    public Intent createDeleteLegsIntent(){
        Intent deleteIntent = new Intent(getApplicationContext(), DeleteSplitMergeLegsActivity.class);
        deleteIntent.putExtra(DeleteSplitMergeLegsActivity.keys.mode, DeleteSplitMergeLegsActivity.keys.DELETE_MODE);
        deleteIntent.putExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripToBeValidated.getDateId());

        return deleteIntent;
    }

    public Intent createMergeLegsIntent(){
        Intent mergeIntent = new Intent(getApplicationContext(), DeleteSplitMergeLegsActivity.class);
        mergeIntent.putExtra(DeleteSplitMergeLegsActivity.keys.mode, DeleteSplitMergeLegsActivity.keys.MERGE_MODE);
        mergeIntent.putExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripToBeValidated.getDateId());

        return mergeIntent;
    }

    public Intent createSplitsLegsIntent(){
        Intent splitIntent = new Intent(getApplicationContext(), DeleteSplitMergeLegsActivity.class);
        splitIntent.putExtra(MyTripsFragment.keys.FULLTRIP_DATE_TO_BE_VALIDATED, fullTripToBeValidated.getDateId());
        splitIntent.putExtra(DeleteSplitMergeLegsActivity.keys.mode, DeleteSplitMergeLegsActivity.keys.SPLIT_MODE);

        return splitIntent;
    }

    public interface keys{

        String CURRENT_TRIP = "currentTrip";

    }

}

package inesc_id.pt.motivandroid.settingsAndProfile;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Locale;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.managers.RewardManager;
import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.data.userSettingsData.Language;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettings;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.home.fragments.ProfileAndSettingsFragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding11Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding12Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding14Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding15Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding16Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding5Fragment;
import inesc_id.pt.motivandroid.onboarding.fragments.Onboarding6_7Fragment;
import inesc_id.pt.motivandroid.onboarding.wrappers.ModeOfTransportUsed;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.campaigns.CampaignMenuFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.campaigns.CampaignScoresFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.EditProfileFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.SetLanguageFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.campaigns.TargetAndRewardsFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.demographicInfo.DemographicInfoFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.demographicInfo.GeneralDemographicInfoFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.demographicInfo.HouseholdDemographicInfoFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.demographicInfo.WorthwhilnessSettingsFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.setHomeAndWork.SetHomeAndWorkFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.setHomeAndWork.SetHomeOrWorkFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.tutorials.TutorialSettingsFragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.tutorials.worthwhilenessTutorial.WhatIsWorthwhileness2Fragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.tutorials.worthwhilenessTutorial.WhatIsWorthwhileness3Fragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.tutorials.worthwhilenessTutorial.WhatIsWorthwhileness4Fragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.tutorials.worthwhilenessTutorial.WhatIsWorthwhileness5Fragment;
import inesc_id.pt.motivandroid.settingsAndProfile.fragments.tutorials.worthwhilenessTutorial.WhatÍsWorthwhileness1Fragment;

/**
 * ProfileAndSettingsActivity
 *
 * Profile and settings main activity.
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

public class ProfileAndSettingsActivity extends AppCompatActivity implements SetHomeAndWorkFragment.OnFragmentInteractionListener,
                                                                                SetHomeOrWorkFragment.OnFragmentInteractionListener,
                                                                                    DemographicInfoFragment.OnFragmentInteractionListener,
                                                                                        GeneralDemographicInfoFragment.OnFragmentInteractionListener,
                                                                                            WorthwhilnessSettingsFragment.OnFragmentInteractionListener,
                                                                                                View.OnClickListener,
                                                                                                    TutorialSettingsFragment.OnFragmentInteractionListener,
                                                                                                        WhatÍsWorthwhileness1Fragment.OnFragmentInteractionListener,
                                                                                                            WhatIsWorthwhileness2Fragment.OnFragmentInteractionListener,
                                                                                                                WhatIsWorthwhileness3Fragment.OnFragmentInteractionListener,
                                                                                                                    WhatIsWorthwhileness4Fragment.OnFragmentInteractionListener,
                                                                                                                        WhatIsWorthwhileness5Fragment.OnFragmentInteractionListener,
                                                                                                                            SetLanguageFragment.OnFragmentInteractionListener,
                                                                                                                                EditProfileFragment.OnFragmentInteractionListener,
                                                                                                                                    Onboarding11Fragment.OnFragmentInteractionListener,
                                                                                                                                    Onboarding5Fragment.OnFragmentInteractionListener,
                                                                                                                                    Onboarding6_7Fragment.OnFragmentInteractionListener,
                                                                                                                                    Onboarding12Fragment.OnFragmentInteractionListener,
                                                                                                                                    Onboarding14Fragment.OnFragmentInteractionListener,
                                                                                                                                        Onboarding15Fragment.OnFragmentInteractionListener,
                                                                                                                                        Onboarding16Fragment.OnFragmentInteractionListener,
                                                                                                                                        HouseholdDemographicInfoFragment.OnFragmentInteractionListener,
                                                                                                                                            CampaignScoresFragment.OnFragmentInteractionListener, CampaignMenuFragment.OnFragmentInteractionListener, TargetAndRewardsFragment.OnFragmentInteractionListener {

    public TextView titleTextView;
    public ImageButton backButton;

    ConstraintLayout headerLayout;

    UserSettingStateWrapper userSettingStateWrapper;

    public final static int SETTINGS_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_settings);

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(getApplicationContext(), "notExistent");

        titleTextView = (TextView) findViewById(R.id.profileAndSettingsTitleTextView);

        headerLayout = (ConstraintLayout) findViewById(R.id.headerProfileHomeLayout);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        /**
         * check if we need to forward the user to one of the sub menus of the profile and settings
         */
        int selectedOption = getIntent().getIntExtra(ProfileAndSettingsFragment.keys.selectedOptionKey, R.string.Set_Home_And_Work);

        Fragment fragment = null;
        Class fragmentClass = null;

        switch (selectedOption){
            case R.string.Set_Home_And_Work:
                fragmentClass = SetHomeAndWorkFragment.class;
                break;
            case R.string.Demographic_Info:
                fragmentClass = DemographicInfoFragment.class;
                break;
            case R.string.Worthwhileness_Settings:
                fragmentClass = WorthwhilnessSettingsFragment.class;
                break;
            case R.string.Tutorials:
                fragmentClass = TutorialSettingsFragment.class;
                break;
            case R.string.App_Language:
                fragmentClass = SetLanguageFragment.class;
                break;
            case R.string.Edit_Profile:
                fragmentClass = EditProfileFragment.class;
                break;
            case R.string.Transport_Preferences:
                fragment = (Fragment) Onboarding5Fragment.newInstance(SETTINGS_TAG);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragment).commit();
                return;
            case R.string.Log_Out:
                break;
            case R.string.Campaigns:
                fragmentClass = CampaignMenuFragment.class;

                break;

        }

        try {

            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.profile_and_settings_main_fragment, fragment).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     *
     *  assign points to campaigns (reward for filling in profile information) and save changes
     * persistently
     *
     * @param score to add to the campaigns
     * @return
     */
    public ArrayList<RewardData>
    updateGlobalScoreForCampaignsAndSave(int score){

        Log.e("Profile", "UpdateGlobal score for campaigns and save");

        ArrayList<Campaign> campaignArrayList = CampaignManager.getInstance(getApplicationContext()).getCampaigns();
        Log.e("Profile", "campaign size " + campaignArrayList.size());

        ArrayList<RewardData> completedRewards = new ArrayList<>();

        for (Campaign campaignToGivePoints : campaignArrayList){

            Log.e("Profile", "Checking campaign " + campaignToGivePoints.getCampaignID());

            CampaignScore.updateCampaignScore(0, score, campaignToGivePoints.getCampaignID(), userSettingStateWrapper.getUserSettings().getPointsPerCampaign());
            completedRewards.addAll(RewardManager.getInstance().checkAndAssignPoints(campaignToGivePoints.getCampaignID(), DateTime.now().getMillis(), getApplicationContext(), score));
        }

        SharedPreferencesUtil.writeOnboardingUserData(getApplicationContext(), userSettingStateWrapper, true);

        return completedRewards;

    }

    @Override
    protected void onResume() {
        super.onResume();

        userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(this, "notExistent");
    }

    public void setTitleTextView(String title){
        titleTextView.setText(title);
    }

    public void setHeaderColor(int color){

        headerLayout.setBackgroundColor(getResources().getColor(color));

    }

    /**
     *
     * methods for updating profile info
     *
     */

    public void saveAge(int minAge, int maxAge){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();
            userSettings.setMinAge(minAge);
            userSettings.setMaxAge(maxAge);
        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public void saveGender(String gender){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();
            userSettings.setGender(gender);
        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public void saveCountry(String country){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();
            userSettings.setCountry(country);
        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public void saveCity(String city){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();
            userSettings.setCity(city);
        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public void saveDegree(String degree){

        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();

            if(isUnfilled(userSettings.getDegree())){
                updateGlobalScoreForCampaignsAndSave(25);
//                needsToAssignPoints = true;
            }

            userSettings.setDegree(degree);
        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }

    }

    public boolean isUnfilled(String toCheck){

        if ( toCheck == null || toCheck.equals("") ){

            return true;

        }
        return false;
    }

    public void saveRegularModes(ArrayList<ModeOfTransportUsed> modes){

        try {
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();
            userSettings.setPreferedModesOfTransport(modes);
            SharedPreferencesUtil.writeOnboardingUserData(getApplicationContext(), userSettingStateWrapper, true);
        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }

    }

    public void saveMaritalStatus(String status){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();

            if(isUnfilled(userSettings.getMaritalStatusHousehold())){
                updateGlobalScoreForCampaignsAndSave(25);
            }

            userSettings.setMaritalStatusHousehold(status);

        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public void savePeopleHousehold(String status){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();

            if(isUnfilled(userSettings.getNumberPeopleHousehold())){
                updateGlobalScoreForCampaignsAndSave(25);
            }

            userSettings.setNumberPeopleHousehold(status);

        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public void saveYearsResidence(String status){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();

            if(isUnfilled(userSettings.getYearsOfResidenceHousehold())){
                updateGlobalScoreForCampaignsAndSave(25);
            }

            userSettings.setYearsOfResidenceHousehold(status);


        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public void saveLabourStatus(String status){
        try{
            UserSettings userSettings = userSettingStateWrapper.getUserSettings();

            if(isUnfilled(userSettings.getLabourStatusHousehold())){
                updateGlobalScoreForCampaignsAndSave(25);
            }

            userSettings.setLabourStatusHousehold(status);


        }catch(Exception e){
            Log.e("ProfileAndSettings", "User Settings Not Defined");
        }
    }

    public UserSettingStateWrapper getUserSettingStateWrapper(){
        return this.userSettingStateWrapper;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backButton:
                onBackPressed();
                break;
        }
    }
}

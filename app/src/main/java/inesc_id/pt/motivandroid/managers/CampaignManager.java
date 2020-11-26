package inesc_id.pt.motivandroid.managers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.Campaign;
import inesc_id.pt.motivandroid.data.pointSystem.CampaignScore;
import inesc_id.pt.motivandroid.data.userSettingsData.UserSettingStateWrapper;
import inesc_id.pt.motivandroid.persistence.SharedPreferencesUtil;

/**
 *
 * CampaignManager
 *
 *   Singleton class to manage campaign data.
 *   Checks if there are any pending notifications to se sent to the user, and if so show the
 *   notification.
 *   Also checks if there are any surveys that should already have been triggered, and if so trigger
 *   them.
 *   Checks as well if there is a story that needs to made available to the user.
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

public class CampaignManager {

    private static CampaignManager instance;

    private ArrayList<Campaign> campaigns;

    public synchronized static CampaignManager getInstance(Context context) {

        if(instance == null){
            instance = new CampaignManager(context);
        }

        return instance;
    }

    private CampaignManager(Context context) {

        refreshCampaigns(context);
    }

    /**
     * refresh user campaigns data from user user data
     *
     * @param context
     */
    public synchronized void refreshCampaigns(Context context){

        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, "notExistent");

        campaigns = new ArrayList<>();

        if ((userSettingStateWrapper != null) && (userSettingStateWrapper.getUid().equals(FirebaseAuth.getInstance().getUid()))) {

            ArrayList<Campaign> userCampaigns = userSettingStateWrapper.getUserSettings().getCampaigns();


            if(userCampaigns != null && userCampaigns.size() > 0){

                campaigns.addAll(userCampaigns);
            }

            Log.d("CampaignManager", "Campaign size " + campaigns.size());
        }else{
            campaigns = new ArrayList<>();
            Log.e("CampaignManager", "Onboarding not from user " + campaigns.size());

        }

        if (campaigns.size() == 0){

            Log.d("CampaignManager", "Campaign size == 0, returning list with only dummy campaign");

            campaigns.add(Campaign.getDummyCampaign());

        }
    }

    /**
     * check which campaigns should be awarded points
     *
     * @param surveyTargetedCampaigns
     * @return list of campaigns that should be awarded points
     */
    public ArrayList<Campaign> getCampaignIDsToPointSurveys(ArrayList<String> surveyTargetedCampaigns){

        ArrayList<Campaign> result = new ArrayList<>();

        if(surveyTargetedCampaigns == null || surveyTargetedCampaigns.size() == 0){
            Log.e("CampaignManager", "Either the survey does not target any campaigns or array is still null");
            result.add(Campaign.getDummyCampaign());
            return result;
        }

        for (String targetedCampaignID : surveyTargetedCampaigns){

            for (Campaign campaign : campaigns){

                if (targetedCampaignID.equals(campaign.getCampaignID())){
                    result.add(campaign);
                    Log.e("CampaignManager", "Adding campaign to be awarded points: " + campaign.getCampaignID() + " " + campaign.getName());
                    break;
                }

            }
        }

        if (result.size() == 0){
            result.add(Campaign.getDummyCampaign());
            Log.e("CampaignManager", "No campaigns...awarding points to the dummy campaign");
        }

        return result;
    }

    public String getCampaignName(String campaignID){

        for (Campaign campaign : campaigns){
            if (campaign.getCampaignID().equals(campaignID)){
                return campaign.getName();
            }
        }
        return null;
    }

    /**
     * @param campaignIDToCheck
     * @param context
     * @return user current campaign score for the provided campaignIDToCheck campaign
     */
    public int getCampaignScore(String campaignIDToCheck, Context context){

        if (campaigns == null || campaigns.size() == 0){
            return 0;
        }

        UserSettingStateWrapper userSettingStateWrapper = SharedPreferencesUtil.readOnboardingUserData(context, "");

        if (userSettingStateWrapper == null || userSettingStateWrapper.getUserSettings() == null || userSettingStateWrapper.getUserSettings().getPointsPerCampaign() == null){
            return 0;
        }

        ArrayList<CampaignScore> userCampaignScores = userSettingStateWrapper.getUserSettings().getPointsPerCampaign();

        for(CampaignScore campaignScore : userCampaignScores){

            if (campaignScore.getCampaignID().equals(campaignIDToCheck)){
                return campaignScore.getCampaignScore();
            }

        }

        return 0;
    }

    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }
}

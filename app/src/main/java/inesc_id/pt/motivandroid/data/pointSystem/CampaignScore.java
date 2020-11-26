package inesc_id.pt.motivandroid.data.pointSystem;

import android.util.Log;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 *
 * LegScored
 *
 *  Class to represent the score of one campaign
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
public class CampaignScore {

    @Expose
    String campaignID;

    @Expose
    int campaignScore;

    public CampaignScore(String campaignID, int campaignScore) {
        this.campaignID = campaignID;
        this.campaignScore = campaignScore;
    }

    public String getCampaignID() {
        return campaignID;
    }

    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    public int getCampaignScore() {
        return campaignScore;
    }

    public void setCampaignScore(int campaignScore) {
        this.campaignScore = campaignScore;
    }

    public static void updateCampaignScore(int prevScore, int newScore, String campaignID, ArrayList<CampaignScore> campaignScores){

        Log.d("CampaignScore", "Updating for trip");
        Log.d("CampaignScore", "CampaignID to update " + campaignID);


        for (CampaignScore cs : campaignScores){

            if(cs.getCampaignID() != null && cs.getCampaignID().equals(campaignID)){

                if (cs.getCampaignScore() - prevScore + newScore <= 0){
                    cs.setCampaignScore(0);
                    Log.d("CampaignScore", "Campaign already scored " + campaignID + " - below points - setting points 0");
                    return;
                }else{
                    cs.setCampaignScore(cs.getCampaignScore() - prevScore + newScore);
                    Log.d("CampaignScore", "Campaign already scored " + campaignID + " -  setting points "+ cs.getCampaignScore());

                    return;
                }

            }

        }

        Log.d("CampaignScore", "campaign not yet scored. Adding entry" + campaignID + " points " + newScore);
        //if it reaches this line, it means there was no score yet for this campaign
        campaignScores.add(new CampaignScore(campaignID, newScore));

        return;
    }

    public static void updateCampaignScoreForSurvey(int pointsToAdd, String campaignID, ArrayList<CampaignScore> campaignScores){

        Log.d("CampaignScore", "Updating for survey");
        Log.d("CampaignScore", "CampaignID to update " + campaignID);

        for (CampaignScore cs : campaignScores){

            if(cs.getCampaignID() != null && cs.getCampaignID().equals(campaignID)){
                Log.d("CampaignScore", "Campaign scored already. Adding entry" + campaignID + " points " + pointsToAdd);
                cs.setCampaignScore(cs.getCampaignScore() + pointsToAdd);
                return;
            }

        }

        Log.d("CampaignScore", "campaign not yet scored. Adding entry" + campaignID + " points " + pointsToAdd);

        //if it reaches this line, it means there was no score yet for this campaign
        campaignScores.add(new CampaignScore(campaignID, pointsToAdd));
        return;

    }



}

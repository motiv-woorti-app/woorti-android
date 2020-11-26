package inesc_id.pt.motivandroid.data.pointSystem;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import inesc_id.pt.motivandroid.managers.CampaignManager;
import inesc_id.pt.motivandroid.data.Campaign;

/**
 *
 * Scored
 *
 * Abstract class to represent an abstract score
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
public abstract class Scored implements Serializable{

    private HashMap<String, ArrayList<Score>> scores;

    protected ArrayList<Campaign> campaignList;

    public Scored(Context context){

        scores = new HashMap<>();

        campaignList = CampaignManager.getInstance(context).getCampaigns();

    }

    // get score for all campaigns
    public int getScore(){

        int result = 0;

        for(ArrayList<Score> arrayListScore : scores.values()){

            for(Score score : arrayListScore) {
                result += score.getValue();
            }

        }

        return result;
    }



    // get total score for the specified campaign
    public int getScoreForDefaultCampaign(){

        int result = 0;

        ArrayList<Score> scoresForCampaign = scores.get(campaignList.get(0).getCampaignID());

        if(scoresForCampaign != null){

            for (Score score : scoresForCampaign){
                result += score.getValue();
            }

        }
        return result;
    }

    // update score value for a single entry
    public void updateScoreValue(String campaignID, String id, int value){


        if(scores.get(campaignID) != null){

            boolean exists = false;

            for (Score score : scores.get(campaignID)){

                if (score.getId().equals(id)){
                    score.setValue(value);
                    exists = true;
                }
            }

            if(!exists){
                scores.get(campaignID).add(new Score(id, value));
            }

        }else{

            ArrayList<Score> toPut = new ArrayList<>();
            toPut.add(new Score(id, value));
            scores.put(campaignID, toPut);

        }

    }

    public HashMap<String, Integer> getResultScores(){

        HashMap<String, Integer> result = new HashMap<>();

        for (Map.Entry<String, ArrayList<Score>> entry : scores.entrySet()) {

            Integer eachCampaignResult = 0;

            String key = entry.getKey();
            ArrayList<Score> value = entry.getValue();

            for (Score eachCampaignScore : value){
                eachCampaignResult += eachCampaignScore.getValue();
            }

            result.put(key, eachCampaignResult);

        }

        return result;

    }

    public int getPossibleAllInfoPoints(){
        for(Campaign campaign: campaignList){
            return campaign.getPointsAllInfo();
        }
        return 0;
    }

    public int getPossiblePurposePoints(){
        for(Campaign campaign: campaignList){
            return campaign.getPointsTripPurpose();
        }
        return 0;
    }

    public int getPossibleWorthPoints(){
        for(Campaign campaign: campaignList){
            return campaign.getPointsWorth();
        }
        return 0;
    }

    public int getPossibleActivitiesPoints(){
        for(Campaign campaign: campaignList){
            return campaign.getPointsActivities();
        }
        return 0;
    }

    public int getPossibleTransportPoints(){
        for(Campaign campaign: campaignList){
            return campaign.getPointsTransportMode();
        }
        return 0;
    }



}

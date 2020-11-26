package inesc_id.pt.motivandroid.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;

import java.util.ArrayList;

import inesc_id.pt.motivandroid.auth.FirebaseTokenManager;
import inesc_id.pt.motivandroid.data.rewards.RewardData;
import inesc_id.pt.motivandroid.data.rewards.RewardStatus;
import inesc_id.pt.motivandroid.data.rewards.RewardStatusServer;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.utils.DateHelper;

/**
 *
 * RewardManager
 *
 *   Singleton class to manage user rewards.
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

public class RewardManager {

    private static RewardManager instance;

    ArrayList<RewardData> rewardDataArrayList = new ArrayList<>();

    int daysWithTripsAllTime;

    int tripsAllTime;

    public synchronized static RewardManager getInstance(){
        if(instance == null){
            instance = new RewardManager();
        }

        return instance;
    }

    public synchronized ArrayList<RewardData> getRewardDataArrayList() {

        return rewardDataArrayList;
    }

    public synchronized ArrayList<RewardData> getValidRewardsDataArrayList(){

        ArrayList<RewardData> result = new ArrayList<>();

        for (RewardData rewardData : rewardDataArrayList){

            if(!rewardData.isRemoved()){
                result.add(rewardData);
            }

        }

        return result;

    }


    /**
     * assigns points to the rewards and checks which rewards were completed due to this assignement
     *
     * @param campaignID
     * @param tripTS
     * @param context
     * @param scoreToAssign
     *
     * @return rewards that were completed due to this assignment
     */
    public synchronized ArrayList<RewardData> checkAndAssignPoints(String campaignID, long tripTS, Context context, int scoreToAssign){

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        String uid;

        if (FirebaseAuth.getInstance().getUid() != null){
            uid = FirebaseAuth.getInstance().getUid();
        }else{
            uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
        }

        Log.e("RewardManager", "checkAndAssignPointsForTrip" + "uid" + uid);

        ArrayList<RewardData> rewardNamesIfCompleted = new ArrayList<>();

        for (RewardData rewardData : getRewardsTargettingCampaign(campaignID)){

            Log.e("RewardManager", "inside for checking reward " + rewardData.getRewardId() + " campaignID " + campaignID);


            if ((rewardData.getStartDate() < tripTS) && (rewardData.getEndDate() > tripTS) && (!rewardData.isRemoved())){

                Log.e("RewardManager", "Reward date valid and not removed");


                RewardStatus rewardStatus = getRewardStatus(context, rewardData.getRewardId());

                switch (rewardData.getTargetType()){

                    case RewardData.keys.POINTS:

                        Log.e("RewardManager", "POINTS reward");

                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if ((rewardStatus.getCurrentValue() < rewardData.getTargetValue())
                                    &&
                                    (rewardStatus.getCurrentValue() + scoreToAssign >= rewardData.getTargetValue()))
                            {
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            rewardStatus.setCurrentValue(rewardStatus.getCurrentValue() + scoreToAssign);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion() +1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), scoreToAssign, new ArrayList<Long>(), 1, false, false);

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if (newRewardStatus.getCurrentValue() >= rewardData.getTargetValue()){
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                    case RewardData.keys.POINTS_ALL_TIME:

                        int campaignScore = CampaignManager.getInstance(context).getCampaignScore(rewardData.getTargetCampaignId(), context);

                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            rewardStatus.setCurrentValue(campaignScore);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion()+1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), campaignScore, new ArrayList<Long>(), 1, false, false);

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                }



            }

        }

        return rewardNamesIfCompleted;

    }

    /**
     * assigns points to the rewards and checks which rewards were completed due to this assignement
     * (specific for trip validation)
     *
     * @param campaignID
     * @param tripTS
     * @param context
     * @param scoreToAssign
     *
     * @return rewards that were completed due to this assignment
     */    public synchronized ArrayList<RewardData> checkAndAssignPointsForTrip(String campaignID, long tripTS, Context context, int scoreToAssign){

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        String uid;

        if (FirebaseAuth.getInstance().getUid() != null){
            uid = FirebaseAuth.getInstance().getUid();
        }else{
            uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
        }

        Log.e("RewardManager", "checkAndAssignPointsForTrip" + "uid" + uid);

        ArrayList<RewardData> rewardNamesIfCompleted = new ArrayList<>();

        for (RewardData rewardData : getRewardsTargettingCampaign(campaignID)){

            Log.e("RewardManager", "inside for checking reward " + rewardData.getRewardId() + " campaignID " + campaignID);

            if ((rewardData.getStartDate() < tripTS) && (rewardData.getEndDate() > tripTS) && (!rewardData.isRemoved())){

                Log.e("RewardManager", "Reward date valid and not removed");


                RewardStatus rewardStatus = getRewardStatus(context, rewardData.getRewardId());

                switch (rewardData.getTargetType()){

                    case RewardData.keys.POINTS:

                        Log.e("RewardManager", "POINTS reward");

                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if ((rewardStatus.getCurrentValue() < rewardData.getTargetValue())
                                    &&
                                    (rewardStatus.getCurrentValue() + scoreToAssign >= rewardData.getTargetValue()))
                            {
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            rewardStatus.setCurrentValue(rewardStatus.getCurrentValue() + scoreToAssign);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion() +1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), scoreToAssign, new ArrayList<Long>(), 1, false, false);

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if (newRewardStatus.getCurrentValue() >= rewardData.getTargetValue()){
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                    case RewardData.keys.DAYS:

                        if (rewardStatus != null){

                            boolean doInsert = true;

                            for (long timestampOfPrevDaysWithTrips : rewardStatus.getTimestampsOfDaysWithTrips()){

                                if(DateHelper.isSameDay(tripTS, timestampOfPrevDaysWithTrips)){
                                    doInsert = false;
                                    break;
                                }

                            }

                            if (doInsert){

                                //if reward had not been completed before, and is completed now, add to the "return array"
                                if ((rewardStatus.getCurrentValue() < rewardData.getTargetValue())
                                        && (rewardStatus.getCurrentValue() + 1 >= rewardData.getTargetValue())){
                                    rewardNamesIfCompleted.add(rewardData);
                                }

                                rewardStatus.getTimestampsOfDaysWithTrips().add(tripTS);
                                rewardStatus.setCurrentValue(rewardStatus.getCurrentValue() + 1);
                                rewardStatus.setRewardVersion(rewardStatus.getRewardVersion() +1);
                                rewardStatus.setHasBeenSentToServer(false);
                                persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);
                            }

                        }else{

                            ArrayList<Long> tsList = new ArrayList<>();
                            tsList.add(tripTS);

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), 1, tsList, 1, false, false);

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if (newRewardStatus.getCurrentValue() >= rewardData.getTargetValue()){
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                    case RewardData.keys.TRIPS:

                        if (rewardStatus != null){

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if ((rewardStatus.getCurrentValue() < rewardData.getTargetValue())
                                    && (rewardStatus.getCurrentValue() + 1 >= rewardData.getTargetValue())){
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            rewardStatus.setCurrentValue(rewardStatus.getCurrentValue() + 1);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion() +1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.insertOrReplaceRewardStatusObject(rewardStatus, uid);

                        }else{
                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), 1, new ArrayList<Long>(), 1, false, false);

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if (newRewardStatus.getCurrentValue() >= rewardData.getTargetValue()){
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                    case RewardData.keys.POINTS_ALL_TIME:

                        int campaignScore = CampaignManager.getInstance(context).getCampaignScore(rewardData.getTargetCampaignId(), context);

                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            rewardStatus.setCurrentValue(campaignScore);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion()+1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), campaignScore, new ArrayList<Long>(), 1, false, false);

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                         break;

                    case RewardData.keys.DAYS_ALL_TIME:


                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            rewardStatus.setCurrentValue(daysWithTripsAllTime);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion()+1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), daysWithTripsAllTime, new ArrayList<Long>(), 1, false, false);
                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                    case RewardData.keys.TRIPS_ALL_TIME:

                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            rewardStatus.setCurrentValue(tripsAllTime);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion()+1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), tripsAllTime, new ArrayList<Long>(), 1, false, false);
                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;
                }

            }

        }

        return rewardNamesIfCompleted;

    }

    /**
     * assigns points to the rewards and checks which rewards were completed due to this assignement
     * (specific for survey)
     * @param campaignID
     * @param context
     * @param scoreToAssign
     *
     * @return rewards that were completed due to this assignment
     */
    public synchronized ArrayList<RewardData> checkAndAssignPointsForSurvey(String campaignID, Context context, int scoreToAssign){

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        String uid;

        if (FirebaseAuth.getInstance().getUid() != null){
            uid = FirebaseAuth.getInstance().getUid();
        }else{
            uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
        }

        ArrayList<RewardData> rewardNamesIfCompleted = new ArrayList<>();

        Log.e("RewardManager", "checkAndAssignPointsForSurvey uid " + uid +  " campaignID " + campaignID);

        for (RewardData rewardData : getRewardsTargettingCampaign(campaignID)){

            Log.e("RewardManager", "inside for checking reward " + rewardData.getRewardId() );


            if ((rewardData.getStartDate() < DateTime.now().getMillis()) && (rewardData.getEndDate() > DateTime.now().getMillis()) && (!rewardData.isRemoved())){

                Log.e("RewardManager", "Reward date valid and not removed");


                RewardStatus rewardStatus = getRewardStatus(context, rewardData.getRewardId());

                switch (rewardData.getTargetType()){

                    case RewardData.keys.POINTS:

                        Log.e("RewardManager", "POINTS reward");

                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if (rewardStatus.getCurrentValue() + scoreToAssign > rewardData.getTargetValue()){
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            rewardStatus.setCurrentValue(rewardStatus.getCurrentValue() + scoreToAssign);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion() +1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), scoreToAssign, new ArrayList<Long>(), 1, false, false);

                            //if reward had not been completed before, and is completed now, add to the "return array"
                            if (newRewardStatus.getCurrentValue() > rewardData.getTargetValue()){
                                rewardNamesIfCompleted.add(rewardData);
                            }

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                    case RewardData.keys.POINTS_ALL_TIME:

                        int campaignScore = CampaignManager.getInstance(context).getCampaignScore(rewardData.getTargetCampaignId(), context);

                        if (rewardStatus != null){

                            Log.e("RewardManager", "Status != null");

                            rewardStatus.setCurrentValue(campaignScore);
                            rewardStatus.setRewardVersion(rewardStatus.getRewardVersion()+1);
                            rewardStatus.setHasBeenSentToServer(false);
                            persistentTripStorage.updateRewardStatusObject(rewardStatus, uid);

                        }else{

                            Log.e("RewardManager", "Status == null");

                            RewardStatus newRewardStatus = new RewardStatus(rewardData.getRewardId(), campaignScore, new ArrayList<Long>(), 1, false, false);

                            persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);
                        }

                        break;

                }



            }

        }

        return rewardNamesIfCompleted;

    }


    /**
     * set a reward as shown to the user and save this info persistently
     *
     * @param rewardID reward id of the reward to set as shown to the user
     * @param context
     */
    public synchronized void setRewardAsShown(String rewardID, Context context){

        synchronized (lockReward) {

            PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

            String uid;

            if (FirebaseAuth.getInstance().getUid() != null) {
                uid = FirebaseAuth.getInstance().getUid();
            } else {
                uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
            }

            Log.e("REWARD", "Trying to set reward as shown");

            RewardStatus rewardStatus = getRewardStatus(context, rewardID);

            if (rewardStatus != null) {

                Log.e("REWARD", "Reward being set as shown not null");

                rewardStatus.setRewardVersion(rewardStatus.getRewardVersion() + 1);
                rewardStatus.setHasBeenSentToServer(false);
                rewardStatus.setHasShownPopup(true);
                persistentTripStorage.insertOrReplaceRewardStatusObject(rewardStatus, uid);
            }

        }

    }


    /**
     * check which rewards target the specified campaignID campaign
     *
     * @param campaignID id of the campaign to be checked
     * @return
     */
    public ArrayList<RewardData> getRewardsTargettingCampaign(String campaignID){

        ArrayList<RewardData> result = new ArrayList<>();

        for (RewardData rewardRaw : rewardDataArrayList){

            if (rewardRaw.getTargetCampaignId().equals(campaignID)){
                result.add(rewardRaw);
            }

        }
        return result;
    }

    final Object lockReward = new Object();

    public void setRewardDataArrayList(ArrayList<RewardData> rewardDataArrayList) {

        synchronized (lockReward) {
            this.rewardDataArrayList = rewardDataArrayList;
        }
    }


    /**
     * @param context
     * @param rewardID
     * @return the reward status (user reward state) for the specified rewardID reward
     */
    public synchronized RewardStatus getRewardStatus(Context context, String rewardID){

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        String uid;

        if (FirebaseAuth.getInstance().getUid() != null){
            uid = FirebaseAuth.getInstance().getUid();
        }else{
            uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
        }

        return persistentTripStorage.getRewardStatus(uid, rewardID);

    }

    public void setAllTimeTripValues(int daysWithTripsAllTime, int tripsAllTime){

        this.daysWithTripsAllTime = daysWithTripsAllTime;
        this.tripsAllTime = tripsAllTime;

    }

    /**
     * save persistently the reward statuses retrieved from the server
     *
     * @param rewardStatusesServer reward statuses retrieved from server
     * @param context
     */
    public synchronized void saveRewardStatusesFromServer(ArrayList<RewardStatusServer> rewardStatusesServer, Context context) {

        synchronized (lockReward) {

            PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

            String uid;

            if (FirebaseAuth.getInstance().getUid() != null) {
                uid = FirebaseAuth.getInstance().getUid();
            } else {
                uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
            }

            for (RewardStatusServer rewardStatusServer : rewardStatusesServer) {
                persistentTripStorage.insertOrReplaceRewardStatusObject(new RewardStatus(rewardStatusServer), uid);
            }

            Intent localIntent = new Intent(keys.rewardsStatusRefreshedBroadcastKey);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        }
    }

    public static long oneDayInMilliseconds = 86400000;


    /**
     * check if a reward status has already been created for all-time rewards, and if not, create it
     * and save it persistently
     *
     * @param context
     */
    public synchronized void  checkAndCreateRewardStatusesForAllTimeRewards(Context context){

        PersistentTripStorage persistentTripStorage = new PersistentTripStorage(context);

        String uid;

        if (FirebaseAuth.getInstance().getUid() != null){
            uid = FirebaseAuth.getInstance().getUid();
        }else{
            uid = FirebaseTokenManager.getInstance(context).getLastKnownUID();
        }

        for (RewardData rewardData :rewardDataArrayList){

            //plus one day
            if ((rewardData.getStartDate() < DateTime.now().getMillis()) && ((rewardData.getEndDate()+oneDayInMilliseconds) > DateTime.now().getMillis()) && (!rewardData.isRemoved())) {

                if (isAllTimeReward(rewardData)) {

                    RewardStatus rewardStatus = getRewardStatus(context, rewardData.getRewardId());

                    if (rewardStatus == null) {

                        RewardStatus newRewardStatus;

                        switch (rewardData.getTargetType()) {

                            case RewardData.keys.POINTS_ALL_TIME:

                                int campaignScore = CampaignManager.getInstance(context).getCampaignScore(rewardData.getTargetCampaignId(), context);

                                Log.e("RewardManager", "Status == null");
                                newRewardStatus = new RewardStatus(rewardData.getRewardId(), campaignScore, new ArrayList<Long>(), 1, false, false);
                                persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);

                                break;

                            case RewardData.keys.DAYS_ALL_TIME:


                                Log.e("RewardManager", "Status == null");

                                newRewardStatus = new RewardStatus(rewardData.getRewardId(), daysWithTripsAllTime, new ArrayList<Long>(), 1, false, false);
                                persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);


                                break;

                            case RewardData.keys.TRIPS_ALL_TIME:

                                Log.e("RewardManager", "Status == null");
                                newRewardStatus = new RewardStatus(rewardData.getRewardId(), tripsAllTime, new ArrayList<Long>(), 1, false, false);
                                persistentTripStorage.insertOrReplaceRewardStatusObject(newRewardStatus, uid);

                                break;


                        }


                    }

                }
            }

        }

    }

    public boolean isAllTimeReward(RewardData rewardData){

        switch (rewardData.getTargetType()){

            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public interface keys{

        String rewardsStatusRefreshedBroadcastKey = "RewardBroadcastKey";

    }

    public int getDaysWithTripsAllTime() {
        return daysWithTripsAllTime;
    }

    public void setDaysWithTripsAllTime(int daysWithTripsAllTime) {
        this.daysWithTripsAllTime = daysWithTripsAllTime;
    }

    public int getTripsAllTime() {
        return tripsAllTime;
    }

    public void setTripsAllTime(int tripsAllTime) {
        this.tripsAllTime = tripsAllTime;
    }
}

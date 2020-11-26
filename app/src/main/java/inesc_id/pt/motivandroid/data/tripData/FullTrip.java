package inesc_id.pt.motivandroid.data.tripData;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import inesc_id.pt.motivandroid.data.pointSystem.TripScored;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigest;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.TripStats;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.TripObjectiveWrapper;
import inesc_id.pt.motivandroid.utils.DateHelper;

/**
 * FullTrip
 *
 * Trip data (including legs, trip metadata, scores for each campaign)
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

public class FullTrip implements Serializable{

    @SerializedName("trips")
    @Expose
    private ArrayList<FullTripPart> tripList;


    /**
     * start date in milliseconds
     */
    @SerializedName("startDate")
    @Expose
    private long initTimestamp;

    /**
     * end date in milliseconds
     */
    @SerializedName("endDate")
    @Expose
    private long endTimestamp;

    /**
     * distance in meters
     */
    @SerializedName("distance")
    @Expose
    private long distanceTraveled;

    /**
     * duration in milliseconds
     */
    @SerializedName("duration")
    @Expose
    private long timeTraveled;

    /**
     * average speed in km/s
     */
    @SerializedName("avSpeed")
    @Expose
    private float averageSpeed;

    /**
     * max speed in km/s
     */
    @SerializedName("mSpeed")
    @Expose
    private float maxSpeed;

    @SerializedName("model")
    @Expose
    private String smartphoneModel;

    @SerializedName("oS")
    @Expose
    private String operatingSystem;

    @SerializedName("oSVersion")
    @Expose
    private String oSVersion;

    @SerializedName("userID")
    @Expose
    private String userID;

    /**
     * false if the trip has not yet been sent to the server, true otherwise
     */
    @SerializedName("sentToServer")
    @Expose
    private boolean sentToServer;

    @SerializedName("countryInfo")
    @Expose
    private String country;

    @SerializedName("cityInfo")
    @Expose
    private String cityInfo;

    /**
     *  true if the trip has already been validated by the user (and therefore can be sent to the
     * server), false otherwise
     */
    @SerializedName("validated")
    @Expose
    private boolean validated;

    public TripStats getTripStats() {
        return tripStats;
    }

    public void setTripStats(TripStats tripStats) {
        this.tripStats = tripStats;
    }

    public TripScored getTripScored() {
        return tripScored;
    }

    @Expose
    private TripStats tripStats = null;

    public TripScored getTripScored(Context context) {

        if(tripScored == null){
            tripScored = new TripScored(context);
        }

        return tripScored;
    }

    public long getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(long validationDate) {
        this.validationDate = validationDate;
    }

    @Expose
    private long validationDate;

    public void setTripScored(TripScored tripScored) {
        this.tripScored = tripScored;
    }


    /**
     * keeps points/score assigned to the trip.
     */
    TripScored tripScored;


    /**
     *  keeps track of possible previous validation (if a user revalidates a trip, we must know what
     * was the previously assigned score)
     */
    @SerializedName("prevScoreCampaigns")
    @Expose
    public HashMap<String, Integer> prevScore = new HashMap<>();

    public HashMap<String, Integer> getPrevScore() {
        return prevScore;
    }

    public void setPrevScore(HashMap<String, Integer> prevScore) {
        this.prevScore = prevScore;
    }

    @SerializedName("objectives")
    @Expose
    private ArrayList<TripObjectiveWrapper> objectivesOfTheTrip;

    @SerializedName("startAddress")
    @Expose
    private String startAddress;

    @SerializedName("finalAddress")
    @Expose
    private String finalAddress;


    /**
     * "Overall, how did you feel about this trip?"
     *
     * 1 to 5 (stars)
     */
    @Expose
    private Integer overallScore;


    /**
     * "Did you have to arrive at a fixed time?"
     *
     * YES = 1
     * NO = 0
     * NOT_SURE_YES_OR_NO = -1
     */
    @Expose
    private Integer didYouHaveToArrive;

    /**
     * "How often do you make this trip?"
     *
     *  REGULARY = 0
     *  OCCASIONALLY = 1
     *  FIRST_TIME = 2
     *  NOT_SURE_HOW_OFTEN = -1
     *
     */
    @Expose
    private Integer howOften;

    /**
     * "Would you have liked to use your travel time more for ... ?"
     *
     * productivity = 0
     * enjoyment = 1
     * fitness = 2
     *
     */
    @Expose
    private Integer useTripMoreFor;

    /**
     * "Anything else you'd like to share?"
     */
    @Expose
    private String shareInformation;

    /**
     * amount of legs merged by the user
     */
    @Expose
    private int numMerges = 0;

    /**
     * amount of legs split by the user
     */
    @Expose
    private int numSplits = 0;

    /**
     * amount of legs deleted by the user
     */
    @Expose
    private int numDeletes = 0;

    public int getNumMerges() {
        return numMerges;
    }

    public void setNumMerges(int numMerges) {
        this.numMerges = numMerges;
    }

    public int getNumSplits() {
        return numSplits;
    }

    public void setNumSplits(int numSplits) {
        this.numSplits = numSplits;
    }

    public int getNumDeletes() {
        return numDeletes;
    }

    public void setNumDeletes(int numDeletes) {
        this.numDeletes = numDeletes;
    }

    public boolean isManualTripStart() {
        return manualTripStart;
    }

    public void setManualTripStart(boolean manualTripStart) {
        this.manualTripStart = manualTripStart;
    }

    public boolean isManualTripEnd() {
        return manualTripEnd;
    }

    public void setManualTripEnd(boolean manualTripEnd) {
        this.manualTripEnd = manualTripEnd;
    }


    /**
     * false if trip start was detected automatically by the app, true otherwise
     */
    @Expose
    private boolean manualTripStart = false;

    /**
     * false if trip end was detected automatically by the app, true otherwise
     */
    @Expose
    private boolean manualTripEnd = false;


    public FullTrip(ArrayList<FullTripPart> tripList, long initTimestamp, long endTimestamp, long distanceTraveled,
                    long timeTraveled, float averageSpeed, float maxSpeed, String smartphoneModel, String operatingSystem, String oSVersion, String userID, boolean sentToServer, boolean manualTripStart, boolean manualTripEnd) {
        this.tripList = tripList;
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.distanceTraveled = distanceTraveled;
        this.timeTraveled = timeTraveled;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.smartphoneModel = smartphoneModel;
        this.operatingSystem = operatingSystem;
        this.oSVersion = oSVersion;
        this.userID = userID;
        this.sentToServer = sentToServer;
        this.validated = false;
        this.manualTripStart = manualTripStart;
        this.manualTripEnd = manualTripEnd;
    }


    /**
     *
     * This method computes the score to assign from this trip
     *
     * @param context
     * @return hashmap (keys are campaign ids and values the points to assign to each of the
     *         campaigns)
     */
    public HashMap<String, Integer> computeTripScoreForAllCampaigns(Context context){

        HashMap<String, Integer> result = new HashMap<>();

        for(FullTripPart tripPart : tripList){

            //leg
            if(tripPart.isTrip()){

                if (((Trip) tripPart).legScored != null) {

                    for (Map.Entry<String, Integer> entry : ((Trip) tripPart).legScored.getResultScores().entrySet()) {

                        String campaignID = entry.getKey();
                        Integer campaignScore = entry.getValue();

                        if(result.containsKey(campaignID)){
                            Integer formerResult = result.get(campaignID);
                            Integer updatedResult = formerResult + campaignScore;
                            result.put(campaignID,updatedResult);
                        }else{
                            result.put(campaignID, campaignScore);
                        }

                    }

                }

            }else{  //waiting event

                if(((WaitingEvent) tripPart).waitingEventScored != null) {

                    for (Map.Entry<String, Integer> entry : ((WaitingEvent) tripPart).waitingEventScored.getResultScores().entrySet()) {

                        String campaignID = entry.getKey();
                        Integer campaignScore = entry.getValue();

                        if(result.containsKey(campaignID)){
                            Integer formerResult = result.get(campaignID);
                            Integer updatedResult = formerResult + campaignScore;
                            result.put(campaignID,updatedResult);
                        }else{
                            result.put(campaignID, campaignScore);
                        }

                    }

                }
            }

        }


        for (Map.Entry<String, Integer> entry : getTripScored(context).getResultScores().entrySet()) {

            String campaignID = entry.getKey();
            Integer campaignScore = entry.getValue();

//                        for (Score eachCampaignScore : value){
//                            eachCampaignResult += eachCampaignScore.getValue();
//                        }

            if(result.containsKey(campaignID)){
                Integer formerResult = result.get(campaignID);
                Integer updatedResult = formerResult + campaignScore;
                result.put(campaignID,updatedResult);
            }else{
                result.put(campaignID, campaignScore);
            }


        }

        return result;

    }

    public int computeTripScore(Context context){

        int result = 0;

        for(FullTripPart tripPart : tripList){

            if(tripPart.isTrip()){

                if (((Trip) tripPart).legScored != null) {
                    result += ((Trip) tripPart).legScored.getScoreForDefaultCampaign();
                }

            }else{

                if(((WaitingEvent) tripPart).waitingEventScored != null) {
                    result += ((WaitingEvent) tripPart).waitingEventScored.getScoreForDefaultCampaign();
                }
            }

        }

        result += getTripScored(context).getScoreForDefaultCampaign();

        return result;

    }

    public FullTrip() {
    }

    public ArrayList<FullTripPart> getTripList() {
        return tripList;
    }

    public void setTripList(ArrayList<FullTripPart> tripList) {
        this.tripList = tripList;
    }

    public long getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(long initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(long distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public long getTimeTraveled() {
        return timeTraveled;
    }

    public void setTimeTraveled(long timeTraveled) {
        this.timeTraveled = timeTraveled;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getSmartphoneModel() {
        return smartphoneModel;
    }

    public void setSmartphoneModel(String smartphoneModel) {
        this.smartphoneModel = smartphoneModel;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getoSVersion() {
        return oSVersion;
    }

    public void setoSVersion(String oSVersion) {
        this.oSVersion = oSVersion;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isSentToServer() {
        return sentToServer;
    }

    public void setSentToServer(boolean sentToServer) {
        this.sentToServer = sentToServer;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(String cityInfo) {
        this.cityInfo = cityInfo;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public ArrayList<TripObjectiveWrapper> getObjectivesOfTheTrip() {
        return objectivesOfTheTrip;
    }

    public void setObjectivesOfTheTrip(ArrayList<TripObjectiveWrapper> objectiveOfTheTrip) {
        this.objectivesOfTheTrip = objectiveOfTheTrip;
    }

//    public TripObjectiveWrapper getObjectiveOfTheTrip() {
//        return objectiveOfTheTrip;
//    }
//
//    public void setObjectiveOfTheTrip(TripObjectiveWrapper objectiveOfTheTrip) {
//        this.objectiveOfTheTrip = objectiveOfTheTrip;
//    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getFinalAddress() {
        return finalAddress;
    }

    public void setFinalAddress(String finalAddress) {
        this.finalAddress = finalAddress;
    }

    public String getInitialTextLocation(){
        if(getStartAddress() == null){
            return getDeparturePlace().getLatitude() + "," + getDeparturePlace().getLongitude();
        }else{
            return startAddress;
        }
    }

    public String getFinalTextLocation(){
        if(getFinalAddress() == null){
            return getArrivalPlace().getLatitude() + "," + getArrivalPlace().getLongitude();
        }else{
            return finalAddress;
        }
    }
    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public Integer getDidYouHaveToArrive() {
        return didYouHaveToArrive;
    }

    public void setDidYouHaveToArrive(Integer didYouHaveToArrive) {
        this.didYouHaveToArrive = didYouHaveToArrive;
    }

    public Integer getHowOften() {
        return howOften;
    }

    public void setHowOften(Integer howOften) {
        this.howOften = howOften;
    }

    public Integer getUseTripMoreFor() {
        return useTripMoreFor;
    }

    public void setUseTripMoreFor(Integer useTripMoreFor) {
        this.useTripMoreFor = useTripMoreFor;
    }

    public String getShareInformation() {
        return shareInformation;
    }

    public void setShareInformation(String shareInformation) {
        this.shareInformation = shareInformation;
    }


    public String getDescription(){

        StringBuilder sb = new StringBuilder();
        sb.append("Trip"+"\n");

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
        String initDate = sdf.format(this.getInitTimestamp());
        String endDate = sdf.format(this.getEndTimestamp());

        sb.append("Start Date: "+initDate +"\n");
        sb.append("End Date: "+endDate +"\n");
        sb.append("Distance traveled: "+this.getDistanceTraveled()+ "m" +"\n");
        sb.append("Time traveled: " + DateHelper.getHMSfromMS(this.getTimeTraveled()) +"\n");
        sb.append("Average speed: "+ this.getAverageSpeed() + "km/h"+"\n");
        sb.append("Maximum speed: "+ this.getMaxSpeed() + "km/h"+"\n");

        for(FullTripPart ftp: tripList){

            sb.append(ftp.getDescription());

        }

        return sb.toString();
    }

    public String toString(){

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
        String initDate = sdf.format(this.getInitTimestamp());

        return initDate;
    }

    public String getDateId(){
//        return DateHelper.getDateFromTSString(getTripList().get(0).getInitTimestamp());
          return initTimestamp+"";
    }

    public ArrayList<Trip> getAllLegs(){

        ArrayList<Trip> legList = new ArrayList<>();

        for(FullTripPart fullTripPart : tripList){
            if(fullTripPart.isTrip()) legList.add((Trip)fullTripPart);
        }

        return legList;
    }


    public LocationDataContainer getDeparturePlace(){

        Log.e("s", "size legs" + getAllLegs().size());

        for (FullTripPart ftp : getAllLegs()){

            Log.e("l", "lcds" + ftp.getLocationDataContainers().size());

        }

        return getAllLegs().get(0).getLocationDataContainers().get(0);
    }

    public LocationDataContainer getArrivalPlace(){
        return getAllLegs().get(getAllLegs().size()-1).getLocationDataContainers().get(getAllLegs().get(getAllLegs().size()-1).getLocationDataContainers().size()-1);
    }

    public boolean areAllLegsValidated(){

        ArrayList<Trip> legList = new ArrayList<>();

        for(FullTripPart fullTripPart : tripList){
            if(fullTripPart.isTrip()){

             if(((Trip)fullTripPart).getCorrectedModeOfTransport() == - 1) return false;

            }
        }

        return true;
    }

    public void updateLegActivities(int realIndex,
                                    ArrayList<ActivityLeg> prodActivityLegs,
                                    ArrayList<ActivityLeg> mindActivityLegs,
                                    ArrayList<ActivityLeg> bodyActivityLegs){

        getTripList().get(realIndex).setProdActivities(prodActivityLegs);
        getTripList().get(realIndex).setMindActivities(mindActivityLegs);
        getTripList().get(realIndex).setBodyActivities(bodyActivityLegs);

    }

    public void updateLegCorrectedModeOfTransport(ArrayList<FullTripPartValidationWrapper> modifiedTripPartList){

        for (FullTripPartValidationWrapper fullTripPartValidationWrapper : modifiedTripPartList){

            if(fullTripPartValidationWrapper.getFullTripPart() instanceof Trip){

                ((Trip) tripList.get(fullTripPartValidationWrapper.realIndex)).setCorrectedModeOfTransport(((Trip)fullTripPartValidationWrapper.getFullTripPart()).getCorrectedModeOfTransport());

            }

        }

    }

    public void updateRelaxingProductivity(ArrayList<FullTripPartValidationWrapper> modifiedTripPartList){

        for (FullTripPartValidationWrapper fullTripPartValidationWrapper : modifiedTripPartList){

            if(fullTripPartValidationWrapper.getFullTripPart() instanceof Trip || fullTripPartValidationWrapper.getFullTripPart() instanceof WaitingEvent){

                tripList.get(fullTripPartValidationWrapper.realIndex).setProductivityRelaxingRating(fullTripPartValidationWrapper.getFullTripPart().getProductivityRelaxingRating());
            }

        }

    }



    public FullTripDigest getFullTripDigest(){


            return new FullTripDigest(initTimestamp, endTimestamp, userID, sentToServer, validated, startAddress, finalAddress, getDeparturePlace(), getArrivalPlace(), getDateId(), getTripStats(), getDistanceTraveled());



    }


}
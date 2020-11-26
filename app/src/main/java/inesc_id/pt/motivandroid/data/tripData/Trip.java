package inesc_id.pt.motivandroid.data.tripData;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.ActivityDataContainer;
import inesc_id.pt.motivandroid.data.GeneratedFrom;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetectedWrapper;
import inesc_id.pt.motivandroid.data.pointSystem.LegScored;
import inesc_id.pt.motivandroid.utils.DateHelper;

/**
 * Trip
 *
 *   Subclass of FullTripPart. Data structure of a leg (name Trip deprecated)
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

public class Trip extends FullTripPart implements Serializable{

    @SerializedName("activityList")
    @Expose
    ArrayList<ActivityDataContainer> activityDataContainers;

    @SerializedName("modeOfTransport")
    @Expose
    int modality;

    @SerializedName("distance")
    @Expose
    long distanceTraveled;

    @SerializedName("duration")
    @Expose
    long timeTraveled;

    @SerializedName("avSpeed")
    @Expose
    float averageSpeed;

    @SerializedName("mSpeed")
    @Expose
    float maxSpeed;

    @SerializedName("accelerationMean")
    @Expose
    double accelerationAverage;

    @SerializedName("accelerations")
    @Expose
    ArrayList<AccelerationData> accelerationData;

    @SerializedName("wrongLeg")
    @Expose
    boolean isWrongLeg;

    /**
     * mode of transport corrected by the user. See ActivityDetected class for codes
     */
    @SerializedName("correctedModeOfTransport")
    @Expose
    int correctedModeOfTransport;

    @SerializedName("filteredAcceleration")
    @Expose
    double filteredAcceleration;

    @SerializedName("filteredAccelerationBelowThreshold")
    @Expose
    double filteredAccelerationBelowThreshold;

    @SerializedName("filteredSpeed")
    @Expose
    double filteredSpeed;

    @SerializedName("filteredSpeedBelowThreshold")
    @Expose
    double filteredSpeedBelowThreshold;

    @SerializedName("accuracyMean")
    @Expose
    float accuracyMean;

    /**
     * mode of transport originally detected by the system. See ActivityDetected class for codes
     */
    @SerializedName("detectedModeOfTransport")
    @Expose
    int suggestedModeOfTransport;

    /**
     * keeps score assigned to the leg
     */
    public LegScored legScored;

    public LegScored getLegScored(Context context) {

        if (legScored == null){
            legScored = new LegScored(context);
        }

        return legScored;
    }

    public void setLegScored(LegScored legScored) {
        this.legScored = legScored;
    }

    /**
     *  true distance is equal to the distance which the mode of transport was correctly identified
     * by the app. E.g. if this leg is a result of merging two legs, and if the system only identi-
     * fied the correct mode of one of those legs, the true distance of the resulting leg should be
     * the distance of the leg which was correctly identified.
     */
    @Expose
    public double trueDistance;

    /**
     * is this leg a result of merging two legs/waiting events
     */
    @Expose
    public boolean wasMerged;

    /**
     * was this leg splitted
     */
    @Expose
    public boolean wasSplitted;

    public void mergedFromLegs(Trip leg1, Trip leg2){

        this.wasMerged = true;

        this.originalLegs = new ArrayList<>();

        ArrayList<Trip> from = new ArrayList<>();

        from.add(leg1);
        from.add(leg2);

        for(Trip leg : from){

            if((leg.getOriginalLegs() != null)){


                for(GeneratedFrom origin : leg.originalLegs){
                        this.originalLegs.add(origin);
                }

            }else{
                    originalLegs.add(new GeneratedFrom(leg.getDistanceTraveled(), leg.correctedModeOfTransport));
            }
        }

//        setTrueDistance(getTrueDistance());
    }

    public void mergedWithWE(Trip leg, WaitingEvent we){

        this.wasMerged = true;
        this.originalLegs = new ArrayList<>();

        if(leg.originalLegs != null && leg.originalLegs.size() > 0){

            double distanceFromPrevious = 0;

            for(GeneratedFrom origin : leg.originalLegs){

                if(origin != leg.originalLegs.get(leg.originalLegs.size()-1)){

                    distanceFromPrevious += origin.getDistance();

                }

                this.originalLegs.add(origin);

            }

            this.originalLegs.get(this.originalLegs.size()-1).setDistance(this.distanceTraveled - distanceFromPrevious);

        }

//        setTrueDistance(getTrueDistance());

    }

    public void splittedFromLeg(Trip leg){

        this.wasSplitted = true;
        double currentLegDistance = this.distanceTraveled;

        this.originalLegs = new ArrayList<>();

        if (leg.getOriginalLegs() != null){

            for(GeneratedFrom origin : leg.getOriginalLegs()){

                double minDistance = Math.min(currentLegDistance, origin.getDistance());
                this.originalLegs.add(new GeneratedFrom(minDistance, origin.getModeOfTransport()));

                currentLegDistance -= minDistance;

                if(currentLegDistance == 0){
                    return;
                }

            }

        }else{

            this.originalLegs.add(new GeneratedFrom(currentLegDistance, leg.suggestedModeOfTransport));

        }
//        setTrueDistance(getTrueDistance());


    }

    public double getTrueDistance() {

        double trueDistance=0;

        if(originalLegs != null) {
            for (GeneratedFrom originalLeg : originalLegs) {

                if (ActivityDetectedWrapper.isGeneralMode(originalLeg.getModeOfTransport(), this.suggestedModeOfTransport)) {
//                if (originalLeg.getModeOfTransport() == this.suggestedModeOfTransport) {

                    trueDistance += originalLeg.getDistance();
                }
            }
        }

        if(trueDistance > 0) return trueDistance;

        if(ActivityDetectedWrapper.isGeneralMode(this.suggestedModeOfTransport, this.correctedModeOfTransport)){
//        if(this.suggestedModeOfTransport == this.correctedModeOfTransport){
//            this.trueDistance = this.distanceTraveled;
            return this.distanceTraveled;
        }

//        this.trueDistance = 0;
        return 0;
    }



    public void setTrueDistance(double trueDistance) {
        this.trueDistance = trueDistance;
    }

    public boolean isWasMerged() {
        return wasMerged;
    }

    public void setWasMerged(boolean wasMerged) {
        this.wasMerged = wasMerged;
    }

    public ArrayList<GeneratedFrom> getOriginalLegs() {
        return originalLegs;
    }

    public void setOriginalLegs(ArrayList<GeneratedFrom> originalLegs) {
        this.originalLegs = originalLegs;
    }

    /**
     *  if the leg is a result of a merge/split operation, this array keeps the history of the modes
     * of the original legs (in order to be able to compute the true distance)
     */
    @Expose
    public ArrayList<GeneratedFrom> originalLegs;

    //    todo uncomment for new trip structure
    @Expose
    private String otherMotText;

    public Trip(ArrayList<LocationDataContainer> locationDataContainers, ArrayList<ActivityDataContainer> activityDataContainers,
                int modality, long initTimestamp, long endTimestamp, long distanceTraveled, long timeTraveled, float averageSpeed, float maxSpeed,
                double accelerationAverage, ArrayList<AccelerationData> accelerationData,
                double filteredAcceleration, double filteredAccelerationBelowThreshold, double filteredSpeed, double filteredSpeedBelowThreshold,
                float accuracyMean, int suggestedModeOfTransport, int correctedModeOfTransport) {

        super(locationDataContainers, initTimestamp, endTimestamp);

        this.activityDataContainers = activityDataContainers;
        this.modality = modality;
        this.distanceTraveled = distanceTraveled;
        this.timeTraveled = timeTraveled;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.accelerationAverage = accelerationAverage;
        this.accelerationData = accelerationData;
        this.isWrongLeg = false;
        this.filteredAcceleration = filteredAcceleration;
        this.filteredAccelerationBelowThreshold = filteredAccelerationBelowThreshold;
        this.filteredSpeed = filteredSpeed;
        this.filteredSpeedBelowThreshold = filteredSpeedBelowThreshold;
        this.accuracyMean = accuracyMean;
        //defaultValue
        this.correctedModeOfTransport = correctedModeOfTransport;
        this.suggestedModeOfTransport = suggestedModeOfTransport;
        this.activityDataContainers = new ArrayList<>();
    }

    //copy constructor
    public Trip(Trip trip) {
        super(trip.locationDataContainers, trip.initTimestamp, trip.endTimestamp);
        this.activityDataContainers = trip.activityDataContainers;
        this.modality = trip.modality;
        this.distanceTraveled = trip.distanceTraveled;
        this.timeTraveled = trip.timeTraveled;
        this.averageSpeed = trip.averageSpeed;
        this.maxSpeed = trip.maxSpeed;
        this.accelerationAverage = trip.accelerationAverage;
        this.accelerationData = trip.accelerationData;
        this.isWrongLeg = trip.isWrongLeg;
        this.correctedModeOfTransport = trip.correctedModeOfTransport;
        this.filteredAcceleration = trip.filteredAcceleration;
        this.filteredAccelerationBelowThreshold = trip.filteredAccelerationBelowThreshold;
        this.filteredSpeed = trip.filteredSpeed;
        this.filteredSpeedBelowThreshold = trip.filteredSpeedBelowThreshold;
        this.accuracyMean = trip.accuracyMean;
        this.suggestedModeOfTransport = trip.suggestedModeOfTransport;
        this.trueDistance = trip.trueDistance;
        this.wasMerged = trip.wasMerged;
        this.wasSplitted = trip.wasSplitted;
        this.originalLegs = trip.originalLegs;
        this.otherMotText = trip.otherMotText;
    }

    public Trip(ArrayList<LocationDataContainer> locationDataContainers, long initTimestamp, long endTimestamp) {
        super(locationDataContainers, initTimestamp, endTimestamp);
    }

    public Trip(ArrayList<LocationDataContainer> locationDataContainers, ArrayList<AccelerationData> accelerationData,
                long initTimestamp, long endTimestamp, int identifiedModality,
                float averageSpeed, float maxSpeed, long distance
                ){

        super(locationDataContainers, initTimestamp, endTimestamp);

        this.suggestedModeOfTransport = identifiedModality;
        this.modality = identifiedModality;
        this.correctedModeOfTransport = -1;

        this.accelerationData = accelerationData;

        this.distanceTraveled = distance;
        this.maxSpeed = maxSpeed;
        this.averageSpeed = averageSpeed;

    }

    public int getModality() {
        return modality;
    }

    public void setModality(int modality) {
        this.modality = modality;
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

    @Override
    public boolean isTrip() {
        return true;
    }

    @Override
    public String getDescription() {

        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------"+"\n");
        sb.append("--------Leg---------" +"\n");

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
        String initDate = sdf.format(this.getInitTimestamp());
        String endDate = sdf.format(this.getEndTimestamp());

        sb.append("Start Date: "+initDate +"\n");
        sb.append("End Date: "+endDate +"\n");
        sb.append("Distance traveled: "+this.getDistanceTraveled() + "m" +"\n");
        sb.append("Time traveled: " + DateHelper.getHMSfromMS(this.getTimeTraveled()) +"\n");
        sb.append("Average speed: "+ this.getAverageSpeed() + "km/h" +"\n");
        sb.append("Maximum speed: "+ this.getMaxSpeed() + "km/h" +"\n");
        sb.append("Mode of transport: " + ActivityDetected.keys.modalities[modality] +"\n");
        if(this.getCorrectedModeOfTransport() != -1) {
            sb.append("Corrected mode of transport: " + ActivityDetected.keys.modalities[getCorrectedModeOfTransport()] + "\n");
        }
        sb.append("Average acceleration: "+ this.getAccelerationAverage() + " m/s2" +"\n");
        sb.append("Average accuracy: "+ this.getAccuracyMean() + " m" +"\n");
        sb.append("Filtered acceleration: "+ this.getFilteredAcceleration() + " m/s2"+"\n");
        sb.append("Acceleration below threshold percentage: "+ this.getFilteredAccelerationBelowThreshold() * 100 + "%"+"\n");
        sb.append("Filtered speed: "+ this.getFilteredSpeed() + " m/s"+"\n");
        sb.append("Speed below threshold percentage: "+ this.getFilteredSpeedBelowThreshold() * 100 + "%"+"\n");

        return sb.toString();

    }

    @Override
    public int getFullTripPartType() {
        if (correctedModeOfTransport == -1){
            return keys.NOT_VALIDATED_LEG;
        }
        return keys.VALIDATED_LEG;
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

    public ArrayList<LocationDataContainer> getLocationDataContainers() {
        return locationDataContainers;
    }

    public void setLocationDataContainers(ArrayList<LocationDataContainer> locationDataContainers) {
        this.locationDataContainers = locationDataContainers;
    }

    public ArrayList<ActivityDataContainer> getActivityDataContainers() {
        return activityDataContainers;
    }

    public void setActivityDataContainers(ArrayList<ActivityDataContainer> activityDataContainers) {
        this.activityDataContainers = activityDataContainers;
    }

    public double getAccelerationAverage() {
        return accelerationAverage;
    }

    public void setAccelerationAverage(double accelerationAverage) {
        this.accelerationAverage = accelerationAverage;
    }

    public ArrayList<AccelerationData> getAccelerationData() {
        return accelerationData;
    }

    public void setAccelerationData(ArrayList<AccelerationData> accelerationData) {
        this.accelerationData = accelerationData;
    }

    public boolean isWrongLeg() {
        return isWrongLeg;
    }

    public void setWrongLeg(boolean wrongLeg) {
        isWrongLeg = wrongLeg;
    }

    public int getCorrectedModeOfTransport() {
        return correctedModeOfTransport;
    }

    public void setCorrectedModeOfTransport(int correctedModeOfTransport) {
        this.correctedModeOfTransport = correctedModeOfTransport;
    }

    public double getFilteredAcceleration() {
        return filteredAcceleration;
    }

    public void setFilteredAcceleration(double filteredAcceleration) {
        this.filteredAcceleration = filteredAcceleration;
    }

    public double getFilteredAccelerationBelowThreshold() {
        return filteredAccelerationBelowThreshold;
    }

    public void setFilteredAccelerationBelowThreshold(double filteredAccelerationBelowThreshold) {
        this.filteredAccelerationBelowThreshold = filteredAccelerationBelowThreshold;
    }

    public double getFilteredSpeed() {
        return filteredSpeed;
    }

    public void setFilteredSpeed(double filteredSpeed) {
        this.filteredSpeed = filteredSpeed;
    }

    public double getFilteredSpeedBelowThreshold() {
        return filteredSpeedBelowThreshold;
    }

    public void setFilteredSpeedBelowThreshold(double filteredSpeedBelowThreshold) {
        this.filteredSpeedBelowThreshold = filteredSpeedBelowThreshold;
    }

    public float getAccuracyMean() {
        return accuracyMean;
    }

    public void setAccuracyMean(float accuracyMean) {
        this.accuracyMean = accuracyMean;
    }

    public int getSugestedModeOfTransport() {
        return suggestedModeOfTransport;
    }

    public void setSugestedModeOfTransport(int suggestedModeOfTransport) {
        this.suggestedModeOfTransport = suggestedModeOfTransport;
    }

    public String getOtherMotText() {
        return otherMotText;
    }

    public void setOtherMotText(String otherMotText) {
        this.otherMotText = otherMotText;
    }


}
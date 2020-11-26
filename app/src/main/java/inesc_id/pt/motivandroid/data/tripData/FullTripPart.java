package inesc_id.pt.motivandroid.data.tripData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.validationAndRating.TripPartFactor;
import inesc_id.pt.motivandroid.data.validationAndRating.ValueFromTrip;
import inesc_id.pt.motivandroid.data.validationAndRating.activities.ActivityLeg;
import inesc_id.pt.motivandroid.data.validationAndRating.ProductivityRelaxingLegRating;
import inesc_id.pt.motivandroid.data.validationAndRating.RelaxingProductiveFactor;

/**
 * FullTripPart
 *
 *
 * Super class for each type of trip segment. This segments could be WaitingEvent or Leg (class Trip)
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

public abstract class FullTripPart implements Serializable{

    public FullTripPart(ArrayList<LocationDataContainer> locationDataContainers, long initTimestamp, long endTimestamp) {
        this.locationDataContainers = locationDataContainers;
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.productivityRelaxingRating = new ProductivityRelaxingLegRating();

        this.ProdActivities = new ArrayList<>();
        this.MindActivities = new ArrayList<>();
        this.BodyActivities = new ArrayList<>();

//        this.legActivities = new ArrayList<>();
        this.relaxingFactorsArrayList = new ArrayList<>();
        this.productiveFactorsArrayList = new ArrayList<>();
    }

    @Expose
    String ProductiveFactorsText;

    @Expose
    String RelaxingFactorsText;

    @SerializedName("RelaxingFactors")
    @Expose
    ArrayList<RelaxingProductiveFactor> relaxingFactorsArrayList;

    @SerializedName("ProductiveFactors")
    @Expose
    ArrayList<RelaxingProductiveFactor> productiveFactorsArrayList;

    @SerializedName("locations")

    @Expose
    ArrayList<LocationDataContainer> locationDataContainers;

    @SerializedName("startDate")
    @Expose()
    long initTimestamp;

    @SerializedName("endDate")
    @Expose()
    long endTimestamp;

    @Expose
    ProductivityRelaxingLegRating productivityRelaxingRating;

    public Integer getWastedTime() {
        return wastedTime;
    }

    public void setWastedTime(Integer wastedTime) {
        this.wastedTime = wastedTime;
    }



    public ArrayList<ValueFromTrip> getValueFromTript() {
        return valueFromTrip;
    }

    public void setValueFromTript(ArrayList<ValueFromTrip> valueFromTript) {
        this.valueFromTrip = valueFromTript;
    }

    @Expose
    Integer wastedTime;

    @Expose
    ArrayList<TripPartFactor> activitiesFactors;

    @Expose
    ArrayList<TripPartFactor> comfortAndPleasentFactors;

    @Expose
    ArrayList<TripPartFactor> gettingThereFactors;

    @Expose
    ArrayList<TripPartFactor> whileYouRideFactors;

    @Expose
    String otherFactor;


    @Expose
    ArrayList<ValueFromTrip> valueFromTrip;

    public ArrayList<ActivityLeg> getGenericActivities() {
        return genericActivities;
    }

    public void setGenericActivities(ArrayList<ActivityLeg> genericActivities) {
        this.genericActivities = genericActivities;
    }

    @Expose
    ArrayList<ActivityLeg> genericActivities;


//    @Expose
//    ArrayList<ActivityLeg> legActivities;

//    @Expose
    ArrayList<ActivityLeg> ProdActivities;

    public ArrayList<ActivityLeg> getProdActivities() {
        return ProdActivities;
    }

    public void setProdActivities(ArrayList<ActivityLeg> prodActivities) {
        ProdActivities = prodActivities;
    }

    public ArrayList<ActivityLeg> getMindActivities() {
        return MindActivities;
    }

    public void setMindActivities(ArrayList<ActivityLeg> mindActivities) {
        MindActivities = mindActivities;
    }

    public ArrayList<ActivityLeg> getBodyActivities() {
        return BodyActivities;
    }

    public void setBodyActivities(ArrayList<ActivityLeg> bodyActivities) {
        BodyActivities = bodyActivities;
    }

//    @Expose
    ArrayList<ActivityLeg> MindActivities;

//    @Expose
    ArrayList<ActivityLeg> BodyActivities;

    public ArrayList<LocationDataContainer> getLocationDataContainers() {
        return locationDataContainers;
    }

    public void setLocationDataContainers(ArrayList<LocationDataContainer> locationDataContainers) {
        this.locationDataContainers = locationDataContainers;
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

    public ProductivityRelaxingLegRating getProductivityRelaxingRating() {
        if (productivityRelaxingRating == null) productivityRelaxingRating = new ProductivityRelaxingLegRating();
        return productivityRelaxingRating;
    }

    public void setProductivityRelaxingRating(ProductivityRelaxingLegRating productivityRelaxingRating) {
        this.productivityRelaxingRating = productivityRelaxingRating;
    }

    public ArrayList<ActivityLeg> getLegActivities() {
        ArrayList<ActivityLeg> result = new ArrayList<>();

        if(ProdActivities == null){
            ProdActivities = new ArrayList<>();
        }

        if(MindActivities == null){
            MindActivities = new ArrayList<>();
        }

        if(BodyActivities == null){
            BodyActivities = new ArrayList<>();
        }

        result.addAll(ProdActivities);
        result.addAll(MindActivities);
        result.addAll(BodyActivities);

        return result;
    }

//    public void setLegActivities(ArrayList<ActivityLeg> legActivities) {
//        this.legActivities = legActivities;
//    }

    public abstract boolean isTrip();

    public abstract String getDescription();

    public abstract int getFullTripPartType();


    public ArrayList<RelaxingProductiveFactor> getRelaxingFactorsArrayList() {
        return relaxingFactorsArrayList;
    }

    public void setRelaxingFactorsArrayList(ArrayList<RelaxingProductiveFactor> relaxingFactorsArrayList) {
        this.relaxingFactorsArrayList = relaxingFactorsArrayList;
    }

    public ArrayList<RelaxingProductiveFactor> getProductiveFactorsArrayList() {
        return productiveFactorsArrayList;
    }

    public void setProductiveFactorsArrayList(ArrayList<RelaxingProductiveFactor> productiveFactorsArrayList) {
        this.productiveFactorsArrayList = productiveFactorsArrayList;
    }

    public String getProductiveFactorsText() {
        return ProductiveFactorsText;
    }

    public void setProductiveFactorsText(String productiveFactorsText) {
        ProductiveFactorsText = productiveFactorsText;
    }

    public String getRelaxingFactorsText() {
        return RelaxingFactorsText;
    }

    public void setRelaxingFactorsText(String relaxingFactorsText) {
        RelaxingFactorsText = relaxingFactorsText;
    }

    public ArrayList<TripPartFactor> getActivitiesFactors() {
        return activitiesFactors;
    }

    public ArrayList<TripPartFactor> getComfortAndPleasantFactors() {
        return comfortAndPleasentFactors;
    }

    public ArrayList<TripPartFactor> getGettingThereFactors() {
        return gettingThereFactors;
    }

    public void setActivitiesFactors(ArrayList<TripPartFactor> activitiesFactors) {
        this.activitiesFactors = activitiesFactors;
    }

    public void setComfortAndPleasantFactors(ArrayList<TripPartFactor> comfortAndPleasantFactors) {
        this.comfortAndPleasentFactors = comfortAndPleasantFactors;
    }

    public void setGettingThereFactors(ArrayList<TripPartFactor> gettingThereFactors) {
        this.gettingThereFactors = gettingThereFactors;
    }

    public ArrayList<TripPartFactor> getWhileYouRideFactors() {
        return whileYouRideFactors;
    }

    public void setWhileYouRideFactors(ArrayList<TripPartFactor> whileYouRideFactors) {
        this.whileYouRideFactors = whileYouRideFactors;
    }

    public String getOtherFactors() {
        return otherFactor;
    }

    public void setOtherFactors(String otherFactors) {
        this.otherFactor = otherFactors;
    }


    public interface keys{
        int VALIDATED_LEG = 0;
        int NOT_VALIDATED_LEG = 1;
        int WAITING_EVENT = 2;
        int DUMMY_DEPARTURE_LEG = 3;
        int DUMMY_ARRIVAL_LEG = 4;

        //activity related
        int FULL_TRIP_PART_WITH_ACTIVITIES = 5;
        int FULL_TRIP_PART_WITHOUT_ACTIVITIES = 6;
    }

}

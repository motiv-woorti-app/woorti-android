package inesc_id.pt.motivandroid.data.tripDigest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripDigest.tripStats.TripStats;

/**
 * FullTripDigest
 *
 *  Data structure for a trip digest. Includes the trip data except locations and accelerations.
 * Allows to access a trip and compute stats, etc in a more efficient way.
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

public class FullTripDigest implements Serializable{

    public FullTripDigest(long initTimestamp, long endTimestamp, String userID, boolean sentToServer, boolean validated, String startAddress, String finalAddress, LocationDataContainer startLocation, LocationDataContainer finalLocation, String tripID, TripStats tripStats, double distanceTraveled) {
        this.initTimestamp = initTimestamp;
        this.endTimestamp = endTimestamp;
        this.userID = userID;
        this.sentToServer = sentToServer;
        this.validated = validated;
        this.startAddress = startAddress;
        this.finalAddress = finalAddress;
        this.startLocation = startLocation;
        this.finalLocation = finalLocation;
        this.tripID = tripID;
        this.tripStats = tripStats;
        this.distanceTraveled = distanceTraveled;
    }

    @SerializedName("startDate")
    @Expose
    private long initTimestamp;

    @SerializedName("endDate")
    @Expose
    private long endTimestamp;

    @SerializedName("userID")
    @Expose
    private String userID;

    @SerializedName("sentToServer")
    @Expose
    private boolean sentToServer;

    @SerializedName("validated")
    @Expose
    private boolean validated;

    @SerializedName("startAddress")
    @Expose
    private String startAddress;

    @SerializedName("finalAddress")
    @Expose
    private String finalAddress;

    @SerializedName("startLocation")
    @Expose
    private LocationDataContainer startLocation;

    @SerializedName("finalLocation")
    @Expose
    private LocationDataContainer finalLocation;

    @Expose
    private String tripID;

    @Expose
    private TripStats tripStats;

    @Expose
    private double distanceTraveled;

    public TripStats getTripStats() {
        return tripStats;
    }

    public void setTripStats(TripStats tripStats) {
        this.tripStats = tripStats;
    }

    //new structures

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

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

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

    public LocationDataContainer getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LocationDataContainer startLocation) {
        this.startLocation = startLocation;
    }

    public LocationDataContainer getFinalLocation() {
        return finalLocation;
    }

    public void setFinalLocation(LocationDataContainer finalLocation) {
        this.finalLocation = finalLocation;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public static int checkNumberOfValidatedTrips(ArrayList<FullTripDigest> digests){

        if (digests == null) return 0;

        int i = 0;

        for (FullTripDigest fullTripDigest : digests){
            if (fullTripDigest.isValidated()) i++;
        }

        return i;

    }

}

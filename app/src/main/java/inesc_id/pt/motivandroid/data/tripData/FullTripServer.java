package inesc_id.pt.motivandroid.data.tripData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import inesc_id.pt.motivandroid.data.validationAndRating.TripObjectiveWrapper;

/**
 * FullTripServer
 *
 * This class represents the format the server is expecting for a Trip.
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
public class FullTripServer implements Serializable{

    @SerializedName("trips")
    @Expose
    private ArrayList<FullTripPart> tripList;

    @SerializedName("startDate")
    @Expose
    private long initTimestamp;

    @SerializedName("endDate")
    @Expose
    private long endTimestamp;

    @SerializedName("distance")
    @Expose
    private long distanceTraveled;

    @SerializedName("duration")
    @Expose
    private long timeTraveled;

    @SerializedName("avSpeed")
    @Expose
    private float averageSpeed;

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

    @SerializedName("sentToServer")
    @Expose
    private boolean sentToServer;

    @SerializedName("countryInfo")
    @Expose
    private String country;

    @SerializedName("cityInfo")
    @Expose
    private String cityInfo;

    @SerializedName("validated")
    @Expose
    private boolean validated;

//    @SerializedName("prevScore")
//    @Expose
//    public int prevScore = 0;


    //    todo uncomment for new trip structure - erase former objectiveOfTheTrip
    @SerializedName("objectives")
    @Expose
    private ArrayList<TripObjectiveWrapper> objectivesOfTheTrip;

    @SerializedName("startAddress")
    @Expose
    private String startAddress;

    @SerializedName("finalAddress")
    @Expose
    private String finalAddress;

    @Expose
    private int overallScore;

    @Expose
    private Integer didYouHaveToArrive;

    @Expose
    private Integer howOften;

    @Expose
    private Integer useTripMoreFor;

    @Expose
    private String shareInformation;

    @Expose
    private int numMerges = 0;

    @Expose
    private int numSplits = 0;

    @Expose
    private int numDeletes = 0;

    @Expose
    private boolean manualTripStart = false;

    @Expose
    private boolean manualTripEnd = false;

    @Expose
    private long validationDate;

//    public FullTripServer(FullTrip fullTrip) {
//        this.tripList = tripList;
//        this.initTimestamp = initTimestamp;
//        this.endTimestamp = endTimestamp;
//        this.distanceTraveled = distanceTraveled;
//        this.timeTraveled = timeTraveled;
//        this.averageSpeed = averageSpeed;
//        this.maxSpeed = maxSpeed;
//        this.smartphoneModel = smartphoneModel;
//        this.operatingSystem = operatingSystem;
//        this.oSVersion = oSVersion;
//        this.userID = userID;
//        this.sentToServer = sentToServer;
//        this.validated = false;
//    }

    public FullTripServer(FullTrip fullTrip) {
        this.tripList = fullTrip.getTripList();
        this.initTimestamp = fullTrip.getInitTimestamp();
        this.endTimestamp = fullTrip.getEndTimestamp();
        this.distanceTraveled = fullTrip.getDistanceTraveled();
        this.timeTraveled = fullTrip.getTimeTraveled();
        this.averageSpeed = fullTrip.getAverageSpeed();
        this.maxSpeed = fullTrip.getMaxSpeed();
        this.smartphoneModel = fullTrip.getSmartphoneModel();
        this.operatingSystem = fullTrip.getOperatingSystem();
        this.oSVersion = fullTrip.getoSVersion();
        this.userID = fullTrip.getUserID();
        this.sentToServer = fullTrip.isSentToServer();
        this.country = fullTrip.getCountry();
        this.cityInfo = fullTrip.getCityInfo();
        this.validated = fullTrip.isValidated();
        this.objectivesOfTheTrip = fullTrip.getObjectivesOfTheTrip();
        this.startAddress = fullTrip.getStartAddress();
        this.finalAddress = fullTrip.getFinalAddress();
//        this.overallScore = fullTrip.getOverallScore();
        this.didYouHaveToArrive = fullTrip.getDidYouHaveToArrive();
        this.howOften = fullTrip.getHowOften();
        this.useTripMoreFor = fullTrip.getUseTripMoreFor();
        this.shareInformation = fullTrip.getShareInformation();
        this.overallScore = fullTrip.getOverallScore();
        this.manualTripStart = fullTrip.isManualTripStart();
        this.manualTripEnd = fullTrip.isManualTripEnd();
        this.numMerges = fullTrip.getNumMerges();
        this.numDeletes = fullTrip.getNumDeletes();
        this.numSplits = fullTrip.getNumSplits();
        this.validationDate = fullTrip.getValidationDate();
    }

    public FullTripServer() {
    }


}

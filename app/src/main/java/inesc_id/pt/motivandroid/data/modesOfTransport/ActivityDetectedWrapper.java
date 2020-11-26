package inesc_id.pt.motivandroid.data.modesOfTransport;

import java.util.ArrayList;


/**
 *
 * ActivityDetectedWrapper
 *
 *  Wrapper for ActivityDetected (mode of transport) to add the concept of MOT group.
 * Label/group id could be ActivityDetected.keys.PUBLIC_TRANSPORT, ActivityDetected.keys.ACTIVE_TRANSPORT
 * or ActivityDetected.keys.PRIVATE_TRANSPORT
 *
 *  * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
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

public class ActivityDetectedWrapper {

    public ActivityDetectedWrapper(int icon, String text, int code, String label) {
        this.icon = icon;
        this.text = text;
        this.code = code;
        this.label = label;
    }

    public ActivityDetectedWrapper(int icon, String text, int code) {
        this.icon = icon;
        this.text = text;
        this.code = code;
    }

    int icon;
    String text;
    int code;
    String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static boolean isGeneralMode(int detectedModeOfTransport, int correctedModeOfTransport){

        if(detectedModeOfTransport == correctedModeOfTransport) return true;

        ArrayList<MOTGroup> defaultGroups = MOTGroup.getDefaultGroups();

        MOTGroup mainGroupMOTDetected = MOTGroup.getGroupMOTFromMOTCode(defaultGroups, detectedModeOfTransport);
        MOTGroup mainGroupMOTCorrected = MOTGroup.getGroupMOTFromMOTCode(defaultGroups, correctedModeOfTransport);

        if (mainGroupMOTCorrected.getCode() == mainGroupMOTDetected.getCode()){
            return true;
        }else{
            return false;
        }

    }

    public static ArrayList<ActivityDetectedWrapper> getFullList(){

        ArrayList<ActivityDetectedWrapper> activityDetectedWrappers = new ArrayList<>();

        ActivityDetectedWrapper carWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.car],
                ActivityDetected.keys.car);

        ActivityDetectedWrapper bicycleWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bicycle),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bicycle],
                ActivityDetected.keys.bicycle);

        ActivityDetectedWrapper walkingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.walking),
                ActivityDetected.keys.modalities[ActivityDetected.keys.walking],
                ActivityDetected.keys.walking);

        ActivityDetectedWrapper trainWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.train),
                ActivityDetected.keys.modalities[ActivityDetected.keys.train],
                ActivityDetected.keys.train);

        ActivityDetectedWrapper ferryWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.ferry),
                ActivityDetected.keys.modalities[ActivityDetected.keys.ferry],
                ActivityDetected.keys.ferry);

        ActivityDetectedWrapper busWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bus),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bus],
                ActivityDetected.keys.bus);

        ActivityDetectedWrapper subwayWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.subway),
                ActivityDetected.keys.modalities[ActivityDetected.keys.subway],
                ActivityDetected.keys.subway);

        ActivityDetectedWrapper tramWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.tram),
                ActivityDetected.keys.modalities[ActivityDetected.keys.tram],
                ActivityDetected.keys.tram);

        activityDetectedWrappers.add(carWrapper);
        activityDetectedWrappers.add(bicycleWrapper);
        activityDetectedWrappers.add(walkingWrapper);
        activityDetectedWrappers.add(trainWrapper);
        activityDetectedWrappers.add(ferryWrapper);
        activityDetectedWrappers.add(busWrapper);
        activityDetectedWrappers.add(subwayWrapper);
        activityDetectedWrappers.add(tramWrapper);

        return activityDetectedWrappers;
    }

    public static ArrayList<ActivityDetectedWrapper> getPublicFullList() {
        ArrayList<ActivityDetectedWrapper> activityDetectedWrappers = new ArrayList<>();

        ////PUBLIC TRANSPORTS

        ActivityDetectedWrapper subwayWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.subway),
                ActivityDetected.keys.modalities[ActivityDetected.keys.subway],
                ActivityDetected.keys.subway, ActivityDetected.getTransportLabel(ActivityDetected.keys.subway));

        ActivityDetectedWrapper tramWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.tram),
                ActivityDetected.keys.modalities[ActivityDetected.keys.tram],
                ActivityDetected.keys.tram, ActivityDetected.getTransportLabel(ActivityDetected.keys.tram));

        ActivityDetectedWrapper busWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bus),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bus],
                ActivityDetected.keys.bus, ActivityDetected.getTransportLabel(ActivityDetected.keys.bus));

        ActivityDetectedWrapper busLongDistanceWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.busLongDistance),
                ActivityDetected.keys.modalities[ActivityDetected.keys.busLongDistance],
                ActivityDetected.keys.busLongDistance, ActivityDetected.getTransportLabel(ActivityDetected.keys.busLongDistance));

        ActivityDetectedWrapper urbanTrainWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.train),
                ActivityDetected.keys.modalities[ActivityDetected.keys.train],
                ActivityDetected.keys.train, ActivityDetected.getTransportLabel(ActivityDetected.keys.train));

        ActivityDetectedWrapper intercityTrainWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.intercityTrain),
                ActivityDetected.keys.modalities[ActivityDetected.keys.intercityTrain],
                ActivityDetected.keys.intercityTrain, ActivityDetected.getTransportLabel(ActivityDetected.keys.intercityTrain));

        ActivityDetectedWrapper highSpeedTrainWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.highSpeedTrain),
                ActivityDetected.keys.modalities[ActivityDetected.keys.highSpeedTrain],
                ActivityDetected.keys.highSpeedTrain, ActivityDetected.getTransportLabel(ActivityDetected.keys.highSpeedTrain));

        ActivityDetectedWrapper ferryWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.ferry),
                ActivityDetected.keys.modalities[ActivityDetected.keys.ferry],
                ActivityDetected.keys.ferry, ActivityDetected.getTransportLabel(ActivityDetected.keys.ferry));

        ActivityDetectedWrapper planeWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.plane),
                ActivityDetected.keys.modalities[ActivityDetected.keys.plane],
                ActivityDetected.keys.plane, ActivityDetected.getTransportLabel(ActivityDetected.keys.plane));

        ActivityDetectedWrapper otherPublicWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.other),
                ActivityDetected.keys.modalities[ActivityDetected.keys.otherPublic],
                ActivityDetected.keys.otherPublic, ActivityDetected.getTransportLabel(ActivityDetected.keys.otherPublic));

        activityDetectedWrappers.add(subwayWrapper);
        activityDetectedWrappers.add(tramWrapper);
        activityDetectedWrappers.add(busWrapper);
        activityDetectedWrappers.add(busLongDistanceWrapper);
        activityDetectedWrappers.add(urbanTrainWrapper);
        activityDetectedWrappers.add(intercityTrainWrapper);
        activityDetectedWrappers.add(highSpeedTrainWrapper);
        activityDetectedWrappers.add(ferryWrapper);
        activityDetectedWrappers.add(planeWrapper);
        activityDetectedWrappers.add(otherPublicWrapper);

        return activityDetectedWrappers;
    }

    public static ArrayList<ActivityDetectedWrapper> getActiveFullList(){
        ArrayList<ActivityDetectedWrapper> activityDetectedWrappers = new ArrayList<>();

        //ACTIVE/SEMI-ACTIVE TRANSPORTS

        ActivityDetectedWrapper walkingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.walking),
                ActivityDetected.keys.modalities[ActivityDetected.keys.walking],
                ActivityDetected.keys.walking, ActivityDetected.getTransportLabel(ActivityDetected.keys.walking));

        ActivityDetectedWrapper runningWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.running),
                ActivityDetected.keys.modalities[ActivityDetected.keys.running],
                ActivityDetected.keys.running, ActivityDetected.getTransportLabel(ActivityDetected.keys.running));

        ActivityDetectedWrapper wheelChairWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.wheelChair),
                ActivityDetected.keys.modalities[ActivityDetected.keys.wheelChair],
                ActivityDetected.keys.wheelChair, ActivityDetected.getTransportLabel(ActivityDetected.keys.wheelChair));

        ActivityDetectedWrapper bicycleWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bicycle),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bicycle],
                ActivityDetected.keys.bicycle, ActivityDetected.getTransportLabel(ActivityDetected.keys.bicycle));

        ActivityDetectedWrapper eletricBikeWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.electricBike),
                ActivityDetected.keys.modalities[ActivityDetected.keys.electricBike],
                ActivityDetected.keys.electricBike, ActivityDetected.getTransportLabel(ActivityDetected.keys.electricBike));

        ActivityDetectedWrapper cargoBikeWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.cargoBike),
                ActivityDetected.keys.modalities[ActivityDetected.keys.cargoBike],
                ActivityDetected.keys.cargoBike, ActivityDetected.getTransportLabel(ActivityDetected.keys.cargoBike));

        ActivityDetectedWrapper bikeSharingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bikeSharing),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bikeSharing],
                ActivityDetected.keys.bikeSharing, ActivityDetected.getTransportLabel(ActivityDetected.keys.bikeSharing));

        ActivityDetectedWrapper microScooterWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.microScooter),
                ActivityDetected.keys.modalities[ActivityDetected.keys.microScooter],
                ActivityDetected.keys.microScooter, ActivityDetected.getTransportLabel(ActivityDetected.keys.microScooter));

        ActivityDetectedWrapper otherActiveWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.other),
                ActivityDetected.keys.modalities[ActivityDetected.keys.otherActive],
                ActivityDetected.keys.otherActive, ActivityDetected.getTransportLabel(ActivityDetected.keys.otherActive));

        activityDetectedWrappers.add(walkingWrapper);
        activityDetectedWrappers.add(runningWrapper);
        activityDetectedWrappers.add(wheelChairWrapper);
        activityDetectedWrappers.add(bicycleWrapper);
        activityDetectedWrappers.add(eletricBikeWrapper);
        activityDetectedWrappers.add(cargoBikeWrapper);
        activityDetectedWrappers.add(bikeSharingWrapper);
        activityDetectedWrappers.add(microScooterWrapper);
        activityDetectedWrappers.add(otherActiveWrapper);

        return activityDetectedWrappers;

    }

    public static ArrayList<ActivityDetectedWrapper> getPrivateFullList(){

        ArrayList<ActivityDetectedWrapper> activityDetectedWrappers = new ArrayList<>();

        //PRIVATE MOTORISED TRANSPORTS

        ActivityDetectedWrapper carWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.car],
                ActivityDetected.keys.car, ActivityDetected.getTransportLabel(ActivityDetected.keys.car));

        ActivityDetectedWrapper carPassengerWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.carPassenger],
                ActivityDetected.keys.carPassenger, ActivityDetected.getTransportLabel(ActivityDetected.keys.carPassenger));

        ActivityDetectedWrapper taxiWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.taxi],
                ActivityDetected.keys.taxi, ActivityDetected.getTransportLabel(ActivityDetected.keys.taxi));

        ActivityDetectedWrapper carShareDriverWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.carSharing],
                ActivityDetected.keys.carSharing, ActivityDetected.getTransportLabel(ActivityDetected.keys.carSharing));

        ActivityDetectedWrapper carSharePassengerWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.carSharingPassenger],
                ActivityDetected.keys.carSharingPassenger, ActivityDetected.getTransportLabel(ActivityDetected.keys.carSharingPassenger));

        ActivityDetectedWrapper mopedWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.moped],
                ActivityDetected.keys.moped, ActivityDetected.getTransportLabel(ActivityDetected.keys.moped));

        ActivityDetectedWrapper motorcycleWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.motorcycle],
                ActivityDetected.keys.motorcycle, ActivityDetected.getTransportLabel(ActivityDetected.keys.motorcycle));

        ActivityDetectedWrapper electricWheelchairWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconInt("car"),
                ActivityDetected.keys.modalities[ActivityDetected.keys.electricWheelchair],
                ActivityDetected.keys.electricWheelchair, ActivityDetected.getTransportLabel(ActivityDetected.keys.electricWheelchair));

        ActivityDetectedWrapper otherPrivateWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.other),
                ActivityDetected.keys.modalities[ActivityDetected.keys.otherPrivate],
                ActivityDetected.keys.otherPrivate, ActivityDetected.getTransportLabel(ActivityDetected.keys.otherPrivate));


        activityDetectedWrappers.add(carWrapper);
        activityDetectedWrappers.add(carPassengerWrapper);
        activityDetectedWrappers.add(taxiWrapper);
        activityDetectedWrappers.add(carShareDriverWrapper);
        activityDetectedWrappers.add(carSharePassengerWrapper);
        activityDetectedWrappers.add(mopedWrapper);
        activityDetectedWrappers.add(motorcycleWrapper);
        activityDetectedWrappers.add(electricWheelchairWrapper);
        activityDetectedWrappers.add(otherPrivateWrapper);

        return activityDetectedWrappers;
    }
}

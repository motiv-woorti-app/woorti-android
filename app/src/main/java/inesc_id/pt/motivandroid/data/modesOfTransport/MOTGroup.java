package inesc_id.pt.motivandroid.data.modesOfTransport;

import java.util.ArrayList;

/**
 * MOTGroup
 *
 *  Class that defines which modes of transport that even though are different, should be considered
 *  as equal and therefore evaluated by the app as correct. E.g. if a car ride is detected and the
 *  user corrects it to "ride hailing" or "car passenger", the mode of transport suggested by the app
 *  in the first place should be considered as correct for statistical purposes.
 *
 *  (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
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

public class MOTGroup {

    int code;
    ArrayList<ActivityDetectedWrapper> mots;
    ActivityDetectedWrapper main;

    public MOTGroup(int code, ArrayList<ActivityDetectedWrapper> mots, ActivityDetectedWrapper main) {
        this.code = code;
        this.mots = mots;
        this.main = main;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<ActivityDetectedWrapper> getMots() {
        return mots;
    }

    public void setMots(ArrayList<ActivityDetectedWrapper> mots) {
        this.mots = mots;
    }

    public ActivityDetectedWrapper getMain() {
        return main;
    }

    public void setMain(ActivityDetectedWrapper main) {
        this.main = main;
    }

    public static ArrayList<MOTGroup> getDefaultGroups(){

        ArrayList<MOTGroup> result = new ArrayList<>();

        result.add(getBicycleGroup());
        result.add(getStillGroup());
        result.add(getWalkingGroup());
        result.add(getCarGroup());
        result.add(getBusGroup());
        result.add(getTrainGroup());
        result.add(getFerryBoatGroup());
        result.add(getPlaneGroup());
        result.add(getOtherGroup());

        return result;
    }

    public ActivityDetectedWrapper getGroupMainModeOfTransport(int detectedMode){

        for(ActivityDetectedWrapper mot : mots){

            if(mot.getCode() == detectedMode){
                return main;
            }
        }

        return null;
    }

    public static MOTGroup getGroupMOTFromMOTCode(ArrayList<MOTGroup> groups, int detectedMode){

        for(MOTGroup group : groups){

            if(group.getGroupMainModeOfTransport(detectedMode) != null){
                return group;
            }
        }

        return null;
    }


    public static MOTGroup getBicycleGroup(){

        ActivityDetectedWrapper bicycleWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bicycle),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bicycle],
                ActivityDetected.keys.bicycle);

        ActivityDetectedWrapper electricBicycleWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.electricBike),
                ActivityDetected.keys.modalities[ActivityDetected.keys.electricBike],
                ActivityDetected.keys.electricBike);

        ActivityDetectedWrapper bikeSharingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bikeSharing),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bikeSharing],
                ActivityDetected.keys.bikeSharing);

        ActivityDetectedWrapper microScooterWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.microScooter),
                ActivityDetected.keys.modalities[ActivityDetected.keys.microScooter],
                ActivityDetected.keys.microScooter);

        ActivityDetectedWrapper skateWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.skate),
                ActivityDetected.keys.modalities[ActivityDetected.keys.skate],
                ActivityDetected.keys.skate);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(bicycleWrapper);
        groupMOTs.add(electricBicycleWrapper);
        groupMOTs.add(bikeSharingWrapper);
        groupMOTs.add(microScooterWrapper);
        groupMOTs.add(skateWrapper);

        return new MOTGroup(ActivityDetected.keys.bicycle, groupMOTs, bicycleWrapper);

    }

    public static MOTGroup getWalkingGroup(){

        ActivityDetectedWrapper walkingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.walking),
                ActivityDetected.keys.modalities[ActivityDetected.keys.walking],
                ActivityDetected.keys.walking);

        ActivityDetectedWrapper runningWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.running),
                ActivityDetected.keys.modalities[ActivityDetected.keys.running],
                ActivityDetected.keys.running);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(walkingWrapper);
        groupMOTs.add(runningWrapper);

        return new MOTGroup(ActivityDetected.keys.running, groupMOTs, walkingWrapper);
    }

    public static MOTGroup getCarGroup(){

        ActivityDetectedWrapper carWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.car),
                ActivityDetected.keys.modalities[ActivityDetected.keys.car],
                ActivityDetected.keys.car);

        ActivityDetectedWrapper motorcycleWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.motorcycle),
                ActivityDetected.keys.modalities[ActivityDetected.keys.motorcycle],
                ActivityDetected.keys.motorcycle);

        ActivityDetectedWrapper mopedWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.moped),
                ActivityDetected.keys.modalities[ActivityDetected.keys.moped],
                ActivityDetected.keys.moped);

        ActivityDetectedWrapper carPassengerWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.carPassenger),
                ActivityDetected.keys.modalities[ActivityDetected.keys.carPassenger],
                ActivityDetected.keys.carPassenger);

        ActivityDetectedWrapper taxiWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.taxi),
                ActivityDetected.keys.modalities[ActivityDetected.keys.taxi],
                ActivityDetected.keys.taxi);

        ActivityDetectedWrapper rideHaillingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.rideHailing),
                ActivityDetected.keys.modalities[ActivityDetected.keys.rideHailing],
                ActivityDetected.keys.rideHailing);

        ActivityDetectedWrapper carSharingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.carSharing),
                ActivityDetected.keys.modalities[ActivityDetected.keys.carSharing],
                ActivityDetected.keys.carSharing);

        ActivityDetectedWrapper carpoolingWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.carpooling),
                ActivityDetected.keys.modalities[ActivityDetected.keys.carpooling],
                ActivityDetected.keys.carpooling);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(carWrapper);
        groupMOTs.add(motorcycleWrapper);
        groupMOTs.add(mopedWrapper);
        groupMOTs.add(carPassengerWrapper);
        groupMOTs.add(taxiWrapper);
        groupMOTs.add(rideHaillingWrapper);
        groupMOTs.add(carSharingWrapper);
        groupMOTs.add(carpoolingWrapper);

        return new MOTGroup(ActivityDetected.keys.car, groupMOTs, carWrapper);
    }

    public static MOTGroup getBusGroup(){

        ActivityDetectedWrapper busWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.bus),
                ActivityDetected.keys.modalities[ActivityDetected.keys.bus],
                ActivityDetected.keys.bus);

        ActivityDetectedWrapper busLongDistanceWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.busLongDistance),
                ActivityDetected.keys.modalities[ActivityDetected.keys.busLongDistance],
                ActivityDetected.keys.busLongDistance);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(busWrapper);
        groupMOTs.add(busLongDistanceWrapper);

        return new MOTGroup(ActivityDetected.keys.bus, groupMOTs, busWrapper);
    }

    public static MOTGroup getTrainGroup(){

        ActivityDetectedWrapper trainWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.train),
                ActivityDetected.keys.modalities[ActivityDetected.keys.train],
                ActivityDetected.keys.train);

        ActivityDetectedWrapper highSpeedTrainWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.highSpeedTrain),
                ActivityDetected.keys.modalities[ActivityDetected.keys.highSpeedTrain],
                ActivityDetected.keys.highSpeedTrain);

        ActivityDetectedWrapper tramWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.tram),
                ActivityDetected.keys.modalities[ActivityDetected.keys.tram],
                ActivityDetected.keys.tram);

        ActivityDetectedWrapper subwayWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.subway),
                ActivityDetected.keys.modalities[ActivityDetected.keys.subway],
                ActivityDetected.keys.subway);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(trainWrapper);
        groupMOTs.add(highSpeedTrainWrapper);
        groupMOTs.add(tramWrapper);
        groupMOTs.add(subwayWrapper);

        return new MOTGroup(ActivityDetected.keys.train, groupMOTs, trainWrapper);
    }

    public static MOTGroup getFerryBoatGroup(){

        ActivityDetectedWrapper ferryBoatWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.ferry),
                ActivityDetected.keys.modalities[ActivityDetected.keys.ferry],
                ActivityDetected.keys.ferry);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(ferryBoatWrapper);

        return new MOTGroup(ActivityDetected.keys.ferry, groupMOTs, ferryBoatWrapper);
    }

    public static MOTGroup getPlaneGroup(){

        ActivityDetectedWrapper planeWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.plane),
                ActivityDetected.keys.modalities[ActivityDetected.keys.plane],
                ActivityDetected.keys.plane);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(planeWrapper);

        return new MOTGroup(ActivityDetected.keys.plane, groupMOTs, planeWrapper);
    }

    public static MOTGroup getOtherGroup(){

        ActivityDetectedWrapper otherWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.other),
                ActivityDetected.keys.modalities[ActivityDetected.keys.other],
                ActivityDetected.keys.other);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(otherWrapper);

        return new MOTGroup(ActivityDetected.keys.other, groupMOTs, otherWrapper);
    }

    public static MOTGroup getStillGroup(){

        ActivityDetectedWrapper stillWrapper = new ActivityDetectedWrapper(ActivityDetected.getTransportIconFromInt(ActivityDetected.keys.still),
                ActivityDetected.keys.modalities[ActivityDetected.keys.still],
                ActivityDetected.keys.still);

        ArrayList<ActivityDetectedWrapper> groupMOTs = new ArrayList<>();
        groupMOTs.add(stillWrapper);

        return new MOTGroup(ActivityDetected.keys.still, groupMOTs, stillWrapper);
    }

}

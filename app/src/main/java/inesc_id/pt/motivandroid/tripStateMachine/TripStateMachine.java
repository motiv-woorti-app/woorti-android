package inesc_id.pt.motivandroid.tripStateMachine;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import inesc_id.pt.motivandroid.managers.EngagementManager;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.showOngoingTrip.OngoingTripWrapper;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.LocationUtils;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.persistence.PersistentTripStorage;
import inesc_id.pt.motivandroid.persistence.TSMSnapshotHelper;
import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.ActivityDataContainer;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;

import static org.joda.time.DateTimeZone.UTC;

/**
 * TripStateMachine
 *
 *   Trip State machine logic. Receives location and acceleration samples from the Activity
 *  Recognition Service, and decides if a trip has been started or ended. When in trip,
 *  there are two possible states: Trip, Waiting Event.
 *
 *  A trip starts if the user commutes to place at least 100 meters away (goes from Still state to
 *  the Trip state)
 *  Throughout a trip, if the user stay within a 100 meters radius for more than 5 minutes, user
 *  go to the WaitingEvent state. If the user leaves a 100 meters radius again,
 *  goes back to Trip state. If the the user stays within a 100 meters radius for another 25
 *  minutes, the trip is ended (goes to still state, the last waiting event is removed - because the
 *  trip has ended and the trip is saved).
 *
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


public class TripStateMachine {

    private static final Logger LOG = LoggerFactory.getLogger(TripStateMachine.class.getSimpleName());

    public state currentState;
    Context context;

    // Constants
    /**
     * minimum radius(meters)  - more than 100 meters to account for the false positives
     */
    final static int tripDistanceLimit = 115;

    /**
     * time constant - 5 minutes in milliseconds - time within a tripDistance limit to go from trip
     * to waiting event
     */
    int tripTimeLimit = 5*60*1000;

    /**
     * time constant - 25 minutes in milliseconds - time within a tripDistance limit to go from
     * waiting event state to still state (end of trip)
     */
    int fullTripTimeLimit = 25*60*1000;

    /**
     * time constant - 30 minutes in milliseconds - time interval which a recovered trip snapshot is
     * considered to be valid.
     */
    final int tripSnapshotFreshTime = 30*60*1000;

    /**
     * for testing purposes
     */
    boolean testing;

    TripAnalysis tripAnalysis;
    public PersistentTripStorage persistentTripStorage;
    TSMSnapshotHelper tsmSnapshotHelper;

    private static TripStateMachine instance = null;

    final Object lockStateMachine = new Object();

    /**
     * if true: machine acts as if in real mode, if true: machine force starts a trip
     */
    boolean fullDetectionMode;

    RawDataPreProcessing rawDataPreProcessing;

    /**
     * true if user has force started a trip, false if trip start was detected by the app
     */
    boolean manualTripStart = false;

    /**
     * true if user has force ended a trip, false if trip end was detected by the app
     */
    boolean manualTripEnd = false;


    synchronized public static TripStateMachine getInstance(Context context, boolean testing, boolean fullDetectionMode){

        if(instance == null){
            LOG.debug( "instantiating new tsm");
            instance = new TripStateMachine(context,testing, fullDetectionMode);
        }
        LOG.debug( "returning tsm");
        return instance;

    }


    /**
     *  method to allow components of the app external to the trip state machine to get the ongoing
     * trip for showing purposes
     *
     * @return ongoing trip, if there is one.
     */
    public OngoingTripWrapper getCurrentOngoingTrip(){

        synchronized (lockStateMachine) {

            if(currentState ==state.still){
                return null;
            }else{
                return new OngoingTripWrapper(tripList, currentState.getStateInt(), currentListOfLocations);
            }

        }

    }


    public void initializeStateMachine(boolean testing){

        //checkIfItCanBeRestored();
        tsmSnapshotHelper.deleteAllSnapshotRecords();

        currentState = state.still;
        tsmSnapshotHelper.saveState(state.still.getStateInt());

        currentToBeCompared = null;

        currentListOfLocations = new ArrayList<>();
        currentListOfActivities = new ArrayList<>();
        currentListOfAccelerations = new ArrayList<>();

        temporaryListOfLocations = new LinkedList<>();

        lastInsertedLocationTS = 0;
        lastValidLocationTS = 0;

        tripList= new ArrayList<>();

        tripAnalysis = new TripAnalysis(testing);

        this.testing = testing;

        if(testing){
            tripTimeLimit = 10000;
            fullTripTimeLimit = 40000;
        }

        manualTripStart = false;
        manualTripEnd = false;

    }


    /**
     *  method that forces the start of a trip (a trip was not acknowledged to have been started by
     * the app and the user presses "Start trip" option
     */
    public void forceStartTrip(){
        synchronized (lockStateMachine) {
            currentState = state.trip;
            manualTripStart = true;
            tsmSnapshotHelper.saveState(currentState.getStateInt());
            LOG.debug( "Telling the ActivityRecognitionService to change to high accuracy mode to force start trip");
            Intent localIntentTripStarted = new Intent("FullTripStarted");
            localIntentTripStarted.putExtra("ManualTripStart",manualTripStart);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntentTripStarted);
        }
    }

    /**
     *   method that forces the end of a current (does nothing if there is no ongoing trip of if
     *  there is a trip but is considered to have no legs)
     */
    public void forceFinishTrip(boolean testing){

        synchronized (lockStateMachine) {
            if (currentState == state.still) {
                initializeStateMachine(testing);
            }else{

                manualTripEnd = true;

                if(currentState == state.trip) {

                    try{
                        ArrayList<FullTripPart> currentTrips = rawDataPreProcessing.rawDataDetection.classifyTrip(currentListOfLocations, currentListOfAccelerations, isFirstLeg());
                        tripList.addAll(currentTrips);

                    }catch(Exception e){

                        tsmSnapshotHelper.deleteAllSnapshotRecords();
                        initializeStateMachine(false);
                        LOG.debug("Exception while trying to force finish current trip");
                        LOG.debug(e.getMessage());

                        LOG.debug( "Trip force finished but discarded(no legs)");
                        // Broadcast that the full trip has finished so that other app components/modules may act on it
                        Intent localIntent = new Intent("FullTripFinished");
                        localIntent.putExtra("result", "FullTripFinished");
                        localIntent.putExtra("isTrip", false);
                        Toast.makeText(context, "Trip discarded. No legs were identified!",Toast.LENGTH_LONG).show();

                        LOG.debug("Caught exception trying force finish trip",e);

                        tsmSnapshotHelper.deleteAllSnapshotRecords();
                        initializeStateMachine(testing);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                        return;
                    }

                }

                LOG.debug( "State machine was forced to finish by the activity during a leg.");

                // Broadcast that the full trip has finished so that other app components/modules may act on it
                Intent localIntent = new Intent("FullTripFinished");
                localIntent.putExtra("result", "FullTripFinished");


                //check if any of the trip parts has 0 locations
                int ftpNumber = 0;
                for (Iterator<FullTripPart> iter = tripList.listIterator(); iter.hasNext(); ) {
                    FullTripPart a = iter.next();
                    if (a.getLocationDataContainers().size() == 0) {
                        LOG.error("FullTripPart " + ftpNumber + " has 0 locations, discarding it!");
                        ftpNumber++;
                        iter.remove();
                    }
                }

                //check if after filtering 0 locations trip parts if any trip parts are left
                if(tripList.size()>0){

                    try{

                        FullTrip fullTripToBeSaved = tripAnalysis.analyseListOfTrips(tripList, manualTripStart, manualTripEnd);

                        logFullTripInfo(fullTripToBeSaved);

                        //save the full trip persistently
                        persistentTripStorage.insertFullTripObject(fullTripToBeSaved);
                        String dateId = DateHelper.getDateFromTSString(fullTripToBeSaved.getTripList().get(0).getInitTimestamp());

                        localIntent.putExtra("date", dateId);
                        localIntent.putExtra("isTrip", true);

                        LOG.debug( "Trip force finished with "+tripList.size()+ " legs");

                        EngagementManager.getInstance().checkEngagement(context, false, true);


                    }catch(Exception e){

                        tsmSnapshotHelper.deleteAllSnapshotRecords();
                        initializeStateMachine(false);
                        LOG.debug("Exception while trying to close trip");
                        LOG.debug(e.getMessage());

                        LOG.debug( "Trip force finished but discarded(no legs)");
                        // Broadcast that the full trip has finished so that other app components/modules may act on it
                        localIntent.putExtra("isTrip", false);
                        Toast.makeText(context, "Trip discarded. No legs were identified!",Toast.LENGTH_LONG).show();

                        tsmSnapshotHelper.deleteAllSnapshotRecords();
                        initializeStateMachine(testing);

                        LOG.debug("Caught exception trying force finish trip",e);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                        return;

                    }


                }else{
                    LOG.debug( "Trip force finished but discarded(no legs)");
                    localIntent.putExtra("isTrip", false);
                    Toast.makeText(context, "Trip discarded. No legs were identified!",Toast.LENGTH_LONG).show();
                }

                tsmSnapshotHelper.deleteAllSnapshotRecords();
                initializeStateMachine(testing);

                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

            }
        }
    }

    /**
     *
     * Constructor for the TripStateMachine class
     *
     * @param context
     * @param testing - testing purposes
     * @param fullDetectionMode
     */
    private TripStateMachine(Context context, boolean testing, boolean fullDetectionMode){

        this.context = context.getApplicationContext();
        tsmSnapshotHelper = new TSMSnapshotHelper(context);
        tripAnalysis = new TripAnalysis(testing);
        persistentTripStorage = new PersistentTripStorage(context);

        this.fullDetectionMode = fullDetectionMode;

        rawDataPreProcessing = RawDataPreProcessing.getInstance(context);

        LOG.debug( "Exists saved state?:"+tsmSnapshotHelper.existsSavedState());

        //check if the key "tripstate" exists
        if(tsmSnapshotHelper.existsSavedState() && (tsmSnapshotHelper.getSavedCurrentToBeCompared(null) != null) ){

            currentToBeCompared = tsmSnapshotHelper.getSavedCurrentToBeCompared(null);

            LOG.debug( "Time of current to be Compared="+ DateHelper.getDateFromTSString(currentToBeCompared.getSysTimestamp()) +
                    " Time now:"+ DateHelper.getDateFromTSString(new DateTime(UTC).getMillis()));

            LOG.debug( "State saved:" + tsmSnapshotHelper.getSavedState(-1));

            //  check how much time has passed since the current to be compared location saved...if
            // it has more than half an hour, discard snapshot and initialize state machine
            if(currentToBeCompared.getSysTimestamp() > (new DateTime(UTC).getMillis() - tripSnapshotFreshTime)){


                LOG.debug( "Recovering state");
                currentState = getStateFromInt(tsmSnapshotHelper.getSavedState(4));
                LOG.debug( "Recovered state:"+currentState);

                currentListOfLocations = tsmSnapshotHelper.getSavedLocations();
                currentListOfActivities = tsmSnapshotHelper.getSavedActivities();
                currentListOfAccelerations = tsmSnapshotHelper.getSavedAccelerationValues();
                tripList = tsmSnapshotHelper.getSavedFullTripParts();

                if(currentState != state.still){
                    LOG.debug( "Trip state was recovered from memory. Telling the ActivityRecognitionService to change to high accuracy mode");
                    Intent localIntentTripStarted = new Intent("FullTripStartedRecovered");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntentTripStarted);
                }

                //else initialize the state machine from scratch
            }else{
                LOG.debug( "Snapshot is not fresh. Initializing State Machine from scratch.");
                initializeStateMachine(testing);
            }


        }else{
            LOG.debug( "Either saved state is null, current to be compared is null. Initializing State Machine from scratch.");
            initializeStateMachine(testing);
        }
        this.testing = testing;
    }

    /**
     *  this location is the one used to compare with further locations and decide if a trip has
     * started or not, or if a trip has ended or not.
     */
    private LocationDataContainer currentToBeCompared;

    //global

    /**
     * list of locations of the current leg.
     */
    private ArrayList<LocationDataContainer> currentListOfLocations;

    @Deprecated
    private ArrayList<ActivityDataContainer> currentListOfActivities;


    private ArrayList<AccelerationData> currentListOfAccelerations;

    /**
     *  throughout the trip, there are locations that might not belong to current leg. E.g. the leg
     * has ended but this hasn't been acknowledged yet by the trip state machine so the last
     * locations of the leg must be pushed to the next waiting event
     */
    private LinkedList<LocationDataContainer> temporaryListOfLocations = new LinkedList<>();

    /**
     * leg list
     */
    ArrayList<FullTripPart> tripList;

    /**
     *  not used anymore. First iteration of mode of transport detection (using android activity
     * recognition service data)
     *
     * @param activityDataContainer
     */
    @Deprecated
    public void insertActivityUpdate(ActivityDataContainer activityDataContainer){

        currentListOfActivities.add(activityDataContainer);
        tsmSnapshotHelper.saveActivity(activityDataContainer);


        for(ActivityDetected activityDetected: activityDataContainer.getListOfDetectedActivities()) {
            LOG.debug( "getType " + activityDetected.getType() + " getConf " + activityDetected.getConfidenceLevel());
        }

    }

    /**
     * counter of saved acceleration values - will only take snapshot of those values from 30 to 30 seconds
     */
    private ArrayList<AccelerationData> cacheAccelerationValues = new ArrayList<>();

    public synchronized void insertAccelerometerUpdate(AccelerationData accelerationData){

        //   added still state in order to try to get the first accelerations of the trip to be
        // considered - just like the locations

        if((currentState == state.trip) || (currentState == state.waitingEvent) || (currentState == state.still)) {

            currentListOfAccelerations.add(accelerationData);

            if(currentState == state.trip){
                rawDataPreProcessing.insertAcceleration(accelerationData);
            }

            if(currentState != state.still) {

                cacheAccelerationValues.add(accelerationData);

                if (cacheAccelerationValues.size() >= 30) {
                    tsmSnapshotHelper.saveAccelerationValues(cacheAccelerationValues);
                    cacheAccelerationValues = new ArrayList<>();
                }
            }
        }

    }

    long lastInsertedLocationTS = 0;
    long lastValidLocationTS = 0;


    public long getlastValidLocationTS(){

        synchronized (lockStateMachine){
            return lastValidLocationTS;
        }

    }


    public synchronized void insertLocationUpdate(LocationDataContainer locationDataContainer, boolean fromAlarm) {
        LOG.debug( "Receiving location in trip state machine");

        synchronized (lockStateMachine) {

            if(lastInsertedLocationTS == 0){
                lastInsertedLocationTS = locationDataContainer.getLocTimestamp();
            }else{

                if(lastInsertedLocationTS == locationDataContainer.getLocTimestamp()){
                    LOG.debug("Duplicate location. Discarding.");
                    return;
                }else{
                    lastInsertedLocationTS = locationDataContainer.getLocTimestamp();
                }

            }

            if ((locationDataContainer.getAccuracy() > 100) && (currentState == state.still)) {

                LOG.debug( "Still - Low accuracy - location discarded");
                return;

            }

//            if ((locationDataContainer.getAccuracy() > 200) && (currentState != state.still)) {
            if ((locationDataContainer.getAccuracy() > 150) && (currentState != state.still)) {

                LOG.debug( "In leg/waiting event - Low accuracy - location discarded");
                return;

            }


//            if(fromAlarm){
//                LOG.debug("Location from GPS - KeepAwakeAlarm");
//                if((locationDataContainer.getLocTimestamp() - lastValidLocationTS) > 1000*60*3){
//                    //3 minutes have passed since the last valid location
//                    //let's accept this location that came from the KeepAwakeAlarm
//                    LOG.debug("3 minutes have passed since the last valid location let's accept this location that came from the KeepAwakeAlarm");
//                }else{
//                    //last accepted location is fresh - less than three minutes - discard this one
//                    LOG.debug("last accepted location is fresh - less than three minutes - discard this one");
//                    return;
//                }
//            }


            // if it gets here it means that the location is accepted
            lastValidLocationTS = locationDataContainer.getSysTimestamp();

            long ts = locationDataContainer.getSysTimestamp();

            if (currentState != state.still) {

                if(currentState == state.trip) {

                    rawDataPreProcessing.insertLocation(locationDataContainer);
                }

                currentListOfLocations.add(locationDataContainer);
                tsmSnapshotHelper.saveLocation(locationDataContainer);
            }


            //synchronized (currentState) {
            switch (currentState) {

                case still:

                    //in case it's the first location received by the state machine
                    if (currentToBeCompared == null) {
                        if(!fullDetectionMode){
                            forceStartTrip();
                            LOG.debug( "First location on a force started trip");
                        }
                        else{
                            LOG.debug( "First location - full detection mode");
                        }
                        currentToBeCompared = locationDataContainer;
                        tsmSnapshotHelper.saveCurrentToBeCompared(currentToBeCompared);

                        LOG.debug("Current to be compared equals null - setting " + locationDataContainer.getLatLng().toString());

                        temporaryListOfLocations.addFirst(locationDataContainer);

                    } else {

                        LOG.debug("Distance between current to be compared - " + currentToBeCompared.getLatLng().toString() + " at " + DateHelper.getDateFromTSString(currentToBeCompared.getSysTimestamp()));
                        LOG.debug("And current location - " + locationDataContainer.getLatLng().toString() + " at " + DateHelper.getDateFromTSString(locationDataContainer.getSysTimestamp()));
                        LOG.debug("Is " + (LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared)));


                        if(!checkSuspectsStill(locationDataContainer)){
                            // we cant say that a trip has started -> return
                            LOG.debug("CheckSuspectsStill FALSE -> we cant say that a trip has started -> return");
                            return;
                        }else{
                            LOG.debug("CheckSuspectsStill TRUE -> we can now say a trip has started -> proceed");
                        }


                            LinkedList<LocationDataContainer> toBeAdded  = new LinkedList<>();

                            LOG.debug( "List of temporary location size" + temporaryListOfLocations.size());
                            LOG.debug( "Checking which locations from the temporary list of location should be added to the trip ");

                            int i = -1;
                            for (LocationDataContainer tempLCD : temporaryListOfLocations){
                                i++;

                                LOG.debug("checking location i " + tempLCD.getLatLng().toString() + "at " + DateHelper.getDateFromTSString(tempLCD.getSysTimestamp()));
                                LOG.debug("Distance between " + i + " and " + "current location is " + LocationUtils.meterDistanceBetweenTwoLocations(tempLCD, locationDataContainer));
                                LOG.debug("Time between them " + (locationDataContainer.getSysTimestamp() - tempLCD.getSysTimestamp()));


                                //todo trip start 5 minutes ago regardless of vector distance of each point to the CTBC
//                                if((LocationUtils.meterDistanceBetweenTwoLocations(tempLCD, locationDataContainer) < tripDistanceLimit)
//                                        && ((locationDataContainer.getSysTimestamp() - tempLCD.getSysTimestamp()) < tripTimeLimit )){


                                //adapted for check suspects still
//                                if((locationDataContainer.getSysTimestamp() - tempLCD.getSysTimestamp()) < tripTimeLimit){
                                if((firstSuspect.getSysTimestamp() - tempLCD.getSysTimestamp()) < tripTimeLimit){

                                    LOG.debug( "Location " + i + " should be added to the new trip: " + DateHelper.getDateFromTSString(tempLCD.getSysTimestamp()));
                                    toBeAdded.addFirst(tempLCD);

                                }else{

                                    LOG.debug( "Location " + i + " fails condition. Break!");
//                                    }

                                    break;
                                }
                            }

                            currentListOfLocations.addAll(toBeAdded);

                            //  if no location was appended to the trip that has just started,
                            // it means we have to add the last location
                            if (toBeAdded.size() == 0){

                                LOG.debug("No locations to be added in a 100meter/5 min radius, trying to get the last location");
                                // if temporary list of locations has locations, add the last one inserted
                                if (temporaryListOfLocations.size() > 0){
                                    LOG.debug("Adding location from " + DateHelper.getDateFromTSString(temporaryListOfLocations.getFirst().getSysTimestamp()));
                                    currentListOfLocations.add(temporaryListOfLocations.getFirst());
                                }else{
                                    //if not (I think I will never happen but just in case)
                                    LOG.debug("No locations to be added, plus no pior location in temp location list. Just adding current to be compared from " + DateHelper.getDateFromTSString(currentToBeCompared.getSysTimestamp()));
                                    currentListOfLocations.add(currentToBeCompared);
                                }

                            }

                            for(LocationDataContainer location : currentListOfLocations){
                                tsmSnapshotHelper.saveLocation(location);
                            }

                            currentToBeCompared = locationDataContainer;
                            tsmSnapshotHelper.saveCurrentToBeCompared(locationDataContainer);

                            LOG.debug( "Looking for accelerations that happened after the location acknowledged as trip start " + DateHelper.getDateFromTSString(currentListOfLocations.get(0).getSysTimestamp()));

                            insertSortedLocsAccels(currentListOfLocations, currentListOfAccelerations);

                            currentState = state.trip;
                            tsmSnapshotHelper.saveState(state.trip.getStateInt());
                            //currentListOfLocations.add(locationDataContainer);

                            LOG.debug( "From still to trip");

                            LOG.debug( "Telling the ActivityRecognitionService to change to high accuracy mode");
                            Intent localIntentTripStarted = new Intent("FullTripStarted");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntentTripStarted);


                    }

                    break;

                case trip:
                    LOG.debug( "  Current state: Trip");

                    //todo workaround for forcestart trip
                    if(currentToBeCompared == null){
                        currentToBeCompared = locationDataContainer;
                    }

                    // todo finish trip is no fresh locations for 30 minutes
                    // todo uncomment to next version

                    long differenceFromLastValid;

                    long lastLocationTS;
                    if(currentListOfLocations.size() == 0){
                        lastLocationTS = 0;
                        differenceFromLastValid = 0;
                    }else if(currentListOfLocations.size() == 1){
                        differenceFromLastValid = 0;
                        lastLocationTS = locationDataContainer.getSysTimestamp();
                    }else{
                        lastLocationTS = locationDataContainer.getSysTimestamp();
                        differenceFromLastValid = lastLocationTS - currentListOfLocations.get(currentListOfLocations.size()-2).getSysTimestamp();

                        LOG.debug("curr to size -2 " + LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentListOfLocations.get(currentListOfLocations.size()-2)));
                    }

                    LOG.debug("difference from current to ctbc" + (locationDataContainer.getSysTimestamp() - currentToBeCompared.getSysTimestamp()) + " ms");
                    LOG.debug("difference from last valid" + differenceFromLastValid + " ms");
                    LOG.debug("curr to ctbc" + LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared));


                    //if last valid location older than 30 minutes and new location is within the trip distance radius
                    if((LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared) <= tripDistanceLimit * 2.5) && (lastLocationTS != 0) &&
                            ((differenceFromLastValid > 30*1000*60))){

                        LOG.debug("Last valid location older than 30 minutes and new location is within the trip distance radius - close trip");

                        //remove this location - does not belong to this closed trip
                        currentListOfLocations.remove(currentListOfLocations.size()-1);

                        ArrayList<FullTripPart> currentTrips =
                                rawDataPreProcessing.rawDataDetection.classifyTrip(currentListOfLocations, currentListOfAccelerations, isFirstLeg());

                        tripList.addAll(currentTrips);

                        try {

                            FullTrip fullTripToBeSaved = tripAnalysis.analyseListOfTrips(tripList, manualTripStart, manualTripEnd);

                            logFullTripInfo(fullTripToBeSaved);

                            //check if any of the trip parts has 0 locations
                            boolean foundErrors = false;
                            int ftpNumber = 0;
                            for (Iterator<FullTripPart> iter = fullTripToBeSaved.getTripList().listIterator(); iter.hasNext(); ) {
                                FullTripPart a = iter.next();
                                if (a.getLocationDataContainers().size() == 0) {
                                    LOG.error("FullTripPart " + ftpNumber + " has 0 locations, discarding it!");
                                    foundErrors = true;
                                    ftpNumber++;
                                    iter.remove();
                                }
                            }

                            //save the full trip persistently
                            persistentTripStorage.insertFullTripObject(fullTripToBeSaved);

                            tsmSnapshotHelper.deleteAllSnapshotRecords();

                            String dateId = DateHelper.getDateFromTSString(fullTripToBeSaved.getTripList().get(0).getInitTimestamp());

                            initializeStateMachine(testing);

                            // Broadcast that the full trip has finished so that other app components/modules may act on it
                            Intent localIntent = new Intent("FullTripFinished");
                            localIntent.putExtra("result", "FullTripFinished");
                            localIntent.putExtra("date", dateId);
                            localIntent.putExtra("isTrip", true);

                            EngagementManager.getInstance().checkEngagement(context, false, true);

                            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
                        }catch (Exception e){

                            tsmSnapshotHelper.deleteAllSnapshotRecords();
                            initializeStateMachine(false);
                            LOG.debug("Exception while trying to force finish current trip");
                            LOG.debug(e.getMessage());

                            LOG.debug( "Trip force finished but discarded(no legs)");
                            // Broadcast that the full trip has finished so that other app components/modules may act on it
                            Intent localIntent = new Intent("FullTripFinished");
                            localIntent.putExtra("result", "FullTripFinished");
                            localIntent.putExtra("isTrip", false);
                            Toast.makeText(context, "Trip discarded. No legs were identified!",Toast.LENGTH_LONG).show();

                            LOG.debug("Caught exception trying force finish trip",e);

                            tsmSnapshotHelper.deleteAllSnapshotRecords();
                            initializeStateMachine(testing);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                            return;

                        }

                    }
                    else
                    if ((LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared) <= tripDistanceLimit)
                            && (ts - currentToBeCompared.getSysTimestamp() >= tripTimeLimit)) {
                        currentState = state.waitingEvent;
                        tsmSnapshotHelper.saveState(state.waitingEvent.getStateInt());

                        LOG.debug( "From trip to waiting event");

                        currentToBeCompared = locationDataContainer;
                        tsmSnapshotHelper.saveCurrentToBeCompared(locationDataContainer);

                        //code to add the last five minutes of the leg to the following waiting event

                        ArrayList<LocationDataContainer> legsLocationsToInsert = new ArrayList<>();
                        ArrayList<AccelerationData> legsAccelsToInsert = new ArrayList<>();

                        for (Iterator<LocationDataContainer> iter = currentListOfLocations.listIterator(); iter.hasNext(); ) {
                            LocationDataContainer a = iter.next();
                            if (locationDataContainer.getSysTimestamp() - a.getSysTimestamp() > tripTimeLimit*0.8) {
//                            if (locationDataContainer.getSysTimestamp() - a.getSysTimestamp() > tripTimeLimit*0.6) {todo changed from 0.8 to 0.6
                                legsLocationsToInsert.add(a);
                                iter.remove();
                            }
                        }

                        for (Iterator<AccelerationData> iter = currentListOfAccelerations.listIterator(); iter.hasNext(); ) {
                            AccelerationData a = iter.next();
                            if (locationDataContainer.getSysTimestamp() - a.getTimestamp() > tripTimeLimit*0.8) {
//                            if (locationDataContainer.getSysTimestamp() - a.getTimestamp() > tripTimeLimit*0.6) {todo changed from 0.8 to 0.6
                                legsAccelsToInsert.add(a);
                                iter.remove();
                            }
                        }


                        if(currentListOfLocations.size()>0) {
                            legsLocationsToInsert.add(currentListOfLocations.get(0));
                        }

                        ArrayList<FullTripPart> currentTrips =
                                rawDataPreProcessing.rawDataDetection.classifyTrip(legsLocationsToInsert, legsAccelsToInsert, isFirstLeg());

                        tripList.addAll(currentTrips);
                        tsmSnapshotHelper.saveFullTripParts(currentTrips);

                        tsmSnapshotHelper.deleteSavedLocations();

                        for(LocationDataContainer ldc : legsLocationsToInsert){
                            LOG.debug("Location kept for leg " + DateHelper.getDateFromTSString(ldc.getSysTimestamp()));
                        }

                        for(LocationDataContainer ldc : currentListOfLocations){
                            tsmSnapshotHelper.saveLocation(ldc);
                            LOG.debug("Location pushed to the waiting event " + DateHelper.getDateFromTSString(ldc.getSysTimestamp()));
                        }

                        tsmSnapshotHelper.deleteSavedAccelerationValues();
                        tsmSnapshotHelper.saveAccelerationValues(currentListOfAccelerations);


                    } else if ((LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared) >= tripDistanceLimit)) {
                        currentToBeCompared = locationDataContainer;
                        tsmSnapshotHelper.saveCurrentToBeCompared(locationDataContainer);

                        LOG.debug( "In trip - refreshed currentToBeCompared");

                        //distance covered is larger than the limit so we set a new location to be compared
                    }
                    break;

                case waitingEvent:
                    LOG.debug( "  Current state: Waiting event");


                    if ((LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared) >= tripDistanceLimit)) {


                        LOG.debug( "1st case-" + LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared));

                        LinkedList<LocationDataContainer> toBeAddedToTheNextLeg  = new LinkedList<>();

                        ListIterator<LocationDataContainer> listIter = currentListOfLocations.listIterator(currentListOfLocations.size());
                        while (listIter.hasPrevious()) {
                            LocationDataContainer prev = listIter.previous();
                            // Do something with prev here

                            if ((LocationUtils.meterDistanceBetweenTwoLocations(prev, locationDataContainer) < tripDistanceLimit)
                                    && (locationDataContainer.getSysTimestamp() - prev.getSysTimestamp() < tripTimeLimit)) {

                                LOG.debug("To Be Added loc with ts: " + DateHelper.getDateFromTSString(prev.getSysTimestamp()));

                                toBeAddedToTheNextLeg.addFirst(prev);
                                listIter.remove();

                            } else {

                                toBeAddedToTheNextLeg.addFirst(currentListOfLocations.get(currentListOfLocations.size()-1));
                                break;

                            }
                        }

                        LOG.debug("From waiting event to trip - Waiting event locations");

                        for (LocationDataContainer ldc : currentListOfLocations) {
                            LOG.debug(DateHelper.getDateFromTSString(ldc.getSysTimestamp()));
                        }

                        //currentTrip = new WaitingEvent(currentListOfLocations,0,0,null);
                        FullTripPart currentTrip = tripAnalysis.analyseTripPart(currentListOfLocations, null, false, -1, null, -1);

                        currentListOfLocations = new ArrayList<>();
                        currentListOfLocations.addAll(toBeAddedToTheNextLeg);

                        tripList.add(currentTrip);
                        tsmSnapshotHelper.saveFullTripPart(currentTrip);


                        tsmSnapshotHelper.deleteSavedLocations();

                        LOG.debug("From waiting event to trip");

                        if (currentListOfLocations.size() > 0){

                            long initTS = currentListOfLocations.get(0).getSysTimestamp();

                            for (Iterator<AccelerationData> iter = currentListOfAccelerations.listIterator(); iter.hasNext(); ) {
                                AccelerationData a = iter.next();
                                if (a.getTimestamp() < initTS) {
                                    iter.remove();
                                }else{
                                    break;
                                }
                            }

                        }


                        tsmSnapshotHelper.deleteSavedAccelerationValues();
                        tsmSnapshotHelper.saveAccelerationValues(currentListOfAccelerations);

                        for(LocationDataContainer ldc : currentListOfLocations){
                            tsmSnapshotHelper.saveLocation(ldc);
                        }

                        insertSortedLocsAccels(currentListOfLocations, currentListOfAccelerations);

                        currentState = state.trip;
                        tsmSnapshotHelper.saveState(state.trip.getStateInt());
                        currentToBeCompared = locationDataContainer;
                        tsmSnapshotHelper.saveCurrentToBeCompared(locationDataContainer);

                        LOG.debug( "From waiting event to trip");

                    } else if (fullDetectionMode && (LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared) <= tripDistanceLimit)
                            && (ts - currentToBeCompared.getSysTimestamp() >= fullTripTimeLimit)) {

                        LOG.debug( "2nd case-" + LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared));

                        LOG.debug( "FullTripEnded");
                        LOG.debug( "Telling the ActivityRecognitionService to change to power balanced mode");

                        try{

                            FullTrip fullTripToBeSaved = tripAnalysis.analyseListOfTrips(tripList, manualTripStart, manualTripEnd);

                            logFullTripInfo(fullTripToBeSaved);

                            //check if any of the trip parts has 0 locations
                            boolean foundErrors = false;
                            int ftpNumber = 0;
                            for (Iterator<FullTripPart> iter = fullTripToBeSaved.getTripList().listIterator(); iter.hasNext(); ) {
                                FullTripPart a = iter.next();
                                if (a.getLocationDataContainers().size() == 0) {
                                    LOG.error("FullTripPart " + ftpNumber + " has 0 locations, discarding it!");
                                    foundErrors = true;
                                    ftpNumber++;
                                    iter.remove();
                                }
                            }

                            //save the full trip persistently
                            persistentTripStorage.insertFullTripObject(fullTripToBeSaved);

                            tsmSnapshotHelper.deleteAllSnapshotRecords();

                            String dateId = DateHelper.getDateFromTSString(fullTripToBeSaved.getTripList().get(0).getInitTimestamp());

                            initializeStateMachine(testing);

                            // Broadcast that the full trip has finished so that other app components/modules may act on it
                            Intent localIntent = new Intent("FullTripFinished");
                            localIntent.putExtra("result", "FullTripFinished");
                            localIntent.putExtra("date", dateId);
                            localIntent.putExtra("isTrip", true);

                            EngagementManager.getInstance().checkEngagement(context, false, true);

                            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                        }catch (Exception e){

                            tsmSnapshotHelper.deleteAllSnapshotRecords();
                            initializeStateMachine(false);
                            LOG.debug("Exception while trying close current trip");
                            LOG.debug(e.getMessage());

                            LOG.debug( "Trip force finished but discarded(no legs)");
                            // Broadcast that the full trip has finished so that other app components/modules may act on it
                            Intent localIntent = new Intent("FullTripFinished");
                            localIntent.putExtra("result", "FullTripFinished");
                            localIntent.putExtra("isTrip", false);
                            Toast.makeText(context, "Trip discarded. No legs were identified!",Toast.LENGTH_LONG).show();

                            LOG.debug("Caught exception trying force finish trip",e);

                            tsmSnapshotHelper.deleteAllSnapshotRecords();
                            initializeStateMachine(testing);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

                            return;

                        }

                    }

                    break;

                //}
            }

        }
    }


    boolean currentlySuspecting = false;

    int locationsInsideRadius = 0;              //tripDistanceLimit
    int numberOfReallyCloseLocations = 0;       //tripDistanceLimit /2
    int numberOfCloseLocations = 0;             //tripDistanceLimit /4

    ArrayList<Integer> suspectIndexes = new ArrayList<>();
    int locationsOutsideRadius = 0;

    LocationDataContainer firstSuspect = null;

    private void reinitializeSuspectStillData(){
        suspectIndexes = new ArrayList<>();
        currentlySuspecting = false;
        locationsOutsideRadius = 0;
        locationsInsideRadius = 0;
        numberOfCloseLocations = 0;
        numberOfReallyCloseLocations = 0;
    }

    /**
     *
     *  this method uses the last known locations to decide if as trip has really been started or if
     * it probably is a false start. Whenever a location that should trigger a trip start is received,
     * the trip start is delayed until we have evidence that that location isn't an outlier.
     *
     * @param locationDataContainer - current location received in the TSM (to be checked)
     * @return true to continue -> trip started
     *          false to return(either trip was discarded because of suspects or still no
     *          confirmation that the trip has started)
     */

    private boolean checkSuspectsStill(LocationDataContainer locationDataContainer) {

        int MIN_NUMBER_OF_ASCENDING_LOCATIONS = 5;

        int MIN_NUMBER_OF_REALLY_CLOSE_LOCATIONS = 2;
        int MIN_NUMBER_OF_CLOSE_LOCATIONS = 3;
        int MIN_NUMBER_LOCATIONS_INSIDE_RADIUS = 5;

        int MIN_LOCATIONS_OUTSIDE_RADIUS_TRIP_START = 8;
        int MIN_LOCATIONS_OUTSIDE_RADIUS_LETS_CHECK_DISTANCES = 3;

        double ASCENDING_ORDER_DISTANCE_ALPHA = 0.8;

        //add all suspects to current list of locations by order AFTER filtering them by some reasonable criteria, e.g., increasing distance to source.

//        Set<Integer> LocsToBeRemoved = new LinkedHashSet<>();
        double currentDistanceToCTBC = LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, currentToBeCompared);

        temporaryListOfLocations.addFirst(locationDataContainer);

        //if we are in still state, current to be compared is the first one inserted on the temporaryListOfLocations, and therefore the last

        // outside radius
        if (currentDistanceToCTBC >= tripDistanceLimit) {

            locationsOutsideRadius++;

            if(currentlySuspecting){

                //we assume we are in trip if there are 10 locations outside the radius
                if(locationsOutsideRadius >= MIN_LOCATIONS_OUTSIDE_RADIUS_TRIP_START){

                    reinitializeSuspectStillData();
                    return true;

                //let's check suspects
                }else if(locationsOutsideRadius >= MIN_LOCATIONS_OUTSIDE_RADIUS_LETS_CHECK_DISTANCES){

                    int numConsistentLocationsWithTrip = 0;

                    //let's check number of suspects
                    //we start on the location before the last -> the last is always the current to be compared (add first to list...in the end it will the last of the list)
                    for (int i = temporaryListOfLocations.size() - 3; i >= 0; i--) {
                        LocationDataContainer secondNext = temporaryListOfLocations.get(i), firstNext = temporaryListOfLocations.get(i + 1);
                        //we go through the list... we check if the location next to the current is more than 0.8 times the distance of the last to the current to be compared
                        if (LocationUtils.meterDistanceBetweenTwoLocations(secondNext, currentToBeCompared) >
                                LocationUtils.meterDistanceBetweenTwoLocations(firstNext, currentToBeCompared) * ASCENDING_ORDER_DISTANCE_ALPHA) {

                            numConsistentLocationsWithTrip++;

                        }
                    }

                    if (numConsistentLocationsWithTrip >= MIN_NUMBER_OF_ASCENDING_LOCATIONS){
                        reinitializeSuspectStillData();
                        return true;
                    }

                }

            }else{
                firstSuspect = locationDataContainer;
                currentlySuspecting = true;

                LOG.debug( "Started suspecting - broadcasting");
                Intent localIntentTripStarted = new Intent("StartedSuspecting");
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntentTripStarted);
            }

        // inside radius
        }else{
            //we are already suspecting a trip has started
            if(currentlySuspecting){

                if (currentDistanceToCTBC < (tripDistanceLimit/4)){
                    numberOfReallyCloseLocations++;
                    numberOfCloseLocations++;
                    locationsInsideRadius++;
                }else
                    if (currentDistanceToCTBC < tripDistanceLimit/2){
                    numberOfCloseLocations++;
                    locationsInsideRadius++;
                }else{
                    locationsInsideRadius++;
                }

                // false trip detected
                if((numberOfReallyCloseLocations >= MIN_NUMBER_OF_REALLY_CLOSE_LOCATIONS)
                        || (numberOfCloseLocations >= MIN_NUMBER_OF_CLOSE_LOCATIONS)
                            || (locationsInsideRadius >= MIN_NUMBER_LOCATIONS_INSIDE_RADIUS)){

                    //reinitialize
                    reinitializeSuspectStillData();

                    // NOTE: let's not clear the temporary list of locations
//                    temporaryListOfLocations.clear();
//                    temporaryListOfLocations.add(currentToBeCompared);
                }

            }
        }

        return false;
    }


    /**
     *  this method inserts locations and accelerations in the raw data pre processing. Since raw date
     *  pre processing will act in a "real time manner", we must ensure that both locations and
     *  accelearation are inserted ordered by the timestamp.
     *
     * @param locationsToInsert
     * @param accelerationsToInsert
     */
    public void insertSortedLocsAccels(ArrayList<LocationDataContainer> locationsToInsert, ArrayList<AccelerationData> accelerationsToInsert){

        int numAccels = accelerationsToInsert.size();

        long firstLocTS = 0;
        if (locationsToInsert.size() > 0){
            firstLocTS = locationsToInsert.get(0).getSysTimestamp();
        }

        int currAccel = 0;

        if(numAccels == 0){
            for(LocationDataContainer location : locationsToInsert) {
                rawDataPreProcessing.insertLocation(location);
            }
            return;
        }

        for(LocationDataContainer location : locationsToInsert){

            while((accelerationsToInsert.get(currAccel).getTimestamp() <= location.getSysTimestamp())){

                if(currAccel == (numAccels - 1)) break;

                if(accelerationsToInsert.get(currAccel).getTimestamp() > firstLocTS){

                    rawDataPreProcessing.insertAcceleration(accelerationsToInsert.get(currAccel));

                    LOG.debug( "Inserting acceleration (appended from still state) in classifier with ts " + DateHelper.getDateFromTSString(accelerationsToInsert.get(currAccel).getTimestamp()));
                }

                currAccel++;

            }

            LOG.debug( "Inserting Location (appended from still state) in classifier with ts " + DateHelper.getDateFromTSString(location.getSysTimestamp()));

            rawDataPreProcessing.insertLocation(location);
        }

        while(currAccel < numAccels){

            if(accelerationsToInsert.get(currAccel).getTimestamp() > firstLocTS){

                rawDataPreProcessing.insertAcceleration(accelerationsToInsert.get(currAccel));

                LOG.debug( "Inserting acceleration (After last location) (appended from still state) in classifier with ts " + DateHelper.getDateFromTSString(accelerationsToInsert.get(currAccel).getTimestamp()));
            }

            currAccel++;
        }

    }


    /**
     * method used to log full trip info
     *
     * @param fullTripToBeSaved to be logged
     */
    private void logFullTripInfo(FullTrip fullTripToBeSaved) {

        LOG.debug("---------------------------------------------------------------------");
        LOG.debug("---------------------------------------------------------------------");

        LOG.debug(DateHelper.getDateFromTSString(fullTripToBeSaved.getInitTimestamp()));
        LOG.debug(DateHelper.getDateFromTSString(fullTripToBeSaved.getEndTimestamp()));


        int j = 0;
        for (FullTripPart ftp : fullTripToBeSaved.getTripList()) {

            LOG.debug("---------------------------------------------------------------------");


            LOG.debug("!!! init time ftp " + DateHelper.getDateFromTSString(ftp.getInitTimestamp()));
            LOG.debug("!!! end time ftp " + DateHelper.getDateFromTSString(ftp.getEndTimestamp()));


            LOG.debug("!!! is trip" + ftp.isTrip() + "");
            if (ftp.isTrip()) {

                int size = ((Trip) ftp).getAccelerationData().size();

                if (((Trip) ftp).getAccelerationData().size() > 0) {

                    LOG.debug("!!! accel " + DateHelper.getDateFromTSString(((Trip) ftp).getAccelerationData().get(0).getTimestamp()));
                    LOG.debug("!!! accel " + DateHelper.getDateFromTSString(((Trip) ftp).getAccelerationData().get(size - 1).getTimestamp()));
                } else {

                    LOG.debug("!!! accel", "No acceleration values in this leg");
                }

            }

            for (LocationDataContainer locationDataContainer : ftp.getLocationDataContainers()) {

                LOG.debug( "!!! j=" + j +
                        DateHelper.getDateFromTSString(locationDataContainer.getSysTimestamp()) + " " + "lat:" + locationDataContainer.getLatitude() + " lng:" + locationDataContainer.getLongitude() + " + acc: " + locationDataContainer.getAccuracy());
                j++;
            }

        }
    }


    /**
     * @return true if there is no leg yet on the ongoing trip, false otherwise
     */
    public boolean isFirstLeg() {
        return  (tripList.size()==0);
    }


    public state getStateFromInt(int savedState){

        switch(savedState){
            case 0:
                return state.still;
            case 1:
                return state.trip;
            case 2:
                return state.waitingEvent;
        }
        return null;
    }

    public enum state{
        still(0),
        trip(1),
        waitingEvent(2)
        ;

        private final int stateInt;

        state(int stateInt) {
            this.stateInt = stateInt;
        }

        public int getStateInt() {
            return this.stateInt;
        }


    }

}
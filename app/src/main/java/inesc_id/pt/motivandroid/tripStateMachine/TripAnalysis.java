package inesc_id.pt.motivandroid.tripStateMachine;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import inesc_id.pt.motivandroid.BuildConfig;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.myTrips.sort.SortFullTripValidationWrapperReverse;
import inesc_id.pt.motivandroid.tripStateMachine.dataML.SpeedDistanceWrapper;
import inesc_id.pt.motivandroid.utils.DateHelper;
import inesc_id.pt.motivandroid.utils.LocationUtils;
import inesc_id.pt.motivandroid.utils.MiscUtils;
import inesc_id.pt.motivandroid.utils.NumbersUtil;
import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.ActivityDataContainer;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;
import inesc_id.pt.motivandroid.data.tripData.WaitingEvent;

/**
 *  TripAnalysis
 *
 *   Class responsible for:
 *      Generating a leg (from a list of locations, mode of transport)
 *      Generating a trip (from multiple legs)
 *      Merge legs/waiting events
 *      Split legs/waiting events
 *      Remove legs/waiting events
 *      Find suspicious legs and filter them
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

public class TripAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(TripAnalysis.class.getSimpleName());


    private static String LOG_TAG = "TripAnalysis";

    public TripAnalysis(boolean testing) {

        if(testing) batchTimeConstant = 10000;

    }

    private long batchTimeConstant = 60*1000*3;  //3600*1000*2= 2 minutes in milliseconds

    public ArrayList<Integer> getStillSegments(ArrayList<FullTripPart> fullTripParts){

        ArrayList<Integer> result = new ArrayList<>();

        int i = 0;
        for (FullTripPart ftp : fullTripParts){

            if(ftp.isTrip()){

                if(((Trip) ftp).getSugestedModeOfTransport() == ActivityDetected.keys.still){
                    result.add(i);
                }

            }
            i++;
        }

        return result;

    }

    public int getNumberOfLegs(ArrayList<FullTripPart> fullTripParts){

        int result = 0;
        for (FullTripPart ftp : fullTripParts){

            if(ftp.isTrip()){
                result++;
            }

        }

        return result;
    }


    // method called whenever the state machine acknowledges that a trip (former full trip) has ended, computes the stats necessary
    // receives a list of full trip parts (trips/legs or waiting events)
    // returns the FullTrip
    public FullTrip analyseListOfTrips(ArrayList<FullTripPart> fullTripParts, boolean manualTripStart, boolean manualTripEnd) throws Exception{

        long distanceTraveled = 0;
        float maxSpeed = 0;

        float avgSpeed;

        LOG.debug("analyseListOfTrips - before filtering");

        int count = 0;
        for(FullTripPart ftp : fullTripParts){

            LOG.debug("FTP " + count + " isLeg() " + ftp.isTrip() + " initTS " + DateHelper.getDateFromTSString(ftp.getInitTimestamp()) + " endTS " + DateHelper.getDateFromTSString(ftp.getEndTimestamp()));
            Log.e("TA Filter stiils","FTP " + count + " isLeg() " + ftp.isTrip() + " initTS " + DateHelper.getDateFromTSString(ftp.getInitTimestamp()) + " endTS " + DateHelper.getDateFromTSString(ftp.getEndTimestamp()));

            if(ftp.isTrip()){

                Trip trip = (Trip) ftp;

                logSuspectStuff(trip, count, true);

                SpeedDistanceWrapper speedDistanceWrapper = RawDataDetection.computeSpeedsDistance(ftp.getLocationDataContainers());

                LOG.debug("FTP " + count + " isLeg() " + ftp.isTrip() + " legDistance " + speedDistanceWrapper.getDistance() + " prevdist" + trip.getDistanceTraveled() + " mode " + trip.getSugestedModeOfTransport());

                if(trip.getSugestedModeOfTransport() == ActivityDetected.keys.still){

                    LocationDataContainer initLoc = trip.getLocationDataContainers().get(0);
                    LocationDataContainer endLoc = trip.getLocationDataContainers().get(trip.getLocationDataContainers().size()-1);

                    if(LocationUtils.meterDistanceBetweenTwoLocations(initLoc, endLoc) > (TripStateMachine.tripDistanceLimit * 2.5)){
                        trip.setSugestedModeOfTransport(ActivityDetected.keys.train);
                        LOG.debug("Switched still for train - leg distance: " + LocationUtils.meterDistanceBetweenTwoLocations(initLoc, endLoc));
                    }

                }


                distanceTraveled+=trip.getDistanceTraveled();

                if(trip.getMaxSpeed()>maxSpeed){
                    maxSpeed = trip.getMaxSpeed();
                }

            }

            count++;
        }

        //workaround for 0 meters legs
        for (FullTripPart ftp : fullTripParts){

            if(ftp.isTrip()) {
                Trip trip = (Trip) ftp;

                if(trip.getDistanceTraveled() <= 1){
                    trip.setDistanceTraveled(20);
                }
            }

        }

        //check if there are "still" segments
        ArrayList<Integer> stillTripParts = getStillSegments(fullTripParts);
        int numberOfLegs = getNumberOfLegs(fullTripParts);

        //if all trip parts are still
        if(stillTripParts.size() == fullTripParts.size()){

            throw new InvalidTripException("stillTripParts.size() == fullTripParts.size()");


        }

        //if all trip parts other than waiting events are still
        if(stillTripParts.size() == numberOfLegs){

            throw new InvalidTripException("stillTripParts.size() == numberOfLegs");

        }


        int decrementedIndex = 0;

        for(int index : stillTripParts){

            //first
            if ((index - decrementedIndex) == 0){

                LOG.debug("-Still is first ");

                //the next one is a waiting event - remove both trip parts
                if(!fullTripParts.get(index-decrementedIndex+1).isTrip()){

                    LOG.debug("-- 1.Still 2.WE");


                    fullTripParts.remove(index - decrementedIndex + 1);
                    fullTripParts.remove(index - decrementedIndex);

                    decrementedIndex = decrementedIndex + 2;

                //next is leg - merge both
                }else{

                    LOG.debug("-- 1.Still 2.Leg");

                    ArrayList<FullTripPart> toBeMerged = new ArrayList<>();

                    toBeMerged.add(fullTripParts.get(index - decrementedIndex));
                    toBeMerged.add(fullTripParts.get(index - decrementedIndex +1));

                    int mode = ((Trip)fullTripParts.get(index - decrementedIndex + 1)).getSugestedModeOfTransport();

                    Trip tripMerged = joinFullTripParts(toBeMerged, mode, -1);

                    fullTripParts.remove(index - decrementedIndex + 1);
                    fullTripParts.remove(index - decrementedIndex);

                    fullTripParts.add(index - decrementedIndex, tripMerged);

                    decrementedIndex = decrementedIndex + 1;

                }

            //last
            }else if((index - decrementedIndex) == fullTripParts.size()-1){

                LOG.debug("-Still is last ");


                if(!fullTripParts.get(index - decrementedIndex -1).isTrip()){

                    LOG.debug("-- 1.WE 2.Still");


                    fullTripParts.remove(index - decrementedIndex);
                    fullTripParts.remove(index - decrementedIndex -1);

                    decrementedIndex = decrementedIndex + 2;

                    //before is leg - merge both
                }else{

                    LOG.debug("-- 1.Leg 2.Still");

                    LocationDataContainer initLastLegStillLocation = fullTripParts.get(index - decrementedIndex).getLocationDataContainers().get(0);
                    LocationDataContainer endLastLegStillLocation = fullTripParts.get(index - decrementedIndex).getLocationDataContainers().get(fullTripParts.get(index - decrementedIndex).getLocationDataContainers().size()-1);

                    if(LocationUtils.meterDistanceBetweenTwoLocations(initLastLegStillLocation, endLastLegStillLocation) < TripStateMachine.tripDistanceLimit){

                        LOG.debug("---- Small still - removing");

                        fullTripParts.remove(index - decrementedIndex);
                        decrementedIndex = decrementedIndex + 1;

                    }else{

                        LOG.debug("---- Not so small still - merging with last leg");

                        ArrayList<FullTripPart> toBeMerged = new ArrayList<>();

                        toBeMerged.add(fullTripParts.get(index - decrementedIndex -1));
                        toBeMerged.add(fullTripParts.get(index - decrementedIndex));

                        int mode = ((Trip)fullTripParts.get(index - decrementedIndex - 1)).getSugestedModeOfTransport();

                        Trip tripMerged = joinFullTripParts(toBeMerged, mode,-1);

                        fullTripParts.remove(index - decrementedIndex);
                        fullTripParts.remove(index - decrementedIndex -1);

                        fullTripParts.add(index - decrementedIndex -1, tripMerged);

                        decrementedIndex = decrementedIndex + 1;

                    }
//                    if(fullTripParts.get(index - decrementedIndex).getLocationDataContainers().get)

                }
            //middle
            }else{

                LOG.debug("-Still is in the middle");

                boolean prevIsTrip = fullTripParts.get(index-decrementedIndex -1).isTrip();
                boolean nextIsTrip = fullTripParts.get(index-decrementedIndex +1).isTrip();

                FullTripPart prevFTP = fullTripParts.get(index-decrementedIndex -1);
                FullTripPart nextFTP = fullTripParts.get(index-decrementedIndex +1);

                if(!prevIsTrip && !nextIsTrip){

                    LOG.debug("-- prev.WE next.WE");


                    ArrayList<LocationDataContainer> toBeMerged = new ArrayList<>();

                    toBeMerged.addAll(prevFTP.getLocationDataContainers());
                    toBeMerged.addAll(fullTripParts.get(index - decrementedIndex   ).getLocationDataContainers());
                    toBeMerged.addAll(nextFTP.getLocationDataContainers());

                    FullTripPart mergedWE = analyseTripPart(toBeMerged, null, false, -1, null,-1);

                    fullTripParts.remove(index - decrementedIndex +1);
                    fullTripParts.remove(index - decrementedIndex);
                    fullTripParts.remove(index - decrementedIndex -1);

                    fullTripParts.add(index - decrementedIndex -1, mergedWE);

                    decrementedIndex = decrementedIndex + 2;

                }else if(prevIsTrip && nextIsTrip && (((Trip)prevFTP).getSugestedModeOfTransport() == ((Trip)nextFTP).getSugestedModeOfTransport())){

                    LOG.debug("-- prev.Leg next.Leg same mode");

                    ArrayList<FullTripPart> toBeMerged = new ArrayList<>();

                    toBeMerged.add(fullTripParts.get(index - decrementedIndex - 1));
                    toBeMerged.add(fullTripParts.get(index - decrementedIndex));
                    toBeMerged.add(fullTripParts.get(index - decrementedIndex + 1));

                    int mode = ((Trip)prevFTP).getSugestedModeOfTransport();

                    Trip tripMerged = joinFullTripParts(toBeMerged, mode, -1);

                    fullTripParts.remove(index - decrementedIndex + 1);
                    fullTripParts.remove(index - decrementedIndex);
                    fullTripParts.remove(index - decrementedIndex -1);

                    fullTripParts.add(index - decrementedIndex -1, tripMerged);

                    decrementedIndex = decrementedIndex + 2;

                }else if(prevIsTrip && nextIsTrip){

                    LOG.debug("-- prev.Leg next.Leg different mode");


                    ArrayList<FullTripPart> toBeMerged = new ArrayList<>();

                    toBeMerged.add(fullTripParts.get(index - decrementedIndex -1));
                    toBeMerged.add(fullTripParts.get(index - decrementedIndex));

                    int mode = ((Trip)prevFTP).getSugestedModeOfTransport();

                    Trip tripMerged = joinFullTripParts(toBeMerged, mode, -1);

                    fullTripParts.remove(index - decrementedIndex);
                    fullTripParts.remove(index - decrementedIndex -1);

                    fullTripParts.add(index - decrementedIndex -1, tripMerged);

                    decrementedIndex = decrementedIndex + 1;

                }else if(!prevIsTrip){

                    LOG.debug("-- prev.WE next.Leg");

                    ArrayList<LocationDataContainer> toBeMerged = new ArrayList<>();

                    toBeMerged.addAll(prevFTP.getLocationDataContainers());
                    toBeMerged.addAll(fullTripParts.get(index - decrementedIndex   ).getLocationDataContainers());

                    FullTripPart mergedWE = analyseTripPart(toBeMerged, null, false, -1, null, -1);

                    fullTripParts.remove(index - decrementedIndex);
                    fullTripParts.remove(index - decrementedIndex -1);

                    fullTripParts.add(index - decrementedIndex -1, mergedWE);

                    decrementedIndex = decrementedIndex + 1;

                }else if(!nextIsTrip){

                    LOG.debug("-- next.WE");

                    ArrayList<LocationDataContainer> toBeMerged = new ArrayList<>();

                    toBeMerged.addAll(fullTripParts.get(index - decrementedIndex   ).getLocationDataContainers());
                    toBeMerged.addAll(nextFTP.getLocationDataContainers());

                    FullTripPart mergedWE = analyseTripPart(toBeMerged, null, false, -1, null,-1);

                    fullTripParts.remove(index - decrementedIndex + 1);
                    fullTripParts.remove(index - decrementedIndex);

                    fullTripParts.add(index - decrementedIndex, mergedWE);

                    decrementedIndex = decrementedIndex + 1;

                }

            }

        }

        LOG.debug("analyseListOfTrips - after filtering");

        count = 0;
        for(FullTripPart ftp : fullTripParts){
            LOG.debug("FTP " + count + " isLeg() " + ftp.isTrip() + " initTS " + DateHelper.getDateFromTSString(ftp.getInitTimestamp()) + " endTS " + DateHelper.getDateFromTSString(ftp.getEndTimestamp()));
            Log.e("TA Filter stiils","FTP " + count + " isLeg() " + ftp.isTrip() + " initTS " + DateHelper.getDateFromTSString(ftp.getInitTimestamp()) + " endTS " + DateHelper.getDateFromTSString(ftp.getEndTimestamp()));

            if (ftp.isTrip()) {
                logSuspectStuff((Trip) ftp, count, false);
            }

            count++;
        }

        long initTimestamp = fullTripParts.get(0).getInitTimestamp();
        long endTimestamp = fullTripParts.get(fullTripParts.size()-1).getEndTimestamp();

        LOG.debug("Distance traveled: " + distanceTraveled+"");

        avgSpeed = NumbersUtil.getSegmentSpeedKm(distanceTraveled,endTimestamp-initTimestamp);

        String uid;

        try {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (Exception e){
            LOG.debug("Caught exception trying to retrieve the users UID");
//            FirebaseTokenManager.getInstance().getLastKnownUID()
            uid = "";
        }

//        if(filterFinalSuspectTripParts(fullTripParts)){
//            LOG.error("Filtered leg from trip " + DateHelper.getDateFromTSString(initTimestamp));
//        }
//
//        //if all trip parts other than waiting events are still
//        if(fullTripParts.size() == 0){
//
//            throw new InvalidTripException("After filterFinalSuspectTripParts no legs - discarding trip");
//
//        }

        LOG.debug("analyseListOfTrips - after SUPER filtering");

        count = 0;
        for(FullTripPart ftp : fullTripParts){
            LOG.debug("FTP " + count + " isLeg() " + ftp.isTrip() + " initTS " + DateHelper.getDateFromTSString(ftp.getInitTimestamp()) + " endTS " + DateHelper.getDateFromTSString(ftp.getEndTimestamp()));
            Log.e("TA Filter stiils","FTP " + count + " isLeg() " + ftp.isTrip() + " initTS " + DateHelper.getDateFromTSString(ftp.getInitTimestamp()) + " endTS " + DateHelper.getDateFromTSString(ftp.getEndTimestamp()));

            if (ftp.isTrip()) {
                logSuspectStuff((Trip) ftp, count, false);
            }

            count++;
        }

        return new FullTrip(fullTripParts,initTimestamp,endTimestamp,distanceTraveled,endTimestamp-initTimestamp, avgSpeed,maxSpeed, MiscUtils.getDeviceName(), "Android AppVersion " + BuildConfig.VERSION_CODE , MiscUtils.getOSVersion(), uid, false, manualTripStart, manualTripEnd);
    }

    public boolean filterFinalSuspectTripParts(ArrayList<FullTripPart> fullTripParts){

        boolean hasFiltered = false;

        LOG.debug("filterFinalSuspectTripParts");
        LOG.debug("ind " + (fullTripParts.size()-1));

        LocationDataContainer finalLocation = fullTripParts.get(fullTripParts.size()-1).getLocationDataContainers().get(fullTripParts.get(fullTripParts.size()-1).getLocationDataContainers().size()-1);

        for (int ind = fullTripParts.size()-1; ind >= 0; ind--){

            FullTripPart curr = fullTripParts.get(ind);

            if (curr.isTrip()){

                LOG.debug("is leg - check");

//                if(LocationUtils.meterDistanceBetweenTwoLocations(curr.getLocationDataContainers().get(0),
//                        curr.getLocationDataContainers().get(curr.getLocationDataContainers().size() - 1)) < 40){

                if(LocationUtils.meterDistanceBetweenTwoLocations(curr.getLocationDataContainers().get(0),
                        finalLocation) < (TripStateMachine.tripDistanceLimit/2.0)){

                    LOG.debug("Leg location close to final trip location (less than 40 meters)");

                    if (((Trip) curr).getSugestedModeOfTransport() != ActivityDetected.keys.walking){

                        LOG.debug("Leg not walking");

                        if ((((Trip) curr).getAverageSpeed() * 12) < (((Trip) curr).getMaxSpeed())){

                            LOG.debug("averageSpeed*12 < max speed. Removing leg");

                            fullTripParts.remove(ind);
                            hasFiltered = true;

                        }else{
                            LOG.debug("Consistent average speed - SKIPPING FILTERING");
                            break;
                        }

                    }else{
                        LOG.debug("Walking - SKIPPING FILTERING");
                        break;
                    }

                }else{
                    LOG.debug("Leg first location FAR AWAY FROM TRIP END - SKIPPING FILTERING");

                    break;
                }

            }else{
                LOG.debug("Waiting event last - removing waiting event");
                fullTripParts.remove(ind);
                hasFiltered = true;

            }

        }

        return hasFiltered;

    }

//    public boolean filterSuspectWaitingEventLegs(ArrayList<FullTripPart> fullTripParts){
//
//        boolean areThereMutipleWE = (getNumberOfLegs(fullTripParts) <= fullTripParts.size()-2);
//
//        if (areThereMutipleWE){
//
//            ArrayList<Integer> waitingEventIndexes = new ArrayList<>();
//
//            int i = 0;
//
//            for (FullTripPart fullTripPart : fullTripParts){
//                if (!fullTripPart.isTrip()) waitingEventIndexes.add(i);
//                i++;
//            }
//
//            WaitingEvent first = (WaitingEvent) fullTripParts.get(waitingEventIndexes.get(0));
//            double firstWEAvgLat = first.getAverageLocationLatitude();
//            double firstWEAvgLon = first.getAverageLocationLongitude();
//
//            LocationDataContainer avgfirstLocation = new LocationDataContainer();
//            avgfirstLocation.setLatitude(firstWEAvgLat);
//            avgfirstLocation.setLongitude(firstWEAvgLon);
//
//            WaitingEvent second = (WaitingEvent) fullTripParts.get(waitingEventIndexes.get(1));
//            double secondWEAvgLat = second.getAverageLocationLatitude();
//            double secondWEAvgLon = second.getAverageLocationLongitude();
//
//            LocationDataContainer avgSecondLocation = new LocationDataContainer();
//            avgSecondLocation.setLatitude(secondWEAvgLat);
//            avgSecondLocation.setLongitude(secondWEAvgLon);
//
//            if (LocationUtils.meterDistanceBetweenTwoLocations(avgfirstLocation, avgSecondLocation) < (TripStateMachine.tripDistanceLimit/2.0)){
//
//
//
//            }
//
//        }
//        return false;
//
//    }

    private void  logSuspectStuff(Trip trip, int count, boolean before) {

        try {

            String indicator;

            if (before) {
                indicator = "B";
            } else {
                indicator = "A";
            }

            LOG.debug("--PP--" + indicator + " leg #" + count);
            LOG.debug("--PP--" + indicator + " distance " + trip.getDistanceTraveled() + " time " + TimeUnit.MILLISECONDS.toMinutes(trip.getTimeTraveled()) + " # samples " + trip.getLocationDataContainers().size());
            LOG.debug("--PP--" + indicator + " mode " + ActivityDetected.keys.transportName[trip.getSugestedModeOfTransport()] + " avg speed " + trip.getAverageSpeed() + " max speed " + trip.getMaxSpeed());

            if (trip.getLocationDataContainers().size() > 1) {

                LOG.debug("--PP--" + indicator + " distance between init and end locations" +
                        LocationUtils.meterDistanceBetweenTwoLocations(trip.getLocationDataContainers().get(0),
                                trip.getLocationDataContainers().get(trip.getLocationDataContainers().size() - 1)));

            }
        }catch (Exception e){
            LOG.error("Error logging suspect legs");
        }
    }

    // computes the stats necessary (either for leg/trip or waiting event)
    // receives a list of locations, list of activities, if it is a trip or waiting event and the most probable activity in case of a trip
    // returns the FullTripPart Object (either a trip/leg or waiting event)
    public FullTripPart analyseTripPart(ArrayList<LocationDataContainer> locationDataContainers, ArrayList<ActivityDataContainer> activityDataContainers, boolean isTrip, int
            mostProbableTripActivity, ArrayList<AccelerationData> accelerationData, int correctedModeOfTransport){

        if(locationDataContainers.size()<2 && isTrip){
            return null;
        }

        long initTimestamp = locationDataContainers.get(0).getSysTimestamp();
        long endTimestamp = locationDataContainers.get(locationDataContainers.size()-1).getSysTimestamp();

        if(isTrip){

            long timeTraveled = endTimestamp - initTimestamp;
            double distanceTraveled = 0;
            float maxSpeed = 0;

            //compute average speed of segments with speed above the threshold
            //beware - speed threshold in meters per second
            final double speedThreshold = 1.08;
            int countSpeedAboveThreshold=0;
            double sumSpeedAboveThreshold=0;

            //compute time in which speed is below average
            double sumTimeSpeedBelowThreshold=0;

            //compute maxSpeed, avgSpeed, distance traveled
            LocationDataContainer lastLocation = null;

            float sumAccuracy=0;

            for (LocationDataContainer lcd : locationDataContainers) {

                sumAccuracy += lcd.getAccuracy();

                if(lastLocation != null) {

                    double distanceBetweenLastTwo = LocationUtils.meterDistanceBetweenTwoLocations(lcd, lastLocation);

                    LOG.debug( "from " + lcd.getLatitude() +","+ lcd.getLongitude() + " to " + lastLocation.getLatitude()+","+lastLocation.getLongitude());

                    LOG.debug( "distance between last two " + distanceBetweenLastTwo);
                    double timeBetweenLastTwo = (lcd.getSysTimestamp() - lastLocation.getSysTimestamp());
                    float segmentSpeed = NumbersUtil.getSegmentSpeedKm(distanceBetweenLastTwo,timeBetweenLastTwo);

                    LOG.debug( "time between " + timeBetweenLastTwo);
                    LOG.debug( "segment speed " + segmentSpeed);

                    if(segmentSpeed>maxSpeed) maxSpeed = segmentSpeed;

                    distanceTraveled = distanceTraveled + distanceBetweenLastTwo;

                    //compute speed above threshold
                    if(segmentSpeed>=speedThreshold){
                        sumSpeedAboveThreshold += segmentSpeed;
                        countSpeedAboveThreshold++;
                    }else{
                        //compute percentage of time below threshold
                        sumTimeSpeedBelowThreshold+=timeBetweenLastTwo;
                    }

                    /*if (loopIter <= locationDataContainers.size() - 2) {

                        distanceTraveled += LocationUtils.meterDistanceBetweenTwoLocations(lcd, locationDataContainers.get(loopIter + 1));
                    }*/
                }
                lastLocation = lcd;
            }

            LOG.debug( "distance traveled " + distanceTraveled);
            LOG.debug( "time traveled" + timeTraveled);

            float avgSpeed = NumbersUtil.getSegmentSpeedKm(distanceTraveled,timeTraveled);

            //compute acceleration average below threshold(0.2)
            double accelerationSumAboveThreshold=0;
            double countAccelerationAboveThreshold=0;
            final double thresholdAccelerationValue=0.2;

            //compute acceleration average
            double accelerationSum=0;

            //compute time percentage below threshold
            double sumTimeAccelerationBelowThreshold=0;
            AccelerationData lastAccelerationData = null;

            for(AccelerationData ad : accelerationData){
                Double accLength = Math.sqrt(ad.getxValue()*ad.getxValue()+ad.getyValue()*ad.getyValue()+ad.getzValue()*ad.getzValue());
                accelerationSum += accLength;

                if(accLength>=thresholdAccelerationValue){
                    accelerationSumAboveThreshold += accLength;
                    countAccelerationAboveThreshold++;
                }

                if(lastAccelerationData !=null){
                    Double accLength2 = Math.sqrt(lastAccelerationData.getxValue()*lastAccelerationData.getxValue()+ad.getyValue()*lastAccelerationData.getyValue()+ad.getzValue()*lastAccelerationData.getzValue());

                    if(accLength2<=thresholdAccelerationValue){
                        sumTimeAccelerationBelowThreshold += (ad.getTimestamp()-lastAccelerationData.getTimestamp());
                    }

                }

                lastAccelerationData = ad;

            }

            //acceleration avg
            accelerationSum = accelerationSum/accelerationData.size();

            //filtered acceleration
            if(Double.isNaN(accelerationSumAboveThreshold) || countAccelerationAboveThreshold == 0){
                accelerationSumAboveThreshold = 0;
            }else{
                accelerationSumAboveThreshold = accelerationSumAboveThreshold/countAccelerationAboveThreshold;
            }

            LOG.debug( "accelSumAboveThreshold " + accelerationSumAboveThreshold);
            LOG.debug( "countaccelabovethreshold" + countAccelerationAboveThreshold);

            //percentage of time acceleration values are below the threshold
            Double percentageOfTimeAccelerationBelowThreshold = sumTimeAccelerationBelowThreshold/(endTimestamp-initTimestamp);

            //filtered speed
            sumSpeedAboveThreshold = sumSpeedAboveThreshold/countSpeedAboveThreshold;

            if (Double.isNaN(sumSpeedAboveThreshold)){
                sumSpeedAboveThreshold = 0;
            }

            if(Double.isNaN(accelerationSum)){
                accelerationSum = 0;
            }

            //percentage of time speed values are below the threshold
            Double percentageOfTimeSpeedBelowThreshold = sumTimeSpeedBelowThreshold/(endTimestamp-initTimestamp);

            //accuracy avg (m/s)
            sumAccuracy = sumAccuracy/locationDataContainers.size();

            LOG.debug( "accelSum " + accelerationSum);
            LOG.debug( "accelAvgAboveThreshold " + accelerationSumAboveThreshold);
            LOG.debug( "speedAvgAboveThreshold " + sumSpeedAboveThreshold);
            LOG.debug( "percentage of time speed below threshold " + percentageOfTimeSpeedBelowThreshold);
            LOG.debug( "percentage of time accel below threshold " + percentageOfTimeAccelerationBelowThreshold);
            LOG.debug( "accuracy avg " + sumAccuracy);
            LOG.debug( "avg speed " + avgSpeed);
            LOG.debug( "maxspeed " + maxSpeed);

            int suggestedModality = getSuggestedModality(mostProbableTripActivity);

            return new Trip(locationDataContainers,activityDataContainers,mostProbableTripActivity,initTimestamp,endTimestamp,(long) distanceTraveled,timeTraveled/1000,avgSpeed,maxSpeed,
                    accelerationSum, accelerationData, accelerationSumAboveThreshold, percentageOfTimeAccelerationBelowThreshold, sumSpeedAboveThreshold, percentageOfTimeSpeedBelowThreshold, sumAccuracy, suggestedModality, correctedModeOfTransport);



        }else{

            double sumLat=0;
            double sumLng=0;

            for(LocationDataContainer ldc : locationDataContainers){
                sumLat+=ldc.getLatitude();
                sumLng+=ldc.getLongitude();
            }

            ArrayList<LocationDataContainer> aux = new ArrayList<>();
            aux.addAll(locationDataContainers);

            double avgLat = sumLat/locationDataContainers.size();
            double avgLng = sumLng/locationDataContainers.size();

            return new WaitingEvent(aux,initTimestamp,endTimestamp,avgLat,avgLng);
        }
    }

    public int getSuggestedModality(int detectedModality){

        switch (detectedModality){
            case ActivityDetected.keys.vehicle:
                return ActivityDetected.keys.car;
            case ActivityDetected.keys.unknown:
                return ActivityDetected.keys.other;
            case ActivityDetected.keys.tilting:
                return ActivityDetected.keys.other;
            case ActivityDetected.keys.still:
                return ActivityDetected.keys.train;
        }

        return detectedModality;

    }

    // receives a list of locations, interval initial timestamp and interval end timestamp
    // returns the list of locations within the interval defined by the init timestamp and end timestamp
    public ArrayList<LocationDataContainer> getLocationsInsideInterval(ArrayList<LocationDataContainer> locationDataContainers, long initIntervalTS, long endIntervalTS, boolean isLastLeg){

        ArrayList<LocationDataContainer> locationsInsideInterval = new ArrayList<>();

        LocationDataContainer lastAdded = null;

        LOG.debug( "init " + DateHelper.getDateFromTSString(initIntervalTS));
        LOG.debug( "end " + DateHelper.getDateFromTSString(endIntervalTS));

        for(LocationDataContainer ldc : locationDataContainers){
            LOG.debug( "ldc" + DateHelper.getDateFromTSString(ldc.getSysTimestamp()));
            if(checkIfInInterval(initIntervalTS,endIntervalTS,ldc.getSysTimestamp())){
                locationsInsideInterval.add(ldc);
                lastAdded = ldc;
                LOG.debug("inside");
            }else{
                LOG.debug( "outside");
            }
        }

        if(lastAdded != null && isLastLeg && locationDataContainers.size() > locationDataContainers.indexOf(lastAdded) + 1){
            locationsInsideInterval.add(locationDataContainers.get(locationDataContainers.indexOf(lastAdded)+1));
        }

        return locationsInsideInterval;
    }

    public LocationDataContainer getFirstLocationAfterTS(ArrayList<LocationDataContainer> locationDataContainers, long ts){

        for(LocationDataContainer ldc : locationDataContainers){

            if (ldc.getSysTimestamp() >= ts){
                return ldc;
            }
        }

        return null;
    }

    // receives a list of activities, interval initial timestamp and interval end timestamp
    // returns the list of activities within the interval defined by the init timestamp and end timestamp
    private ArrayList<ActivityDataContainer> getActivitiesInsideInterval(ArrayList<ActivityDataContainer> locationDataContainers, long initIntervalTS, long endIntervalTS){

        ArrayList<ActivityDataContainer> activitiesInsideInterval = new ArrayList<>();

        for(ActivityDataContainer adc : locationDataContainers){
            if(checkIfInInterval(initIntervalTS,endIntervalTS,adc.getTimestamp())){
                activitiesInsideInterval.add(adc);
            }
        }

        return activitiesInsideInterval;
    }

    // receives a list of acceleration data, interval initial timestamp and interval end timestamp
    // returns the list of acceleration data points within the interval defined by the init timestamp and end timestamp
    public ArrayList<AccelerationData> getAccelerationsInsideInterval(ArrayList<AccelerationData> locationDataContainers, long initIntervalTS, long endIntervalTS){

        ArrayList<AccelerationData> accelerationInsideInterval = new ArrayList<>();

        for(AccelerationData adc : locationDataContainers){
            if(checkIfInInterval(initIntervalTS,endIntervalTS,adc.getTimestamp())){
                accelerationInsideInterval.add(adc);
            }
        }

        return accelerationInsideInterval;
    }

    private int[] classifyBatchByMod(ArrayList<ActivityDataContainer> activityDataContainers, long initBatchTS, long endBatchTS) {


        ActivityDataContainer lastActivity = null;
        int[] activitiesScore = new int[9];

        for(ActivityDataContainer adc : activityDataContainers){

            if(lastActivity!=null){

                int[] actScores = classifyActivity(initBatchTS, adc.getTimestamp(), lastActivity);
                for (int i = 0; i < activitiesScore.length; ++i) {
                    activitiesScore[i] = activitiesScore[i] + actScores[i];
                }
            }

            lastActivity = adc;
        }


        //last mod
        int[] actScores = classifyActivity(initBatchTS, endBatchTS, lastActivity);
        for (int i = 0; i < activitiesScore.length; ++i) {
            activitiesScore[i] = activitiesScore[i] + actScores[i];
        }

        return activitiesScore;
    }

    //  receives an activity data container, the initial timestamp of the batch, the timestamp of the last activity
    //  returns the score assigned to that activity (array with the scores of the multiple modalities -
    //for each activity android says the probability level for each exisiting modality)
    private int[] classifyActivity(long initBatchTS, long endActivityTS, ActivityDataContainer initActivity) {

        int[] activitiesScore = new int[9];

        long initActivityTS = initActivity.getTimestamp();

        if(initActivityTS < initBatchTS) initActivityTS = initBatchTS;

        long duration = endActivityTS - initActivityTS;

        for(ActivityDetected ad : initActivity.getListOfDetectedActivities()){

            LOG.debug( "getType analysis: " + ad.getType());

            //let's ignore if activity is onfoot or still
            if((ad.getType() != ActivityDetected.keys.onfoot) && (ad.getType() != ActivityDetected.keys.unknown) && (ad.getType() != ActivityDetected.keys.tilting) && (ad.getType() <= 8))

                //if(ad.getConfidenceLevel()>60) {
                    activitiesScore[ad.getType()] += (ad.getConfidenceLevel() * duration);
                //}
            }

        return activitiesScore;
    }

    // checks if TS is within the interval defined by initTS and endTS
    private boolean checkIfInInterval(long initTS, long endTS, long TS){

        return TS >= initTS && TS < endTS;

    }

    public Trip joinFullTripParts(ArrayList<FullTripPart> tripPartsToBeJoined, int modality, int correctedModality){

        ArrayList<ActivityDataContainer> activityDataContainerArrayList = new ArrayList<>();
        ArrayList<AccelerationData> accelerationDataArrayList = new ArrayList<>();
        ArrayList<LocationDataContainer> locationDataContainerArrayList = new ArrayList<>();

        for(FullTripPart fullTripPart : tripPartsToBeJoined){

            locationDataContainerArrayList.addAll(fullTripPart.getLocationDataContainers());

            if(fullTripPart.isTrip()) {
                //activityDataContainerArrayList.addAll(((Trip) toBeJoined).getActivityDataContainers());
                accelerationDataArrayList.addAll(((Trip) fullTripPart).getAccelerationData());
            }

        }
        return (Trip) analyseTripPart(locationDataContainerArrayList,activityDataContainerArrayList,true,modality,accelerationDataArrayList, correctedModality);

    }

    public ArrayList<Trip> splitLeg(Trip legToBeSplit, LocationDataContainer whereToSplit, boolean isLastLeg){

        ArrayList<Trip> toBeReturned = new ArrayList<>();

        long timestampSplit = whereToSplit.getSysTimestamp();

        LOG.debug( DateHelper.getDateFromTSString(timestampSplit) + " timesplit");
        LOG.debug( DateHelper.getDateFromTSString(legToBeSplit.getInitTimestamp()) + " initleg");
        LOG.debug( DateHelper.getDateFromTSString(legToBeSplit.getEndTimestamp()) + " endleg");
        LOG.debug( legToBeSplit.getLocationDataContainers().size() + " trip list size");

//        for(LocationDataContainer lcd : legToBeSplit.getLocationDataContainers()){
//            LOG.debug( DateHelper.getDateFromTSString(lcd.getSysTimestamp()));
//        }

        ArrayList<LocationDataContainer> LDCListFirstLeg = getLocationsInsideInterval(legToBeSplit.getLocationDataContainers(), legToBeSplit.getInitTimestamp(), timestampSplit, false);

        LOG.debug( "first " + LDCListFirstLeg.size());

//        ArrayList<ActivityDataContainer> ADCListFirstLeg = getActivitiesInsideInterval(legToBeSplit.getActivityDataContainers(), legToBeSplit.getInitTimestamp(), timestampSplit);
        ArrayList<AccelerationData> AccelDataListFirstLeg = getAccelerationsInsideInterval(legToBeSplit.getAccelerationData(), legToBeSplit.getInitTimestamp(), timestampSplit);

        ArrayList<LocationDataContainer> LDCListSecondLeg = getLocationsInsideInterval(legToBeSplit.getLocationDataContainers(), timestampSplit, legToBeSplit.getEndTimestamp(), isLastLeg);
//        ArrayList<ActivityDataContainer> ADCListSecondLeg = getActivitiesInsideInterval(legToBeSplit.getActivityDataContainers(), timestampSplit, legToBeSplit.getEndTimestamp());
        ArrayList<AccelerationData> AccelDataListSecondLeg = getAccelerationsInsideInterval(legToBeSplit.getAccelerationData(), timestampSplit, legToBeSplit.getEndTimestamp());

        LOG.debug( "second " + LDCListSecondLeg.size());

        Trip firstSplitLeg = (Trip) analyseTripPart(LDCListFirstLeg,new ArrayList<ActivityDataContainer>(),true,legToBeSplit.getModality(),AccelDataListFirstLeg, legToBeSplit.getCorrectedModeOfTransport());
        Trip secondSplitLeg = (Trip) analyseTripPart(LDCListSecondLeg,new ArrayList<ActivityDataContainer>(),true,legToBeSplit.getModality(),AccelDataListSecondLeg, legToBeSplit.getCorrectedModeOfTransport());

        if(firstSplitLeg == null || secondSplitLeg == null){
            LOG.debug( (firstSplitLeg == null) + "");
            LOG.debug( (secondSplitLeg == null) + "");
            return null;
        }else{
            firstSplitLeg.splittedFromLeg(legToBeSplit);
            secondSplitLeg.splittedFromLeg(legToBeSplit);
            toBeReturned.add(firstSplitLeg);
            toBeReturned.add(secondSplitLeg);
            return toBeReturned;
        }

    }

    public FullTrip removeAndInsertLegsIntoTrip(FullTrip fullTrip, ArrayList<Trip> toBeInserted, int index){

        fullTrip.getTripList().remove(index);
        fullTrip.getTripList().addAll(index, toBeInserted);

        return fullTrip;

    }

    public FullTrip removeAndInsertLegsIntoTrip(FullTrip fullTrip, Trip joinedTrip, int index){

        fullTrip.getTripList().remove(index);
        fullTrip.getTripList().remove(index);

        fullTrip.getTripList().add(index, joinedTrip);

        return fullTrip;

    }

    public FullTrip removeTripPartsAndInsertMergedTrip(FullTrip fullTrip, ArrayList<FullTripPartValidationWrapper> list, Trip toMerge){

        // remove higher indexes first
        SortFullTripValidationWrapperReverse sortReverse = new SortFullTripValidationWrapperReverse();
        Collections.sort(list, sortReverse);

        int indexToInsert = -1;

        for(FullTripPartValidationWrapper fullTripPartValidationWrapper : list){
            fullTrip.getTripList().remove(fullTripPartValidationWrapper.getRealIndex());
            indexToInsert = fullTripPartValidationWrapper.getRealIndex();
        }

        fullTrip.getTripList().add(indexToInsert, toMerge);

        return fullTrip;

    }

    public FullTrip removeTripParts(FullTrip fullTrip, ArrayList<FullTripPartValidationWrapper> list){

        // remove higher indexes first
        SortFullTripValidationWrapperReverse sortReverse = new SortFullTripValidationWrapperReverse();
        Collections.sort(list, sortReverse);

        for(FullTripPartValidationWrapper fullTripPartValidationWrapper : list){
            fullTrip.getTripList().remove(fullTripPartValidationWrapper.getRealIndex());
        }

        return fullTrip;

    }

    public FullTrip merge(FullTrip fullTrip, ArrayList<FullTripPartValidationWrapper> toBeMerged, int chosenModality){

        FullTripPart previous = null;

        Trip merging = null;

        for(FullTripPartValidationWrapper fullTripPartBeingMerged : toBeMerged){

            if(previous == null){
                previous = fullTripPartBeingMerged.getFullTripPart();

            }else{

                ArrayList<FullTripPart> intermediateMerge = new ArrayList<>();

                if(merging == null){

                    intermediateMerge.add(previous);
                    intermediateMerge.add(fullTripPartBeingMerged.getFullTripPart());

                    merging = joinFullTripParts(intermediateMerge, ActivityDetected.keys.unknown ,chosenModality);

                    //both are legs
                    if(previous.isTrip() && fullTripPartBeingMerged.getFullTripPart().isTrip()){
                        merging.mergedFromLegs(((Trip) previous), ((Trip) fullTripPartBeingMerged.getFullTripPart()));
                        //previous is we, current is trip
                    }else if(!previous.isTrip() && fullTripPartBeingMerged.getFullTripPart().isTrip()){
                        merging.mergedWithWE(((Trip) fullTripPartBeingMerged.getFullTripPart()), ((WaitingEvent) previous));
                    }else{
                        //previous trip is leg and current is we (there are no consecutive waiting events)
                        merging.mergedWithWE(((Trip) previous), ((WaitingEvent) fullTripPartBeingMerged.getFullTripPart()));
                    }

                }else{

                    intermediateMerge.add(merging);
                    intermediateMerge.add(fullTripPartBeingMerged.getFullTripPart());

                    Trip trip = joinFullTripParts(intermediateMerge, ActivityDetected.keys.unknown, chosenModality);

                    if(fullTripPartBeingMerged.getFullTripPart().isTrip()){
                        trip.mergedFromLegs(merging, ((Trip) fullTripPartBeingMerged.getFullTripPart()));
                    }else{
                        trip.mergedWithWE(merging, ((WaitingEvent) fullTripPartBeingMerged.getFullTripPart()));
                    }

                    merging = new Trip(trip);

                }


            }

        }


        return removeTripPartsAndInsertMergedTrip(fullTrip, toBeMerged, merging);

    }

    public FullTrip mergeFullTrips(ArrayList<FullTrip> fullTrips) throws Exception{

        ArrayList<FullTripPart> mergedTripParts = new ArrayList<>();

        for(FullTrip fullTrip : fullTrips){
            mergedTripParts.addAll(fullTrip.getTripList());
        }

        return analyseListOfTrips(mergedTripParts,fullTrips.get(0).isManualTripStart(), fullTrips.get(fullTrips.size()-1).isManualTripEnd());
    }

    public FullTrip deleteTripParts(FullTrip fullTrip, ArrayList<FullTripPartValidationWrapper> toBeDeleted) {

        return removeTripParts(fullTrip, toBeDeleted);

    }
}

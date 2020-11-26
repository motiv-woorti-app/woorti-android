package inesc_id.pt.motivandroid.utils;

import java.util.ArrayList;
import java.util.Iterator;

import inesc_id.pt.motivandroid.data.tripData.AccelerationData;
import inesc_id.pt.motivandroid.data.tripDigest.FullTripDigestWrapper;
import inesc_id.pt.motivandroid.data.tripData.FullTripPartValidationWrapper;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;

/**
 *
 * NumbersUtil
 *
 *  Utility functions
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

public class NumbersUtil {

    public static int indexOfMaxInRange(int[] a) {
        int max = 0;
        int maxIndex = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * @param distanceBetweenMeters
     * @param timeBetweenMiliseconds
     * @return computed segment speed
     */
    public static float getSegmentSpeedKm(double distanceBetweenMeters, double timeBetweenMiliseconds){

        double speed = (distanceBetweenMeters/1000.0)/(timeBetweenMiliseconds/3600000.0);

        if(!Double.isNaN(speed) && !Double.isInfinite(speed)) {
            return (float) ((distanceBetweenMeters/1000.0)/(timeBetweenMiliseconds/3600000.0));
        }else{
            return 0;
        }
    }

    /**
     * @param list
     * @return true if ints in list are consecutive, false otherwise
     */
    public static boolean areIntsConsecutive(ArrayList<Integer> list) {
        Iterator<Integer> it = list.iterator();
        if (!it.hasNext()) {
            return true;
        }

        Integer prev = it.next();
        while (it.hasNext()) {
            Integer curr = it.next();
            if (prev + 1 != curr /* mismatch */ || prev + 1 < prev /* overflow */) {
                return false;
            }
            prev = curr;
        }
        return true;
    }

    /**
     * @param list
     * @return true if digests in list are consecutive, false otherwise
     */
    public static boolean areFullTripDigestsConsecutive(ArrayList<FullTripDigestWrapper> list) {
        Iterator<FullTripDigestWrapper> it = list.iterator();
        if (!it.hasNext()) {
            return true;
        }

        FullTripDigestWrapper prev = it.next();
        while (it.hasNext()) {
            FullTripDigestWrapper curr = it.next();
            if (prev.getInteger() + 1 != curr.getInteger() /* mismatch */ || prev.getInteger() + 1 < prev.getInteger() /* overflow */) {
                return false;
            }
            prev = curr;
        }
        return true;
    }

    /**
     * @param list
     * @return true if tripParts in list are consecutive, false otherwise
     */
    public static boolean areTripPartsConsecutive(ArrayList<FullTripPartValidationWrapper> list) {
        Iterator<FullTripPartValidationWrapper> it = list.iterator();
        if (!it.hasNext()) {
            return true;
        }

        FullTripPartValidationWrapper prev = it.next();
        while (it.hasNext()) {
            FullTripPartValidationWrapper curr = it.next();
            if (prev.getRealIndex() + 1 != curr.getRealIndex() /* mismatch */ || prev.getRealIndex() + 1 < prev.getRealIndex() /* overflow */) {
                return false;
            }
            prev = curr;
        }
        return true;
    }

    /**
     * @param accelerations
     * @param locations
     * @return the last timestamp in between both provided arrays accelerations and locations
     */
    public static long getLastTimestamp(ArrayList<AccelerationData> accelerations, ArrayList<LocationDataContainer> locations){

        if(accelerations.size() == 0){
            return locations.get(locations.size()-1).getSysTimestamp();
        }

        if(locations.size() == 0){
            return accelerations.get(accelerations.size()-1).getTimestamp();
        }

        AccelerationData lastAccel = accelerations.get(accelerations.size()-1);
        LocationDataContainer lastLocation = locations.get(locations.size()-1);

        if(lastAccel.getTimestamp() > lastLocation.getSysTimestamp()) {
            return lastAccel.getTimestamp();
        }
        return lastLocation.getSysTimestamp();

    }


    /**
     * @param number
     * @return number provided rounded to one decimal place
     */
    public static double roundToOneDecimalPlace(double number){

        return Math.round(number * 10.0) / 10.0;

    }

    /**
     * @param number
     * @return number provided rounded to no decimal place
     */
    public static int roundNoDecimalPlace(double number){

        return (int) Math.round(number);

    }



}

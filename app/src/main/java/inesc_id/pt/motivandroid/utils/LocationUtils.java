package inesc_id.pt.motivandroid.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.LocationFromServer;

/**
 *
 * LocationUtils
 *
 *  Utility functions for calculating distances
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

public class LocationUtils {

    /**
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     *
     * @return distance in meters between a and b
     */
    public static double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (double) (180.f/Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        double result = 6366000 * tt;

        if(Double.isNaN(result)){
            return 0;
        }else{
            return result;
        }
    }

    /**
     * @param locA
     * @param locB
     * @return distance in meters between locA and locB
     */
    public static double meterDistanceBetweenTwoLocations(Location locA, Location locB) {

        return meterDistanceBetweenPoints(locA.getLatitude(),locA.getLongitude(), locB.getLatitude(), locB.getLongitude());

    }

    /**
     * @param locA
     * @param locB
     * @return distance in meters between locA and locB
     */
    public static double meterDistanceBetweenTwoLocations(LocationDataContainer locA, LocationDataContainer locB) {

        return meterDistanceBetweenPoints(locA.getLatitude(),locA.getLongitude(), locB.getLatitude(), locB.getLongitude());

    }

    /**
     * @param latLng
     * @param locationDataContainer
     * @return distance in meters between latlng and locationDataContainer
     */
    private static double meterDistanceBetweenTwoLocations(LatLng latLng, LocationDataContainer locationDataContainer){

        return meterDistanceBetweenPoints(latLng.latitude,latLng.longitude, locationDataContainer.getLatitude(), locationDataContainer.getLongitude());

    }

    /**
     * @param latLng
     * @param locationDataContainer
     * @return distance in meters between latlng and locationDataContainer
     */
    public static double meterDistanceBetweenTwoLocations(Location latLng, LocationFromServer locationDataContainer){

        return meterDistanceBetweenPoints(latLng.getLatitude(),latLng.getLongitude(), locationDataContainer.getLat(), locationDataContainer.getLng());

    }


    /**
     * @param test
     * @param target
     *
     * @return the closest point of target locations to the test location
     */
    private LatLng findNearestPoint(LatLng test, List<LatLng> target) {
        double distance = -1;
        LatLng minimumDistancePoint = test;

        if (test == null || target == null) {
            return minimumDistancePoint;
        }

        for (int i = 0; i < target.size(); i++) {
            LatLng point = target.get(i);

            int segmentPoint = i + 1;
            if (segmentPoint >= target.size()) {
                segmentPoint = 0;
            }

            double currentDistance = PolyUtil.distanceToLine(test, point, target.get(segmentPoint));
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;
                minimumDistancePoint = findNearestPoint(test, point, target.get(segmentPoint));
            }
        }

        return minimumDistancePoint;
    }

    /**
     * @param test
     * @param target
     *
     * @return the closest point of target locations to the test location
     */
    public static LatLng findNearestPointLDC(LatLng test, List<LocationDataContainer> target) {
        double distance = -1;
        LatLng minimumDistancePoint = test;

        if (test == null || target == null) {
            return null;
        }

        for (int i = 0; i < target.size(); i++) {
            LatLng point = new LatLng(target.get(i).getLatitude(),target.get(i).getLongitude());

            int segmentPoint = i + 1;
            if (segmentPoint >= target.size()) {
                segmentPoint = 0;
            }

            double currentDistance = PolyUtil.distanceToLine(test, point, new LatLng(target.get(segmentPoint).getLatitude(),target.get(segmentPoint).getLongitude()));
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;
                minimumDistancePoint = findNearestPoint(test, point, new LatLng(target.get(segmentPoint).getLatitude(),target.get(segmentPoint).getLongitude()));
            }
        }

        return minimumDistancePoint;
    }

    /**
     * Based on `distanceToLine` method from
     * https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/PolyUtil.java
     */
    private static LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end) {
        if (start.equals(end)) {
            return start;
        }

        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }

        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));


    }

    /**
     * @param latLng
     * @param locationDataContainerList
     *
     * @return the closest point of target locations to the test location
     */
    public static LocationDataContainer findNearestLDC(LatLng latLng, List<LocationDataContainer> locationDataContainerList){

        LocationDataContainer nearestLCD = null;
        double minDistance=999999999;

        for(LocationDataContainer lcd : locationDataContainerList){
            double currentDistance = LocationUtils.meterDistanceBetweenTwoLocations(latLng, lcd);

            if(currentDistance < minDistance){
                minDistance = currentDistance;
                nearestLCD = lcd;
            }
        }
        return nearestLCD;
    }

    /**
     * @param latLngList
     * @return list of LocationDataContainers from the latLngList provided in LatLng class format
     */
    public static ArrayList<LocationDataContainer> getLatLngArrayFromLDCArray(List<LatLng> latLngList){

        ArrayList<LocationDataContainer> result = new ArrayList<>();

        for (LatLng latLng : latLngList){
            result.add(new LocationDataContainer(latLng));
        }
        return result;
    }

    /**
     * @param locationDataContainer
     * @return textual representation of the provided locationDataContainer
     */
    public static String getTextLatLng(LocationDataContainer locationDataContainer){

        return locationDataContainer.getLatitude() + ", " + locationDataContainer.getLongitude();
    }


    /**
     * @param pathLCD
     * @return extrapolated middle location from path provided from param pathLCD
     */
    public static LatLng extrapolateMiddleLocation(List<LocationDataContainer> pathLCD) {
        LatLng extrapolated = null;

        ArrayList<LatLng> path = new ArrayList<>();

        LatLng origin = pathLCD.get(0).getLatLng();



        for (LocationDataContainer lcd : pathLCD){
//            Log.e("extrapolate", lcd.getLatLng().toString());

            path.add(lcd.getLatLng());
        }

        Double distance = SphericalUtil.computeLength(path) / 2;
//        Log.e("extrapolate", "distance: " + distance);

//        if (!PolyUtil.isLocationOnPath(origin, path, false, 1)) { // If the location is not on path non geodesic, 1 meter tolerance
//            return null;
//        }

        float accDistance = 0f;
        boolean foundStart = false;
        List<LatLng> segment = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {
            LatLng segmentStart = path.get(i);
            LatLng segmentEnd = path.get(i + 1);

            segment.clear();
            segment.add(segmentStart);
            segment.add(segmentEnd);

            double currentDistance = 0d;

            if (!foundStart) {
                if (PolyUtil.isLocationOnPath(origin, segment, false, 1)) {
                    foundStart = true;

                    currentDistance = SphericalUtil.computeDistanceBetween(origin, segmentEnd);

                    if (currentDistance > distance) {
                        double heading = SphericalUtil.computeHeading(origin, segmentEnd);
                        extrapolated = SphericalUtil.computeOffset(origin, distance - accDistance, heading);
                        break;
                    }
                }
            } else {
                currentDistance = SphericalUtil.computeDistanceBetween(segmentStart, segmentEnd);

                if (currentDistance + accDistance > distance) {
                    double heading = SphericalUtil.computeHeading(segmentStart, segmentEnd);
                    extrapolated = SphericalUtil.computeOffset(segmentStart, distance - accDistance, heading);
                    break;
                }
            }

            accDistance += currentDistance;
        }

        return extrapolated;
    }


}

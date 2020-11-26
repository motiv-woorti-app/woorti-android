package inesc_id.pt.motivandroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import inesc_id.pt.motivandroid.R;
import inesc_id.pt.motivandroid.data.modesOfTransport.ActivityDetected;
import inesc_id.pt.motivandroid.data.tripData.FullTrip;
import inesc_id.pt.motivandroid.data.tripData.FullTripPart;
import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.data.tripData.Trip;

/**
 *
 * MotivMapUtils
 *
 *  Utility functions to draw trips on the map
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
public class MotivMapUtils {


    /**
     * draws the trips to be merged provided in the list fullTrips on the GoogleMap mMap
     *
     * @param fullTrips
     * @param mMap
     * @param context
     */
    public static void drawTripsToBeMerged(ArrayList<FullTrip> fullTrips, GoogleMap mMap, Context context){

        int i = 0;

        LocationDataContainer lastLoc = null;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (FullTrip fullTrip : fullTrips){

            //first trip to be merged
            if (i == 0){

                Drawable modeDrawable = context.getResources().getDrawable(R.drawable.mytrips_transports_starting_point_bubble);

                MarkerOptions initTripMarker = new MarkerOptions()
                        .position(new LatLng(fullTrip.getTripList().get(0).getLocationDataContainers().get(0).getLatitude(),
                                fullTrip.getTripList().get(0).getLocationDataContainers().get(0).getLongitude()))
                        .title("Start " + DateHelper.getHoursMinutesFromTSString(fullTrip.getTripList().get(0).getInitTimestamp())
                        );

                initTripMarker.icon(getMarkerIconFromDrawable(modeDrawable, 0.5f));
                mMap.addMarker(initTripMarker);
            }else //last trip to be Merged
                if(i == fullTrips.size() -1){

                    Drawable modeDrawable = context.getResources().getDrawable(R.drawable.mytrips_transports_arrival_bubble);

                    MarkerOptions finishTripMarker = new MarkerOptions()
                            .position(fullTrip.getTripList().get(fullTrip.getTripList().size()-1).getLocationDataContainers().get(fullTrip.getTripList().get(fullTrip.getTripList().size()-1).getLocationDataContainers().size()-1).getLatLng())
                            .title("Finish " + DateHelper.getHoursMinutesFromTSString(fullTrip.getTripList().get(fullTrip.getTripList().size()-1).getEndTimestamp())
                            );

                    finishTripMarker.icon(getMarkerIconFromDrawable(modeDrawable, 0.5f));
                    mMap.addMarker(finishTripMarker);
            }


            ArrayList<LatLng> pointList = new ArrayList<>();

            for(FullTripPart ftp : fullTrip.getTripList()){

                for(LocationDataContainer locationDataContainer : ftp.getLocationDataContainers()){


                    pointList.add(locationDataContainer.getLatLng());
                    builder.include(locationDataContainer.getLatLng());

                }

                Log.e("!!! end time ftp", DateHelper.getDateFromTSString(ftp.getEndTimestamp()));


            }


            //add trip polyline to map
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(10);

            Polyline currentPolyline = mMap.addPolyline(lineOptions);
            currentPolyline.setPoints(pointList);

            //add connection between trips
            if(lastLoc == null){
                lastLoc = fullTrip.getTripList().get(fullTrip.getTripList().size()-1).getLocationDataContainers().get(fullTrip.getTripList().get(fullTrip.getTripList().size()-1).getLocationDataContainers().size()-1);
            }else{

                PolylineOptions connectionOptions = new PolylineOptions();
                connectionOptions.width(10);
                connectionOptions.color(Color.RED);
                connectionOptions.add(lastLoc.getLatLng());
                connectionOptions.add(fullTrip.getTripList().get(0).getLocationDataContainers().get(0).getLatLng());

                mMap.addPolyline(connectionOptions);

                lastLoc = fullTrip.getTripList().get(fullTrip.getTripList().size()-1).getLocationDataContainers().get(fullTrip.getTripList().get(fullTrip.getTripList().size()-1).getLocationDataContainers().size()-1);


            }

            i++;
        }

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        LatLngBounds bounds = builder.build();

        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }


    /**
     * draws the provided ongoing trip on the map. tripParts correspond to the list of already
     * finished trip parts. currentLocations correspond to the locations of the currently ongoing
     * trip part
     *
     * @param tripParts
     * @param currentLocations
     * @param mMap
     * @param context
     */
    public static void drawRouteOnMapOngoing(ArrayList<FullTripPart> tripParts, ArrayList<LocationDataContainer> currentLocations, GoogleMap mMap, Context context){

        int j = 0;

        LocationDataContainer lastLocation = null;

        int leg = 0;

        for(FullTripPart ftp : tripParts){

            for(LocationDataContainer locationDataContainer : ftp.getLocationDataContainers()){

                if(lastLocation == null){
                    lastLocation = locationDataContainer;

                }else{

                    double distanceToTheLastLocation = LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, lastLocation);
                    float segmentSpeed = NumbersUtil.getSegmentSpeedKm(distanceToTheLastLocation,locationDataContainer.getSysTimestamp() - lastLocation.getSysTimestamp());

                    MarkerOptions finishTripMarker = new MarkerOptions()
                            .position(locationDataContainer.getLatLng())
                            .title("counter j ="+ j
                            );


                    lastLocation = locationDataContainer;

                }

                j++;



            }

            Log.e("!!! end time ftp", DateHelper.getDateFromTSString(ftp.getEndTimestamp()));

            leg ++;

        }


        int i = 0;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        boolean drawnColour=false;

        for(FullTripPart ftp : tripParts){


            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(10);


            if(ftp.isTrip()) {

                if(ftp.getLocationDataContainers().size() == 0){
                    continue;
                }

                if (!drawnColour) {
                    lineOptions.color(R.color.orangeFactor);
                    drawnColour = true;
                }else {
                    lineOptions.color(Color.BLACK);
                    drawnColour = false;
                }

                Drawable modeDrawable;

                if(((Trip) ftp).getCorrectedModeOfTransport() == -1) {

                    modeDrawable = context.getResources().getDrawable(ActivityDetected.getTransportIconBubbleFromInt(((Trip) ftp).getSugestedModeOfTransport()));
                }else{
                    modeDrawable = context.getResources().getDrawable(ActivityDetected.getTransportIconBubbleFromInt(((Trip) ftp).getCorrectedModeOfTransport()));
                }

                LatLng legMiddleLocation;

                try{
                    legMiddleLocation = LocationUtils.extrapolateMiddleLocation(ftp.getLocationDataContainers());

                    if(legMiddleLocation == null){
                        legMiddleLocation = ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()/2).getLatLng();
                    }

                }catch(Exception e){
                    legMiddleLocation = ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()/2).getLatLng();
                }


                MarkerOptions initLegMarker = new MarkerOptions().position(legMiddleLocation).title(
                        DateHelper.getHoursMinutesFromTSString(ftp.getInitTimestamp())
                                + " > " +
                                DateHelper.getHoursMinutesFromTSString(ftp.getEndTimestamp())
                );;

                initLegMarker.icon(getMarkerIconFromDrawable(modeDrawable, 0.5f));
                mMap.addMarker(initLegMarker);


            }else{
                lineOptions.color(Color.RED);
                lineOptions.width(20);

                ArrayList<LocationDataContainer> aux = new ArrayList<>();

                aux.add(ftp.getLocationDataContainers().get(0));
                aux.add(ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()-1));

//                LatLng legMiddleLocation = LocationUtils.extrapolateMiddleLocation(aux);

                MarkerOptions initWEMarker = new MarkerOptions()
                        .position(ftp.getLocationDataContainers().get(0).getLatLng()
                        )
                        .title(
                                DateHelper.getHoursMinutesFromTSString(ftp.getInitTimestamp())
                                        + " > " +
                                        DateHelper.getHoursMinutesFromTSString(ftp.getEndTimestamp())
                        );



                Drawable weDrawable = context.getResources().getDrawable(R.drawable.mytrips_navigation_transfer);
                initWEMarker.icon(getMarkerIconFromDrawable(weDrawable, 0.5f));

                mMap.addMarker(initWEMarker);



            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
            String initDateFtp = sdf.format(ftp.getInitTimestamp());

//            Log.e("showMaps", "trip part starting at" + initDateFtp+ "");

            Polyline currentPolyline = mMap.addPolyline(lineOptions);



            List<LatLng> latLngList = new ArrayList<>();

            for(LocationDataContainer ldc : ftp.getLocationDataContainers()){

                SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                String initSysDate = sdf2.format(ldc.getSysTimestamp());

                String initLocDate = sdf2.format(ldc.getLocTimestamp());

                latLngList.add(ldc.getLatLng());
                builder.include(ldc.getLatLng());

//                Log.d("showMaps", ldc.getLatitude() + " " + ldc.getLongitude() + " acc:" + ldc.getAccuracy() + " speed:" + ldc.getSpeed() + " " + initSysDate + " " + initLocDate);

            }
            currentPolyline.setPoints(latLngList);


            i++;
        }

//        LatLngBounds bounds = builder.build();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

//        mMap.animateCamera(cu);

        /////////////////////////////
        /////////////////////////////

        PolylineOptions lineOptionsCurrent = new PolylineOptions();
        lineOptionsCurrent.width(10);
        lineOptionsCurrent.color(Color.BLACK);

        for (LocationDataContainer currentLocation : currentLocations ){

            lineOptionsCurrent.add(currentLocation.getLatLng());
        }

        if (currentLocations.size() > 0){

            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    currentLocations.get(currentLocations.size()-1).getLatLng(), 15);
            mMap.animateCamera(location);

        }

        mMap.addPolyline(lineOptionsCurrent);

    }


    /**
     * draws the provided trip fullTripBeingShown on the map.
     *
     * @param fullTripBeingShown
     * @param mMap
     * @param context
     * @param height
     * @param width
     */
    public static void drawRouteOnMap(FullTrip fullTripBeingShown, GoogleMap mMap, Context context, int height, int width){

        int j = 0;

        LocationDataContainer lastLocation = null;

        int leg = 0;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(FullTripPart ftp : fullTripBeingShown.getTripList()){


//            Log.e("ShowCurrentTrip","--------------------------------------------------------------------------");
//            Log.e("ShowCurrentTrip","--------------------------------------------------------------------------");
//
//            Log.e("!!! init time ftp", DateHelper.getDateFromTSString(ftp.getInitTimestamp()) + " is trip: " + ftp.isTrip());

            for(LocationDataContainer locationDataContainer : ftp.getLocationDataContainers()){

                builder.include(locationDataContainer.getLatLng());

                if(lastLocation == null){
                    lastLocation = locationDataContainer;

//                    Crashlytics.log(Log.DEBUG, "!!!", "j=" + j +
//                            DateHelper.getDateFromTSString(locationDataContainer.getSysTimestamp()) +" "+ "lat:"+locationDataContainer.getLatitude()+" lng:"+locationDataContainer.getLongitude() + " + acc: " + locationDataContainer.getAccuracy() + " ts " + locationDataContainer.getSysTimestamp());

                }else{


                    double distanceToTheLastLocation = LocationUtils.meterDistanceBetweenTwoLocations(locationDataContainer, lastLocation);
                    float segmentSpeed = NumbersUtil.getSegmentSpeedKm(distanceToTheLastLocation,locationDataContainer.getSysTimestamp() - lastLocation.getSysTimestamp());

//                    Crashlytics.log(Log.DEBUG, "!!!", "j=" + j +
//                            DateHelper.getDateFromTSString(locationDataContainer.getSysTimestamp()) +" "+ "lat:"+locationDataContainer.getLatitude()+" lng:"+locationDataContainer.getLongitude() + " + acc: " + locationDataContainer.getAccuracy() + " ts " + locationDataContainer.getSysTimestamp());
//
//                    Crashlytics.log(Log.DEBUG, "!!!", "j=" + j +
//                            DateHelper.getDateFromTSString(locationDataContainer.getSysTimestamp()) +" Distance to last"+
//                            +distanceToTheLastLocation + " at speed " + segmentSpeed +
//                            " + acc: " + locationDataContainer.getAccuracy());

                    MarkerOptions finishTripMarker = new MarkerOptions()
                            .position(locationDataContainer.getLatLng())
                            .title("counter j ="+ j
                            );




                    lastLocation = locationDataContainer;

                }

                j++;



            }

//            Log.e("!!! end time ftp", DateHelper.getDateFromTSString(ftp.getEndTimestamp()));

            leg ++;

        }


        int i = 0;



        boolean drawnColour=false;

        for(FullTripPart ftp : fullTripBeingShown.getTripList()){

            if(i == 0){

                Drawable modeDrawable = context.getResources().getDrawable(R.drawable.mytrips_transports_starting_point_bubble);



                MarkerOptions initTripMarker = new MarkerOptions()
                        .position(new LatLng(ftp.getLocationDataContainers().get(0).getLatitude(),
                                ftp.getLocationDataContainers().get(0).getLongitude()))
                        .title("Start " + DateHelper.getHoursMinutesFromTSString(ftp.getInitTimestamp())
                        );

                initTripMarker.icon(getMarkerIconFromDrawable(modeDrawable, 0.5f));


//                initTripMarker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                Marker initMarker = mMap.addMarker(initTripMarker);

            }

            if(i== fullTripBeingShown.getTripList().size()-1){

                Drawable modeDrawable = context.getResources().getDrawable(R.drawable.mytrips_transports_arrival_bubble);

                MarkerOptions finishTripMarker = new MarkerOptions()
                        .position(ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()-1).getLatLng())
                        .title("Finish " + DateHelper.getHoursMinutesFromTSString(ftp.getEndTimestamp())
                        );

//                finishTripMarker.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(R.drawable.mytrips_transports_arrival_bubble, 0.2f, context)));

                finishTripMarker.icon(getMarkerIconFromDrawable(modeDrawable, 0.5f));
                mMap.addMarker(finishTripMarker);

            }

            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(10);


            if(ftp.isTrip()) {

                if(ftp.getLocationDataContainers().size() == 0){
                    continue;
                }

                if (!drawnColour) {
                    lineOptions.color(ContextCompat.getColor(context,R.color.orangeFactor));
                    drawnColour = true;
                }else {
                    lineOptions.color(Color.BLACK);
                    drawnColour = false;
                }

                Drawable modeDrawable;

                if(((Trip) ftp).getCorrectedModeOfTransport() == -1) {

                    modeDrawable = context.getResources().getDrawable(ActivityDetected.getTransportIconBubbleFromInt(((Trip) ftp).getSugestedModeOfTransport()));
                }else{
                    modeDrawable = context.getResources().getDrawable(ActivityDetected.getTransportIconBubbleFromInt(((Trip) ftp).getCorrectedModeOfTransport()));
                }

                LatLng legMiddleLocation;

                try{
                    legMiddleLocation = LocationUtils.extrapolateMiddleLocation(ftp.getLocationDataContainers());

                    if(legMiddleLocation == null){
                        legMiddleLocation = ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()/2).getLatLng();
                    }

                }catch(Exception e){
                    legMiddleLocation = ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()/2).getLatLng();
                }


                MarkerOptions initLegMarker = new MarkerOptions().position(legMiddleLocation).title(
                        DateHelper.getHoursMinutesFromTSString(ftp.getInitTimestamp())
                                + " > " +
                                DateHelper.getHoursMinutesFromTSString(ftp.getEndTimestamp())
                );;

                initLegMarker.icon(getMarkerIconFromDrawable(modeDrawable, 0.5f));
                Marker legMarker = mMap.addMarker(initLegMarker);

                legMarker.setTag(i);

                List<LatLng> latLngList = new ArrayList<>();

                for(LocationDataContainer ldc : ftp.getLocationDataContainers()){

                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
                    String initSysDate = sdf2.format(ldc.getSysTimestamp());

                    String initLocDate = sdf2.format(ldc.getLocTimestamp());

                    latLngList.add(ldc.getLatLng());
//                    builder.include(ldc.getLatLng());


                }

                lineOptions.addAll(latLngList);
                mMap.addPolyline(lineOptions);

            }else{
                lineOptions.color(Color.RED);
                lineOptions.width(20);

                ArrayList<LatLng> aux = new ArrayList<>();

                aux.add(ftp.getLocationDataContainers().get(0).getLatLng());
                aux.add(ftp.getLocationDataContainers().get(ftp.getLocationDataContainers().size()-1).getLatLng());

//                LatLng legMiddleLocation = LocationUtils.extrapolateMiddleLocation(aux);

                MarkerOptions initWEMarker = new MarkerOptions()
                        .position(ftp.getLocationDataContainers().get(0).getLatLng()
                        )
                        .title(
                                DateHelper.getHoursMinutesFromTSString(ftp.getInitTimestamp())
                                        + " > " +
                                        DateHelper.getHoursMinutesFromTSString(ftp.getEndTimestamp())
                        );



                Drawable weDrawable = context.getResources().getDrawable(R.drawable.mytrips_transports_transfer_bubble);
                initWEMarker.icon(getMarkerIconFromDrawable(weDrawable,0.5f));

                Marker weMarker = mMap.addMarker(initWEMarker);
                weMarker.setTag(i);

                lineOptions.addAll(aux);
                mMap.addPolyline(lineOptions);


            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a");
            String initDateFtp = sdf.format(ftp.getInitTimestamp());


            i++;
        }

        LatLngBounds bounds = builder.build();

        if ((height == -1) && (width == -1)){

             width = context.getResources().getDisplayMetrics().widthPixels;
             height = context.getResources().getDisplayMetrics().heightPixels;

        }

        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }

    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable, float weight) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(Math.round(drawable.getIntrinsicWidth() * weight), Math.round(drawable.getIntrinsicHeight() * weight), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, Math.round(drawable.getIntrinsicWidth() * weight), Math.round(drawable.getIntrinsicHeight() * weight));

        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Bitmap getBitmapMarkerIconFromDrawable(Drawable drawable, float weight) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(Math.round(drawable.getIntrinsicWidth() * weight), Math.round(drawable.getIntrinsicHeight() * weight), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, Math.round(drawable.getIntrinsicWidth() * weight), Math.round(drawable.getIntrinsicHeight() * weight));

        drawable.draw(canvas);
        return bitmap;
    }



}

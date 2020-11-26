package inesc_id.pt.motivandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inesc_id.pt.motivandroid.data.tripData.LocationDataContainer;
import inesc_id.pt.motivandroid.tripStateMachine.TripStateMachine;
import inesc_id.pt.motivandroid.utils.DateHelper;

import static org.joda.time.DateTimeZone.UTC;

/**
 * KeepAwakeReceiver
 *
 *  Broadcast receiver used to trigger the app to periodically retrieve a location. Basically trying
 * to keep the app from "dying"
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

public class KeepAwakeReceiver extends BroadcastReceiver
{
    private static final Logger LOG = LoggerFactory.getLogger(KeepAwakeReceiver.class.getSimpleName());

    TripStateMachine tripStateMachine;
    FusedLocationProviderClient fusedLocationProviderClient;


    @SuppressWarnings("MissingPermission")
    //per https://stackoverflow.com/questions/25207548/android-how-to-execute-a-method-every-x-hours-or-minutes/25208088
    @Override
    public void onReceive(Context context, Intent intent)
    {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        LOG.debug("Alarm fired at " + DateHelper.getDateFromTSString(new DateTime(UTC).getMillis()));
        tripStateMachine = TripStateMachine.getInstance(context, false, true);
        long lastValidLocationTS = tripStateMachine.getlastValidLocationTS();

        //3 minutes
        if((new DateTime(UTC).getMillis() - lastValidLocationTS) > 2.5 * 1000 * 60){
            LOG.debug("Last valid location - more than 3 minutes ago - asking for high accuracy location request");

            fusedLocationProviderClient.requestLocationUpdates(buildHighAccuracyLocRequest(),mLocationCallback,null);

        }else{
            LOG.debug("Last valid location - less than 3 minutes ago - dont ask for high accuracy location request");
        }

    }
//
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                LocationDataContainer received = new LocationDataContainer(
                        new DateTime(UTC).getMillis(),
                        location.getAccuracy(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getSpeed(),
                        location.getTime());

                LOG.debug("CSV: " + location.getLatitude() + "," + location.getLongitude() + "," + "ACC " + location.getAccuracy() + " " +
                        DateHelper.getDateFromTSString(new DateTime(UTC).getMillis()) + " from KeepAwakeReceiver");

                tripStateMachine.insertLocationUpdate(received, false);

            }
        };
    };

    private LocationRequest buildHighAccuracyLocRequest(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LOG.debug("Building high accuracy location request");

        return mLocationRequest;

    }

}
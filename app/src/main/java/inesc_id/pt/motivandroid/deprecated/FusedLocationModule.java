package inesc_id.pt.motivandroid.deprecated;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

@Deprecated
public class FusedLocationModule implements LocationListener {

    protected final static String LOG_TAG = "FusedLocation";

    private final Context mContext;
    private final GoogleApiClient mGoogleApiClient;


    public FusedLocationModule(Context ctx, GoogleApiClient client) {
        this.mContext = ctx;
        this.mGoogleApiClient = client;



    }

    @Override
    public void onLocationChanged(Location location) {
        Intent localIntent = new Intent("pt.trace.inesc.locationUpdate");
        localIntent.putExtra("locationUpdate", location);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);
    }

    /* Module Interface
     ***********************************************************************************************
     ***********************************************************************************************
     ***********************************************************************************************
     */

    private boolean isTracking = false;



    @SuppressWarnings("MissingPermission")
    public void startTracking() {
        if (!isTracking) {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    this.mGoogleApiClient,
                    createLocationRequest(),
                    this);

            isTracking = true;
        }
    }


    public void stopTracking() {
        if(isTracking) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, this);
            isTracking = false;

        }
    }


    public boolean isTracking() {
        return isTracking;
    }

    /* Tracking Configuration Profile
     ***********************************************************************************************
     ***********************************************************************************************
     ***********************************************************************************************
     */
    private long mInterval = 10000,
            mFastInterval = 5000;

    private int mPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private float mMinimumDisplacement = 2f; //meters

    @Deprecated
    private float mMinimumAccuracy;

    @Deprecated
    private float mMaximumSpeed;

    public void setInterval(long mInterval) {
        this.mInterval = mInterval;
    }

    @Deprecated
    public void setFastInterval(long mFastInterval) {
        this.mFastInterval = 1000;
    }

    @Deprecated
    public void setPriority(int mPriority) {
        this.mPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    }

    @Deprecated
    public void setMinimumDisplacement(float mMinimumDisplacement) {
        this.mMinimumDisplacement = 5;
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(mInterval);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        return mLocationRequest;
    }

    private LocationRequest createLocationRequest(boolean precise){
        LocationRequest request = createLocationRequest();

        if(!precise){
            request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        }

        return request;
    }

    @Deprecated
    public void setMinimumAccuracy(float minimumAccuracy) {
        this.mMinimumAccuracy = minimumAccuracy;
    }

    @Deprecated
    public void setMaximumSpeed(float mMaximumSpeed) {
        this.mMaximumSpeed = mMaximumSpeed;
    }

    @Deprecated
    public void activateRemoveOutliers(boolean activeOutlierRemoval) {
        return;
    }

    /* Testing
     ***********************************************************************************************
     ***********************************************************************************************
     ***********************************************************************************************
     */

    @SuppressWarnings("MissingPermission")

    public void override(boolean fullThrottle){

        LocationRequest customRequest = createLocationRequest(fullThrottle);

        LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, this);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                this.mGoogleApiClient,
                customRequest,
                this);
    }

    private boolean isPaused = false;

    public void pauseTracking(){
        if (isTracking && !isPaused){
            LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, this);
            isPaused = true;
        }
    }

    @SuppressWarnings("MissingPermission")
    public void unpauseTracking(){
        if(isTracking && isPaused){

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    this.mGoogleApiClient,
                    createLocationRequest(true),
                    this);
            isPaused = false;
        }
    }
}
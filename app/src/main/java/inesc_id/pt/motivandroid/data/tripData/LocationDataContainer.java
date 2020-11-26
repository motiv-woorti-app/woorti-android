package inesc_id.pt.motivandroid.data.tripData;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * LocationDataContainer
 *
 *  This class corresponds to the data structure of a location sample generated locally.
 *  Both fields "speed" and "locTimestamp" have been deprecated.
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

public class LocationDataContainer implements Serializable {

    @Expose
    @SerializedName("timestamp")
    long sysTimestamp;

    @SerializedName("acc")
    @Expose
    float accuracy;

    @SerializedName("lat")
    @Expose
    double latitude;

    @SerializedName("lon")
    @Expose
    double longitude;

    @Expose(serialize = false)
    float speed;

    @Expose(serialize = false)
    long locTimestamp;

    public LocationDataContainer(long sysTimestamp, float accuracy, double latitude, double longitude, float speed, long locTimestamp) {
        this.sysTimestamp = sysTimestamp;
        this.accuracy = accuracy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.locTimestamp = locTimestamp;
    }

    public LocationDataContainer() {
    }

    public LocationDataContainer(LatLng latLng){

        this.sysTimestamp = 0;
        this.accuracy = 0;
        this.latitude = latLng.latitude;
        this.longitude = latLng.latitude;
        this.speed = 0;
        this.locTimestamp = 0;

    }

    public long getSysTimestamp() {
        return sysTimestamp;
    }

    public void setSysTimestamp(long sysTimestamp) {
        this.sysTimestamp = sysTimestamp;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getLocTimestamp() {
        return locTimestamp;
    }

    public void setLocTimestamp(long locTimestamp) {
        this.locTimestamp = locTimestamp;
    }

    public LatLng getLatLng(){
        return new LatLng(getLatitude(), getLongitude());
    }

}

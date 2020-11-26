package inesc_id.pt.motivandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import inesc_id.pt.motivandroid.data.tripData.LocationFromServer;

/**
 * Campaign
 *
 * Data structure to represent a campaign data
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
public class Campaign implements Serializable, Parcelable {

    @Expose
    String body;

    @Expose
    String country;

    @Expose
    String name;

    @Expose
    String city;

    public LocationFromServer getLocation() {
        return location;
    }

    public void setLocation(LocationFromServer location) {
        this.location = location;
    }

    @Expose
    LocationFromServer location;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Expose
    double radius;

    @Expose
    boolean isPrivate;

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getPointsWorth() {
        return pointsWorth;
    }

    public void setPointsWorth(int pointsWorth) {
        this.pointsWorth = pointsWorth;
    }

    public int getPointsActivities() {
        return pointsActivities;
    }

    public void setPointsActivities(int pointsActivities) {
        this.pointsActivities = pointsActivities;
    }

    public int getPointsTransportMode() {
        return pointsTransportMode;
    }

    public void setPointsTransportMode(int pointsTransportMode) {
        this.pointsTransportMode = pointsTransportMode;
    }

    public int getPointsTripPurpose() {
        return pointsTripPurpose;
    }

    public void setPointsTripPurpose(int pointsTripPurpose) {
        this.pointsTripPurpose = pointsTripPurpose;
    }

    public int getPointsAllInfo() {
        return pointsAllInfo;
    }

    public void setPointsAllInfo(int pointsAllInfo) {
        this.pointsAllInfo = pointsAllInfo;
    }

    @Expose
    int pointsWorth;

    @Expose
    int pointsActivities;

    @Expose
    int pointsTransportMode;

    @Expose
    int pointsTripPurpose;

    @Expose
    String campaignID;

    @Expose
    String campaignId;

    public Campaign(String campaignID, String body, String country, String name, String city, int pointsWorth, int pointsActivities, int pointsTransportMode, int pointsTripPurpose, int pointsAllInfo) {
        this.campaignId = campaignID;
        this.campaignID = campaignID;
        this.body = body;
        this.country = country;
        this.name = name;
        this.city = city;
        this.pointsWorth = pointsWorth;
        this.pointsActivities = pointsActivities;
        this.pointsTransportMode = pointsTransportMode;
        this.pointsTripPurpose = pointsTripPurpose;
        this.pointsAllInfo = pointsAllInfo;
    }

    @Expose
    int pointsAllInfo;

//    public Campaign(String body, String country, String name, String city) {
//        this.body = body;
//        this.country = country;
//        this.name = name;
//        this.city = city;
//    }

    public Campaign() {
    }

    public Campaign(Parcel parcel){
        this.campaignID = parcel.readString();
        this.body = parcel.readString();
        this.country = parcel.readString();
        this.name = parcel.readString();
        this.city = parcel.readString();
        this.pointsWorth = parcel.readInt();
        this.pointsActivities = parcel.readInt();
        this.pointsTransportMode = parcel.readInt();
        this.pointsTripPurpose = parcel.readInt();
        this.pointsAllInfo = parcel.readInt();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCampaignID() {

        if (campaignId != null){
            return campaignId;
        }else if(campaignID != null){
            return campaignID;
        }else{
            return name;
        }

     }


    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(campaignID);
        parcel.writeString(body);
        parcel.writeString(country);
        parcel.writeString(name);
        parcel.writeString(city);
        parcel.writeInt(pointsWorth);
        parcel.writeInt(pointsActivities);
        parcel.writeInt(pointsTransportMode);
        parcel.writeInt(pointsTripPurpose);
        parcel.writeInt(pointsAllInfo);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }

        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };

//    public static ArrayList<Campaign> getDummyCampaignList(){
//
//        ArrayList<Campaign> result = new ArrayList<>();
//        result.add(new Campaign("dummyCampaignID","bodyDummy","countryDummy", "nameDummy", "cityDummy", 5, 5, 5, 20, 15));
//
//        return result;
//    }

    public static Campaign getDummyCampaign(){

        return new Campaign("dummyCampaignID","bodyDummy","countryDummy", "dummyCampaignID", "cityDummy", 5, 5, 5, 20, 15);

    }
}

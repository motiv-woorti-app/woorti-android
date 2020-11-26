package inesc_id.pt.motivandroid.data.statsFromServer;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * StatsFromTimeIntervalStruct
 *
 * Contains trip metrics calculated for a given time interval for a given community
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
public class StatsFromTimeIntervalStruct implements Serializable{

    public StatsFromTimeIntervalStruct() {
    }

    @Expose
    String dateType;

    @Expose
    String geoType;

    //city, country or campaign id
    @Expose
    String name;

    @Expose
    long date;

    @Expose
    int overallScoreCount;

    @Expose
    int overallScoreTotal;

    @Expose
    double totalDistance;

    @Expose
    long totalDuration;

    @Expose
    long totalLegs;

    @Expose
    long wastedTimeTotal;

    @Expose
    long wastedTimeTotalCount;

    @Expose
    long totalUsers;

    @Expose
    ArrayList<ValueFromTripTotalServer> valueFromTripTotal;

    @Expose
    ArrayList<ValueFromTripTotalCountServer> valueFromTripTotalCount;

    @Expose
    ArrayList<CorrectedModeServer> correctedModes;


    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getGeoType() {
        return geoType;
    }

    public void setGeoType(String geoType) {
        this.geoType = geoType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getOverallScoreCount() {
        return overallScoreCount;
    }

    public void setOverallScoreCount(int overallScoreCount) {
        this.overallScoreCount = overallScoreCount;
    }

    public int getOverallScoreTotal() {
        return overallScoreTotal;
    }

    public void setOverallScoreTotal(int overallScoreTotal) {
        this.overallScoreTotal = overallScoreTotal;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getTotalLegs() {
        return totalLegs;
    }

    public void setTotalLegs(long totalLegs) {
        this.totalLegs = totalLegs;
    }

    public long getWastedTimeTotal() {
        return wastedTimeTotal;
    }

    public void setWastedTimeTotal(long wastedTimeTotal) {
        this.wastedTimeTotal = wastedTimeTotal;
    }

    public long getWastedTimeTotalCount() {
        return wastedTimeTotalCount;
    }

    public void setWastedTimeTotalCount(long wastedTimeTotalCount) {
        this.wastedTimeTotalCount = wastedTimeTotalCount;
    }

    public ArrayList<ValueFromTripTotalServer> getValueFromTripTotal() {
        return valueFromTripTotal;
    }

    public void setValueFromTripTotal(ArrayList<ValueFromTripTotalServer> valueFromTripTotal) {
        this.valueFromTripTotal = valueFromTripTotal;
    }

    public ArrayList<ValueFromTripTotalCountServer> getValueFromTripTotalCount() {
        return valueFromTripTotalCount;
    }

    public void setValueFromTripTotalCount(ArrayList<ValueFromTripTotalCountServer> valueFromTripTotalCount) {
        this.valueFromTripTotalCount = valueFromTripTotalCount;
    }

    public ArrayList<CorrectedModeServer> getCorrectedModes() {
        return correctedModes;
    }

    public void setCorrectedModes(ArrayList<CorrectedModeServer> correctedModes) {
        this.correctedModes = correctedModes;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

}

package inesc_id.pt.motivandroid.data.statsFromServer;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * CorrectedModeServer
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
public class CorrectedModeServer implements Serializable{

    public CorrectedModeServer() {
    }

    @Expose
    int mode;

    @Expose
    long count;

    @Expose
    double distance;

    @Expose
    long duration;

    @Expose
    long wastedTimeMode;

    @Expose
    long wastedTimeModeCount;

    @Expose
    ArrayList<ValueFromTripTotalServer> valueFromTripMode;

    @Expose
    ArrayList<ValueFromTripTotalCountServer> valueFromTripModeCount;

    @Expose
    ArrayList<ValueFromTripTotalServer> valueFromTripModeWSum;

    @Expose
    long wastedTimeWSum;

    @Expose
    long weightedSum;

    public ArrayList<ValueFromTripTotalServer> getValueFromTripMode() {
        return valueFromTripMode;
    }

    public void setValueFromTripMode(ArrayList<ValueFromTripTotalServer> valueFromTripMode) {
        this.valueFromTripMode = valueFromTripMode;
    }

    public ArrayList<ValueFromTripTotalCountServer> getValueFromTripModeCount() {
        return valueFromTripModeCount;
    }

    public void setValueFromTripModeCount(ArrayList<ValueFromTripTotalCountServer> valueFromTripModeCount) {
        this.valueFromTripModeCount = valueFromTripModeCount;
    }


    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getWastedTimeMode() {
        return wastedTimeMode;
    }

    public void setWastedTimeMode(long wastedTimeMode) {
        this.wastedTimeMode = wastedTimeMode;
    }

    public long getWastedTimeModeCount() {
        return wastedTimeModeCount;
    }

    public void setWastedTimeModeCount(long wastedTimeModeCount) {
        this.wastedTimeModeCount = wastedTimeModeCount;
    }

    public ArrayList<ValueFromTripTotalServer> getValueFromTripModeWSum() {
        return valueFromTripModeWSum;
    }

    public void setValueFromTripModeWSum(ArrayList<ValueFromTripTotalServer> valueFromTripModeWSum) {
        this.valueFromTripModeWSum = valueFromTripModeWSum;
    }

    public long getWastedTimeWSum() {
        return wastedTimeWSum;
    }

    public void setWastedTimeWSum(long wastedTimeWSum) {
        this.wastedTimeWSum = wastedTimeWSum;
    }

    public long getWeightedSum() {
        return weightedSum;
    }

    public void setWeightedSum(long weightedSum) {
        this.weightedSum = weightedSum;
    }
}

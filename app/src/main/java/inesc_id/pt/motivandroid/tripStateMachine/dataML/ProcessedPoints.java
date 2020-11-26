package inesc_id.pt.motivandroid.tripStateMachine.dataML;

/**
 *  ProcessedPoints
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
public class ProcessedPoints {

    private double avgSpeed;
    private double maxSpeed;

    private double minSpeed;
    private double stdDevSpeed;

    private double avgAcc;
    private double maxAcc;
    private double minAcc;
    private double stdDevAcc;

    public int getEstimatedSpeed() {
        return estimatedSpeed;
    }

    public void setEstimatedSpeed(int estimatedSpeed) {
        this.estimatedSpeed = estimatedSpeed;
    }

    int estimatedSpeed = 0;

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public double getStdDevSpeed() {
        return stdDevSpeed;
    }

    public void setStdDevSpeed(double stdDevSpeed) {
        this.stdDevSpeed = stdDevSpeed;
    }

    public double getAvgAcc() {
        return avgAcc;
    }

    public void setAvgAcc(double avgAcc) {
        this.avgAcc = avgAcc;
    }

    public double getMaxAcc() {
        return maxAcc;
    }

    public void setMaxAcc(double maxAcc) {
        this.maxAcc = maxAcc;
    }

    public double getMinAcc() {
        return minAcc;
    }

    public void setMinAcc(double minAcc) {
        this.minAcc = minAcc;
    }

    public double getStdDevAcc() {
        return stdDevAcc;
    }

    public void setStdDevAcc(double stdDevAcc) {
        this.stdDevAcc = stdDevAcc;
    }

    public double getGpsTimeMean() {
        return gpsTimeMean;
    }

    public void setGpsTimeMean(double gpsTimeMean) {
        this.gpsTimeMean = gpsTimeMean;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public ProcessedPoints() {
    }

    private double gpsTimeMean;

    private double distance;


    public ProcessedPoints(double avgSpeed, double maxSpeed, double minSpeed, double stdDevSpeed, double avgAcc, double maxAcc, double minAcc, double stdDevAcc, double gpsTimeMean, double distance) {
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.stdDevSpeed = stdDevSpeed;
        this.avgAcc = avgAcc;
        this.maxAcc = maxAcc;
        this.minAcc = minAcc;
        this.stdDevAcc = stdDevAcc;
        this.gpsTimeMean = gpsTimeMean;
        this.distance = distance;
    }



}

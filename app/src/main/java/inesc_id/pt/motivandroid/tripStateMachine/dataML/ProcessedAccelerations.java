package inesc_id.pt.motivandroid.tripStateMachine.dataML;

/**
 *  ProcessedAccelerations
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
public class ProcessedAccelerations {

    private double avgAccel;
    private double maxAccel;

    private double minAccel;
    private double stdDevAccel;

    private double between_03_06;
    private double between_06_1;
    private double between_1_3;
    private double between_3_6;
    private double above_6;

    public ProcessedAccelerations() {
    }

    public ProcessedAccelerations(double avgAccel, double maxAccel, double minAccel, double stdDevAccel) {
        this.avgAccel = avgAccel;
        this.maxAccel = maxAccel;
        this.minAccel = minAccel;
        this.stdDevAccel = stdDevAccel;
    }

    public double getAvgAccel() {
        return avgAccel;
    }

    public void setAvgAccel(double avgAccel) {
        this.avgAccel = avgAccel;
    }

    public double getMaxAccel() {
        return maxAccel;
    }

    public void setMaxAccel(double maxAccel) {
        this.maxAccel = maxAccel;
    }

    public double getMinAccel() {
        return minAccel;
    }

    public void setMinAccel(double minAccel) {
        this.minAccel = minAccel;
    }

    public double getStdDevAccel() {
        return stdDevAccel;
    }

    public void setStdDevAccel(double stdDevAccel) {
        this.stdDevAccel = stdDevAccel;
    }

    public double getBetween_03_06() {
        return between_03_06;
    }

    public void setBetween_03_06(double between_03_06) {
        this.between_03_06 = between_03_06;
    }

    public double getBetween_06_1() {
        return between_06_1;
    }

    public void setBetween_06_1(double between_06_1) {
        this.between_06_1 = between_06_1;
    }

    public double getBetween_1_3() {
        return between_1_3;
    }

    public void setBetween_1_3(double between_1_3) {
        this.between_1_3 = between_1_3;
    }

    public double getBetween_3_6() {
        return between_3_6;
    }

    public void setBetween_3_6(double between_3_6) {
        this.between_3_6 = between_3_6;
    }

    public double getAbove_6() {
        return above_6;
    }

    public void setAbove_6(double above_6) {
        this.above_6 = above_6;
    }


}
